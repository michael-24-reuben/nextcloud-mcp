# Brief

## Original Request

Review the two architecture blueprint files:

- `blueprint/project-structure.md`
- `blueprint/nextcloud-api-model.md`

Then create a core architect record for the project that can later be used to create smaller architect entries to accomplish the objective.

## Objective

Define the project-level architecture for `nextcloud-mcp` as a Java/Maven Nextcloud MCP capability server. The record should preserve the intended module boundaries, dependency direction, API surface, security posture, and implementation order so later work can be split into focused child architects.

## Scope Boundaries

- Create only architect tracking folders and files.
- Do not create Maven modules.
- Do not create Java packages.
- Do not implement Nextcloud clients, MCP tools, CLI commands, or Spring server code.
- Preserve the existing repository scaffold unless a future child architect explicitly covers changes.

## Stable Architecture Summary

The project should become:

- a plain Java Nextcloud API SDK for user APIs
- a separate plain Java admin SDK
- a framework-neutral MCP tool contract
- a reusable MCP runtime dispatcher
- Nextcloud capability tool modules
- a general CLI caller
- an admin/operator CLI
- a Spring Boot MCP server that exposes and wires the runtime

The core design rule is:

```text
Nextcloud API clients know HTTP/WebDAV/OCS.
Tools know capability names and schemas.
Runtime knows how to list and invoke tools.
Spring only exposes the runtime over HTTP/WebSocket.
```
