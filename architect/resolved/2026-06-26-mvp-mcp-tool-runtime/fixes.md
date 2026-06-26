# Fixes

## Files Changed

- `lib/nextcloud-mcp-tool-api/src/main/java/org/mcp/nextcloud/tool/api/`
- `lib/nextcloud-mcp-tool-runtime/src/main/java/org/mcp/nextcloud/tool/runtime/`
- `lib/nextcloud-mcp-tool-runtime/src/test/java/org/mcp/nextcloud/tool/runtime/ToolDispatcherTest.java`
- `lib/nextcloud-mcp-tool-runtime/pom.xml`

## API Module

- Added descriptor, input/output schema, parameter, security metadata, invocation context, invocation, content, result, and handler contracts.
- Kept API free of Spring and free of direct security-module dependencies by representing required scopes as strings in `ToolSecurity`.

## Runtime Module

- Added an in-memory registry with duplicate tool ID checks.
- Added dispatcher behavior for list/invoke.
- Added argument validation for required, unknown, enum, and basic JSON-compatible parameter types.
- Added Jackson-backed argument mapping helper for future typed tool requests.
- Added policy interceptor backed by `ToolAccessPolicy`.
- Added audit sink integration using existing `AuditEvent`.

## Tests

- Added fake-tool dispatcher coverage for listing, successful invocation, validation failure, policy denial, duplicate registration, and typed argument conversion.
