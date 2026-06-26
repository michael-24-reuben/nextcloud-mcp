package org.mcp.nextcloud.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mcp.nextcloud.http.HttpClientAdapter;
import org.mcp.nextcloud.http.HttpMethod;
import org.mcp.nextcloud.http.HttpRequestSpec;
import org.mcp.nextcloud.http.HttpResponseSpec;

class NextcloudAdminUsersClientTest {
    @Test
    void listAndGetUsersUseProvisioningRoutes() {
        RecordingHttpClient http = new RecordingHttpClient();
        http.enqueue(json("""
                {"ocs":{"meta":{"status":"ok","statuscode":100,"message":"OK"},"data":{"users":["admin","temporary"]}}}
                """));
        http.enqueue(json("""
                {"ocs":{"meta":{"status":"ok","statuscode":100,"message":"OK"},"data":{
                  "id":"temporary",
                  "displayname":"tempo",
                  "email":"tempo@example.com",
                  "enabled":true,
                  "quota":"default"
                }}}
                """));
        NextcloudAdminClient client = client(http);

        List<String> users = client.users().listUsers("temp", 10, 5);
        AdminUser user = client.users().getUser("temporary");

        assertEquals(List.of("admin", "temporary"), users);
        assertEquals("temporary", user.id());
        assertEquals("tempo", user.displayName());
        HttpRequestSpec list = http.requests().get(0);
        assertEquals(HttpMethod.GET, list.method());
        assertEquals("https://cloud.example.com/ocs/v1.php/cloud/users?search=temp&limit=10&offset=5", list.uri().toString());
        HttpRequestSpec get = http.requests().get(1);
        assertEquals("https://cloud.example.com/ocs/v1.php/cloud/users/temporary", get.uri().toString());
    }

    @Test
    void createUserPreservesRepeatedGroupAndSubadminFields() {
        RecordingHttpClient http = new RecordingHttpClient();
        http.enqueue(json("""
                {"ocs":{"meta":{"status":"ok","statuscode":100,"message":"OK"},"data":{"id":"new-user"}}}
                """));
        NextcloudAdminClient client = client(http);
        AdminUserCreateRequest request = new AdminUserCreateRequest(
                "new-user",
                "temp-pass",
                "New User",
                "new@example.com",
                List.of("members", "reviewers"),
                List.of("members"),
                "1 GB",
                "en");

        AdminUserCreated created = client.users().createUser(request);

        assertEquals("new-user", created.id());
        HttpRequestSpec create = http.requests().getFirst();
        assertEquals(HttpMethod.POST, create.method());
        assertEquals("https://cloud.example.com/ocs/v1.php/cloud/users", create.uri().toString());
        assertEquals(
                "userid=new-user&password=temp-pass&displayName=New%20User&email=new%40example.com"
                        + "&groups%5B%5D=members&groups%5B%5D=reviewers&subadmin%5B%5D=members&quota=1%20GB&language=en",
                body(create));
    }

    @Test
    void userMutationsUseEncodedProvisioningRoutes() {
        RecordingHttpClient http = new RecordingHttpClient();
        http.enqueue(ok());
        http.enqueue(ok());
        http.enqueue(ok());
        http.enqueue(ok());
        NextcloudAdminClient client = client(http);

        client.users().updateUserField("space user", "displayname", "Space User");
        client.users().disableUser("space user");
        client.users().enableUser("space user");
        client.users().deleteUser("space user");

        assertEquals("https://cloud.example.com/ocs/v1.php/cloud/users/space%20user", http.requests().get(0).uri().toString());
        assertEquals("key=displayname&value=Space%20User", body(http.requests().get(0)));
        assertEquals("https://cloud.example.com/ocs/v1.php/cloud/users/space%20user/disable", http.requests().get(1).uri().toString());
        assertEquals(HttpMethod.PUT, http.requests().get(1).method());
        assertEquals("https://cloud.example.com/ocs/v1.php/cloud/users/space%20user/enable", http.requests().get(2).uri().toString());
        assertEquals("https://cloud.example.com/ocs/v1.php/cloud/users/space%20user", http.requests().get(3).uri().toString());
        assertEquals(HttpMethod.DELETE, http.requests().get(3).method());
    }

    @Test
    void readsEditableFieldsGroupsSubadminsAndWelcomeEndpoint() {
        RecordingHttpClient http = new RecordingHttpClient();
        http.enqueue(json("""
                {"ocs":{"meta":{"status":"ok","statuscode":100,"message":"OK"},"data":["email","displayname","quota"]}}
                """));
        http.enqueue(json("""
                {"ocs":{"meta":{"status":"ok","statuscode":100,"message":"OK"},"data":{"groups":["members"]}}}
                """));
        http.enqueue(json("""
                {"ocs":{"meta":{"status":"ok","statuscode":100,"message":"OK"},"data":{"subadmins":["members"]}}}
                """));
        http.enqueue(ok());
        NextcloudAdminClient client = client(http);

        List<String> fields = client.users().editableFields();
        List<String> groups = client.users().getUserGroups("temporary");
        List<String> subadmins = client.users().getSubadminGroups("temporary");
        AdminProvisioningOperation welcome = client.users().resendWelcomeEmail("temporary");

        assertEquals(List.of("email", "displayname", "quota"), fields);
        assertEquals(List.of("members"), groups);
        assertEquals(List.of("members"), subadmins);
        assertEquals("/ocs/v1.php/cloud/users/temporary/welcome", welcome.endpoint());
        assertTrue(http.requests().stream().anyMatch(request -> request.uri().toString().endsWith("/cloud/user/fields")));
        assertTrue(http.requests().stream().anyMatch(request -> request.uri().toString().endsWith("/cloud/users/temporary/groups")));
        assertTrue(http.requests().stream().anyMatch(request -> request.uri().toString().endsWith("/cloud/users/temporary/subadmins")));
        assertEquals(HttpMethod.POST, http.requests().get(3).method());
    }

    private static NextcloudAdminClient client(RecordingHttpClient http) {
        return new NextcloudAdminClient(http, NextcloudAdminCredentials.of(
                "admin", "https://cloud.example.com/", "admin", "app-password"));
    }

    private static HttpResponseSpec json(String body) {
        return new HttpResponseSpec(200, Map.of("Content-Type", List.of("application/json")), body.getBytes(StandardCharsets.UTF_8));
    }

    private static HttpResponseSpec ok() {
        return json("""
                {"ocs":{"meta":{"status":"ok","statuscode":100,"message":"OK"},"data":[]}}
                """);
    }

    private static String body(HttpRequestSpec request) {
        return new String(request.body(), StandardCharsets.UTF_8);
    }

    private static final class RecordingHttpClient implements HttpClientAdapter {
        private final ArrayDeque<HttpResponseSpec> responses = new ArrayDeque<>();
        private final List<HttpRequestSpec> requests = new ArrayList<>();

        @Override
        public HttpResponseSpec send(HttpRequestSpec request) {
            requests.add(request);
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
