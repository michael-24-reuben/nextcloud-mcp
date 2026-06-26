package org.mcp.nextcloud.core.result;

import java.util.List;

public record PageResult<T>(List<T> items, String nextPageToken) {
    public PageResult {
        items = items == null ? List.of() : List.copyOf(items);
    }
}
