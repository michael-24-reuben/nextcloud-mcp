# Plan

## Child Architect Decomposition

Create smaller architect entries from this parent in dependency order. Each child should stay focused enough to implement and verify independently.

## Recommended Order

1. Build foundation and parent POM
   - Reconcile the generated Spring Boot parent POM with the blueprint.
   - Establish Java 25, Maven packaging, module naming, dependency management, and test baseline.
   - Add only the modules that already exist or have been created/approved by the user.

2. Core, HTTP, and configuration libraries
   - Define domain errors, result wrappers, IDs, string masking, HTTP adapter contracts, auth headers, retry/rate-limit policy, config model, secret resolution, and validation.
   - Keep this slice Spring-free.

3. User Nextcloud client
   - Defer Login Flow V2 to post-MVP.
   - Use app-password Basic Auth for MVP.
   - Implement app-password Basic Auth, current-user resolution, WebDAV files, OCS shares, sharees, user metadata, and capabilities.
   - Defer trash, versions, comments, and status unless the slice explicitly includes them.

4. MCP tool API and runtime
   - Define tool descriptors, schemas, parameters, invocation, result, errors, and content types.
   - Add registry, dispatcher, schema validation, invocation context, result mapping, audit interceptor, and policy interceptor.

5. MVP tool modules
   - Files tools: list/stat/download/upload/mkdir/delete/move/copy/search/favorite as the approved scope allows.
   - Share tools: list/get/create/update/delete/send-email plus sharee search/recommended.
   - User tools: current user, capabilities, metadata with non-admin isolation.

6. CLI caller
   - Add `nextcloud-mcp tools list`, `nextcloud-mcp call`, `accounts test`, and `config check`.
   - Reuse the same runtime and tool modules as the server.
   - MVP CLI runs in-process only.
   - Do not add remote/server-call CLI mode in MVP.

7. Spring Boot server transport
   - Wire config, clients, runtime, tools, account registry, audit service, and transport controllers.
   - Expose internal HTTP endpoints and optional MCP-compatible JSON-RPC endpoints.
   - Keep capability logic outside Spring controllers.

8. Security hardening
   - Add or expand account isolation, Login Flow V2, scope evaluation, delete-by-default behavior, secret storage/masking, and audit event coverage.
   - Make admin capability opt-in.

9. Post-MVP capability modules
   - Add trash, versions, comments, user status, tool catalog, and admin CLI in separate child architects.
   - Treat admin client and admin tools as already represented by resolved admin architect entries.

10. Packaging and operations
   - Add Docker, config examples, schemas, scripts, installer/distribution support, and deployment documentation.

## Dependency Direction

Keep the intended dependency direction:

```text
app/nextcloud-mcp-server
  -> tool-runtime
  -> tool modules
  -> nextcloud client/admin client

cli/*
  -> tool-runtime
  -> tool modules
  -> nextcloud client/admin client

tools/*
  -> tool-api
  -> nextcloud client/admin client
  -> security

nextcloud-mcp-client
  -> http
  -> core

nextcloud-mcp-admin
  -> nextcloud-mcp-client
  -> http
  -> core
```

Avoid:

```text
nextcloud-mcp-client -> Spring Boot
nextcloud-mcp-tool-api -> Spring Boot
nextcloud-mcp-core -> Spring Boot
```

## Verification Strategy

- Validate Maven module graph after each module slice.
- Unit test XML request/response parsing with fixtures before live calls.
- Unit test OCS request shapes and response mapping.
- Add integration tests only after stable config and secrets handling exist.
- For live verification, use a non-admin app-password account first.
- Exercise delete, trash, admin, and public-share operations only behind explicit policy checks.

## Known Design Tensions

- The current root POM is generated with Spring Boot dependencies, while the blueprint wants lower-level libraries to remain Spring-free.
- The full blueprint includes more modules than the MVP needs.
- Admin access must not bleed into normal user tools.
- WebDAV XML parsing should be robust enough for Nextcloud-specific properties without hardcoding brittle string parsing.
