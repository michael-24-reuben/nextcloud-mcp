# Context

## Parent

- `architect/pending/2026-06-26-nextcloud-mcp-core-architecture`

## Intended Modules

- `lib/nextcloud-mcp-tool-api`
- `lib/nextcloud-mcp-tool-runtime`

## Runtime Rule

The runtime owns list/invoke behavior. Transports should not duplicate validation or dispatch logic.

## Verification Baseline

```powershell
.\mvnw.cmd -pl lib/nextcloud-mcp-tool-api,lib/nextcloud-mcp-tool-runtime test
```
