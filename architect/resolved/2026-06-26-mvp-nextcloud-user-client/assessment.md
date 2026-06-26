# Assessment

## Result

The fourth MVP slice needed a Spring-free Java client layer that future runtime and tool modules can call without constructing raw Nextcloud HTTP requests themselves.

## Final Design

- `NextcloudClient` is the facade for user, files, shares, and sharees clients.
- `NextcloudCredentials` keeps internal account id, API base URL, login username, and app password separate.
- OCS APIs use app-password Basic Auth plus `OCS-APIRequest: true` and `Accept: application/json`.
- WebDAV APIs use app-password Basic Auth, per-segment path encoding, and collection trailing slash handling.
- OCS parsing uses Jackson JSON.
- WebDAV multistatus parsing uses a namespace-aware JDK DOM parser with external entity processing disabled.

## Risk

No live Nextcloud calls were made in this slice. Live smoke coverage remains delegated to later integration verification/docs and must remain opt-in.
