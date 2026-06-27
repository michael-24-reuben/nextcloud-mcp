# Verification

Focused verification passed:

```powershell
.\mvnw.cmd -pl tools/nextcloud-mcp-admin-tools -am "-Dtest=NextcloudAdminToolsTest,NextcloudAdminOccBridgeTest" "-Dsurefire.failIfNoSpecifiedTests=false" test
```

Result: build success with the admin tools and OCC bridge tests passing.

Full reactor verification also passed with `.\mvnw.cmd test`.
