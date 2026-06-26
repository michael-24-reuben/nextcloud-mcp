package org.mcp.nextcloud.client;

import java.net.URI;
import java.util.Optional;

import org.mcp.nextcloud.config.NextcloudAccountConfig;
import org.mcp.nextcloud.config.SecretResolver;
import org.mcp.nextcloud.core.error.ConfigurationException;
import org.mcp.nextcloud.core.id.AccountId;
import org.mcp.nextcloud.core.util.Preconditions;

public record NextcloudCredentials(AccountId accountId, URI baseUri, String username, String appPassword) {
    public NextcloudCredentials {
        accountId = Preconditions.requireNonNull(accountId, "account id");
        baseUri = normalizeBaseUri(baseUri);
        username = Preconditions.requireNonBlank(username, "username");
        appPassword = Preconditions.requireNonBlank(appPassword, "app password");
    }

    public static NextcloudCredentials of(String accountId, String baseUrl, String username, String appPassword) {
        return new NextcloudCredentials(new AccountId(accountId), URI.create(baseUrl), username, appPassword);
    }

    public static NextcloudCredentials fromAccount(NextcloudAccountConfig account, SecretResolver resolver) {
        Preconditions.requireNonNull(account, "account");
        String resolvedPassword = resolveSecret(account.appPassword(), resolver);
        return of(account.id(), account.baseUrl(), account.username(), resolvedPassword);
    }

    public String baseUrl() {
        return baseUri.toString();
    }

    private static URI normalizeBaseUri(URI uri) {
        Preconditions.requireNonNull(uri, "base uri");
        if (uri.getScheme() == null || uri.getHost() == null) {
            throw new ConfigurationException("invalid_nextcloud_base_url", "Nextcloud base URL must be absolute");
        }
        String value = uri.toString();
        while (value.endsWith("/") && value.length() > uri.getScheme().length() + 3) {
            value = value.substring(0, value.length() - 1);
        }
        return URI.create(value);
    }

    private static String resolveSecret(String value, SecretResolver resolver) {
        String candidate = Preconditions.requireNonBlank(value, "app password");
        if (!candidate.startsWith("${") || !candidate.endsWith("}")) {
            return candidate;
        }
        String name = candidate.substring(2, candidate.length() - 1).trim();
        if (name.isBlank()) {
            throw new ConfigurationException("invalid_secret_ref", "Secret reference must name a variable");
        }
        Optional<String> resolved = resolver == null ? Optional.empty() : resolver.resolve(name);
        return resolved.orElseThrow(() -> new ConfigurationException(
                "unresolved_secret_ref", "Secret reference could not be resolved: " + name));
    }
}
