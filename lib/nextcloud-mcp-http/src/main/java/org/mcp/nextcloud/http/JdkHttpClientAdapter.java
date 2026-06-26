package org.mcp.nextcloud.http;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

public final class JdkHttpClientAdapter implements HttpClientAdapter {
    private final HttpClient client;

    public JdkHttpClientAdapter() {
        this(HttpClient.newHttpClient());
    }

    public JdkHttpClientAdapter(HttpClient client) {
        this.client = client;
    }

    @Override
    public HttpResponseSpec send(HttpRequestSpec request) throws IOException, InterruptedException {
        HttpResponse<byte[]> response = client.send(request.toJdkRequest(), HttpResponse.BodyHandlers.ofByteArray());
        return new HttpResponseSpec(response.statusCode(), response.headers().map(), response.body());
    }
}
