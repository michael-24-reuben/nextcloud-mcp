# Context

## Parent

- `architect/pending/2026-06-26-nextcloud-mcp-core-architecture`

## Intended Modules

- `lib/nextcloud-mcp-core`
- `lib/nextcloud-mcp-http`
- `lib/nextcloud-mcp-config`

## Key Design Rules

- Use `java.net.http.HttpClient` by default.
- Do not bind these modules to Spring.
- Config must work for CLI, server, tests, and future standalone runners.
- OCS requests require `OCS-APIRequest: true` and `Accept: application/json`.

## Verification Baseline

```powershell
.\mvnw.cmd -pl lib/nextcloud-mcp-core,lib/nextcloud-mcp-http,lib/nextcloud-mcp-config test
```
