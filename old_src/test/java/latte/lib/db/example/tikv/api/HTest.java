package latte.lib.db.example.tikv.api;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


public class HTest {
    @BeforeClass
    public static void beforeClass() throws InterruptedException {
        System.out.println("Test beforeClass " + System.currentTimeMillis());
        Thread.sleep(1000);
    }
    @Before
    public void before() {
        System.out.println("Test before");
    }
    @Test
    public void t() {
        System.out.println("t1");
        System.out.println("t");
        System.out.println("t2");
    }
    @AfterClass
    public static void afterClass() throws InterruptedException {
        System.out.println("Test afterClass " + + System.currentTimeMillis());
        Thread.sleep(1000);
    }
    @After
    public void after() {
        System.out.println("Test after");
    }
}
