# Notes

## Activation

- Activated from pending for resolution order 5.
- Keep Spring, CLI command parsing, and actual Nextcloud tool implementations out of this slice.
- API module remains framework-neutral and depends only on core.
- Runtime module uses existing security policy/audit types instead of redefining principals, scopes, or audit events.
