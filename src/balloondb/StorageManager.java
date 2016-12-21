package balloondb;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

public class StorageManager {
	
	private HashMap<Class<? extends DataObject>, HashMap<Object, DataObject>> data;
	private Schema schema;
	private StorageDriver storageDriver;
	private File rootDir;
	
	public StorageManager(File rootDir, Schema schema) {
		data = new HashMap<Class<? extends DataObject>, HashMap<Object, DataObject>>();
		this.schema = schema;
		this.rootDir = rootDir;
		storageDriver = new StorageDriver();
	}
	
	public void saveObject(DataObject obj) {
		try {
			storageDriver.saveObject(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public DataObject loadObject(File file) throws ClassNotFoundException, IOException {
		return storageDriver.loadObject(file);
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
	
	public void insertType(Class<? extends DataObject> type) {
		if(!data.containsKey(type))
			data.put(type, new HashMap<Object, DataObject>());
		schema.insert(type);
	}
	
	public HashMap<Class<? extends DataObject>, HashMap<Object, DataObject>> getData() {
		return data;
	}
	
	public void saveAllData() {
		saveSchema();
		for(Entry<Class<? extends DataObject>, HashMap<Object, DataObject>> entry : data.entrySet()) {
			for(Entry<Object, DataObject> objs : entry.getValue().entrySet()) {
				saveObject(objs.getValue());
			}
		}
	}
	
	private void saveSchema() {
		try {
			storageDriver.saveSchema(schema, rootDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void updateSchema() {
		try {
			storageDriver.saveSchema(schema, rootDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public File getRootDir() {
		return rootDir;
	}
	

}
