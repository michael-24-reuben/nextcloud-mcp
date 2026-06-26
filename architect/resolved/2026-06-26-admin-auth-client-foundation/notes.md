# Notes

- User redirected from MVP outbound slices to admin API resolutions because CLI/server/integration wiring depends on having admin endpoints available first.
- Existing `NextcloudHttpRequestFactory` already supplies the required Basic Auth and OCS headers; admin should reuse it instead of creating a parallel HTTP stack.
- Existing client `OcsParser` is package-private in the user/content client module, so admin owns a small admin-local parser to keep module boundaries clean.
