package latte.lib.stabilize.task;


public interface Task {

    void initialize() throws Exception;

    void start();

    void stop();

    String name();

    boolean isStopped();

    TaskConfig getConfig();

}
