# Verification

## Automated Checks

```powershell
.\mvnw.cmd -pl lib/nextcloud-mcp-admin -am test
```

Result: passed.

```powershell
.\mvnw.cmd test
```

Result: passed.

```powershell
Get-ChildItem -Recurse -Filter meta.json -LiteralPath 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp\architect' | ForEach-Object { Get-Content -LiteralPath $_.FullName | ConvertFrom-Json | Out-Null }
git diff --check -- architect lib
```

Result: passed. `git diff --check` reported only LF-to-CRLF working-copy warnings.

## Test Coverage Added

- `NextcloudAdminSharesSupportTest.adminShareSupportUsesNormalShareApiWithAdminCredentials`

## Notes

The test verifies admin-authenticated share listing uses `/ocs/v2.php/apps/files_sharing/api/v1/shares` with admin Basic Auth and OCS headers. No live share operations were performed.
