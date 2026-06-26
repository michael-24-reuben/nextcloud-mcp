package org.mcp.nextcloud.tool.runtime;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.mcp.nextcloud.core.id.ToolId;
import org.mcp.nextcloud.core.util.Preconditions;

public final class InMemoryToolRegistry implements ToolRegistry {
    private final Map<ToolId, ToolRegistration> registrations = new LinkedHashMap<>();

    @Override
    public synchronized ToolRegistration register(ToolRegistration registration) {
        registration = Preconditions.requireNonNull(registration, "tool registration");
        ToolId toolId = registration.descriptor().id();
        if (registrations.containsKey(toolId)) {
            throw new IllegalArgumentException("tool already registered: " + toolId.value());
        }
        registrations.put(toolId, registration);
        return registration;
    }

    @Override
    public synchronized Optional<ToolRegistration> find(ToolId toolId) {
        return Optional.ofNullable(registrations.get(toolId));
    }

    @Override
    public synchronized List<ToolRegistration> list() {
        return registrations.values().stream()
                .sorted(Comparator.comparing(registration -> registration.descriptor().id().value()))
                .toList();
    }

    public synchronized List<ToolId> toolIds() {
        return new ArrayList<>(registrations.keySet());
    }
}
