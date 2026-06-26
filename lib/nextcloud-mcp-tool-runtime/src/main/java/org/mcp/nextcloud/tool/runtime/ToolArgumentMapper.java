package org.mcp.nextcloud.tool.runtime;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.mcp.nextcloud.core.util.Preconditions;

public final class ToolArgumentMapper {
    private final ObjectMapper objectMapper;

    public ToolArgumentMapper() {
        this(new ObjectMapper());
    }

    public ToolArgumentMapper(ObjectMapper objectMapper) {
        this.objectMapper = Preconditions.requireNonNull(objectMapper, "object mapper");
    }

    public <T> T convert(Map<String, Object> arguments, Class<T> targetType) {
        return objectMapper.convertValue(arguments == null ? Map.of() : arguments, targetType);
    }
}
