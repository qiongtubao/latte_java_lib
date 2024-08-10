package latte.lib.common.thread;

import com.google.common.util.concurrent.MoreExecutors;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LatteScheduledThreadPoolExecutor implements ScheduledExecutorService {

  private ScheduledExecutorService schedule;
  private ThreadPoolExecutor executor;

  private static final RejectedExecutionHandler DEFAULT_HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

  static Logger logger = LoggerFactory.getLogger(LatteScheduledThreadPoolExecutor.class);
  public LatteScheduledThreadPoolExecutor(String name, int scheduleCorePoolSize, int executorCorePoolSize,int executorMaximumPoolSize, long keepAliveTime) {

    this.schedule = MoreExecutors.getExitingScheduledExecutorService(new ScheduledThreadPoolExecutor(
        scheduleCorePoolSize,
        new ThreadFactoryBuilder().setNameFormat("latte-" + name + "-scheduled-%d").build(),
        DEFAULT_HANDLER
    ));
    //当队列数量不满时 不会创建临时线程
    this.executor = new ThreadPoolExecutor(
        executorCorePoolSize,
        executorMaximumPoolSize,
        keepAliveTime,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(10000),
        LatteThreadFactory.create(String.format("latte-" + name + "-executor")),
        DEFAULT_HANDLER);
    logger.info("executorMaximumPoolSize: {}", executorMaximumPoolSize);
  }

  public void setScheduleCorePoolSize(int poolSize) {
    this.executor.setCorePoolSize(poolSize);
  }



  public void setExecutorMaximumPoolSize(int poolSize) {
      this.executor.setMaximumPoolSize(poolSize);
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
    logger.info("[latte] submit {}", executor.getMaximumPoolSize());
    Future<?> future = executor.submit(task);
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
