package org.mcp.nextcloud.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
        Map<String, java.util.List<String>> headers = new HttpHeadersBuilder().ocsJsonDefaults().build();

        assertEquals(java.util.List.of("true"), headers.get("OCS-APIRequest"));
        assertTrue(headers.get("Accept").contains("application/json"));
    }
}
