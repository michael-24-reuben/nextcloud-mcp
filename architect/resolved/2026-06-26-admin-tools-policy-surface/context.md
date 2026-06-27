# Context

## Risk Levels

- Low: list users, get user, list groups, get group members, list apps, get app info.
- Medium: create user, edit display name/email, create group, add/remove user from normal group.
- High: disable user, enable user, quota changes, promote/demote subadmin, delete group.
- Critical: delete user, add/remove admin group, enable/disable apps, OCC bridge commands.

Admin tools should be disabled by default unless the config and policy grant the required scopes.
