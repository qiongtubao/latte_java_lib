package latte.lib.stabilize.task;




import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
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

    ScheduledExecutorService scheduled;

    public AbstractTask(TaskConfig config, ScheduledExecutorService scheduled) {
        this.config = config;
        this.name = getClass().getSimpleName();
        this.scheduled = scheduled;
    }



    @Override
    public void initialize() throws Exception {

        if (initialized.compareAndSet(false, true)) {
            doInitialize();
        }
    }

    @Override
    public void start() {

        if (!initialized.get()) {
            throw new IllegalStateException("[start]task: " + name() + ", is not initialized");
        }
        if (stopped.get() == true) return;
        if (started.compareAndSet(false, true)) {
            stopped.set(false);
            doStart();
        }
    }

    @Override
    public void stop() throws IllegalStateException {

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
        logger.info("{} {} start!", this.getClass().getSimpleName(), this);
    }

    protected void doStop() {
        logger.info("{} {} stop!", this.getClass().getSimpleName(), this);
    }

    protected void doInitialize() throws Exception {
        logger.info("{} initialize ok!", this.getClass().getSimpleName());
    }
}
