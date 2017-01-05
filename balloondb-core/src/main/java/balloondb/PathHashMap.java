package balloondb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class PathHashMap {
	
	private HashMap<String, Node> types;
	
	public PathHashMap() {
		types = new HashMap<String, Node>();
	}
	
	private static class Node {
		HashMap<String, Node> adjacentNodes;
		Class<? extends DataObject> type;
		Node() {adjacentNodes = new HashMap<String, Node>();}
	}
	
	public void put(Class<? extends DataObject> type) {
		if(!types.containsKey(type.getSimpleName())) {
			nodeInsert(type);
		} else {
			try {
				if(get(type.getName()) == null)
					appendNode(type);
				else
					throw new Exception("The type " + type + " already exists");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private void nodeInsert(Class<? extends DataObject> type) {
		String[] classPath = type.getName().toLowerCase().split("\\.");
		int size = classPath.length;
		Node n1 = new Node();
		types.put(classPath[(size-1)], n1);
		for(int i = 1; i < size; i++) {
			Node n2 = new Node();
			n1.adjacentNodes.put(classPath[(size-1)-i], n2);
			n1 = n2;
		}
		n1.type = type;
	}
	
	private void appendNode(Class<? extends DataObject> type) {
		String[] classPath = type.getName().toLowerCase().split("\\.");
		int size = classPath.length;
		Node n1 = types.get(classPath[(size-1)]);
		int i;
		for(i = 1; i < size; i++) {
			Node n2;
			n2 = n1.adjacentNodes.get(classPath[(size-1)-i]);
			if(n2 == null)
				break;
			n1 = n2;
		}
		//begin append
		for(; i < size; i++) {
			Node n2 = new Node();
			n1.adjacentNodes.put(classPath[(size-1)-i], n2);
		}
		
		n1.type = type;
	}
	
	
	public Class<? extends DataObject> get(String typeName) throws Exception {
		String[] classPath = typeName.toLowerCase().split("\\.");
		int size = classPath.length;
		Node n = types.get(classPath[size-1]);
		for(int i = 1; i < size; i++) {
			if(n == null)
				return null;
			n = n.adjacentNodes.get(classPath[(size-1)-i]);
		}
		if(n == null)
			return null;
		return findTypeFromNode(n);
	}
	
	private Class<? extends DataObject> findTypeFromNode(Node n) throws Exception {
		Set<Entry<String, Node>> set = n.adjacentNodes.entrySet();
		Class<? extends DataObject> type = n.type;
		while(!set.isEmpty()){
			if(set.size() > 1)
				throw new Exception("Ambiguous");
			Iterator<Entry<String, Node>> iter = set.iterator();
			Node n1 = iter.next().getValue();
			if(n1.type != null) {
				if(type != null)
					throw new Exception("Ambiguous");
				type = n1.type;
			}
			set = n1.adjacentNodes.entrySet();
		}
		return type;
	}
	
	public List<Class<? extends DataObject>> getAllTypes() {
		List<Class<? extends DataObject>> result = new ArrayList<Class<? extends DataObject>>();
		for(Entry<String, Node> entry : types.entrySet()) {
			if(entry.getValue().type != null)
				result.add(entry.getValue().type);
			traverseNodeAndGetTypes(entry.getValue(), result);
		}
		return result;
	}
	
	private void traverseNodeAndGetTypes(Node n, List<Class<? extends DataObject>> list) {
		for(Entry<String, Node> entry : n.adjacentNodes.entrySet()) {
			if(entry.getValue().type != null)
				list.add(entry.getValue().type);
			traverseNodeAndGetTypes(entry.getValue(), list);
		}
	}
	
	//TODO create method to check if class is in PathHashMap

}
