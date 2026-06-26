# Fixes

## Files Tools

- Added `NextcloudFilesTools` with registration descriptors and handlers for list, stat, download, upload, mkdir, delete, move, copy, search, and favorite.
- File handlers use the invocation account id for WebDAV paths.
- Download returns base64 content with byte length. Upload accepts plain text or base64 input.

## Share Tools

- Added `NextcloudShareTools` with registration descriptors and handlers for share list, create, delete, and sharee search.
- Registered unsupported blueprint tools as deferred: get, update, send email, and recommended sharees.

## User Tools

- Added `NextcloudUserTools` with authenticated user, capabilities, and self metadata registrations.

## Build and Tests

- Added `nextcloud-mcp-tool-runtime` dependencies to the three MVP tool modules.
- Added fake HTTP-backed tests for descriptor coverage, scope metadata, route selection, and representative handler invocation.
- Made one HTTP helper test deterministic by replacing unordered `Map.of` form input with an insertion-ordered map.
