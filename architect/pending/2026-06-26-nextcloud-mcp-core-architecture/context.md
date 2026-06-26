# Context

## Source Files Reviewed

- `blueprint/project-structure.md`
- `blueprint/nextcloud-api-model.md`

## Current Repo State Observed

- Branch: `master`
- Root project path: `J:\Users\jbeas\Repositories\Dev.java-2026\artifacts\nextcloud-mcp`
- Root `pom.xml` has packaging `pom`.
- Root `pom.xml` currently lists the user-created `lib/` and `tools/` modules.
- Existing generated Spring Boot scaffold is present under root `src/`.
- User-created modules exist; MVP slices should inspect the actual POMs before editing.

## Blueprint Architecture

The project is intended to become a Java/Maven multi-module project with these broad areas:

- `lib/` for plain Java libraries:
  - core
  - config
  - http
  - user Nextcloud client
  - admin Nextcloud client
  - tool API
  - tool runtime
  - tool catalog
  - security
- `tools/` for MCP capability modules:
  - files
  - shares
  - users
  - comments
  - trash
  - versions
  - status
  - admin
- `cli/` for general and admin CLIs.
- `app/` for the Spring Boot MCP server.
- `docs/`, `config/`, `docker/`, `scripts/`, and `test-fixtures/` for documentation, examples, packaging, support scripts, and test data.

## Nextcloud API Model

The user automation path should start with app-password Basic Auth:

```text
username = Nextcloud user ID or login name
password = app password
```

Primary API families:

- WebDAV files under `/remote.php/dav/files/{user}/`
- OCS sharing under `/ocs/v2.php/apps/files_sharing/api/v1`
- OCS sharees under `/ocs/v1.php/apps/files_sharing/api/v1`
- OCS current user/capabilities under `/ocs/v1.php/cloud/...`
- Optional later WebDAV APIs for trashbin, versions, and comments
- Optional later OCS APIs for user status and preferences
- Admin APIs only through a separate opt-in admin path

OCS requests should include:

```http
OCS-APIRequest: true
Accept: application/json
```

## MVP Interpretation

The MVP should be:

```text
Auth + current user resolution
Files WebDAV
Shares OCS
Sharee discovery
User/capabilities OCS
Config loading and validation
MCP tool API/runtime
Files/share/user tools
General CLI
Spring server transport
```

Post-MVP:

```text
Search refinements and live search verification
Trashbin
Versions
Comments
Status
Admin client/tools/CLI
Tool catalog export
Docker/release packaging
```

## Boundaries For Future Agents

- Do not let Spring dependencies enter core, http, config, client, tool-api, or tool-runtime modules.
- Do not enable admin tools by default.
- Do not implement destructive file/share/admin tools without policy checks and verification coverage.
- Do not rely on chat history for project ordering; use this parent architect and the blueprint files.
- If the user creates modules manually, child architects should inspect the actual layout before editing POMs or source.
