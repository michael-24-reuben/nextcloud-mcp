package org.mcp.nextcloud.client;

public final class SharePermission {
    public static final int READ = 1;
    public static final int UPDATE = 2;
    public static final int CREATE = 4;
    public static final int DELETE = 8;
    public static final int SHARE = 16;
    public static final int ALL = READ | UPDATE | CREATE | DELETE | SHARE;

    private SharePermission() {
    }
}
