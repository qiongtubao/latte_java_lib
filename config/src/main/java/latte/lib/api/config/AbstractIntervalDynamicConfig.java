package latte.lib.api.config;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractIntervalDynamicConfig extends AbstractDynamicConfig {
    Map<String, String> cached = null;
    enum Status {
      BUSY,
      IDLE
    };
    Status status = Status.IDLE;
    public int interval = 60;
    protected ScheduledExecutorService scheduledExecutorService;
    Logger logger = LoggerFactory.getLogger(getClass());


    AbstractIntervalDynamicConfig(int interval, ScheduledExecutorService scheduledExecutorService) {
      this.interval = interval;
      this.scheduledExecutorService = scheduledExecutorService;
      AbstractDynamicConfig self = this;
      scheduledExecutorService.scheduleAtFixedRate(() -> {
        this.updateCached();
      }, this.interval, this.interval, TimeUnit.SECONDS);
    }

  abstract Map<String,Object> loadCached() throws Exception;
  boolean updateCached() {
    if (this.status == Status.BUSY) {
      return true;
    }
    boolean result = true;
    synchronized (this.status) {
      this.status = Status.BUSY;
      try {
        setCached(loadCached());
      } catch (Exception e) {
        result = false;
      }
      this.status = Status.IDLE;
    }
    return result;
  }

}
