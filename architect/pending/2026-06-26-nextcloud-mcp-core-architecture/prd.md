# PRD

## Goal

Build `nextcloud-mcp` as a structured, testable, framework-conscious MCP server for Nextcloud automation. It should expose file, share, user, and later admin capabilities through stable MCP tool contracts while keeping Nextcloud API clients reusable outside Spring.

## Users

- Automation agents that need MCP-style tools for Nextcloud files and shares.
- Operators who need CLI and server deployment paths.
- Future developers who need clear module ownership and low coupling.

## Requirements

- Use Java package root `org.mcp.nextcloud`.
- Keep lower-level libraries independent of Spring.
- Use `java.net.http.HttpClient` as the default low-level HTTP transport unless a future child architect proves a better reason.
- Use Basic Auth with app passwords for normal Nextcloud automation accounts.
- Resolve the authenticated user before constructing WebDAV file paths.
- Include OCS headers for OCS API calls: `OCS-APIRequest: true` and `Accept: application/json`.
- Treat admin access as a separate client/tool family and disable admin tools by default.
- Use explicit scopes and policies for tool access.
- Keep MCP tool descriptors, schemas, invocation, and result mapping framework-neutral.
- Let CLI and Spring server share the same tool runtime.
- Preserve auditability for tool calls and security-sensitive operations.

## MVP Capabilities

- Auth and current-user resolution.
- WebDAV file list/stat/upload/download/mkdir/delete/move/copy/favorite basics.
- OCS shares list/get/create/update/delete.
- Sharee search and recommended sharees.
- Current user and capabilities.
- Basic configuration loading, secret resolution, and validation.
- MCP tool API, registry, dispatcher, validation, and result mapping.
- General CLI and Spring server transport around the runtime.

## Post-MVP Capabilities

- WebDAV search.
- Trashbin list/restore/delete/empty.
- Versions list/restore.
- Comments list/create/update/delete/mark-read.
- User status tools.
- Admin client, admin tools, and admin CLI.
- Tool catalog export.
- Expanded security and policy module.
- Docker packaging and release scripts.

## Non-Goals For This Parent Entry

- Creating project modules.
- Implementing Maven child POMs.
- Writing Java implementation classes.
- Running against a live Nextcloud instance.
- Choosing every third-party dependency.
- Finalizing deployment topology.

## Acceptance Criteria

- The parent architect captures the project architecture and dependency direction.
- Future work can be split into child architects in dependency order.
- The architect record clearly separates MVP from post-MVP capability groups.
- The record preserves the user boundary that modules should not be created in this pass.
- The root handoff files point future work at this parent record and the next safe action.
