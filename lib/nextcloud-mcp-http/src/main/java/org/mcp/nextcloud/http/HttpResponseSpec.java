package org.mcp.nextcloud.http;

import java.util.List;
import java.util.Map;

public record HttpResponseSpec(int statusCode, Map<String, List<String>> headers, byte[] body) {
    public HttpResponseSpec {
        headers = headers == null ? Map.of() : Map.copyOf(headers);
        body = body == null ? new byte[0] : body.clone();
    }
}
