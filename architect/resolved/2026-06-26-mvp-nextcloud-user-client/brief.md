# Brief

## Objective

Implement the non-admin Nextcloud client used by MVP tools.

## Scope

- App-password Basic Auth.
- Current user resolution.
- WebDAV file operations for list, stat, upload, download, mkdir, delete, move, copy, favorite, and search if approved.
- OCS shares and sharee discovery.
- OCS current user and capabilities.
- Robust WebDAV XML parsing for requested Nextcloud properties.

## Non-Goals

- Admin client.
- Trashbin, versions, comments, status, preferences, public share WebDAV, and bulk upload unless explicitly pulled into MVP.
- Live destructive testing without an explicit policy decision.

## Resolution Position

Resolve after core/http/config and before tool modules.
