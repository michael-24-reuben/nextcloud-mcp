# Context

## Endpoint Family

```text
/ocs/v1.php/cloud/users
```

User provisioning is admin-owned when targeting arbitrary users or mutating user state. Normal self metadata remains part of the non-admin client boundary.

## Risk Notes

- Create user and ordinary field edits are medium risk.
- Enable/disable and quota edits are high risk.
- Delete user is critical risk.
