package org.mcp.nextcloud.server;

import java.io.IOException;
import java.util.Collections;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mcp.nextcloud.client.NextcloudClient;
import org.mcp.nextcloud.client.NextcloudCredentials;
import org.mcp.nextcloud.client.NextcloudUser;
import org.mcp.nextcloud.config.ConfigPaths;
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
import org.mcp.nextcloud.security.Principal;
import org.mcp.nextcloud.security.PrincipalContext;
import org.mcp.nextcloud.security.Scope;
import org.mcp.nextcloud.tool.api.ToolDescriptor;
import org.mcp.nextcloud.tool.api.ToolParameter;
import org.mcp.nextcloud.tool.api.ToolResult;
import org.mcp.nextcloud.tool.runtime.InMemoryToolRegistry;
import org.mcp.nextcloud.tool.runtime.ToolDispatcher;
import org.mcp.nextcloud.tool.runtime.ToolRegistration;
import org.mcp.nextcloud.tool.runtime.ToolRuntimeContext;
import org.mcp.nextcloud.tools.files.NextcloudFilesTools;
import org.mcp.nextcloud.tools.share.NextcloudShareTools;
import org.mcp.nextcloud.tools.user.NextcloudUserTools;
import org.springframework.stereotype.Service;

@Service
public class NextcloudMcpRuntimeService {
    private final NextcloudMcpServerProperties properties;
    private final YamlConfigLoader configLoader;
    private final ConfigValidator configValidator;
    private final SecretResolver secretResolver;
    private final NextcloudHttpClientFactory httpClientFactory;

    NextcloudMcpRuntimeService(
            NextcloudMcpServerProperties properties,
            YamlConfigLoader configLoader,
            ConfigValidator configValidator,
            SecretResolver secretResolver,
            NextcloudHttpClientFactory httpClientFactory) {
        this.properties = properties;
        this.configLoader = configLoader;
        this.configValidator = configValidator;
        this.secretResolver = secretResolver;
        this.httpClientFactory = httpClientFactory;
    }

    public void validateConfiguration() {
        loadValidatedConfig();
    }

    public Map<String, Object> health() {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("status", "UP");
        Optional<Path> configPath = findConfigPath();
        configPath.map(Path::toString).ifPresent(path -> values.put("configPath", path));
        values.put("configLoaded", configPath.isPresent());
        if (configPath.isPresent()) {
            try {
                LoadedConfig loaded = loadValidatedConfig();
                values.put("accounts", loaded.config().accounts().size());
                values.put("tools", session(loaded, null, false).dispatcher().listTools().size());
            } catch (RuntimeException ex) {
                values.put("configError", ex.getMessage());
            }
        }
        return Collections.unmodifiableMap(values);
    }

    public List<Map<String, Object>> listAccounts() {
        LoadedConfig loaded = loadValidatedConfig();
        return loaded.config().accounts().entrySet().stream()
                .map(entry -> accountMap(entry.getKey(), accountWithId(entry.getKey(), entry.getValue())))
                .toList();
    }

    public Map<String, Object> testAccount(String requestedAccountId) {
        ServerSession session = session(loadValidatedConfig(), requestedAccountId, true);
        NextcloudUser identity = session.identity();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("accountId", session.configuredAccountId());
        result.put("baseUrl", session.credentials().baseUrl());
        result.put("username", session.credentials().username());
        result.put("connected", true);
        result.put("userId", identity.id());
        result.put("displayName", identity.displayName());
        result.put("enabled", identity.enabled());
        return Collections.unmodifiableMap(result);
    }

    public List<Map<String, Object>> listTools(String requestedAccountId) {
        ServerSession session = session(loadValidatedConfig(), requestedAccountId, false);
        return session.dispatcher().listTools().stream()
                .map(this::descriptorMap)
                .toList();
    }

    public Map<String, Object> callTool(ToolCallRequest request) {
        if (request == null || request.tool() == null || request.tool().isBlank()) {
            throw new ServerRequestException("request.invalid", "tool is required");
        }
        ServerSession session = session(loadValidatedConfig(), request.accountId(), true);
        ToolResult result = session.dispatcher().invoke(
                new ToolId(request.tool()),
                request.arguments(),
                runtimeContext(session, request.invocationId()));
        return resultMap(result);
    }

    private LoadedConfig loadValidatedConfig() {
        Path path = findConfigPath()
                .orElseThrow(() -> new ServerRequestException("config.not_found", "config file not found"));
        try {
            NextcloudMcpConfig config = configLoader.load(path);
            List<ConfigValidationError> errors = configValidator.validate(config);
            if (!errors.isEmpty()) {
                ConfigValidationError first = errors.getFirst();
                throw new ServerRequestException(
                        "config.invalid",
                        "config validation failed: " + first.path() + " " + first.message());
            }
            return new LoadedConfig(path.toAbsolutePath().normalize(), config);
        } catch (IOException ex) {
            throw new ServerRequestException("config.load_failed", "could not load config: " + ex.getMessage());
        }
    }

    private Optional<Path> findConfigPath() {
        return ConfigPaths.findFromSystemSources(properties.configPath());
    }

    private ServerSession session(LoadedConfig loaded, String requestedAccountId, boolean resolveIdentity) {
        AccountSelection selection = selectAccount(loaded.config(), requestedAccountId);
        NextcloudCredentials credentials = credentials(selection, resolveIdentity);
        NextcloudClient client = new NextcloudClient(httpClientFactory.create(credentials), credentials);
        InMemoryToolRegistry registry = registry(client);
        ToolDispatcher dispatcher = new ToolDispatcher(registry);
        NextcloudUser identity = resolveIdentity ? client.users().currentUser() : null;
        Set<Scope> scopes = selection.account().scopes().stream()
                .map(Scope::new)
                .collect(Collectors.toUnmodifiableSet());
        return new ServerSession(selection.accountId(), credentials, dispatcher, identity, scopes);
    }

    private AccountSelection selectAccount(NextcloudMcpConfig config, String requestedAccountId) {
        String accountId = requestedAccountId == null || requestedAccountId.isBlank()
                ? properties.defaultAccountId()
                : requestedAccountId;
        if (accountId != null && !accountId.isBlank()) {
            NextcloudAccountConfig account = config.accounts().get(accountId);
            if (account == null) {
                throw new ServerRequestException("account.not_found", "account not found: " + accountId);
            }
            return enabledSelection(accountId, account);
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
        throw new ServerRequestException("account.not_found", "no enabled account configured");
    }

    private AccountSelection enabledSelection(String accountId, NextcloudAccountConfig account) {
        NextcloudAccountConfig normalized = accountWithId(accountId, account);
        if (!normalized.enabled()) {
            throw new ServerRequestException("account.disabled", "account is disabled: " + accountId);
        }
        return new AccountSelection(accountId, normalized);
    }

    private NextcloudAccountConfig accountWithId(String accountId, NextcloudAccountConfig account) {
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

    private NextcloudCredentials credentials(AccountSelection selection, boolean resolveSecret) {
        if (resolveSecret) {
            return NextcloudCredentials.fromAccount(selection.account(), secretResolver);
        }
        NextcloudAccountConfig account = selection.account();
        return NextcloudCredentials.of(selection.accountId(), account.baseUrl(), account.username(), "not-used-for-tool-listing");
    }

    private InMemoryToolRegistry registry(NextcloudClient client) {
        InMemoryToolRegistry registry = new InMemoryToolRegistry();
        NextcloudFilesTools.registrations(client).forEach(registry::register);
        NextcloudShareTools.registrations(client).forEach(registry::register);
        NextcloudUserTools.registrations(client).forEach(registry::register);
        return registry;
    }

    private ToolRuntimeContext runtimeContext(ServerSession session, String requestedInvocationId) {
        NextcloudUser identity = session.identity();
        String userId = identity == null || identity.id() == null || identity.id().isBlank()
                ? session.credentials().username()
                : identity.id();
        String displayName = identity == null ? userId : identity.displayName();
        Principal principal = new Principal(new PrincipalId(userId), displayName, false, session.scopes());
        String invocationId = requestedInvocationId == null || requestedInvocationId.isBlank()
                ? "http-" + UUID.randomUUID()
                : requestedInvocationId;
        PrincipalContext principalContext = new PrincipalContext(
                principal,
                new AccountId(userId),
                new InvocationId(invocationId));
        return new ToolRuntimeContext(principalContext, Map.of(
                "source", "spring-server",
                "configuredAccountId", session.configuredAccountId()));
    }

    private Map<String, Object> accountMap(String accountId, NextcloudAccountConfig account) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("accountId", accountId);
        values.put("baseUrl", account.baseUrl());
        values.put("username", account.username());
        values.put("defaultAccount", account.defaultAccount());
        values.put("admin", account.admin());
        values.put("enabled", account.enabled());
        values.put("scopes", account.scopes());
        return Collections.unmodifiableMap(values);
    }

    private Map<String, Object> descriptorMap(ToolDescriptor descriptor) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("name", descriptor.name());
        values.put("description", descriptor.description());
        values.put("destructive", descriptor.security().destructive());
        values.put("requiredScopes", descriptor.security().requiredScopes());
        values.put("parameters", descriptor.inputSchema().parameters().stream().map(this::parameterMap).toList());
        values.put("metadata", descriptor.metadata());
        return Collections.unmodifiableMap(values);
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
        return Collections.unmodifiableMap(values);
    }

    private record LoadedConfig(Path path, NextcloudMcpConfig config) {
    }

    private record AccountSelection(String accountId, NextcloudAccountConfig account) {
    }

    private record ServerSession(
            String configuredAccountId,
            NextcloudCredentials credentials,
            ToolDispatcher dispatcher,
            NextcloudUser identity,
            Set<Scope> scopes) {
    }
}
