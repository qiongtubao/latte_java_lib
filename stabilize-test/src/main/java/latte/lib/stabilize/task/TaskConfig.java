package latte.lib.stabilize.task;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskConfig {

  Map<String, Object> args;

  public TaskConfig() {

  }

  public int getIntArg(String key) {
    return (int)this.getArg(key);
  }

  public String getArgStr(String key) {
    return (String) getArg(key);
  }

  public double getDoubleArg(String key) {
    Object d = this.getArg(key);
    if (d instanceof Double) {
      return (double)d;
    }
    return Double.valueOf(String.valueOf(d));
  }

  public Object getArg(String key) {
    return this.args.get(key);
  }
}