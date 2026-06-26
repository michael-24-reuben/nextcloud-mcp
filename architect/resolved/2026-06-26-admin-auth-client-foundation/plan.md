# Plan

## Scope

Build the admin client foundation without adding concrete provisioning routes yet.

## Steps

1. Add admin credentials resolved from `NextcloudMcpConfig.admin.accountId`.
2. Reuse `NextcloudHttpRequestFactory` for Basic Auth and OCS JSON headers.
3. Add an admin facade and identity probe through `/ocs/v1.php/cloud/user`.
4. Add protected OCS helper methods for later user/group/app provisioning clients.
5. Verify with fake HTTP tests only.

## Boundaries

- Do not call live admin endpoints in this slice.
- Do not put admin provisioning behavior in `nextcloud-mcp-client`.
- Do not add MCP admin tools until the policy-surface child architect.
