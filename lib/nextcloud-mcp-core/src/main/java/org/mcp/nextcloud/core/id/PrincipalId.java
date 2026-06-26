package org.mcp.nextcloud.core.id;

import org.mcp.nextcloud.core.util.Preconditions;

public record PrincipalId(String value) {
    public PrincipalId {
        value = Preconditions.requireNonBlank(value, "principal id");
    }
}
