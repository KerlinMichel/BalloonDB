package balloondb.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import balloondb.BalloonDB;
import balloondb.test.queryconditions.EqualityAndRelationalTest;
import balloondb.test.simplequeries.SimpleQuerySuite;


public class Tester {
	
	public static File testingDir;
	public static BalloonDB db;
	
	public static void main(String[] args) {
		Tester.loadProperties();
		db = new BalloonDB(testingDir);

		Result result = JUnitCore.runClasses(SimpleQuerySuite.class, 
				EqualityAndRelationalTest.class);
	
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}

		System.out.println("File: " + new File("./").getName());;

		System.out.println("The tests were " + (result.wasSuccessful() ? "succesful." : "a failure."));
	}

	public static void loadProperties() {
		Properties variables = new Properties();
		try {
			variables.load(new FileInputStream(new File("./.properties")));
		} catch(IOException e) {
			e.printStackTrace();
		}

		for (Map.Entry<Object, Object> variable : variables.entrySet()) {
			System.out.println("Loading " + variable.getKey());
			if(variable.getKey().toString().equals("TEST_STORAGE_DIR"))
				testingDir = new File(variable.getValue().toString());
		}
	}

}
