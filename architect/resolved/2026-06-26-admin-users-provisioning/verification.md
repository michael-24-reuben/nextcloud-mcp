# Verification

## Automated

```powershell
.\mvnw.cmd -pl lib/nextcloud-mcp-admin -am test
```

Result: passed.

## Coverage Notes

- Verified user list/search query construction.
- Verified arbitrary user lookup.
- Verified create user form body including repeated `groups[]` and `subadmin[]`.
- Verified update, enable, disable, delete, editable fields, groups, subadmins, and welcome routes.
- No live admin calls were made.
