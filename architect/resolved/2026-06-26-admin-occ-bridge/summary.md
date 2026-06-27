# Summary

The admin OCC bridge is implemented as a non-executing command-plan builder. It exposes only allowlisted critical operations, keeps the default Nextcloud AIO container assumptions explicit, and leaves real shell execution to a future audited executor.
