# Context

## Parent

- `architect/pending/2026-06-26-nextcloud-mcp-core-architecture`

## Intended Areas

- `docs/`
- `config/examples/`
- `config/schemas/`
- `test-fixtures/`

## Minimum MVP Proof

```text
Build passes.
Unit/fixture tests pass.
CLI can list tools.
CLI can call a read-only tool.
Server can start.
Server can list tools.
Server can call a read-only tool.
Secrets are masked.
Admin tools are absent or disabled.
```

## Verification Baseline

```powershell
.\mvnw.cmd test
```
