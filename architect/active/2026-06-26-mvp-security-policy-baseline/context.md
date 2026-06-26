# Context

## Parent

- `architect/pending/2026-06-26-nextcloud-mcp-core-architecture`

## Intended Module

- `lib/nextcloud-mcp-security`

## MVP Scopes From Parent Blueprint

```text
nextcloud.files.read
nextcloud.files.write
nextcloud.files.delete
nextcloud.shares.read
nextcloud.shares.write
nextcloud.comments.write
nextcloud.trash.restore
```

Admin scopes should remain out of MVP or explicitly disabled.

## Verification Baseline

```powershell
.\mvnw.cmd -pl lib/nextcloud-mcp-security test
```
