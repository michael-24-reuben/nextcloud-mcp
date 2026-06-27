package org.mcp.nextcloud.server;

final class ServerRequestException extends RuntimeException {
    private final String code;

    ServerRequestException(String code, String message) {
        super(message);
        this.code = code;
    }

    String code() {
        return code;
    }
}
