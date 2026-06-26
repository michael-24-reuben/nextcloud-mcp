# Context

The admin blueprint says some tasks are better handled through OCC:

```powershell
docker exec -u www-data -it nextcloud-aio-nextcloud php occ ...
```

This bridge is critical risk. It should remain optional and should not become a general shell transport.
