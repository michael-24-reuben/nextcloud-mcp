# Nextcloud WebDAV Scripts

Local helper scripts for live Nextcloud smoke checks.

These scripts read `.env` from this directory by default. They do not print the app password.

## Current User

```powershell
.\scripts\nextcloud\WEBDAV\current-user.ps1
```

Use this before building WebDAV paths. The returned `Id` is the path segment for:

```text
/remote.php/dav/files/{Id}/
```

Do not use the display name in WebDAV paths.

When `-Username` is omitted, the script tries `NC_MCP_MAIN_USERNAME` first and then `NC_MCP_MAIN_ACCOUNT_ID`. This handles local configs where the username field accidentally contains a display name.

## Upload Smoke Test

```powershell
.\scripts\nextcloud\WEBDAV\file-upload.ps1
```

Optional overrides:

```powershell
.\scripts\nextcloud\WEBDAV\file-upload.ps1 -UploadDir /CodexScratch -UserId temporary
```

Important request rules:

- Nextcloud base URL is the host root, not `/u/<profile>`.
- Use a Nextcloud app password, not the account password.
- The script resolves the current OCS user first and uses that returned `Id` in WebDAV paths.
- `MKCOL` collection paths should include a trailing slash.
- URL-encode each path segment separately.
