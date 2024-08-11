package latte.lib.common.thread;

import com.google.common.util.concurrent.MoreExecutors;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import latte.lib.common.concurrent.DynamicBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LatteScheduledThreadPoolExecutor implements ScheduledExecutorService {

  private ScheduledExecutorService schedule;
  private ThreadPoolExecutor executor;

  private static final RejectedExecutionHandler DEFAULT_HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

  static Logger logger = LoggerFactory.getLogger(LatteScheduledThreadPoolExecutor.class);

  int executorCorePoolSize;

  int executorMaximumPoolSize;

  long keepAliveTime;

  String name;

  int queueNum = 0;

  public LatteScheduledThreadPoolExecutor(String name, int scheduleCorePoolSize, int executorCorePoolSize,int executorMaximumPoolSize, long keepAliveTime, int queueSize) {

    this.schedule = MoreExecutors.getExitingScheduledExecutorService(new ScheduledThreadPoolExecutor(
        scheduleCorePoolSize,
        new ThreadFactoryBuilder().setNameFormat("latte-" + name + "-scheduled-%d").build(),
        DEFAULT_HANDLER
    ));
    this.executorCorePoolSize = executorCorePoolSize;
    this.executorMaximumPoolSize = executorMaximumPoolSize;
    this.keepAliveTime = keepAliveTime;
    this.name = name;
    //当队列数量不满时 不会创建临时线程
    this.setExecutorQueueNum(queueSize);
    logger.info("queueNum: {}", queueSize);
  }

  public void setScheduleCorePoolSize(int poolSize) {
    this.executor.setCorePoolSize(poolSize);
  }



  public void setExecutorMaximumPoolSize(int poolSize) {
      this.executor.setMaximumPoolSize(poolSize);
  }

  public void setExecutorQueueNum(int queueNum) {
    if (this.queueNum != queueNum) {
      this.queueNum = queueNum;
      this.executor = new ThreadPoolExecutor(
          this.executorCorePoolSize,
          this.executorMaximumPoolSize,
          this.keepAliveTime,
          TimeUnit.SECONDS,
          new DynamicBlockingQueue<>(queueNum),
          LatteThreadFactory.create(String.format("latte-" + this.name + "-executor")),
          DEFAULT_HANDLER);
    }
  }

  @Override
  public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
    return schedule.schedule(command, delay, unit);
  }

  @Override
  public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
    return schedule.schedule(callable, delay, unit);
  }

  @Override
  public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
    return schedule.scheduleAtFixedRate(command, initialDelay, period, unit);
  }

  @Override
  public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
    return schedule.scheduleWithFixedDelay(command, initialDelay, delay, unit);
  }

  // 代理其他 ExecutorService 方法
  @Override
  public void shutdown() {
    executor.shutdown();
  }

  @Override
  public List<Runnable> shutdownNow() {
    List<Runnable> tasks = executor.shutdownNow();
    return tasks;
  }

  @Override
  public boolean isShutdown() {
    return  executor.isShutdown();
  }

  @Override
  public boolean isTerminated() {
    return  executor.isTerminated();
  }

  @Override
  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    return executor.awaitTermination(timeout, unit);
  }

  @Override
  public <T> Future<T> submit(Callable<T> task) {
    return executor.submit(task);
  }

  @Override
  public <T> Future<T> submit(Runnable task, T result) {
    return executor.submit(task, result);
  }

  @Override
  public Future<?> submit(Runnable task) {
    Future<?> future = executor.submit(task);
//    logger.info("[latte] submit {}", executor.getQueue().size());
    return future;
  }

  @Override
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
    return executor.invokeAll(tasks);
  }

  @Override
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
    return executor.invokeAll(tasks, timeout, unit);
  }

  @Override
  public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
    return executor.invokeAny(tasks);
  }

  @Override
  public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    return executor.invokeAny(tasks, timeout, unit);
  }

  @Override
  public void execute(Runnable command) {
    executor.execute(command);
  }
}
