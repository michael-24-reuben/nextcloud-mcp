# Context

The admin blueprint uses the same auth shape as the normal client:

```text
Authorization: Basic base64(adminUser:adminAppPassword)
OCS-APIRequest: true
Accept: application/json
```

This foundation must keep the admin module separate from `nextcloud-mcp-client` while reusing shared HTTP route construction. The admin account should come from config via `NextcloudAdminConfig.accountId`.
