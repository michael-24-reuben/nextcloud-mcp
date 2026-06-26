package org.mcp.nextcloud.client;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mcp.nextcloud.http.BasicAuth;
import org.mcp.nextcloud.http.HttpClientAdapter;
import org.mcp.nextcloud.http.HttpMethod;
import org.mcp.nextcloud.http.HttpRequestSpec;
import org.mcp.nextcloud.http.HttpResponseSpec;

class NextcloudClientTest {
    @Test
    void currentUserUsesOcsHeadersAndKeepsDisplayNameSeparateFromUserId() {
        RecordingHttpClient http = new RecordingHttpClient();
        http.enqueue(json("""
                {"ocs":{"meta":{"status":"ok","statuscode":100,"message":"OK"},"data":{
                  "id":"temporary",
                  "display-name":"tempo",
                  "email":null,
                  "enabled":true
                }}}
                """));
        NextcloudClient client = client(http);

        NextcloudUser user = client.users().currentUser();

        assertEquals("temporary", user.id());
        assertEquals("tempo", user.displayName());
        HttpRequestSpec request = http.requests().getFirst();
        assertEquals(HttpMethod.GET, request.method());
        assertEquals("https://cloud.example.com/ocs/v1.php/cloud/user", request.uri().toString());
        assertHeader(request, "OCS-APIRequest", "true");
        assertHeader(request, "Accept", "application/json");
        assertHeader(request, "Authorization", BasicAuth.authorizationHeader("temporary", "app-password"));
    }

    @Test
    void webDavListUsesCurrentUserIdPathAndParsesMultistatus() {
        RecordingHttpClient http = new RecordingHttpClient();
        http.enqueue(json("""
                {"ocs":{"meta":{"status":"ok","statuscode":100,"message":"OK"},"data":{
                  "id":"temporary",
                  "display-name":"tempo"
                }}}
                """));
        http.enqueue(xml("""
                <?xml version="1.0" encoding="utf-8"?>
                <d:multistatus xmlns:d="DAV:" xmlns:oc="http://owncloud.org/ns">
                  <d:response>
                    <d:href>/remote.php/dav/files/temporary/Codex%20Scratch/</d:href>
                    <d:propstat><d:prop><d:resourcetype><d:collection/></d:resourcetype></d:prop></d:propstat>
                  </d:response>
                  <d:response>
                    <d:href>/remote.php/dav/files/temporary/Codex%20Scratch/note%201.txt</d:href>
                    <d:propstat><d:prop>
                      <d:getcontentlength>5</d:getcontentlength>
                      <d:getcontenttype>text/plain</d:getcontenttype>
                      <d:getetag>&quot;abc&quot;</d:getetag>
                      <oc:favorite>1</oc:favorite>
                      <oc:permissions>RGDNVW</oc:permissions>
                      <oc:owner-id>temporary</oc:owner-id>
                      <oc:owner-display-name>tempo</oc:owner-display-name>
                    </d:prop></d:propstat>
                  </d:response>
                </d:multistatus>
                """));
        NextcloudClient client = client(http);

        NextcloudUser user = client.users().currentUser();
        List<WebDavResource> resources = client.files().list(user, "/Codex Scratch");

        assertEquals(1, resources.size());
        WebDavResource file = resources.getFirst();
        assertEquals("note 1.txt", file.name());
        assertEquals(5L, file.size());
        assertEquals("text/plain", file.contentType());
        assertEquals(1, file.favorite());
        assertEquals("temporary", file.ownerId());

        HttpRequestSpec propfind = http.requests().get(1);
        assertEquals(HttpMethod.PROPFIND, propfind.method());
        assertEquals("https://cloud.example.com/remote.php/dav/files/temporary/Codex%20Scratch/", propfind.uri().toString());
        assertFalse(propfind.uri().toString().contains("/tempo/"));
        assertHeader(propfind, "Depth", "1");
    }

    @Test
    void webDavWriteOperationsUseCollectionSlashAndReturnBodyBytes() {
        RecordingHttpClient http = new RecordingHttpClient();
        http.enqueue(empty(201));
        http.enqueue(empty(201));
        http.enqueue(new HttpResponseSpec(200, Map.of(), "hello".getBytes(StandardCharsets.UTF_8)));
        NextcloudClient client = client(http);

        WebDavOperation mkdir = client.files().mkdir("temporary", "/CodexScratch");
        WebDavOperation upload = client.files().upload("temporary", "/CodexScratch/file.txt", "hello".getBytes(StandardCharsets.UTF_8));
        byte[] downloaded = client.files().download("temporary", "/CodexScratch/file.txt");

        assertEquals(201, mkdir.statusCode());
        assertEquals(201, upload.statusCode());
        assertArrayEquals("hello".getBytes(StandardCharsets.UTF_8), downloaded);
        assertEquals("https://cloud.example.com/remote.php/dav/files/temporary/CodexScratch/", http.requests().get(0).uri().toString());
        assertEquals("https://cloud.example.com/remote.php/dav/files/temporary/CodexScratch/file.txt", http.requests().get(1).uri().toString());
    }

    @Test
    void sharesAndShareesUseOcsV2Shapes() {
        RecordingHttpClient http = new RecordingHttpClient();
        http.enqueue(json("""
                {"ocs":{"meta":{"status":"ok","statuscode":100,"message":"OK"},"data":{
                  "id":"42",
                  "path":"/CodexScratch/file.txt",
                  "share_type":3,
                  "url":"https://cloud.example.com/s/abc",
                  "permissions":1,
                  "token":"abc"
                }}}
                """));
        http.enqueue(json("""
                {"ocs":{"meta":{"status":"ok","statuscode":100,"message":"OK"},"data":{
                  "exact":{"users":[{"label":"Michael","value":{"shareWith":"temporary"},"shareType":0}]},
                  "users":[{"label":"Other","value":{"shareWith":"other"},"shareType":0}]
                }}}
                """));
        NextcloudClient client = client(http);

        ShareInfo share = client.shares().createShare(ShareCreateRequest.publicLink("/CodexScratch/file.txt", SharePermission.READ));
        List<Sharee> sharees = client.sharees().search("temp", "file", 1, 25);

        assertEquals("42", share.id());
        assertEquals("https://cloud.example.com/s/abc", share.url());
        HttpRequestSpec create = http.requests().get(0);
        assertEquals(HttpMethod.POST, create.method());
        assertEquals("https://cloud.example.com/ocs/v2.php/apps/files_sharing/api/v1/shares", create.uri().toString());
        assertEquals("path=%2FCodexScratch%2Ffile.txt&shareType=3&permissions=1", body(create));
        assertHeader(create, "Content-Type", "application/x-www-form-urlencoded");

        assertEquals(2, sharees.size());
        assertEquals("users", sharees.getFirst().kind());
        assertTrue(http.requests().get(1).uri().toString().contains("search=temp"));
        assertTrue(http.requests().get(1).uri().toString().contains("perPage=25"));
    }

    private static NextcloudClient client(RecordingHttpClient http) {
        return new NextcloudClient(http, NextcloudCredentials.of(
                "main", "https://cloud.example.com/", "temporary", "app-password"));
    }

    private static HttpResponseSpec json(String body) {
        return new HttpResponseSpec(200, Map.of("Content-Type", List.of("application/json")), body.getBytes(StandardCharsets.UTF_8));
    }

    private static HttpResponseSpec xml(String body) {
        return new HttpResponseSpec(207, Map.of("Content-Type", List.of("application/xml")), body.getBytes(StandardCharsets.UTF_8));
    }

    private static HttpResponseSpec empty(int statusCode) {
        return new HttpResponseSpec(statusCode, Map.of(), new byte[0]);
    }

    private static void assertHeader(HttpRequestSpec request, String name, String value) {
        assertEquals(value, request.headers().get(name).getFirst());
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
