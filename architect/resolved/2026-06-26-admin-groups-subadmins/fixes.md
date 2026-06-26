# Fixes

## Files Changed

- `lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/NextcloudAdminClient.java`
- `lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/NextcloudAdminGroupsClient.java`
- `lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/AdminGroup.java`
- `lib/nextcloud-mcp-admin/src/test/java/org/mcp/nextcloud/admin/NextcloudAdminGroupsClientTest.java`

## Behavioral Changes

- Added `NextcloudAdminClient.groups()`.
- Added group list/search, create, member read, subadmin read, display-name update, and delete.
- Added user group add/remove and subadmin promote/demote methods.
