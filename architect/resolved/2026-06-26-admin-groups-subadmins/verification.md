# Verification

## Automated

```powershell
.\mvnw.cmd -pl lib/nextcloud-mcp-admin -am test
```

Result: passed.

## Coverage Notes

- Verified group list/search and creation routes.
- Verified group member and subadmin reads.
- Verified display-name update and delete route construction.
- Verified user membership add/remove and subadmin promote/demote form bodies.
- No live admin calls were made.
