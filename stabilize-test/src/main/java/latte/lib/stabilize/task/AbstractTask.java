package latte.lib.stabilize.task;




import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

@Setter
@Getter
public class AbstractTask implements Task {
    private String name;

    private AtomicBoolean initialized = new AtomicBoolean(false);

    private AtomicBoolean started = new AtomicBoolean(false);

    private AtomicBoolean stopped = new AtomicBoolean(false);

    protected Logger logger = LoggerFactory.getLogger(getClass());

    TaskConfig config;

    ScheduledThreadPoolExecutor scheduled;

    ExecutorService executors;
    public AbstractTask(TaskConfig config, ScheduledThreadPoolExecutor scheduled, ExecutorService executors) {
        this.config = config;
        this.name = getClass().getSimpleName();
        this.scheduled = scheduled;
        this.executors = executors;
    }



    @Override
    public void initialize() {

        if (initialized.compareAndSet(false, true)) {
            doInitialize();
        }
    }

    @Override
    public void start() {

        if (!initialized.get()) {
            throw new IllegalStateException("[start]task: " + name() + ", is not initialized");
        }

        if (started.compareAndSet(false, true)) {
            stopped.set(false);
            doStart();
        }
    }

    @Override
    public void stop() {

        if (!initialized.get()) {
            throw new IllegalStateException("[stop]task: " + name() + ", is not initialized");
        }

        if (stopped.compareAndSet(false, true)) {
            started.set(false);
            doStop();
        }
    }

    @Override
    public boolean isStopped() {
        return stopped.get();
    }

    @Override
    public TaskConfig getConfig() {
        return config;
    }

    @Override
    public String name() {
        return name;
    }

    protected void doStart() {

    }

    protected void doStop() {

    }

    protected void doInitialize() {

    }
}
