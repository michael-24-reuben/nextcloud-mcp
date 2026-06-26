# Blockers

## BLOCKED: Module directories are missing

The core/http/config implementation needs these modules:

- `lib/nextcloud-mcp-core`
- `lib/nextcloud-mcp-http`
- `lib/nextcloud-mcp-config`

The user previously stated they would create the modules. To proceed, either create these module folders/POMs or explicitly approve Codex to create them.

Until then, implementation would violate the current boundary by creating project modules.
