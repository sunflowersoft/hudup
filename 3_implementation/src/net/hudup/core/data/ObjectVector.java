package net.hudup.core.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Cloneable;
import net.hudup.core.Util;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;


/**
 * This class represents a vector of objects. Each object is associated with a key. Key is also called field or field identifier (field ID).
 * So this vector stores map of entries {@link #ratedMap}. Each entry has a integer key (field or field ID) and an object value.
 * This vector firstly is created by storing ratings but it is now replaced by {@link RatingVector} class.
 *  
 * @author Loc Nguyen
 * @version 10.0
 *
 * @param <E> type of objects in vector.
 */
public abstract class ObjectVector<E extends Serializable> implements Cloneable, TextParsable, Serializable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Identifier of this vector.
	 */
	protected int id = -1;
	
	
	/**
	 * This is mnap of entries. Each entry has a integer key and an object value.
	 */
	protected Map<Integer, E> ratedMap = Util.newMap();

	
	/**
	 * Default constructor.
	 */
	public ObjectVector() {
		super();
		// TODO Auto-generated constructor stub
		
		if (!(ratedMap instanceof Serializable))
			throw new RuntimeException("RatingVector isn't serializable class");
	}

	
	/**
	 * Constructor with specified identifier (ID).
	 * @param id specified identifier (ID).
	 */
	public ObjectVector(int id) {
		this();
		this.id = id;
	}

	
	/**
	 * Getting the identifier (ID) of this vector.
	 * @return id
	 */
	public int id() {
		return id;
	}
	
	
	/**
	 * Setting the identifier (ID) of this vector by specified ID.
	 * @param id specified ID.
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Getting the size of this vector.
	 * @return size of this vector.
	 */
	public int size() {
		return ratedMap.size();
	}
	
	
	/**
	 * Method is used to get rating (value) at the key (field)
	 * @param fieldId specified field.
	 * @return value at specified field.
	 */
	public E get(int fieldId) {
		return ratedMap.get(fieldId);
	}
	
	
	/**
	 * Getting all values.
	 * @return all values in the returend collection.
	 */
	public Collection<E> gets() {
		return ratedMap.values();
	}
	
	
	/**
	 * Method is used to put value at specified field.
	 * @param fieldId specified field.
	 * @param value specified value.
	 */
	public void put(int fieldId, E value) {
		ratedMap.put(fieldId, value);
	}
	
	
	/**
	 * Putting all values from other vector into this vector.
	 * @param vector other vector.
	 */
	public void put(ObjectVector<E> vector) {
		Set<Integer> fieldIds = vector.fieldIds();
		for (int fieldId : fieldIds) {
			ratedMap.put(fieldId, vector.get(fieldId));
		}
		
	}

	
	/**
	 * To remove value at specified field.
	 * @param fieldId specified field identifier (fieldID).
	 */
	public void remove(int fieldId) {
		ratedMap.remove(fieldId);
		
	}

	
	/**
	 * Removing entries having fields in specified collection.
	 * @param fieldIds specified collection of fields whose entries are removed from this vector.
	 */
	public void remove(Collection<Integer> fieldIds) {
		 for (Integer fieldId : fieldIds) {
			 ratedMap.remove(fieldId);
		 }
	}
	
	
	/**
	 * Keeping entries whose field IDs are in the specified collection.
	 * @param fieldIds specified collection of field IDs.
	 */
	public void retain(Collection<Integer> fieldIds) {
		Set<Integer> allFields = fieldIds();
		
		 for (Integer fieldId : allFields) {
			 if (!fieldIds.contains(fieldId))
				 ratedMap.remove(fieldId);
		 }
	}
	
	
	/**
	 * This method is used to get keys (fields).
	 * @return keys contained in this vector.
	 */
	public Set<Integer> fieldIds() {
		return ratedMap.keySet();
	}

	
	/**
	 * Testing whether this vector contains the entry having specified field.
	 * @param fieldId specified field identifier (field ID).
	 * @return whether this vector contains the entry having specified field.
	 */
	public boolean contains(int fieldId) {
		return ratedMap.containsKey(fieldId);
	}

	
	/**
	 * Clearing this vector.
	 */
	public void clear() {
		ratedMap.clear();
	}

	
	@Override
    public Object clone() {
		Map<Integer, E> ratedMap = clone(this.ratedMap);
		
    	ObjectVector<E> vector = newInstance();
    	vector.ratedMap = ratedMap;
    	return vector;
    }

	
	/**
	 * Creating new instance of this vector.
	 * @return new instance of this vector.
	 */
	@SuppressWarnings("unchecked")
	public ObjectVector<E> newInstance() {
		ObjectVector<E> result = null;
		
		try {
			result = this.getClass().newInstance();
			result.setId(this.id);
		} 
		catch (Exception e) {
			e.printStackTrace();
			result = null;
		}
		
		return result;
	}

	
	@Override
	public String toText() {
		// TODO Auto-generated method stub
		StringBuffer buffer = new StringBuffer();
		buffer.append(id);
		
		List<Integer> fieldIdList = Util.newList();
		fieldIdList.addAll(fieldIds());
		if (fieldIdList.size() == 0)
			return buffer.toString();
		
		buffer.append(" " + TextParserUtil.MAIN_SEP + " ");
		for (int i = 0; i < fieldIdList.size(); i++) {
			if (i > 0)
				buffer.append(", ");
			
			int fieldId = fieldIdList.get(i);
			E value = get(fieldId);
			String valueText = null;
			if (value instanceof TextParsable)
				valueText = ((TextParsable)value).toText();
			else
				valueText = value.toString();
			buffer.append(fieldId + "=" + valueText);
		}
		
		return buffer.toString();
	}


	@SuppressWarnings("unchecked")
	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		clear();
		
		List<String> list = TextParserUtil.split(spec, TextParserUtil.MAIN_SEP, null);
		if (list.size() == 0)
			return;
		
		id = Integer.parseInt(list.get(0));
		if (list.size() == 1)
			return;
		
		list = TextParserUtil.split(list.get(1), ",", null);
		for (String el : list) {
			List<String> pair = TextParserUtil.split(el, "=", null);
			if (pair.size() < 2)
				continue;
			
			int fieldId = Integer.parseInt(pair.get(0));
			E value = (E) TextParserUtil.parseObjectByClass(pair.get(1), newObject().getClass());
			put(fieldId, value);
		}
		
	}

	
	/**
	 * Creating a new instance of type of objects in vector (type {@code E}).
	 * @return instance of template.
	 */
	protected abstract E newObject();
	
	
	/**
	 * Deep cloning the specified of map of entries. Each entry has a integer key (field or field ID) and an object value.
	 * @param <E> type of values of map.type of objects in vector.
	 * @param map specified map
	 * @return the map cloned from specified map.
	 */
	@SuppressWarnings("unchecked")
	public final static <E extends Serializable> Map<Integer, E> clone(Map<Integer, E> map) {
		Map<Integer, E> newMap = Util.newMap();
		
		Set<Integer> keys = map.keySet();
		for (Integer key : keys) {
			int newKey = key.intValue();
			E newValue = (E) Util.clone(map.get(key));
			newMap.put(newKey, newValue);
		}
		
		return newMap;
	}

	
}
