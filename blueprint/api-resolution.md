I will start by checking the root directory of the workspace to see the project's structure and locate any configuration files, architect logs, or security-related components.

Listed directory nextcloud-mcp

I will list the contents of the `architect` directory to check for any documentation on the project design, including security policies or architectural decisions.

Listed directory architect

I will list the contents of the `lib/nextcloud-mcp-security` module to understand what security abstractions, classes, or patterns are defined there.

Listed directory nextcloud-mcp-security

I will read the `README.md` of `nextcloud-mcp-security` to understand the core security design.

Viewed README.md

I will check the directory structure of `lib/nextcloud-mcp-security/src` to find the Java source files containing the security implementation.

Listed directory src

I will search for Java package/class definitions inside `nextcloud-mcp-security` to locate the source code files.

Searched for "package"

I will search for occurrences of "Authorization" or "username" across the library code to see where credentials and API tokens are processed.

Searched for "authorization"

I will view `BasicAuth.java` to see how Basic Authentication is implemented for Nextcloud requests.

Viewed BasicAuth.java:1-19

I will view `BearerAuth.java` to see if Bearer Authentication (e.g. for OAuth or App tokens) is also supported or implemented.

Viewed BearerAuth.java:1-13

I will view `NextcloudHttpRequestFactory.java` to see how the headers, authentication, and HTTP client requests are generated and structured.

Viewed NextcloudHttpRequestFactory.java:1-206

I will list the contents of `lib/nextcloud-mcp-config/src/main/java` to see how configuration (including properties for credentials, tokens, etc.) is loaded.

Listed directory java

I will search for Java package/class files inside `nextcloud-mcp-config` to see the structure of configuration management.

Searched for "package"

I will view `NextcloudMcpConfig.java` to see the top-level configuration structure.

Viewed NextcloudMcpConfig.java:1-17

I will view `NextcloudAccountConfig.java` to see what fields/properties represent credentials for an account.

Viewed NextcloudAccountConfig.java:1-18

I will view `NextcloudAdminConfig.java` to see the admin configuration structure and credentials.

Viewed NextcloudAdminConfig.java:1-8

I will view `SecretResolver.java` to see how credentials or secret placeholders are resolved.

Viewed SecretResolver.java:1-8

I will view `EnvironmentSecretResolver.java` to see how it resolves secrets (e.g. from `.env` or system environment).

Viewed EnvironmentSecretResolver.java:1-14

I will view `YamlConfigLoader.java` to see if/how placeholders in YAML are replaced (e.g. `${SECRET_NAME}`).

Viewed YamlConfigLoader.java:1-25

I will view `ToolPermission.java` to see what tool permissions are defined.

Viewed ToolPermission.java:1-12

I will view `Scope.java` to see what types of scopes are supported.

Viewed Scope.java:1-23

I will view `Principal.java` to see the structure of a user or client principal inside the security module.

Viewed Principal.java:1-12

I will view `PrincipalContext.java` to see how principal contexts are managed (e.g. ThreadLocal or similar context holder).

Viewed PrincipalContext.java:1-8

I will view `ScopeEvaluator.java` to see how scopes are matched/evaluated.

Viewed ScopeEvaluator.java:1-12

I will view `AccountAccessPolicy.java` to see how access to accounts is controlled.

Viewed AccountAccessPolicy.java:1-16

I will view `ToolAccessPolicy.java` to see how access to tools is controlled.

Viewed ToolAccessPolicy.java:1-29

I will view `SecretMasker.java` to see how secrets are masked.

Viewed SecretMasker.java:1-16

I will view `AuditEvent.java` to see what fields are tracked for auditing.

Viewed AuditEvent.java:1-23

I will list the Java source files in the `nextcloud-mcp-client` module to see where client implementations reside.

Searched for "package"

I will view `NextcloudCredentials.java` to see how credentials are represented on the client side.

Viewed NextcloudCredentials.java:1-60

I will view `AbstractNextcloudClient.java` to see how the client communicates with the HTTP factory or configures authentication.

Viewed AbstractNextcloudClient.java:1-44

I will search for `requests.` references in `nextcloud-mcp-client` to map the API paths called and identify their request type (OCS vs WebDAV).

Searched for "requests."

I will list the contents of the `lib` directory to see all the submodules and trace any additional client/security modules.

Listed directory lib

I will search for Java package/class files inside `nextcloud-mcp-admin` to locate the source code files.

Searched for "package"

I will view `NextcloudUsersClient.java` to see the API path for user checks and details.

Viewed NextcloudUsersClient.java:1-37

I will view `NextcloudFilesClient.java` to see the WebDAV operations and endpoints called for files, folders, and operations.

Viewed NextcloudFilesClient.java:1-230

I will view `NextcloudSharesClient.java` to see the endpoints called for sharing logic.

Viewed NextcloudSharesClient.java:1-92

I will view `NextcloudShareesClient.java` to see the endpoints called for sharees (users/groups to share with).

Viewed NextcloudShareesClient.java:1-75

I will view `NextcloudAdminCredentials.java` to see if admin credentials use the same structure or have special admin-only configurations.

Viewed NextcloudAdminCredentials.java:1-75

I will view `AbstractNextcloudAdminClient.java` to see the structure of admin client HTTP wrapper.

Viewed AbstractNextcloudAdminClient.java:1-74

I will search for `_ENDPOINT` or similar endpoint strings in `nextcloud-mcp-admin` to map the admin API endpoints.

Searched for "ENDPOINT"

I will view `NextcloudAdminAppsClient.java` to see what endpoints it accesses for managing apps.

Viewed NextcloudAdminAppsClient.java:1-77

I will view `NextcloudAdminGroupsClient.java` to see user group management endpoints.

Viewed NextcloudAdminGroupsClient.java:1-115

I will view `NextcloudAdminUsersClient.java` to see provisioning and admin operations for users.

Viewed NextcloudAdminUsersClient.java:1-126

I will view `AdminAuthClient.java` to see if there is any other auth logic, perhaps related to token generation or validation.

Viewed AdminAuthClient.java:1-28

I will search the repository for `ToolAccessPolicy` or `PrincipalContext` references to see how security policy enforcement is integrated with the MCP server runtime.

Searched for "ToolAccessPolicy"

I will view `DefaultToolPolicyInterceptor.java` to see how security policies are enforced on tool execution.

Viewed DefaultToolPolicyInterceptor.java:1-41

I will search for occurrences of `PrincipalContext` in the codebase to see how security contexts are created and populated.

Searched for "PrincipalContext"

I will view `ToolRuntimeContext.java` to see if it holds security details during execution.

Viewed ToolRuntimeContext.java:1-14

I will view `ToolDispatcher.java` to see where the evaluation and interceptors are run.

Viewed ToolDispatcher.java:1-108

I will check the contents of the `src` directory at the root to see if there is any server setup or main entry point.

Listed directory src

I will search for package/class definitions in the root `src` directory to see if there is a main server application there.

Searched for "package"

I will view `NextcloudMcpApplication.java` to see how the Spring application entry point is set up and if any security wiring or interceptors are registered here.

Viewed NextcloudMcpApplication.java:1-14

I will search the repository for `@Configuration` or `@Component` annotations to locate any Spring-based configuration or beans.

Searched for "@Configuration"

I will search the repository for `@Component` annotations.

Searched for "@Component"

I will list the contents of the `tools` directory to see all the individual tool submodules.

Listed directory tools

I will search for Java package/class files inside `nextcloud-mcp-files-tools` to see how file tools are registered and what security annotations/descriptors they might have.

Searched for "package"

I will view `NextcloudFilesTools.java` to see how tools define their scopes and permissions.

Viewed NextcloudFilesTools.java:1-198

I will search the `tools/` directory for tool registrations to locate all registered tools, their required scopes, and their destructive flags.

Searched for "registrations("

I will search for `tool(` inside the `tools/` directory to see all tool definitions.

Searched for "tool("

I will search for all Java packages defined in the `tools/` directory to locate any java source files.

Searched for "package"

I will view `NextcloudAdminTools.java` to see what admin-level tools are defined and their required scopes.

Viewed NextcloudAdminTools.java:1-360

I will view `NextcloudUserTools.java` to see what user tools are registered and their security specifications.

Viewed NextcloudUserTools.java:1-94

I will view `NextcloudShareTools.java` to see what sharing-related tools are defined and their security specifications.

Viewed NextcloudShareTools.java:1-239

Here is a detailed breakdown of the security implementation in this project, mapping credentials, tokens, policies, and low-level authorization headers per API path and operation.

---

## 1. Core Security Architecture & Credentials Management

### Credentials Configuration (`nextcloud-mcp-config`)
* **Source**: Loaded from configuration files (via [YamlConfigLoader](file:///j:/Users/jbeas/Repositories/Dev.java-2026/artifacts/nextcloud-mcp/lib/nextcloud-mcp-config/src/main/java/org/mcp/nextcloud/config/YamlConfigLoader.java)) mapping to [NextcloudMcpConfig](file:///j:/Users/jbeas/Repositories/Dev.java-2026/artifacts/nextcloud-mcp/lib/nextcloud-mcp-config/src/main/java/org/mcp/nextcloud/config/NextcloudMcpConfig.java).
* **Secret Resolution**: Credentials (specifically `appPassword` in [NextcloudAccountConfig](file:///j:/Users/jbeas/Repositories/Dev.java-2026/artifacts/nextcloud-mcp/lib/nextcloud-mcp-config/src/main/java/org/mcp/nextcloud/config/NextcloudAccountConfig.java)) can be declared directly or referenced dynamically as environment variables (e.g., `${NEXTCLOUD_APP_PASSWORD}`). These references are resolved by [EnvironmentSecretResolver](file:///j:/Users/jbeas/Repositories/Dev.java-2026/artifacts/nextcloud-mcp/lib/nextcloud-mcp-config/src/main/java/org/mcp/nextcloud/config/EnvironmentSecretResolver.java).
* **Credentials Sanitization**: The project employs [SecretMasker](file:///j:/Users/jbeas/Repositories/Dev.java-2026/artifacts/nextcloud-mcp/lib/nextcloud-mcp-security/src/main/java/org/mcp/nextcloud/security/SecretMasker.java) to scrub credentials and tokens from logs or error outputs.

### Access Control Policies (`nextcloud-mcp-security`)
Every tool invocation is intercepted and validated against security rules before firing the actual HTTP requests:
1. **Account Verification**: [AccountAccessPolicy](file:///j:/Users/jbeas/Repositories/Dev.java-2026/artifacts/nextcloud-mcp/lib/nextcloud-mcp-security/src/main/java/org/mcp/nextcloud/security/AccountAccessPolicy.java) confirms that the invoking [Principal](file:///j:/Users/jbeas/Repositories/Dev.java-2026/artifacts/nextcloud-mcp/lib/nextcloud-mcp-security/src/main/java/org/mcp/nextcloud/security/Principal.java) has authorization to request operations on the given `accountId`. Admin-marked accounts require `admin = true` on the principal.
2. **Scope Enforcement**: [ToolAccessPolicy](file:///j:/Users/jbeas/Repositories/Dev.java-2026/artifacts/nextcloud-mcp/lib/nextcloud-mcp-security/src/main/java/org/mcp/nextcloud/security/ToolAccessPolicy.java) checks if the principal possesses all the required scopes declared in the tool's descriptor.
3. **Destructive Operations Filter**: If a tool is flagged as `destructive`, the policy interceptor rejects it by default unless the principal explicitly possesses the `nextcloud.files.delete` scope.
4. **Audit Logging**: Successful or blocked attempts are passed to a `ToolAuditSink` recording [AuditEvent](file:///j:/Users/jbeas/Repositories/Dev.java-2026/artifacts/nextcloud-mcp/lib/nextcloud-mcp-security/src/main/java/org/mcp/nextcloud/security/AuditEvent.java) metadata (timestamps, invocation ID, principal, target account, and outcomes).

### Network-Level Authorization Headers (`nextcloud-mcp-http`)
* All outbound OCS and WebDAV calls are decorated by [NextcloudHttpRequestFactory](file:///j:/Users/jbeas/Repositories/Dev.java-2026/artifacts/nextcloud-mcp/lib/nextcloud-mcp-http/src/main/java/org/mcp/nextcloud/http/NextcloudHttpRequestFactory.java) using the configured account username and resolved app-password.
* **Header format**: Basic Auth encoded in Base64:
  `Authorization: Basic base64(username:appPassword)`
* **OCS Specific Headers**: OCS API endpoints require two mandatory HTTP headers:
  ```http
  OCS-APIRequest: true
  Accept: application/json
  ```

---

## 2. API Path Call Mapping

Below is the security mapping for each API endpoint accessed in the codebase.

### User & System Capabilities APIs
* **Endpoint Base**: `/ocs/v1.php/cloud`

| API Path | HTTP Method | Associated MCP Tool | Required Security Scopes | Destructive? | Auth Header | Client Method |
| :--- | :--- | :--- | :--- | :---: | :--- | :--- |
| `/user` | `GET` | `nextcloud.user.me` <br> `nextcloud.user.metadata` | `nextcloud.user.read` | No | Basic | `NextcloudUsersClient.currentUser()` |
| `/capabilities` | `GET` | `nextcloud.user.capabilities` | `nextcloud.user.read` | No | Basic | `NextcloudUsersClient.capabilities()` |

---

### WebDAV Files & Folders APIs
* **Endpoint Base**: `/remote.php/dav/files/{userId}`
* **Authorization**: Basic Authentication.
* **Client Implementation**: [NextcloudFilesClient](file:///j:/Users/jbeas/Repositories/Dev.java-2026/artifacts/nextcloud-mcp/lib/nextcloud-mcp-client/src/main/java/org/mcp/nextcloud/client/NextcloudFilesClient.java)

| API Path | HTTP Method | Associated MCP Tool | Required Security Scopes | Destructive? | Extra Headers | Description |
| :--- | :--- | :--- | :--- | :---: | :--- | :--- |
| `/{path}` | `PROPFIND` | `nextcloud.files.list` | `nextcloud.files.read` | No | `Depth: 1` | Lists contents of folder |
| `/{path}` | `PROPFIND` | `nextcloud.files.stat` | `nextcloud.files.read` | No | `Depth: 0` | Inspects target metadata |
| `/{path}` | `GET` | `nextcloud.files.download` | `nextcloud.files.read` | No | None | Downloads file content |
| `/{path}` | `PUT` | `nextcloud.files.upload` | `nextcloud.files.write` | No | `Content-Type: application/octet-stream` | Uploads file content |
| `/{path}/` | `MKCOL` | `nextcloud.files.mkdir` | `nextcloud.files.write` | No | None | Creates a folder |
| `/{path}` | `DELETE` | `nextcloud.files.delete` | `nextcloud.files.delete` | **Yes** | None | Deletes file or folder |
| `/{path}` | `MOVE` | `nextcloud.files.move` | `nextcloud.files.write`, `nextcloud.files.delete` | **Yes** | `Destination: {targetUri}` | Renames or moves a file |
| `/{path}` | `COPY` | `nextcloud.files.copy` | `nextcloud.files.write` | No | `Destination: {targetUri}` | Copies a file |
| `/{path}` | `PROPPATCH` | `nextcloud.files.favorite` | `nextcloud.files.write` | No | None | Sets favorite metadata property |
| `/remote.php/dav/` | `SEARCH` | `nextcloud.files.search` | `nextcloud.files.read` | No | None | Performs search scoping `/files/{userId}` |

---

### OCS Shares APIs
* **Endpoint Base**: `/ocs/v2.php/apps/files_sharing/api/v1`
* **Authorization**: Basic Authentication + OCS Headers.
* **Client Implementation**: [NextcloudSharesClient](file:///j:/Users/jbeas/Repositories/Dev.java-2026/artifacts/nextcloud-mcp/lib/nextcloud-mcp-client/src/main/java/org/mcp/nextcloud/client/NextcloudSharesClient.java) & [NextcloudShareesClient](file:///j:/Users/jbeas/Repositories/Dev.java-2026/artifacts/nextcloud-mcp/lib/nextcloud-mcp-client/src/main/java/org/mcp/nextcloud/client/NextcloudShareesClient.java)

| API Path | HTTP Method | Associated MCP Tool | Required Security Scopes | Destructive? | Content Type / Query Params | Description |
| :--- | :--- | :--- | :--- | :---: | :--- | :--- |
| `/shares` | `GET` | `nextcloud.shares.list` <br> `nextcloud.shares.get` (deferred) | `nextcloud.shares.read` | No | URL queries (optional `path` filter) | Lists user's file shares |
| `/shares` | `POST` | `nextcloud.shares.create` <br> `nextcloud.shares.update` (deferred) | `nextcloud.shares.write` | No | `application/x-www-form-urlencoded` | Creates folder/file share |
| `/shares/{shareId}` | `DELETE` | `nextcloud.shares.delete` | `nextcloud.shares.write` | **Yes** | None | Deletes file share |
| `/sharees` | `GET` | `nextcloud.sharees.search` <br> `nextcloud.sharees.recommended` (deferred) | `nextcloud.shares.read` | No | URL queries (`search`, `itemType`, etc.) | Searches users/groups to share with |

---

### OCS Admin Provisioning & Apps APIs
* **Endpoint Base**: `/ocs/v1.php/cloud`
* **Authorization**: Admin Basic Authentication + OCS Headers. *(Requires matching admin scopes, and target user accounts configured with `admin: true`).*
* **Client Implementation**: [NextcloudAdminUsersClient](file:///j:/Users/jbeas/Repositories/Dev.java-2026/artifacts/nextcloud-mcp/lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/NextcloudAdminUsersClient.java), [NextcloudAdminGroupsClient](file:///j:/Users/jbeas/Repositories/Dev.java-2026/artifacts/nextcloud-mcp/lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/NextcloudAdminGroupsClient.java), & [NextcloudAdminAppsClient](file:///j:/Users/jbeas/Repositories/Dev.java-2026/artifacts/nextcloud-mcp/lib/nextcloud-mcp-admin/src/main/java/org/mcp/nextcloud/admin/NextcloudAdminAppsClient.java)

| API Path | HTTP Method | Associated MCP Tool | Required Security Scopes | Destructive? | Description |
| :--- | :--- | :--- | :--- | :---: | :--- |
| `/users` | `GET` | `nextcloud.admin.users.list` | `nextcloud.admin.read` | No | Lists users |
| `/users` | `POST` | `nextcloud.admin.users.create` | `nextcloud.admin.write` | No | Provisions new user |
| `/users/{userId}` | `GET` | `nextcloud.admin.users.get` | `nextcloud.admin.read` | No | Fetches details for a user |
| `/users/{userId}` | `PUT` | `nextcloud.admin.users.update_field` | `nextcloud.admin.write` | No | Modifies editable user details |
| `/users/{userId}` | `DELETE` | `nextcloud.admin.users.delete` | `nextcloud.admin.delete` | **Yes** | Deletes user |
| `/users/{userId}/groups` | `GET` | `nextcloud.admin.users.groups` | `nextcloud.admin.read` | No | Lists user's groups |
| `/users/{userId}/groups` | `POST` | `nextcloud.admin.groups.add_user` | `nextcloud.admin.write` | No | Adds user to group |
| `/users/{userId}/groups` | `DELETE` | `nextcloud.admin.groups.remove_user` | `nextcloud.admin.write` | No | Removes user from group |
| `/users/{userId}/subadmins` | `GET` | `nextcloud.admin.users.subadmins` | `nextcloud.admin.read` | No | Lists subadmin group roles |
| `/users/{userId}/subadmins` | `POST` | `nextcloud.admin.subadmins.promote` | `nextcloud.admin.write` | **Yes** | Promotes user to group subadmin |
| `/users/{userId}/subadmins` | `DELETE` | `nextcloud.admin.subadmins.demote` | `nextcloud.admin.write` | **Yes** | Demotes user from group subadmin |
| `/users/{userId}/disable` | `PUT` | `nextcloud.admin.users.disable` | `nextcloud.admin.write` | **Yes** | Disables user account |
| `/users/{userId}/enable` | `PUT` | `nextcloud.admin.users.enable` | `nextcloud.admin.write` | **Yes** | Enables user account |
| `/groups` | `GET` | `nextcloud.admin.groups.list` | `nextcloud.admin.read` | No | Lists user groups |
| `/groups` | `POST` | `nextcloud.admin.groups.create` | `nextcloud.admin.write` | No | Creates new group |
| `/groups/{groupId}` | `GET` | `nextcloud.admin.groups.members` | `nextcloud.admin.read` | No | Lists group members |
| `/groups/{groupId}` | `PUT` | `nextcloud.admin.groups.update_display_name` | `nextcloud.admin.write` | No | Updates group display name |
| `/groups/{groupId}` | `DELETE` | `nextcloud.admin.groups.delete` | `nextcloud.admin.delete` | **Yes** | Deletes group |
| `/groups/{groupId}/subadmins`| `GET` | `nextcloud.admin.groups.subadmins` | `nextcloud.admin.read` | No | Lists group subadmins |
| `/apps` | `GET` | `nextcloud.admin.apps.list`<br>`.enabled` / `.disabled` | `nextcloud.admin.read`, `nextcloud.admin.apps` | No | Lists Nextcloud app store apps |
| `/apps/{appId}` | `GET` | `nextcloud.admin.apps.get` | `nextcloud.admin.read`, `nextcloud.admin.apps` | No | Inspects app information |
| `/apps/{appId}` | `POST` | `nextcloud.admin.apps.enable` | `nextcloud.admin.write`, `nextcloud.admin.apps` | **Yes** | Enables app |
| `/apps/{appId}` | `DELETE` | `nextcloud.admin.apps.disable` | `nextcloud.admin.write`, `nextcloud.admin.apps` | **Yes** | Disables app |
| *(Local OCC bridge)* | N/A | `nextcloud.admin.occ.*` *(various tools)* | `nextcloud.admin.occ` | **Yes** | Constructs command plan templates for OCC execution |