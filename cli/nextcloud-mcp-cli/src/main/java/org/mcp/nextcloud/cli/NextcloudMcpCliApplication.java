package org.mcp.nextcloud.cli;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mcp.nextcloud.client.NextcloudClient;
import org.mcp.nextcloud.client.NextcloudCredentials;
import org.mcp.nextcloud.client.NextcloudUser;
import org.mcp.nextcloud.config.EnvironmentSecretResolver;
import org.mcp.nextcloud.config.NextcloudAccountConfig;
import org.mcp.nextcloud.config.NextcloudMcpConfig;
import org.mcp.nextcloud.config.SecretResolver;
import org.mcp.nextcloud.config.YamlConfigLoader;
import org.mcp.nextcloud.config.validation.ConfigValidationError;
import org.mcp.nextcloud.config.validation.ConfigValidator;
import org.mcp.nextcloud.core.id.AccountId;
import org.mcp.nextcloud.core.id.InvocationId;
import org.mcp.nextcloud.core.id.PrincipalId;
import org.mcp.nextcloud.core.id.ToolId;
import org.mcp.nextcloud.http.HttpClientAdapter;
import org.mcp.nextcloud.http.JdkHttpClientAdapter;
import org.mcp.nextcloud.security.Principal;
import org.mcp.nextcloud.security.PrincipalContext;
import org.mcp.nextcloud.security.Scope;
import org.mcp.nextcloud.tool.api.ToolDescriptor;
import org.mcp.nextcloud.tool.api.ToolParameter;
import org.mcp.nextcloud.tool.api.ToolResult;
import org.mcp.nextcloud.tool.api.ToolValueType;
import org.mcp.nextcloud.tool.runtime.InMemoryToolRegistry;
import org.mcp.nextcloud.tool.runtime.ToolDispatcher;
import org.mcp.nextcloud.tool.runtime.ToolRegistration;
import org.mcp.nextcloud.tool.runtime.ToolRuntimeContext;
import org.mcp.nextcloud.tools.files.NextcloudFilesTools;
import org.mcp.nextcloud.tools.share.NextcloudShareTools;
import org.mcp.nextcloud.tools.user.NextcloudUserTools;

public final class NextcloudMcpCliApplication {
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    private final YamlConfigLoader configLoader;
    private final ConfigValidator configValidator;
    private final SecretResolver secretResolver;
    private final Function<NextcloudCredentials, HttpClientAdapter> httpClientFactory;
    private final ObjectMapper objectMapper;

    public NextcloudMcpCliApplication() {
        this(new YamlConfigLoader(), new ConfigValidator(), new EnvironmentSecretResolver(),
                credentials -> new JdkHttpClientAdapter(), new ObjectMapper().findAndRegisterModules());
    }

    NextcloudMcpCliApplication(
            YamlConfigLoader configLoader,
            ConfigValidator configValidator,
            SecretResolver secretResolver,
            Function<NextcloudCredentials, HttpClientAdapter> httpClientFactory,
            ObjectMapper objectMapper) {
        this.configLoader = configLoader;
        this.configValidator = configValidator;
        this.secretResolver = secretResolver;
        this.httpClientFactory = httpClientFactory;
        this.objectMapper = objectMapper;
    }

    public int run(String[] args, PrintStream out, PrintStream err) {
        CliCommand command;
        try {
            command = CliCommand.parse(args == null ? new String[0] : args);
        } catch (CliUsageException ex) {
            err.println("Error: " + ex.getMessage());
            printUsage(err);
            return 2;
        }

        try {
            if (command.help() || command.words().isEmpty()) {
                printUsage(out);
                return 0;
            }
            return execute(command, out, err);
        } catch (Exception ex) {
            String message = sanitized(ex.getMessage(), List.of());
            if (command.json()) {
                writeJson(out, Map.of(
                        "success", false,
                        "error", Map.of(
                                "code", "cli.error",
                                "message", message == null || message.isBlank() ? "CLI command failed" : message)));
            } else {
                err.println("Error: " + (message == null || message.isBlank() ? "CLI command failed" : message));
            }
            return 1;
        }
    }

    private int execute(CliCommand command, PrintStream out, PrintStream err) throws IOException {
        List<String> words = command.words();
        if (words.size() == 2 && "config".equals(words.get(0)) && "check".equals(words.get(1))) {
            return configCheck(command, out);
        }
        if (words.size() == 2 && "tools".equals(words.get(0)) && "list".equals(words.get(1))) {
            return toolsList(command, out);
        }
        if (words.size() >= 2 && "accounts".equals(words.get(0)) && "test".equals(words.get(1))) {
            String accountId = words.size() >= 3 ? words.get(2) : command.accountId();
            return accountsTest(command.withAccountId(accountId), out);
        }
        if (words.size() >= 2 && "call".equals(words.get(0))) {
            return call(command, words.get(1), out);
        }
        err.println("Error: unknown command: " + String.join(" ", words));
        printUsage(err);
        return 2;
    }

    private int configCheck(CliCommand command, PrintStream out) throws IOException {
        LoadedConfig loaded = loadConfig(command);
        List<ConfigValidationError> errors = configValidator.validate(loaded.config());
        Map<String, Object> result = Map.of(
                "configPath", loaded.path().toString(),
                "valid", errors.isEmpty(),
                "errors", errors.stream().map(this::validationErrorMap).toList());
        if (command.json()) {
            writeJson(out, result);
        } else if (errors.isEmpty()) {
            out.println("Config OK: " + loaded.path());
        } else {
            out.println("Config invalid: " + loaded.path());
            errors.forEach(error -> out.println("- " + error.path() + ": " + error.message()));
        }
        return errors.isEmpty() ? 0 : 2;
    }

    private int toolsList(CliCommand command, PrintStream out) throws IOException {
        CliSession session = session(command, false);
        List<Map<String, Object>> tools = session.dispatcher().listTools().stream()
                .map(this::descriptorMap)
                .toList();
        if (command.json()) {
            writeJson(out, Map.of("tools", tools));
        } else {
            for (Map<String, Object> tool : tools) {
                out.println(tool.get("name") + " - " + tool.get("description"));
            }
        }
        return 0;
    }

    private int accountsTest(CliCommand command, PrintStream out) throws IOException {
        CliSession session = session(command, true);
        NextcloudUser identity = session.identity();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("accountId", session.accountId());
        result.put("baseUrl", session.credentials().baseUrl());
        result.put("username", session.credentials().username());
        result.put("connected", true);
        result.put("userId", identity.id());
        result.put("displayName", identity.displayName());
        result.put("enabled", identity.enabled());
        if (command.json()) {
            writeJson(out, result);
        } else {
            out.println("Account OK: " + session.accountId());
            out.println("User: " + identity.id() + displayNameSuffix(identity));
        }
        return 0;
    }

    private int call(CliCommand command, String toolName, PrintStream out) throws IOException {
        CliSession session = session(command, true);
        ToolDescriptor descriptor = session.registry().find(new ToolId(toolName))
                .map(ToolRegistration::descriptor)
                .orElseThrow(() -> new CliUsageException("tool not found: " + toolName));
        Map<String, Object> arguments = arguments(command, descriptor);
        ToolResult result = session.dispatcher().invoke(new ToolId(toolName), arguments, runtimeContext(session));
        writeJson(out, resultMap(result));
        return result.success() ? 0 : 2;
    }

    private CliSession session(CliCommand command, boolean resolveIdentity) throws IOException {
        LoadedConfig loaded = loadConfig(command);
        List<ConfigValidationError> errors = configValidator.validate(loaded.config());
        if (!errors.isEmpty()) {
            throw new CliUsageException("config validation failed: " + errors.getFirst().path() + " " + errors.getFirst().message());
        }
        AccountSelection selection = selectAccount(loaded.config(), command.accountId());
        NextcloudCredentials credentials = credentials(selection, resolveIdentity);
        NextcloudClient client = new NextcloudClient(httpClientFactory.apply(credentials), credentials);
        InMemoryToolRegistry registry = registry(client);
        ToolDispatcher dispatcher = new ToolDispatcher(registry);
        NextcloudUser identity = resolveIdentity ? client.users().currentUser() : null;
        Set<Scope> scopes = selection.account().scopes().stream()
                .map(Scope::new)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
        return new CliSession(selection.accountId(), credentials, registry, dispatcher, identity, scopes);
    }

    private NextcloudCredentials credentials(AccountSelection selection, boolean resolveSecret) {
        if (resolveSecret) {
            return NextcloudCredentials.fromAccount(selection.account(), secretResolver);
        }
        NextcloudAccountConfig account = selection.account();
        return NextcloudCredentials.of(selection.accountId(), account.baseUrl(), account.username(), "not-used-for-tool-listing");
    }

    private LoadedConfig loadConfig(CliCommand command) throws IOException {
        Path path = command.configPath() == null ? defaultConfigPath() : command.configPath();
        if (path == null || !Files.isRegularFile(path)) {
            throw new CliUsageException("config file not found; use --config <path>");
        }
        return new LoadedConfig(path.toAbsolutePath().normalize(), configLoader.load(path));
    }

    private Path defaultConfigPath() {
        String property = System.getProperty("nextcloud.mcp.config");
        if (property != null && !property.isBlank()) {
            return Path.of(property);
        }
        String env = System.getenv("NEXTCLOUD_MCP_CONFIG");
        if (env != null && !env.isBlank()) {
            return Path.of(env);
        }
        for (String candidate : List.of("nextcloud-mcp.yml", "nextcloud-mcp.yaml", "config/nextcloud-mcp.yml")) {
            Path path = Path.of(candidate);
            if (Files.isRegularFile(path)) {
                return path;
            }
        }
        return null;
    }

    private AccountSelection selectAccount(NextcloudMcpConfig config, String requestedAccountId) {
        if (requestedAccountId != null && !requestedAccountId.isBlank()) {
            NextcloudAccountConfig account = config.accounts().get(requestedAccountId);
            if (account == null) {
                throw new CliUsageException("account not found: " + requestedAccountId);
            }
            return enabledSelection(requestedAccountId, account);
        }
        Optional<Map.Entry<String, NextcloudAccountConfig>> defaultAccount = config.accounts().entrySet().stream()
                .filter(entry -> entry.getValue() != null && entry.getValue().defaultAccount())
                .findFirst();
        if (defaultAccount.isPresent()) {
            return enabledSelection(defaultAccount.get().getKey(), defaultAccount.get().getValue());
        }
        Optional<Map.Entry<String, NextcloudAccountConfig>> enabledAccount = config.accounts().entrySet().stream()
                .filter(entry -> entry.getValue() != null && entry.getValue().enabled())
                .findFirst();
        if (enabledAccount.isPresent()) {
            return enabledSelection(enabledAccount.get().getKey(), enabledAccount.get().getValue());
        }
        throw new CliUsageException("no enabled account configured");
    }

    private AccountSelection enabledSelection(String accountId, NextcloudAccountConfig account) {
        NextcloudAccountConfig normalized = accountId(accountId, account);
        if (!normalized.enabled()) {
            throw new CliUsageException("account is disabled: " + accountId);
        }
        return new AccountSelection(accountId, normalized);
    }

    private NextcloudAccountConfig accountId(String accountId, NextcloudAccountConfig account) {
        if (account.id() != null && !account.id().isBlank()) {
            return account;
        }
        return new NextcloudAccountConfig(
                accountId,
                account.baseUrl(),
                account.username(),
                account.appPassword(),
                account.defaultAccount(),
                account.admin(),
                account.enabled(),
                account.scopes());
    }

    private InMemoryToolRegistry registry(NextcloudClient client) {
        InMemoryToolRegistry registry = new InMemoryToolRegistry();
        NextcloudFilesTools.registrations(client).forEach(registry::register);
        NextcloudShareTools.registrations(client).forEach(registry::register);
        NextcloudUserTools.registrations(client).forEach(registry::register);
        return registry;
    }

    private ToolRuntimeContext runtimeContext(CliSession session) {
        NextcloudUser identity = session.identity();
        String userId = identity == null || identity.id() == null || identity.id().isBlank()
                ? session.credentials().username()
                : identity.id();
        String displayName = identity == null ? userId : identity.displayName();
        Set<Scope> scopes = session.scopes();
        Principal principal = new Principal(new PrincipalId(userId), displayName, false, scopes);
        PrincipalContext principalContext = new PrincipalContext(
                principal,
                new AccountId(userId),
                new InvocationId("cli-" + UUID.randomUUID()));
        return new ToolRuntimeContext(principalContext, Map.of(
                "source", "cli",
                "configuredAccountId", session.accountId()));
    }

    private Map<String, Object> arguments(CliCommand command, ToolDescriptor descriptor) throws IOException {
        Map<String, Object> values = new LinkedHashMap<>();
        if (command.argsJson() != null) {
            values.putAll(objectMapper.readValue(command.argsJson(), MAP_TYPE));
        }
        for (Map.Entry<String, String> entry : command.argValues().entrySet()) {
            values.put(entry.getKey(), coerce(entry.getValue(), parameter(descriptor, entry.getKey())));
        }
        return values;
    }

    private ToolParameter parameter(ToolDescriptor descriptor, String name) {
        return descriptor.inputSchema().parametersByName().get(name);
    }

    private Object coerce(String value, ToolParameter parameter) {
        if (parameter == null) {
            return value;
        }
        ToolValueType type = parameter.type();
        return switch (type) {
            case BOOLEAN -> Boolean.parseBoolean(value);
            case INTEGER -> Long.parseLong(value);
            case NUMBER -> Double.parseDouble(value);
            case OBJECT, ARRAY -> readJsonValue(value);
            case STRING -> value;
        };
    }

    private Object readJsonValue(String value) {
        try {
            return objectMapper.readValue(value, Object.class);
        } catch (IOException ex) {
            throw new CliUsageException("argument must be valid JSON for object or array parameter");
        }
    }

    private Map<String, Object> descriptorMap(ToolDescriptor descriptor) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("name", descriptor.name());
        values.put("description", descriptor.description());
        values.put("destructive", descriptor.security().destructive());
        values.put("requiredScopes", descriptor.security().requiredScopes());
        values.put("parameters", descriptor.inputSchema().parameters().stream().map(this::parameterMap).toList());
        values.put("metadata", descriptor.metadata());
        return Map.copyOf(values);
    }

    private Map<String, Object> parameterMap(ToolParameter parameter) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("name", parameter.name());
        values.put("type", parameter.type().name().toLowerCase());
        values.put("required", parameter.required());
        values.put("description", parameter.description());
        if (!parameter.allowedValues().isEmpty()) {
            values.put("allowedValues", parameter.allowedValues());
        }
        if (parameter.defaultValue() != null) {
            values.put("defaultValue", parameter.defaultValue());
        }
        return Map.copyOf(values);
    }

    private Map<String, Object> resultMap(ToolResult result) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("success", result.success());
        values.put("content", result.content());
        values.put("structuredContent", result.structuredContent());
        values.put("metadata", result.metadata());
        if (result.error() != null) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("code", result.error().code());
            error.put("message", result.error().message());
            error.put("details", result.error().details());
            values.put("error", error);
        }
        return values;
    }

    private Map<String, Object> validationErrorMap(ConfigValidationError error) {
        return Map.of("path", error.path(), "message", error.message());
    }

    private void writeJson(PrintStream out, Object value) {
        try {
            out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value));
        } catch (IOException ex) {
            throw new IllegalStateException("could not write JSON output", ex);
        }
    }

    private static String displayNameSuffix(NextcloudUser identity) {
        return identity.displayName() == null || identity.displayName().isBlank()
                ? ""
                : " (" + identity.displayName() + ")";
    }

    private static String sanitized(String message, List<String> secrets) {
        if (message == null) {
            return null;
        }
        String sanitized = message;
        for (String secret : secrets) {
            if (secret != null && !secret.isBlank()) {
                sanitized = sanitized.replace(secret, "****");
            }
        }
        return sanitized.replaceAll("(?i)Authorization: Basic [A-Za-z0-9+/=]+", "Authorization: Basic ****");
    }

    private void printUsage(PrintStream out) {
        out.println("Usage:");
        out.println("  nextcloud-mcp [--config path] [--account id] [--json] tools list");
        out.println("  nextcloud-mcp [--config path] [--account id] call <tool> --arg key=value [--arg key=value]");
        out.println("  nextcloud-mcp [--config path] accounts test [accountId]");
        out.println("  nextcloud-mcp [--config path] [--json] config check");
    }

    private record LoadedConfig(Path path, NextcloudMcpConfig config) {
    }

    private record AccountSelection(String accountId, NextcloudAccountConfig account) {
    }

    private record CliSession(
            String accountId,
            NextcloudCredentials credentials,
            InMemoryToolRegistry registry,
            ToolDispatcher dispatcher,
            NextcloudUser identity,
            Set<Scope> scopes) {
    }

    private record CliCommand(
            Path configPath,
            String accountId,
            boolean json,
            boolean help,
            List<String> words,
            Map<String, String> argValues,
            String argsJson) {
        static CliCommand parse(String[] args) {
            Path configPath = null;
            String accountId = null;
            boolean json = false;
            boolean help = false;
            List<String> words = new ArrayList<>();
            Map<String, String> argValues = new LinkedHashMap<>();
            String argsJson = null;

            for (int index = 0; index < args.length; index++) {
                String arg = args[index];
                switch (arg) {
                    case "--help", "-h" -> help = true;
                    case "--json" -> json = true;
                    case "--config" -> configPath = Path.of(requiredValue(args, ++index, "--config"));
                    case "--account" -> accountId = requiredValue(args, ++index, "--account");
                    case "--arg" -> {
                        String raw = requiredValue(args, ++index, "--arg");
                        int equals = raw.indexOf('=');
                        if (equals <= 0) {
                            throw new CliUsageException("--arg requires key=value");
                        }
                        argValues.put(raw.substring(0, equals), raw.substring(equals + 1));
                    }
                    case "--args-json" -> argsJson = requiredValue(args, ++index, "--args-json");
                    default -> words.add(arg);
                }
            }
            return new CliCommand(configPath, accountId, json, help, List.copyOf(words), Map.copyOf(argValues), argsJson);
        }

        CliCommand withAccountId(String accountId) {
            return new CliCommand(configPath, accountId, json, help, words, argValues, argsJson);
        }

        private static String requiredValue(String[] args, int index, String option) {
            if (index >= args.length || args[index].isBlank()) {
                throw new CliUsageException(option + " requires a value");
            }
            return args[index];
        }
    }

    private static final class CliUsageException extends RuntimeException {
        private CliUsageException(String message) {
            super(message);
        }
    }
}
