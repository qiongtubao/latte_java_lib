package latte.lib.api.config;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import latte.lib.common.serialization.JsonUtils;

public class FileDynamicConfig extends AbstractIntervalDynamicConfig{
  String filename;
  FileDynamicConfig(String filename, int interval,
      ScheduledExecutorService scheduledExecutorService) throws Exception {
    super(interval, scheduledExecutorService);
    this.filename = filename;
    this.loadCached();
  }

  @Override
  Map<String, Object> loadCached() throws Exception {
    return JsonUtils.decodeJsonFile(filename, Map.class);
  }
}
