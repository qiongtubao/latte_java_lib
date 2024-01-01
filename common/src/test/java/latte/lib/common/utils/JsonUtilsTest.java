package latte.lib.common.utils;

import latte.lib.common.utils.JsonUtils;
import lombok.Getter;
import lombok.Setter;
import org.junit.Assert;
import org.junit.Test;

public class JsonUtilsTest {
    @Setter
    @Getter
    static public class User {
        String name;
        public User(String name) {
            this.name = name;
        }
    }

    @Test
    public void decodeTest() {
        User user = new User("zhangsan");
        String result = null;
        try {
             result = JsonUtils.encode(user);
        } catch (Exception e) {
            Assert.fail();
        }
        Assert.assertEquals("{\"name\":\"zhangsan\"}", result);
    }
}
