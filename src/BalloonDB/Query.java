package BalloonDB;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class Query {
	
	private String query;
	private enum Command {
		SELECT,
		DELETE
	}
	private Command cmd;
	private String[] conditions;
	private String type;
	public Query(String query) {
		this.query = query;
	}
	
	public Object query(BalloonDB bdb) {
		try {
			parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<DataObject> result = new ArrayList<DataObject>();
		HashMap<Object, DataObject> objects = null;
		for(Class<? extends DataObject> type : bdb.getSchema().getClasses()) {
			 if(type.getName().toLowerCase().contains(this.type.toLowerCase())) {
				 for(Entry<Object, DataObject> entry : bdb.getStorage().getData().get(type).entrySet()) {
					 if (conditions != null && conditions.length > 0) {
						 if(checkAllConditions(type, entry.getValue()))
							 result.add(entry.getValue());
					 } else {
						 result.add(entry.getValue());
					 }
				 }
				 objects = bdb.getStorage().getData().get(type);
				 break;
			 }
		}		
		for(DataObject obj : result) {
			operate(bdb, objects, obj);
		}
		if(result.size() == 0)
			return null;
		else if(result.size() == 1)
			return result.get(0);
		else 
			return result;
	}
	
	public Object select(BalloonDB bdb) {
		query = "select " + query;
		return query(bdb);
	}
	
	public Object delete(BalloonDB bdb) {
		query = "delete " + query;
		return query(bdb);
	}
	
	private void operate(BalloonDB bdb, HashMap<Object, DataObject> data, DataObject obj) {
		switch(cmd){
			case DELETE : data.remove(obj.getKey()); obj.delete();
				break;
		}
	}
	
	private boolean checkAllConditions(Class<? extends DataObject> type, DataObject obj) {
		for(String cond : conditions) {
			if(!checkCondition(type, obj, cond))
				return false;
		}
		return true;
	}
	
	private boolean checkCondition(Class<? extends DataObject> type, DataObject obj, String cond) {
		String[] params = cond.split("\\s+"); //create casting system
		String fieldName = params[0];
		String condition = params[1];
		String checkValue = params[2];
		Object field = getField(fieldName, type, obj);
		switch(condition) {
			case "=" :
				if(field.toString().equals(checkValue))
					return true;
		}
		
		return false;
	}
	
	private void parse() throws Exception {
		String[] params = query.split("\\s+");
		switch(params[0].toLowerCase()) {
			case "select" : cmd = Command.SELECT; break;
			case "delete" : cmd = Command.DELETE; break;
		}
		type = params[1];
		if(params.length == 2)
			return;
		if(!params[2].equals("where"))
			throw new Exception();
		conditions = new String[(params.length-3)/3];
		for(int i = 0; i < conditions.length; i++) {
			String s = "";
			for(int j = 0; j < 3; j++)
				s += params[3+j] + " ";
			conditions[i] = s;
		}
	}
	
	private static Object getField(String name, Class type, Object obj) {
		Object field = null;
		try {
			Field f = type.getDeclaredField(name);
			f.setAccessible(true);
			field = f.get(obj);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return field;
	}

}
