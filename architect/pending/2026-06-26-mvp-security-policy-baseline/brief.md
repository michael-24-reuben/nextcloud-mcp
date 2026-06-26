# Brief

## Objective

Define and implement the minimum security and policy layer required for MVP tools to be safe to expose through CLI and server transports.

## Scope

- Principal and account context.
- Account access policy.
- Tool access policy.
- Scope names for MVP tools.
- Secret masking.
- Audit event shape.
- Delete-by-default behavior for destructive tools.

## Non-Goals

- Full post-MVP admin security model.
- Persistence-backed audit storage unless required by the server slice.
- OAuth or browser login flow security.

## Resolution Position

Resolve before tools and server transport are considered complete.
