# Brief

## Objective

Create the Maven and repository foundation required for the MVP module graph without implementing Nextcloud API behavior.

## Scope

- Root parent POM strategy.
- Java 25 baseline.
- Maven module naming and dependency-management rules.
- Initial module list only for user-approved or already-created modules.
- Test baseline for future slices.

## Non-Goals

- Implementing Java classes.
- Creating modules before the user creates or approves them.
- Adding Spring-specific dependencies to lower-level libraries.
- Adding post-MVP modules such as admin, trash, versions, comments, status, or catalog.

## Resolution Position

Resolve this child before every other MVP child because all later slices depend on a stable module and build contract.
