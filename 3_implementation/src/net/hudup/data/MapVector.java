package net.hudup.data;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Util;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 * @param <T>
 */
public class MapVector<T> {


	/**
	 * 
	 */
	protected int id = -1;
	
	
	/**
	 * 
	 */
	protected Map<Integer, T> map = Util.newMap();
	

	/**
	 * 
	 */
	public MapVector() {
	}
	
	
	/**
	 * 
	 * @param id
	 */
	public MapVector(int id) {
		this.id = id;
	}
	
	
	/**
	 * 
	 * @return id
	 */
	public int id() {
		return id;
	}
	
	
	/**
	 * 
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * 
	 * @return size of vector
	 */
	public int size() {
		return map.size();
	}
	
	
	/**
	 * Method is used to get rating at the key
	 * @param fieldId
	 * @return key contain in the map
	 */
	public T get(int fieldId) {
		return map.get(fieldId);
	}
	
	
	/**
	 * 
	 * @return collection of values
	 */
	public Collection<T> gets() {
		return map.values();
	}
	
	
	/**
	 * Method is used to put rating at fieldId
	 * @param fieldId
	 * @param value
	 */
	public void put(int fieldId, T value) {
		map.put(fieldId, value);
	}
	
	
	/**
	 * To remove rating at fieldId
	 * @param fieldId
	 */
	public void remove(int fieldId) {
		map.remove(fieldId);
	}

	
	/**
	 * 
	 * @param fieldIds
	 */
	public void remove(Collection<Integer> fieldIds) {
		 for (Integer fieldId : fieldIds) {
			 map.remove(fieldId);
		 }
	}
	
	
	/**
	 * 
	 */
	public void clear() {
		map.clear();
	}
	
	
	/**
	 * 
	 * @param map
	 */
	protected void setup(Map<Integer, T> map) {
		clear();
		this.map.putAll(map);
	}
	
	
	/**
	 * This method is used to set key
	 * @return key contained in the map
	 */
	public Set<Integer> fieldIds() {
		return map.keySet();
	}


	/**
	 * 
	 * @param fieldPattern
	 * @return list of value according to pattern
	 */
	public List<T> toList(List<Integer> fieldPattern) {
		List<T> list = Util.newList();
		for (int field : fieldPattern) {
			list.add(map.get(field));
		}
		return list;
	}

	
	/**
	 * 
	 * @param fieldId
	 * @return whether contains field id
	 */
	public boolean contains(int fieldId) {
		return map.containsKey(fieldId);
	}

	
	/**
	 * 
	 * @param rowName
	 * @param columnName
	 * @return text presentation
	 */
	public String toString(String rowName, String columnName) {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(rowName + " " + id + ":" + " ");
		List<Integer> fieldIds = Util.newList();
		fieldIds.addAll(fieldIds());
		Collections.sort(fieldIds);
		
		int count = 0;
		for (int i = 0; i < fieldIds.size(); i++) {
			if (count > 0)
				buffer.append(", ");
			
			int fieldId = fieldIds.get(i);
			Object value = get(fieldId);
			String svalue = value == null ? "" : value.toString();
			if (svalue.isEmpty())
				continue;
			
			buffer.append(columnName + " " + fieldId + "=" + value);
			
			count ++;
		}
		
		
		return buffer.toString();
		
	}
	
	
	
}
