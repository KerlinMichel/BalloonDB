package balloondb.test.simplequeries;

import balloondb.test.BalloonDBTestCase;
import balloondb.test.Person;
import balloondb.test.Tester;

public class InsertTest extends BalloonDBTestCase {
	
	
	protected void setUp() {
		Tester.init();
		Tester.db.getStorage().insertType(Person.class);
		Tester.db.delete("person"); 
	}
	
	public void testInsert() {
		System.out.println("Insert tests: ");
		System.out.println("Deleted all entries in Person type. Added Person type to schema.");
		
		System.out.println("Selecting from Person type, should be empty :");
		Object result = Tester.db.select("person");
		System.out.println(result);
		assertNull(result);
		
		System.out.println("\nInsert Person(\"Crimson King\", 1000)");
		result = Tester.db.insert("Person(\"Crimson King\", 1000)");
		System.out.println(result);
		assertNotNull(result);
		
		System.out.println("\nSelect Person where age is 1000");
		result = Tester.db.select("person where age = 1000");
		System.out.println(result);
		assertNotNull(result);
		
		System.out.println("----------");
	}

}
