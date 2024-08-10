package latte.lib.stabilize;

import latte.lib.api.config.DynamicConfig;
import latte.lib.api.config.StringDynamicConfig;
import org.junit.Assert;
import org.junit.Test;

public class StablizeTestConfigTest {
    @Test
    public void parseClass() throws Exception {
        DynamicConfig config = new StringDynamicConfig("{\n"
            + "  \"stabilize-test\": {\n"
            + "      \"tasks\": {\n"
            + "          \"basic\": {\n"
            + "              \"heartbeat\": {\n"
            + "                \"args\": {\n"
            + "                  \"qps\": 1,\n"
            + "                  \"thread\": 2\n"
            + "                }\n"
            + "              }\n"
            + "          }\n"
            + "      },\n"
            + "      \"scheduledNum\": 10,\n"
            + "      \"executorMinNum\": 10,\n"
            + "      \"executorMaxNum\": 100\n"
            + "  }\n"
            + "}");
        StablizeTestConfig c = config.get("stabilize-test",StablizeTestConfig.class);
        Assert.assertEquals(10, c.getExecutorMinNum());
    }

}
