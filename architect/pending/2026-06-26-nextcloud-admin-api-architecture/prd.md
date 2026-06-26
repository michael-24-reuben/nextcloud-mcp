# PRD

## Problem

The project needs admin API support while preserving the existing user/content API boundary. Admin credentials can call normal client APIs, but true admin behavior belongs to provisioning and governance endpoints.

## Requirements

- Use the same Basic Auth app-password and OCS header model as the normal client.
- Keep admin route ownership in `nextcloud-mcp-admin`.
- Keep content route ownership in `nextcloud-mcp-client`.
- Treat `/ocs/v1.php/cloud/users/{userid}` as admin-owned when the operation targets arbitrary users or mutates user state.
- Gate high-risk and critical admin operations through policy metadata before exposing them as tools.
- Keep OCC bridge work optional, explicit, and guarded.

## Acceptance Criteria

- Admin child architects exist for foundation, users, groups/subadmins, apps, share-boundary handling, OCC bridge, and admin tools policy.
- Handoff files identify admin work as a post-MVP track and do not displace the current MVP resolution order.
- Future admin implementation has a clear dependency order.
