# Fixes

## Files Changed

- `lib/nextcloud-mcp-security/src/main/java/org/mcp/nextcloud/security/**`
- `lib/nextcloud-mcp-security/src/test/java/org/mcp/nextcloud/security/SecurityPolicyTest.java`

## Behavioral Changes

- Added MVP scope constants for files, shares, user read, comments write, and trash restore.
- Added principal and invocation context records.
- Added account access policy that blocks non-admin access to admin accounts and cross-account use.
- Added tool access policy with required-scope checks, admin-scope exclusion for non-admin principals, and delete-deny behavior unless delete scope is present.
- Added secret masker and audit event model.
