package org.mcp.nextcloud.http;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.mcp.nextcloud.core.error.ConfigurationException;
import org.mcp.nextcloud.core.util.Preconditions;

public final class NextcloudHttpRequestFactory {
    private final URI baseUri;
    private final String username;
    private final String appPassword;

    public NextcloudHttpRequestFactory(URI baseUri, String username, String appPassword) {
        this.baseUri = normalizeBaseUri(baseUri);
        this.username = Preconditions.requireNonBlank(username, "username");
        this.appPassword = Preconditions.requireNonBlank(appPassword, "app password");
    }

    public HttpRequestSpec getOcs(String path) {
        return request(HttpMethod.GET, uri(path), ocsHeaders(), null);
    }

    public HttpRequestSpec getOcs(String path, Map<String, String> query) {
        return request(HttpMethod.GET, uri(path, query), ocsHeaders(), null);
    }

    public HttpRequestSpec postOcs(String path) {
        return request(HttpMethod.POST, uri(path), ocsHeaders(), null);
    }

    public HttpRequestSpec postOcsForm(String path, Map<String, String> fields) {
        return ocsForm(HttpMethod.POST, path, fields);
    }

    public HttpRequestSpec postOcsFormFields(String path, List<Map.Entry<String, String>> fields) {
        return ocsFormFields(HttpMethod.POST, path, fields);
    }

    public HttpRequestSpec putOcsForm(String path, Map<String, String> fields) {
        return ocsForm(HttpMethod.PUT, path, fields);
    }

    public HttpRequestSpec deleteOcs(String path) {
        return request(HttpMethod.DELETE, uri(path), ocsHeaders(), null);
    }

    public HttpRequestSpec deleteOcsForm(String path, Map<String, String> fields) {
        return ocsForm(HttpMethod.DELETE, path, fields);
    }

    public HttpRequestSpec webDav(
            HttpMethod method,
            String userId,
            String userPath,
            boolean collection,
            Map<String, String> headers,
            byte[] body) {
        return request(method, uri(webDavFilesPath(userId, userPath, collection)), withAuthorization(headers), body);
    }

    public HttpRequestSpec webDavRoot(HttpMethod method, Map<String, String> headers, byte[] body) {
        return request(method, uri("/remote.php/dav/"), withAuthorization(headers), body);
    }

    public URI webDavDestinationUri(String userId, String userPath, boolean collection) {
        return uri(webDavFilesPath(userId, userPath, collection));
    }

    public String webDavFilesPath(String userId, String userPath, boolean collection) {
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

    public String webDavSearchScope(String userId) {
        return "/files/" + encodePathSegment(Preconditions.requireNonBlank(userId, "user id"));
    }

    public URI uri(String path) {
        return uri(path, Map.of());
    }

    public URI uri(String path, Map<String, String> query) {
        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        StringBuilder value = new StringBuilder(baseUri.toString()).append(normalizedPath);
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

    public Map<String, List<String>> ocsHeaders() {
        return new HttpHeadersBuilder()
                .header("Authorization", authorization())
                .ocsJsonDefaults()
                .build();
    }

    public Map<String, List<String>> withAuthorization(Map<String, String> headers) {
        HttpHeadersBuilder builder = new HttpHeadersBuilder().header("Authorization", authorization());
        if (headers != null) {
            headers.forEach(builder::header);
        }
        return builder.build();
    }

    public static String encodePathSegment(String value) {
        return encodeQuery(value);
    }

    private HttpRequestSpec ocsForm(HttpMethod method, String path, Map<String, String> fields) {
        List<Map.Entry<String, String>> entries = fields == null
                ? List.of()
                : fields.entrySet().stream().toList();
        return ocsFormFields(method, path, entries);
    }

    private HttpRequestSpec ocsFormFields(HttpMethod method, String path, List<Map.Entry<String, String>> fields) {
        Map<String, List<String>> headers = new HttpHeadersBuilder()
                .header("Authorization", authorization())
                .ocsJsonDefaults()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();
        return request(method, uri(path), headers, formBody(fields));
    }

    private HttpRequestSpec request(HttpMethod method, URI uri, Map<String, List<String>> headers, byte[] body) {
        return new HttpRequestSpec(method, uri, headers, body);
    }

    private String authorization() {
        return BasicAuth.authorizationHeader(username, appPassword);
    }

    private static URI normalizeBaseUri(URI uri) {
        Preconditions.requireNonNull(uri, "base uri");
        if (uri.getScheme() == null || uri.getHost() == null) {
            throw new ConfigurationException("invalid_nextcloud_base_url", "Nextcloud base URL must be absolute");
        }
        String value = uri.toString();
        while (value.endsWith("/") && value.length() > uri.getScheme().length() + 3) {
            value = value.substring(0, value.length() - 1);
        }
        return URI.create(value);
    }

    private static byte[] formBody(List<Map.Entry<String, String>> fields) {
        List<Map.Entry<String, String>> nonNullFields = new java.util.ArrayList<>();
        if (fields != null) {
            fields.forEach(entry -> {
                if (entry != null && entry.getValue() != null) {
                    nonNullFields.add(entry);
                }
            });
        }
        StringBuilder body = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : nonNullFields) {
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

    private static String encodeQuery(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8)
                .replace("+", "%20")
                .replace("%7E", "~");
    }
}
