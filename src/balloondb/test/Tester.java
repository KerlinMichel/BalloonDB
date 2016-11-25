package balloondb.test;

import java.io.File;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import balloondb.BalloonDB;
import balloondb.test.simplequeries.SimpleQuerySuite;


public class Tester {
	
	private static final File testingDir = new File("/home/kerlinmichel/Desktop/test");
	public static BalloonDB db = new BalloonDB(testingDir);
	
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(SimpleQuerySuite.class);
	
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
		
		System.out.println("The tests were " + (result.wasSuccessful() ? "succesful." : "a failure."));
	}

}
