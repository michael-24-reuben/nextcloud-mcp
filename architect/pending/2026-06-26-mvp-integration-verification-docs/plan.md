# Plan

1. Define the MVP verification matrix.
2. Add or update fixture data for WebDAV and OCS responses.
3. Verify Maven module graph and all MVP tests.
4. Verify CLI list/call/config/account flows.
5. Verify server health/list/call flows.
6. Add config examples and schema notes.
7. Add concise docs for MVP tools, auth, scopes, and limitations.

## Acceptance Criteria

- The MVP can be verified without admin credentials.
- Live verification, if run, uses a non-admin app-password account and avoids destructive calls unless explicitly approved.
- Config examples do not contain secrets.
- Docs clearly separate MVP from post-MVP capabilities.
