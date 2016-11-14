package balloondb.test.simplequeries;

import balloondb.test.Person;
import balloondb.test.Tester;
import junit.framework.TestCase;

public class CreateTest extends TestCase {
	protected Person p1;
	protected Person p2;
	
	protected void setUp() {
		p1 = new Person("Crimson King", 1000);
		p2 = new Person("Norman", 35);
		Tester.db.insert(p1);
		Tester.db.insert(p2);
		Tester.db.forceSave();
	}
	
	public void testDelete() {
		System.out.println("Create tests: ");
		System.out.println("Inserted Person(\"Crimson King\", 1000) and Person(\"Norman\", 35) and force saving Database creating files \n");
		
		System.out.println("Create type knight");
		Object result = Tester.db.create("type Horse (String name, boolean mountable) : pk name");
		System.out.println(result);
		//assertEquals(false, ((DataObject)result).getFile().exists());
		
		System.out.println("----------");
	}
}
