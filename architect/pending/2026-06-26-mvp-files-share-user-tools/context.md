# Context

## Parent

- `architect/pending/2026-06-26-nextcloud-mcp-core-architecture`

## Intended Modules

- `tools/nextcloud-mcp-files-tools`
- `tools/nextcloud-mcp-share-tools`
- `tools/nextcloud-mcp-user-tools`

## Required Tool Names

```text
nextcloud.files.list
nextcloud.files.stat
nextcloud.files.download
nextcloud.files.upload
nextcloud.files.mkdir
nextcloud.files.delete
nextcloud.files.move
nextcloud.files.copy
nextcloud.files.search
nextcloud.files.favorite
nextcloud.shares.list
nextcloud.shares.get
nextcloud.shares.create
nextcloud.shares.update
nextcloud.shares.delete
nextcloud.shares.send_email
nextcloud.sharees.search
nextcloud.sharees.recommended
nextcloud.user.me
nextcloud.user.capabilities
nextcloud.user.metadata
```

## Verification Baseline

```powershell
.\mvnw.cmd -pl tools/nextcloud-mcp-files-tools,tools/nextcloud-mcp-share-tools,tools/nextcloud-mcp-user-tools test
```
