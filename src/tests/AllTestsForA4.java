package tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ MutationTest.class, Parertester.class, TestMutationDuplicate.class, TestMutationInsert.class,
		TestMutationRemove.class, TestMutationReplace.class, TestMutationSwap.class, TestMutationTransform.class,
		TestTreeParsing.class })
public class AllTestsForA4 {

}
