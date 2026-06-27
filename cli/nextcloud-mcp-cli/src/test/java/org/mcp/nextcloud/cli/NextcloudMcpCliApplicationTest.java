package org.mcp.nextcloud.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mcp.nextcloud.client.NextcloudCredentials;
import org.mcp.nextcloud.config.EnvironmentSecretResolver;
import org.mcp.nextcloud.config.YamlConfigLoader;
import org.mcp.nextcloud.config.validation.ConfigValidator;
import org.mcp.nextcloud.http.HttpClientAdapter;
import org.mcp.nextcloud.http.HttpMethod;
import org.mcp.nextcloud.http.HttpRequestSpec;
import org.mcp.nextcloud.http.HttpResponseSpec;

class NextcloudMcpCliApplicationTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @TempDir
    Path tempDir;

    @Test
    void configCheckReportsValidConfigWithoutPrintingSecrets() throws Exception {
        Path config = writeConfig("local", "temporary");
        RecordingHttpClient http = new RecordingHttpClient();
        CliRun run = run(http, "--config", config.toString(), "--json", "config", "check");

        assertEquals(0, run.exitCode());
        assertTrue(run.stdout().contains("\"valid\" : true"));
        assertFalse(run.stdout().contains("local-app-password"));
        assertTrue(http.requests().isEmpty());
    }

    @Test
    void toolsListUsesRuntimeDescriptors() throws Exception {
        Path config = writeConfig("local", "temporary");
        RecordingHttpClient http = new RecordingHttpClient();
        CliRun run = run(http, "--config", config.toString(), "--json", "tools", "list");

        assertEquals(0, run.exitCode());
        JsonNode root = objectMapper.readTree(run.stdout());
        List<String> names = new ArrayList<>();
        root.path("tools").forEach(tool -> names.add(tool.path("name").asText()));
        assertTrue(names.contains("nextcloud.files.list"));
        assertTrue(names.contains("nextcloud.shares.create"));
        assertTrue(names.contains("nextcloud.user.me"));
        assertFalse(names.contains("nextcloud.admin.users.list"));
        assertTrue(http.requests().isEmpty());
    }

    @Test
    void toolsListDoesNotResolveAppPasswordSecrets() throws Exception {
        Path config = writeConfig("local", "temporary", "${NC_MCP_MISSING_TEST_SECRET}", List.of(
                "nextcloud.files.read",
                "nextcloud.user.read"));
        RecordingHttpClient http = new RecordingHttpClient();

        CliRun run = run(http, "--config", config.toString(), "--json", "tools", "list");

        assertEquals(0, run.exitCode(), run.stderr());
        assertTrue(run.stdout().contains("nextcloud.files.list"));
        assertTrue(http.requests().isEmpty());
    }

    @Test
    void accountsTestProbesCurrentUserOnly() throws Exception {
        Path config = writeConfig("local", "temporary");
        RecordingHttpClient http = new RecordingHttpClient();
        http.enqueue(ocsUser("temporary", "Tempo"));

        CliRun run = run(http, "--config", config.toString(), "--json", "accounts", "test", "local");

        assertEquals(0, run.exitCode());
        JsonNode root = objectMapper.readTree(run.stdout());
        assertTrue(root.path("connected").asBoolean());
        assertEquals("local", root.path("accountId").asText());
        assertEquals("temporary", root.path("userId").asText());
        assertEquals(1, http.requests().size());
        assertEquals("https://cloud.example.com/ocs/v1.php/cloud/user", http.requests().getFirst().uri().toString());
    }

    @Test
    void callUsesResolvedUserIdForFileToolWebDavPath() throws Exception {
        Path config = writeConfig("profile-key", "temporary");
        RecordingHttpClient http = new RecordingHttpClient();
        http.enqueue(ocsUser("temporary", "Tempo"));
        http.enqueue(xml("""
                <?xml version="1.0" encoding="utf-8"?>
                <d:multistatus xmlns:d="DAV:" xmlns:oc="http://owncloud.org/ns">
                  <d:response>
                    <d:href>/remote.php/dav/files/temporary/Docs/</d:href>
                    <d:propstat><d:prop><d:resourcetype><d:collection/></d:resourcetype></d:prop></d:propstat>
                  </d:response>
                  <d:response>
                    <d:href>/remote.php/dav/files/temporary/Docs/note.txt</d:href>
                    <d:propstat><d:prop>
                      <d:getcontentlength>5</d:getcontentlength>
                      <d:getcontenttype>text/plain</d:getcontenttype>
                      <oc:owner-id>temporary</oc:owner-id>
                    </d:prop></d:propstat>
                  </d:response>
                </d:multistatus>
                """));

        CliRun run = run(http,
                "--config", config.toString(),
                "--json",
                "call", "nextcloud.files.list",
                "--arg", "path=/Docs");

        assertEquals(0, run.exitCode(), run.stderr());
        JsonNode root = objectMapper.readTree(run.stdout());
        assertTrue(root.path("success").asBoolean());
        assertEquals(
                "https://cloud.example.com/remote.php/dav/files/temporary/Docs/",
                http.requests().get(1).uri().toString());
        assertFalse(http.requests().get(1).uri().toString().contains("profile-key"));
    }

    @Test
    void callDeniesToolWhenAccountScopeIsMissing() throws Exception {
        Path config = writeConfig("local", "temporary", List.of("nextcloud.user.read"));
        RecordingHttpClient http = new RecordingHttpClient();
        http.enqueue(ocsUser("temporary", "Tempo"));

        CliRun run = run(http,
                "--config", config.toString(),
                "--json",
                "call", "nextcloud.files.list",
                "--arg", "path=/Docs");

        assertEquals(2, run.exitCode(), "stdout=" + run.stdout() + " stderr=" + run.stderr());
        JsonNode root = objectMapper.readTree(run.stdout());
        assertFalse(root.path("success").asBoolean());
        assertEquals("tool.policy_denied", root.path("error").path("code").asText());
        assertEquals(1, http.requests().size());
    }

    private CliRun run(RecordingHttpClient http, String... args) {
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        NextcloudMcpCliApplication app = new NextcloudMcpCliApplication(
                new YamlConfigLoader(),
                new ConfigValidator(),
                new EnvironmentSecretResolver(),
                credentials -> http,
                objectMapper);
        int exitCode = app.run(args, new PrintStream(stdout), new PrintStream(stderr));
        return new CliRun(
                exitCode,
                stdout.toString(StandardCharsets.UTF_8),
                stderr.toString(StandardCharsets.UTF_8));
    }

    private Path writeConfig(String accountId, String username) throws Exception {
        return writeConfig(accountId, username, List.of(
                "nextcloud.files.read",
                "nextcloud.files.write",
                "nextcloud.files.delete",
                "nextcloud.shares.read",
                "nextcloud.shares.write",
                "nextcloud.user.read"));
    }

    private Path writeConfig(String accountId, String username, List<String> scopes) throws Exception {
        return writeConfig(accountId, username, "local-app-password", scopes);
    }

    private Path writeConfig(String accountId, String username, String appPassword, List<String> scopes) throws Exception {
        Path config = tempDir.resolve("nextcloud-mcp.yml");
        String scopeLines = scopes.stream()
                .map(scope -> "      - " + scope)
                .reduce("", (left, right) -> left + right + "\n");
        Files.writeString(config, """
                accounts:
                  %s:
                    baseUrl: https://cloud.example.com
                    username: %s
                    appPassword: %s
                    defaultAccount: true
                    admin: false
                    enabled: true
                    scopes:
                %s
                """.formatted(accountId, username, appPassword, scopeLines), StandardCharsets.UTF_8);
        return config;
    }

    private static HttpResponseSpec ocsUser(String id, String displayName) {
        String body = """
                {"ocs":{"meta":{"status":"ok","statuscode":100,"message":"OK"},"data":{"id":"%s","display-name":"%s","enabled":true}}}
                """.formatted(id, displayName);
        return new HttpResponseSpec(200, Map.of("Content-Type", List.of("application/json")),
                body.getBytes(StandardCharsets.UTF_8));
    }

    private static HttpResponseSpec xml(String body) {
        return new HttpResponseSpec(207, Map.of("Content-Type", List.of("application/xml")),
                body.getBytes(StandardCharsets.UTF_8));
    }

    private record CliRun(int exitCode, String stdout, String stderr) {
    }

    private static final class RecordingHttpClient implements HttpClientAdapter {
        private final ArrayDeque<HttpResponseSpec> responses = new ArrayDeque<>();
        private final List<HttpRequestSpec> requests = new ArrayList<>();

        @Override
        public HttpResponseSpec send(HttpRequestSpec request) {
            requests.add(request);
            if (responses.isEmpty()) {
                throw new AssertionError("No fake HTTP response for " + request.method() + " " + request.uri());
            }
            return responses.removeFirst();
        }

        void enqueue(HttpResponseSpec response) {
            responses.addLast(response);
        }

        List<HttpRequestSpec> requests() {
            return requests;
        }
    }
}
