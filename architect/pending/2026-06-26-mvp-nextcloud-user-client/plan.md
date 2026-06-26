# Plan

1. Implement client factory and facade.
2. Implement auth header provider and current user resolution.
3. Implement WebDAV path normalization and XML request/response parsing.
4. Implement files client for MVP operations.
5. Implement OCS client and response parser.
6. Implement shares and sharees clients.
7. Implement user/capabilities client.
8. Add fixture-backed unit tests for WebDAV and OCS responses.

## Acceptance Criteria

- Client methods do not expose raw HTTP details to tools.
- WebDAV paths are normalized safely and consistently.
- OCS errors map to typed API exceptions.
- The client can be exercised with fixtures without a live server.
