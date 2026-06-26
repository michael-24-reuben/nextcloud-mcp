# Brief

## Objective

Implement the MVP MCP tools for files, shares, sharees, and user/capabilities.

## Scope

- Files tools: list, stat, download, upload, mkdir, delete, move, copy, search if supported, and favorite.
- Share tools: list, get, create, update, delete, send email if supported.
- Sharee tools: search and recommended.
- User tools: current user, capabilities, and metadata constrained to the authenticated account.

## Non-Goals

- Admin tools.
- Trash, versions, comments, and status tools.
- Transport-specific behavior.

## Resolution Position

Resolve after client and runtime, before CLI and server transport.
