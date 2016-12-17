package balloondb.query;

import java.util.Arrays;

import balloondb.query.Query.Command;

public class QueryParser {
	
	//This is a utils class and should never be initialize
	private QueryParser(){};
	
	public static void parse(Query query) throws QuerySyntaxError {
		String[] params = query.getQueryString().split("\\s+");
		switch(params[0].toLowerCase()) {
			case "select" : query.setCommand(Command.SELECT); operateQuery(query, params); break;
			case "delete" : query.setCommand(Command.DELETE); operateQuery(query, params); break;
			case "insert" : query.setCommand(Command.INSERT); newInstance(query, params); break;
			case "create" : query.setCommand(Command.CREATE); break;
			default : throw new QuerySyntaxError();
		}
	}
	
	private static void newInstance(Query query, String[] params) {
		String type = query.getQueryString();
		type = type.substring(0, type.indexOf("("));
		String[] qs = type.split("\\s+");
		query.setWorkOnType(qs[qs.length-1]);
		String tuple = query.getQueryString();
		tuple = tuple.substring(tuple.indexOf("(") + 1);
		tuple = tuple.substring(0, tuple.indexOf(")"));
		query.setNewInstance(tuple);
	}
	
	public static Class<?>[] inferTypes(String[] types) {
		Class<?>[] classes = new Class[types.length];
		for(int i = 0; i < types.length; i++) {
			String type = types[i];
				if(type.contains("\"")) 
					classes[i] = String.class;
				
				if(type.equals("true") || type.equals("false"))
					classes[i] = boolean.class;
				
				//matches to integers
				if(type.matches("[-+]?\\d*")) {
					char intType = type.charAt(type.length() - 1);
					if(intType == 'l')
						classes[i] = long.class;
					else
						classes[i] = int.class;
				}
				
				//matches to real float pointer numbers
				else if(type.matches("[-+]?\\d*\\.?\\d+")) {
					char realType = type.charAt(type.length() - 1);
					if(realType == 'd')
						classes[i] = double.class;
					else
						classes[i] = float.class;
				}
		}
		return classes;
	}
	
	public static Object[] parseStrings(Class<?>[] types, String[] values) {
		Object[] result = new Object[types.length];
		for(int i = 0; i < types.length; i++) {
			Class<?> type = types[i];
			String value = values[i];
			if(type.equals(String.class))
				result[i] = value.substring(1, value.length() - 1);
			if(type.equals(int.class))
				result[i] = Integer.parseInt(value);
			if(type.equals(long.class))
				result[i] = Long.parseLong(value);
			if(type.equals(float.class))
				result[i] = Float.parseFloat(value);
			if(type.equals(double.class))
				result[i] = Double.parseDouble(value);
			if(type.equals(boolean.class))
				result[i] = Boolean.parseBoolean(value);
		}
		return result;
	}
	
	private static void operateQuery(Query query, String[] params) {
		query.setWorkOnType(params[1]);
		if(params.length == 2)
			return;
        int numConds = (params.length-3)/3;
		query.setConditions(new String[numConds]);
		for(int i = 0; i < numConds; i++) {
			String cond = "";
			for(int j = 0; j < 3; j++)
				cond += params[3+j] + " ";
			query.getConditions()[i] = cond;
		}
	}
	
	//Old code of old syntax for create statements
	private static void createQuery(Query query, String[] params, String queryStr) throws QuerySyntaxError {
		if(params.length <= 2)
			throw new QuerySyntaxError();
		System.out.println(Arrays.toString(queryStr.split(" ", 4)));
		String[] p = queryStr.split(" ", 4);
		String typeName = p[2];
		String[] specs = p[3].split(":");
		query.setCreateClassFields(splitTuple(specs[0])); 
	}
	
	private static String[] splitTuple(String tuple) throws QuerySyntaxError {
		tuple = tuple.trim();
		System.out.println(tuple);
		if(!tuple.startsWith("(") || !tuple.endsWith(")"))
			throw new QuerySyntaxError();
		String[] items = null;
		tuple = tuple.substring(1,tuple.length()-1);
		items = tuple.split(",");
		System.out.println(Arrays.toString(items));
		return items;
	}
	
	private static Object createField(String field) {
		String[] params = field.split("\\s+");
		switch(field.toLowerCase()) {
			//case "string": return ;
		}
		return null;
	}

}
