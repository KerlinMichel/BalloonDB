package balloondb.test.queryconditions;

import java.util.List;

import balloondb.test.BalloonDBTestCase;
import balloondb.test.Person;
import balloondb.test.Tester;

public class EqualityAndRelationalTest extends BalloonDBTestCase {
	
	protected Person p1;
	protected Person p2;
	
	protected void setUp() {
		p1 = new Person("Crimson King", 1000);
		p2 = new Person("Norman", 35);
		Tester.db.insert(p1);
		Tester.db.insert(p2);
		Tester.db.forceSave();
	}
	
	public void testEqualityAndRealtional() {
		System.out.println("Equality and Realtional tests: ");
		System.out.println("Inserted Person(\"Crimson King\", 1000) and Person(\"Norman\", 35) and force saving Database creating files \n");
	
		System.out.println("Testing select with condition where name = \"Crimson King\"");
		Object result = Tester.db.select("person where name = \"Crimson King\"");
		System.out.println(result);
		assertNotNull(result);
		
		System.out.println("\nTesting select with condition where age < 35");
		result = Tester.db.select("person where age < 35");
		System.out.println(result);
		assertNull(result);
		
		System.out.println("\nTesting select with condition where age >= 35");
		result = Tester.db.select("person where age >= 35");
		System.out.println(result);
		assertNotNull(result);
		assertTrue(result instanceof List<?>);
		assertTrue(((List<?>)result).size() == 2);
		
		System.out.println("\nTesting select with condition where name != \"Crimson King\"");
		result = Tester.db.select("person where name != \"Crimson King\"");
		System.out.println(result);
		assertNotNull(result);
		assertTrue(((Person)result).getName().equals("Norman"));
	}

}
