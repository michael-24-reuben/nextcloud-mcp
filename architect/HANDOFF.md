# Assignment Handoff

## Assignment Status
- assignmentStatus: first MVP child resolved; next two active-blocked
- lastUpdatedAt: 2026-06-26T04:29:45.2912599-04:00
- updatedBy: Codex
- currentBranch: master
- expectedBranch: master
- objectiveId: 2026-06-26-nextcloud-mcp-core-architecture
- objectiveTitle: Nextcloud MCP Core Architecture
- objectiveStatus: pending

## Current Architect Entries
- primary: architect/pending/2026-06-26-nextcloud-mcp-core-architecture
- lastVerifiedCompleted: architect/resolved/2026-06-26-mvp-build-foundation
- pendingFollowUps: see MVP resolution order below
- related: blueprint/project-structure.md, blueprint/nextcloud-api-model.md
- blockedBy: missing user-created or user-approved module directories for `2026-06-26-mvp-core-http-config` and `2026-06-26-mvp-security-policy-baseline`
- shouldNotTouch: Do not create project modules, Maven child POMs, Java packages, or source implementation files until the user creates the modules or explicitly approves that work.

## MVP Resolution Order

1. architect/resolved/2026-06-26-mvp-build-foundation
2. architect/active/2026-06-26-mvp-core-http-config (blocked)
3. architect/active/2026-06-26-mvp-security-policy-baseline (blocked)
4. architect/pending/2026-06-26-mvp-nextcloud-user-client
5. architect/pending/2026-06-26-mvp-mcp-tool-runtime
6. architect/pending/2026-06-26-mvp-files-share-user-tools
7. architect/pending/2026-06-26-mvp-cli-caller
8. architect/pending/2026-06-26-mvp-spring-server-transport
9. architect/pending/2026-06-26-mvp-integration-verification-docs

## Current Objective
- goal: Resolve the MVP child architects first, then create/resolve post-MVP entries later.
- scope: Resolve the first three MVP child architects as far as the current repository boundary allows.
- nonGoals: No Maven module creation, no Java implementation inside missing modules, no Spring wiring, no live Nextcloud calls.
- completionCriteria: Every MVP child reaches `resolved/` with assessment, fixes, verification, and summary before post-MVP capability work starts.

## Last Run Summary
- runEndedAt: 2026-06-26T04:29:45.2912599-04:00
- workCompleted: Resolved the build-foundation child by converting the root POM into a Spring-free Java 25 Maven parent/aggregator; activated the next two child architects and recorded blockers.
- workPartiallyCompleted: Core/http/config and security policy baseline are started but blocked because required module directories do not exist and module creation remains outside the approved boundary.
- testsRun: `.\mvnw.cmd -version`; `.\mvnw.cmd test`; XML parse for `pom.xml`; all architect `meta.json` validation.
- testResult: passed.
- verificationSetup: Validate all architect `meta.json` files with `ConvertFrom-Json`, run `.\mvnw.cmd test`, inspect git status.
- commitCreated: no
- commitHash: n/a

## Files Changed By This Run

| File | State | Reason |
|---|---|---|
| pom.xml | updated | Root Maven project is now a Spring-free Java 25 aggregator/parent with plugin and dependency management. |
| architect/resolved/2026-06-26-mvp-build-foundation/ | moved/resolved | First MVP child completed for the current no-module state. |
| architect/active/2026-06-26-mvp-core-http-config/ | moved/blocked | Second MVP child started and blocked on missing modules. |
| architect/active/2026-06-26-mvp-security-policy-baseline/ | moved/blocked | Third MVP child started and blocked on missing security module/core primitives. |
| architect/ASSIGNMENT.md | updated | Current execution card now points at module-boundary blocker. |
| architect/HANDOFF.md | updated | Persistent ledger records first-three architect progress. |
| architect/pending/2026-06-26-nextcloud-mcp-core-architecture/ | updated | Parent now records the first-three progress. |
| architect/pending/2026-06-26-mvp-nextcloud-user-client/ | created | Fourth MVP child. |
| architect/pending/2026-06-26-mvp-mcp-tool-runtime/ | created | Fifth MVP child. |
| architect/pending/2026-06-26-mvp-files-share-user-tools/ | created | Sixth MVP child. |
| architect/pending/2026-06-26-mvp-cli-caller/ | created | Seventh MVP child. |
| architect/pending/2026-06-26-mvp-spring-server-transport/ | created | Eighth MVP child. |
| architect/pending/2026-06-26-mvp-integration-verification-docs/ | created | Ninth MVP child. |

## Existing Dirty Work Preserved

| Area | State | Handling |
|---|---|---|
| scratch/ | untracked before this run | Left untouched except playing `scratch/callout/calling-to-michael.wav` at the blocker. |
| blueprint/ | existing source of architecture truth | Read only. |

## Unfinished Files

| File | State | Remaining Work | Safe Next Action |
|---|---|---|---|
| architect/active/2026-06-26-mvp-core-http-config/ | blocked | Missing `lib/nextcloud-mcp-core`, `lib/nextcloud-mcp-http`, and `lib/nextcloud-mcp-config`. | Create the modules or approve Codex to create them, then mark unblocked and implement. |
| architect/active/2026-06-26-mvp-security-policy-baseline/ | blocked | Missing `lib/nextcloud-mcp-security` and core primitives. | Create the module or approve Codex to create it after core exists. |

## Next Files To Touch

| File | Planned Change | Depends On |
|---|---|---|
| architect/active/2026-06-26-mvp-core-http-config/ | Unblock and implement core/http/config. | Requires module directories/POMs. |
| architect/active/2026-06-26-mvp-security-policy-baseline/ | Unblock and implement security policy. | Requires security module and core primitives. |
| pom.xml | Add module paths when modules exist. | Requires user-created or approved module directories. |

## Decisions Made
- Decision: The MVP is represented by nine child architects with explicit `resolutionOrder` fields.
- Decision: Security policy baseline is a separate MVP child before runtime/tools/server are resolved.
- Decision: Final integration verification/docs is the last MVP child.
- Decision: Admin, trash, versions, comments, status, catalog, Docker/release packaging, and admin CLI remain post-MVP unless the user reprioritizes them.
- Decision: The root POM must be framework-neutral; Spring Boot dependency management is isolated behind a future `spring-server` profile that activates only when `app/nextcloud-mcp-server/pom.xml` exists.
- Decision: The root `<modules>` list remains empty until module directories exist or Codex is explicitly approved to create them.

## Blockers
- Blocker: Core/http/config module directories are missing.
  - Impact: Cannot implement `2026-06-26-mvp-core-http-config` without creating project modules.
  - Required resolution: User creates `lib/nextcloud-mcp-core`, `lib/nextcloud-mcp-http`, and `lib/nextcloud-mcp-config`, or explicitly approves Codex to create them.
  - Recorded in: `architect/active/2026-06-26-mvp-core-http-config/blockers.md`
- Blocker: Security module directory is missing.
  - Impact: Cannot implement `2026-06-26-mvp-security-policy-baseline` without creating a project module.
  - Required resolution: User creates `lib/nextcloud-mcp-security`, or explicitly approves Codex to create it after core exists.
  - Recorded in: `architect/active/2026-06-26-mvp-security-policy-baseline/blockers.md`

## Risks
- Risk: The dirty `pom.xml` may already contain user module choices.
  - Mitigation: Current run found no pre-existing `pom.xml` diff; root POM was intentionally changed by this run.
- Risk: Some verification commands in child contexts reference modules that may not exist yet.
  - Mitigation: Treat them as intended baselines once the user-created modules exist.
- Risk: The generated root Spring source tree remains present but is no longer compiled by the root `pom` project.
  - Mitigation: Move or replace it when `app/nextcloud-mcp-server` is created.

## Next Action
Create the missing module directories/POMs or explicitly approve Codex to create them. Then update `architect/active/2026-06-26-mvp-core-http-config` from `blocked` to `active`, implement the core/http/config baseline, and add the approved module paths to the root `<modules>` list.

## Resume Commands

```powershell
Get-Content -LiteralPath 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp\architect\ASSIGNMENT.md'
Get-Content -LiteralPath 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp\architect\HANDOFF.md'
Get-Content -LiteralPath 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp\architect\active\2026-06-26-mvp-core-http-config\meta.json' | ConvertFrom-Json | Out-Null
git -C 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp' status --short --branch
.\mvnw.cmd test
```
