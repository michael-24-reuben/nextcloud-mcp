# Context

## Parent

- `architect/pending/2026-06-26-nextcloud-mcp-core-architecture`

## Blueprint Inputs

- `blueprint/project-structure.md` defines the intended Maven module graph.
- The root `pom.xml` currently has packaging `pom` and an empty `<modules>` block.
- `git status` showed `pom.xml` modified before this child was created; preserve and inspect that user change before editing.

## Dependency Rule

The build foundation must make this rule enforceable:

```text
Plain Java libraries do not depend on Spring.
Spring only wires and exposes the tool runtime from the server module.
```

## Verification Baseline

```powershell
git -C 'J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp' status --short --branch
.\mvnw.cmd -version
.\mvnw.cmd test
```
