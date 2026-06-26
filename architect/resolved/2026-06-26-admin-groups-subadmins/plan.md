# Plan

## Scope

Implement admin group provisioning and user/group subadmin linkage routes.

## Steps

1. Add group response record.
2. Add `NextcloudAdminGroupsClient` to the admin facade.
3. Implement `/ocs/v1.php/cloud/groups` read and mutation methods.
4. Implement `/ocs/v1.php/cloud/users/{userid}/groups` membership methods.
5. Implement `/ocs/v1.php/cloud/users/{userid}/subadmins` promotion methods.
6. Verify route construction and form bodies with fake HTTP tests.

## Boundaries

- No live admin calls in this slice.
- No MCP tool policy or confirmation behavior in this slice.
- Admin group membership remains a high/critical policy concern for later tool exposure.
