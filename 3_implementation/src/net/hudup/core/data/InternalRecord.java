package net.hudup.core.data;

import java.io.Serializable;
import java.util.List;

import net.hudup.core.Cloneable;
import net.hudup.core.Util;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;

/**
 * This class represents a single-value record of a {@code unit} in framework database. Note, {@code unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
 * As a convention, this class is called internal record which opposite to external record represented by {@code ExternalRecord}.
 * It includes three parts as follows:
 * <ul>
 * <li>Unit name referred by public variable {@link #unit}.</li>
 * <li>Attribute name (like field name in database table) referred by public variable {@link #attribute}.</li>
 * <li>Value of such attribute name referred by public variable {@link #value}.</li>
 * </ul>
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class InternalRecord implements Serializable, Cloneable, TextParsable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Internal unit name. A unit represents as CSV file, database table and Excel sheet.
	 */
	public String unit = null;
	
	
	/**
	 * Internal attribute in internal unit such as field and column.
	 */
	public String attribute = null;
	
	
	/**
	 * External value is the value of the {@link #attribute}.
	 */
	public Serializable value = null;
	
	
	/**
	 * Default constructor.
	 */
	public InternalRecord() {
		
	}
	
	
	/**
	 * Constructor with specified unit, attribute, and value.
	 * @param unit Specified unit represents internal unit such as CSV file, database table and Excel sheet.
	 * @param attribute Specified attribute represents internal attribute in external unit such as field and column.
	 * @param value Specified value represents internal value which is the value of the attribute.
	 */
	public InternalRecord(
			String unit, 
			String attribute, 
			Serializable value) {
		this.unit = unit;
		this.attribute = attribute;
		this.value = value;
	}

	
	/**
	 * Checking whether this internal record is valid.
	 * @return Whether this internal record is valid.
	 */
	public boolean isValid() {
		return 
			unit != null && !unit.isEmpty() &&
			attribute != null && !attribute.isEmpty() &&
			value != null;
	}
	
	
	/**
	 * Forming three fields {@link #unit}, {@link #attribute}, and {@link #value} as a list.
	 * For example, if {@link #unit}, {@link #attribute}, {@link #value} are <i>item</i>, <i>ID</i>, <i>001</i>, respectively then,
	 * this method returns the list (<i>item</i>, <i>ID</i>, <i>001</i>).
	 * @return list form of this object.
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
		InternalRecord record = (InternalRecord)obj;
		
		return this.unit.equals(record.unit) && 
				this.attribute.equals(record.attribute) &&
				this.value.equals(record.value);
	}
	
	
	@Override
	public Object clone() {
		return new InternalRecord(
			new String(unit), 
			new String(attribute), 
			(Serializable)Util.clone(value)); 
	}
	
	
}
