# Verification

## Commands

```powershell
.\mvnw.cmd -pl tools/nextcloud-mcp-files-tools,tools/nextcloud-mcp-share-tools,tools/nextcloud-mcp-user-tools -am -DskipTests compile
.\mvnw.cmd -pl tools/nextcloud-mcp-files-tools,tools/nextcloud-mcp-share-tools,tools/nextcloud-mcp-user-tools -am test
.\mvnw.cmd test
```

## Results

- Focused compile passed.
- Focused tool-module reactor tests passed after fixing the existing order-sensitive HTTP helper test.
- Full Maven reactor tests passed.

## Notes

- Verification used fake HTTP clients only. No live Nextcloud calls were made.
- Deferred share tools remain visible but return `tool.deferred` until the client layer adds route support.
