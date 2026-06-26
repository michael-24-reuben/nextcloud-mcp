# Notes

## 2026-06-26

- Current module directories are not present yet, so the root `<modules>` block remains empty.
- The previous handoff mentioned a dirty `pom.xml`, but the current worktree had no `pom.xml` diff before this run.
- The generated root Spring source tree remains in place but is no longer part of the root aggregator build. It should be moved or replaced when `app/nextcloud-mcp-server` is created by the server transport slice.
- The root POM now acts as a neutral Maven parent/aggregator. Spring dependency management is reserved for a future server module profile and is not inherited as the root parent.
