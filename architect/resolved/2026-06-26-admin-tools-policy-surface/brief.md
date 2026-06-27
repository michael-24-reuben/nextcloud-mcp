# Brief

## Objective

Define and implement admin MCP tool descriptors, scopes, destructive flags, and confirmation/audit behavior.

## Scope

- Tool descriptor names for admin users, groups, subadmins, apps, and optional OCC operations.
- Required admin scopes.
- Risk metadata for low, medium, high, and critical operations.
- Policy-gating expectations before handlers invoke clients.

## Non-Goals

- Implementing admin client route calls.
- CLI/server transport wiring.
- Unguarded destructive operations.
