package org.mcp.nextcloud.admin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
import org.mcp.nextcloud.core.error.NextcloudApiException;
import org.mcp.nextcloud.http.JdkHttpClientAdapter;

class NextcloudSdkFunctionalityTest {
    private static final Path REPO_ROOT = findRepoRoot();
    private static final String ENABLED_FLAG = "NC_MCP_SDK_FUNCTIONALITY_TEST_ENABLED";

    @Test
    void liveUserSdkFunctionalityFromScratchEnv() {
        ScratchEnv env = ScratchEnv.load();
        Assumptions.assumeTrue(env.enabled(), enableMessage());

        LiveAccount account = env.mainAccount()
                .orElseGet(() -> Assumptions.abort("Main account env is incomplete."));
        UserSdkSession session = connectUser(account);
        NextcloudClient client = session.client();

        NextcloudUser user = session.user();
        NextcloudCapabilities capabilities = client.users().capabilities();
        List<WebDavResource> rootFiles = client.files().list(user, env.rootPath());

        String uploadDir = env.value("NC_MCP_SMOKE_TEST_UPLOAD_DIR").orElse("/CodexScratch");
        String uploadFile = env.value("NC_MCP_SMOKE_TEST_UPLOAD_FILE")
                .orElse("nextcloud-sdk-functionality-%s.txt".formatted(
                        DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(OffsetDateTime.now())));
        UploadTarget uploadTarget = uploadTarget(uploadDir, uploadFile);
        byte[] body = ("Nextcloud MCP SDK functionality test " + OffsetDateTime.now())
                .getBytes(StandardCharsets.UTF_8);

        System.out.printf("SDK user identity: id=%s displayName=%s enabled=%s%n",
                user.id(), user.displayName(), user.enabled());
        System.out.printf("SDK capabilities version: %s%n", capabilities.versionString());
        System.out.printf("SDK root listing: path=%s resources=%d%n", env.rootPath(), rootFiles.size());

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
    void liveAdminSdkReadOnlyFunctionalityFromScratchEnv() {
        ScratchEnv env = ScratchEnv.load();
        Assumptions.assumeTrue(env.enabled(), enableMessage());
        Assumptions.assumeTrue(env.adminEnabled(), "Admin env is not enabled.");

        LiveAccount account = env.adminAccount()
                .orElseGet(() -> Assumptions.abort("Admin account env is incomplete."));
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

    private static Path findRepoRoot() {
        Path current = Path.of("").toAbsolutePath();
        while (current != null) {
            if (Files.isDirectory(current.resolve("architect"))
                    && Files.isDirectory(current.resolve("scripts"))
                    && Files.isRegularFile(current.resolve("AGENTS.md"))) {
                return current;
            }
            current = current.getParent();
        }
        return Path.of("").toAbsolutePath();
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

    private static final class ScratchEnv {
        private final Map<String, String> values;

        private ScratchEnv(Map<String, String> values) {
            this.values = values;
        }

        static ScratchEnv load() {
            Map<String, String> values = new LinkedHashMap<>();
            readEnvFile(REPO_ROOT.resolve(".env")).forEach(values::putIfAbsent);
            readEnvFile(REPO_ROOT.resolve("scripts/nextcloud/WEBDAV/.env")).forEach(values::putIfAbsent);
            System.getenv().forEach(values::put);
            return new ScratchEnv(values);
        }

        boolean enabled() {
            return bool("NC_MCP_SDK_FUNCTIONALITY_TEST_ENABLED")
                    || bool("NC_MCP_SMOKE_TEST_ENABLED");
        }

        boolean adminEnabled() {
            return bool("NC_MCP_ADMIN_ENABLED");
        }

        String rootPath() {
            return value("NC_MCP_SMOKE_TEST_ROOT_PATH").orElse("/");
        }

        Optional<LiveAccount> mainAccount() {
            return account("NC_MCP_MAIN");
        }

        Optional<LiveAccount> adminAccount() {
            return account("NC_MCP_ADMIN");
        }

        Optional<String> value(String key) {
            String value = values.get(key);
            return value == null || value.isBlank() ? Optional.empty() : Optional.of(value);
        }

        private Optional<LiveAccount> account(String prefix) {
            Optional<String> baseUrl = value(prefix + "_BASE_URL");
            Optional<String> appPassword = value(prefix + "_APP_PASSWORD");
            Optional<String> username = value(prefix + "_USERNAME")
                    .or(() -> value(prefix + "_ACCOUNT_ID"));
            if (baseUrl.isEmpty() || appPassword.isEmpty() || username.isEmpty()) {
                return Optional.empty();
            }
            String accountId = value(prefix + "_ACCOUNT_ID").orElse(username.get());
            return Optional.of(new LiveAccount(accountId, baseUrl.get(), username.get(), appPassword.get()));
        }

        private boolean bool(String key) {
            return value(key)
                    .map(value -> switch (value.trim().toLowerCase(Locale.ROOT)) {
                        case "1", "true", "yes", "y", "on" -> true;
                        default -> false;
                    })
                    .orElse(false);
        }

        private static Map<String, String> readEnvFile(Path path) {
            if (!Files.isRegularFile(path)) {
                return Map.of();
            }
            Map<String, String> parsed = new LinkedHashMap<>();
            try {
                for (String rawLine : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                    String line = rawLine.trim();
                    if (line.isBlank() || line.startsWith("#") || !line.contains("=")) {
                        continue;
                    }
                    int separator = line.indexOf('=');
                    String key = line.substring(0, separator).trim();
                    String value = unquote(line.substring(separator + 1).trim());
                    if (!key.isBlank()) {
                        parsed.put(key, value);
                    }
                }
            } catch (IOException ex) {
                throw new IllegalStateException("Could not read scratch env file: " + path, ex);
            }
            return parsed;
        }

        private static String unquote(String value) {
            if (value.length() >= 2) {
                char first = value.charAt(0);
                char last = value.charAt(value.length() - 1);
                if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                    return value.substring(1, value.length() - 1);
                }
            }
            return value;
        }
    }
}
