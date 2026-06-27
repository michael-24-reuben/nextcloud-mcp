# Fixes

- Added `AdminOccCommandPlan` as the immutable OCC plan value.
- Added `NextcloudAdminOccBridge` for allowlisted maintenance, files scan, app config, background-job, and admin-group recovery plans.
- Exposed OCC command-plan tools from `NextcloudAdminTools` with critical-risk metadata and `executes=false` responses.
- Added tests for command construction and policy-gated OCC tool handling.
