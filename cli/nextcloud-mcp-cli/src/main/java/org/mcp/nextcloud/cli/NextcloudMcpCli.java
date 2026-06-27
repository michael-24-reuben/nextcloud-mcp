package org.mcp.nextcloud.cli;

public final class NextcloudMcpCli {
    private NextcloudMcpCli() {
    }

    public static void main(String[] args) {
        int exitCode = new NextcloudMcpCliApplication().run(args, System.out, System.err);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }
}
