package org.mcp.nextcloud.security;

import org.mcp.nextcloud.core.id.AccountId;
import org.mcp.nextcloud.core.id.InvocationId;

public record PrincipalContext(Principal principal, AccountId accountId, InvocationId invocationId) {
}
