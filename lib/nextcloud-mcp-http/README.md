# nextcloud-mcp-http

Transport-neutral HTTP request and response helpers.

## Owns

- HTTP method enum, including WebDAV methods.
- Immutable request and response specs: `HttpRequestSpec`, `HttpResponseSpec`.
- `HttpClientAdapter` abstraction.
- JDK implementation: `JdkHttpClientAdapter`.
- Header builder and auth helpers: `HttpHeadersBuilder`, `BasicAuth`, `BearerAuth`.
- Retry and rate-limit policy records.

## Rules

- Higher modules build request specs; this module only sends them.
- Credentials are encoded into headers by helpers, not logged.
- No Spring dependency belongs here.

## Verification

```powershell
.\mvnw.cmd -pl lib/nextcloud-mcp-http -am test
```
