# Brief

## Objective

Preserve the boundary between admin provisioning and normal file share APIs when admin credentials are used.

## Scope

- Decide whether admin credentials call normal share APIs through `nextcloud-mcp-client` or through a thin admin wrapper.
- Document that `AdminProvisioningClient` is not `FileShareClient`.
- Prevent admin routes from implying cross-user WebDAV filesystem control.
- Ensure share tool permissions differ from provisioning permissions.

## Non-Goals

- Reimplementing normal share tools.
- Adding impersonation support.
- Implementing live share management.
