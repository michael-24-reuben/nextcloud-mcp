# Assessment

Group provisioning and subadmin management are admin governance routes. They should stay in `nextcloud-mcp-admin`, including the user membership/subadmin endpoints under `/users/{userid}`, because their permission model is admin-owned.

Admin tool policy will later need stronger confirmation around admin-group membership and subadmin promotion.
