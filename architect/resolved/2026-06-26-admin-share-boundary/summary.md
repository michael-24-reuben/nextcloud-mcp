# Summary

The admin share boundary is resolved by routing admin-authenticated share work through the existing user/content share client, not through admin provisioning. The admin facade exposes a share-support adapter for normal OCS share APIs while preserving the rule that admin provisioning does not own WebDAV file-space behavior.
