# nextcloud-mcp-tool-runtime

Shared list/invoke runtime for tools.

## Owns

- Registration model: `ToolRegistration`.
- Registry contract and in-memory implementation: `ToolRegistry`, `InMemoryToolRegistry`.
- Dispatcher: `ToolDispatcher`.
- Argument validation: `ToolArgumentValidator`, `ToolValidationResult`.
- Argument mapping: `ToolArgumentMapper`.
- Runtime context: `ToolRuntimeContext`.
- Policy bridge: `ToolPolicyInterceptor`, `DefaultToolPolicyInterceptor`, `ToolPolicyDecision`.
- Audit sink: `ToolAuditSink`.

## Dispatch Flow

1. Find the registered tool.
2. Validate arguments against the descriptor schema.
3. Evaluate security policy.
4. Invoke the handler.
5. Normalize success or error into `ToolResult`.
6. Emit an audit event.

## Rules

- Transports should call this runtime instead of duplicating list/invoke behavior.
- Validation happens before policy and handler execution.
- Policy uses `nextcloud-mcp-security`.
- No Spring or CLI dependency belongs here.

## Verification

```powershell
.\mvnw.cmd -pl lib/nextcloud-mcp-tool-api,lib/nextcloud-mcp-tool-runtime -am test
```
