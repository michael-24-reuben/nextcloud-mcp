package org.mcp.nextcloud.http;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;

import org.mcp.nextcloud.core.util.Preconditions;

public record HttpRequestSpec(HttpMethod method, URI uri, Map<String, List<String>> headers, byte[] body) {
    public HttpRequestSpec {
        method = Preconditions.requireNonNull(method, "http method");
        uri = Preconditions.requireNonNull(uri, "uri");
        headers = headers == null ? Map.of() : Map.copyOf(headers);
        body = body == null ? new byte[0] : body.clone();
    }

    public HttpRequest toJdkRequest() {
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri);
        headers.forEach((name, values) -> values.forEach(value -> builder.header(name, value)));
        HttpRequest.BodyPublisher publisher = body.length == 0
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofByteArray(body);
        return builder.method(method.name(), publisher).build();
    }
}
