# Context

Share APIs under admin credentials still use:

```text
/ocs/v2.php/apps/files_sharing/api/v1
```

The admin blueprint calls out that these are not a separate admin provisioning API. Admin credentials may have broader visibility depending on server settings, but file shares still need path ownership and account-boundary handling.
