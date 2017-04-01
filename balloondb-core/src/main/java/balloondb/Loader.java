package balloondb;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.List;

/*
 * This will load files to prep balloondb
 */
public class Loader {
	
	private Loader(){};
	
	public static void loadSchema(Schema schema, File file) throws IOException, ClassNotFoundException {
		File schemaFile = new File(file.toString(), "/.schema");
		if(!schemaFile.exists())
			return;
		List<String> config = Files.readAllLines(schemaFile.toPath());
		for(String params : config) {
			String[] param = params.split("=");
			switch(param[0]){
				case "types" :	 
					//expects a list like such [type1, type2, ...]
					for(String classFile : param[1].replaceAll("[\\[\\]]", "").split(",")) {
						String[] path = classFile.split("\\.");
						File typeFile = new File(file.toString(), 
								classFile.toString().replace(".", "/") + "/" + path[path.length-1] + ".class");
						if(typeFile.exists()) {
							ClassLoader ucl = new URLClassLoader(
									new URL[]{new File(file.toString(), classFile.toString().replace(".", "/")).toURI().toURL()});
							schema.insert((Class<? extends DataObject>)Class.forName(path[path.length-1], true, ucl));
						}
					}
					break;
			}
		}
	}

}
