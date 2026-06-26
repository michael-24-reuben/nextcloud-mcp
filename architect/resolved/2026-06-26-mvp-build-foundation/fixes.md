# Fixes

## Files Changed

- `pom.xml`

## Build Contract Changes

- Removed the Spring Boot starter parent from the root aggregator.
- Removed root-level Spring Boot and Lombok dependencies.
- Removed root plugin executions that compiled the generated root application scaffold from the aggregator build.
- Added Java 25 compiler defaults, UTF-8 encoding, and managed compiler/test plugin versions.
- Added JUnit BOM dependency management for future test modules.
- Added a `spring-server` profile that imports Spring Boot dependency management only when `app/nextcloud-mcp-server/pom.xml` exists.
- Kept `<modules>` empty because no approved module directories currently exist.
