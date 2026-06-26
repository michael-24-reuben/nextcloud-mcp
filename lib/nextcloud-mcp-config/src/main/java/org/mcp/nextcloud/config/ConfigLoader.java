package org.mcp.nextcloud.config;

import java.io.IOException;
import java.nio.file.Path;

public interface ConfigLoader {
    NextcloudMcpConfig load(Path path) throws IOException;
}
