package org.mcp.nextcloud.security;

import java.util.Collection;

import org.mcp.nextcloud.core.util.StringMasks;

public final class SecretMasker {
    public String mask(String value) {
        return StringMasks.maskSecret(value);
    }

    public String maskKnownSecrets(String text, Collection<String> secrets) {
        return StringMasks.maskKnownSecrets(text, secrets);
    }
}
