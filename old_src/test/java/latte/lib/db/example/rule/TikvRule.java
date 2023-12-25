package latte.lib.db.example.rule;

import org.junit.BeforeClass;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.junit.runners.model.Statement;

import java.util.List;

public class TikvRule  extends Suite {


    public TikvRule(Class<?> klass, RunnerBuilder builder) throws InitializationError {
        super(klass, builder);
    }

    public TikvRule(RunnerBuilder builder, Class<?>[] classes) throws InitializationError {
        super(builder, classes);
    }

    protected TikvRule(Class<?> klass, Class<?>[] suiteClasses) throws InitializationError {
        super(klass, suiteClasses);
    }

    protected TikvRule(RunnerBuilder builder, Class<?> klass, Class<?>[] suiteClasses) throws InitializationError {
        super(builder, klass, suiteClasses);
    }

    protected TikvRule(Class<?> klass, List<Runner> runners) throws InitializationError {
        super(klass, runners);
    }


    @Override
    protected Statement withAfterClasses(Statement statement) {
        Statement statement1 = super.withAfterClasses(statement);
        System.out.println("withAfterClasses");
        return  statement1;
    }

}
