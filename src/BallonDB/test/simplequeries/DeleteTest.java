package BallonDB.test.simplequeries;

import junit.framework.TestCase;
import BallonDB.DataObject;
import BallonDB.test.Person;
import BallonDB.test.Tester;

public class DeleteTest extends TestCase{
	
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
		System.out.println("Delete tests: ");
		System.out.println("Inserted Person(\"Crimson King\", 1000) and Person(\"Norman\", 35) and force saving Database creating files \n");
		
		System.out.println("Testing delete with condition age = 35");
		Object result = Tester.db.delete("person where age = 35");
		System.out.println(result);
		assertEquals(false, ((DataObject)result).getFile().exists());
		
		System.out.println("----------");
	}

}
