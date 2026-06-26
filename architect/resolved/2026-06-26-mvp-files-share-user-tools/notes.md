# Notes

## 2026-06-26 Activation

- Activated after resolving the core client and tool runtime slices.
- Scope remains limited to tool descriptors and handlers in the files, share, and user tool modules.
- No Spring transport, CLI caller, admin tooling, or live Nextcloud calls are part of this slice.

## 2026-06-26 Implementation

- Added registration factories for files, shares/sharees, and user tools.
- File tools are implemented for list, stat, download, upload, mkdir, delete, move, copy, search, and favorite.
- Share tools are implemented for list, create, delete, and sharee search.
- `nextcloud.shares.get`, `nextcloud.shares.update`, `nextcloud.shares.send_email`, and `nextcloud.sharees.recommended` are registered as deferred tools because the client layer does not expose those route methods yet.
- User tools are implemented for `me`, `capabilities`, and authenticated-account metadata.
