# Assessment

OCC support belongs in this project only as a guarded bridge surface, not as direct process execution. The implementation returns explicit command plans for an allowlisted set of operations so a future host executor can add confirmation, audit logging, and environment-specific execution outside the admin client library.

The bridge assumes a Docker/AIO style command shape by default: `docker exec -u www-data nextcloud-aio-nextcloud php occ ...`. Every exposed OCC operation is critical risk and requires the admin OCC scope before a plan can be returned.
