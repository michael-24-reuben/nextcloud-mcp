# Verification

Admin child verification included focused fake-HTTP and command-plan tests.

Latest focused verification passed:

```powershell
.\mvnw.cmd -pl tools/nextcloud-mcp-admin-tools -am "-Dtest=NextcloudAdminToolsTest,NextcloudAdminOccBridgeTest" "-Dsurefire.failIfNoSpecifiedTests=false" test
```

Result: build success with admin tool policy and OCC bridge tests passing.

Full verification also passed:

```powershell
.\mvnw.cmd test
```

Result: build success across the full reactor. The full run executed the live SDK functionality smoke test against the temp account and cleaned up `/CodexScratch/nextcloud-mcp-smoke.txt`; no OCC execution or app enable/disable occurred.
