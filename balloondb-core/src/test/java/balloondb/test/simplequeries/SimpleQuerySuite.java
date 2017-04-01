package balloondb.test.simplequeries;

import balloondb.BalloonDB;
import balloondb.test.Tester;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({ 
   SelectTest.class, DeleteTest.class, CreateTest.class, InsertTest.class
})

public class SimpleQuerySuite {

    @BeforeClass
    public static void init() {
        Tester.loadProperties();
        Tester.db = new BalloonDB(Tester.testingDir);
    }

}
