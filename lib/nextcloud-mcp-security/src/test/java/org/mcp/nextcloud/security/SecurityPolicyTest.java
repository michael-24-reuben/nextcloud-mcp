package org.mcp.nextcloud.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mcp.nextcloud.core.id.AccountId;
import org.mcp.nextcloud.core.id.PrincipalId;
import org.mcp.nextcloud.core.id.ToolId;

class SecurityPolicyTest {
    @Test
    void nonAdminCannotUseAdminAccount() {
        Principal principal = new Principal(new PrincipalId("main"), "Main", false, Set.of(Scope.FILES_READ));

        assertFalse(new AccountAccessPolicy().canUseAccount(principal, new AccountId("admin"), true));
    }

    @Test
    void destructiveToolRequiresDeleteScopeWhenDenyDeleteByDefault() {
        Principal principal = new Principal(new PrincipalId("main"), "Main", false, Set.of(Scope.FILES_READ));
        ToolPermission permission = new ToolPermission(new ToolId("nextcloud.files.delete"), Set.of(Scope.FILES_READ), true);

        assertFalse(new ToolAccessPolicy().canInvoke(principal, permission));
    }

    @Test
    void requiredScopesAllowToolInvocation() {
        Principal principal = new Principal(new PrincipalId("main"), "Main", false, Set.of(Scope.FILES_READ, Scope.SHARES_READ));
        ToolPermission permission = new ToolPermission(new ToolId("nextcloud.files.list"), Set.of(Scope.FILES_READ), false);

        assertTrue(new ToolAccessPolicy().canInvoke(principal, permission));
    }

    @Test
    void maskerRedactsKnownSecrets() {
        String masked = new SecretMasker().maskKnownSecrets("password=super-secret", Set.of("super-secret"));

        assertFalse(masked.contains("super-secret"));
        assertTrue(masked.contains("su********et"));
    }
}
