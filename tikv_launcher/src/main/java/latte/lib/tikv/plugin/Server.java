package latte.lib.tikv.plugin;

public interface Server {
    void start() throws Exception;
    void stop() throws Exception;
    String logMatch(String match);
}
