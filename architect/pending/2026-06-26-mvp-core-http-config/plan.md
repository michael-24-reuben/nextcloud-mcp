# Plan

1. Add core domain primitives and exceptions.
2. Add HTTP method, request, response, header, auth, retry, and rate-limit abstractions.
3. Implement default JDK HTTP adapter.
4. Add config records for server, accounts, tools, scopes, WebDAV behavior, and security defaults.
5. Add environment secret resolution.
6. Add config validation errors and tests.

## Acceptance Criteria

- Client and runtime slices can depend on these modules without Spring.
- Secrets are never rendered unmasked in normal config errors or logs.
- OCS and Basic Auth helper behavior has unit coverage.
- Config validation catches missing base URL, username, secret, and invalid tool/scope declarations.
