# Assessment

The admin module existed only as a POM and README, so future admin endpoint slices had no facade, credentials, OCS parser, or shared request helper base to build on.

The correct boundary is a separate `nextcloud-mcp-admin` client package that reuses `nextcloud-mcp-http` for transport/auth but does not depend on the normal user/content client module.
