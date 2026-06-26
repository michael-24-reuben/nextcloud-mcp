package org.mcp.nextcloud.security;

import org.mcp.nextcloud.core.id.AccountId;

public final class AccountAccessPolicy {
    public boolean canUseAccount(Principal principal, AccountId requestedAccount, boolean accountAdmin) {
        if (principal == null || requestedAccount == null) {
            return false;
        }
        if (accountAdmin && !principal.admin()) {
            return false;
        }
        return principal.admin() || principal.id().value().equals(requestedAccount.value());
    }
}
