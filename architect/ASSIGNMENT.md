# Architect Assignment

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
- recentlyVerifiedResolved: architect/resolved/2026-06-26-admin-auth-client-foundation, architect/resolved/2026-06-26-admin-users-provisioning, architect/resolved/2026-06-26-admin-groups-subadmins, architect/resolved/2026-06-26-admin-apps-provisioning, architect/resolved/2026-06-26-admin-share-boundary
- pendingFollowUps: 2026-06-26-admin-tools-policy-surface, 2026-06-26-admin-occ-bridge
- pausedMvpFollowUps: 2026-06-26-mvp-cli-caller, 2026-06-26-mvp-spring-server-transport, 2026-06-26-mvp-integration-verification-docs
- blockedBy: none
- shouldNotTouch: Preserve unrelated user changes and do not move the generated root Spring scaffold unless the server-module slice explicitly handles it.

## Current Objective
- goal: Finish admin API endpoint support before returning to outbound CLI/server/integration MVP slices.
- completedSlices: Admin auth/client foundation, admin user provisioning, admin group/subadmin provisioning, admin app provisioning, admin share boundary.
- nextSlice: Activate and implement `architect/pending/2026-06-26-admin-tools-policy-surface`.
- completionCriteria: Each admin child reaches `resolved/` with fake HTTP verification before admin tool exposure or outbound wiring begins.

## Last Run Summary
- runEndedAt: 2026-06-26T17:00:12-04:00
- workCompleted: Added admin app provisioning and admin-authenticated share boundary support in `nextcloud-mcp-admin`.
- testsRun: focused admin reactor, full Maven reactor, architect metadata parse, diff whitespace check.
- testResult: passed.
- verificationNote: `.\mvnw.cmd -pl lib/nextcloud-mcp-admin -am test` and `.\mvnw.cmd test` passed. No live Nextcloud admin, app enable/disable, or share calls were made.
- commitCreated: no

## Active Files

| File | State | Reason |
|---|---|---|
| architect/pending/2026-06-26-nextcloud-admin-api-architecture/ | pending | Parent admin API architecture record. |
| architect/resolved/2026-06-26-admin-auth-client-foundation/ | resolved | Admin foundation completed. |
| architect/resolved/2026-06-26-admin-users-provisioning/ | resolved | Admin user provisioning completed. |
| architect/resolved/2026-06-26-admin-groups-subadmins/ | resolved | Admin groups/subadmins completed. |
| architect/resolved/2026-06-26-admin-apps-provisioning/ | resolved | Admin apps provisioning completed. |
| architect/resolved/2026-06-26-admin-share-boundary/ | resolved | Admin share boundary completed. |
| architect/pending/2026-06-26-admin-tools-policy-surface/ | pending | Next admin child to activate. |

## Files Changed By This Run

| File | State | Reason |
|---|---|---|
| lib/nextcloud-mcp-admin/ | updated | Added admin facade, credentials, auth, users, groups/subadmins clients and tests. |
| lib/nextcloud-mcp-admin/ | updated | Added app provisioning client, critical-risk app operation metadata, and admin share support adapter. |
| lib/nextcloud-mcp-config/ | updated | Validates enabled admin config references an enabled admin account. |
| lib/nextcloud-mcp-http/src/main/java/org/mcp/nextcloud/http/NextcloudHttpRequestFactory.java | updated | Supports ordered repeated OCS form fields. |
| architect/resolved/2026-06-26-admin-auth-client-foundation/ | moved/updated | First admin child resolved. |
| architect/resolved/2026-06-26-admin-users-provisioning/ | moved/updated | Second admin child resolved. |
| architect/resolved/2026-06-26-admin-groups-subadmins/ | moved/updated | Third admin child resolved. |
| architect/resolved/2026-06-26-admin-apps-provisioning/ | moved/updated | Fourth admin child resolved. |
| architect/resolved/2026-06-26-admin-share-boundary/ | moved/updated | Fifth admin child resolved. |
| architect/ASSIGNMENT.md | updated | Current execution card points to admin tools policy surface. |
| architect/HANDOFF.md | updated | Continuation ledger records admin resolution progress. |

## Unfinished Files

| File | State | Remaining Work |
|---|---|---|
| architect/pending/2026-06-26-admin-tools-policy-surface/ | pending | Admin tool policy/confirmation surface not started. |
| tools/nextcloud-mcp-admin-tools/ | untouched | Admin tools wait for admin tool policy-surface child. |

## Decisions Made
- Decision: `nextcloud-mcp-admin` owns arbitrary user, group, membership, and subadmin provisioning routes.
- Decision: Admin clients reuse `NextcloudHttpRequestFactory` but keep admin-local parsing and exceptions.
- Decision: Repeated OCS form fields are supported in the shared HTTP layer for `groups[]` and `subadmin[]`.
- Decision: Live admin calls stay out of these endpoint slices; fake HTTP tests are the verification source.
- Decision: Admin app enable/disable returns `AdminRiskLevel.CRITICAL` metadata for future tool gating.
- Decision: Admin-authenticated share operations reuse `nextcloud-mcp-client` share APIs and remain outside admin provisioning/WebDAV control.

## Blockers
- none

## Risks
- Risk: Admin delete and subadmin operations are implemented at client level but need explicit tool-policy confirmation before exposure.
- Risk: App enable/disable can break a live server if later exposed without critical-risk gating.

## Next Action
Continue admin work by activating `architect/pending/2026-06-26-admin-tools-policy-surface` and defining MCP tool exposure, confirmation, and risk behavior for admin operations.

## Verification Baseline

```powershell
git -C 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp' status --short --branch
Get-ChildItem -Recurse -Filter meta.json -LiteralPath 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp\architect' | ForEach-Object { Get-Content -LiteralPath $_.FullName | ConvertFrom-Json | Out-Null }
git -C 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp' diff --check -- architect lib
.\mvnw.cmd test
```
