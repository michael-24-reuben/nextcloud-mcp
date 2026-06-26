# Plan

## Approach

- Keep `lib/nextcloud-mcp-client` Spring-free.
- Build all requests through the existing `HttpClientAdapter` and HTTP primitives.
- Use app-password Basic Auth for OCS and WebDAV.
- Resolve current user through OCS before user-specific WebDAV operations when a caller has a `NextcloudUser`.
- URL-encode WebDAV paths per segment and preserve trailing slash semantics for collections.
- Parse OCS JSON with Jackson and WebDAV multistatus XML with a namespace-aware JDK DOM parser.

## Boundaries

- No admin endpoints.
- No live Nextcloud calls in unit tests.
- No server, MCP runtime, or tool-module implementation in this slice.

## Verification

- Focused module tests: `.\mvnw.cmd -pl lib/nextcloud-mcp-client test`
- Full reactor before resolution: `.\mvnw.cmd test`
- Architect metadata JSON validation after lifecycle move.
