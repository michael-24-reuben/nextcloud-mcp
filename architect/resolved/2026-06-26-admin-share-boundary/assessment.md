# Assessment

Admin-authenticated share calls are not admin provisioning. They are normal OCS share API calls under `/ocs/v2.php/apps/files_sharing/api/v1`.

The correct boundary is to reuse the normal `NextcloudClient` share implementation with admin credentials when admin-authenticated share visibility is needed. `NextcloudAdminClient` should not imply cross-user WebDAV control or ownership of file-space operations.
