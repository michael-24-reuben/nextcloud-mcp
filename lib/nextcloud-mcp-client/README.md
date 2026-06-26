# nextcloud-mcp-client

Spring-free Nextcloud client for non-admin MVP APIs.

## Owns

- Client facade: `NextcloudClient`.
- Credential construction: `NextcloudCredentials`.
- OCS user and capability calls: `NextcloudUsersClient`.
- WebDAV file calls: `NextcloudFilesClient`.
- Share and sharee calls: `NextcloudSharesClient`, `NextcloudShareesClient`.
- Response models for users, capabilities, WebDAV resources, shares, sharees, and operations.
- Internal OCS and WebDAV parsing helpers.

## Supported API Areas

- `GET /ocs/v1.php/cloud/user`
- `GET /ocs/v1.php/cloud/capabilities`
- WebDAV files under `/remote.php/dav/files/{userId}/`
- OCS shares under `/ocs/v2.php/apps/files_sharing/api/v1/shares`
- OCS sharees under `/ocs/v2.php/apps/files_sharing/api/v1/sharees`

## Rules

- Use app passwords, not account passwords.
- Use the host root as `baseUrl`, not a profile URL.
- Keep account ID, login username, current user ID, and display name separate.
- WebDAV paths use the Nextcloud user ID, not display name.
- No live calls are made by unit tests.

## Verification

```powershell
.\mvnw.cmd -pl lib/nextcloud-mcp-client -am test
```
