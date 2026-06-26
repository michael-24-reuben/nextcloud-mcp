# Plan

1. Inspect the actual module directories created by the user.
2. Reconcile root `pom.xml` with the blueprint and current scaffold.
3. Keep aggregator-level dependencies minimal and move Spring dependencies toward the server module when that module exists.
4. Define Maven properties for Java 25, encoding, and test plugin behavior.
5. Add only approved/existing MVP modules to `<modules>`.
6. Establish a minimum Maven verification command for later child architects.

## Acceptance Criteria

- The root build can validate the approved module graph.
- Lower-level modules are not coupled to Spring Boot.
- Future child architects can add implementation without first redesigning the build.
- Existing user changes to `pom.xml` are understood before any edits are made.
