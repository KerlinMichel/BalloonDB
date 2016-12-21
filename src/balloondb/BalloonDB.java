package balloondb;

import java.io.File;
import java.io.IOException;

import balloondb.query.Query;
import balloondb.query.QuerySyntaxError;

public class BalloonDB {
	
	private Schema schema;
	private StorageManager storage;
	
	public BalloonDB(File file) {
		schema = new Schema(file);
		storage = new StorageManager(file, schema);
	}
	
	public void insert(DataObject obj) {
		try {
			storage.insert(obj);
		} catch (EntityIntegerityException e) {
			//e.printStackTrace(); 
		}
	}
	
	public Object create(String query) {
		Query q = new Query(query);
		try {
			return q.create(this);
		} catch (QuerySyntaxError e) {
			//e.printStackTrace();
			return null;
		}
	}
	
	public Object select(String query) {
		Query q = new Query(query);
		try {
			return q.select(this);
		} catch (QuerySyntaxError e) {
			//e.printStackTrace();
			return null;
		}
	}
	
	public Object insert(String query) {
		Query q = new Query(query);
		try {
			return q.insert(this);
		} catch (QuerySyntaxError e) {
			//e.printStackTrace();
			return null;
		}
	}
	
	public Object delete(String query) {
		Query q = new Query(query);
		try {
			return q.delete(this);
		} catch (QuerySyntaxError e) {
			//e.printStackTrace();
			return null;
		}
	}
	
	public void inflateDirectory() throws ClassNotFoundException, IOException {
		inflateDirectoryRec(storage.getRootDir());
	}
	
	private void inflateDirectoryRec(File file) throws ClassNotFoundException, IOException {
		for(File f : file.listFiles()) {
			if(f.isDirectory())
				inflateDirectoryRec(f);
			else {
				if(f.getPath().contains(".ser"))
					storage.loadObject(f);
			}
		}
	}
	
	public void forceSave() {
		storage.saveAllData();
	}
	
	public static void creatDatabase() {
		
	}
	
	public Schema getSchema() {
		return schema;
	}
	
	public StorageManager getStorage() {
		return storage;
	}

}
