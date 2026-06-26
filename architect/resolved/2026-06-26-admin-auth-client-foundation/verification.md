# Verification

## Automated

```powershell
.\mvnw.cmd -pl lib/nextcloud-mcp-admin -am test
```

Result: passed.

## Coverage Notes

- Verified admin identity route construction.
- Verified `Authorization`, `OCS-APIRequest`, and `Accept` headers.
- Verified config mapping rejects disabled admin API and non-admin accounts.
- No live admin calls were made in this foundation slice.
