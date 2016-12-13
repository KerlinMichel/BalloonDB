package balloondb.test.simplequeries;

import balloondb.DataObject;
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
	
	public void testCreate() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		System.out.println("Create tests: ");
		System.out.println("Inserted Person(\"Crimson King\", 1000) and Person(\"Norman\", 35) and force saving Database creating files \n");
		
		System.out.println("Create type Horse");
		Object result = Tester.db.create("type Horse { String name; boolean mountable; }");
		System.out.println(result);
		assertNotNull(result);
		Class<? extends DataObject> horseType = null;
		try {
			horseType = Tester.db.getSchema().getTypes().get("Horse");
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertNotNull(horseType);
		
		System.out.println("----------");
	}
	
}
