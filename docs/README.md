# `docs/` Navigation README

This README is a routing guide for agents working inside the `docs/` folder. Use it to choose the correct document or local capture folder before editing API models, route mappings, implementation notes, or generated API documentation.

---

## Purpose

The `docs/` directory is the project’s documentation workspace for the Nextcloud MCP server.

It separates:

- **live/local request evidence** under `localhost/`
- **design and planning documents** under `blueprint/`
- **generated or mapped API route documentation** in `api_documentation.md`

Agents should avoid mixing these categories. Capture files prove what the running server did. Blueprint files define what the project is supposed to support. API documentation records the mapped HTTP routes exposed by the server.

---

## Folder layout

```text
docs/
├─ README.md
│
├─ localhost/
│  ├─ api/
│  │  └─ v1/
│  └─ mcp/
│
├─ blueprint/
│  ├─ nextcloud-admin-api-model.md
│  ├─ nextcloud-api-mapping.md
│  ├─ nextcloud-client-api-model.md
│  └─ project-structure.md
│
├─ api_documentation.md
└─ verification.md
```

---

## `localhost/`

`localhost/` stores local API request examples, response captures, and logs generated from calls against the locally running server.

Use this folder when the objective is to verify actual runtime behavior.

### `docs/localhost/api/v1/`

Stores request and response evidence for REST-style server routes under:

```text
/api/v1/**
```

Use this path for captures involving:

- account registry routes
- account testing routes
- tool listing routes
- tool invocation through `/api/v1/tools/call`
- admin REST routes under `/api/v1/admin/**`
- health/readiness calls if grouped with API smoke tests

Expected content examples:

```text
docs/localhost/api/v1/accounts-list.request.json
docs/localhost/api/v1/accounts-list.response.json
docs/localhost/api/v1/tools-call.files-list.request.json
docs/localhost/api/v1/tools-call.files-list.response.json
docs/localhost/api/v1/admin-users-list.response.json
docs/localhost/api/v1/smoke-test.log
```

Agent rule:

```text
If the task asks "what did the server return?", "is this endpoint working?", or "capture this route", use docs/localhost/api/v1/.
```

### `docs/localhost/mcp/`

Stores request and response evidence for MCP-compatible routes under:

```text
/mcp
/mcp/tools
/mcp/tools/call
/mcp/ws
```

Use this path for captures involving:

- JSON-RPC calls sent to `/mcp`
- MCP-compatible tool listings
- MCP-compatible tool calls
- WebSocket MCP messages
- MCP transport compatibility checks

Expected content examples:

```text
docs/localhost/mcp/jsonrpc.tools-list.request.json
docs/localhost/mcp/jsonrpc.tools-list.response.json
docs/localhost/mcp/tools-call.files-list.request.json
docs/localhost/mcp/tools-call.files-list.response.json
docs/localhost/mcp/websocket-session.log
```

Agent rule:

```text
If the task asks about MCP compatibility, JSON-RPC shape, `/mcp`, `/mcp/tools`, `/mcp/tools/call`, or `/mcp/ws`, use docs/localhost/mcp/.
```

---

## `blueprint/`

`blueprint/` contains the design source files. These documents describe the intended architecture, API support model, route mapping, and module structure.

Use this folder when the objective is to understand or update design intent.

### `docs/blueprint/project-structure.md`

Defines the target project layout and module responsibilities.

Use it for:

- root project layout
- Maven module organization
- package naming
- library/module purpose
- tool module boundaries
- CLI layout
- Spring Boot server layout
- dependency direction
- minimum starting structure

Agent rule:

```text
If the task asks where code belongs, which module owns a capability, or how the project should be structured, read project-structure.md first.
```

### `docs/blueprint/nextcloud-client-api-model.md`

Defines the non-admin Nextcloud API model for a normal user or automation account.

Use it for:

- app-password authentication
- WebDAV file operations
- OCS headers
- Login Flow v2
- user file APIs
- bulk upload
- search
- trashbin
- versions
- comments
- shares
- sharees
- current user metadata
- user capabilities
- user status
- user preferences
- public share WebDAV
- non-admin client implementation order

Agent rule:

```text
If the task concerns normal account behavior, file automation, shares, WebDAV, OCS user APIs, or non-admin tool capability design, use nextcloud-client-api-model.md.
```

### `docs/blueprint/nextcloud-admin-api-model.md`

Defines the admin API model and separates normal user capabilities from admin-only governance.

Use it for:

- admin credential model
- OCS Provisioning API
- user provisioning
- group provisioning
- subadmin management
- app listing/enabling/disabling
- admin share behavior
- OCC bridge boundaries
- admin wrapper layout
- admin operation risk levels
- minimum admin implementation order

Agent rule:

```text
If the task concerns users, groups, subadmins, quotas, app management, OCC bridge, or admin operation risk, use nextcloud-admin-api-model.md.
```

### `docs/blueprint/nextcloud-api-mapping.md`

Maps implemented or inspected server code to API paths, tools, scopes, auth headers, and client classes.

Use it for:

- endpoint-to-tool mapping
- required scopes
- destructive operation flags
- security policy behavior
- Basic Auth / OCS header handling
- WebDAV path mapping
- admin provisioning route mapping
- client class lookup
- deciding whether implementation matches the blueprint

Agent rule:

```text
If the task asks "what endpoint maps to what tool?", "what scope does this require?", "what class handles this?", or "does implementation match the API model?", use nextcloud-api-mapping.md.
```

---

## `api_documentation.md`

`docs/api_documentation.md` documents mapped HTTP routes exposed by the server.

Use it for:

- REST controller route inventory
- `/api/v1/accounts`
- `/api/v1/tools`
- `/api/v1/tools/call`
- `/api/v1/admin/**`
- `/mcp`
- `/mcp/tools`
- `/mcp/tools/call`
- request body shapes
- path params
- query params
- response placeholders
- generated route documentation cleanup

Agent rule:

```text
If the task asks for currently documented server endpoints, route paths, HTTP methods, request bodies, or controller docs, use api_documentation.md.
```

Important distinction:

```text
api_documentation.md describes exposed server routes.
blueprint/*.md describes intended API/client architecture.
localhost/** stores observed runtime call evidence.
verification.md stores durable MVP verification commands, caveats, and route facts.
```

---

## `verification.md`

`docs/verification.md` records durable verification data from resolved architect
entries and local runtime captures.

Use it for:

- final MVP verification commands
- CLI and server smoke-test baselines
- local server run/config notes
- known live-environment caveats
- current route inventory needed for verification
- reminders about stale runtime captures

Agent rule:

```text
If the task asks about final MVP verification, what commands passed, what caveats remain, or what route facts must be preserved from resolved architect records, use verification.md.
```

---

## Objective routing table

| Objective | Go to | Reason |
| --- | --- | --- |
| Find where a module/class should live | `blueprint/project-structure.md` | Defines project layout, module responsibilities, and dependency direction. |
| Design a normal user Nextcloud client feature | `blueprint/nextcloud-client-api-model.md` | Defines user-authenticated WebDAV and OCS capabilities. |
| Design an admin Nextcloud feature | `blueprint/nextcloud-admin-api-model.md` | Defines provisioning, group, app, subadmin, and OCC boundaries. |
| Check tool-to-endpoint mapping | `blueprint/nextcloud-api-mapping.md` | Maps tools, paths, methods, scopes, destructive flags, and client classes. |
| Check actual REST route documentation | `api_documentation.md` | Lists controller routes, methods, params, and request bodies. |
| Review final MVP verification data | `verification.md` | Captures resolved CLI/server verification, local config rules, and remaining caveats. |
| Store or inspect `/api/v1` call logs | `localhost/api/v1/` | Runtime request/response evidence for REST API routes. |
| Store or inspect `/mcp` call logs | `localhost/mcp/` | Runtime request/response evidence for MCP and JSON-RPC compatibility. |
| Compare intended route support to generated docs | `blueprint/nextcloud-api-mapping.md` + `api_documentation.md` | Mapping explains semantics; generated docs expose server routes. |
| Verify whether a route actually works | `localhost/**` + `api_documentation.md` | Docs define the route; local captures prove behavior. |

---

## Agent workflow

### 1. For design tasks

```text
Read blueprint first.
Do not infer implementation status from blueprint alone.
```

Recommended order:

```text
project-structure.md
→ nextcloud-client-api-model.md or nextcloud-admin-api-model.md
→ nextcloud-api-mapping.md
```

### 2. For implementation-mapping tasks

```text
Read nextcloud-api-mapping.md first.
Then compare against api_documentation.md.
```

Use this when validating whether controllers, tools, scopes, and downstream Nextcloud APIs are aligned.

### 3. For endpoint documentation tasks

```text
Read api_documentation.md first.
Then use blueprint/nextcloud-api-mapping.md to improve descriptions and missing semantics.
```

Use this when cleaning generated API docs or filling empty descriptions.

### 4. For smoke-test or runtime verification tasks

```text
Use localhost/api/v1/ for REST routes.
Use localhost/mcp/ for MCP-compatible routes.
```

Do not store local request/response captures in `blueprint/`.

---

## File authority levels

| Source | Authority | Notes |
| --- | --- | --- |
| `localhost/**` | Runtime evidence | Most useful for proving actual local behavior. May become stale after code changes. |
| `verification.md` | Durable verification record | Best place for commands, caveats, and final MVP verification notes pulled from architect records. |
| `api_documentation.md` | Exposed server route documentation | Useful for controller route inventory. May need cleanup if generated output is sparse. |
| `blueprint/nextcloud-api-mapping.md` | Implementation mapping reference | Best bridge between code, tools, security, and Nextcloud API paths. |
| `blueprint/nextcloud-client-api-model.md` | User API design reference | Intended model for non-admin Nextcloud accounts. |
| `blueprint/nextcloud-admin-api-model.md` | Admin API design reference | Intended model for admin/provisioning/OCC capabilities. |
| `blueprint/project-structure.md` | Structural design reference | Intended project/module layout. |

---

## Naming guidance for local captures

Use predictable names:

```text
{route-area}.{operation}.{status}.request.json
{route-area}.{operation}.{status}.response.json
{route-area}.{operation}.log
```

Examples:

```text
accounts.list.200.response.json
tools.call.files-list.200.request.json
tools.call.files-list.200.response.json
admin.users.create.201.request.json
admin.users.create.201.response.json
mcp.jsonrpc.tools-call.200.request.json
mcp.jsonrpc.tools-call.200.response.json
```

For failed calls, include the status code:

```text
tools.call.files-delete.403.response.json
admin.apps.disable.409.response.json
mcp.jsonrpc.invalid-tool.400.response.json
```

---

## Do not mix these concerns

Do not put runtime logs in:

```text
docs/blueprint/
```

Do not put design-only notes in:

```text
docs/localhost/
```

Do not treat generated route docs as complete API semantics without checking:

```text
docs/blueprint/nextcloud-api-mapping.md
```

Do not treat blueprint intent as proof that the route exists without checking:

```text
docs/api_documentation.md
```

or local captures under:

```text
docs/localhost/
```

---

## Fast lookup

```text
Need structure?                      blueprint/project-structure.md
Need normal user API model?          blueprint/nextcloud-client-api-model.md
Need admin API model?                blueprint/nextcloud-admin-api-model.md
Need endpoint/tool/security mapping? blueprint/nextcloud-api-mapping.md
Need generated server route docs?    api_documentation.md
Need MVP verification notes?         verification.md
Need REST runtime logs?              localhost/api/v1/
Need MCP runtime logs?               localhost/mcp/
```
