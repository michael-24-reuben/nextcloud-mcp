Yes. The **client API will function normally under an admin account**, but the permission model changes by endpoint:

| API area                        | Normal user                                      | Admin user                                                                                      |
| ------------------------------- | ------------------------------------------------ | ----------------------------------------------------------------------------------------------- |
| WebDAV files                    | Own files + shared files                         | Still operates as the authenticated user’s file space unless using share/impersonation patterns |
| Shares API                      | Shares the user owns or has rights to manage     | Broader visibility/control depending on server settings                                         |
| User provisioning               | Mostly own profile fields                        | Create/edit/delete users, quotas, groups, subadmins                                             |
| Group provisioning              | Usually restricted                               | Create/edit/delete groups, list members, manage subadmins                                       |
| App provisioning                | Not available                                    | List apps, inspect app info, enable/disable apps                                                |
| Server config / OCC-level tasks | Not generally exposed through public client APIs | Usually still better handled through `occ`, not HTTP                                            |

So: **admin credentials can use all normal client APIs**, but the truly “admin” part is mostly the **OCS Provisioning API** and server-side `occ`.

---

# Admin API connection model

Use the same auth shape:

```text
Authorization: Basic base64(adminUser:adminAppPassword)
OCS-APIRequest: true
Accept: application/json
```

Base OCS route:

```text
https://YOUR_DOMAIN/ocs/v1.php/cloud/...
```

OCS requests require authentication, normally Basic Auth or valid session cookies, unless the specific endpoint says otherwise. ([Nextcloud][1])

For admin automation, still use an **app password**, not your main login password.

---

# 1. Provisioning API overview

The Provisioning API lets external systems create, edit, delete, and query users; query, set, and remove groups; set quota; query storage usage; manage group-admin scope; and query/enable/disable apps remotely. ([Nextcloud][2])

Recommended implementation module:

```text
NextcloudAdminClient
├─ AdminAuth
├─ UsersProvisioning
├─ GroupsProvisioning
├─ SubadminsProvisioning
├─ AppsProvisioning
├─ SharesAdminSupport
└─ OccBridgeOptional
```

---

# 2. Admin user APIs

Base:

```text
/ocs/v1.php/cloud/users
```

## User management functions

| Function                   |   Method | Endpoint                                     | Body / Query                                                                                |
| -------------------------- | -------: | -------------------------------------------- | ------------------------------------------------------------------------------------------- |
| Create user                |   `POST` | `/ocs/v1.php/cloud/users`                    | `userid`, `password`, `displayName`, `email`, `groups[]`, `subadmin[]`, `quota`, `language` |
| Search/list users          |    `GET` | `/ocs/v1.php/cloud/users`                    | `search`, `limit`, `offset`                                                                 |
| Get user data              |    `GET` | `/ocs/v1.php/cloud/users/{userid}`           | none                                                                                        |
| Edit user field            |    `PUT` | `/ocs/v1.php/cloud/users/{userid}`           | `key`, `value`                                                                              |
| Get editable fields        |    `GET` | `/ocs/v1.php/cloud/user/fields`              | none                                                                                        |
| Disable user               |    `PUT` | `/ocs/v1.php/cloud/users/{userid}/disable`   | none                                                                                        |
| Enable user                |    `PUT` | `/ocs/v1.php/cloud/users/{userid}/enable`    | none                                                                                        |
| Delete user                | `DELETE` | `/ocs/v1.php/cloud/users/{userid}`           | none                                                                                        |
| Get user’s groups          |    `GET` | `/ocs/v1.php/cloud/users/{userid}/groups`    | none                                                                                        |
| Add user to group          |   `POST` | `/ocs/v1.php/cloud/users/{userid}/groups`    | `groupid`                                                                                   |
| Remove user from group     | `DELETE` | `/ocs/v1.php/cloud/users/{userid}/groups`    | `groupid`                                                                                   |
| Promote user to subadmin   |   `POST` | `/ocs/v1.php/cloud/users/{userid}/subadmins` | `groupid`                                                                                   |
| Demote user from subadmin  | `DELETE` | `/ocs/v1.php/cloud/users/{userid}/subadmins` | `groupid`                                                                                   |
| Get user’s subadmin groups |    `GET` | `/ocs/v1.php/cloud/users/{userid}/subadmins` | none                                                                                        |
| Resend welcome email       |   `POST` | `/ocs/v1.php/cloud/users/{userid}/welcome`   | none                                                                                        |

Nextcloud’s user provisioning docs list user creation, search, user editing, enable/disable, delete, group membership management, subadmin promotion/demotion, and welcome-email resend under the user instruction set. ([Nextcloud][3])

## Important admin difference

A normal user can edit some of their own fields, such as email, display name, and password. Admins can also edit quota. ([Nextcloud][3])

---

# 3. Admin group APIs

Base:

```text
/ocs/v1.php/cloud/groups
```

| Function            |   Method | Endpoint                                       | Body / Query                |
| ------------------- | -------: | ---------------------------------------------- | --------------------------- |
| Search/list groups  |    `GET` | `/ocs/v1.php/cloud/groups`                     | `search`, `limit`, `offset` |
| Create group        |   `POST` | `/ocs/v1.php/cloud/groups`                     | `groupid`                   |
| Get group members   |    `GET` | `/ocs/v1.php/cloud/groups/{groupid}`           | none                        |
| Get group subadmins |    `GET` | `/ocs/v1.php/cloud/groups/{groupid}/subadmins` | none                        |
| Edit group field    |    `PUT` | `/ocs/v1.php/cloud/groups/{groupid}`           | `key=displayname`, `value`  |
| Delete group        | `DELETE` | `/ocs/v1.php/cloud/groups/{groupid}`           | none                        |

Nextcloud documents group search, group creation, member listing, subadmin listing, display-name editing, and group deletion under the group provisioning instruction set. ([Nextcloud][4])

---

# 4. Admin app APIs

Base:

```text
/ocs/v1.php/cloud/apps
```

| Function            |   Method | Endpoint                         | Body / Query                                   |
| ------------------- | -------: | -------------------------------- | ---------------------------------------------- |
| List installed apps |    `GET` | `/ocs/v1.php/cloud/apps`         | Optional `filter=enabled` or `filter=disabled` |
| Get app info        |    `GET` | `/ocs/v1.php/cloud/apps/{appid}` | none                                           |
| Enable app          |   `POST` | `/ocs/v1.php/cloud/apps/{appid}` | none                                           |
| Disable app         | `DELETE` | `/ocs/v1.php/cloud/apps/{appid}` | none                                           |

The apps instruction set documents listing installed apps, filtering enabled/disabled apps, getting app info, enabling an app, and disabling an app. ([Nextcloud][5])

I would treat app enable/disable as **high-risk** in your wrapper. It can break the server if dependencies or AIO expectations are involved.

---

# 5. Share APIs under admin credentials

These are not a separate “admin provisioning” API, but admin credentials can still call the normal share API.

Base:

```text
/ocs/v2.php/apps/files_sharing/api/v1
```

| Function                                           |   Method | Endpoint                        |
| -------------------------------------------------- | -------: | ------------------------------- |
| List shares owned/visible to authenticated account |    `GET` | `/shares`                       |
| List shares for path                               |    `GET` | `/shares?path=/some/path`       |
| Get share                                          |    `GET` | `/shares/{share_id}`            |
| Create share                                       |   `POST` | `/shares`                       |
| Update share                                       |    `PUT` | `/shares/{share_id}`            |
| Delete share                                       | `DELETE` | `/shares/{share_id}`            |
| Send share email                                   |   `POST` | `/shares/{share_id}/send-email` |

The Share API base is `/ocs/v2.php/apps/files_sharing/api/v1`, and OCS requests require `OCS-APIRequest: true`. ([Nextcloud][6])

For your architecture, keep this separate from provisioning:

```text
AdminProvisioningClient ≠ FileShareClient
```

Admin can create/manage users and groups, but file shares still need careful path ownership handling.

---

# 6. What admin API does **not** fully cover

Some admin operations are better handled by `occ`, especially in your **Nextcloud AIO Docker** setup.

Examples:

| Task                              | Better interface               |
| --------------------------------- | ------------------------------ |
| Maintenance mode                  | `occ maintenance:mode`         |
| App repair / upgrade / migrations | `occ`                          |
| Files scan                        | `occ files:scan`               |
| Background jobs                   | `occ background:*`             |
| Config values                     | `occ config:*`                 |
| Reset lost admin access           | `occ group:adduser admin USER` |
| Deep troubleshooting              | Docker logs + `occ`            |

For AIO, that means commands against:

```powershell
docker exec -u www-data -it nextcloud-aio-nextcloud php occ ...
```

Your wrapper can expose this later as a guarded **OccBridge**, but not as a general-purpose admin HTTP API.

---

# 7. Recommended admin wrapper layout

```text
NextcloudAdminClient
├─ auth/
│  ├─ buildBasicAuth()
│  ├─ buildOcsHeaders()
│  └─ testAdminIdentity()
│
├─ users/
│  ├─ createUser()
│  ├─ listUsers()
│  ├─ getUser()
│  ├─ updateUserField()
│  ├─ getEditableUserFields()
│  ├─ enableUser()
│  ├─ disableUser()
│  ├─ deleteUser()
│  ├─ getUserGroups()
│  ├─ addUserToGroup()
│  ├─ removeUserFromGroup()
│  ├─ promoteSubadmin()
│  ├─ demoteSubadmin()
│  ├─ getSubadminGroups()
│  └─ resendWelcomeEmail()
│
├─ groups/
│  ├─ listGroups()
│  ├─ createGroup()
│  ├─ getGroupMembers()
│  ├─ getGroupSubadmins()
│  ├─ updateGroupDisplayName()
│  └─ deleteGroup()
│
├─ apps/
│  ├─ listApps()
│  ├─ listEnabledApps()
│  ├─ listDisabledApps()
│  ├─ getAppInfo()
│  ├─ enableApp()
│  └─ disableApp()
│
├─ shares/
│  ├─ listShares()
│  ├─ getShare()
│  ├─ createShare()
│  ├─ updateShare()
│  └─ deleteShare()
│
└─ occ/
   ├─ filesScan()
   ├─ maintenanceMode()
   ├─ configGet()
   ├─ configSet()
   └─ groupAddUserAdminRecovery()
```

---

# 8. Risk levels for your implementation

| Risk     | Functions                                                                             |
| -------- | ------------------------------------------------------------------------------------- |
| Low      | list users, get user, list groups, get group members, list apps, get app info         |
| Medium   | create user, edit display name/email, create group, add/remove user from normal group |
| High     | disable user, enable user, quota changes, promote/demote subadmin, delete group       |
| Critical | delete user, add/remove admin group, enable/disable apps, OCC bridge commands         |

For Meshingress-style tool exposure, I would require confirmation for **High** and **Critical** operations.

---

# 9. Minimum admin API implementation

Start with:

```text
AdminAuth
UsersProvisioning.read
UsersProvisioning.create
GroupsProvisioning.read
GroupsProvisioning.membership
```

Then add:

```text
User enable/disable
Quota edits
Subadmin management
Apps read-only
```

Only after that add:

```text
Delete user
Delete group
Enable/disable apps
OCC bridge
```

---

# 10. Architect note

Your uploaded Architect README defines `architect/` as the project’s resumable engineering memory for PRDs, implementation plans, context, decisions, and verification records, with new entries normally starting with `meta.json`, `brief.md`, `todo.md`, and `context.md`. 

A fitting entry name for this work would be:

```text
architect/pending/2026-06-26-nextcloud-admin-api-client/
```

Minimum files:

```text
meta.json
brief.md
prd.md
todo.md
context.md
```

Core scope:

```text
Implement separate non-admin and admin Nextcloud API clients:
- NextcloudUserClient: WebDAV, shares, comments, versions, trashbin
- NextcloudAdminClient: users, groups, subadmins, apps, guarded OCC bridge
```

The split matters: **user API = content operations**, **admin API = identity/server governance**.

[1]: https://docs.nextcloud.com/server/stable/developer_manual/client_apis/OCS/ocs-api-overview.html "OCS APIs overview — Nextcloud 34 Developer Manual"
[2]: https://docs.nextcloud.com/server/stable/admin_manual/configuration_user/user_provisioning_api.html "User provisioning API — Nextcloud 34 Administration Manual"
[3]: https://docs.nextcloud.com/server/stable/admin_manual/configuration_user/instruction_set_for_users.html "Instruction set for users — Nextcloud 34 Administration Manual"
[4]: https://docs.nextcloud.com/server/stable/admin_manual/configuration_user/instruction_set_for_groups.html "Instruction set for groups — Nextcloud 34 Administration Manual"
[5]: https://docs.nextcloud.com/server/stable/admin_manual/configuration_user/instruction_set_for_apps.html "Instruction set for apps — Nextcloud 34 Administration Manual"
[6]: https://docs.nextcloud.com/server/stable/developer_manual/client_apis/OCS/ocs-share-api.html?utm_source=chatgpt.com "OCS Share API — Nextcloud 34 Developer Manual"
