# Fixes

- Added `nextcloud-mcp-admin` as a dependency of `nextcloud-mcp-admin-tools`.
- Added `NextcloudAdminTools` registrations for admin users, groups, subadmins, apps, and OCC command-plan tools.
- Added admin scope constants for read, write, delete, apps, and OCC operations.
- Added descriptor metadata for admin category, risk level, and confirmation requirements.
- Added tests proving descriptor metadata exists, non-admin callers are denied before handler invocation, and allowed admin read calls reach the admin client.
