# nextcloud-mcp-config

Configuration model, loading, secret resolution, and validation.

## Owns

- Root config model: `NextcloudMcpConfig`.
- Account config: `NextcloudAccountConfig`, `NextcloudAdminConfig`.
- Runtime config sections: `ServerConfig`, `ToolCatalogConfig`, `ToolPolicyConfig`.
- YAML loading through `ConfigLoader` and `YamlConfigLoader`.
- Secret lookup through `SecretResolver` and `EnvironmentSecretResolver`.
- Config validation through `ConfigValidator` and `ConfigValidationError`.

## Rules

- Account `baseUrl` must be absolute.
- Account `accountName` and `appPassword` are required for local env records; YAML config still maps the login name to `username`.
- Admin accounts must be explicitly enabled before use.
- Secrets should stay as references until resolved by the runtime path that needs them.

## Verification

```powershell
.\mvnw.cmd -pl lib/nextcloud-mcp-config -am test
```
