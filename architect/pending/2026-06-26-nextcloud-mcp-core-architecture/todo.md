# Todo

## Parent Record

- [x] Review `blueprint/project-structure.md`.
- [x] Review `blueprint/nextcloud-api-model.md`.
- [x] Create architect lifecycle folders.
- [x] Create root assignment and handoff files.
- [x] Create parent pending architecture entry.
- [x] Preserve the user boundary: do not create project modules.

## Future Child Architects

- [x] Create build-foundation child architect.
- [x] Resolve build-foundation child architect for the current no-module repository state.
- [x] Create core/http/config child architect.
- [x] Activate core/http/config child architect and record module-creation blocker.
- [x] Create MVP security policy baseline child architect.
- [x] Activate MVP security policy baseline child architect and record module-creation blocker.
- [x] Create user Nextcloud client child architect.
- [x] Create MCP tool API/runtime child architect.
- [x] Create MVP files/share/user tools child architect.
- [x] Create CLI child architect.
- [x] Create Spring server transport child architect.
- [x] Create MVP integration verification/docs child architect.
- [ ] Create post-MVP security hardening child architect if MVP policy work leaves deferred scope.
- [ ] Create post-MVP capability child architects for trash, versions, comments, status, catalog, admin client, admin tools, and admin CLI.
- [ ] Create packaging/config/docs child architect.

## Decisions To Revisit

- [x] Whether root Maven dependencies should be moved from the aggregator into app/server modules once module creation begins.
- [ ] Whether Login Flow V2 belongs in MVP or later.
- [ ] Which XML parser abstraction should be used for WebDAV response mapping.
- [x] Whether security is a separate early module or introduced alongside runtime and split later.
- [ ] Whether the first CLI should call the runtime in-process only or also support remote server calls.
