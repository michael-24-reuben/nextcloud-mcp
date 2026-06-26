# Assignment Handoff

## Assignment Status
- assignmentStatus: first five admin API child architects resolved after user requested the next two admin resolutions
- lastUpdatedAt: 2026-06-26T17:00:12-04:00
- updatedBy: Codex
- currentBranch: master
- expectedBranch: master
- objectiveId: 2026-06-26-nextcloud-admin-api-architecture
- objectiveTitle: Nextcloud Admin API Architecture
- objectiveStatus: in-progress

## Current Architect Entries
- primary: architect/pending/2026-06-26-nextcloud-admin-api-architecture
- lastVerifiedCompleted: architect/resolved/2026-06-26-admin-share-boundary
- pendingFollowUps: see Admin API Resolution Order below
- related: blueprint/project-structure.md, blueprint/nextcloud-client-api-model.md, blueprint/nextcloud-admin-api-model.md, AGENTS.md
- blockedBy: none
- shouldNotTouch: Preserve unrelated user changes and do not move the generated root Spring scaffold unless the server-module slice explicitly handles it.

## MVP Resolution Order

1. architect/resolved/2026-06-26-mvp-build-foundation
2. architect/resolved/2026-06-26-mvp-core-http-config
3. architect/resolved/2026-06-26-mvp-security-policy-baseline
4. architect/resolved/2026-06-26-mvp-nextcloud-user-client
5. architect/resolved/2026-06-26-mvp-mcp-tool-runtime
6. architect/resolved/2026-06-26-mvp-files-share-user-tools
7. architect/pending/2026-06-26-mvp-cli-caller
8. architect/pending/2026-06-26-mvp-spring-server-transport
9. architect/pending/2026-06-26-mvp-integration-verification-docs

The MVP outbound slices are intentionally paused until admin endpoint support is far enough along.

## Admin API Resolution Order

Parent:

- architect/pending/2026-06-26-nextcloud-admin-api-architecture

Children:

1. architect/resolved/2026-06-26-admin-auth-client-foundation
2. architect/resolved/2026-06-26-admin-users-provisioning
3. architect/resolved/2026-06-26-admin-groups-subadmins
4. architect/resolved/2026-06-26-admin-apps-provisioning
5. architect/resolved/2026-06-26-admin-share-boundary
6. architect/pending/2026-06-26-admin-tools-policy-surface
7. architect/pending/2026-06-26-admin-occ-bridge

## Current Objective
- goal: Finish admin API endpoint support before returning to CLI/server/integration wiring.
- scope: Admin foundation, users, groups, membership, subadmin, app provisioning, and admin-authenticated share boundary support are implemented in `nextcloud-mcp-admin`.
- nonGoals: No live admin calls were made. No live app enable/disable calls were made. No admin MCP tools or confirmation policy yet. No Spring server wiring in this admin-client work.
- completionCriteria: Admin client endpoint and boundary slices are resolved with fake HTTP tests before tool exposure and outbound runtime wiring.

## Admin Context
- Admin module name: `nextcloud-mcp-admin`.
- Admin tools module: `tools/nextcloud-mcp-admin-tools`.
- Source blueprint: `blueprint/nextcloud-admin-api-model.md`.
- Shared mechanics: Basic Auth app-password credentials, `OCS-APIRequest: true`, `Accept: application/json`, and shared route construction.
- Boundary: `nextcloud-mcp-client` owns WebDAV files, normal shares/sharees, current user, capabilities, and user-content behavior.
- Boundary: `nextcloud-mcp-admin` owns arbitrary user provisioning, group provisioning, subadmin management, app provisioning, and optional guarded OCC bridge behavior.
- Important overlap: `/ocs/v1.php/cloud/users/{userid}` is self metadata only in the user/content boundary, but arbitrary-user reads/mutations are admin-owned.
- Share warning: admin credentials can call normal share APIs through `NextcloudAdminSharesSupport`, which reuses `nextcloud-mcp-client`; `AdminProvisioningClient` is not `FileShareClient`.
- App warning: app enable/disable methods exist at client level but return `AdminRiskLevel.CRITICAL` and must not be exposed without confirmation policy.

## Last Run Summary
- runEndedAt: 2026-06-26T17:00:12-04:00
- workCompleted: Implemented the first five admin API children:
  - `NextcloudAdminCredentials`, `NextcloudAdminClient`, `AdminAuthClient`, admin-local OCS parser, identity probe.
  - `NextcloudAdminUsersClient` with list/search, get, create, update field, editable fields, enable/disable, delete, groups/subadmins reads, welcome resend.
  - `NextcloudAdminGroupsClient` with list/search, create, members, subadmins, display-name update, delete, membership add/remove, subadmin promote/demote.
  - `NextcloudAdminAppsClient` with app list, enabled/disabled filters, app info, enable, disable, and critical-risk operation metadata.
  - `NextcloudAdminSharesSupport` with admin credentials adapted into the normal share client so share routes stay under `/ocs/v2.php/apps/files_sharing/api/v1`.
- workPartiallyCompleted: Admin tools policy and OCC bridge remain pending.
- testsRun: focused admin reactor, full Maven reactor, architect metadata parse, diff whitespace check.
- testResult: passed.
- verificationSetup: Tests used fake HTTP clients only; no live Nextcloud admin, app enable/disable, or share calls.
- commitCreated: no
- commitHash: n/a

## Files Changed By This Run

| File | State | Reason |
|---|---|---|
| lib/nextcloud-mcp-admin/pom.xml | updated | Added core/config/Jackson dependencies needed by admin client code. |
| lib/nextcloud-mcp-admin/pom.xml | updated | Added `nextcloud-mcp-client` dependency for admin share boundary reuse. |
| lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/ | added/updated | Admin credentials, facade, auth, users, groups/subadmins, apps, and share-boundary clients/records. |
| lib/nextcloud-mcp-admin/src/test/java/org/mcp/nextcloud/admin/ | added/updated | Fake HTTP-backed admin client, apps, and share-boundary tests. |
| lib/nextcloud-mcp-config/src/main/java/org/mcp/nextcloud/config/validation/ConfigValidator.java | updated | Validates enabled admin config references an enabled admin account. |
| lib/nextcloud-mcp-config/src/test/java/org/mcp/nextcloud/config/ConfigValidatorTest.java | updated | Covers admin account validation. |
| lib/nextcloud-mcp-http/src/main/java/org/mcp/nextcloud/http/NextcloudHttpRequestFactory.java | updated | Adds ordered repeated OCS form-field support. |
| architect/resolved/2026-06-26-admin-auth-client-foundation/ | moved/updated | First admin child resolved. |
| architect/resolved/2026-06-26-admin-users-provisioning/ | moved/updated | Second admin child resolved. |
| architect/resolved/2026-06-26-admin-groups-subadmins/ | moved/updated | Third admin child resolved. |
| architect/resolved/2026-06-26-admin-apps-provisioning/ | moved/updated | Fourth admin child resolved. |
| architect/resolved/2026-06-26-admin-share-boundary/ | moved/updated | Fifth admin child resolved. |
| architect/ASSIGNMENT.md | updated | Current execution card points to admin tools policy surface. |
| architect/HANDOFF.md | updated | Persistent ledger records admin progress. |

## Existing Dirty Work Preserved

| Area | State | Handling |
|---|---|---|
| scratch/callout/calling-to-michael.wav | added before admin work | Preserved. |

## Unfinished Files

| File | State | Remaining Work | Safe Next Action |
|---|---|---|---|
| architect/pending/2026-06-26-admin-tools-policy-surface/ | pending | Admin tool exposure, risk labels, and confirmation behavior not started. | Move to active and define/admin tool behavior before exposing admin methods. |
| tools/nextcloud-mcp-admin-tools/ | untouched | Admin tool exposure not started. | Wait for admin tools policy-surface child. |

## Next Files To Touch

| File | Planned Change | Depends On |
|---|---|---|
| architect/pending/2026-06-26-admin-tools-policy-surface/ | Move to active. | Depends on resolved admin endpoint clients and share boundary. |
| tools/nextcloud-mcp-admin-tools/ | Add admin tool definitions and risk/confirmation policy. | Depends on admin tools policy-surface decisions. |

## Decisions Made
- Decision: Admin app-password credentials resolve from `NextcloudMcpConfig.admin.accountId`.
- Decision: Enabled admin config must point to an enabled account marked `admin=true`.
- Decision: Admin owns arbitrary user, group, membership, and subadmin provisioning.
- Decision: Admin owns app provisioning, but app enable/disable are critical-risk operations.
- Decision: Admin-authenticated share calls reuse `nextcloud-mcp-client` through `NextcloudAdminSharesSupport`; share routes remain normal OCS share routes.
- Decision: Shared HTTP helper now supports ordered repeated OCS form fields for routes such as user creation.
- Decision: Admin endpoint client slices use fake HTTP tests only; live calls are deferred to explicit integration/smoke-test work.

## Blockers
- none

## Risks
- Risk: Client-level delete, subadmin, and app enable/disable methods can be dangerous if exposed without policy.
  - Mitigation: Keep admin tools pending until `admin-tools-policy-surface` defines high/critical confirmation behavior.
- Risk: App enable/disable can destabilize AIO if used blindly.
  - Mitigation: Implement client methods but treat exposure as critical-risk and verify via fake HTTP only in the app slice.

## Next Action
Continue admin endpoint work by activating `architect/pending/2026-06-26-admin-tools-policy-surface` and defining MCP tool exposure, confirmation, and risk behavior for admin operations.

## Resume Commands

```powershell
Get-Content -LiteralPath 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp\architect\ASSIGNMENT.md'
Get-Content -LiteralPath 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp\architect\HANDOFF.md'
Get-Content -LiteralPath 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp\architect\pending\2026-06-26-admin-tools-policy-surface\meta.json' | ConvertFrom-Json | Out-Null
git -C 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp' status --short --branch
.\mvnw.cmd test
```
