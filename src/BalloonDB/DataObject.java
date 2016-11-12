package BalloonDB;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DataObject implements Serializable {
	private static final long serialVersionUID = 1L;
	//@Primary_Key
	private Object key;
	private File file;
	
	private transient HashMap<String, Method> methodMap;
	
	public DataObject() {
		methodMap = new HashMap<String, Method>();
		addMethods(this.getClass().getDeclaredMethods());
	}
	
	public void addMethods(Method[] methods) {
		for(Method m : methods) {
			this.methodMap.put(m.getName(), m);
		}
	}
	
	public void initMethods() {
		addMethods(this.getClass().getDeclaredMethods());
	}
	
	public Object invoke(String methodName, Object... params) {
		Method m = methodMap.get(methodName);
		if(m == null)
			return null;
		try {
			return m.invoke(this, params);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Object getKey() {
		return key;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof DataObject) {
			return ( this.key == ((DataObject)o).key && this.key != null && ((DataObject)o).key != null);
		}
		return false;
	}
	
	public Object generateKey() {
		UUID key = UUID.randomUUID();
		this.key = key;
		return key;
	}
	
	public void setFile(File rootDir) {
		if(file == null)
			file = new File(rootDir.getAbsoluteFile() + "/" + getClass().getCanonicalName().replace(".", "/") + "/" + key + ".ser");
	}
	
	public File getFile() {
		return file;
	}
	
	public void delete() {
		file.delete();
	}

}
