package org.mcp.nextcloud.core.id;

import org.mcp.nextcloud.core.util.Preconditions;

public record AccountId(String value) {
    public AccountId {
        value = Preconditions.requireNonBlank(value, "account id");
    }
}
