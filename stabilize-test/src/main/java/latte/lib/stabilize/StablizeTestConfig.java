package latte.lib.stabilize;

import java.util.Map;
import latte.lib.api.config.AbstractDynamicConfigClass;
import latte.lib.api.config.DynamicConfig;
import latte.lib.common.serialization.JsonUtils;
import latte.lib.stabilize.task.TaskConfig;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StablizeTestConfig extends AbstractDynamicConfigClass {
  Map<String, Map<String, TaskConfig>> tasks;
  int executorNum = 100;
  int scheduledNum = 100;


  public StablizeTestConfig() {

  }


  @Override
  public void change(Object o) throws Exception {
    StablizeTestConfig now = (StablizeTestConfig) o;
    this.exchange(now);
    this.notify("this", now, this);
  }

  public void exchange(StablizeTestConfig now) {
    int oldExecutorNum = this.executorNum;
    this.executorNum = now.executorNum;
    now.executorNum = oldExecutorNum;

    int oldScheduleNum = this.scheduledNum;
    this.scheduledNum = now.scheduledNum;
    now.scheduledNum = oldScheduleNum;

    Map<String, Map<String,TaskConfig>> oldTasks = this.tasks;
    this.tasks = now.tasks;
    now.tasks = oldTasks;
  }

  //0. unupdate
  //1. update executorNum or scheduledNum
  //2. update tasks
  //3. all
  //4. clean
  public int needUpdate(StablizeTestConfig config) {
    if (config == null) {
      return 4;
    }
    //取巧  all(3) = update executor or scheeduledNum(1) + update tasks(2)
    int result = 0;
    if (config.executorNum == this.executorNum
    || config.scheduledNum == this.scheduledNum) {
        result += 1;
    }
    if (config.tasks.size() != this.tasks.size()) {
      return result + 2;
    }
    try {
      if (JsonUtils.encode(config.tasks).equals(JsonUtils.encode(this.tasks))) {
        return result;
      } else {
        return result + 2;
      }
    } catch (Exception e) {
      //暂时如何转换成字符串错误的话 就不更新  防呆
      return 0;
    }

  }
}
