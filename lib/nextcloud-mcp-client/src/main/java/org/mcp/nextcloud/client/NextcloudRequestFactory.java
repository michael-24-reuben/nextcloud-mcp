package org.mcp.nextcloud.client;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mcp.nextcloud.core.util.Preconditions;
import org.mcp.nextcloud.http.BasicAuth;
import org.mcp.nextcloud.http.HttpHeadersBuilder;
import org.mcp.nextcloud.http.HttpMethod;
import org.mcp.nextcloud.http.HttpRequestSpec;

final class NextcloudRequestFactory {
    private final NextcloudCredentials credentials;

    NextcloudRequestFactory(NextcloudCredentials credentials) {
        this.credentials = Preconditions.requireNonNull(credentials, "credentials");
    }

    HttpRequestSpec getOcs(String path) {
        return request(HttpMethod.GET, uri(path), ocsHeaders(), null);
    }

    HttpRequestSpec getOcs(String path, Map<String, String> query) {
        return request(HttpMethod.GET, uri(path, query), ocsHeaders(), null);
    }

    HttpRequestSpec postOcsForm(String path, Map<String, String> fields) {
        Map<String, List<String>> headers = new HttpHeadersBuilder()
                .header("Authorization", authorization())
                .ocsJsonDefaults()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();
        return request(HttpMethod.POST, uri(path), headers, formBody(fields));
    }

    HttpRequestSpec deleteOcs(String path) {
        return request(HttpMethod.DELETE, uri(path), ocsHeaders(), null);
    }

    HttpRequestSpec webDav(HttpMethod method, String userId, String userPath, boolean collection, Map<String, String> headers, byte[] body) {
        Map<String, List<String>> allHeaders = withAuthorization(headers);
        return request(method, uri(webDavFilesPath(userId, userPath, collection)), allHeaders, body);
    }

    HttpRequestSpec webDavRoot(HttpMethod method, Map<String, String> headers, byte[] body) {
        return request(method, uri("/remote.php/dav/"), withAuthorization(headers), body);
    }

    URI webDavDestinationUri(String userId, String userPath, boolean collection) {
        return uri(webDavFilesPath(userId, userPath, collection));
    }

    String webDavFilesPath(String userId, String userPath, boolean collection) {
        StringBuilder path = new StringBuilder("/remote.php/dav/files/")
                .append(encodePathSegment(Preconditions.requireNonBlank(userId, "user id")))
                .append("/");
        List<String> segments = normalizedPathSegments(userPath);
        for (int index = 0; index < segments.size(); index++) {
            if (index > 0) {
                path.append("/");
            }
            path.append(encodePathSegment(segments.get(index)));
        }
        if (collection && !path.toString().endsWith("/")) {
            path.append("/");
        }
        return path.toString();
    }

    String webDavSearchScope(String userId) {
        return "/files/" + encodePathSegment(Preconditions.requireNonBlank(userId, "user id"));
    }

    URI uri(String path) {
        return uri(path, Map.of());
    }

    URI uri(String path, Map<String, String> query) {
        String base = credentials.baseUrl();
        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        StringBuilder value = new StringBuilder(base).append(normalizedPath);
        if (query != null && !query.isEmpty()) {
            value.append("?");
            boolean first = true;
            for (Map.Entry<String, String> entry : query.entrySet()) {
                if (!first) {
                    value.append("&");
                }
                value.append(encodeQuery(entry.getKey())).append("=").append(encodeQuery(entry.getValue()));
                first = false;
            }
        }
        return URI.create(value.toString());
    }

    Map<String, List<String>> ocsHeaders() {
        return new HttpHeadersBuilder()
                .header("Authorization", authorization())
                .ocsJsonDefaults()
                .build();
    }

    Map<String, List<String>> withAuthorization(Map<String, String> headers) {
        HttpHeadersBuilder builder = new HttpHeadersBuilder().header("Authorization", authorization());
        if (headers != null) {
            headers.forEach(builder::header);
        }
        return builder.build();
    }

    private HttpRequestSpec request(HttpMethod method, URI uri, Map<String, List<String>> headers, byte[] body) {
        return new HttpRequestSpec(method, uri, headers, body);
    }

    private String authorization() {
        return BasicAuth.authorizationHeader(credentials.username(), credentials.appPassword());
    }

    private static byte[] formBody(Map<String, String> fields) {
        Map<String, String> nonNullFields = new LinkedHashMap<>();
        if (fields != null) {
            fields.forEach((key, value) -> {
                if (value != null) {
                    nonNullFields.put(key, value);
                }
            });
        }
        StringBuilder body = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : nonNullFields.entrySet()) {
            if (!first) {
                body.append("&");
            }
            body.append(encodeQuery(entry.getKey())).append("=").append(encodeQuery(entry.getValue()));
            first = false;
        }
        return body.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static List<String> normalizedPathSegments(String userPath) {
        if (userPath == null || userPath.isBlank() || "/".equals(userPath.trim())) {
            return List.of();
        }
        return List.of(userPath.replace("\\", "/").split("/")).stream()
                .filter(segment -> !segment.isBlank())
                .toList();
    }

    static String encodePathSegment(String value) {
        return encodeQuery(value);
    }

    private static String encodeQuery(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8)
                .replace("+", "%20")
                .replace("%7E", "~");
    }
}
