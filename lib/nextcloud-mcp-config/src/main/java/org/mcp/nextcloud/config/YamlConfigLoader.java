package org.mcp.nextcloud.config;

import java.io.IOException;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public final class YamlConfigLoader implements ConfigLoader {
    private final ObjectMapper mapper;

    public YamlConfigLoader() {
        this(new ObjectMapper(new YAMLFactory()).findAndRegisterModules());
    }

    public YamlConfigLoader(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public NextcloudMcpConfig load(Path path) throws IOException {
        return mapper.readValue(path.toFile(), NextcloudMcpConfig.class);
    }
}
