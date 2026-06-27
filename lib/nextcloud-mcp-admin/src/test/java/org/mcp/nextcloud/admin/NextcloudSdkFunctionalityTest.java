package org.mcp.nextcloud.admin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.mcp.nextcloud.client.NextcloudCapabilities;
import org.mcp.nextcloud.client.NextcloudClient;
import org.mcp.nextcloud.client.NextcloudCredentials;
import org.mcp.nextcloud.client.NextcloudUser;
import org.mcp.nextcloud.client.ShareInfo;
import org.mcp.nextcloud.client.WebDavOperation;
import org.mcp.nextcloud.client.WebDavResource;
import org.mcp.nextcloud.config.ConfigPaths;
import org.mcp.nextcloud.config.EnvironmentSecretResolver;
import org.mcp.nextcloud.config.NextcloudAccountConfig;
import org.mcp.nextcloud.config.NextcloudMcpConfig;
import org.mcp.nextcloud.config.YamlConfigLoader;
import org.mcp.nextcloud.core.error.NextcloudApiException;
import org.mcp.nextcloud.http.JdkHttpClientAdapter;

class NextcloudSdkFunctionalityTest {
    private static final String ENABLED_FLAG = "NC_MCP_SDK_FUNCTIONALITY_TEST_ENABLED";

    @Test
    void liveUserSdkFunctionalityFromConfig() {
        LiveSettings settings = LiveSettings.load();
        Assumptions.assumeTrue(settings.enabled(), enableMessage());

        LiveAccount account = settings.mainAccount()
                .orElseGet(() -> Assumptions.abort("Main account config is incomplete."));
        UserSdkSession session = connectUser(account);
        NextcloudClient client = session.client();

        NextcloudUser user = session.user();
        NextcloudCapabilities capabilities = client.users().capabilities();
        List<WebDavResource> rootFiles = client.files().list(user, settings.rootPath());

        String uploadDir = settings.value("NC_MCP_SMOKE_TEST_UPLOAD_DIR").orElse("/CodexScratch");
        String uploadFile = settings.value("NC_MCP_SMOKE_TEST_UPLOAD_FILE")
                .orElse("nextcloud-sdk-functionality-%s.txt".formatted(
                        DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(OffsetDateTime.now())));
        UploadTarget uploadTarget = uploadTarget(uploadDir, uploadFile);
        byte[] body = ("Nextcloud MCP SDK functionality test " + OffsetDateTime.now())
                .getBytes(StandardCharsets.UTF_8);

        System.out.printf("SDK user identity: id=%s displayName=%s enabled=%s%n",
                user.id(), user.displayName(), user.enabled());
        System.out.printf("SDK capabilities version: %s%n", capabilities.versionString());
        System.out.printf("SDK root listing: path=%s resources=%d%n", settings.rootPath(), rootFiles.size());

        boolean uploaded = false;
        try {
            WebDavOperation mkdir = client.files().mkdir(user, uploadTarget.directory());
            WebDavOperation upload = client.files().upload(user, uploadTarget.path(), body);
            uploaded = true;
            WebDavResource stat = client.files().stat(user, uploadTarget.path());
            byte[] downloaded = client.files().download(user, uploadTarget.path());

            System.out.printf("SDK mkdir: path=%s status=%d%n", uploadTarget.directory(), mkdir.statusCode());
            System.out.printf("SDK upload: path=%s status=%d%n", uploadTarget.path(), upload.statusCode());
            System.out.printf("SDK stat: name=%s size=%s contentType=%s%n",
                    stat.name(), stat.size(), stat.contentType());
            System.out.printf("SDK download: bytes=%d%n", downloaded.length);
            try {
                List<ShareInfo> shares = client.shares().listShares(uploadTarget.path());
                System.out.printf("SDK share listing: path=%s shares=%d%n", uploadTarget.path(), shares.size());
            } catch (NextcloudApiException ex) {
                System.out.printf("SDK share listing skipped after API response: status=%d message=%s%n",
                        ex.statusCode(), ex.getMessage());
            }
        } finally {
            if (uploaded) {
                WebDavOperation delete = client.files().delete(user, uploadTarget.path());
                System.out.printf("SDK cleanup delete: path=%s status=%d%n", uploadTarget.path(), delete.statusCode());
            }
        }
    }

    @Test
    void liveAdminSdkReadOnlyFunctionalityFromConfig() {
        LiveSettings settings = LiveSettings.load();
        Assumptions.assumeTrue(settings.enabled(), enableMessage());
        Assumptions.assumeTrue(settings.adminEnabled(), "Admin config is not enabled.");

        LiveAccount account = settings.adminAccount()
                .orElseGet(() -> Assumptions.abort("Admin account config is incomplete."));
        AdminSdkSession session = connectAdmin(account);
        NextcloudAdminClient admin = session.client();

        AdminIdentity identity = session.identity();
        List<String> users = admin.users().listUsers(null, 10, 0);
        List<String> groups = admin.groups().listGroups(null, 10, 0);
        List<String> apps = admin.apps().listApps();
        List<String> enabledApps = admin.apps().listEnabledApps();
        List<String> disabledApps = admin.apps().listDisabledApps();

        System.out.printf("SDK admin identity: id=%s displayName=%s enabled=%s%n",
                identity.id(), identity.displayName(), identity.enabled());
        System.out.printf("SDK admin users listed: %d%n", users.size());
        System.out.printf("SDK admin groups listed: %d%n", groups.size());
        System.out.printf("SDK admin apps listed: installed=%d enabled=%d disabled=%d%n",
                apps.size(), enabledApps.size(), disabledApps.size());
    }

    private static String enableMessage() {
        return "Set " + ENABLED_FLAG + "=true or NC_MCP_SMOKE_TEST_ENABLED=true to run live SDK functionality tests.";
    }

    private static UserSdkSession connectUser(LiveAccount account) {
        List<NextcloudApiException> authFailures = new ArrayList<>();
        for (String login : account.loginCandidates()) {
            NextcloudClient client = new NextcloudClient(new JdkHttpClientAdapter(), account.credentials(login));
            try {
                return new UserSdkSession(client, client.users().currentUser(), login);
            } catch (NextcloudApiException ex) {
                if (ex.statusCode() == 401 || ex.statusCode() == 403) {
                    authFailures.add(ex);
                    continue;
                }
                throw ex;
            }
        }
        throw authFailures.isEmpty()
                ? new IllegalStateException("No login candidates were available for main SDK account.")
                : authFailures.getLast();
    }

    private static AdminSdkSession connectAdmin(LiveAccount account) {
        List<NextcloudApiException> authFailures = new ArrayList<>();
        for (String login : account.loginCandidates()) {
            NextcloudAdminClient client = new NextcloudAdminClient(new JdkHttpClientAdapter(), account.adminCredentials(login));
            try {
                return new AdminSdkSession(client, client.auth().testAdminIdentity(), login);
            } catch (NextcloudApiException ex) {
                if (ex.statusCode() == 401 || ex.statusCode() == 403) {
                    authFailures.add(ex);
                    continue;
                }
                throw ex;
            }
        }
        throw authFailures.isEmpty()
                ? new IllegalStateException("No login candidates were available for admin SDK account.")
                : authFailures.getLast();
    }

    private static UploadTarget uploadTarget(String directory, String file) {
        String cleanDirectory = directory == null || directory.isBlank() ? "/" : directory.trim();
        String cleanFile = file == null || file.isBlank() ? "nextcloud-sdk-functionality.txt" : file.trim();
        if (!cleanDirectory.startsWith("/")) {
            cleanDirectory = "/" + cleanDirectory;
        }
        while (cleanDirectory.endsWith("/") && cleanDirectory.length() > 1) {
            cleanDirectory = cleanDirectory.substring(0, cleanDirectory.length() - 1);
        }
        while (cleanFile.startsWith("/")) {
            cleanFile = cleanFile.substring(1);
        }
        if (cleanFile.contains("/")) {
            String path = "/" + cleanFile;
            int lastSlash = path.lastIndexOf('/');
            String parent = lastSlash <= 0 ? "/" : path.substring(0, lastSlash);
            return new UploadTarget(parent, path);
        }
        return new UploadTarget(cleanDirectory, cleanDirectory + "/" + cleanFile);
    }

    private record UserSdkSession(NextcloudClient client, NextcloudUser user, String login) {
    }

    private record AdminSdkSession(NextcloudAdminClient client, AdminIdentity identity, String login) {
    }

    private record UploadTarget(String directory, String path) {
    }

    private record LiveAccount(String accountId, String baseUrl, String username, String appPassword) {
        NextcloudCredentials credentials(String login) {
            return NextcloudCredentials.of(accountId, baseUrl, login, appPassword);
        }

        NextcloudAdminCredentials adminCredentials(String login) {
            return NextcloudAdminCredentials.of(accountId, baseUrl, login, appPassword);
        }

        List<String> loginCandidates() {
            LinkedHashSet<String> candidates = new LinkedHashSet<>();
            if (username != null && !username.isBlank()) {
                candidates.add(username);
            }
            if (accountId != null && !accountId.isBlank()) {
                candidates.add(accountId);
            }
            return List.copyOf(candidates);
        }
    }

    private static final class LiveSettings {
        private final Map<String, String> values;
        private final NextcloudMcpConfig config;
        private final EnvironmentSecretResolver secretResolver = new EnvironmentSecretResolver();

        private LiveSettings(Map<String, String> values, NextcloudMcpConfig config) {
            this.values = values;
            this.config = config;
        }

        static LiveSettings load() {
            Map<String, String> values = new LinkedHashMap<>();
            System.getenv().forEach(values::put);
            NextcloudMcpConfig config = ConfigPaths.findFromSystemSources(null)
                    .map(LiveSettings::loadConfig)
                    .orElse(null);
            return new LiveSettings(values, config);
        }

        boolean enabled() {
            return bool("NC_MCP_SDK_FUNCTIONALITY_TEST_ENABLED")
                    || bool("NC_MCP_SMOKE_TEST_ENABLED");
        }

        boolean adminEnabled() {
            return config != null && config.admin().enabled();
        }

        String rootPath() {
            return value("NC_MCP_SMOKE_TEST_ROOT_PATH").orElse("/");
        }

        Optional<LiveAccount> mainAccount() {
            if (config == null) {
                return Optional.empty();
            }
            return config.accounts().entrySet().stream()
                    .filter(entry -> entry.getValue() != null && entry.getValue().defaultAccount())
                    .findFirst()
                    .or(() -> config.accounts().entrySet().stream()
                            .filter(entry -> entry.getValue() != null && entry.getValue().enabled() && !entry.getValue().admin())
                            .findFirst())
                    .flatMap(entry -> account(entry.getKey(), entry.getValue()));
        }

        Optional<LiveAccount> adminAccount() {
            if (config == null || !config.admin().enabled()) {
                return Optional.empty();
            }
            return Optional.ofNullable(config.accounts().get(config.admin().accountId()))
                    .flatMap(account -> account(config.admin().accountId(), account));
        }

        Optional<String> value(String key) {
            String value = values.get(key);
            return value == null || value.isBlank() ? Optional.empty() : Optional.of(value);
        }

        private Optional<LiveAccount> account(String accountKey, NextcloudAccountConfig account) {
            String accountId = firstNonBlank(account.id(), accountKey);
            Optional<String> appPassword = resolveSecret(account.appPassword());
            if (isBlank(account.baseUrl()) || appPassword.isEmpty() || isBlank(account.username())) {
                return Optional.empty();
            }
            return Optional.of(new LiveAccount(accountId, account.baseUrl(), account.username(), appPassword.get()));
        }

        private boolean bool(String key) {
            return value(key)
                    .map(value -> switch (value.trim().toLowerCase(Locale.ROOT)) {
                        case "1", "true", "yes", "y", "on" -> true;
                        default -> false;
                    })
                    .orElse(false);
        }

        private Optional<String> resolveSecret(String value) {
            if (isBlank(value)) {
                return Optional.empty();
            }
            String candidate = value.trim();
            if (!candidate.startsWith("${") || !candidate.endsWith("}")) {
                return Optional.of(candidate);
            }
            String name = candidate.substring(2, candidate.length() - 1).trim();
            return secretResolver.resolve(name);
        }

        private static NextcloudMcpConfig loadConfig(Path path) {
            try {
                return new YamlConfigLoader().load(path);
            } catch (IOException ex) {
                throw new IllegalStateException("Could not read Nextcloud MCP config file: " + path, ex);
            }
        }

        private static String firstNonBlank(String value, String fallback) {
            return isBlank(value) ? fallback : value;
        }

        private static boolean isBlank(String value) {
            return value == null || value.isBlank();
        }
    }
}
