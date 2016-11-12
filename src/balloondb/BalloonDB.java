package balloondb;

import java.io.File;

import balloondb.query.Query;

public class BalloonDB {
	
	private Schema schema;
	private Storage storage;
	
	public BalloonDB(File file) {
		schema = new Schema();
		storage = new Storage(file, schema);
	}
	
	public void insert(DataObject obj) {
		try {
			storage.insert(obj);
		} catch (EntityIntegerityException e) {
			//e.printStackTrace(); 
		}
	}
	
	public Object select(String query) {
		Query q = new Query(query);
		return q.select(this);
	}
	
	public Object delete(String query) {
		Query q = new Query(query);
		return q.delete(this);
	}
	
	public void inflateDirectory() {
		inflateDirectoryRec(storage.getRootDir());
	}
	
	private void inflateDirectoryRec(File file) {
		for(File f : file.listFiles()) {
			if(f.isDirectory())
				inflateDirectoryRec(f);
			else {
				if(f.getPath().contains(".ser"))
					storage.loadObject(f);
			}
		}
	}
	
	private void readObjectFile(File file) {
		DataObject obj = null;
		if(!file.getPath().contains(".ser"))
			return;
		obj = storage.loadObject(file);
		if(obj != null) {
			insert(obj);
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
	
	public Storage getStorage() {
		return storage;
	}

}
