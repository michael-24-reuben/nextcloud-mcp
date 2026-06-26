# Assessment

The admin client foundation needed an explicit user provisioning surface before outbound CLI/server layers can expose admin operations. These endpoints differ from the normal current-user client because they target arbitrary users and include governance mutations.

Create, update, enable/disable, welcome resend, and delete are now represented in `nextcloud-mcp-admin`; delete remains critical-risk for later tool-policy gating.
