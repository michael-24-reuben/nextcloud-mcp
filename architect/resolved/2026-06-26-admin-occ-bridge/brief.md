# Brief

## Objective

Design an optional, guarded OCC bridge for admin-only server tasks that are not covered well by HTTP APIs.

## Scope

- Maintenance mode.
- Files scan.
- Config get/set.
- Background jobs.
- Admin recovery helpers.
- Docker/AIO command boundary.

## Non-Goals

- General-purpose shell execution.
- Default enablement.
- Replacing public HTTP admin APIs.
