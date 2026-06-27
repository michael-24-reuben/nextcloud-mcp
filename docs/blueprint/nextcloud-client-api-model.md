# 0. Core connection model

## Auth

Use **Basic Auth** with:

```text
username = Nextcloud user ID or login name
password = app password
```

For WebDAV, Nextcloud’s base authenticated DAV endpoint is:

```text
https://YOUR_DOMAIN/remote.php/dav
```

For user files:

```text
https://YOUR_DOMAIN/remote.php/dav/files/{user}/
```

Nextcloud states that authenticated WebDAV requests use `/remote.php/dav`, and file operations normally live under `/remote.php/dav/files/{user}/...`; app passwords are recommended for WebDAV clients/scripts, especially with 2FA or external auth. ([Nextcloud][1])

## OCS headers

For OCS APIs, include:

```http
OCS-APIRequest: true
Accept: application/json
```

OCS APIs require authentication unless documented otherwise, and the docs recommend `Accept: application/json` for consistent JSON responses. ([Nextcloud][2])

---

# 1. Login / credential generation APIs

These matter if your client/app should connect a user without manually copying an app password.

| Function                              |   Method | Endpoint                          | Notes                                                                       |
| ------------------------------------- | -------: | --------------------------------- | --------------------------------------------------------------------------- |
| Start browser login flow v2           |   `POST` | `/index.php/login/v2`             | Returns login URL + polling endpoint                                        |
| Poll login result                     |   `POST` | `/login/v2/poll`                  | Returns `server`, `loginName`, `appPassword` after user approves            |
| Convert real password to app password |    `GET` | `/ocs/v2.php/core/getapppassword` | Only works when authenticated with real password, not existing app password |
| Delete current app password           | `DELETE` | `/ocs/v2.php/core/apppassword`    | Used when removing the client/account                                       |

Nextcloud’s Login Flow v2 is intended for desktop/client apps: anonymous `POST /index.php/login/v2`, open the returned login URL, poll the endpoint, then receive `server`, `loginName`, and `appPassword`. ([Nextcloud][3])

For your own server automation, the simpler setup is still: **create app password manually once** and store it in your secret vault.

---

# 2. WebDAV file API

This is the main API for your automation user.

Base:

```text
/remote.php/dav/files/{user}/
```

## File/folder functions

| Function                |      Method | Endpoint shape                          | Main headers/body                                |
| ----------------------- | ----------: | --------------------------------------- | ------------------------------------------------ |
| List folder             |  `PROPFIND` | `/remote.php/dav/files/{user}/{path}`   | `Depth: 1` or omit for children                  |
| Get file metadata only  |  `PROPFIND` | `/remote.php/dav/files/{user}/{path}`   | `Depth: 0`                                       |
| Download file           |       `GET` | `/remote.php/dav/files/{user}/{file}`   | none                                             |
| Download folder archive |       `GET` | `/remote.php/dav/files/{user}/{folder}` | `Accept: application/zip` or `application/x-tar` |
| Upload/overwrite file   |       `PUT` | `/remote.php/dav/files/{user}/{file}`   | raw file body                                    |
| Create folder           |     `MKCOL` | `/remote.php/dav/files/{user}/{folder}` | none                                             |
| Delete file/folder      |    `DELETE` | `/remote.php/dav/files/{user}/{path}`   | recursive for folders                            |
| Move/rename             |      `MOVE` | source path                             | `Destination: full target URL`                   |
| Copy                    |      `COPY` | source path                             | `Destination: full target URL`                   |
| Set favorite            | `PROPPATCH` | file/folder path                        | XML body with `oc:favorite`                      |
| List favorites          |    `REPORT` | folder/root path                        | XML filter body                                  |

Nextcloud’s WebDAV cheat sheet lists these core methods: `PROPFIND`, `REPORT`, `GET`, `PUT`, `MKCOL`, `DELETE`, `MOVE`, `COPY`, and `PROPPATCH`; it also documents special upload headers like `X-OC-MTime`, `X-OC-CTime`, `OC-Checksum`, and `X-NC-WebDAV-AutoMkcol`. ([Nextcloud][4])

## Useful properties to request with `PROPFIND`

For a fully implemented client, request these:

```xml
<d:getlastmodified/>
<d:getcontentlength/>
<d:getcontenttype/>
<d:resourcetype/>
<d:getetag/>
<oc:fileid/>
<oc:size/>
<oc:permissions/>
<oc:favorite/>
<oc:tags/>
<oc:comments-href/>
<oc:comments-count/>
<oc:comments-unread/>
<oc:owner-id/>
<oc:owner-display-name/>
<nc:has-preview/>
```

Nextcloud documents extra properties such as tags, favorite state, comments URL/count/unread count, owner ID, owner display name, permissions, checksums, and preview status. ([Nextcloud][4])

---

# 3. Bulk upload API

Use this when uploading many small files.

| Function               |            Method | Endpoint               | Notes                                     |
| ---------------------- | ----------------: | ---------------------- | ----------------------------------------- |
| Bulk upload many files | multipart request | `/remote.php/dav/bulk` | Each part has destination path + metadata |

Per-file part headers:

```http
Content-Length: ...
Content-Type: ...
X-File-MD5: ...
X-File-Mtime: ...
X-File-Path: /target/path/file.txt
```

Nextcloud’s bulk upload endpoint is `/remote.php/dav/bulk`, available to registered users, and is designed to improve throughput when uploading many small files. ([Nextcloud][5])

For your Instagram ingest flow, normal `PUT` is enough initially. Add bulk upload later if you package many JSON/assets individually.

---

# 4. WebDAV search API

Use this for file discovery, filtering, and “find by file ID”.

| Function                      |   Method | Endpoint           | Body                       |
| ----------------------------- | -------: | ------------------ | -------------------------- |
| Search files                  | `SEARCH` | `/remote.php/dav/` | XML `d:searchrequest`      |
| Search by name/mime/size/date | `SEARCH` | `/remote.php/dav/` | WebDAV basicsearch         |
| Find by file ID               | `SEARCH` | `/remote.php/dav/` | filter on `oc:fileid`      |
| List recent files             | `SEARCH` | `/remote.php/dav/` | order by `getlastmodified` |

Nextcloud implements RFC 5323 WebDAV search. The search request is sent to `/remote.php/dav/` with `Content-Type: text/xml`; scopes for file search should start with `files/$username`. ([Nextcloud][6])

---

# 5. Trashbin API

Base:

```text
/remote.php/dav/trashbin/{user}/
```

| Function            |     Method | Endpoint                                | Notes                         |
| ------------------- | ---------: | --------------------------------------- | ----------------------------- |
| List trash          | `PROPFIND` | `/remote.php/dav/trashbin/{user}/trash` | Returns deleted items         |
| Restore item        |     `MOVE` | trash item path → `/restore`            | Restores to original location |
| Delete trashed item |   `DELETE` | trash item path                         | Permanently removes item      |
| Empty trash         |   `DELETE` | `/remote.php/dav/trashbin/{user}/trash` | Clears trash                  |

Nextcloud exposes the user trashbin through WebDAV and provides extra properties for original filename, original location, and deletion time. ([Nextcloud][7])

---

# 6. Versions API

Base:

```text
/remote.php/dav/versions/{user}/
```

| Function        |     Method | Endpoint                                            | Notes                        |
| --------------- | ---------: | --------------------------------------------------- | ---------------------------- |
| List versions   | `PROPFIND` | `/remote.php/dav/versions/{user}/versions/{fileId}` | Version names are timestamps |
| Restore version |     `MOVE` | version path → `/restore`                           | Restores version             |

Nextcloud exposes file versions through WebDAV at `/remote.php/dav/versions/USER/versions/FILEID`, and restore is done by moving a version to the special restore folder. ([Nextcloud][8])

---

# 7. Comments API

Base:

```text
/remote.php/dav/comments/files/{fileId}
```

| Function        |      Method | Endpoint                                              | Notes               |
| --------------- | ----------: | ----------------------------------------------------- | ------------------- |
| List comments   |  `PROPFIND` | `/remote.php/dav/comments/files/{fileId}`             | Request properties  |
| Create comment  |      `POST` | `/remote.php/dav/comments/files/{fileId}`             | Add comment         |
| Search comments |    `REPORT` | `/remote.php/dav/comments/files/{fileId}`             | XML report          |
| Update comment  | `PROPPATCH` | `/remote.php/dav/comments/files/{fileId}/{commentId}` | Edit comment        |
| Delete comment  |    `DELETE` | `/remote.php/dav/comments/files/{fileId}/{commentId}` | Remove comment      |
| Mark read       | `PROPPATCH` | object endpoint                                       | Update `readMarker` |

Nextcloud documents comments over WebDAV; the object endpoint supports `POST`, `PROPFIND`, `PROPPATCH`, and `REPORT`, while the individual comment endpoint supports update, delete, and property listing. ([Nextcloud][9])

---

# 8. Sharing API

Base:

```text
/ocs/v2.php/apps/files_sharing/api/v1
```

Required header:

```http
OCS-APIRequest: true
```

## Share functions

| Function                    |   Method | Endpoint                        | Notes                                     |
| --------------------------- | -------: | ------------------------------- | ----------------------------------------- |
| List user’s shares          |    `GET` | `/shares`                       | All shares owned by user                  |
| List shares for file/folder |    `GET` | `/shares?path=/path`            | Optional `reshares`, `subfiles`           |
| Get known share             |    `GET` | `/shares/{share_id}`            | Read share metadata                       |
| Create share                |   `POST` | `/shares`                       | User/group/public/email/federated/etc.    |
| Update share                |    `PUT` | `/shares/{share_id}`            | Permissions, password, expiry, note, etc. |
| Delete/unshare              | `DELETE` | `/shares/{share_id}`            | Remove share                              |
| Send share email            |   `POST` | `/shares/{share_id}/send-email` | Send recipient email                      |

The Share API base URL is `/ocs/v2.php/apps/files_sharing/api/v1`, and all OCS endpoints require `OCS-APIRequest: true`. It supports listing shares, fetching a specific share, creating shares, updating shares, deleting shares, and sending share emails. ([Nextcloud][10])

## Share types

Common `shareType` values:

| Value | Meaning               |
| ----: | --------------------- |
|   `0` | User                  |
|   `1` | Group                 |
|   `3` | Public link           |
|   `4` | Email                 |
|   `6` | Federated cloud share |
|   `7` | Circle                |
|  `10` | Talk conversation     |

## Permissions bitmask

| Value | Meaning |
| ----: | ------- |
|   `1` | Read    |
|   `2` | Update  |
|   `4` | Create  |
|   `8` | Delete  |
|  `16` | Share   |
|  `31` | All     |

Nextcloud documents these exact share types and permission values for the OCS Share API. ([Nextcloud][10])

---

# 9. Sharee discovery API

Base:

```text
/ocs/v1.php/apps/files_sharing/api/v1
```

| Function                | Method | Endpoint                             | Notes                   |
| ----------------------- | -----: | ------------------------------------ | ----------------------- |
| Search share recipients |  `GET` | `/sharees?search=TERM`               | Finds users/groups/etc. |
| Recommended sharees     |  `GET` | `/sharees_recommended?itemType=file` | Suggested recipients    |

The Sharee API base URL is `/ocs/v1.php/apps/files_sharing/api/v1`; it supports searching sharees with `search`, `lookup`, `perPage`, and `itemType`, plus recommended sharees. ([Nextcloud][11])

Use this before creating a user/group share so your client can resolve valid recipients.

---

# 10. User metadata / capabilities API

Base examples:

```text
/ocs/v1.php/cloud/user
/ocs/v1.php/cloud/users/{userId}
/ocs/v1.php/cloud/capabilities
```

| Function             | Method | Endpoint                           | Non-admin behavior                       |
| -------------------- | -----: | ---------------------------------- | ---------------------------------------- |
| Get current user     |  `GET` | `/ocs/v1.php/cloud/user`           | Safe                                     |
| Get user metadata    |  `GET` | `/ocs/v1.php/cloud/users/{userId}` | Normal user can only access own metadata |
| Get capabilities     |  `GET` | `/ocs/v1.php/cloud/capabilities`   | Shows enabled app capabilities           |
| Theming capabilities |  `GET` | OCS theming endpoint               | Useful for client branding               |

The OCS overview states that normal users can access only their own user metadata, while admin users can see all users. ([Nextcloud][2])

This is important for your implementation: your non-admin API wrapper should call `cloud/user` after login to resolve the actual user ID used in WebDAV paths.

---

# 11. User status API

Base:

```text
/ocs/v2.php/apps/user_status/api/v1/user_status
```

| Function                |   Method | Endpoint                  | Notes                                           |
| ----------------------- | -------: | ------------------------- | ----------------------------------------------- |
| Fetch own status        |    `GET` | `/`                       | Current user status                             |
| Set own status type     |    `PUT` | `/status`                 | `online`, `away`, `dnd`, `invisible`, `offline` |
| Set predefined message  |    `PUT` | `/message/predefined`     | Uses predefined message ID                      |
| Set custom message      |    `PUT` | `/message/custom`         | Custom status message                           |
| Clear message           | `DELETE` | `/message`                | Clear status message                            |
| Get predefined statuses |    `GET` | `/predefined_statuses`    | Available presets                               |
| Retrieve statuses       |    `GET` | `/statuses/{userId}` etc. | Depends on endpoint/capability                  |

Nextcloud’s Status API lets clients access and modify user status through OCS, with base URL `/ocs/v2.php/apps/user_status/api/v1/user_status`; setting status supports `online`, `away`, `dnd`, `invisible`, and `offline`. ([Nextcloud][12])

This is optional unless your tool wants to mark automation users as “busy”, “syncing”, etc.

---

# 12. User preferences API

Base:

```text
/ocs/v2.php/apps/provisioning_api/api/v1/config/users/
```

| Function                    |   Method | Endpoint               | Notes               |
| --------------------------- | -------: | ---------------------- | ------------------- |
| Set preference              |   `POST` | `/{appId}/{configKey}` | Body: `configValue` |
| Set multiple preferences    |   `POST` | `/{appId}`             | Body: config map    |
| Delete preference           | `DELETE` | `/{appId}/{configKey}` | Remove one key      |
| Delete multiple preferences | `DELETE` | `/{appId}`             | Body: config keys   |

The User Preferences API allows setting and deleting user preferences through OCS; the base URL is `/ocs/v2.php/apps/provisioning_api/api/v1/config/users/`, and it requires a user-authenticated request. ([Nextcloud][13])

Use this carefully. Some preferences may be blocked or invalid.

---

# 13. Public share WebDAV API

This is for accessing a public link share, not for your authenticated service account.

Base:

```text
/public.php/dav/files/{share_token}
```

| Function                      |     Method | Endpoint                                        | Notes                             |
| ----------------------------- | ---------: | ----------------------------------------------- | --------------------------------- |
| List public share             | `PROPFIND` | `/public.php/dav/files/{share_token}`           | May need headers                  |
| Download public file          |      `GET` | same                                            | No auth unless password protected |
| Upload to public upload share |      `PUT` | public share path                               | Requires public upload enabled    |
| Auth for passworded share     | Basic Auth | username `anonymous`, password = share password | For protected shares              |

Nextcloud documents `/public.php/dav/files/{share_token}` for public link shares; password-protected shares use Basic Auth with username `anonymous`, and non-GET requests may require `X-Requested-With: XMLHttpRequest`. ([Nextcloud][1])

---

# 14. APIs I would exclude from a normal automation user initially

These either require admin privileges, app-specific setup, or are not central to file automation:

| API                                       | Why not first                                  |
| ----------------------------------------- | ---------------------------------------------- |
| Provisioning user/group admin APIs        | Usually admin-only                             |
| Server app management                     | Admin-only                                     |
| Assistant/Text/Image/Task processing APIs | Depends on enabled Nextcloud AI apps           |
| FullTextSearch collection API             | Requires FullTextSearch apps/config            |
| Remote wipe                               | Client-device lifecycle, not file automation   |
| Notifications send API                    | App/server-side integration use case           |
| Calendar/Contacts/CardDAV/CalDAV          | Useful later, separate domain from file ingest |

---

# Recommended implementation modules

For your **non-admin API user client**, I’d implement in this order:

```text
NextcloudClient
├─ Auth
│  ├─ basicAuthHeaders()
│  ├─ ocsHeaders()
│  └─ resolveCurrentUser()
│
├─ FilesWebDav
│  ├─ list(path)
│  ├─ stat(path)
│  ├─ upload(path, bytes/file)
│  ├─ download(path)
│  ├─ mkdir(path)
│  ├─ mkdirs(path)
│  ├─ delete(path)
│  ├─ move(source, target)
│  ├─ copy(source, target)
│  ├─ favorite(path, boolean)
│  └─ search(query)
│
├─ SharesOcs
│  ├─ listShares()
│  ├─ getShares(path)
│  ├─ createPublicLink(path, permissions, password?, expireDate?)
│  ├─ shareWithUser(path, userId, permissions)
│  ├─ updateShare(shareId, patch)
│  └─ deleteShare(shareId)
│
├─ ShareesOcs
│  ├─ searchSharees(term)
│  └─ recommended(itemType)
│
├─ TrashbinWebDav
│  ├─ listTrash()
│  ├─ restore(trashPath)
│  ├─ deleteTrashItem(trashPath)
│  └─ emptyTrash()
│
├─ VersionsWebDav
│  ├─ listVersions(fileId)
│  └─ restoreVersion(versionPath)
│
└─ CommentsWebDav
   ├─ listComments(fileId)
   ├─ createComment(fileId, message)
   ├─ updateComment(fileId, commentId, message)
   └─ deleteComment(fileId, commentId)
```

For your use case, the MVP should be:

```text
Auth + FilesWebDav + SharesOcs
```

Then add:

```text
Search + Trashbin + Versions + Comments
```

That gives you a fully capable **non-admin Nextcloud API account** without touching server admin privileges.

[1]: https://docs.nextcloud.com/server/stable/developer_manual/client_apis/WebDAV/basic.html "Basic File & Folder Operations — Nextcloud 34 Developer Manual"
[2]: https://docs.nextcloud.com/server/stable/developer_manual/client_apis/OCS/ocs-api-overview.html "OCS APIs overview — Nextcloud 34 Developer Manual"
[3]: https://docs.nextcloud.com/server/stable/developer_manual/client_apis/LoginFlow/index.html "Login Flow — Nextcloud 34 Developer Manual"
[4]: https://docs.nextcloud.com/server/stable/developer_manual/client_apis/WebDAV/basic.html?utm_source=chatgpt.com "Basic File & Folder Operations"
[5]: https://docs.nextcloud.com/server/stable/developer_manual/client_apis/WebDAV/bulkupload.html "File bulk upload — Nextcloud 34 Developer Manual"
[6]: https://docs.nextcloud.com/server/stable/developer_manual/client_apis/WebDAV/search.html "Search — Nextcloud 34 Developer Manual"
[7]: https://docs.nextcloud.com/server/stable/developer_manual/client_apis/WebDAV/trashbin.html?utm_source=chatgpt.com "Trashbin — Nextcloud 34 Developer Manual"
[8]: https://docs.nextcloud.com/server/stable/developer_manual/client_apis/WebDAV/versions.html?utm_source=chatgpt.com "Versions — Nextcloud 34 Developer Manual"
[9]: https://docs.nextcloud.com/server/stable/developer_manual/client_apis/WebDAV/comments.html?utm_source=chatgpt.com "Comments — Nextcloud 34 Developer Manual"
[10]: https://docs.nextcloud.com/server/stable/developer_manual/client_apis/OCS/ocs-share-api.html "OCS Share API — Nextcloud 34 Developer Manual"
[11]: https://docs.nextcloud.com/server/stable/developer_manual/client_apis/OCS/ocs-sharee-api.html "OCS Sharee API — Nextcloud 34 Developer Manual"
[12]: https://docs.nextcloud.com/server/stable/developer_manual/client_apis/OCS/ocs-status-api.html "OCS Status API — Nextcloud 34 Developer Manual"
[13]: https://docs.nextcloud.com/server/stable/developer_manual/client_apis/OCS/ocs-user-preferences-api.html "OCS user preferences API — Nextcloud 34 Developer Manual"
