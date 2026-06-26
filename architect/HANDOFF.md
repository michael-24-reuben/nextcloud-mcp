# Assignment Handoff

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
- lastVerifiedCompleted: architect/resolved/2026-06-26-mvp-files-share-user-tools
- pendingFollowUps: see MVP resolution order and admin API resolution order below
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

## Admin API Resolution Order

Parent:

- architect/pending/2026-06-26-nextcloud-admin-api-architecture

Children:

1. architect/pending/2026-06-26-admin-auth-client-foundation
2. architect/pending/2026-06-26-admin-users-provisioning
3. architect/pending/2026-06-26-admin-groups-subadmins
4. architect/pending/2026-06-26-admin-apps-provisioning
5. architect/pending/2026-06-26-admin-share-boundary
6. architect/pending/2026-06-26-admin-tools-policy-surface
7. architect/pending/2026-06-26-admin-occ-bridge

## Current Objective
- goal: Resolve the MVP child architects first unless explicitly redirected, while preserving a planned admin API track based on the admin blueprint.
- scope: Sixth MVP slice completed; next MVP slice is the CLI caller that wires config, credentials, runtime dispatch, and concrete tool registrations for local invocation.
- nonGoals: No Spring server wiring in the CLI slice unless its architect says so. No live Nextcloud calls unless the integration slice explicitly opts in. No admin implementation has been started by these records.
- completionCriteria: Every MVP child reaches `resolved/` with assessment, fixes, verification, and summary before post-MVP capability work starts, unless the user changes priority.

## Admin Context
- Admin module name: `nextcloud-mcp-admin`.
- Admin tools module: `tools/nextcloud-mcp-admin-tools`.
- Source blueprint: `blueprint/nextcloud-admin-api-model.md`.
- Shared mechanics: Basic Auth app-password credentials, `OCS-APIRequest: true`, `Accept: application/json`, and shared route construction.
- Boundary: `nextcloud-mcp-client` owns WebDAV files, normal shares/sharees, current user, capabilities, and user-content behavior.
- Boundary: `nextcloud-mcp-admin` owns arbitrary user provisioning, group provisioning, subadmin management, app provisioning, and optional guarded OCC bridge behavior.
- Important overlap: `/ocs/v1.php/cloud/users/{userid}` is self metadata only in the user/content boundary, but arbitrary-user reads/mutations are admin-owned.
- Share warning: admin credentials can call normal share APIs, but `AdminProvisioningClient` is not `FileShareClient`.

## Last Run Summary
- runEndedAt: 2026-06-26T15:54:38-04:00
- workCompleted: Implemented `NextcloudFilesTools`, `NextcloudShareTools`, and `NextcloudUserTools` registration factories. Added fake HTTP-backed tests for descriptor coverage, route selection, scope metadata, and representative handler invocation. Moved `2026-06-26-mvp-files-share-user-tools` to resolved.
- workPartiallyCompleted: Share get, update, send email, and recommended sharees are intentionally deferred because the client layer does not yet expose those methods.
- testsRun: focused compile, focused tool-module reactor tests, full Maven reactor tests.
- testResult: passed.
- verificationSetup: Tests used fake HTTP clients only. No live Nextcloud calls were made.
- commitCreated: no
- commitHash: n/a

## Files Changed By This Run

| File | State | Reason |
|---|---|---|
| architect/resolved/2026-06-26-mvp-files-share-user-tools/ | moved/updated | Sixth MVP child activated and resolved with evidence. |
| tools/nextcloud-mcp-files-tools/pom.xml | updated | Added `nextcloud-mcp-tool-runtime` dependency. |
| tools/nextcloud-mcp-files-tools/src/main/java/org/mcp/nextcloud/tools/files/NextcloudFilesTools.java | added | Files tool descriptors and handlers. |
| tools/nextcloud-mcp-files-tools/src/test/java/org/mcp/nextcloud/tools/files/NextcloudFilesToolsTest.java | added | Descriptor and fake HTTP-backed files handler tests. |
| tools/nextcloud-mcp-share-tools/pom.xml | updated | Added `nextcloud-mcp-tool-runtime` dependency. |
| tools/nextcloud-mcp-share-tools/src/main/java/org/mcp/nextcloud/tools/share/NextcloudShareTools.java | added | Share/sharee tool descriptors, handlers, and deferred registrations. |
| tools/nextcloud-mcp-share-tools/src/test/java/org/mcp/nextcloud/tools/share/NextcloudShareToolsTest.java | added | Descriptor, deferred, and fake HTTP-backed share tests. |
| tools/nextcloud-mcp-user-tools/pom.xml | updated | Added `nextcloud-mcp-tool-runtime` dependency. |
| tools/nextcloud-mcp-user-tools/src/main/java/org/mcp/nextcloud/tools/user/NextcloudUserTools.java | added | User/capabilities/self metadata tool descriptors and handlers. |
| tools/nextcloud-mcp-user-tools/src/test/java/org/mcp/nextcloud/tools/user/NextcloudUserToolsTest.java | added | Descriptor and fake HTTP-backed user tests. |
| lib/nextcloud-mcp-http/src/test/java/org/mcp/nextcloud/http/HttpHelpersTest.java | updated | Replaced unordered form input with insertion-ordered input. |
| architect/ASSIGNMENT.md | updated | Current execution card points to resolution 7. |
| architect/HANDOFF.md | updated | Persistent ledger records resolution 6. |

## Existing Dirty Work Preserved

| Area | State | Handling |
|---|---|---|
| scratch/callout/calling-to-michael.wav | added before this run | Preserved. |
| architect/pending/2026-06-26-nextcloud-admin-api-architecture/ and child admin entries | added before this run | Preserved and referenced only in handoff context. |

## Unfinished Files

| File | State | Remaining Work | Safe Next Action |
|---|---|---|---|
| architect/pending/2026-06-26-mvp-cli-caller/ | pending | Seventh MVP child not started. | Move to active and wire CLI caller through config, client, registry, and runtime dispatcher. |
| architect/pending/2026-06-26-admin-auth-client-foundation/ | pending | First admin child not started. | Activate only if the user redirects to admin API work or MVP sequence is complete. |

## Next Files To Touch

| File | Planned Change | Depends On |
|---|---|---|
| architect/pending/2026-06-26-mvp-cli-caller/ | Move to active and implement CLI caller. | Depends on resolved runtime and concrete tool modules. |
| app or CLI module identified by the CLI architect | Wire local invocation command. | Depends on repository module layout and POMs. |
| tools/nextcloud-mcp-*-tools/ | Register concrete factories into CLI runtime. | Depends on CLI design. |

## Decisions Made
- Decision: Tool modules expose public `NextcloudFilesTools.registrations(NextcloudClient)`, `NextcloudShareTools.registrations(NextcloudClient)`, and `NextcloudUserTools.registrations(NextcloudClient)`.
- Decision: Concrete tool modules depend on `nextcloud-mcp-tool-runtime` because runtime `ToolRegistration` is the registration contract.
- Decision: File tool handlers use `ToolInvocationContext.accountId()` for WebDAV user path selection.
- Decision: Share get, update, send email, and recommended sharees are registered as deferred tools instead of fake implementations.
- Decision: Full Maven tests must remain green after the existing HTTP helper test was made deterministic.

## Blockers
- none

## Risks
- Risk: CLI caller could bypass policy if it directly invokes handlers.
  - Mitigation: Use `ToolDispatcher` and registry wiring rather than handler calls.
- Risk: Deferred share tools may surprise callers.
  - Mitigation: CLI/server layers should present `tool.deferred` clearly and keep descriptors marked with `metadata.deferred=true`.
- Risk: Admin route work could blur into user/content route work.
  - Mitigation: Keep `nextcloud-mcp-client` and `nextcloud-mcp-admin` route ownership separate in every child architect.

## Next Action
Continue with MVP resolution 7 by activating `architect/pending/2026-06-26-mvp-cli-caller`, unless the user explicitly asks to start the admin API track.

## Resume Commands

```powershell
Get-Content -LiteralPath 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp\architect\ASSIGNMENT.md'
Get-Content -LiteralPath 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp\architect\HANDOFF.md'
Get-Content -LiteralPath 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp\architect\pending\2026-06-26-mvp-cli-caller\meta.json' | ConvertFrom-Json | Out-Null
git -C 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp' status --short --branch
.\mvnw.cmd test
```
