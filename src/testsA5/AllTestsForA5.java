package testsA5;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ critterTest.class, ExecutorTest.class, InterpreterTest.class, WorldTest.class })
public class AllTestsForA5 {

}
