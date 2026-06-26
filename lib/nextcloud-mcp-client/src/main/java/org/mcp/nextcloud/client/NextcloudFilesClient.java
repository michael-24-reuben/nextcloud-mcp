package org.mcp.nextcloud.client;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.mcp.nextcloud.core.util.Preconditions;
import org.mcp.nextcloud.http.HttpClientAdapter;
import org.mcp.nextcloud.http.HttpMethod;
import org.mcp.nextcloud.http.HttpResponseSpec;
import org.mcp.nextcloud.http.NextcloudHttpRequestFactory;

public final class NextcloudFilesClient extends AbstractNextcloudClient {
    private static final byte[] PROPFIND_BODY = """
            <?xml version="1.0" encoding="UTF-8"?>
            <d:propfind xmlns:d="DAV:" xmlns:oc="http://owncloud.org/ns">
              <d:prop>
                <d:getlastmodified/>
                <d:getetag/>
                <d:getcontenttype/>
                <d:getcontentlength/>
                <d:resourcetype/>
                <oc:favorite/>
                <oc:permissions/>
                <oc:owner-id/>
                <oc:owner-display-name/>
              </d:prop>
            </d:propfind>
            """.getBytes(StandardCharsets.UTF_8);

    private final WebDavParser parser;

    NextcloudFilesClient(HttpClientAdapter httpClient, NextcloudHttpRequestFactory requests, WebDavParser parser) {
        super(httpClient, requests);
        this.parser = parser;
    }

    public List<WebDavResource> list(NextcloudUser user, String path) {
        return list(user.id(), path);
    }

    public List<WebDavResource> list(String userId, String path) {
        String target = requests.webDavFilesPath(userId, path, true);
        HttpResponseSpec response = sendExpecting(requests.webDav(
                HttpMethod.PROPFIND,
                userId,
                path,
                true,
                Map.of("Depth", "1", "Content-Type", "application/xml; charset=utf-8"),
                PROPFIND_BODY), 207);
        return parser.resources(response.body()).stream()
                .filter(resource -> !sameHref(resource.href(), target))
                .toList();
    }

    public WebDavResource stat(NextcloudUser user, String path) {
        return stat(user.id(), path);
    }

    public WebDavResource stat(String userId, String path) {
        HttpResponseSpec response = sendExpecting(requests.webDav(
                HttpMethod.PROPFIND,
                userId,
                path,
                false,
                Map.of("Depth", "0", "Content-Type", "application/xml; charset=utf-8"),
                PROPFIND_BODY), 207);
        return parser.resources(response.body()).stream()
                .findFirst()
                .orElseThrow(() -> new NextcloudClientException("webdav_empty_stat", "WebDAV stat returned no resources"));
    }

    public WebDavOperation mkdir(NextcloudUser user, String path) {
        return mkdir(user.id(), path);
    }

    public WebDavOperation mkdir(String userId, String path) {
        HttpResponseSpec response = sendExpecting(requests.webDav(
                HttpMethod.MKCOL, userId, path, true, Map.of(), null), 201, 405);
        return new WebDavOperation(HttpMethod.MKCOL, path, response.statusCode());
    }

    public WebDavOperation upload(NextcloudUser user, String path, byte[] body) {
        return upload(user.id(), path, body);
    }

    public WebDavOperation upload(String userId, String path, byte[] body) {
        HttpResponseSpec response = sendExpecting(requests.webDav(
                HttpMethod.PUT,
                userId,
                path,
                false,
                Map.of("Content-Type", "application/octet-stream"),
                body), 200, 201, 204);
        return new WebDavOperation(HttpMethod.PUT, path, response.statusCode());
    }

    public byte[] download(NextcloudUser user, String path) {
        return download(user.id(), path);
    }

    public byte[] download(String userId, String path) {
        return sendExpecting(requests.webDav(HttpMethod.GET, userId, path, false, Map.of(), null), 200).body();
    }

    public WebDavOperation delete(NextcloudUser user, String path) {
        return delete(user.id(), path);
    }

    public WebDavOperation delete(String userId, String path) {
        HttpResponseSpec response = sendExpecting(requests.webDav(
                HttpMethod.DELETE, userId, path, false, Map.of(), null), 200, 202, 204);
        return new WebDavOperation(HttpMethod.DELETE, path, response.statusCode());
    }

    public WebDavOperation move(NextcloudUser user, String fromPath, String toPath) {
        return move(user.id(), fromPath, toPath);
    }

    public WebDavOperation move(String userId, String fromPath, String toPath) {
        Preconditions.requireNonBlank(toPath, "destination path");
        HttpResponseSpec response = sendExpecting(requests.webDav(
                HttpMethod.MOVE,
                userId,
                fromPath,
                false,
                Map.of("Destination", requests.webDavDestinationUri(userId, toPath, false).toString()),
                null), 201, 204);
        return new WebDavOperation(HttpMethod.MOVE, fromPath, response.statusCode());
    }

    public WebDavOperation copy(NextcloudUser user, String fromPath, String toPath) {
        return copy(user.id(), fromPath, toPath);
    }

    public WebDavOperation copy(String userId, String fromPath, String toPath) {
        Preconditions.requireNonBlank(toPath, "destination path");
        HttpResponseSpec response = sendExpecting(requests.webDav(
                HttpMethod.COPY,
                userId,
                fromPath,
                false,
                Map.of("Destination", requests.webDavDestinationUri(userId, toPath, false).toString()),
                null), 201, 204);
        return new WebDavOperation(HttpMethod.COPY, fromPath, response.statusCode());
    }

    public WebDavOperation favorite(NextcloudUser user, String path, boolean favorite) {
        return favorite(user.id(), path, favorite);
    }

    public WebDavOperation favorite(String userId, String path, boolean favorite) {
        byte[] body = ("""
                <?xml version="1.0" encoding="UTF-8"?>
                <d:propertyupdate xmlns:d="DAV:" xmlns:oc="http://owncloud.org/ns">
                  <d:set>
                    <d:prop><oc:favorite>%s</oc:favorite></d:prop>
                  </d:set>
                </d:propertyupdate>
                """.formatted(favorite ? "1" : "0")).getBytes(StandardCharsets.UTF_8);
        HttpResponseSpec response = sendExpecting(requests.webDav(
                HttpMethod.PROPPATCH,
                userId,
                path,
                false,
                Map.of("Content-Type", "application/xml; charset=utf-8"),
                body), 207);
        return new WebDavOperation(HttpMethod.PROPPATCH, path, response.statusCode());
    }

    public List<WebDavResource> search(NextcloudUser user, String query) {
        return search(user.id(), query);
    }

    public List<WebDavResource> search(String userId, String query) {
        String escaped = xmlEscape(Preconditions.requireNonBlank(query, "search query"));
        String scope = requests.webDavSearchScope(userId);
        byte[] body = ("""
                <?xml version="1.0" encoding="UTF-8"?>
                <d:searchrequest xmlns:d="DAV:" xmlns:oc="http://owncloud.org/ns">
                  <d:basicsearch>
                    <d:select>
                      <d:prop>
                        <d:getlastmodified/>
                        <d:getcontenttype/>
                        <d:getcontentlength/>
                        <d:resourcetype/>
                        <oc:permissions/>
                      </d:prop>
                    </d:select>
                    <d:from>
                      <d:scope>
                        <d:href>%s</d:href>
                        <d:depth>infinity</d:depth>
                      </d:scope>
                    </d:from>
                    <d:where>
                      <d:like>
                        <d:prop><d:displayname/></d:prop>
                        <d:literal>%%%s%%</d:literal>
                      </d:like>
                    </d:where>
                  </d:basicsearch>
                </d:searchrequest>
                """.formatted(scope, escaped)).getBytes(StandardCharsets.UTF_8);
        HttpResponseSpec response = sendExpecting(requests.webDavRoot(
                HttpMethod.SEARCH,
                Map.of("Content-Type", "application/xml; charset=utf-8"),
                body), 207);
        return parser.resources(response.body());
    }

    private static boolean sameHref(String href, String target) {
        if (href == null) {
            return false;
        }
        String normalizedHref = href.endsWith("/") ? href.substring(0, href.length() - 1) : href;
        String normalizedTarget = target.endsWith("/") ? target.substring(0, target.length() - 1) : target;
        return normalizedHref.equals(normalizedTarget);
    }

    private static String xmlEscape(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
