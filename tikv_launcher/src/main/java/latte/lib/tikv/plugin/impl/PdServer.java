package latte.lib.tikv.plugin.impl;

import latte.lib.tikv.plugin.Shell;

public class PdServer extends AbstractServer {
    public final String shell_dir = "scripts";
    public final String start_pd_shell = "start_pd.sh";
    public final String stop_pd_shell = "stop_pd.sh";
    private int port;

    public PdServer(int port) {
        this.port = port;
    }
    @Override
    public void start() throws Exception{
        assert checkPortIsUsed(port) == false: String.format("pd port(%d) is used", port);
        Shell.execResourceShellFile(this.getClass(), shell_dir, start_pd_shell, String.valueOf(port));
        Thread.sleep(3000);
        assert checkPortIsUsed(port) == true: String.format("pd port(%d) is start fail", port);
    }

    @Override
    public void stop() throws Exception {
        assert checkPortIsUsed(port) == true: String.format("pd port(%d) is stoped", port);
        Shell.execResourceShellFile(this.getClass(), shell_dir, stop_pd_shell, String.valueOf(port));
        Thread.sleep(3000);
        assert checkPortIsUsed(port) == false: String.format("pd port(%d) is stop fail", port);
    }

    @Override
    public String logMatch(String match) {
        return null;
    }
}
