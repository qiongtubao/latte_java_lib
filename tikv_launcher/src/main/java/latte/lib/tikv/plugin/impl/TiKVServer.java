package latte.lib.tikv.plugin.impl;

import latte.lib.tikv.plugin.Shell;

public class TiKVServer extends AbstractServer {
    final String shell_dir = "scripts";
    final String start_shell = "start_tikv_server.sh";
    final String stop_shell = "stop_tikv_server.sh";
    private int port;
    private int pd_port;

    public TiKVServer(int port, int pd_port) {
        this.port = port;
        this.pd_port = pd_port;
    }

    @Override
    public void start() throws Exception{
        assert checkPortIsUsed(port) == false: String.format("tikv port(%d) is used", port);
        Shell.execResourceShellFile(this.getClass() ,shell_dir, start_shell, String.valueOf(port), String.valueOf(pd_port));
        Thread.sleep(10000);
        assert checkPortIsUsed(port) == true: String.format("tikv port(%d) is start fail", port);
    }

    @Override
    public void stop() throws Exception {
        assert checkPortIsUsed(port) == true: String.format("tikv port(%d) is stoped", port);
        Shell.execResourceShellFile(this.getClass(), shell_dir, stop_shell, String.valueOf(port));
        Thread.sleep(3000);
        assert checkPortIsUsed(port) == false: String.format("tikv port(%d) is stop fail", port);
    }



    @Override
    public String logMatch(String match) {
        return null;
    }
}
