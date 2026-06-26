package org.mcp.nextcloud.core.util;

import java.util.Collection;

public final class StringMasks {
    public static final String MASK = "********";

    private StringMasks() {
    }

    public static String maskSecret(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        if (value.length() <= 4) {
            return MASK;
        }
        return value.substring(0, 2) + MASK + value.substring(value.length() - 2);
    }

    public static String maskKnownSecrets(String text, Collection<String> secrets) {
        if (text == null || text.isEmpty() || secrets == null || secrets.isEmpty()) {
            return text;
        }
        String masked = text;
        for (String secret : secrets) {
            if (secret != null && !secret.isBlank()) {
                masked = masked.replace(secret, maskSecret(secret));
            }
        }
        return masked;
    }
}
