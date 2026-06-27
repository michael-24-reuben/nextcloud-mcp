# Nextcloud MCP Local Config

`config/` is the current repo-local application configuration and local-data source for the server and live SDK checks.

Keep process and tool policy data in `config/server.yaml`:

- server bind settings
- feature flags that describe enabled tool groups
- admin account selection

Keep user/account data in `config/db/u/usr-*.env`:

- Nextcloud account id
- login username
- base URL
- app password
- display name and email for future provisioning flows
- account scopes and role flags

The `usr-*.env` files are intentionally ignored by git through `*.env`. They are the current one-layer local data store and can later migrate cleanly into SQL rows. The scratch `.env` files can still be useful for local shell work, but runtime code should treat this directory as the official source.

Keep the directory name `config/` for now because it contains both runtime config and the temporary local data backing that config. A future SQL-backed store can replace `config/db/` without renaming the top-level config root.
