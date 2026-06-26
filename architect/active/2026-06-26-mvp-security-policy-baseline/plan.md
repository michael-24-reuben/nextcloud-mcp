# Plan

1. Define MVP scopes for files, shares, user metadata, comments placeholder, trash placeholder, and admin exclusion.
2. Implement principal/account context.
3. Implement account and tool policy checks.
4. Implement secret masking utilities and tests.
5. Implement audit event model and runtime interceptor contract support.
6. Make destructive operations require explicit config opt-in or dedicated scope.

## Acceptance Criteria

- Non-admin accounts cannot request admin behavior.
- User metadata access is constrained to the authenticated account unless an admin path is later enabled.
- Delete/share-write operations require explicit scopes.
- Secrets are masked in errors, audit records, and display output.
