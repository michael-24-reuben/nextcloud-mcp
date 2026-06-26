# Summary

Admin app provisioning is implemented in `nextcloud-mcp-admin`. The admin facade now exposes `apps()` with list, enabled/disabled filters, app info, enable, and disable methods. Enable/disable responses carry critical-risk metadata so future admin MCP tools can require confirmation before exposing them.
