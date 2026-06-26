# Plan

## Scope

Implement admin-owned user provisioning APIs in `nextcloud-mcp-admin`.

## Steps

1. Add request/response records for user creation and user data.
2. Add `NextcloudAdminUsersClient` to the admin facade.
3. Implement route helpers for `/ocs/v1.php/cloud/users`.
4. Support ordered repeated form fields for user creation groups/subadmins.
5. Add fake HTTP-backed tests for read, create, mutate, and critical route construction.

## Boundaries

- No live admin calls in this slice.
- No MCP tool exposure or confirmation policy in this slice.
- Group membership mutations are handled by the groups/subadmins child.
