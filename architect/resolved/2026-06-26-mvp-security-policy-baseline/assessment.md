# Assessment

The security slice needed a small framework-neutral policy layer before tools and server transport can safely expose capabilities. The MVP requirement is not a full auth system; it is a baseline contract for principal context, scope checks, admin exclusion, destructive-action handling, masking, and audit event shape.

This belongs in `lib/nextcloud-mcp-security` and depends only on core primitives.
