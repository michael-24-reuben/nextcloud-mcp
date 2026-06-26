# Brief

## Objective

Implement the framework-neutral MCP tool API and runtime dispatcher shared by CLI and server transports.

## Scope

- Tool descriptor, schema, parameter, invocation, result, error, and content models.
- Tool registry.
- Dispatcher.
- Invocation context and validation.
- Result mapping.
- Audit and policy interceptors.

## Non-Goals

- HTTP transport endpoints.
- CLI commands.
- Tool catalog export beyond what is needed for `tools list`.
- Post-MVP streaming or websocket behavior.

## Resolution Position

Resolve before MVP tools, CLI, and server transport.
