package latte.lib.common.thread;



public interface LatteRejectedExecutionHandler {
  void rejectedExecution(Runnable r, LatteThreadPoolExecutor executor);
}
