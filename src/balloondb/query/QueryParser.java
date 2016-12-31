package balloondb.query;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import balloondb.query.Query.Command;

public class QueryParser {
	
	//This is a utils class and should never be initialize
	private QueryParser(){};
	
	public static void parse(Query query) throws QuerySyntaxError {
		List<String> matchList = new LinkedList<String>();
		Pattern regex = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
		Matcher regexMatcher = regex.matcher(query.getQueryString());
		while (regexMatcher.find()) {
		    matchList.add(regexMatcher.group());
		} 
		String[] params = new String[matchList.size()];
		matchList.toArray(params);
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
			classes[i] = inferType(types[i]);
		}
		return classes;
	}
	
	public static Class<?> inferType(String value) {
		char firstChar = value.charAt(0);
		char lastChar = value.charAt(value.length()-1);
		
		if(firstChar == '"' && lastChar == '"')
			return String.class;
		
		if(firstChar == '\'' && lastChar == '\'')
			return char.class;
		
		if(value.equals("true") || value.equals("false"))
			return boolean.class;
		
		//matches to integers
		if(value.matches("[-+]?\\d*")) {
			if(lastChar == 'l' || lastChar == 'L')
				return long.class;
			else
				return int.class;
		}
		//matches to floating point numbers
		else if(value.matches("[-+]?\\d*\\.?\\d+")) {
			if(lastChar == 'd' || lastChar == 'D')
				return double.class;
			else
				return float.class;
		}
		
		return null;
	}
	
	public static Object[] parseStringsToValue(Class<?>[] types, String[] values) {
		Object[] result = new Object[types.length];
		for(int i = 0; i < types.length; i++) {
			result[i] = parseStringToValue(types[i], values[i]); 
		}
		return result;
	}
	
	public static Object parseStringToValue(Class<?> type, String value) {
		if(type.equals(String.class))
			return value.substring(1, value.length() - 1);
		if(type.equals(int.class))
			return Integer.parseInt(value);
		if(type.equals(long.class))
			return Long.parseLong(value);
		if(type.equals(float.class))
			return Float.parseFloat(value);
		if(type.equals(double.class))
			return Double.parseDouble(value);
		if(type.equals(boolean.class))
			return Boolean.parseBoolean(value);
		
		return null;
	}
	
	public static Object parseStringToValue(String value) throws QuerySyntaxError {
		Class<?> type = inferType(value);
		if(type == null)
			throw new QuerySyntaxError();
		return parseStringToValue(type, value);
	}
	
	private static void operateQuery(Query query, String[] params) throws QuerySyntaxError {
		query.setWorkOnType(params[1]);
		if(params.length == 2)
			return;
		if(!params[2].equals("where"))
			throw new QuerySyntaxError();
		//the first three words are [command] [type] where 
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
