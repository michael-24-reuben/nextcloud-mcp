# Context

## Endpoint Family

```text
/ocs/v1.php/cloud/groups
/ocs/v1.php/cloud/users/{userid}/groups
/ocs/v1.php/cloud/users/{userid}/subadmins
```

Group and subadmin operations are governance APIs. Adding/removing normal group membership is medium risk, while admin-group membership and subadmin promotion need stricter policy gates.
