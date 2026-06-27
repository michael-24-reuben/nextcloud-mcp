package org.mcp.nextcloud.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mcp.nextcloud.config.EnvironmentSecretResolver;
import org.mcp.nextcloud.config.SecretResolver;
import org.mcp.nextcloud.config.YamlConfigLoader;
import org.mcp.nextcloud.config.validation.ConfigValidator;
import org.mcp.nextcloud.http.JdkHttpClientAdapter;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(NextcloudMcpServerProperties.class)
class NextcloudMcpServerConfiguration {
    @Bean
    YamlConfigLoader nextcloudMcpConfigLoader() {
        return new YamlConfigLoader();
    }

    @Bean
    ConfigValidator nextcloudMcpConfigValidator() {
        return new ConfigValidator();
    }

    @Bean
    SecretResolver nextcloudMcpSecretResolver() {
        return new EnvironmentSecretResolver();
    }

    @Bean
    ObjectMapper nextcloudMcpObjectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }

    @Bean
    NextcloudHttpClientFactory nextcloudMcpHttpClientFactory() {
        return credentials -> new JdkHttpClientAdapter();
    }

    @Bean
    ApplicationRunner nextcloudMcpStartupValidator(
            NextcloudMcpServerProperties properties,
            NextcloudMcpRuntimeService runtimeService) {
        return args -> {
            if (properties.validateOnStartup()) {
                runtimeService.validateConfiguration();
            }
        };
    }
}
