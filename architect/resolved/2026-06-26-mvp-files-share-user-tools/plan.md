# Plan

1. Implement files tool descriptors and schemas.
2. Implement files tool invocation using the user client.
3. Implement share/sharee descriptors and schemas.
4. Implement share/sharee invocation.
5. Implement user/capabilities descriptors and invocation.
6. Add policy scope declarations for every tool.
7. Add unit tests with fake clients.

## Acceptance Criteria

- Tool names match the blueprint.
- Every tool declares required scopes.
- Destructive tools are policy-gated.
- Tools return MCP result types, not raw client objects.
