# Assignment Handoff

## Assignment Status
- assignmentStatus: admin API architecture resolved; ready to resume MVP outbound slices
- lastUpdatedAt: 2026-06-26T20:45:00-04:00
- updatedBy: Codex
- currentBranch: master
- expectedBranch: master
- objectiveId: 2026-06-26-mvp-cli-caller
- objectiveTitle: MVP CLI Caller
- objectiveStatus: pending

## Current Architect Entries
- primary: architect/pending/2026-06-26-mvp-cli-caller
- lastVerifiedCompleted: architect/resolved/2026-06-26-nextcloud-admin-api-architecture
- pendingFollowUps: architect/pending/2026-06-26-mvp-cli-caller, architect/pending/2026-06-26-mvp-spring-server-transport, architect/pending/2026-06-26-mvp-integration-verification-docs
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

The MVP outbound slices are unpaused now that admin endpoint support is resolved.

## Admin API Resolution Order

Parent:

- architect/resolved/2026-06-26-nextcloud-admin-api-architecture

Children:

1. architect/resolved/2026-06-26-admin-auth-client-foundation
2. architect/resolved/2026-06-26-admin-users-provisioning
3. architect/resolved/2026-06-26-admin-groups-subadmins
4. architect/resolved/2026-06-26-admin-apps-provisioning
5. architect/resolved/2026-06-26-admin-share-boundary
6. architect/resolved/2026-06-26-admin-tools-policy-surface
7. architect/resolved/2026-06-26-admin-occ-bridge

## Current Objective
- goal: Resume MVP outbound wiring after completing the admin API architecture.
- scope: Admin foundation, users, groups, membership, subadmin, app provisioning, admin-authenticated share boundary support, admin MCP tool policy, and guarded OCC planning are implemented.
- nonGoals: OCC bridge does not execute shell commands. Spring server wiring and CLI behavior remain in the MVP outbound records.
- completionCriteria: Activate and resolve the remaining MVP CLI/server/integration records with verification.

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
- runEndedAt: 2026-06-26T20:45:00-04:00
- workCompleted: Implemented all admin API children:
  - `NextcloudAdminCredentials`, `NextcloudAdminClient`, `AdminAuthClient`, admin-local OCS parser, identity probe.
  - `NextcloudAdminUsersClient` with list/search, get, create, update field, editable fields, enable/disable, delete, groups/subadmins reads, welcome resend.
  - `NextcloudAdminGroupsClient` with list/search, create, members, subadmins, display-name update, delete, membership add/remove, subadmin promote/demote.
  - `NextcloudAdminAppsClient` with app list, enabled/disabled filters, app info, enable, disable, and critical-risk operation metadata.
  - `NextcloudAdminSharesSupport` with admin credentials adapted into the normal share client so share routes stay under `/ocs/v2.php/apps/files_sharing/api/v1`.
  - `NextcloudAdminTools` with admin scopes, risk metadata, confirmation flags, and admin endpoint tool registrations.
  - `NextcloudAdminOccBridge` and `AdminOccCommandPlan` for allowlisted non-executing OCC command plans.
- workPartiallyCompleted: none for admin API architecture.
- testsRun: focused admin tools/OCC reactor, full Maven reactor, architect metadata parse, diff whitespace check.
- testResult: passed.
- verificationSetup: Focused tests used fake HTTP clients and command-plan checks. The full Maven run also executed the live SDK functionality smoke test against the temp account and cleaned up `/CodexScratch/nextcloud-mcp-smoke.txt`; no OCC execution or app enable/disable occurred.
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
| architect/resolved/2026-06-26-admin-tools-policy-surface/ | moved/updated | Sixth admin child resolved. |
| architect/resolved/2026-06-26-admin-occ-bridge/ | moved/updated | Seventh admin child resolved. |
| architect/resolved/2026-06-26-nextcloud-admin-api-architecture/ | moved/updated | Parent admin API architecture resolved. |
| tools/nextcloud-mcp-admin-tools/ | updated | Added admin MCP tool surface, risk metadata, policy tests, and OCC tool plans. |
| lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/NextcloudAdminOccBridge.java | added | Non-executing guarded OCC command-plan bridge. |
| lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/AdminOccCommandPlan.java | added | Immutable OCC command-plan record. |
| architect/ASSIGNMENT.md | updated | Current execution card points to admin tools policy surface. |
| architect/HANDOFF.md | updated | Persistent ledger records admin progress. |

## Existing Dirty Work Preserved

| Area | State | Handling |
|---|---|---|
| scratch/callout/calling-to-michael.wav | added before admin work | Preserved. |

## Unfinished Files

| File | State | Remaining Work | Safe Next Action |
|---|---|---|---|
| architect/pending/2026-06-26-mvp-cli-caller/ | pending | CLI caller behavior not started. | Activate this next. |
| architect/pending/2026-06-26-mvp-spring-server-transport/ | pending | Server transport behavior not started. | Resolve after CLI caller or according to outbound order. |
| architect/pending/2026-06-26-mvp-integration-verification-docs/ | pending | Integration verification/docs not started. | Resolve last in MVP order. |

## Next Files To Touch

| File | Planned Change | Depends On |
|---|---|---|
| architect/pending/2026-06-26-mvp-cli-caller/ | Move to active and implement CLI caller behavior. | Depends on resolved tool runtime and current admin/client APIs. |
| tools/nextcloud-mcp-*/ | Wire CLI-facing behavior as required by the CLI caller architect. | Depends on CLI caller decisions. |

## Decisions Made
- Decision: Admin app-password credentials resolve from `NextcloudMcpConfig.admin.accountId`.
- Decision: Enabled admin config must point to an enabled account marked `admin=true`.
- Decision: Admin owns arbitrary user, group, membership, and subadmin provisioning.
- Decision: Admin owns app provisioning, but app enable/disable are critical-risk operations.
- Decision: Admin-authenticated share calls reuse `nextcloud-mcp-client` through `NextcloudAdminSharesSupport`; share routes remain normal OCS share routes.
- Decision: Shared HTTP helper now supports ordered repeated OCS form fields for routes such as user creation.
- Decision: Admin endpoint client slices use fake HTTP tests only; live calls are deferred to explicit integration/smoke-test work.
- Decision: Admin MCP tools use dedicated admin scopes and carry risk/confirmation metadata.
- Decision: OCC bridge support is command-plan only and returns `executes=false`; no shell execution happens in the SDK/tool layer.

## Blockers
- none

## Risks
- Risk: Existing destructive tool policy still shares the generic destructive permission check.
  - Mitigation: Admin tools add explicit admin scopes and risk/confirmation metadata; split destructive permission semantics later if needed.
- Risk: OCC command plans assume the default AIO container name.
  - Mitigation: `NextcloudAdminOccBridge` can be constructed with a different container name when needed.

## Next Action
Return to MVP outbound work by activating `architect/pending/2026-06-26-mvp-cli-caller`.

## Resume Commands

```powershell
Get-Content -LiteralPath 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp\architect\ASSIGNMENT.md'
Get-Content -LiteralPath 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp\architect\HANDOFF.md'
Get-Content -LiteralPath 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp\architect\pending\2026-06-26-mvp-cli-caller\meta.json' | ConvertFrom-Json | Out-Null
git -C 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp' status --short --branch
.\mvnw.cmd test
```
