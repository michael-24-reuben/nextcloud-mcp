# Fixes

## Files Changed

- `lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/NextcloudAdminClient.java`
- `lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/NextcloudAdminAppsClient.java`
- `lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/AdminApp.java`
- `lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/AdminAppOperation.java`
- `lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/AdminRiskLevel.java`
- `lib/nextcloud-mcp-admin/src/test/java/org/mcp/nextcloud/admin/NextcloudAdminAppsClientTest.java`

## Behavioral Changes

- Added `NextcloudAdminClient.apps()`.
- Added installed-app listing with optional `enabled` and `disabled` filters.
- Added app info parsing with raw OCS JSON retained.
- Added app enable/disable calls under `/ocs/v1.php/cloud/apps/{appid}`.
- Marked app enable/disable results as `CRITICAL` for later MCP tool policy.
