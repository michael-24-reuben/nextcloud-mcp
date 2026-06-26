# Brief

## Objective

Implement the Spring-free `nextcloud-mcp-admin` client foundation for admin credentials and OCS provisioning routes.

## Scope

- Admin client facade.
- Admin credential construction from configured admin account.
- Shared Basic Auth and OCS header behavior.
- Admin identity test path.
- Internal request helpers for admin provisioning endpoints.

## Non-Goals

- User/content WebDAV behavior.
- Concrete admin tools.
- Live admin calls.
