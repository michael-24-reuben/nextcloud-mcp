# nextcloud-mcp-core

Shared primitives for the Nextcloud MCP project.

## Owns

- Stable ID records: `AccountId`, `InvocationId`, `PrincipalId`, `ToolId`.
- Common result records: `OperationResult`, `ErrorResult`, `PageResult`, `ProgressEvent`.
- Base exceptions: `NextcloudMcpException`, `NextcloudApiException`, `ToolExecutionException`, `ConfigurationException`.
- Small utilities: `Preconditions`, `StringMasks`.

## Does Not Own

- HTTP calls.
- Nextcloud API mapping.
- Tool dispatch.
- Spring or CLI wiring.

## Verification

```powershell
.\mvnw.cmd -pl lib/nextcloud-mcp-core -am test
```
