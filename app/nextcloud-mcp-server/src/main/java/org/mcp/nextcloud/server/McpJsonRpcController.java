package org.mcp.nextcloud.server;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class McpJsonRpcController {
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    private final NextcloudMcpRuntimeService runtimeService;
    private final ObjectMapper objectMapper;

    McpJsonRpcController(NextcloudMcpRuntimeService runtimeService, ObjectMapper objectMapper) {
        this.runtimeService = runtimeService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/mcp")
    Map<String, Object> jsonRpc(@RequestBody JsonRpcRequest request) {
        if (request == null || request.method() == null || request.method().isBlank()) {
            return error(null, -32600, "Invalid request");
        }
        Object result = switch (request.method()) {
            case "initialize" -> Map.of(
                    "protocolVersion", "2024-11-05",
                    "serverInfo", Map.of("name", "nextcloud-mcp-server"),
                    "capabilities", Map.of("tools", Map.of()));
            case "tools/list", "tools.list" -> Map.of("tools", runtimeService.listTools(textParam(request.params(), "accountId")));
            case "tools/call", "tools.call" -> runtimeService.callTool(toolCallRequest(request.params()));
            case "accounts/list", "accounts.list" -> Map.of("accounts", runtimeService.listAccounts());
            case "accounts/test", "accounts.test" -> runtimeService.testAccount(textParam(request.params(), "accountId"));
            case "health" -> runtimeService.health();
            default -> null;
        };
        if (result == null) {
            return error(request.id(), -32601, "Method not found: " + request.method());
        }
        Map<String, Object> response = new java.util.LinkedHashMap<>();
        response.put("jsonrpc", "2.0");
        response.put("id", idValue(request.id()));
        response.put("result", result);
        return java.util.Collections.unmodifiableMap(response);
    }

    private ToolCallRequest toolCallRequest(Map<String, Object> params) {
        if (params == null) {
            throw new ServerRequestException("request.invalid", "params are required");
        }
        JsonNode rawArguments = objectMapper.valueToTree(params.get("arguments"));
        Map<String, Object> arguments = rawArguments.isObject()
                ? objectMapper.convertValue(rawArguments, MAP_TYPE)
                : Map.of();
        String tool = textParam(params, "name");
        if (tool == null) {
            tool = textParam(params, "tool");
        }
        return new ToolCallRequest(
                tool,
                textParam(params, "accountId"),
                textParam(params, "invocationId"),
                arguments);
    }

    private static String textParam(Map<String, Object> params, String name) {
        if (params == null || !params.containsKey(name)) {
            return null;
        }
        Object raw = params.get(name);
        String value = raw == null ? null : raw.toString();
        return value == null || value.isBlank() ? null : value;
    }

    private static Map<String, Object> error(Object id, int code, String message) {
        Map<String, Object> response = new java.util.LinkedHashMap<>();
        response.put("jsonrpc", "2.0");
        response.put("id", idValue(id));
        response.put("error", Map.of("code", code, "message", message));
        return java.util.Collections.unmodifiableMap(response);
    }

    private static Object idValue(Object id) {
        return id;
    }
}
