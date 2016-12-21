package balloondb;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class Schema {

	private PathHashMap types;
	private boolean absoluteEntityIntegerity;
	
	public Schema(File root) {
		types = new PathHashMap();
		absoluteEntityIntegerity = false; 
		try {
			Loader.loadSchema(this, root);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void insert(Class<? extends DataObject> type) {
		types.put(type);
	}

	public PathHashMap getTypes() {
		return types;
	}
	
	
	public boolean entityIntegerity() {
		return absoluteEntityIntegerity;
	}
	//This is hack TODO: Make this not a hack
	public static Class<? extends DataObject> generateClass(String src, BalloonDB bdb) throws ClassNotFoundException {
		Scanner scn = new Scanner(src);
		scn.next(); //remove create word
		String type = "";
		if(!scn.next().equals("type")) {
			scn.close();
			return null;
		}
		String typeName = scn.next();
		String accessType = "";
		type += "public class " + typeName + " extends balloondb.DataObject {";
		scn.next();
		while(scn.hasNext()) {
			String token = scn.next();
			switch(token) {
				case "boolean" :
				case "byte" :
				case "char" : 
				case "short" : 
				case "int" : 
				case "long" : 
				case "float" :
				case "String" : 
				case "double" : type += "\n " + (accessType.isEmpty() ? "private " : accessType + " ")
								+ token + " " + scn.next(); break; 
				case "private" : 
				case "public" : type += "\n " + token + " ";
					while(!token.contains(";"))
						type += (token = scn.next()) + " ";
					type += "\n";
					break;
				case "private:" :
				case "public:" : accessType = token.replace(":", ""); break;
				default: 
					String func = "";
					if(token.charAt(0) == '(') {//is a constructor 
						func += "public " + typeName;
						func += token + " ";
						while(!token.contains("}"))
							func += (token = scn.next()) + " ";
						type += func;
					} else if(token.contains("(")) { // is a function
						func += token + " ";
						while(!token.contains(")"))
							func += (token = scn.next()) + " ";
						String returnType = scn.next();
						if(returnType.equals("{")) { // no return type so void
							func += returnType  + " ";
							returnType = "";
							while(!token.contains("}")) {
								String prev = token;
								token = scn.next();
								if(token.contains("(") && prev.matches("[A-Za-z0-9]+")) {
									String[] obj = token.split("\\("); 
									func += obj[0] + " = new " + prev + "(" + obj[1];
									continue;
								}
								func += token + " ";
							}
						} else { //there is a return type
							String open = scn.next();
							if(open.equals("{")) { //multi line function
								func += open + " ";
								while(!token.contains("}")) {
									String prev = token;
									token = scn.next();
									if(token.contains("(") && prev.matches("[A-Za-z0-9]+")) {
										String[] obj = token.split("\\("); 
										func += obj[0] + " = new " + prev + "(" + obj[1];
										continue;
									}
									func += token + " ";
								}
							}
							else { // single line function
								func += "{ ";
								func += open + " ";
								while(!token.contains(";")) {
									String prev = token;
									token = scn.next();
									if(token.contains("(") && prev.matches("[A-Za-z0-9]+")) {
										String[] obj = token.split("\\("); 
										func += obj[0] + " = new " + prev + "(" + obj[1];
										continue;
									}
									func += token + " ";
								}
								func += " }";
							}
						}
						type += (accessType.isEmpty() ? "public " : accessType + " ") +
								(returnType.isEmpty() ? "void " : returnType + " ") + func;
					}
					type += "\n";
					break;
			}
		}
		type += "\n}";
		//System.out.println(type);
		Class<?> c = null;
		File root = bdb.getStorage().getRootDir();
		File sourceFile = new File(root.toString(), "/" + typeName +".java");
		try {
			Files.write(sourceFile.toPath(), type.getBytes(StandardCharsets.UTF_8));
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			compiler.run(null, null, null, sourceFile.getPath());
			sourceFile.delete();
			URLClassLoader ucl = new URLClassLoader(
					new URL[]{root.toURI().toURL()});
			c = Class.forName(typeName, true, ucl);
			File reloc =new File(root.getAbsoluteFile() + "/" + c.getCanonicalName().replace(".", "/") + "/" + typeName + ".class");
			reloc.getParentFile().mkdirs();
			if(!reloc.exists())
				Files.move(new File(root.toString(), typeName + ".class").toPath(), reloc.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		scn.close();
		bdb.getSchema().insert((Class<? extends DataObject>)c);
		bdb.getStorage().updateSchema();
		return (Class<? extends DataObject>) c;
	}
	

}
