# Plan

1. Define tool API model.
2. Define JSON-compatible schema representation.
3. Implement registry and duplicate-name checks.
4. Implement dispatcher and invocation validation.
5. Implement policy and audit interceptor chain.
6. Implement result/error mapping.
7. Add runtime unit tests with fake tools.

## Acceptance Criteria

- Tools can be listed and invoked without Spring.
- Invalid arguments fail before client calls.
- Policy checks run before destructive operations.
- CLI and server slices can reuse the same runtime.
