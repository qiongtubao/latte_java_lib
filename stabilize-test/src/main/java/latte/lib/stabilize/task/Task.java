package latte.lib.stabilize.task;


public interface Task {

    void initialize();

    void start();

    void stop();

    String name();

    boolean isStopped();

    TaskConfig getConfig();

}
