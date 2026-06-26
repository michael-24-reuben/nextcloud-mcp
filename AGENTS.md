# AGENTS.md

Guidance for Codex and other agents working in this repository.

## Project Shape

- This is a Java 25 / Maven multi-module project for a Nextcloud MCP capability server.
- Keep plain Java capability logic out of Spring. Spring belongs in the future server module for wiring and transport only.
- Root `pom.xml` should remain a parent/aggregator and dependency-management file, not an application module.
- Existing module families:
  - `lib/nextcloud-mcp-core`
  - `lib/nextcloud-mcp-http`
  - `lib/nextcloud-mcp-config`
  - `lib/nextcloud-mcp-client`
  - `lib/nextcloud-mcp-security`
  - `lib/nextcloud-mcp-tool-*`
  - `tools/nextcloud-mcp-*-tools`
- Read the active architect records before continuing implementation:
  - `architect/ASSIGNMENT.md`
  - `architect/HANDOFF.md`
  - the next pending/active architect entry in resolution order

## Local Scratch Configuration

- `.env` is a local scratch file and is ignored by git.
- Do not commit `.env` or print app passwords/secrets in logs, summaries, or architect files.
- Use `.env` only to populate local config/tests or manual smoke requests.
- The main non-admin account is the first live verification target. Admin modules and admin tests must remain disabled until explicitly requested.

## Verified Nextcloud Auth Facts

- For normal WebDAV and OCS API calls, use a Nextcloud app password, not the account password.
- AppAPI daemon registration is not required for normal WebDAV uploads or OCS current-user checks.
- The Nextcloud base URL is the host root:
  - `https://alphasunny11-07.tail93ea23.ts.net`
- Do not include the profile path (`/u/temporary`) in API base URLs.
- The profile URL `/u/temporary` identifies the user profile, not the WebDAV or OCS base path.
- Live OCS check confirmed:
  - user id: `temporary`
  - display name: `tempo`
- Auth with username `temporary` succeeds against:
  - `GET /ocs/v1.php/cloud/user`
- Auth with username `tempo` fails. Treat `tempo` as display name only.

## WebDAV Request Rules

- Auth header:
  - `Authorization: Basic base64(username:appPassword)`
  - username must be the Nextcloud user id, currently `temporary`
- User files base:
  - `/remote.php/dav/files/{userId}/`
  - for the verified account, use `/remote.php/dav/files/temporary/`
- Do not use the display name in the WebDAV path. `/remote.php/dav/files/tempo/` returned `404`.
- Use URL-encoding per path segment, not on the full path string.
- For `MKCOL`, include a trailing slash for collection paths:
  - good: `/remote.php/dav/files/temporary/CodexScratch/`
  - this avoided request/path normalization issues in live testing.
- For file `PUT`, use the full encoded file path without a trailing slash.
- A successful live smoke test uploaded and verified:
  - `/CodexScratch/nextcloud-mcp-smoke-20260626-104514.txt`
  - `MKCOL`: `201`
  - `PUT`: `201`
  - `PROPFIND`: `207`
  - `GET`: `200`

## OCS Request Rules

- Include OCS headers:
  - `OCS-APIRequest: true`
  - `Accept: application/json`
- Current user endpoint:
  - `GET /ocs/v1.php/cloud/user`
- Use OCS current-user resolution before constructing user-specific WebDAV paths when the configured username may be ambiguous.

## Implementation Habits

- Prefer fixture-backed unit tests before live Nextcloud calls.
- Live API endpoint probes are allowed on the temp user account (`temporary`) because it exists for testing.
- Live tests should remain non-destructive first and use the non-admin app-password account unless a later architect explicitly activates destructive/admin coverage.
- Do not use admin scopes or admin accounts until the admin architect/module is active.
- When creating HTTP clients, separate:
  - configured account id
  - login/user id returned by OCS
  - display name
  - API base URL
- Account id is an internal config key. It is not necessarily the login id.
- Display name is not a login id and must not be used in WebDAV paths.

## Verification Commands

Use focused verification while developing:

```powershell
.\mvnw.cmd -pl lib/nextcloud-mcp-client test
```

Use full verification before closing a slice:

```powershell
.\mvnw.cmd test
```

Validate architect metadata after moving or resolving records:

```powershell
Get-ChildItem -Recurse -Filter meta.json -LiteralPath .\architect |
  ForEach-Object { Get-Content -LiteralPath $_.FullName | ConvertFrom-Json | Out-Null }
```
