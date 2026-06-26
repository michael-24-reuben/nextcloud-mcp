# Fixes

## Files Changed

- `lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/NextcloudAdminClient.java`
- `lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/NextcloudAdminUsersClient.java`
- `lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/AdminUser.java`
- `lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/AdminUserCreateRequest.java`
- `lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/AdminUserCreated.java`
- `lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/AdminProvisioningOperation.java`
- `lib/nextcloud-mcp-admin/src/test/java/org/mcp/nextcloud/admin/NextcloudAdminUsersClientTest.java`
- `lib/nextcloud-mcp-http/src/main/java/org/mcp/nextcloud/http/NextcloudHttpRequestFactory.java`

## Behavioral Changes

- Added `NextcloudAdminClient.users()`.
- Added user list/search, get, create, field update, editable fields, enable, disable, delete, group reads, subadmin reads, and welcome resend.
- Added ordered repeated form field support for user creation fields such as `groups[]` and `subadmin[]`.
