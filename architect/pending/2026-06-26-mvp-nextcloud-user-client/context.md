# Context

## Parent

- `architect/pending/2026-06-26-nextcloud-mcp-core-architecture`

## Intended Module

- `lib/nextcloud-mcp-client`

## API Base Paths

```text
/remote.php/dav/files/{user}/
/ocs/v2.php/apps/files_sharing/api/v1
/ocs/v1.php/apps/files_sharing/api/v1
/ocs/v1.php/cloud/user
/ocs/v1.php/cloud/capabilities
```

## Verification Baseline

```powershell
.\mvnw.cmd -pl lib/nextcloud-mcp-client test
```
