package org.mcp.nextcloud.security;

import org.mcp.nextcloud.core.util.Preconditions;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public record Scope(String value, String description, boolean destructive, Set<Scope> prerequisites) {

    public Scope {
        value = Preconditions.requireNonBlank(value, "scope");
        description = Preconditions.requireNonBlank(description, "description");

        if (prerequisites == null || prerequisites.isEmpty()) {
            prerequisites = Set.of();
        } else {
            prerequisites = Collections.unmodifiableSet(new LinkedHashSet<>(prerequisites));
        }
    }

    public Scope(String value, String description) {
        this(value, description, false, Set.of());
    }

    public Scope(String value, String description, boolean destructive) {
        this(value, description, destructive, Set.of());
    }

    public Scope(String value, String description, Set<Scope> prerequisites) {
        this(value, description, false, prerequisites);
    }

    public boolean adminScope() {
        return value.startsWith("nextcloud.admin.");
    }

    public boolean riskScope() {
        return value.startsWith("risk.");
    }

    public boolean nextcloudScope() {
        return value.startsWith("nextcloud.");
    }

    public Set<Scope> flattenedPrerequisites() {
        Set<Scope> resolved = new LinkedHashSet<>();
        collectPrerequisites(this, resolved);
        resolved.remove(this);
        return Collections.unmodifiableSet(resolved);
    }

    private static void collectPrerequisites(Scope scope, Set<Scope> resolved) {
        if (!resolved.add(scope)) {
            return;
        }

        for (Scope prerequisite : scope.prerequisites()) {
            collectPrerequisites(prerequisite, resolved);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Scope scope)) return false;
        return value.equals(scope.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}