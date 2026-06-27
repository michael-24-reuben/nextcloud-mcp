# API Documentation

## McpJsonRpcController

---
### Handles MCP-compatible JSON-RPC requests on the shared{@code /mcp} endpoint.

> BASIC

**Path:** /mcp

**Method:** POST

**Desc:**

Handles MCP-compatible JSON-RPC requests on the shared{@code /mcp} endpoint.


> REQUEST

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| Content-Type | application/json | NO |  |

**Request Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| jsonrpc | string |  |
| method | string |  |
| params | map |  |
| id | object |  |


**Request Demo:**

```json
{
  "jsonrpc": "",
  "method": "",
  "params": {
    "": {    }
  },
  "id": {  }
}
```

> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

## NextcloudMcpAdminController

---
### apps

> BASIC

**Path:** /api/v1/admin/apps

**Method:** GET


> REQUEST



> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### disabledApps

> BASIC

**Path:** /api/v1/admin/apps/disabled

**Method:** GET


> REQUEST



> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### enabledApps

> BASIC

**Path:** /api/v1/admin/apps/enabled

**Method:** GET


> REQUEST



> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### app

> BASIC

**Path:** /api/v1/admin/apps/{appId}

**Method:** GET


> REQUEST

**Path Params:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| appId |  | NO |  |


> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### disableApp

> BASIC

**Path:** /api/v1/admin/apps/{appId}/disable

**Method:** POST


> REQUEST

**Path Params:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| appId |  | NO |  |


> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### enableApp

> BASIC

**Path:** /api/v1/admin/apps/{appId}/enable

**Method:** POST


> REQUEST

**Path Params:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| appId |  | NO |  |


> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### groups

> BASIC

**Path:** /api/v1/admin/groups

**Method:** GET


> REQUEST

**Query:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| search |  | NO |  |
| limit |  | NO |  |
| offset |  | NO |  |


> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### createGroup

> BASIC

**Path:** /api/v1/admin/groups

**Method:** POST


> REQUEST

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| Content-Type | application/json | NO |  |

**Request Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| groupId | string |  |


**Request Demo:**

```json
{
  "groupId": ""
}
```

> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### group

> BASIC

**Path:** /api/v1/admin/groups/{groupId}

**Method:** GET


> REQUEST

**Path Params:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| groupId |  | NO |  |


> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### patchGroup

> BASIC

**Path:** /api/v1/admin/groups/{groupId}

**Method:** PATCH


> REQUEST

**Path Params:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| groupId |  | NO |  |

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| Content-Type | application/json | NO |  |

**Request Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| displayName | string |  |


**Request Demo:**

```json
{
  "displayName": ""
}
```

> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### putGroup

> BASIC

**Path:** /api/v1/admin/groups/{groupId}

**Method:** PUT


> REQUEST

**Path Params:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| groupId |  | NO |  |

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| Content-Type | application/json | NO |  |

**Request Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| displayName | string |  |


**Request Demo:**

```json
{
  "displayName": ""
}
```

> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### deleteGroup

> BASIC

**Path:** /api/v1/admin/groups/{groupId}

**Method:** DELETE


> REQUEST

**Path Params:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| groupId |  | NO |  |


> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### groupSubadmins

> BASIC

**Path:** /api/v1/admin/groups/{groupId}/subadmins

**Method:** GET


> REQUEST

**Path Params:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| groupId |  | NO |  |


> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### backgroundJobs

> BASIC

**Path:** /api/v1/admin/occ/background-jobs

**Method:** POST


> REQUEST



> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### configGet

> BASIC

**Path:** /api/v1/admin/occ/config-get

**Method:** POST


> REQUEST

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| Content-Type | application/json | NO |  |

**Request Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| app | string |  |
| key | string |  |


**Request Demo:**

```json
{
  "app": "",
  "key": ""
}
```

> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### configSet

> BASIC

**Path:** /api/v1/admin/occ/config-set

**Method:** POST


> REQUEST

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| Content-Type | application/json | NO |  |

**Request Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| app | string |  |
| key | string |  |
| value | string |  |


**Request Demo:**

```json
{
  "app": "",
  "key": "",
  "value": ""
}
```

> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### filesScan

> BASIC

**Path:** /api/v1/admin/occ/files-scan

**Method:** POST


> REQUEST

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| Content-Type | application/json | NO |  |

**Request Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| userId | string |  |


**Request Demo:**

```json
{
  "userId": ""
}
```

> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### maintenanceMode

> BASIC

**Path:** /api/v1/admin/occ/maintenance-mode

**Method:** POST


> REQUEST

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| Content-Type | application/json | NO |  |

**Request Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| enabled | boolean |  |


**Request Demo:**

```json
{
  "enabled": false
}
```

> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### recoverAdminGroup

> BASIC

**Path:** /api/v1/admin/occ/recover-admin-group

**Method:** POST


> REQUEST

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| Content-Type | application/json | NO |  |

**Request Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| userId | string |  |


**Request Demo:**

```json
{
  "userId": ""
}
```

> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### users

> BASIC

**Path:** /api/v1/admin/users

**Method:** GET


> REQUEST

**Query:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| search |  | NO |  |
| limit |  | NO |  |
| offset |  | NO |  |


> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### createUser

> BASIC

**Path:** /api/v1/admin/users

**Method:** POST


> REQUEST

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| Content-Type | application/json | NO |  |

**Request Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| userId | string |  |
| password | string |  |
| displayName | string |  |
| email | string |  |
| groups | string[] |  |
| subadmins | string[] |  |
| quota | string |  |
| language | string |  |


**Request Demo:**

```json
{
  "userId": "",
  "password": "",
  "displayName": "",
  "email": "",
  "groups": [
    ""
  ],
  "subadmins": [
    ""
  ],
  "quota": "",
  "language": ""
}
```

> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### user

> BASIC

**Path:** /api/v1/admin/users/{userId}

**Method:** GET


> REQUEST

**Path Params:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| userId |  | NO |  |


> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### patchUser

> BASIC

**Path:** /api/v1/admin/users/{userId}

**Method:** PATCH


> REQUEST

**Path Params:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| userId |  | NO |  |

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| Content-Type | application/json | NO |  |

**Request Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string |  |
| value | string |  |


**Request Demo:**

```json
{
  "key": "",
  "value": ""
}
```

> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### putUser

> BASIC

**Path:** /api/v1/admin/users/{userId}

**Method:** PUT


> REQUEST

**Path Params:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| userId |  | NO |  |

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| Content-Type | application/json | NO |  |

**Request Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string |  |
| value | string |  |


**Request Demo:**

```json
{
  "key": "",
  "value": ""
}
```

> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### deleteUser

> BASIC

**Path:** /api/v1/admin/users/{userId}

**Method:** DELETE


> REQUEST

**Path Params:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| userId |  | NO |  |


> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### disableUser

> BASIC

**Path:** /api/v1/admin/users/{userId}/disable

**Method:** PUT


> REQUEST

**Path Params:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| userId |  | NO |  |


> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### enableUser

> BASIC

**Path:** /api/v1/admin/users/{userId}/enable

**Method:** PUT


> REQUEST

**Path Params:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| userId |  | NO |  |


> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### userGroups

> BASIC

**Path:** /api/v1/admin/users/{userId}/groups

**Method:** GET


> REQUEST

**Path Params:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| userId |  | NO |  |


> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### addUserToGroup

> BASIC

**Path:** /api/v1/admin/users/{userId}/groups

**Method:** POST


> REQUEST

**Path Params:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| userId |  | NO |  |

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| Content-Type | application/json | NO |  |

**Request Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| groupId | string |  |


**Request Demo:**

```json
{
  "groupId": ""
}
```

> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### removeUserFromGroup

> BASIC

**Path:** /api/v1/admin/users/{userId}/groups/{groupId}

**Method:** DELETE


> REQUEST

**Path Params:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| userId |  | NO |  |
| groupId |  | NO |  |


> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### userSubadmins

> BASIC

**Path:** /api/v1/admin/users/{userId}/subadmins

**Method:** GET


> REQUEST

**Path Params:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| userId |  | NO |  |


> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### promoteSubadmin

> BASIC

**Path:** /api/v1/admin/users/{userId}/subadmins

**Method:** POST


> REQUEST

**Path Params:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| userId |  | NO |  |

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| Content-Type | application/json | NO |  |

**Request Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| groupId | string |  |


**Request Demo:**

```json
{
  "groupId": ""
}
```

> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### demoteSubadmin

> BASIC

**Path:** /api/v1/admin/users/{userId}/subadmins/{groupId}

**Method:** DELETE


> REQUEST

**Path Params:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| userId |  | NO |  |
| groupId |  | NO |  |


> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### resendWelcomeEmail

> BASIC

**Path:** /api/v1/admin/users/{userId}/welcome

**Method:** POST


> REQUEST

**Path Params:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| userId |  | NO |  |


> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

## NextcloudMcpApiController

---
### Lists configured Nextcloud accounts that the server can use for tool calls.

> BASIC

**Path:** /api/v1/accounts

**Method:** GET

**Desc:**

Lists configured Nextcloud accounts that the server can use for tool calls.


> REQUEST



> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### Registers a local MCP account record in the configured local account store.

> BASIC

**Path:** /api/v1/accounts

**Method:** POST

**Desc:**

Registers a local MCP account record in the configured local account store.


> REQUEST

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| Content-Type | application/json | NO |  |

**Request Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| accountId | string |  |
| nextcloudAccountId | string |  |
| baseUrl | string |  |
| username | string |  |
| appPassword | string |  |
| displayName | string |  |
| email | string |  |
| defaultAccount | boolean |  |
| admin | boolean |  |
| enabled | boolean |  |
| scopes | string[] |  |


**Request Demo:**

```json
{
  "accountId": "",
  "nextcloudAccountId": "",
  "baseUrl": "",
  "username": "",
  "appPassword": "",
  "displayName": "",
  "email": "",
  "defaultAccount": false,
  "admin": false,
  "enabled": false,
  "scopes": [
    ""
  ]
}
```

> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### Validates that a configured account can authenticate against Nextcloud.

> BASIC

**Path:** /api/v1/accounts/test

**Method:** POST

**Desc:**

Validates that a configured account can authenticate against Nextcloud.


> REQUEST

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| Content-Type | application/json | NO |  |

**Request Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| accountId | string |  |


**Request Demo:**

```json
{
  "accountId": ""
}
```

> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### Reads one configured MCP account.

> BASIC

**Path:** /api/v1/accounts/{accountId}

**Method:** GET

**Desc:**

Reads one configured MCP account.


> REQUEST

**Path Params:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| accountId |  | NO | local account key |


> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### Updates non-secret local MCP account metadata and access flags.

> BASIC

**Path:** /api/v1/accounts/{accountId}

**Method:** PATCH

**Desc:**

Updates non-secret local MCP account metadata and access flags.


> REQUEST

**Path Params:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| accountId |  | NO | local account key |

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| Content-Type | application/json | NO |  |

**Request Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| accountId | string |  |
| nextcloudAccountId | string |  |
| baseUrl | string |  |
| username | string |  |
| displayName | string |  |
| email | string |  |
| defaultAccount | boolean |  |
| admin | boolean |  |
| enabled | boolean |  |
| scopes | string[] |  |


**Request Demo:**

```json
{
  "accountId": "",
  "nextcloudAccountId": "",
  "baseUrl": "",
  "username": "",
  "displayName": "",
  "email": "",
  "defaultAccount": false,
  "admin": false,
  "enabled": false,
  "scopes": [
    ""
  ]
}
```

> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### Deletes a local MCP account record from the configured account store.

> BASIC

**Path:** /api/v1/accounts/{accountId}

**Method:** DELETE

**Desc:**

Deletes a local MCP account record from the configured account store.


> REQUEST

**Path Params:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| accountId |  | NO | local account key |


> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### Rotates or sets the stored app password for a local MCP account.

> BASIC

**Path:** /api/v1/accounts/{accountId}/app-password

**Method:** POST

**Desc:**

Rotates or sets the stored app password for a local MCP account.


> REQUEST

**Path Params:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| accountId |  | NO | local account key |

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| Content-Type | application/json | NO |  |

**Request Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| appPassword | string |  |


**Request Demo:**

```json
{
  "appPassword": ""
}
```

> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### Disables a local MCP account record.

> BASIC

**Path:** /api/v1/accounts/{accountId}/disable

**Method:** POST

**Desc:**

Disables a local MCP account record.


> REQUEST

**Path Params:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| accountId |  | NO | local account key |


> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### Enables a local MCP account record.

> BASIC

**Path:** /api/v1/accounts/{accountId}/enable

**Method:** POST

**Desc:**

Enables a local MCP account record.


> REQUEST

**Path Params:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| accountId |  | NO | local account key |


> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### Marks a local MCP account as the default account and clears other local defaults.

> BASIC

**Path:** /api/v1/accounts/{accountId}/make-default

**Method:** POST

**Desc:**

Marks a local MCP account as the default account and clears other local defaults.


> REQUEST

**Path Params:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| accountId |  | NO | local account key |


> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### Lists tools enabled for the selected or default account.

> BASIC

**Path:** /api/v1/tools

**Method:** GET

**Desc:**

Lists tools enabled for the selected or default account.


> REQUEST

**Query:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| accountId |  | NO | optional account id used to evaluate account-specific tool availability |


> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### Invokes a Nextcloud MCP tool through the REST API.

> BASIC

**Path:** /api/v1/tools/call

**Method:** POST

**Desc:**

Invokes a Nextcloud MCP tool through the REST API.


> REQUEST

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| Content-Type | application/json | NO |  |

**Request Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| tool | string |  |
| accountId | string |  |
| invocationId | string |  |
| arguments | map |  |


**Request Demo:**

```json
{
  "tool": "",
  "accountId": "",
  "invocationId": "",
  "arguments": {
    "": {    }
  }
}
```

> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### Reports server readiness, loaded configuration status, account count, and tool count.

> BASIC

**Path:** /health

**Method:** GET

**Desc:**

Reports server readiness, loaded configuration status, account count, and tool count.


> REQUEST



> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### Lists MCP tools through the compatibility route while reusing the REST tool listing behavior.

> BASIC

**Path:** /mcp/tools

**Method:** GET

**Desc:**

Lists MCP tools through the compatibility route while reusing the REST tool listing behavior.


> REQUEST

**Query:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| accountId |  | NO | optional account id used to evaluate account-specific tool availability |


> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```

---
### Invokes a Nextcloud MCP tool through the compatibility route.

> BASIC

**Path:** /mcp/tools/call

**Method:** POST

**Desc:**

Invokes a Nextcloud MCP tool through the compatibility route.


> REQUEST

**Headers:**

| name | value | required | desc |
| ------------ | ------------ | ------------ | ------------ |
| Content-Type | application/json | NO |  |

**Request Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| tool | string |  |
| accountId | string |  |
| invocationId | string |  |
| arguments | map |  |


**Request Demo:**

```json
{
  "tool": "",
  "accountId": "",
  "invocationId": "",
  "arguments": {
    "": {    }
  }
}
```

> RESPONSE

**Body:**

| name | type | desc |
| ------------ | ------------ | ------------ |
| key | string | |
| value | object | |


**Response Demo:**

```json
{
  "": {  }
}
```
