package BallonDB.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import BallonDB.BallonDB;
import BallonDB.DataObject;
import BallonDB.test.simplequeries.SimpleQuerySuite;

public class Tester {
	
	private static final File testingDir = new File("/home/kerlinmichel/Desktop/test");
	public static BallonDB db = new BallonDB(testingDir);
	
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(SimpleQuerySuite.class);
	
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
		
		System.out.println(result.wasSuccessful());
	}

}
