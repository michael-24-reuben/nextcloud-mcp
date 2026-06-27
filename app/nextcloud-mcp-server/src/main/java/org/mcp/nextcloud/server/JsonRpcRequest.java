package org.mcp.nextcloud.server;

import java.util.Map;

public record JsonRpcRequest(String jsonrpc, String method, Map<String, Object> params, Object id) {
    public JsonRpcRequest {
        params = params == null ? Map.of() : Map.copyOf(params);
    }
}
