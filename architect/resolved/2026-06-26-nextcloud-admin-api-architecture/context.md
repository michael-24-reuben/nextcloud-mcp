# Context

## Blueprint

- `blueprint/nextcloud-admin-api-model.md`
- `blueprint/nextcloud-client-api-model.md`
- `blueprint/project-structure.md`

## Current Boundary Decision

Admin API support should not conflict with the client API because the shared request model is the same, while endpoint ownership and permission policy differ.

`nextcloud-mcp-client` remains the normal content client. `nextcloud-mcp-admin` is the admin API module.

## Admin Endpoint Families

- Users: `/ocs/v1.php/cloud/users`
- Groups: `/ocs/v1.php/cloud/groups`
- Apps: `/ocs/v1.php/cloud/apps`
- Share APIs under admin credentials still use the normal share API and must remain separate from provisioning.
- OCC bridge is not a general HTTP admin API and must stay optional/guarded.
