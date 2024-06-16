package latte.lib.stabilize.task;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskConfig {

  Map<String, String> args;

  public TaskConfig() {

  }

  public int getIntArg(String key) {
    return Integer.valueOf(this.getArg(key));
  }

  public String getArg(String key) {
    return this.args.get(key);
  }

  public double getDoubleArg(String key) {
    return Double.valueOf(this.getArg(key));
  }
}