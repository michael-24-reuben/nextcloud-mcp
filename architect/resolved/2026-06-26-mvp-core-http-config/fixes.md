# Fixes

## Files Changed

- `pom.xml`
- `lib/nextcloud-mcp-core/pom.xml`
- `lib/nextcloud-mcp-http/pom.xml`
- `lib/nextcloud-mcp-config/pom.xml`
- `lib/nextcloud-mcp-core/src/main/java/org/mcp/nextcloud/core/**`
- `lib/nextcloud-mcp-http/src/main/java/org/mcp/nextcloud/http/**`
- `lib/nextcloud-mcp-config/src/main/java/org/mcp/nextcloud/config/**`
- `lib/nextcloud-mcp-http/src/test/java/org/mcp/nextcloud/http/HttpHelpersTest.java`
- `lib/nextcloud-mcp-config/src/test/java/org/mcp/nextcloud/config/ConfigValidatorTest.java`

## Behavioral Changes

- Added shared core exceptions, IDs, result wrappers, precondition checks, and secret masking utilities.
- Added Spring-free HTTP request/response contracts, JDK `HttpClient` adapter, Basic/Bearer auth helpers, OCS JSON default headers, retry policy, and rate-limit policy records.
- Added configuration records, YAML loader contract/implementation, environment secret resolver, and validation for account base URL, username, app password, and disabled admin accounts.
- Moved the root POM back to dependency-management/aggregator responsibility and managed internal module versions centrally.
