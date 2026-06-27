package org.mcp.nextcloud.server;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NextcloudMcpApiController {
    private final NextcloudMcpRuntimeService runtimeService;

    NextcloudMcpApiController(NextcloudMcpRuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @GetMapping("/health")
    Map<String, Object> health() {
        return runtimeService.health();
    }

    @GetMapping("/api/v1/accounts")
    Map<String, Object> accounts() {
        return Map.of("accounts", runtimeService.listAccounts());
    }

    @PostMapping("/api/v1/accounts/test")
    Map<String, Object> testAccount(@RequestBody(required = false) AccountTestRequest request) {
        return runtimeService.testAccount(request == null ? null : request.accountId());
    }

    @GetMapping("/api/v1/tools")
    Map<String, Object> tools(@RequestParam(name = "accountId", required = false) String accountId) {
        return Map.of("tools", runtimeService.listTools(accountId));
    }

    @PostMapping("/api/v1/tools/call")
    Map<String, Object> call(@RequestBody ToolCallRequest request) {
        return runtimeService.callTool(request);
    }

    @GetMapping("/mcp/tools")
    Map<String, Object> mcpTools(@RequestParam(name = "accountId", required = false) String accountId) {
        return tools(accountId);
    }

    @PostMapping("/mcp/tools/call")
    Map<String, Object> mcpCall(@RequestBody ToolCallRequest request) {
        return call(request);
    }
}
