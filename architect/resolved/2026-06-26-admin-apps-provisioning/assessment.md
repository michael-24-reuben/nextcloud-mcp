# Assessment

Admin app provisioning is a distinct OCS provisioning API family under `/ocs/v1.php/cloud/apps`.

Read-only app list and app info operations are low-risk endpoint reads. App enable and disable operations are critical-risk because a bad call can destabilize the server or conflict with AIO-managed expectations. The client therefore exposes enable/disable as callable Java methods but returns explicit `AdminRiskLevel.CRITICAL` metadata for later tool-policy enforcement.
