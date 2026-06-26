# Blockers

## BLOCKED: Security module directory is missing

The security policy baseline implementation needs:

- `lib/nextcloud-mcp-security`

It also depends on the core primitives from:

- `lib/nextcloud-mcp-core`

The user previously stated they would create the modules. To proceed, either create the needed module folders/POMs or explicitly approve Codex to create them.

Until then, implementation would violate the current boundary by creating project modules.
