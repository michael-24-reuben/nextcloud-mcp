# Brief

## Objective

Define and implement the admin-side Nextcloud API architecture without conflicting with the normal user/content client API.

## Scope

- Admin API module: `lib/nextcloud-mcp-admin`.
- Admin tools module: `tools/nextcloud-mcp-admin-tools`.
- Shared request/auth mechanics from `nextcloud-mcp-http`.
- Admin-only OCS provisioning routes for users, groups, subadmins, and apps.
- Guardrails for high-risk and critical admin operations.

## Non-Goals

- Moving normal WebDAV files, shares, sharees, current-user, or capabilities behavior into the admin module.
- Implementing server transport or CLI behavior in this architecture record.
- Adding unguarded OCC command execution.

## Source Blueprint

- `blueprint/nextcloud-admin-api-model.md`
