# Architect Assignment

## Assignment Status
- assignmentStatus: admin API architecture resolved; return to MVP outbound slices
- lastUpdatedAt: 2026-06-26T20:45:00-04:00
- updatedBy: Codex
- currentBranch: master
- expectedBranch: master
- objectiveId: 2026-06-26-mvp-cli-caller
- objectiveTitle: MVP CLI Caller
- objectiveStatus: pending

## Current Architect Entries
- primary: architect/pending/2026-06-26-mvp-cli-caller
- recentlyVerifiedResolved: architect/resolved/2026-06-26-nextcloud-admin-api-architecture, architect/resolved/2026-06-26-admin-tools-policy-surface, architect/resolved/2026-06-26-admin-occ-bridge
- pendingFollowUps: 2026-06-26-mvp-cli-caller, 2026-06-26-mvp-spring-server-transport, 2026-06-26-mvp-integration-verification-docs
- pausedMvpFollowUps: none
- blockedBy: none
- shouldNotTouch: Preserve unrelated user changes and do not move the generated root Spring scaffold unless the server-module slice explicitly handles it.

## Current Objective
- goal: Resume outbound MVP wiring now that admin endpoint support is complete.
- completedSlices: Admin auth/client foundation, admin user provisioning, admin group/subadmin provisioning, admin app provisioning, admin share boundary, admin tool policy surface, guarded OCC bridge, and parent admin API architecture.
- nextSlice: Activate and implement `architect/pending/2026-06-26-mvp-cli-caller`.
- completionCriteria: MVP CLI, Spring server transport, and integration verification slices reach `resolved/` with tests before final MVP handoff.

## Last Run Summary
- runEndedAt: 2026-06-26T20:45:00-04:00
- workCompleted: Added admin MCP tool registrations with scoped policy metadata and added a guarded non-executing OCC command-plan bridge.
- testsRun: focused admin tools/OCC reactor, full Maven reactor, architect metadata parse, diff whitespace check.
- testResult: passed.
- verificationNote: Focused admin tests passed, then `.\mvnw.cmd test` passed. The full run executed the live SDK functionality smoke test against the temp account and cleaned up `/CodexScratch/nextcloud-mcp-smoke.txt`; no OCC execution or app enable/disable occurred.
- commitCreated: no

## Active Files

| File | State | Reason |
|---|---|---|
| architect/resolved/2026-06-26-nextcloud-admin-api-architecture/ | resolved | Parent admin API architecture completed. |
| architect/resolved/2026-06-26-admin-auth-client-foundation/ | resolved | Admin foundation completed. |
| architect/resolved/2026-06-26-admin-users-provisioning/ | resolved | Admin user provisioning completed. |
| architect/resolved/2026-06-26-admin-groups-subadmins/ | resolved | Admin groups/subadmins completed. |
| architect/resolved/2026-06-26-admin-apps-provisioning/ | resolved | Admin apps provisioning completed. |
| architect/resolved/2026-06-26-admin-share-boundary/ | resolved | Admin share boundary completed. |
| architect/resolved/2026-06-26-admin-tools-policy-surface/ | resolved | Admin tool policy surface completed. |
| architect/resolved/2026-06-26-admin-occ-bridge/ | resolved | Guarded OCC command-plan bridge completed. |
| architect/pending/2026-06-26-mvp-cli-caller/ | pending | Next MVP outbound slice. |

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
| architect/resolved/2026-06-26-admin-tools-policy-surface/ | moved/updated | Sixth admin child resolved. |
| architect/resolved/2026-06-26-admin-occ-bridge/ | moved/updated | Seventh admin child resolved. |
| architect/resolved/2026-06-26-nextcloud-admin-api-architecture/ | moved/updated | Parent admin API architecture resolved. |
| tools/nextcloud-mcp-admin-tools/ | updated | Added admin MCP registrations, policy metadata, and tests. |
| lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/ | updated | Added non-executing OCC command-plan bridge. |
| architect/ASSIGNMENT.md | updated | Current execution card points to admin tools policy surface. |
| architect/HANDOFF.md | updated | Continuation ledger records admin resolution progress. |

## Unfinished Files

| File | State | Remaining Work |
|---|---|---|
| architect/pending/2026-06-26-mvp-cli-caller/ | pending | CLI caller slice not started. |
| architect/pending/2026-06-26-mvp-spring-server-transport/ | pending | Server transport slice not started. |
| architect/pending/2026-06-26-mvp-integration-verification-docs/ | pending | Integration verification/docs slice not started. |

## Decisions Made
- Decision: `nextcloud-mcp-admin` owns arbitrary user, group, membership, and subadmin provisioning routes.
- Decision: Admin clients reuse `NextcloudHttpRequestFactory` but keep admin-local parsing and exceptions.
- Decision: Repeated OCS form fields are supported in the shared HTTP layer for `groups[]` and `subadmin[]`.
- Decision: Live admin calls stay out of these endpoint slices; fake HTTP tests are the verification source.
- Decision: Admin app enable/disable returns `AdminRiskLevel.CRITICAL` metadata for future tool gating.
- Decision: Admin-authenticated share operations reuse `nextcloud-mcp-client` share APIs and remain outside admin provisioning/WebDAV control.
- Decision: Admin MCP tools use dedicated admin scopes and carry risk/confirmation metadata.
- Decision: OCC support is command-plan only; it does not execute shell commands.

## Blockers
- none

## Risks
- Risk: Existing destructive tool policy still shares the generic destructive permission check; admin destructive policy can be split later if needed.
- Risk: OCC command plans assume the default AIO container name unless callers provide a bridge configured for another container.

## Next Action
Return to MVP outbound work by activating `architect/pending/2026-06-26-mvp-cli-caller`.

## Verification Baseline

```powershell
git -C 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp' status --short --branch
Get-ChildItem -Recurse -Filter meta.json -LiteralPath 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp\architect' | ForEach-Object { Get-Content -LiteralPath $_.FullName | ConvertFrom-Json | Out-Null }
git -C 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp' diff --check -- architect lib tools
.\mvnw.cmd test
```
