# Brief

## Objective

Implement the Spring-free core, HTTP, and configuration foundation required by the Nextcloud user client and MCP runtime.

## Scope

- Core errors, result wrappers, IDs, and masking utilities.
- HTTP adapter contracts around `java.net.http.HttpClient`.
- Basic Auth and OCS header helpers.
- Request/response model objects.
- YAML/config model, secret resolver, and validation.

## Non-Goals

- Nextcloud-specific API clients beyond generic request support.
- Spring properties or server wiring.
- Admin account handling beyond reserving config shape.

## Resolution Position

Resolve this after build foundation and before client/runtime/tool work.
