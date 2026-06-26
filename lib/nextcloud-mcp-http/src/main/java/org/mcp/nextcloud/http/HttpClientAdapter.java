package org.mcp.nextcloud.http;

import java.io.IOException;

public interface HttpClientAdapter {
    HttpResponseSpec send(HttpRequestSpec request) throws IOException, InterruptedException;
}
