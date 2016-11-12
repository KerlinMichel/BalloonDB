package BallonDB;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import BallonDB.test.Person;

public class Schema {
	
	private List<Class<? extends DataObject>> classes;
	private boolean absoluteEntityIntegerity;
	
	public Schema() {
		classes = new ArrayList<Class<? extends DataObject>>();
		absoluteEntityIntegerity = false; 
		//classes.add(Person.class);
	}
	
	public boolean check(DataObject o) {
		boolean a = false;
		for(Class c : classes) {
			if(c.isInstance(o) ) {
				//System.out.println("methods: "  + c.getMethods()[0].getName());
				String method = c.getMethods()[0].getName();
				try {
					Method m = c.getDeclaredMethod(method);
					System.out.println("lord help: " + m.invoke(o));
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				a = true;
			}
		}
		return a;
	}
	
	public void insert(Class<? extends DataObject> type) {
		if(!classes.contains(type))
			classes.add(type);
	}

	public List<Class<? extends DataObject>> getClasses() {
		return classes;
	}
	
	
	public boolean entityIntegerity() {
		return absoluteEntityIntegerity;
	}
	

}
