package balloondb.test;

import balloondb.BalloonDB;
import junit.framework.TestCase;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 * Created by kerlinmichel on 4/1/17.
 */
@Ignore
public class BalloonDBTestCase extends TestCase {

    @BeforeClass
    public static void init() {
        Tester.loadProperties();
        Tester.db = new BalloonDB(Tester.testingDir);
    }

}
