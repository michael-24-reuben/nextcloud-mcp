package org.mcp.nextcloud.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

class NextcloudAdminGroupsClientTest {
    @Test
    void listCreateAndReadGroupsUseGroupProvisioningRoutes() {
        RecordingHttpClient http = new RecordingHttpClient();
        http.enqueue(json("""
                {"ocs":{"meta":{"status":"ok","statuscode":100,"message":"OK"},"data":{"groups":["members","reviewers"]}}}
                """));
        http.enqueue(json("""
                {"ocs":{"meta":{"status":"ok","statuscode":100,"message":"OK"},"data":{"id":"reviewers","displayname":"Reviewers"}}}
                """));
        http.enqueue(json("""
                {"ocs":{"meta":{"status":"ok","statuscode":100,"message":"OK"},"data":{"users":["temporary"]}}}
                """));
        http.enqueue(json("""
                {"ocs":{"meta":{"status":"ok","statuscode":100,"message":"OK"},"data":{"subadmins":["temporary"]}}}
                """));
        NextcloudAdminClient client = client(http);

        List<String> groups = client.groups().listGroups("rev", 20, 0);
        AdminGroup created = client.groups().createGroup("reviewers");
        List<String> members = client.groups().getGroupMembers("reviewers");
        List<String> subadmins = client.groups().getGroupSubadmins("reviewers");

        assertEquals(List.of("members", "reviewers"), groups);
        assertEquals("reviewers", created.id());
        assertEquals(List.of("temporary"), members);
        assertEquals(List.of("temporary"), subadmins);
        assertEquals("https://cloud.example.com/ocs/v1.php/cloud/groups?search=rev&limit=20&offset=0", http.requests().get(0).uri().toString());
        assertEquals("groupid=reviewers", body(http.requests().get(1)));
        assertEquals("https://cloud.example.com/ocs/v1.php/cloud/groups/reviewers", http.requests().get(2).uri().toString());
        assertEquals("https://cloud.example.com/ocs/v1.php/cloud/groups/reviewers/subadmins", http.requests().get(3).uri().toString());
    }

    @Test
    void groupMutationsUseEncodedGroupRoutes() {
        RecordingHttpClient http = new RecordingHttpClient();
        http.enqueue(ok());
        http.enqueue(ok());
        NextcloudAdminClient client = client(http);

        client.groups().updateGroupDisplayName("space group", "Space Group");
        client.groups().deleteGroup("space group");

        assertEquals("https://cloud.example.com/ocs/v1.php/cloud/groups/space%20group", http.requests().get(0).uri().toString());
        assertEquals("key=displayname&value=Space%20Group", body(http.requests().get(0)));
        assertEquals(HttpMethod.PUT, http.requests().get(0).method());
        assertEquals("https://cloud.example.com/ocs/v1.php/cloud/groups/space%20group", http.requests().get(1).uri().toString());
        assertEquals(HttpMethod.DELETE, http.requests().get(1).method());
    }

    @Test
    void membershipAndSubadminMutationsUseUserProvisioningRoutes() {
        RecordingHttpClient http = new RecordingHttpClient();
        http.enqueue(ok());
        http.enqueue(ok());
        http.enqueue(ok());
        http.enqueue(ok());
        NextcloudAdminClient client = client(http);

        client.groups().addUserToGroup("space user", "members");
        client.groups().removeUserFromGroup("space user", "members");
        client.groups().promoteSubadmin("space user", "members");
        client.groups().demoteSubadmin("space user", "members");

        assertEquals("https://cloud.example.com/ocs/v1.php/cloud/users/space%20user/groups", http.requests().get(0).uri().toString());
        assertEquals(HttpMethod.POST, http.requests().get(0).method());
        assertEquals("groupid=members", body(http.requests().get(0)));
        assertEquals(HttpMethod.DELETE, http.requests().get(1).method());
        assertEquals("groupid=members", body(http.requests().get(1)));
        assertEquals("https://cloud.example.com/ocs/v1.php/cloud/users/space%20user/subadmins", http.requests().get(2).uri().toString());
        assertEquals(HttpMethod.POST, http.requests().get(2).method());
        assertEquals(HttpMethod.DELETE, http.requests().get(3).method());
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
