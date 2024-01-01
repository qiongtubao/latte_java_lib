package latte.lib.stablize.task;

public interface Task {
    void initialize();

    void start();

    void stop();

    String name();

    boolean isStopped();
}
