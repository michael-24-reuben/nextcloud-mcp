package org.mcp.nextcloud.security;

import org.mcp.nextcloud.core.util.Preconditions;

public record Scope(String value) {
    public static final Scope FILES_READ = new Scope("nextcloud.files.read");
    public static final Scope FILES_WRITE = new Scope("nextcloud.files.write");
    public static final Scope FILES_DELETE = new Scope("nextcloud.files.delete");
    public static final Scope SHARES_READ = new Scope("nextcloud.shares.read");
    public static final Scope SHARES_WRITE = new Scope("nextcloud.shares.write");
    public static final Scope USER_READ = new Scope("nextcloud.user.read");
    public static final Scope COMMENTS_WRITE = new Scope("nextcloud.comments.write");
    public static final Scope TRASH_RESTORE = new Scope("nextcloud.trash.restore");

    public Scope {
        value = Preconditions.requireNonBlank(value, "scope");
    }

    public boolean adminScope() {
        return value.startsWith("nextcloud.admin.");
    }
}
