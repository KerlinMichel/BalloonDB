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
			case "create" : query.setCommand(Command.SELECT); createQuery(query, params, query.getQueryString()); break;
			default : throw new QuerySyntaxError();
		}
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
	
	private static void createQuery(Query query, String[] params, String queryStr) throws QuerySyntaxError {// create type Person (String name, int age) : pk name;
		System.out.println(Arrays.toString(params));
		//String typeName = params[2];
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
