package org.mcp.nextcloud.client;

public record WebDavResource(
        String href,
        String name,
        boolean collection,
        Long size,
        String contentType,
        String etag,
        String lastModified,
        Integer favorite,
        String permissions,
        String ownerId,
        String ownerDisplayName) {
}
