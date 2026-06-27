# Assessment

Admin tool exposure belongs in `nextcloud-mcp-admin-tools` and depends on the already separated admin client module. The resolved surface uses dedicated admin scopes, stable `nextcloud.admin.*` tool names, and metadata that preserves the blueprint risk classification for future transports.

High-risk, destructive, and critical operations are flagged for confirmation. The existing runtime policy also requires the shared destructive permission check to pass for destructive tools; that remains a known policy quirk to revisit if admin destructive permissions need to be fully separated from file deletion semantics.
