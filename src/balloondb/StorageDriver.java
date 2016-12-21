package balloondb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class StorageDriver {
	
	private FileInputStream fileIn;
	private ObjectInputStream objIn;
	
	private FileOutputStream fileOut;
	private ObjectOutputStream objOut;
	
	public StorageDriver() {
		
	}
	
	public void saveObject(DataObject obj) throws IOException {
		File file = obj.getFile();
		if(!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		fileOut = new FileOutputStream(obj.getFile());
		objOut = new ObjectOutputStream(fileOut);
		objOut.writeObject(obj);
		objOut.close();
		fileOut.close();
	}
	
	public DataObject loadObject(File file) throws IOException, ClassNotFoundException {
		DataObject obj = null;
		
		fileIn = new FileInputStream(file);
        objIn = new ObjectInputStream(fileIn);
        
        obj = (DataObject) objIn.readObject();
        
        objIn.close();
        fileIn.close();
        
        if(obj != null)
        	obj.initMethods();
		
		return obj;
	}
	
	public void saveSchema(Schema schema, File file) throws IOException {
		String args = "types=[";
		for(Class<? extends DataObject> type : schema.getTypes().getAllTypes()) {
			args += type.getCanonicalName() + ",";
		}
		args += "]\n";
		File sch = new File(file.getAbsoluteFile() + "/.schema");
		Files.write(sch.toPath(), args.getBytes(StandardCharsets.UTF_8));
	}

}
