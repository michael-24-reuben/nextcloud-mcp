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

import org.mcp.nextcloud.admin.NextcloudAdminClient;
import org.mcp.nextcloud.admin.NextcloudAdminCredentials;
import org.mcp.nextcloud.client.NextcloudClient;
import org.mcp.nextcloud.client.NextcloudCredentials;
import org.mcp.nextcloud.client.NextcloudUser;
import org.mcp.nextcloud.config.ConfigPaths;
import org.mcp.nextcloud.config.LocalUserAccountRecord;
import org.mcp.nextcloud.config.LocalUserAccountRepository;
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
import org.mcp.nextcloud.tools.admin.NextcloudAdminTools;
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

    public Map<String, Object> getAccount(String accountId) {
        LoadedConfig loaded = loadValidatedConfig();
        NextcloudAccountConfig account = loaded.config().accounts().get(accountId);
        if (account == null) {
            throw new ServerRequestException("account.not_found", "account not found: " + accountId);
        }
        return accountMap(accountId, accountWithId(accountId, account));
    }

    public Map<String, Object> createAccount(AccountCreateRequest request) {
        if (request == null) {
            throw new ServerRequestException("request.invalid", "account request is required");
        }
        try {
            Path configPath = loadValidatedConfig().path();
            LocalUserAccountRecord record = new LocalUserAccountRecord(
                    required(request.accountId(), "accountId"),
                    valueOr(request.nextcloudAccountId(), request.accountId()),
                    required(request.baseUrl(), "baseUrl"),
                    required(request.username(), "username"),
                    required(request.appPassword(), "appPassword"),
                    request.displayName(),
                    request.email(),
                    valueOr(request.defaultAccount(), false),
                    valueOr(request.admin(), false),
                    valueOr(request.enabled(), true),
                    request.scopes());
            return localAccountMap(LocalUserAccountRepository.create(configPath, record), false);
        } catch (IllegalStateException ex) {
            throw new ServerRequestException("account.exists", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            throw new ServerRequestException("request.invalid", ex.getMessage());
        } catch (IOException ex) {
            throw new ServerRequestException("account.write_failed", "could not write account: " + ex.getMessage());
        }
    }

    public Map<String, Object> updateAccount(String accountId, AccountPatchRequest request) {
        if (request == null) {
            throw new ServerRequestException("request.invalid", "account patch request is required");
        }
        try {
            Path configPath = loadValidatedConfig().path();
            LocalUserAccountRecord existing = LocalUserAccountRepository.find(configPath, accountId)
                    .orElseThrow(() -> new ServerRequestException("account.not_found", "local account not found: " + accountId));
            LocalUserAccountRecord updated = new LocalUserAccountRecord(
                    existing.accountKey(),
                    valueOr(request.nextcloudAccountId(), existing.accountId()),
                    valueOr(request.baseUrl(), existing.baseUrl()),
                    valueOr(request.username(), existing.username()),
                    existing.appPassword(),
                    valueOr(request.displayName(), existing.displayName()),
                    valueOr(request.email(), existing.email()),
                    valueOr(request.defaultAccount(), existing.defaultAccount()),
                    valueOr(request.admin(), existing.admin()),
                    valueOr(request.enabled(), existing.enabled()),
                    request.scopes() == null ? existing.scopes() : request.scopes());
            return localAccountMap(LocalUserAccountRepository.write(configPath, updated), false);
        } catch (ServerRequestException ex) {
            throw ex;
        } catch (IllegalArgumentException ex) {
            throw new ServerRequestException("request.invalid", ex.getMessage());
        } catch (IOException ex) {
            throw new ServerRequestException("account.write_failed", "could not write account: " + ex.getMessage());
        }
    }

    public Map<String, Object> deleteAccount(String accountId) {
        try {
            Path configPath = loadValidatedConfig().path();
            boolean deleted = LocalUserAccountRepository.delete(configPath, accountId);
            if (!deleted) {
                throw new ServerRequestException("account.not_found", "local account not found: " + accountId);
            }
            return Map.of("accountId", accountId, "deleted", true);
        } catch (ServerRequestException ex) {
            throw ex;
        } catch (IllegalArgumentException ex) {
            throw new ServerRequestException("request.invalid", ex.getMessage());
        } catch (IOException ex) {
            throw new ServerRequestException("account.write_failed", "could not delete account: " + ex.getMessage());
        }
    }

    public Map<String, Object> setAccountAppPassword(String accountId, AppPasswordRequest request) {
        if (request == null || request.appPassword() == null || request.appPassword().isBlank()) {
            throw new ServerRequestException("request.invalid", "appPassword is required");
        }
        try {
            Path configPath = loadValidatedConfig().path();
            LocalUserAccountRecord existing = LocalUserAccountRepository.find(configPath, accountId)
                    .orElseThrow(() -> new ServerRequestException("account.not_found", "local account not found: " + accountId));
            LocalUserAccountRecord updated = new LocalUserAccountRecord(
                    existing.accountKey(),
                    existing.accountId(),
                    existing.baseUrl(),
                    existing.username(),
                    request.appPassword(),
                    existing.displayName(),
                    existing.email(),
                    existing.defaultAccount(),
                    existing.admin(),
                    existing.enabled(),
                    existing.scopes());
            return localAccountMap(LocalUserAccountRepository.write(configPath, updated), false);
        } catch (ServerRequestException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new ServerRequestException("account.write_failed", "could not write account app password: " + ex.getMessage());
        }
    }

    public Map<String, Object> setAccountEnabled(String accountId, boolean enabled) {
        return updateAccount(accountId, new AccountPatchRequest(null, null, null, null, null, null, null, null, enabled, null));
    }

    public Map<String, Object> makeDefaultAccount(String accountId) {
        try {
            Path configPath = loadValidatedConfig().path();
            return localAccountMap(LocalUserAccountRepository.makeDefault(configPath, accountId), false);
        } catch (IllegalArgumentException ex) {
            throw new ServerRequestException("account.not_found", ex.getMessage());
        } catch (IOException ex) {
            throw new ServerRequestException("account.write_failed", "could not write default account: " + ex.getMessage());
        }
    }

    NextcloudAdminClient adminClient() {
        LoadedConfig loaded = loadValidatedConfig();
        NextcloudAdminCredentials adminCredentials = adminCredentials(loaded.config(), true);
        NextcloudCredentials httpCredentials = NextcloudCredentials.of(
                adminCredentials.accountId().value(),
                adminCredentials.baseUrl(),
                adminCredentials.username(),
                adminCredentials.appPassword());
        return new NextcloudAdminClient(httpClientFactory.create(httpCredentials), adminCredentials);
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
        InMemoryToolRegistry registry = registry(loaded.config(), client, resolveIdentity);
        ToolDispatcher dispatcher = new ToolDispatcher(registry);
        NextcloudUser identity = resolveIdentity ? client.users().currentUser() : null;
        Set<Scope> scopes = selection.account().scopes().stream()
                .map(Scope::new)
                .collect(Collectors.toUnmodifiableSet());
        return new ServerSession(selection.accountId(), credentials, dispatcher, identity, scopes, selection.account().admin());
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

    private InMemoryToolRegistry registry(NextcloudMcpConfig config, NextcloudClient client, boolean resolveSecret) {
        InMemoryToolRegistry registry = new InMemoryToolRegistry();
        NextcloudFilesTools.registrations(client).forEach(registry::register);
        NextcloudShareTools.registrations(client).forEach(registry::register);
        NextcloudUserTools.registrations(client).forEach(registry::register);
        if (config.admin().enabled()) {
            NextcloudAdminCredentials adminCredentials = adminCredentials(config, resolveSecret);
            NextcloudCredentials httpCredentials = NextcloudCredentials.of(
                    adminCredentials.accountId().value(),
                    adminCredentials.baseUrl(),
                    adminCredentials.username(),
                    adminCredentials.appPassword());
            NextcloudAdminClient adminClient = new NextcloudAdminClient(httpClientFactory.create(httpCredentials), adminCredentials);
            NextcloudAdminTools.registrations(adminClient).forEach(registry::register);
        }
        return registry;
    }

    private NextcloudAdminCredentials adminCredentials(NextcloudMcpConfig config, boolean resolveSecret) {
        if (!config.admin().enabled()) {
            throw new ServerRequestException("admin.disabled", "Nextcloud admin API is not enabled");
        }
        String adminAccountId = required(config.admin().accountId(), "admin account id");
        NextcloudAccountConfig account = config.accounts().get(adminAccountId);
        if (account == null) {
            throw new ServerRequestException("admin.account_not_found", "Admin account was not found: " + adminAccountId);
        }
        if (!account.enabled()) {
            throw new ServerRequestException("admin.account_disabled", "Admin account is disabled: " + adminAccountId);
        }
        if (!account.admin()) {
            throw new ServerRequestException("admin.account_not_marked_admin", "Configured admin account must be marked admin");
        }
        NextcloudAccountConfig normalized = accountWithId(adminAccountId, account);
        if (resolveSecret) {
            NextcloudCredentials credentials = NextcloudCredentials.fromAccount(normalized, secretResolver);
            return NextcloudAdminCredentials.of(
                    credentials.accountId().value(),
                    credentials.baseUrl(),
                    credentials.username(),
                    credentials.appPassword());
        }
        return NextcloudAdminCredentials.of(adminAccountId, normalized.baseUrl(), normalized.username(), "not-used-for-tool-listing");
    }

    private ToolRuntimeContext runtimeContext(ServerSession session, String requestedInvocationId) {
        NextcloudUser identity = session.identity();
        String userId = identity == null || identity.id() == null || identity.id().isBlank()
                ? session.credentials().username()
                : identity.id();
        String displayName = identity == null ? userId : identity.displayName();
        Principal principal = new Principal(new PrincipalId(userId), displayName, session.admin(), session.scopes());
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

    private Map<String, Object> localAccountMap(LocalUserAccountRecord record, boolean includeSecret) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("accountId", record.accountKey());
        values.put("nextcloudAccountId", record.accountId());
        values.put("baseUrl", record.baseUrl());
        values.put("username", record.username());
        put(values, "displayName", record.displayName());
        put(values, "email", record.email());
        values.put("defaultAccount", record.defaultAccount());
        values.put("admin", record.admin());
        values.put("enabled", record.enabled());
        values.put("scopes", record.scopes());
        values.put("hasAppPassword", record.appPassword() != null && !record.appPassword().isBlank());
        if (includeSecret) {
            put(values, "appPassword", record.appPassword());
        }
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

    private static void put(Map<String, Object> values, String name, Object value) {
        if (value != null) {
            values.put(name, value);
        }
    }

    private static String required(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new ServerRequestException("request.invalid", name + " is required");
        }
        return value;
    }

    private static String valueOr(String value, String fallback) {
        return value == null ? fallback : value;
    }

    private static boolean valueOr(Boolean value, boolean fallback) {
        return value == null ? fallback : value;
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
            Set<Scope> scopes,
            boolean admin) {
    }
}
