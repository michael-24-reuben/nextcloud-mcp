package org.mcp.nextcloud.core.result;

import java.time.Instant;
import java.util.Map;

import org.mcp.nextcloud.core.util.Preconditions;

public record ProgressEvent(String stage, String message, Instant occurredAt, Map<String, Object> attributes) {
    public ProgressEvent {
        stage = Preconditions.requireNonBlank(stage, "progress stage");
        occurredAt = occurredAt == null ? Instant.now() : occurredAt;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}
