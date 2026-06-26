package org.mcp.nextcloud.http;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class HttpHeadersBuilder {
    private final Map<String, List<String>> headers = new LinkedHashMap<>();

    public HttpHeadersBuilder header(String name, String value) {
        headers.computeIfAbsent(name, ignored -> new ArrayList<>()).add(value);
        return this;
    }

    public HttpHeadersBuilder ocsJsonDefaults() {
        return header("OCS-APIRequest", "true").header("Accept", "application/json");
    }

    public Map<String, List<String>> build() {
        Map<String, List<String>> copy = new LinkedHashMap<>();
        headers.forEach((key, values) -> copy.put(key, List.copyOf(values)));
        return Map.copyOf(copy);
    }
}
