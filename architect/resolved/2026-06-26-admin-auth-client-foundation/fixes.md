# Fixes

## Files Changed

- `lib/nextcloud-mcp-admin/pom.xml`
- `lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/NextcloudAdminCredentials.java`
- `lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/NextcloudAdminClient.java`
- `lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/AdminAuthClient.java`
- `lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/AbstractNextcloudAdminClient.java`
- `lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/AdminOcsParser.java`
- `lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/AdminIdentity.java`
- `lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/NextcloudAdminClientException.java`
- `lib/nextcloud-mcp-admin/src/test/java/org/mcp/nextcloud/admin/NextcloudAdminClientTest.java`
- `lib/nextcloud-mcp-config/src/main/java/org/mcp/nextcloud/config/validation/ConfigValidator.java`
- `lib/nextcloud-mcp-config/src/test/java/org/mcp/nextcloud/config/ConfigValidatorTest.java`

## Behavioral Changes

- Admin credentials now resolve from `NextcloudMcpConfig.admin.accountId`.
- Enabled admin config must reference an enabled configured account marked `admin=true`.
- Admin auth identity probe uses `/ocs/v1.php/cloud/user` with shared Basic Auth and OCS JSON headers.
- Later admin clients can reuse protected OCS request helpers without depending on the normal client module.
