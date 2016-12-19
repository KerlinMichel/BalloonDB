package balloondb.query;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import balloondb.BalloonDB;
import balloondb.DataObject;
import balloondb.EntityIntegerityException;
import balloondb.Schema;

public class Query {
	
	private String query;
	public enum Command {
		SELECT,
		DELETE,
		CREATE,
		INSERT
	}
	private Command cmd;
	private String[] conditions;
	private String workOnType;
	private Object[] createClassFields;
	
	private String createSrc;
	private String newInstance;
	
	public Query(String query) {
		this.query = query;
	}
	
	public Object query(BalloonDB bdb) throws QuerySyntaxError {
		try {
			parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
		switch(cmd){
			case DELETE:
			case SELECT: return operateOnDB(bdb);
			case INSERT: return insertNewInstance(bdb);// insert Person("pName", 1)
			case CREATE: try {
				return Schema.generateClass(query, bdb);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			default: return null;
		}
	}
	
	private Object operateOnDB(BalloonDB bdb) throws QuerySyntaxError {
		List<DataObject> result = new ArrayList<DataObject>();
		HashMap<Object, DataObject> objects = null;
		Class<? extends DataObject> type = null;
		try {
			type = bdb.getSchema().getTypes().get(this.workOnType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		objects = bdb.getStorage().getData().get(type);
		for(Entry<Object, DataObject> entry : bdb.getStorage().getData().get(type).entrySet()) { //loop through all objects
			if (conditions != null && conditions.length > 0) {
				if(checkAllConditions(type, entry.getValue()))
					result.add(entry.getValue());
			} else {
				result.add(entry.getValue());
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
	
	public Object select(BalloonDB bdb) throws QuerySyntaxError {
		query = "select " + query;
		return query(bdb);
	}
	
	public Object delete(BalloonDB bdb) throws QuerySyntaxError {
		query = "delete " + query;
		return query(bdb);
	}
	
	public Object create(BalloonDB bdb) throws QuerySyntaxError {
		query = "create " + query;
		return query(bdb);
	}
	
	public Object insert(BalloonDB bdb) throws QuerySyntaxError {
		query = "insert " + query;
		return query(bdb);
	}
	
	//TODO: Create method to match newInstace to array of class for constructor
	private Object insertNewInstance(BalloonDB bdb) {
		Class<? extends DataObject> type = null;
		try {
			type = bdb.getSchema().getTypes().get(workOnType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String[] params = newInstance.split(",");
		
		for(int i = 0; i < params.length; i++)
			params[i] = params[i].trim();
		
		Class<?>[] typeList = QueryParser.inferTypes(params);
		
		Object[] values = QueryParser.parseStringsToValue(typeList, params);
		
		Constructor<?> constructor =  null;
		
		try {
			constructor = type.getDeclaredConstructor(typeList);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
		if(constructor == null)
			return null;
		
		Object data = null;
		
		try {
			data = constructor.newInstance(values);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		try {
			bdb.getStorage().insert((DataObject)data);
		} catch (EntityIntegerityException e) {
			e.printStackTrace();
		}
		
		return data;
	}
	
	private void operate(BalloonDB bdb, HashMap<Object, DataObject> data, DataObject obj) {
		switch(cmd){
			case DELETE : data.remove(obj.getKey()); obj.delete();
				break;
		}
	}
	
	private boolean checkAllConditions(Class<? extends DataObject> type, DataObject obj) throws QuerySyntaxError {
		for(String cond : conditions) {
			if(!checkCondition(type, obj, cond))
				return false;
		}
		return true;
	}
	
	private boolean checkCondition(Class<? extends DataObject> type, DataObject obj, String cond) throws QuerySyntaxError {
		String[] params = cond.split("\\s+"); 
		
		String fieldName = params[0];
		String condition = params[1];
		Object checkValue = QueryParser.parseStringToValue(params[2]);;
		if(checkValue == null)
			return false;
		Object field = obj.getField(fieldName, type);
		
		switch(condition) {
			case "=" :
				if(field.equals(checkValue))
					return true;
				return false;
			case "!=" :
				if(!field.equals(checkValue))
					return true;
				return false;
			case ">=" :
				if(field.equals(checkValue))
					return true;
				//if not equal then fall through to greater than check
			case ">" : 
				if(field instanceof Comparable && checkValue instanceof Comparable) {
					int i = compare(field, checkValue);
					if(i > 0)
						return true;
					return false;
				}
				//cannot compare objects
				else
					throw new QuerySyntaxError();
			case "<=" :
				if(field.equals(checkValue))
					return true;
				//if not equal then fall through to less than check
			case "<" : 
				if(field instanceof Comparable && checkValue instanceof Comparable) {
					int i = compare(field, checkValue);
					if(i < 0)
						return true;
					return false;
				}
				//cannot compare objects
				else
					throw new QuerySyntaxError();
		}
		
		return false;
	}
	
	private int compare(Object a, Object b) throws QuerySyntaxError {
		if(a instanceof Comparable && b instanceof Comparable)
			return ((Comparable<Object>)a).compareTo(b);
		else
			throw new QuerySyntaxError();
	}
	
	private void parse() { 
		try {
			QueryParser.parse(this);
		} catch (QuerySyntaxError e) {
			e.printStackTrace();
		}
	}
	
	public String getQueryString() {
		return query;
	}
	
	public void setCommand(Command cmd) {
		this.cmd = cmd;
	}
	
	public String getWorkOnType() {
		return workOnType;
	}
	
	public void setWorkOnType(String type) {
		workOnType = type;
	}
	
	public String[] getConditions() {
		return conditions;
	}
	
	public void setConditions(String[] cond) {
		conditions = cond;
	}

	public Object[] getCreateClassFields() {
		return createClassFields;
	}

	public void setCreateClassFields(Object[] classFields) {
		createClassFields = classFields;
	}

	public String getCreateSrc() {
		return createSrc;
	}

	public void setCreateSrc(String createSrc) {
		this.createSrc = createSrc;
	}
	
	public void setNewInstance(String instance) {
		newInstance = instance;
	}

}
