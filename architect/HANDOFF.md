# Assignment Handoff

## Assignment Status
- assignmentStatus: MVP CLI caller resolved; ready for Spring server transport
- lastUpdatedAt: 2026-06-26T23:25:50-04:00
- updatedBy: Codex
- currentBranch: master
- expectedBranch: master
- objectiveId: 2026-06-26-mvp-spring-server-transport
- objectiveTitle: MVP Spring Server Transport
- objectiveStatus: pending

## Current Architect Entries
- primary: architect/pending/2026-06-26-mvp-spring-server-transport
- lastVerifiedCompleted: architect/resolved/2026-06-26-mvp-cli-caller
- pendingFollowUps: architect/pending/2026-06-26-mvp-spring-server-transport, architect/pending/2026-06-26-mvp-integration-verification-docs
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
7. architect/resolved/2026-06-26-mvp-cli-caller
8. architect/pending/2026-06-26-mvp-spring-server-transport
9. architect/pending/2026-06-26-mvp-integration-verification-docs

## Current Objective
- goal: Continue outbound MVP wiring after the CLI caller.
- scope: Implement the Spring server transport using the existing client/tool runtime and current CLI decisions as compatible behavior.
- nonGoals: Do not add admin CLI behavior in the server slice. Do not execute OCC commands. Do not move root Spring scaffold unless the server record explicitly requires it.
- completionCriteria: Server transport and final integration verification/docs records are resolved with tests.

## Admin Context
- Admin module name: `nextcloud-mcp-admin`.
- Admin tools module: `tools/nextcloud-mcp-admin-tools`.
- Shared mechanics: Basic Auth app-password credentials, `OCS-APIRequest: true`, `Accept: application/json`, and shared route construction.
- Boundary: `nextcloud-mcp-client` owns WebDAV files, normal shares/sharees, current user, capabilities, and user-content behavior.
- Boundary: `nextcloud-mcp-admin` owns arbitrary user provisioning, group provisioning, subadmin management, app provisioning, and optional guarded OCC bridge behavior.
- Important overlap: `/ocs/v1.php/cloud/users/{userid}` is self metadata only in the user/content boundary, but arbitrary-user reads/mutations are admin-owned.
- Share warning: admin credentials can call normal share APIs through `NextcloudAdminSharesSupport`, which reuses `nextcloud-mcp-client`; `AdminProvisioningClient` is not `FileShareClient`.
- App warning: app enable/disable methods exist at client level but return `AdminRiskLevel.CRITICAL` and must not be exposed without confirmation policy.

## Last Run Summary
- runEndedAt: 2026-06-26T23:25:50-04:00
- workCompleted: Added the `cli/nextcloud-mcp-cli` module and wired a local CLI around the existing config/client/security/tool runtime.
- workPartiallyCompleted: none for CLI caller.
- testsRun: `.\mvnw.cmd -pl cli/nextcloud-mcp-cli -am test`; `.\mvnw.cmd test`.
- testResult: passed.
- verificationSetup: Fake HTTP tests cover CLI commands and WebDAV user-id routing. Full Maven verification also executed the existing live SDK smoke test against the temp account and cleaned up `/CodexScratch/nextcloud-mcp-smoke.txt`.
- commitCreated: no
- commitHash: n/a

## CLI Behavior Added
- `nextcloud-mcp tools list`
- `nextcloud-mcp call <tool> --arg key=value`
- `nextcloud-mcp accounts test [accountId]`
- `nextcloud-mcp config check`
- Global options: `--config`, `--account`, `--json`, `--arg`, `--args-json`.
- `tools list` does not resolve app-password secrets or perform HTTP.
- `call` and `accounts test` perform current-user OCS probing.
- Tool calls use account-configured scopes and the OCS-returned user id in the invocation account id so file tools build WebDAV paths with the real Nextcloud user id.

## Files Changed By This Run

| File | State | Reason |
|---|---|---|
| pom.xml | updated | Added `cli/nextcloud-mcp-cli` to the Maven reactor. |
| cli/nextcloud-mcp-cli/pom.xml | added | CLI module dependencies. |
| cli/nextcloud-mcp-cli/src/main/java/org/mcp/nextcloud/cli/NextcloudMcpCli.java | added | Java main entrypoint. |
| cli/nextcloud-mcp-cli/src/main/java/org/mcp/nextcloud/cli/NextcloudMcpCliApplication.java | added | CLI parser, config loading, runtime wiring, command execution, JSON output. |
| cli/nextcloud-mcp-cli/src/test/java/org/mcp/nextcloud/cli/NextcloudMcpCliApplicationTest.java | added | Command-level tests with fake HTTP. |
| architect/resolved/2026-06-26-mvp-cli-caller/ | moved/updated | Resolved record and evidence. |
| architect/ASSIGNMENT.md | updated | Next action points to Spring server transport. |
| architect/HANDOFF.md | updated | Persistent ledger records CLI decisions. |

## Existing Dirty Work Preserved

| Area | State | Handling |
|---|---|---|
| scratch/callout/calling-to-michael.wav | added before this run | Preserved. |

## Unfinished Files

| File | State | Remaining Work | Safe Next Action |
|---|---|---|---|
| architect/pending/2026-06-26-mvp-spring-server-transport/ | pending | Server transport behavior not started. | Activate this next. |
| architect/pending/2026-06-26-mvp-integration-verification-docs/ | pending | Integration verification/docs not started. | Resolve after server transport. |

## Next Files To Touch

| File | Planned Change | Depends On |
|---|---|---|
| architect/pending/2026-06-26-mvp-spring-server-transport/ | Move to active and implement server transport. | Depends on resolved CLI and tool runtime behavior. |
| app/nextcloud-mcp-server/ or existing Spring scaffold | Inspect before editing; wire transport only where the server record indicates. | Depends on server transport architect context. |

## Decisions Made
- Decision: The CLI uses the existing runtime directly instead of inventing a separate caller path.
- Decision: Admin CLI remains out of scope.
- Decision: `tools list` is safe without local secret resolution.
- Decision: Current-user probing protects WebDAV path construction from account-id/user-id mismatches.

## Blockers
- none

## Risks
- Risk: CLI packaging is not a native command or shaded distribution yet.
  - Mitigation: The MVP provides a Java main module and tested application entrypoint; packaging can be added later.

## Next Action
Activate `architect/pending/2026-06-26-mvp-spring-server-transport`, inspect the current Spring scaffold/module state, and wire transport using the same runtime behavior as the CLI where possible.

## Resume Commands

```powershell
Get-Content -LiteralPath 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp\architect\ASSIGNMENT.md'
Get-Content -LiteralPath 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp\architect\HANDOFF.md'
Get-Content -LiteralPath 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp\architect\pending\2026-06-26-mvp-spring-server-transport\meta.json' | ConvertFrom-Json | Out-Null
git -C 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp' status --short --branch
.\mvnw.cmd test
```
