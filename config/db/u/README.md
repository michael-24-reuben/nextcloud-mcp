# User Env Records

User records live in files matching `usr-*.env`. The file suffix is the fallback account key, so `usr-temporary.env` becomes account key `temporary` unless `ACCOUNT_KEY` is set.

Supported fields:

```dotenv
ACCOUNT_KEY=temporary
ACCOUNT_ID=temporary
BASE_URL=https://cloud.example.com
USERNAME=login-name
APP_PASSWORD=nextcloud-app-password
DISPLAY_NAME=Temporary User
EMAIL=temporary@example.com
DEFAULT_ACCOUNT=true
ADMIN=false
ENABLED=true
SCOPES=nextcloud.files.read,nextcloud.files.write,nextcloud.shares.read,nextcloud.shares.write,nextcloud.user.read
```

Only the account setup fields are used by the current runtime. Profile fields such as `DISPLAY_NAME` and `EMAIL` are kept here for upcoming provisioning and SQL migration work.
