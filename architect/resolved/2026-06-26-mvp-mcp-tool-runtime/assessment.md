# Assessment

## Result

The runtime slice needed a Spring-free tool contract that future CLI and server transports can share. The project had empty `nextcloud-mcp-tool-api` and `nextcloud-mcp-tool-runtime` modules, while the security baseline already defined principals, scopes, policy checks, and audit events.

## Final Diagnosis

- Tool contracts belong in `lib/nextcloud-mcp-tool-api` and depend only on core primitives.
- Runtime behavior belongs in `lib/nextcloud-mcp-tool-runtime`.
- Policy and audit should reuse `nextcloud-mcp-security` types to avoid a second principal/scope model.
- Actual Nextcloud file/share/user tool implementations belong to the next MVP child architect, not this runtime slice.

## Remaining Risk

Transport-specific MCP JSON shape mapping is not implemented here. The server and CLI slices should adapt to these descriptors and results instead of duplicating validation or dispatch behavior.
