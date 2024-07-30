package latte.lib.stabilize.task;


public interface Task {

    void initialize() throws Exception;

    void start();

    void stop() throws IllegalStateException;

    String name();

    boolean isStopped();

    TaskConfig getConfig();

}
