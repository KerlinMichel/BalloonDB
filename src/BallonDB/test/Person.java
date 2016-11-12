package BallonDB.test;

import BalloonDB.DataObject;

public class Person extends DataObject {
	
	private String name;
	private int age;
	
	public Person(String name, int age) {
		super();
		this.name = name;
		this.age = age;
	}
	
	public String getName() {
		return name;
	}
	
	public String getName(int a) {
		return name;
	}
	
	@Override
	public String toString() {
		return "The Person, " + name  + ", is " + age + " years old";
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}
