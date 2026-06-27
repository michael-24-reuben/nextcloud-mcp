package org.mcp.nextcloud.server;

import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    /**
     * Reports server readiness, loaded configuration status, account count, and tool count.
     *
     * @return health details from the runtime service
     */
    @GetMapping("/health")
    Map<String, Object> health() {
        return runtimeService.health();
    }

    /**
     * Lists configured Nextcloud accounts that the server can use for tool calls.
     *
     * @return response containing available account descriptors
     */
    @GetMapping("/api/v1/accounts")
    Map<String, Object> accounts() {
        return Map.of("accounts", runtimeService.listAccounts());
    }

    /**
     * Registers a local MCP account record in the configured local account store.
     *
     * @param request account connection and metadata fields
     * @return sanitized account descriptor without secret material
     */
    @PostMapping("/api/v1/accounts")
    Map<String, Object> createAccount(@RequestBody AccountCreateRequest request) {
        return runtimeService.createAccount(request);
    }

    /**
     * Reads one configured MCP account.
     *
     * @param accountKey local account key
     * @return sanitized account descriptor
     */
    @GetMapping("/api/v1/accounts/{accountKey}")
    Map<String, Object> account(@PathVariable String accountKey) {
        return runtimeService.getAccount(accountKey);
    }

    /**
     * Updates non-secret local MCP account metadata and access flags.
     *
     * @param accountKey local account key
     * @param request patch fields to persist
     * @return sanitized account descriptor after update
     */
    @PatchMapping("/api/v1/accounts/{accountKey}")
    Map<String, Object> updateAccount(@PathVariable String accountKey, @RequestBody AccountPatchRequest request) {
        return runtimeService.updateAccount(accountKey, request);
    }

    /**
     * Deletes a local MCP account record from the configured account store.
     *
     * @param accountKey local account key
     * @return deletion result
     */
    @DeleteMapping("/api/v1/accounts/{accountKey}")
    Map<String, Object> deleteAccount(@PathVariable String accountKey) {
        return runtimeService.deleteAccount(accountKey);
    }

    /**
     * Rotates or sets the stored app password for a local MCP account.
     *
     * @param accountKey local account key
     * @param request new app password payload
     * @return sanitized account descriptor after update
     */
    @PostMapping("/api/v1/accounts/{accountKey}/app-password")
    Map<String, Object> setAppPassword(@PathVariable String accountKey, @RequestBody AppPasswordRequest request) {
        return runtimeService.setAccountAppPassword(accountKey, request);
    }

    /**
     * Enables a local MCP account record.
     *
     * @param accountKey local account key
     * @return sanitized account descriptor after update
     */
    @PostMapping("/api/v1/accounts/{accountKey}/enable")
    Map<String, Object> enableAccount(@PathVariable String accountKey) {
        return runtimeService.setAccountEnabled(accountKey, true);
    }

    /**
     * Disables a local MCP account record.
     *
     * @param accountKey local account key
     * @return sanitized account descriptor after update
     */
    @PostMapping("/api/v1/accounts/{accountKey}/disable")
    Map<String, Object> disableAccount(@PathVariable String accountKey) {
        return runtimeService.setAccountEnabled(accountKey, false);
    }

    /**
     * Marks a local MCP account as the default account and clears other local defaults.
     *
     * @param accountKey local account key
     * @return sanitized account descriptor after update
     */
    @PostMapping("/api/v1/accounts/{accountKey}/make-default")
    Map<String, Object> makeDefaultAccount(@PathVariable String accountKey) {
        return runtimeService.makeDefaultAccount(accountKey);
    }

    /**
     * Validates that a configured account can authenticate against Nextcloud.
     *
     * @param request optional request body selecting an account; defaults are resolved by the runtime
     * @return account validation result from the runtime service
     */
    @PostMapping("/api/v1/accounts/test")
    Map<String, Object> testAccount(@RequestBody(required = false) AccountTestRequest request) {
        return runtimeService.testAccount(request == null ? null : request.accountKey());
    }

    /**
     * Lists tools enabled for the selected or default account.
     *
     * @param accountId optional account id used to evaluate account-specific tool availability
     * @return response containing tool descriptors exposed by the runtime
     */
    @GetMapping("/api/v1/tools")
    Map<String, Object> tools(@RequestParam(name = "accountId", required = false) String accountId) {
        return Map.of("tools", runtimeService.listTools(accountId));
    }

    /**
     * Invokes a Nextcloud MCP tool through the REST API.
     *
     * @param request tool name, account selection, invocation id, and tool arguments
     * @return tool invocation result from the runtime service
     */
    @PostMapping("/api/v1/tools/call")
    Map<String, Object> call(@RequestBody ToolCallRequest request) {
        return runtimeService.callTool(request);
    }

    /**
     * Lists MCP tools through the compatibility route while reusing the REST tool listing behavior.
     *
     * @param accountId optional account id used to evaluate account-specific tool availability
     * @return response containing tool descriptors exposed by the runtime
     */
    @GetMapping("/mcp/tools")
    Map<String, Object> mcpTools(@RequestParam(name = "accountId", required = false) String accountId) {
        return tools(accountId);
    }

    /**
     * Invokes a Nextcloud MCP tool through the compatibility route.
     *
     * @param request tool name, account selection, invocation id, and tool arguments
     * @return tool invocation result from the runtime service
     */
    @PostMapping("/mcp/tools/call")
    Map<String, Object> mcpCall(@RequestBody ToolCallRequest request) {
        return call(request);
    }
}
//
