package latte.lib.tikv.plugin.impl;

import latte.lib.tikv.plugin.Server;

import java.net.Socket;

public abstract class AbstractServer implements Server {
    protected int port;

    public boolean checkPortIsUsed(int port) {
        try {
            Socket socket = new Socket("localhost", port);
            socket.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
