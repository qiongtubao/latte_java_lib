package latte.lib.db.example;

import latte.lib.db.example.rule.TikvRule;
import latte.lib.db.example.tikv.TikvTest;
import latte.lib.db.example.tikv.api.AllApiTest;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(TikvRule.class)
@Suite.SuiteClasses({
    AllApiTest.class
})
public class AllTests {
    @BeforeClass
    public static void setUpTikv() throws InterruptedException {
        System.out.println("setUpTikv" + System.currentTimeMillis());
        Thread.sleep(1000);
    }

    @AfterClass
    public static void shutdownTikv() throws InterruptedException {
        System.out.println("shutdownTikv"+ System.currentTimeMillis());
        Thread.sleep(1000);
    }

}
