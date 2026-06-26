# Fixes

## Files Changed

- `lib/nextcloud-mcp-admin/pom.xml`
- `lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/NextcloudAdminClient.java`
- `lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/NextcloudAdminSharesSupport.java`
- `lib/nextcloud-mcp-admin/src/test/java/org/mcp/nextcloud/admin/NextcloudAdminSharesSupportTest.java`

## Behavioral Changes

- Added a dependency from `nextcloud-mcp-admin` to `nextcloud-mcp-client`.
- Added `NextcloudAdminClient.shares()`.
- Added `NextcloudAdminSharesSupport`, which adapts admin credentials into a normal `NextcloudClient` and exposes only `NextcloudSharesClient`.
- Kept WebDAV file operations out of the admin share boundary.

## Decision

Admin share calls reuse `nextcloud-mcp-client`; they are not duplicated under admin provisioning.
