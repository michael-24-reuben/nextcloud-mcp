# Fixes

## Files Changed

- `lib/nextcloud-mcp-client/pom.xml`
- `lib/nextcloud-mcp-client/src/main/java/org/mcp/nextcloud/client/`
- `lib/nextcloud-mcp-client/src/test/java/org/mcp/nextcloud/client/NextcloudClientTest.java`
- `architect/active/2026-06-26-mvp-nextcloud-user-client/`
- `architect/pending/2026-06-26-nextcloud-mcp-core-architecture/`

## Client Surface Added

- Credentials and facade:
  - `NextcloudCredentials`
  - `NextcloudClient`
  - `NextcloudClientException`
- User/metadata:
  - `NextcloudUsersClient`
  - `NextcloudUser`
  - `NextcloudCapabilities`
- Files/WebDAV:
  - `NextcloudFilesClient`
  - `WebDavResource`
  - `WebDavOperation`
  - `WebDavParser`
- Sharing:
  - `NextcloudSharesClient`
  - `NextcloudShareesClient`
  - `ShareCreateRequest`
  - `ShareInfo`
  - `Sharee`
  - `ShareType`
  - `SharePermission`

## Behavior Added

- OCS current user and capabilities.
- WebDAV list, stat, mkdir, upload, download, delete, move, copy, favorite, and search request support.
- OCS share listing, creation, deletion, and sharee search.
- Fixture-backed tests proving auth headers, OCS path usage, WebDAV user-id paths, path encoding, collection slash behavior, and response parsing.
