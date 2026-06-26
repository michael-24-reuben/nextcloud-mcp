package org.mcp.nextcloud.tool.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mcp.nextcloud.tool.api.ToolDescriptor;
import org.mcp.nextcloud.tool.api.ToolParameter;
import org.mcp.nextcloud.tool.api.ToolValueType;

public final class ToolArgumentValidator {
    public ToolValidationResult validate(ToolDescriptor descriptor, Map<String, Object> arguments) {
        arguments = arguments == null ? Map.of() : arguments;
        List<String> errors = new ArrayList<>();
        Map<String, ToolParameter> parameters = descriptor.inputSchema().parametersByName();

        for (ToolParameter parameter : parameters.values()) {
            Object value = arguments.get(parameter.name());
            if (value == null) {
                if (parameter.required() && parameter.defaultValue() == null) {
                    errors.add("missing required parameter: " + parameter.name());
                }
                continue;
            }
            if (!matchesType(parameter.type(), value)) {
                errors.add("invalid type for parameter " + parameter.name() + ": expected " + parameter.type());
                continue;
            }
            if (!parameter.allowedValues().isEmpty() && !parameter.allowedValues().contains(String.valueOf(value))) {
                errors.add("invalid value for parameter " + parameter.name());
            }
        }

        if (!descriptor.inputSchema().additionalProperties()) {
            for (String providedName : arguments.keySet()) {
                if (!parameters.containsKey(providedName)) {
                    errors.add("unknown parameter: " + providedName);
                }
            }
        }

        return errors.isEmpty() ? ToolValidationResult.ok() : ToolValidationResult.failed(errors);
    }

    private boolean matchesType(ToolValueType type, Object value) {
        return switch (type) {
            case STRING -> value instanceof String;
            case INTEGER -> value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long;
            case NUMBER -> value instanceof Number;
            case BOOLEAN -> value instanceof Boolean;
            case OBJECT -> value instanceof Map<?, ?>;
            case ARRAY -> value instanceof List<?> || value.getClass().isArray();
        };
    }
}
