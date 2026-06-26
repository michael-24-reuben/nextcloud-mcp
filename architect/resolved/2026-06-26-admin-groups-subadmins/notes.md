# Notes

- Group list/create/member operations are admin provisioning concerns.
- User membership and subadmin promotion endpoints are grouped here because their permission model is governance-oriented even though their paths are under `/users/{userid}`.
- Admin tools must later gate admin-group membership and subadmin promotion more strictly than ordinary group membership.
