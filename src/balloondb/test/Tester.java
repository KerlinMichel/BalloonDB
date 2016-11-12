package balloondb.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import balloondb.BalloonDB;
import balloondb.DataObject;
import balloondb.test.simplequeries.SimpleQuerySuite;


public class Tester {
	
	private static final File testingDir = new File("/home/kerlinmichel/Desktop/test");
	public static BalloonDB db = new BalloonDB(testingDir);
	
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(SimpleQuerySuite.class);
	
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
		
		System.out.println("The test were " + (result.wasSuccessful() ? "succesful." : " a failure."));
	}

}
