package org.mcp.nextcloud.security;

import java.util.Set;

public final class Scopes {

    private Scopes() {
    }

    public static final class Files {
        private Files() {
        }

        public static final Scope READ = new Scope(
                "nextcloud.files.read",
                "Read file and folder metadata/content from the authenticated user's file space."
        );

        public static final Scope WRITE = new Scope(
                "nextcloud.files.write",
                "Create or modify files, folders, and writable WebDAV metadata."
        );

        public static final Scope DELETE = new Scope(
                "nextcloud.files.delete",
                "Delete files or folders from the authenticated user's file space.",
                true,
                Set.of(READ)
        );

        public static final Scope SEARCH = new Scope(
                "nextcloud.files.search",
                "Search files by display name or indexed WebDAV properties.",
                false,
                Set.of(READ)
        );

        public static final Scope FAVORITE = new Scope(
                "nextcloud.files.favorite",
                "Set or clear favorite metadata on files or folders.",
                false,
                Set.of(WRITE)
        );

        public static final Scope LIST = new Scope(
                "nextcloud.files.list",
                "List files in a Nextcloud folder.",
                false,
                Set.of(READ)
        );

        public static final Scope STAT = new Scope(
                "nextcloud.files.stat",
                "Read metadata for a Nextcloud file or folder.",
                false,
                Set.of(READ)
        );

        public static final Scope DOWNLOAD = new Scope(
                "nextcloud.files.download",
                "Download a file as base64 content.",
                false,
                Set.of(READ)
        );

        public static final Scope UPLOAD = new Scope(
                "nextcloud.files.upload",
                "Upload text or base64 content to a file path.",
                true,
                Set.of(WRITE)
        );

        public static final Scope MKDIR = new Scope(
                "nextcloud.files.mkdir",
                "Create a folder.",
                true,
                Set.of(WRITE)
        );

        public static final Scope MOVE = new Scope(
                "nextcloud.files.move",
                "Move a file or folder.",
                false,
                Set.of(WRITE, DELETE)
        );

        public static final Scope COPY = new Scope(
                "nextcloud.files.copy",
                "Copy a file or folder.",
                false,
                Set.of(WRITE)
        );

        public static final Set<Scope> ALL = Set.of(
                READ,
                WRITE,
                DELETE,
                SEARCH,
                FAVORITE,
                LIST,
                STAT,
                DOWNLOAD,
                UPLOAD,
                MKDIR,
                MOVE,
                COPY
        );
    }

    public static final class Shares {
        private Shares() {
        }

        public static final Scope READ = new Scope(
                "nextcloud.shares.read",
                "Read shares owned by or visible to the authenticated user."
        );

        public static final Scope WRITE = new Scope(
                "nextcloud.shares.write",
                "Create or update file and folder shares."
        );

        public static final Scope DELETE = new Scope(
                "nextcloud.shares.delete",
                "Delete or revoke file and folder shares.",
                true,
                Set.of(READ)
        );

        public static final Set<Scope> ALL = Set.of(
                READ,
                WRITE,
                DELETE
        );
    }

    public static final class Sharees {
        private Sharees() {
        }

        public static final Scope READ = new Scope(
                "nextcloud.sharees.read",
                "Search users, groups, circles, emails, or other valid share recipients."
        );

        public static final Set<Scope> ALL = Set.of(READ);
    }

    public static final class User {
        private User() {
        }

        public static final Scope READ = new Scope(
                "nextcloud.user.read",
                "Read the authenticated user's own metadata, groups, quota, and account state."
        );

        public static final Scope CAPABILITIES_READ = new Scope(
                "nextcloud.user.capabilities.read",
                "Read server capabilities visible to the authenticated user.",
                Set.of(READ)
        );

        public static final Scope STATUS_READ = new Scope(
                "nextcloud.user.status.read",
                "Read the authenticated user's status information.",
                Set.of(READ)
        );

        public static final Scope STATUS_WRITE = new Scope(
                "nextcloud.user.status.write",
                "Update the authenticated user's status information.",
                Set.of(READ)
        );

        public static final Scope PREFERENCES_READ = new Scope(
                "nextcloud.user.preferences.read",
                "Read user-scoped preference values where supported."
        );

        public static final Scope PREFERENCES_WRITE = new Scope(
                "nextcloud.user.preferences.write",
                "Update user-scoped preference values where supported.",
                Set.of(PREFERENCES_READ)
        );

        public static final Set<Scope> ALL = Set.of(
                READ,
                CAPABILITIES_READ,
                STATUS_READ,
                STATUS_WRITE,
                PREFERENCES_READ,
                PREFERENCES_WRITE
        );
    }

    public static final class Comments {
        private Comments() {
        }

        public static final Scope READ = new Scope(
                "nextcloud.comments.read",
                "Read comments attached to files or folders."
        );

        public static final Scope WRITE = new Scope(
                "nextcloud.comments.write",
                "Create or update comments attached to files or folders.",
                Set.of(READ)
        );

        public static final Scope DELETE = new Scope(
                "nextcloud.comments.delete",
                "Delete comments attached to files or folders.",
                true,
                Set.of(READ)
        );

        public static final Set<Scope> ALL = Set.of(
                READ,
                WRITE,
                DELETE
        );
    }

    public static final class Trash {
        private Trash() {
        }

        public static final Scope READ = new Scope(
                "nextcloud.trash.read",
                "List deleted files and folders in the authenticated user's trashbin."
        );

        public static final Scope RESTORE = new Scope(
                "nextcloud.trash.restore",
                "Restore files or folders from the authenticated user's trashbin.",
                Set.of(READ)
        );

        public static final Scope DELETE = new Scope(
                "nextcloud.trash.delete",
                "Permanently delete a file or folder from the authenticated user's trashbin.",
                true,
                Set.of(READ)
        );

        public static final Scope EMPTY = new Scope(
                "nextcloud.trash.empty",
                "Empty the authenticated user's trashbin.",
                true,
                Set.of(READ, DELETE)
        );

        public static final Set<Scope> ALL = Set.of(
                READ,
                RESTORE,
                DELETE,
                EMPTY
        );
    }

    public static final class Versions {
        private Versions() {
        }

        public static final Scope READ = new Scope(
                "nextcloud.versions.read",
                "List previous versions of a file."
        );

        public static final Scope RESTORE = new Scope(
                "nextcloud.versions.restore",
                "Restore a previous version of a file.",
                Set.of(READ, Files.WRITE)
        );

        public static final Set<Scope> ALL = Set.of(
                READ,
                RESTORE
        );
    }

    public static final class Admin {
        private Admin() {
        }

        public static final Scope READ = new Scope(
                "nextcloud.admin.read",
                "Read administrative user, group, app, or server metadata."
        );

        public static final Scope WRITE = new Scope(
                "nextcloud.admin.write",
                "Create or update administrative resources."
        );

        public static final Scope DELETE = new Scope(
                "nextcloud.admin.delete",
                "Delete administrative resources.",
                true,
                Set.of(READ)
        );

        public static final class Users {
            private Users() {
            }

            public static final Scope READ = new Scope(
                    "nextcloud.admin.users.read",
                    "Read users and user metadata as an administrator.",
                    Set.of(Admin.READ)
            );

            public static final Scope WRITE = new Scope(
                    "nextcloud.admin.users.write",
                    "Create or update users as an administrator.",
                    Set.of(Admin.WRITE, READ)
            );

            public static final Scope DISABLE = new Scope(
                    "nextcloud.admin.users.disable",
                    "Enable or disable user accounts.",
                    true,
                    Set.of(Admin.WRITE, READ)
            );

            public static final Scope DELETE = new Scope(
                    "nextcloud.admin.users.delete",
                    "Delete user accounts.",
                    true,
                    Set.of(Admin.DELETE, READ, Risk.CRITICAL)
            );

            public static final Set<Scope> ALL = Set.of(
                    READ,
                    WRITE,
                    DISABLE,
                    DELETE
            );
        }

        public static final class Groups {
            private Groups() {
            }

            public static final Scope READ = new Scope(
                    "nextcloud.admin.groups.read",
                    "Read groups, group members, and group subadmins.",
                    Set.of(Admin.READ)
            );

            public static final Scope WRITE = new Scope(
                    "nextcloud.admin.groups.write",
                    "Create or update groups and group membership.",
                    Set.of(Admin.WRITE, READ)
            );

            public static final Scope DELETE = new Scope(
                    "nextcloud.admin.groups.delete",
                    "Delete groups.",
                    true,
                    Set.of(Admin.DELETE, READ, Risk.CRITICAL)
            );

            public static final Set<Scope> ALL = Set.of(
                    READ,
                    WRITE,
                    DELETE
            );
        }

        public static final class Subadmins {
            private Subadmins() {
            }

            public static final Scope READ = new Scope(
                    "nextcloud.admin.subadmins.read",
                    "Read subadmin assignments.",
                    Set.of(Admin.READ)
            );

            public static final Scope WRITE = new Scope(
                    "nextcloud.admin.subadmins.write",
                    "Promote or demote group subadmins.",
                    true,
                    Set.of(Admin.WRITE, READ, Risk.DESTRUCTIVE)
            );

            public static final Set<Scope> ALL = Set.of(
                    READ,
                    WRITE
            );
        }

        public static final class Apps {
            private Apps() {
            }

            public static final Scope READ = new Scope(
                    "nextcloud.admin.apps.read",
                    "Read installed, enabled, or disabled Nextcloud apps.",
                    Set.of(Admin.READ)
            );

            public static final Scope WRITE = new Scope(
                    "nextcloud.admin.apps.write",
                    "Enable or disable Nextcloud apps.",
                    true,
                    Set.of(Admin.WRITE, READ, Risk.CRITICAL)
            );

            public static final Set<Scope> ALL = Set.of(
                    READ,
                    WRITE
            );
        }

        public static final class Occ {
            private Occ() {
            }

            public static final Scope PLAN = new Scope(
                    "nextcloud.admin.occ.plan",
                    "Generate or inspect guarded OCC command plans.",
                    Set.of(Admin.READ)
            );

            public static final Scope EXECUTE = new Scope(
                    "nextcloud.admin.occ.execute",
                    "Execute guarded OCC commands on the Nextcloud runtime.",
                    true,
                    Set.of(Admin.WRITE, PLAN, Risk.CRITICAL)
            );

            public static final Set<Scope> ALL = Set.of(
                    PLAN,
                    EXECUTE
            );
        }

        public static final Set<Scope> ALL = Set.of(
                READ,
                WRITE,
                DELETE,

                Users.READ,
                Users.WRITE,
                Users.DISABLE,
                Users.DELETE,

                Groups.READ,
                Groups.WRITE,
                Groups.DELETE,

                Subadmins.READ,
                Subadmins.WRITE,

                Apps.READ,
                Apps.WRITE,

                Occ.PLAN,
                Occ.EXECUTE
        );
    }

    public static final class Risk {
        private Risk() {
        }

        public static final Scope DESTRUCTIVE = new Scope(
                "risk.destructive",
                "Allows destructive operations after an explicit policy or user confirmation gate."
        );

        public static final Scope CRITICAL = new Scope(
                "risk.critical",
                "Allows critical administrative operations after the strongest confirmation gate.",
                true,
                Set.of(DESTRUCTIVE)
        );

        public static final Set<Scope> ALL = Set.of(
                DESTRUCTIVE,
                CRITICAL
        );
    }

    public static final Set<Scope> ALL = union(
            Files.ALL,
            Shares.ALL,
            Sharees.ALL,
            User.ALL,
            Comments.ALL,
            Trash.ALL,
            Versions.ALL,
            Admin.ALL,
            Risk.ALL
    );

    @SafeVarargs
    private static Set<Scope> union(Set<Scope>... sets) {
        java.util.LinkedHashSet<Scope> result = new java.util.LinkedHashSet<>();
        for (Set<Scope> set : sets) {
            result.addAll(set);
        }
        return java.util.Collections.unmodifiableSet(result);
    }
}