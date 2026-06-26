package org.mcp.nextcloud.tool.runtime;

import org.mcp.nextcloud.security.PrincipalContext;
import org.mcp.nextcloud.tool.api.ToolDescriptor;

public interface ToolPolicyInterceptor {
    ToolPolicyDecision evaluate(ToolDescriptor descriptor, PrincipalContext principalContext);
}
