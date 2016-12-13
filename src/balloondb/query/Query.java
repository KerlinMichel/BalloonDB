package balloondb.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import balloondb.BalloonDB;
import balloondb.DataObject;
import balloondb.Schema;

public class Query {
	
	private String query;
	public enum Command {
		SELECT,
		DELETE,
		CREATE
	}
	private Command cmd;
	private String[] conditions;
	private String workOnType;
	private Object[] createClassFields;
	
	private String createSrc;
	
	public Query(String query) {
		this.query = query;
	}
	
	public Object query(BalloonDB bdb) {
		try {
			parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
		switch(cmd){
			case DELETE:
			case SELECT: return operateOnDB(bdb);
			case CREATE: try {
				return Schema.generateClass(query, bdb);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			default: return null;
		}
	}
	
	private Object operateOnDB(BalloonDB bdb) {
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
		/*for(Entry<String, Class<? extends DataObject>> type : bdb.getSchema().getTypes().entrySet()) { // loop all types in database
			 if(type.getValue().getName().toLowerCase().contains(this.workOnType.toLowerCase())) { // search matching type
				 for(Entry<Object, DataObject> entry : bdb.getStorage().getData().get(type.getValue()).entrySet()) { //loop through all objects
					 if (conditions != null && conditions.length > 0) {
						 if(checkAllConditions(type.getValue(), entry.getValue()))
							 result.add(entry.getValue());
					 } else {
						 result.add(entry.getValue());
					 }
				 }
				 objects = bdb.getStorage().getData().get(type.getValue());
				 break;
			 }
		}*/		
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
	
	public Object create(BalloonDB bdb) {
		query = "create " + query;
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
		Object field = obj.getField(fieldName, type);//getField(fieldName, type, obj);
		switch(condition) {
			case "=" :
				if(field.toString().equals(checkValue))
					return true;
		}
		
		return false;
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

}
