# Architect Assignment

## Assignment Status
- assignmentStatus: first six MVP child architects resolved; admin API architects remain staged
- lastUpdatedAt: 2026-06-26T15:54:38-04:00
- updatedBy: Codex
- currentBranch: master
- expectedBranch: master
- objectiveId: 2026-06-26-nextcloud-mcp-core-architecture
- objectiveTitle: Nextcloud MCP Core Architecture
- objectiveStatus: pending

## Current Architect Entries
- primary: architect/pending/2026-06-26-nextcloud-mcp-core-architecture
- recentlyVerifiedResolved: architect/resolved/2026-06-26-mvp-build-foundation, architect/resolved/2026-06-26-mvp-core-http-config, architect/resolved/2026-06-26-mvp-security-policy-baseline, architect/resolved/2026-06-26-mvp-nextcloud-user-client, architect/resolved/2026-06-26-mvp-mcp-tool-runtime, architect/resolved/2026-06-26-mvp-files-share-user-tools
- pendingFollowUps: 2026-06-26-mvp-cli-caller, 2026-06-26-mvp-spring-server-transport, 2026-06-26-mvp-integration-verification-docs
- adminFollowUps: 2026-06-26-nextcloud-admin-api-architecture, 2026-06-26-admin-auth-client-foundation, 2026-06-26-admin-users-provisioning, 2026-06-26-admin-groups-subadmins, 2026-06-26-admin-apps-provisioning, 2026-06-26-admin-share-boundary, 2026-06-26-admin-tools-policy-surface, 2026-06-26-admin-occ-bridge
- blockedBy: none
- shouldNotTouch: Preserve unrelated user changes and do not move the generated root Spring scaffold unless the server-module slice explicitly handles it.

## Current Objective
- goal: Resolve the MVP child architects first, in dependency order, while keeping the admin API track staged from `blueprint/nextcloud-admin-api-model.md`.
- completedSlices: Created nine MVP child architects; resolved build foundation, core/http/config, security policy baseline, Nextcloud user client, MCP tool runtime, and files/share/user tool modules; created admin API parent and child architects.
- nextSlice: Activate and implement `architect/pending/2026-06-26-mvp-cli-caller` unless the user explicitly redirects to admin work.
- adminObjective: After MVP or explicit redirect, start with `architect/pending/2026-06-26-admin-auth-client-foundation`, then users, groups/subadmins, apps, share-boundary, admin tools policy, and guarded OCC bridge.
- completionCriteria: Each MVP child is moved through active to resolved with verification evidence before post-MVP child work begins, unless the user explicitly changes priority.

## Last Run Summary
- runEndedAt: 2026-06-26T15:54:38-04:00
- workCompleted: Resolved MVP resolution 6 by adding concrete files, share/sharee, and user tool registration factories plus fake HTTP-backed tests.
- testsRun: focused compile, focused tool-module reactor tests, full Maven reactor tests.
- testResult: passed.
- verificationNote: `.\mvnw.cmd -pl tools/nextcloud-mcp-files-tools,tools/nextcloud-mcp-share-tools,tools/nextcloud-mcp-user-tools -am test` and `.\mvnw.cmd test` passed. No live Nextcloud calls were made.
- commitCreated: no

## Active Files

| File | State | Reason |
|---|---|---|
| architect/pending/2026-06-26-nextcloud-mcp-core-architecture/ | pending | Parent architecture record. |
| architect/resolved/2026-06-26-mvp-files-share-user-tools/ | resolved | Sixth MVP child completed. |
| architect/pending/2026-06-26-mvp-cli-caller/ | pending | Next MVP child to activate. |
| architect/pending/2026-06-26-nextcloud-admin-api-architecture/ | pending | Parent admin API architecture record. |
| architect/HANDOFF.md | current | Detailed continuation context. |

## Files Changed By This Run

| File | State | Reason |
|---|---|---|
| architect/resolved/2026-06-26-mvp-files-share-user-tools/ | moved/updated | Activated then resolved sixth MVP child. |
| tools/nextcloud-mcp-files-tools/ | updated | Added files tool registration factory and tests. |
| tools/nextcloud-mcp-share-tools/ | updated | Added share/sharee tool registration factory and tests. |
| tools/nextcloud-mcp-user-tools/ | updated | Added user tool registration factory and tests. |
| lib/nextcloud-mcp-http/src/test/java/org/mcp/nextcloud/http/HttpHelpersTest.java | updated | Made form-body assertion deterministic. |
| architect/ASSIGNMENT.md | updated | Current execution card now points to resolution 7. |
| architect/HANDOFF.md | updated | Continuation ledger now records resolution 6 outcome. |

## Unfinished Files

| File | State | Remaining Work |
|---|---|---|
| architect/pending/2026-06-26-mvp-cli-caller/ | pending | Next MVP child not started. |
| architect/pending/2026-06-26-admin-auth-client-foundation/ | pending | First admin child not started. |

## Decisions Made
- Decision: Tool modules expose public `Nextcloud*Tools.registrations(NextcloudClient)` factories returning runtime `ToolRegistration` values.
- Decision: Share get, update, send email, and recommended sharees are registered as deferred tools until the client layer supports those routes.
- Decision: Tool handlers use invocation account id for user-content operations.
- Decision: Admin API work remains staged and should not displace the current MVP resolution order unless the user explicitly redirects.

## Blockers
- none

## Risks
- Risk: Deferred share tools are discoverable but not executable; CLI/server layers must surface `tool.deferred` cleanly.
- Risk: CLI caller wiring must preserve policy checks through the runtime dispatcher rather than invoking handlers directly.

## Next Action
Continue with MVP resolution 7 by activating `architect/pending/2026-06-26-mvp-cli-caller`, unless the user explicitly asks to start the admin API track.

## Verification Baseline

```powershell
git -C 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp' status --short --branch
Get-ChildItem -Recurse -Filter meta.json -LiteralPath 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp\architect' | ForEach-Object { Get-Content -LiteralPath $_.FullName | ConvertFrom-Json | Out-Null }
git -C 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp' diff --check -- architect tools lib/nextcloud-mcp-http/src/test/java/org/mcp/nextcloud/http/HttpHelpersTest.java
.\mvnw.cmd test
```
