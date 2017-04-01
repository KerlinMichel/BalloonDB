package balloondb.test.queryconditions;

import balloondb.BalloonDB;
import balloondb.test.Tester;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
	EqualityAndRelationalTest.class
})

public class QueryConditionsSuite {

	@BeforeClass
	public static void init() {
		Tester.loadProperties();
		Tester.db = new BalloonDB(Tester.testingDir);
	}
}
