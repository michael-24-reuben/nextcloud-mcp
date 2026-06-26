package org.mcp.nextcloud.client;

import java.io.IOException;
import java.util.Arrays;

import org.mcp.nextcloud.core.error.NextcloudApiException;
import org.mcp.nextcloud.core.util.Preconditions;
import org.mcp.nextcloud.http.HttpClientAdapter;
import org.mcp.nextcloud.http.HttpRequestSpec;
import org.mcp.nextcloud.http.HttpResponseSpec;

abstract class AbstractNextcloudClient {
    protected final HttpClientAdapter httpClient;
    protected final NextcloudRequestFactory requests;

    protected AbstractNextcloudClient(HttpClientAdapter httpClient, NextcloudRequestFactory requests) {
        this.httpClient = Preconditions.requireNonNull(httpClient, "http client");
        this.requests = Preconditions.requireNonNull(requests, "request factory");
    }

    protected HttpResponseSpec send(HttpRequestSpec request) {
        try {
            return httpClient.send(request);
        } catch (IOException ex) {
            throw new NextcloudClientException("nextcloud_io_error", "Nextcloud request failed", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new NextcloudClientException("nextcloud_interrupted", "Nextcloud request was interrupted", ex);
        }
    }

    protected HttpResponseSpec sendExpecting(HttpRequestSpec request, int... allowedStatusCodes) {
        HttpResponseSpec response = send(request);
        if (Arrays.stream(allowedStatusCodes).noneMatch(code -> code == response.statusCode())) {
            throw new NextcloudApiException(
                    "nextcloud_unexpected_status",
                    "Unexpected Nextcloud HTTP status " + response.statusCode() + " for " + request.method() + " " + request.uri(),
                    response.statusCode());
        }
        return response;
    }
}
