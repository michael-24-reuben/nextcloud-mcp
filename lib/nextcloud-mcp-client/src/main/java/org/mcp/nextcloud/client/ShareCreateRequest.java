package org.mcp.nextcloud.client;

import org.mcp.nextcloud.core.util.Preconditions;

public record ShareCreateRequest(
        String path,
        ShareType shareType,
        String shareWith,
        int permissions,
        Boolean publicUpload,
        String password,
        String expireDate,
        String note,
        String label) {
    public ShareCreateRequest {
        path = Preconditions.requireNonBlank(path, "share path");
        shareType = Preconditions.requireNonNull(shareType, "share type");
    }

    public static ShareCreateRequest publicLink(String path, int permissions) {
        return new ShareCreateRequest(path, ShareType.PUBLIC_LINK, null, permissions, null, null, null, null, null);
    }

    public static ShareCreateRequest user(String path, String userId, int permissions) {
        return new ShareCreateRequest(path, ShareType.USER, Preconditions.requireNonBlank(userId, "share user"), permissions,
                null, null, null, null, null);
    }
}
