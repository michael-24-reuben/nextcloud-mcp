# Plan

## Admin Resolution Order

1. `2026-06-26-admin-auth-client-foundation`
2. `2026-06-26-admin-users-provisioning`
3. `2026-06-26-admin-groups-subadmins`
4. `2026-06-26-admin-apps-provisioning`
5. `2026-06-26-admin-share-boundary`
6. `2026-06-26-admin-tools-policy-surface`
7. `2026-06-26-admin-occ-bridge`

## Route Boundary

- User/content client owns WebDAV files, normal shares/sharees, current user, capabilities, and self metadata.
- Admin client owns arbitrary user provisioning, group provisioning, subadmin management, app provisioning, and guarded server-governance operations.
- Shared route construction belongs below both clients in `nextcloud-mcp-http`.

## Verification Strategy

- Unit-test request shapes with fake HTTP clients.
- Verify admin tool descriptors declare admin scopes and destructive risk metadata.
- Keep live Nextcloud calls for a later explicit integration slice.
