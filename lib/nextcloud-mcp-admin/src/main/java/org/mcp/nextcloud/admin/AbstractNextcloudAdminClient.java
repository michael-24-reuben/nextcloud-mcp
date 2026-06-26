package org.mcp.nextcloud.admin;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.mcp.nextcloud.core.error.NextcloudApiException;
import org.mcp.nextcloud.core.util.Preconditions;
import org.mcp.nextcloud.http.HttpClientAdapter;
import org.mcp.nextcloud.http.HttpRequestSpec;
import org.mcp.nextcloud.http.HttpResponseSpec;
import org.mcp.nextcloud.http.NextcloudHttpRequestFactory;

abstract class AbstractNextcloudAdminClient {
    protected final HttpClientAdapter httpClient;
    protected final NextcloudHttpRequestFactory requests;

    protected AbstractNextcloudAdminClient(HttpClientAdapter httpClient, NextcloudHttpRequestFactory requests) {
        this.httpClient = Preconditions.requireNonNull(httpClient, "http client");
        this.requests = Preconditions.requireNonNull(requests, "request factory");
    }

    protected HttpRequestSpec getOcs(String path) {
        return requests.getOcs(path);
    }

    protected HttpRequestSpec getOcs(String path, Map<String, String> query) {
        return requests.getOcs(path, query);
    }

    protected HttpRequestSpec postOcsForm(String path, Map<String, String> fields) {
        return requests.postOcsForm(path, fields);
    }

    protected HttpRequestSpec postOcsFormFields(String path, java.util.List<Map.Entry<String, String>> fields) {
        return requests.postOcsFormFields(path, fields);
    }

    protected HttpRequestSpec putOcsForm(String path, Map<String, String> fields) {
        return requests.putOcsForm(path, fields);
    }

    protected HttpRequestSpec deleteOcs(String path) {
        return requests.deleteOcs(path);
    }

    protected HttpRequestSpec deleteOcsForm(String path, Map<String, String> fields) {
        return requests.deleteOcsForm(path, fields);
    }

    protected HttpResponseSpec send(HttpRequestSpec request) {
        try {
            return httpClient.send(request);
        } catch (IOException ex) {
            throw new NextcloudAdminClientException("nextcloud_admin_io_error", "Nextcloud admin request failed", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new NextcloudAdminClientException("nextcloud_admin_interrupted", "Nextcloud admin request was interrupted", ex);
        }
    }

    protected HttpResponseSpec sendExpecting(HttpRequestSpec request, int... allowedStatusCodes) {
        HttpResponseSpec response = send(request);
        if (Arrays.stream(allowedStatusCodes).noneMatch(code -> code == response.statusCode())) {
            throw new NextcloudApiException(
                    "nextcloud_admin_unexpected_status",
                    "Unexpected Nextcloud admin HTTP status " + response.statusCode() + " for "
                            + request.method() + " " + request.uri(),
                    response.statusCode());
        }
        return response;
    }
}
