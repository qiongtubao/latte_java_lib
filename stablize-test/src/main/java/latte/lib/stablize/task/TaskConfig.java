package latte.lib.stablize.task;

import java.util.Map;
import latte.lib.common.serialization.JsonUtils;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskConfig {
  String taskName;
  Map<String, String> args;

  public TaskConfig() {

  }

  public int getIntArg(String key) {
    String value = this.args.get(key);
    return Integer.valueOf(value);
  }
}