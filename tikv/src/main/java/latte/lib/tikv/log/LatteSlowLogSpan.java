package latte.lib.tikv.log;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tikv.common.log.SlowLogSpan;
import org.tikv.common.log.SlowLogSpanEmptyImpl;
import org.tikv.shade.com.google.gson.JsonElement;
import org.tikv.shade.com.google.gson.JsonObject;

public class LatteSlowLogSpan implements SlowLogSpan  {
    String name;

    Logger logger = LoggerFactory.getLogger(LatteSlowLogSpan.class);
    public LatteSlowLogSpan(String name) {
        this.name = name;
    }

    long start_time;
    @Override
    public void addProperty(String key, String value) {

    }

    @Override
    public void start() {
        start_time = System.currentTimeMillis();
    }

    @Override
    public void end() {
        logger.info("{} use {}ms", name, System.currentTimeMillis() - start_time);
        System.out.println(String.format("%s use %d ms", name, (System.currentTimeMillis() - start_time)));
    }

    @Override
    public JsonElement toJsonElement() {
        return new JsonObject();
    }
}
