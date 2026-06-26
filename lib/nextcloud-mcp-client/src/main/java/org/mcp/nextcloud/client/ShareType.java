package org.mcp.nextcloud.client;

public enum ShareType {
    USER(0),
    GROUP(1),
    PUBLIC_LINK(3),
    EMAIL(4),
    REMOTE(6),
    CIRCLE(7);

    private final int code;

    ShareType(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}
