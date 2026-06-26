# nextcloud-mcp-admin

Admin-side Nextcloud API module.

Owns provisioning and governance routes such as users, groups, subadmins, apps, and later guarded OCC bridge support. It reuses shared HTTP/auth routing from `nextcloud-mcp-http` and must stay separate from the normal content client.

Does not own WebDAV file operations, normal shares/sharees, current-user metadata, or user-content MCP tools.
