package net.hudup.core.data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Cloneable;
import net.hudup.core.Util;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;

/**
 * The class represents an external record stored in other database (different from the framework database).
 * As a convention, this class is called external record which opposite to internal record represented by {@code ExternalRecord}.
 * External record has three public variables such as {@link #unit}, {@link #attribute}, and {@link #value}.
 * The variable {@link #unit} represents external unit such as CSV file, database table and Excel sheet.
 * The variable {@link #attribute} represents external attribute in external unit such as field and column.
 * The variable {@link #value} represents external value is the value of the {@link #attribute}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ExternalRecord implements Serializable, Cloneable, TextParsable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Internal unit name. A unit represents as CSV file, database table and Excel sheet.
	 */
	public String unit = null;
	
	
	/**
	 * External attribute in external unit such as field and column.
	 */
	public String attribute = null;
	
	
	/**
	 * External value is the value of the {@link #attribute}.
	 */
	public Serializable value = null;
	
	
	/**
	 * Default constructor
	 */
	public ExternalRecord() {
		
	}
	
	
	/**
	 * Constructor with specified unit, attribute, and value.
	 * @param unit Specified unit represents external unit such as CSV file, database table and Excel sheet.
	 * @param attribute Specified attribute represents external attribute in external unit such as field and column.
	 * @param value Specified value represents external value which is the value of the attribute.
	 */
	public ExternalRecord(
			String unit, 
			String attribute, 
			Serializable value) {
		this.unit = unit;
		this.attribute = attribute;
		this.value = value;
	}

	
	/**
	 * Checking whether this external record is valid.
	 * @return Whether this external record is valid.
	 */
	public boolean isValid() {
		return 
			unit != null && !unit.isEmpty() &&
			attribute != null && !attribute.isEmpty() &&
			value != null;
	}
	
	
	/**
	 * Forming three fields {@link #unit}, {@link #attribute}, {@link #value} as a list.
	 * For example, if {@link #unit}, {@link #attribute}, {@link #value} are <i>item</i>, <i>ID</i>, <i>001</i>, respectively then,
	 * this method returns the list (<i>item</i>, <i>ID</i>, <i>001</i>).
	 * @return List of three fields {@link #unit}, {@link #attribute}, {@link #value}.
	 */
	public List<Object> toList() {
		List<Object> list = Util.newList();
		list.add(unit);
		list.add(attribute);
		list.add(value);
		
		return list;
	}


	@Override
	public String toText() {
		// TODO Auto-generated method stub
		if (!isValid())
			throw new RuntimeException("Invalid fields");
		
		return 
			TextParserUtil.encryptReservedChars(unit) + TextParserUtil.CONNECT_SEP +
			TextParserUtil.encryptReservedChars(attribute) + TextParserUtil.CONNECT_SEP +
			TextParserUtil.encryptReservedChars(value.toString());
	}


	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		List<String> list = TextParserUtil.split(spec, TextParserUtil.CONNECT_SEP, null);
		
		unit = TextParserUtil.decryptReservedChars(list.get(0));
		attribute = TextParserUtil.decryptReservedChars(list.get(1));
		value = TextParserUtil.decryptReservedChars(list.get(2));
	}
	
	
	@Override
	public String toString() {
		return toText();
	}

	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		ExternalRecord record = (ExternalRecord)obj;
		
		return this.unit.equals(record.unit) && 
				this.attribute.equals(record.attribute) &&
				this.value.equals(record.value);
	}


	@Override
	public Object clone() {
		return new ExternalRecord(
			new String(unit), 
			new String(attribute), 
			(Serializable)Util.clone(value)); 
	}
	
	
	/**
	 * Cloning (duplicating) the specified map of external records.
	 * @param map Specified map of external records.
	 * @return Cloned map (duplicated map) of external records.
	 */
	public static Map<Integer, ExternalRecord> clone(Map<Integer, ExternalRecord> map) {
		Map<Integer, ExternalRecord> newMap = Util.newMap();
		Set<Integer> keys = map.keySet();
		for (int key : keys) {
			ExternalRecord value = map.get(key);
			newMap.put(new Integer(key), (ExternalRecord)value.clone());
		}
		
		return newMap;
	}
	
	
}
