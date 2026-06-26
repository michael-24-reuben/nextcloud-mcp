package org.mcp.nextcloud.tool.runtime;

public record ToolPolicyDecision(boolean allowed, String reason) {
    public static ToolPolicyDecision allow() {
        return new ToolPolicyDecision(true, "");
    }

    public static ToolPolicyDecision deny(String reason) {
        return new ToolPolicyDecision(false, reason == null || reason.isBlank() ? "denied by policy" : reason);
    }
}
