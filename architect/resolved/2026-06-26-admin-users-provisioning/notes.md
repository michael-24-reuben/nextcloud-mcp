# Notes

- User provisioning is admin-owned when targeting arbitrary users or mutating user state.
- Normal `/ocs/v1.php/cloud/user` current-user metadata remains outside this child, except as the already-resolved admin auth identity probe.
- Creating users may need repeated form fields (`groups[]`, `subadmin[]`), so the shared HTTP helper now supports ordered form field lists.
