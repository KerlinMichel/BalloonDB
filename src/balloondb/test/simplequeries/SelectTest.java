package balloondb.test.simplequeries;

import balloondb.test.Person;
import balloondb.test.Tester;
import junit.framework.TestCase;

public class SelectTest extends TestCase {
	
	protected Person p1;
	protected Person p2;
	
	protected void setUp() {
		p1 = new Person("Crimson King", 1000);
		p2 = new Person("Norman", 35);
		Tester.db.insert(p1);
		Tester.db.insert(p2);
	}
	
	public void testSelect() {
		System.out.println("Select tests: ");
		System.out.println("Inserted Person(\"Crimson King\", 1000) and Person(\"Norman\", 35)\n");
		
		System.out.println("Testing select with no conditions:");
		Object result = Tester.db.select("person");
		System.out.println(result);
		assertNotNull(result);
		
		System.out.println("\nTesting select with condition where age = 1000");
		result = Tester.db.select("person where age = 1000");
		System.out.println(result);
		assertNotNull(result);
		
		System.out.println("\nTesting select with condition where name = \"Norman\"");
		result = Tester.db.select("person where name = \"Norman\"");
		System.out.println(result);
		assertNotNull(result);
		
		System.out.println("\nTesting select with condition where age = -1");
		result = Tester.db.select("person where age = -1");
		System.out.println(result);
		assertNull(result);
		
		System.out.println("\nTesting select with condition where age = 14years");
		result = Tester.db.select("person where age = 14years");
		System.out.println(result);
		assertNull(result);
		
		System.out.println("\nTesting select with condition where field = doesnotexist");
		result = Tester.db.select("person where field = doesnotexist");
		System.out.println(result);
		assertNull(result);
		
		System.out.println("----------");
	}

}
