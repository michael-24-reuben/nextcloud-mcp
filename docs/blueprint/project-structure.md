# Root project layout

```text
nextcloud-mcp/
├─ pom.xml
├─ README.md
├─ .gitignore
├─ LICENSE
│
├─ docs/
│  ├─ architecture.md
│  ├─ api-capabilities.md
│  ├─ configuration.md
│  ├─ mcp-tool-contract.md
│  ├─ security-model.md
│  └─ nextcloud-api-map.md
│
├─ config/
│  ├─ examples/
│  │  ├─ nextcloud-mcp.yaml
│  │  ├─ nextcloud-accounts.yaml
│  │  ├─ nextcloud-tools.yaml
│  │  └─ nextcloud-admin.yaml
│  │
│  └─ schemas/
│     ├─ nextcloud-mcp.schema.json
│     ├─ nextcloud-account.schema.json
│     ├─ nextcloud-tool.schema.json
│     └─ nextcloud-policy.schema.json
│
├─ lib/
│  ├─ nextcloud-mcp-core/
│  ├─ nextcloud-mcp-config/
│  ├─ nextcloud-mcp-http/
│  ├─ nextcloud-mcp-client/
│  ├─ nextcloud-mcp-admin/
│  ├─ nextcloud-mcp-tool-api/
│  ├─ nextcloud-mcp-tool-runtime/
│  ├─ nextcloud-mcp-tool-catalog/
│  └─ nextcloud-mcp-security/
│
├─ tools/
│  ├─ nextcloud-mcp-files-tools/
│  ├─ nextcloud-mcp-share-tools/
│  ├─ nextcloud-mcp-user-tools/
│  ├─ nextcloud-mcp-comments-tools/
│  ├─ nextcloud-mcp-trash-tools/
│  ├─ nextcloud-mcp-versions-tools/
│  ├─ nextcloud-mcp-status-tools/
│  └─ nextcloud-mcp-admin-tools/
│
├─ cli/
│  ├─ nextcloud-mcp-cli/
│  └─ nextcloud-mcp-admin-cli/
│
├─ app/
│  └─ nextcloud-mcp-server/
│
├─ docker/
│  ├─ Dockerfile
│  └─ docker-compose.yml
│
├─ scripts/
│  ├─ dev/
│  ├─ test/
│  └─ release/
│
└─ test-fixtures/
   ├─ responses/
   ├─ webdav/
   ├─ ocs/
   └─ configs/
```

---

# Root `pom.xml` module list

```xml
<modules>
    <!-- Shared plain Java libraries -->
    <module>lib/nextcloud-mcp-core</module>
    <module>lib/nextcloud-mcp-config</module>
    <module>lib/nextcloud-mcp-http</module>
    <module>lib/nextcloud-mcp-client</module>
    <module>lib/nextcloud-mcp-admin</module>
    <module>lib/nextcloud-mcp-security</module>

    <!-- MCP model/runtime -->
    <module>lib/nextcloud-mcp-tool-api</module>
    <module>lib/nextcloud-mcp-tool-runtime</module>
    <module>lib/nextcloud-mcp-tool-catalog</module>

    <!-- Tool modules -->
    <module>tools/nextcloud-mcp-files-tools</module>
    <module>tools/nextcloud-mcp-share-tools</module>
    <module>tools/nextcloud-mcp-user-tools</module>
    <module>tools/nextcloud-mcp-comments-tools</module>
    <module>tools/nextcloud-mcp-trash-tools</module>
    <module>tools/nextcloud-mcp-versions-tools</module>
    <module>tools/nextcloud-mcp-status-tools</module>
    <module>tools/nextcloud-mcp-admin-tools</module>

    <!-- CLI apps -->
    <module>cli/nextcloud-mcp-cli</module>
    <module>cli/nextcloud-mcp-admin-cli</module>

    <!-- Spring Boot server -->
    <module>app/nextcloud-mcp-server</module>
</modules>
```

---

# Package naming

Use:

```text
org.mcp.nextcloud
```

Package layout:

```text
org.mcp.nextcloud.core
org.mcp.nextcloud.config
org.mcp.nextcloud.http
org.mcp.nextcloud.client
org.mcp.nextcloud.admin
org.mcp.nextcloud.security
org.mcp.nextcloud.tool.api
org.mcp.nextcloud.tool.runtime
org.mcp.nextcloud.tool.catalog
org.mcp.nextcloud.tools.files
org.mcp.nextcloud.tools.shares
org.mcp.nextcloud.tools.users
org.mcp.nextcloud.tools.comments
org.mcp.nextcloud.tools.trash
org.mcp.nextcloud.tools.versions
org.mcp.nextcloud.tools.status
org.mcp.nextcloud.tools.admin
org.mcp.nextcloud.cli
org.mcp.nextcloud.server
```

---

# Module purpose

## `lib/nextcloud-mcp-core`

General domain objects, errors, result wrappers, IDs, and runtime primitives.

```text
lib/nextcloud-mcp-core/
└─ src/main/java/org/mcp/nextcloud/core/
   ├─ error/
   │  ├─ NextcloudMcpException.java
   │  ├─ NextcloudApiException.java
   │  ├─ ToolExecutionException.java
   │  └─ ConfigurationException.java
   │
   ├─ result/
   │  ├─ OperationResult.java
   │  ├─ PageResult.java
   │  ├─ ErrorResult.java
   │  └─ ProgressEvent.java
   │
   ├─ id/
   │  ├─ AccountId.java
   │  ├─ ToolId.java
   │  ├─ InvocationId.java
   │  └─ PrincipalId.java
   │
   └─ util/
      ├─ Preconditions.java
      └─ StringMasks.java
```

---

## `lib/nextcloud-mcp-http`

Plain Java HTTP abstraction.

```text
lib/nextcloud-mcp-http/
└─ src/main/java/org/mcp/nextcloud/http/
   ├─ HttpClientAdapter.java
   ├─ JdkHttpClientAdapter.java
   ├─ HttpRequestSpec.java
   ├─ HttpResponseSpec.java
   ├─ HttpMethod.java
   ├─ HttpHeadersBuilder.java
   ├─ BasicAuth.java
   ├─ BearerAuth.java
   ├─ RetryPolicy.java
   └─ RateLimitPolicy.java
```

Recommended default:

```text
java.net.http.HttpClient
```

Do not bind this layer to Spring.

---

## `lib/nextcloud-mcp-config`

External configuration parser and validator.

```text
lib/nextcloud-mcp-config/
└─ src/main/java/org/mcp/nextcloud/config/
   ├─ NextcloudMcpConfig.java
   ├─ NextcloudAccountConfig.java
   ├─ NextcloudAdminConfig.java
   ├─ ToolCatalogConfig.java
   ├─ ToolPolicyConfig.java
   ├─ ServerConfig.java
   ├─ ConfigLoader.java
   ├─ YamlConfigLoader.java
   ├─ SecretResolver.java
   ├─ EnvironmentSecretResolver.java
   └─ validation/
      ├─ ConfigValidator.java
      └─ ConfigValidationError.java
```

This allows config to work from:

```text
CLI
Spring Boot server
tests
future standalone runners
```

---

## `lib/nextcloud-mcp-client`

Plain Java Nextcloud user API client.

```text
lib/nextcloud-mcp-client/
└─ src/main/java/org/mcp/nextcloud/client/
   ├─ NextcloudClient.java
   ├─ NextcloudClientConfig.java
   ├─ NextcloudClientFactory.java
   │
   ├─ auth/
   │  ├─ NextcloudCredentials.java
   │  ├─ AppPasswordCredentials.java
   │  ├─ LoginFlowV2Client.java
   │  └─ AuthHeaderProvider.java
   │
   ├─ dav/
   │  ├─ WebDavClient.java
   │  ├─ WebDavPath.java
   │  ├─ WebDavProperty.java
   │  ├─ WebDavResource.java
   │  ├─ WebDavRequestBuilder.java
   │  ├─ WebDavXmlParser.java
   │  └─ WebDavNamespace.java
   │
   ├─ files/
   │  ├─ FilesClient.java
   │  ├─ FileMetadata.java
   │  ├─ ListFilesRequest.java
   │  ├─ UploadFileRequest.java
   │  ├─ DownloadFileRequest.java
   │  ├─ MoveFileRequest.java
   │  └─ CopyFileRequest.java
   │
   ├─ shares/
   │  ├─ SharesClient.java
   │  ├─ ShareInfo.java
   │  ├─ CreateShareRequest.java
   │  ├─ UpdateShareRequest.java
   │  ├─ ShareType.java
   │  └─ SharePermission.java
   │
   ├─ sharees/
   │  ├─ ShareesClient.java
   │  ├─ ShareeInfo.java
   │  └─ ShareeSearchRequest.java
   │
   ├─ trash/
   │  ├─ TrashbinClient.java
   │  └─ TrashItem.java
   │
   ├─ versions/
   │  ├─ VersionsClient.java
   │  └─ FileVersion.java
   │
   ├─ comments/
   │  ├─ CommentsClient.java
   │  ├─ CommentInfo.java
   │  └─ CreateCommentRequest.java
   │
   ├─ user/
   │  ├─ UserClient.java
   │  ├─ CurrentUserInfo.java
   │  └─ CapabilitiesInfo.java
   │
   ├─ status/
   │  ├─ UserStatusClient.java
   │  ├─ UserStatus.java
   │  └─ SetUserStatusRequest.java
   │
   └─ ocs/
      ├─ OcsClient.java
      ├─ OcsResponse.java
      ├─ OcsMeta.java
      └─ OcsResponseParser.java
```

Facade:

```java
public interface NextcloudClient {
    UserClient user();
    FilesClient files();
    SharesClient shares();
    ShareesClient sharees();
    TrashbinClient trashbin();
    VersionsClient versions();
    CommentsClient comments();
    UserStatusClient status();
}
```

---

## `lib/nextcloud-mcp-admin`

Plain Java admin client. This is separate from normal user access.

```text
lib/nextcloud-mcp-admin/
└─ src/main/java/org/mcp/nextcloud/admin/
   ├─ NextcloudAdminClient.java
   ├─ NextcloudAdminClientConfig.java
   ├─ NextcloudAdminClientFactory.java
   │
   ├─ users/
   │  ├─ AdminUsersClient.java
   │  ├─ CreateUserRequest.java
   │  ├─ UpdateUserRequest.java
   │  ├─ UserAdminInfo.java
   │  └─ UserState.java
   │
   ├─ groups/
   │  ├─ AdminGroupsClient.java
   │  ├─ GroupInfo.java
   │  ├─ GroupMembershipRequest.java
   │  └─ GroupMembershipInfo.java
   │
   ├─ apps/
   │  ├─ AdminAppsClient.java
   │  └─ NextcloudAppInfo.java
   │
   └─ system/
      ├─ ServerStatusClient.java
      ├─ ServerHealthInfo.java
      └─ ServerCapabilitiesAdminClient.java
```

Admin tools should be disabled by default in config.

---

## `lib/nextcloud-mcp-security`

Tool authorization, account isolation, credential masking, and policy checks.

```text
lib/nextcloud-mcp-security/
└─ src/main/java/org/mcp/nextcloud/security/
   ├─ Principal.java
   ├─ PrincipalContext.java
   ├─ AccountAccessPolicy.java
   ├─ ToolAccessPolicy.java
   ├─ ToolPermission.java
   ├─ Scope.java
   ├─ ScopeEvaluator.java
   ├─ SecretMasker.java
   └─ AuditEvent.java
```

Example scopes:

```text
nextcloud.files.read
nextcloud.files.write
nextcloud.files.delete
nextcloud.shares.read
nextcloud.shares.write
nextcloud.comments.write
nextcloud.trash.restore
nextcloud.admin.users.write
```

---

# MCP layer

## `lib/nextcloud-mcp-tool-api`

Framework-neutral MCP tool interfaces.

```text
lib/nextcloud-mcp-tool-api/
└─ src/main/java/org/mcp/nextcloud/tool/api/
   ├─ McpTool.java
   ├─ McpToolDescriptor.java
   ├─ McpToolSchema.java
   ├─ McpToolParameter.java
   ├─ McpToolInvocation.java
   ├─ McpToolResult.java
   ├─ McpToolError.java
   ├─ McpContent.java
   ├─ McpJsonContent.java
   ├─ McpTextContent.java
   ├─ McpBinaryContent.java
   └─ McpToolCategory.java
```

Core interface:

```java
public interface McpTool {
    McpToolDescriptor descriptor();

    McpToolResult invoke(McpToolInvocation invocation);
}
```

---

## `lib/nextcloud-mcp-tool-runtime`

Tool registry, dispatcher, validation, and execution.

```text
lib/nextcloud-mcp-tool-runtime/
└─ src/main/java/org/mcp/nextcloud/tool/runtime/
   ├─ McpToolRegistry.java
   ├─ DefaultMcpToolRegistry.java
   ├─ McpToolDispatcher.java
   ├─ DefaultMcpToolDispatcher.java
   ├─ McpInvocationContext.java
   ├─ McpInvocationValidator.java
   ├─ McpToolSchemaValidator.java
   ├─ McpResultMapper.java
   ├─ McpToolExecutionInterceptor.java
   ├─ AuditToolExecutionInterceptor.java
   └─ PolicyToolExecutionInterceptor.java
```

This module should be used by both:

```text
CLI
Spring Boot server
```

---

## `lib/nextcloud-mcp-tool-catalog`

Builds `tools/list`-style metadata from enabled tool modules.

```text
lib/nextcloud-mcp-tool-catalog/
└─ src/main/java/org/mcp/nextcloud/tool/catalog/
   ├─ ToolCatalog.java
   ├─ ToolCatalogBuilder.java
   ├─ ToolCatalogExporter.java
   ├─ ToolDescriptorNormalizer.java
   └─ ToolCatalogJsonWriter.java
```

This is where the MCP server can expose all available capabilities.

---

# Tool modules

## `tools/nextcloud-mcp-files-tools`

```text
tools/nextcloud-mcp-files-tools/
└─ src/main/java/org/mcp/nextcloud/tools/files/
   ├─ ListFilesTool.java
   ├─ GetFileMetadataTool.java
   ├─ DownloadFileTool.java
   ├─ UploadFileTool.java
   ├─ CreateFolderTool.java
   ├─ DeleteFileTool.java
   ├─ MoveFileTool.java
   ├─ CopyFileTool.java
   ├─ SearchFilesTool.java
   └─ SetFavoriteTool.java
```

Tool names:

```text
nextcloud.files.list
nextcloud.files.stat
nextcloud.files.download
nextcloud.files.upload
nextcloud.files.mkdir
nextcloud.files.delete
nextcloud.files.move
nextcloud.files.copy
nextcloud.files.search
nextcloud.files.favorite
```

---

## `tools/nextcloud-mcp-share-tools`

```text
tools/nextcloud-mcp-share-tools/
└─ src/main/java/org/mcp/nextcloud/tools/shares/
   ├─ ListSharesTool.java
   ├─ GetShareTool.java
   ├─ CreateShareTool.java
   ├─ UpdateShareTool.java
   ├─ DeleteShareTool.java
   ├─ SendShareEmailTool.java
   ├─ SearchShareesTool.java
   └─ RecommendedShareesTool.java
```

Tool names:

```text
nextcloud.shares.list
nextcloud.shares.get
nextcloud.shares.create
nextcloud.shares.update
nextcloud.shares.delete
nextcloud.shares.send_email
nextcloud.sharees.search
nextcloud.sharees.recommended
```

---

## `tools/nextcloud-mcp-user-tools`

```text
tools/nextcloud-mcp-user-tools/
└─ src/main/java/org/mcp/nextcloud/tools/users/
   ├─ GetCurrentUserTool.java
   ├─ GetCapabilitiesTool.java
   └─ GetUserMetadataTool.java
```

Tool names:

```text
nextcloud.user.me
nextcloud.user.capabilities
nextcloud.user.metadata
```

For non-admin accounts, `metadata` should only allow the current authenticated account.

---

## `tools/nextcloud-mcp-comments-tools`

```text
tools/nextcloud-mcp-comments-tools/
└─ src/main/java/org/mcp/nextcloud/tools/comments/
   ├─ ListCommentsTool.java
   ├─ CreateCommentTool.java
   ├─ UpdateCommentTool.java
   ├─ DeleteCommentTool.java
   └─ MarkCommentsReadTool.java
```

Tool names:

```text
nextcloud.comments.list
nextcloud.comments.create
nextcloud.comments.update
nextcloud.comments.delete
nextcloud.comments.mark_read
```

---

## `tools/nextcloud-mcp-trash-tools`

```text
tools/nextcloud-mcp-trash-tools/
└─ src/main/java/org/mcp/nextcloud/tools/trash/
   ├─ ListTrashTool.java
   ├─ RestoreTrashItemTool.java
   ├─ DeleteTrashItemTool.java
   └─ EmptyTrashTool.java
```

Tool names:

```text
nextcloud.trash.list
nextcloud.trash.restore
nextcloud.trash.delete
nextcloud.trash.empty
```

---

## `tools/nextcloud-mcp-versions-tools`

```text
tools/nextcloud-mcp-versions-tools/
└─ src/main/java/org/mcp/nextcloud/tools/versions/
   ├─ ListVersionsTool.java
   └─ RestoreVersionTool.java
```

Tool names:

```text
nextcloud.versions.list
nextcloud.versions.restore
```

---

## `tools/nextcloud-mcp-status-tools`

```text
tools/nextcloud-mcp-status-tools/
└─ src/main/java/org/mcp/nextcloud/tools/status/
   ├─ GetUserStatusTool.java
   ├─ SetUserStatusTool.java
   ├─ SetStatusMessageTool.java
   ├─ ClearStatusMessageTool.java
   └─ ListPredefinedStatusesTool.java
```

Tool names:

```text
nextcloud.status.get
nextcloud.status.set
nextcloud.status.message.set
nextcloud.status.message.clear
nextcloud.status.predefined
```

---

## `tools/nextcloud-mcp-admin-tools`

Admin-only MCP tools.

```text
tools/nextcloud-mcp-admin-tools/
└─ src/main/java/org/mcp/nextcloud/tools/admin/
   ├─ AdminListUsersTool.java
   ├─ AdminCreateUserTool.java
   ├─ AdminDisableUserTool.java
   ├─ AdminDeleteUserTool.java
   ├─ AdminListGroupsTool.java
   ├─ AdminCreateGroupTool.java
   ├─ AdminAddUserToGroupTool.java
   ├─ AdminRemoveUserFromGroupTool.java
   └─ AdminServerHealthTool.java
```

Tool names:

```text
nextcloud.admin.users.list
nextcloud.admin.users.create
nextcloud.admin.users.disable
nextcloud.admin.users.delete
nextcloud.admin.groups.list
nextcloud.admin.groups.create
nextcloud.admin.groups.add_user
nextcloud.admin.groups.remove_user
nextcloud.admin.server.health
```

These should require explicit config:

```yaml
nextcloud-mcp:
  tools:
    admin:
      enabled: false
```

---

# Spring Boot server

## `app/nextcloud-mcp-server`

```text
app/nextcloud-mcp-server/
├─ pom.xml
└─ src/main/
   ├─ java/org/mcp/nextcloud/server/
   │  ├─ NextcloudMcpServerApplication.java
   │  │
   │  ├─ config/
   │  │  ├─ NextcloudMcpProperties.java
   │  │  ├─ NextcloudClientSpringConfiguration.java
   │  │  ├─ NextcloudAdminSpringConfiguration.java
   │  │  ├─ McpToolSpringConfiguration.java
   │  │  ├─ SecuritySpringConfiguration.java
   │  │  └─ JacksonConfiguration.java
   │  │
   │  ├─ controller/
   │  │  ├─ HealthController.java
   │  │  ├─ McpToolsController.java
   │  │  ├─ McpInvokeController.java
   │  │  ├─ NextcloudAccountsController.java
   │  │  └─ AdminController.java
   │  │
   │  ├─ transport/
   │  │  ├─ http/
   │  │  │  ├─ McpHttpController.java
   │  │  │  └─ McpHttpRequestMapper.java
   │  │  │
   │  │  └─ websocket/
   │  │     ├─ McpWebSocketHandler.java
   │  │     ├─ McpWebSocketSessionRegistry.java
   │  │     └─ McpWebSocketMessageMapper.java
   │  │
   │  ├─ service/
   │  │  ├─ AccountRegistryService.java
   │  │  ├─ ToolExecutionService.java
   │  │  ├─ ToolCatalogService.java
   │  │  └─ AuditService.java
   │  │
   │  ├─ api/
   │  │  ├─ dto/
   │  │  │  ├─ ToolListResponse.java
   │  │  │  ├─ ToolInvokeRequest.java
   │  │  │  ├─ ToolInvokeResponse.java
   │  │  │  └─ AccountInfoResponse.java
   │  │  │
   │  │  └─ mapper/
   │  │     ├─ ToolDtoMapper.java
   │  │     └─ AccountDtoMapper.java
   │  │
   │  └─ persistence/
   │     ├─ entity/
   │     │  ├─ AccountEntity.java
   │     │  ├─ ToolInvocationEntity.java
   │     │  └─ AuditEventEntity.java
   │     │
   │     ├─ repository/
   │     │  ├─ AccountRepository.java
   │     │  ├─ ToolInvocationRepository.java
   │     │  └─ AuditEventRepository.java
   │     │
   │     └─ mapper/
   │        ├─ AccountEntityMapper.java
   │        └─ AuditEventEntityMapper.java
   │
   └─ resources/
      ├─ application.yml
      ├─ application-dev.yml
      ├─ application-prod.yml
      └─ db/migration/
         ├─ V001__init.sql
         ├─ V002__accounts.sql
         ├─ V003__tool_invocations.sql
         └─ V004__audit_events.sql
```

---

# Server endpoints

Internal HTTP layer should expose:

```text
GET  /health
GET  /api/v1/tools
POST /api/v1/tools/call
GET  /api/v1/accounts
POST /api/v1/accounts/test
GET  /api/v1/audit
```

Optional MCP-compatible shape:

```text
POST /mcp
GET  /mcp/tools
POST /mcp/tools/call
WS   /mcp/ws
```

Recommended JSON-RPC shape:

```json
{
  "jsonrpc": "2.0",
  "id": "req_001",
  "method": "tools/call",
  "params": {
    "name": "nextcloud.files.list",
    "arguments": {
      "account": "main",
      "path": "/Documents"
    }
  }
}
```

---

# CLI layout

## `cli/nextcloud-mcp-cli`

General tool caller.

```text
cli/nextcloud-mcp-cli/
└─ src/main/java/org/mcp/nextcloud/cli/
   ├─ NextcloudMcpCli.java
   ├─ command/
   │  ├─ ToolsCommand.java
   │  ├─ CallToolCommand.java
   │  ├─ AccountsCommand.java
   │  └─ ConfigCheckCommand.java
   └─ output/
      ├─ JsonOutput.java
      ├─ TableOutput.java
      └─ PrettyPrinter.java
```

Example:

```bash
nextcloud-mcp tools list
nextcloud-mcp call nextcloud.files.list --arg path=/Documents
nextcloud-mcp accounts test main
```

---

## `cli/nextcloud-mcp-admin-cli`

Admin/operator-only CLI.

```text
cli/nextcloud-mcp-admin-cli/
└─ src/main/java/org/mcp/nextcloud/admincli/
   ├─ NextcloudMcpAdminCli.java
   ├─ command/
   │  ├─ AdminUsersCommand.java
   │  ├─ AdminGroupsCommand.java
   │  ├─ AdminAppsCommand.java
   │  ├─ AdminHealthCommand.java
   │  └─ AdminConfigCommand.java
   └─ output/
      ├─ AdminJsonOutput.java
      └─ AdminTableOutput.java
```

Example:

```bash
nextcloud-mcp-admin users list
nextcloud-mcp-admin users create ingest_bot
nextcloud-mcp-admin groups add-user ingest_bot automation
nextcloud-mcp-admin health
```

---

# Configuration shape

Main config:

```yaml
nextcloud-mcp:
  server:
    enabled: true
    host: 0.0.0.0
    port: 8080

  accounts:
    main:
      base-url: https://cloud.example.com
      username: ingest_bot
      app-password: ${NC_MCP_MAIN_APP_PASSWORD}
      default: true
      scopes:
        - nextcloud.files.read
        - nextcloud.files.write
        - nextcloud.shares.read
        - nextcloud.shares.write
        - nextcloud.comments.write
        - nextcloud.trash.restore

    admin:
      base-url: https://cloud.example.com
      username: admin_operator
      app-password: ${NC_MCP_ADMIN_APP_PASSWORD}
      admin: true
      enabled: false
      scopes:
        - nextcloud.admin.users.read
        - nextcloud.admin.users.write
        - nextcloud.admin.groups.read
        - nextcloud.admin.groups.write

  tools:
    files:
      enabled: true
    shares:
      enabled: true
    users:
      enabled: true
    comments:
      enabled: true
    trash:
      enabled: true
    versions:
      enabled: true
    status:
      enabled: true
    admin:
      enabled: false

  security:
    require-account-scope: true
    mask-secrets: true
    audit-tool-calls: true
    deny-delete-by-default: true

  webdav:
    preserve-modified-time: true
    auto-mkcol: true
    default-depth: 1
```

---

# Dependency direction

Keep it like this:

```text
app/nextcloud-mcp-server
  → tool-runtime
  → tool modules
  → nextcloud client/admin client

cli/*
  → tool-runtime
  → tool modules
  → nextcloud client/admin client

tools/*
  → tool-api
  → nextcloud client/admin client
  → security

nextcloud-mcp-client
  → http
  → core

nextcloud-mcp-admin
  → nextcloud-mcp-client
  → http
  → core
```

Avoid this:

```text
nextcloud-mcp-client → Spring Boot
nextcloud-mcp-tool-api → Spring Boot
nextcloud-mcp-core → Spring Boot
```

Spring should only wire and expose the runtime. It should not own the actual capability logic.

---

# Minimum starting structure

To avoid overbuilding, start with this:

```text
nextcloud-mcp/
├─ pom.xml
├─ lib/
│  ├─ nextcloud-mcp-core/
│  ├─ nextcloud-mcp-http/
│  ├─ nextcloud-mcp-config/
│  ├─ nextcloud-mcp-client/
│  ├─ nextcloud-mcp-tool-api/
│  └─ nextcloud-mcp-tool-runtime/
│
├─ tools/
│  ├─ nextcloud-mcp-files-tools/
│  ├─ nextcloud-mcp-share-tools/
│  └─ nextcloud-mcp-user-tools/
│
├─ cli/
│  └─ nextcloud-mcp-cli/
│
└─ app/
   └─ nextcloud-mcp-server/
```

Then add:

```text
nextcloud-mcp-admin
nextcloud-mcp-admin-cli
nextcloud-mcp-admin-tools
nextcloud-mcp-trash-tools
nextcloud-mcp-versions-tools
nextcloud-mcp-comments-tools
nextcloud-mcp-status-tools
nextcloud-mcp-tool-catalog
nextcloud-mcp-security
```

---

# Best final interpretation

`nextcloud-mcp` should be structured as:

```text
Nextcloud API SDK
+ Admin SDK
+ MCP tool contract
+ MCP runtime dispatcher
+ Nextcloud capability tools
+ CLI caller
+ Admin CLI
+ Spring Boot MCP server
```

The core design rule:

```text
Nextcloud API clients know HTTP/WebDAV/OCS.
Tools know capability names and schemas.
Runtime knows how to list/invoke tools.
Spring only exposes the runtime over HTTP/WebSocket.
```

That gives you a real MCP-style Nextcloud capability server, not just a connector.
