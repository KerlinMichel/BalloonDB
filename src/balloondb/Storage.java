package balloondb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map.Entry;

public class Storage {
	
	private HashMap<Class<? extends DataObject>, HashMap<Object, DataObject>> data;
	private Schema schema;
	private File rootDir;
	
	public Storage(File rootDir, Schema schema) {
		data = new HashMap<Class<? extends DataObject>, HashMap<Object, DataObject>>();
		this.schema = schema;
		this.rootDir = rootDir;
	}
	
	public void saveObject(File file, DataObject obj) {
		try {
			FileOutputStream fileOut = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
	        out.writeObject(obj);
	        out.close();
	        fileOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public DataObject loadObject(File file) {
		DataObject obj = null;
		try {
			FileInputStream fileIn = new FileInputStream(file);
	        ObjectInputStream in = new ObjectInputStream(fileIn);
	        obj = (DataObject) in.readObject();
	        in.close();
	        fileIn.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if(obj != null)
			obj.initMethods();
			try {
				insert(obj);
			} catch (EntityIntegerityException e) {
				e.printStackTrace();
			}
		return obj;
	}
	
	public void insert(DataObject obj) throws EntityIntegerityException {
		if(obj.getKey() == null)
			obj.generateKey();
		obj.setFile(rootDir);
		Class<? extends DataObject> objType = obj.getClass();
		if(data.containsKey(objType)) {
			HashMap<Object, DataObject> objects = data.get(objType);
			if(objects.containsKey(obj.getKey())) 
				throw new EntityIntegerityException();
			else
				data.get(objType).put(obj.getKey(), obj);
		}
		else {
			HashMap<Object, DataObject> objects = new HashMap<Object, DataObject>();
			objects.put(obj.getKey(), obj);
			data.put(objType, objects);
			schema.insert(objType);
		}
	}
	
	public HashMap<Class<? extends DataObject>, HashMap<Object, DataObject>> getData() {
		return data;
	}
	
	public void saveAllData() {
		for(Entry<Class<? extends DataObject>, HashMap<Object, DataObject>> entry : data.entrySet()) {
			for(Entry<Object, DataObject> objs : entry.getValue().entrySet()) {
				try {
					 File file = objs.getValue().getFile();
					 file.getParentFile().mkdirs();
					 file.createNewFile();
			         saveObject(file, objs.getValue());
			      }catch(IOException i) {
			         i.printStackTrace();
			      }
			}
		}
	}
	
	public File getRootDir() {
		return rootDir;
	}
	

}
