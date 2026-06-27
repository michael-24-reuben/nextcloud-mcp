# Architect Assignment

## Assignment Status
- assignmentStatus: MVP CLI caller resolved; continue outbound MVP slices
- lastUpdatedAt: 2026-06-26T23:25:50-04:00
- updatedBy: Codex
- currentBranch: master
- expectedBranch: master
- objectiveId: 2026-06-26-mvp-spring-server-transport
- objectiveTitle: MVP Spring Server Transport
- objectiveStatus: pending

## Current Architect Entries
- primary: architect/pending/2026-06-26-mvp-spring-server-transport
- recentlyVerifiedResolved: architect/resolved/2026-06-26-mvp-cli-caller, architect/resolved/2026-06-26-nextcloud-admin-api-architecture, architect/resolved/2026-06-26-admin-tools-policy-surface, architect/resolved/2026-06-26-admin-occ-bridge
- pendingFollowUps: 2026-06-26-mvp-spring-server-transport, 2026-06-26-mvp-integration-verification-docs
- blockedBy: none
- shouldNotTouch: Preserve unrelated user changes and do not move the generated root Spring scaffold unless the server-module slice explicitly handles it.

## Current Objective
- goal: Continue outbound MVP wiring after completing the CLI caller.
- completedSlices: MVP build foundation, core HTTP/config, security policy baseline, user client, tool runtime, files/share/user tools, admin API architecture, and MVP CLI caller.
- nextSlice: Activate and implement `architect/pending/2026-06-26-mvp-spring-server-transport`.
- completionCriteria: Spring server transport and integration verification/docs slices reach `resolved/` with tests before final MVP handoff.

## Last Run Summary
- runEndedAt: 2026-06-26T23:25:50-04:00
- workCompleted: Added `cli/nextcloud-mcp-cli` with `tools list`, `call`, `accounts test`, `config check`, JSON output, account-scope policy context, and OCS user-id resolution before tool calls.
- testsRun: focused CLI reactor and full Maven reactor.
- testResult: passed.
- verificationNote: `.\mvnw.cmd -pl cli/nextcloud-mcp-cli -am test` and `.\mvnw.cmd test` passed. The full run also executed the existing live SDK smoke test and cleaned up the temp file.
- commitCreated: no

## Active Files

| File | State | Reason |
|---|---|---|
| architect/resolved/2026-06-26-mvp-cli-caller/ | resolved | CLI caller slice completed. |
| cli/nextcloud-mcp-cli/ | added | New MVP CLI module. |
| architect/pending/2026-06-26-mvp-spring-server-transport/ | pending | Next MVP outbound slice. |

## Files Changed By This Run

| File | State | Reason |
|---|---|---|
| pom.xml | updated | Added `cli/nextcloud-mcp-cli` to the reactor. |
| cli/nextcloud-mcp-cli/ | added | CLI implementation and tests. |
| architect/resolved/2026-06-26-mvp-cli-caller/ | moved/updated | Resolved CLI architect record. |
| architect/ASSIGNMENT.md | updated | Points to Spring server transport next. |
| architect/HANDOFF.md | updated | Records CLI completion and next action. |

## Unfinished Files

| File | State | Remaining Work |
|---|---|---|
| architect/pending/2026-06-26-mvp-spring-server-transport/ | pending | Server transport behavior not started. |
| architect/pending/2026-06-26-mvp-integration-verification-docs/ | pending | Integration verification/docs not started. |

## Decisions Made
- Decision: The CLI uses a small in-module parser instead of adding a CLI framework dependency.
- Decision: The MVP CLI exposes files, shares, and user tools only; admin CLI is out of scope.
- Decision: `tools list` is descriptor-only and does not resolve app-password secrets.
- Decision: `call` resolves the current OCS user id before invoking tools so WebDAV paths use the real user id.

## Blockers
- none

## Risks
- Risk: CLI packaging is only a Java main module; installer/shell wrapper packaging is not included in this slice.

## Next Action
Activate `architect/pending/2026-06-26-mvp-spring-server-transport` and wire the Spring server transport without changing the CLI behavior unless the server slice requires shared extraction.

## Verification Baseline

```powershell
git -C 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp' status --short --branch
Get-ChildItem -Recurse -Filter meta.json -LiteralPath 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp\architect' | ForEach-Object { Get-Content -LiteralPath $_.FullName | ConvertFrom-Json | Out-Null }
git -C 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp' diff --check -- architect cli pom.xml
.\mvnw.cmd test
```
