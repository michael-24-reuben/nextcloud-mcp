package org.mcp.nextcloud.core.id;

import org.mcp.nextcloud.core.util.Preconditions;

public record InvocationId(String value) {
    public InvocationId {
        value = Preconditions.requireNonBlank(value, "invocation id");
    }
}
