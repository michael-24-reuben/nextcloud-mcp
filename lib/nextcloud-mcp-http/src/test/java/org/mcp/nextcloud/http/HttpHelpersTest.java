package org.mcp.nextcloud.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class HttpHelpersTest {
    @Test
    void basicAuthUsesUtf8UsernameAndPassword() {
        String expected = Base64.getEncoder().encodeToString("michael:app-secret".getBytes(StandardCharsets.UTF_8));

        assertEquals("Basic " + expected, BasicAuth.authorizationHeader("michael", "app-secret"));
    }

    @Test
    void ocsDefaultsIncludeRequiredHeaders() {
        Map<String, List<String>> headers = new HttpHeadersBuilder().ocsJsonDefaults().build();

        assertEquals(List.of("true"), headers.get("OCS-APIRequest"));
        assertTrue(headers.get("Accept").contains("application/json"));
    }

    @Test
    void nextcloudRequestFactoryBuildsSharedOcsAndWebDavRoutes() {
        NextcloudHttpRequestFactory requests = new NextcloudHttpRequestFactory(
                URI.create("https://cloud.example.com/"), "temporary", "app-secret");

        HttpRequestSpec adminUsers = requests.getOcs("/ocs/v1.php/cloud/users", Map.of("search", "temp"));
        HttpRequestSpec userFiles = requests.webDav(
                HttpMethod.PROPFIND, "temporary", "/Codex Scratch", true, Map.of("Depth", "1"), null);
        HttpRequestSpec updateUser = requests.putOcsForm(
                "/ocs/v1.php/cloud/users/temporary", Map.of("key", "displayname", "value", "tempo"));

        assertEquals("https://cloud.example.com/ocs/v1.php/cloud/users?search=temp", adminUsers.uri().toString());
        assertEquals("https://cloud.example.com/remote.php/dav/files/temporary/Codex%20Scratch/", userFiles.uri().toString());
        assertEquals("key=displayname&value=tempo", new String(updateUser.body(), StandardCharsets.UTF_8));
        assertEquals("application/x-www-form-urlencoded", updateUser.headers().get("Content-Type").getFirst());
        assertEquals(BasicAuth.authorizationHeader("temporary", "app-secret"), adminUsers.headers().get("Authorization").getFirst());
    }
}
