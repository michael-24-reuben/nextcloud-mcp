# MVP Verification Notes

This document captures stable verification data from the latest resolved MVP
architect records and the current local documentation workspace. It is not a
secret store and must not contain app passwords.

## Source Records Reviewed

- `architect/resolved/2026-06-26-mvp-cli-caller/verification.md`
- `architect/resolved/2026-06-26-mvp-cli-caller/summary.md`
- `architect/resolved/2026-06-26-mvp-spring-server-transport/verification.md`
- `architect/resolved/2026-06-26-mvp-spring-server-transport/summary.md`
- `architect/resolved/2026-06-26-mvp-spring-server-transport/fixes.md`
- `architect/pending/2026-06-26-mvp-integration-verification-docs/*`
- `docs/localhost/api/v1/*.json`
- `docs/localhost/mcp/*.json`

## Verified Baseline

The last resolved MVP slices established this baseline:

| Area | Verification | Result |
| --- | --- | --- |
| CLI caller | `.\mvnw.cmd -pl cli/nextcloud-mcp-cli -am test` | Passed |
| Server transport | `.\mvnw.cmd -pl app/nextcloud-mcp-server -am test` | Passed |
| Non-live full reactor | `NC_MCP_SDK_FUNCTIONALITY_TEST_ENABLED=false`, `NC_MCP_SMOKE_TEST_ENABLED=false`, then `.\mvnw.cmd test` | Passed |
| Server launch | `.\mvnw.cmd -pl app/nextcloud-mcp-server spring-boot:run '-Dspring-boot.run.arguments=--server.port=8080'` | Passed |
| Health smoke | `Invoke-WebRequest -Uri 'http://127.0.0.1:8080/health' -UseBasicParsing` | Passed |
| Architect metadata parse | Convert every `architect/**/meta.json` with `ConvertFrom-Json` | Passed |
| Diff whitespace check | `git diff --check -- app/nextcloud-mcp-server architect pom.xml` | Passed with only CRLF warnings |

Plain `.\mvnw.cmd test` with live smoke enabled previously failed after the
server module had already passed because the live Nextcloud host returned HTTP
`502` for:

```text
GET https://alphasunny11-07.tail93ea23.ts.net/ocs/v1.php/cloud/user
```

Treat that as a live-environment caveat unless it reproduces in fake-HTTP tests
or in the non-live reactor.

## Runtime Configuration

The Spring server reads its MCP config from:

```yaml
nextcloud:
  mcp:
    config-path: ${NEXTCLOUD_MCP_CONFIG:config/server.yaml}
    default-account-id: ${NEXTCLOUD_MCP_ACCOUNT:}
    validate-on-startup: ${NEXTCLOUD_MCP_VALIDATE_ON_STARTUP:false}
```

Usable configuration sources:

| Setting | Purpose |
| --- | --- |
| `NEXTCLOUD_MCP_CONFIG` | Path to the server YAML config. Defaults to `config/server.yaml`. |
| `NEXTCLOUD_MCP_ACCOUNT` | Optional default account override. |
| `NEXTCLOUD_MCP_VALIDATE_ON_STARTUP` | When true, fail startup if config validation fails. |
| `-Dnextcloud.mcp.config=...` | CLI/system-property config path used by config path discovery. |

Runtime user account records are stored under:

```text
config/db/u/usr-*.env
```

Known non-secret fields used by local records:

```text
ACCOUNT_KEY
ACCOUNT_NAME
BASE_URL
USERNAME or LOGIN_NAME or LOGIN
DISPLAY_NAME
EMAIL
DEFAULT_ACCOUNT
ADMIN
ENABLED
SCOPES
```

`APP_PASSWORD` or `APP_PASS` may exist in these files but must not be copied into
docs.

## Current Local Capture Snapshot

The stored `docs/localhost` captures show:

| Capture | Stable data |
| --- | --- |
| `docs/localhost/api/v1/health.get-res.json` | Server status `UP`, config loaded, one account, 21 tools. |
| `docs/localhost/api/v1/accounts.get-res.json` | Account `temporary`, base URL `https://alphasunny11-07.tail93ea23.ts.net`, username `tempo`, default enabled non-admin account. |
| `docs/localhost/api/v1/tools.get-res.json` | REST tool descriptor list. |
| `docs/localhost/mcp/tools.get-res.json` | MCP alias tool descriptor list. |

Freshness caveat: the currently running local servers on `127.0.0.1:8080` and
`127.0.0.1:8765` still report these share tools as `deferred=true`:

```text
nextcloud.shares.get
nextcloud.shares.update
nextcloud.shares.send_email
nextcloud.sharees.recommended
```

Current source code has these registrations wired to client methods with
`deferred=false`. Rebuild and restart the server before refreshing
`docs/localhost/**` captures for share descriptor state.

## Server Route Inventory

MVP server routes:

| Method | Path | Purpose |
| --- | --- | --- |
| `GET` | `/health` | Read server readiness and config/tool counts. |
| `GET` | `/api/v1/accounts` | List configured local accounts without secrets. |
| `POST` | `/api/v1/accounts/test` | Probe selected account with the current-user OCS endpoint. |
| `GET` | `/api/v1/tools` | List local tool descriptors. |
| `POST` | `/api/v1/tools/call` | Invoke a tool through the runtime. |
| `GET` | `/mcp/tools` | MCP-compatible alias for tool listing. |
| `POST` | `/mcp/tools/call` | MCP-compatible alias for tool calls. |
| `POST` | `/mcp` | Minimal JSON-RPC endpoint for `initialize`, `tools/list`, `tools/call`, `accounts/list`, `accounts/test`, and `health`. |

Local account management routes:

| Method | Path |
| --- | --- |
| `POST` | `/api/v1/accounts` |
| `GET` | `/api/v1/accounts/{accountId}` |
| `PATCH` | `/api/v1/accounts/{accountId}` |
| `DELETE` | `/api/v1/accounts/{accountId}` |
| `POST` | `/api/v1/accounts/{accountId}/app-password` |
| `POST` | `/api/v1/accounts/{accountId}/enable` |
| `POST` | `/api/v1/accounts/{accountId}/disable` |
| `POST` | `/api/v1/accounts/{accountId}/make-default` |

Admin routes are present in the server controller, but final MVP verification
should keep non-admin verification separate from admin verification:

| Area | Route family |
| --- | --- |
| Admin users | `/api/v1/admin/users/**` |
| Admin groups | `/api/v1/admin/groups/**` |
| Admin apps | `/api/v1/admin/apps/**` |
| OCC command plans | `/api/v1/admin/occ/**` |

## Tool Contract Expectations

Non-admin MVP tool groups:

| Group | Examples | Required scope family |
| --- | --- | --- |
| Files | `nextcloud.files.list`, `nextcloud.files.upload`, `nextcloud.files.download`, `nextcloud.files.mkdir` | `nextcloud.files.*` |
| Shares | `nextcloud.shares.list`, `nextcloud.shares.create`, `nextcloud.sharees.search` | `nextcloud.shares.*` |
| User | `nextcloud.user.me`, `nextcloud.user.metadata`, `nextcloud.user.capabilities` | `nextcloud.user.read` |

Destructive file operations require explicit destructive scopes. Share deletion
is destructive and requires `nextcloud.shares.write`. Admin destructive tools
use admin scope families and must not be treated as file delete operations.

## Final MVP Verification Checklist

Before resolving `2026-06-26-mvp-integration-verification-docs`, prove:

- Maven tests pass with live smoke disabled.
- CLI can list tools.
- CLI can call one read-only tool.
- Server can start with `config/server.yaml`.
- `/health` returns `UP`.
- `/api/v1/tools` and `/mcp/tools` list tools.
- `/api/v1/tools/call` can call one read-only tool.
- Secrets are not echoed by account routes or docs captures.
- Admin routes and admin tools are either separately verified or clearly marked outside non-admin MVP verification.
