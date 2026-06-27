package org.mcp.nextcloud.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.mcp.nextcloud.config.EnvironmentSecretResolver;
import org.mcp.nextcloud.config.YamlConfigLoader;
import org.mcp.nextcloud.config.validation.ConfigValidator;
import org.mcp.nextcloud.http.HttpClientAdapter;
import org.mcp.nextcloud.http.HttpRequestSpec;
import org.mcp.nextcloud.http.HttpResponseSpec;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class NextcloudMcpRuntimeServiceTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @TempDir
    Path tempDir;

    @Test
    void toolsEndpointListsRuntimeDescriptorsWithoutResolvingSecretsOrCallingNextcloud() throws Exception {
        RecordingHttpClient http = new RecordingHttpClient();
        MockMvc mvc = mockMvc(writeConfig("local", "temporary"), http);

        String body = mvc.perform(get("/api/v1/tools"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(body);
        List<String> names = new ArrayList<>();
        root.path("tools").forEach(tool -> names.add(tool.path("name").asText()));
        assertTrue(names.contains("nextcloud.files.list"));
        assertTrue(names.contains("nextcloud.shares.create"));
        assertTrue(names.contains("nextcloud.user.me"));
        assertFalse(names.contains("nextcloud.admin.users.list"));
        assertTrue(http.requests().isEmpty());
    }

    @Test
    void toolsEndpointListsAdminDescriptorsWhenAdminConfigIsEnabled() throws Exception {
        RecordingHttpClient http = new RecordingHttpClient();
        MockMvc mvc = mockMvc(writeAdminConfig("admin", "admin"), http);

        String body = mvc.perform(get("/api/v1/tools"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(body);
        List<String> names = new ArrayList<>();
        root.path("tools").forEach(tool -> names.add(tool.path("name").asText()));
        assertTrue(names.contains("nextcloud.admin.users.create"));
        assertTrue(names.contains("nextcloud.admin.groups.delete"));
        assertTrue(names.contains("nextcloud.admin.occ.maintenance_mode"));
        assertTrue(http.requests().isEmpty());
    }

    @Test
    void accountCreateRoutePersistsLocalEnvRecordWithoutEchoingSecret() throws Exception {
        RecordingHttpClient http = new RecordingHttpClient();
        Path config = writeConfig("local", "temporary");
        MockMvc mvc = mockMvc(config, http);

        String body = mvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountKey": "member",
                                  "accountName": "member-login",
                                  "baseUrl": "https://cloud.example.com",
                                  "appPassword": "member-app-password",
                                  "displayName": "Member User",
                                  "email": "member@example.com",
                                  "defaultAccount": false,
                                  "admin": false,
                                  "enabled": true,
                                  "scopes": ["nextcloud.files.read"]
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(body);
        assertEquals("member", root.path("accountKey").asText());
        assertEquals("member-login", root.path("accountName").asText());
        assertTrue(root.path("hasAppPassword").asBoolean());
        assertFalse(root.has("appPassword"));

        Path env = config.getParent().resolve("db").resolve("u").resolve("usr-member.env");
        String envContent = Files.readString(env, StandardCharsets.UTF_8);
        assertTrue(envContent.contains("ACCOUNT_NAME=member-login"));
        assertTrue(envContent.contains("APP_PASSWORD=member-app-password"));
    }

    @Test
    void adminUsersRouteUsesProvisioningApi() throws Exception {
        RecordingHttpClient http = new RecordingHttpClient();
        http.enqueue(ocsList("users", List.of("admin", "temporary")));
        MockMvc mvc = mockMvc(writeAdminConfig("admin", "admin"), http);

        String body = mvc.perform(get("/api/v1/admin/users")
                        .queryParam("search", "temp")
                        .queryParam("limit", "5")
                        .queryParam("offset", "0"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(body);
        assertEquals("admin", root.path("users").get(0).asText());
        assertEquals(1, http.requests().size());
        assertEquals(
                "https://cloud.example.com/ocs/v1.php/cloud/users?search=temp&limit=5&offset=0",
                http.requests().getFirst().uri().toString());
    }

    @Test
    void accountTestEndpointProbesCurrentUserOnly() throws Exception {
        RecordingHttpClient http = new RecordingHttpClient();
        http.enqueue(ocsUser("temporary", "Tempo"));
        MockMvc mvc = mockMvc(writeConfig("local", "temporary"), http);

        String body = mvc.perform(post("/api/v1/accounts/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"accountKey":"local"}
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(body);
        assertTrue(root.path("connected").asBoolean());
        assertEquals("local", root.path("accountKey").asText());
        assertEquals("temporary", root.path("accountName").asText());
        assertEquals("temporary", root.path("userId").asText());
        assertEquals(1, http.requests().size());
        assertEquals("https://cloud.example.com/ocs/v1.php/cloud/user", http.requests().getFirst().uri().toString());
    }

    @Test
    void nextcloudApiFailuresAreReturnedAsControlledGatewayErrors() throws Exception {
        RecordingHttpClient http = new RecordingHttpClient();
        http.enqueue(new HttpResponseSpec(
                502,
                Map.of("Content-Type", List.of("text/plain")),
                "Bad gateway".getBytes(StandardCharsets.UTF_8)));
        MockMvc mvc = mockMvc(writeConfig("local", "temporary"), http);

        String body = mvc.perform(post("/api/v1/accounts/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"accountKey":"local"}
                                """))
                .andExpect(status().isBadGateway())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(body);
        assertFalse(root.path("success").asBoolean());
        assertEquals("nextcloud_unexpected_status", root.path("error").path("code").asText());
        assertEquals(502, root.path("error").path("nextcloudStatus").asInt());
    }

    @Test
    void jsonRpcToolCallUsesResolvedUserIdForWebDavPath() throws Exception {
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
        MockMvc mvc = mockMvc(writeConfig("profile-key", "temporary"), http);

        String body = mvc.perform(post("/mcp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "jsonrpc": "2.0",
                                  "id": 7,
                                  "method": "tools/call",
                                  "params": {
                                    "name": "nextcloud.files.list",
                                    "arguments": {
                                      "path": "/Docs"
                                    }
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode root = objectMapper.readTree(body);
        assertEquals(7, root.path("id").asInt());
        assertTrue(root.path("result").path("success").asBoolean(), body);
        assertEquals(2, http.requests().size());
        assertEquals(
                "https://cloud.example.com/remote.php/dav/files/temporary/Docs/",
                http.requests().get(1).uri().toString());
        assertFalse(http.requests().get(1).uri().toString().contains("profile-key"));
    }

    @Test
    void mcpToolsAliasDelegatesToToolListing() throws Exception {
        RecordingHttpClient http = new RecordingHttpClient();
        MockMvc mvc = mockMvc(writeConfig("local", "temporary"), http);

        String body = mvc.perform(get("/mcp/tools"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(body.contains("nextcloud.files.list"));
        assertTrue(http.requests().isEmpty());
    }

    private MockMvc mockMvc(Path config, RecordingHttpClient http) {
        NextcloudMcpRuntimeService service = new NextcloudMcpRuntimeService(
                new NextcloudMcpServerProperties(config.toString(), null, false),
                new YamlConfigLoader(),
                new ConfigValidator(),
                new EnvironmentSecretResolver(),
                credentials -> http);
        return MockMvcBuilders.standaloneSetup(
                        new NextcloudMcpApiController(service),
                        new NextcloudMcpAdminController(service),
                        new McpJsonRpcController(service, objectMapper))
                .setControllerAdvice(new NextcloudMcpExceptionHandler())
                .build();
    }

    private Path writeConfig(String accountId, String username) throws Exception {
        Path config = tempDir.resolve("nextcloud-mcp.yml");
        Files.writeString(config, """
                accounts:
                  %s:
                    baseUrl: https://cloud.example.com
                    username: %s
                    appPassword: local-app-password
                    defaultAccount: true
                    admin: false
                    enabled: true
                    scopes:
                      - nextcloud.files.read
                      - nextcloud.files.write
                      - nextcloud.files.delete
                      - nextcloud.shares.read
                      - nextcloud.shares.write
                      - nextcloud.user.read
                """.formatted(accountId, username), StandardCharsets.UTF_8);
        return config;
    }

    private Path writeAdminConfig(String accountId, String username) throws Exception {
        Path config = tempDir.resolve("nextcloud-mcp-admin.yml");
        Files.writeString(config, """
                admin:
                  enabled: true
                  accountId: %s
                accounts:
                  %s:
                    baseUrl: https://cloud.example.com
                    username: %s
                    appPassword: admin-app-password
                    defaultAccount: true
                    admin: true
                    enabled: true
                    scopes:
                      - nextcloud.files.read
                      - nextcloud.files.write
                      - nextcloud.files.delete
                      - nextcloud.shares.read
                      - nextcloud.shares.write
                      - nextcloud.user.read
                      - nextcloud.admin.read
                      - nextcloud.admin.write
                      - nextcloud.admin.delete
                      - nextcloud.admin.apps
                      - nextcloud.admin.occ
                """.formatted(accountId, accountId, username), StandardCharsets.UTF_8);
        return config;
    }

    private static HttpResponseSpec ocsUser(String id, String displayName) {
        String body = """
                {"ocs":{"meta":{"status":"ok","statuscode":100,"message":"OK"},"data":{"id":"%s","display-name":"%s","enabled":true}}}
                """.formatted(id, displayName);
        return new HttpResponseSpec(200, Map.of("Content-Type", List.of("application/json")),
                body.getBytes(StandardCharsets.UTF_8));
    }

    private static HttpResponseSpec ocsList(String name, List<String> values) {
        String items = values.stream()
                .map(value -> "\"" + value + "\"")
                .collect(java.util.stream.Collectors.joining(","));
        String body = """
                {"ocs":{"meta":{"status":"ok","statuscode":100,"message":"OK"},"data":{"%s":[%s]}}}
                """.formatted(name, items);
        return new HttpResponseSpec(200, Map.of("Content-Type", List.of("application/json")),
                body.getBytes(StandardCharsets.UTF_8));
    }

    private static HttpResponseSpec xml(String body) {
        return new HttpResponseSpec(207, Map.of("Content-Type", List.of("application/xml")),
                body.getBytes(StandardCharsets.UTF_8));
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
