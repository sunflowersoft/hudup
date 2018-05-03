/**
 * 
 */
package net.hudup.core.data;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.hudup.core.Cloneable;
import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.logistic.MinMax;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;


/**
 * {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
 * Profile uses attributes to specify its data types. Attribute is represented by {@link Attribute} class.
 * Therefore, profile owns two variables such as {@link #attRef} representing a list of attributes and {@link #attValues} representing a list of values.
 * Such two lists are aligned so that it is easy to know a given value belongs to which attribute.
 * In a profile, a key attribute or a set of some key attributes are used to distinguish two profiles.
 * If there is only one key attribute, we state that the profile has single key. A single key is also called identification (ID).
 * If there are a set of key attributes, we state that the profile has complex key.
 * Single key and complex key are represented by this {@link Keys} class.
 * The concept of profile is derived from the concept of profile in Weka - Data Mining Software in Java, available at <a href="http://www.cs.waikato.ac.nz/ml/weka">http://www.cs.waikato.ac.nz/ml/weka</a>
 * 
 * @author Loc Nguyen
 * @version 10.0
 * 
 */
public class Profile implements Cloneable, TextParsable, Serializable {
	

	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * This built-in variable is a list of attribute. Note that this variable is a reference, which implies that many profiled can share the same attribute list.
	 * Two lists {@link #attRef} and {@link #attValues} are aligned so that it is easy to know a given value belongs to which attribute.
	 */
	protected AttributeList attRef = new AttributeList();
	
	
	/**
	 * This built-in variable is a list of values.
	 * Two lists {@link #attRef} and {@link #attValues} are aligned so that it is easy to know a given value belongs to which attribute.
	 */
	protected List<Object> attValues = Util.newList();
	
	
	/**
	 * Default constructor
	 */
	public Profile() {
		init();
	}
	
	
	/**
	 * Constructor with specified attribute list.
	 * @param attRef specified attribute list as {@link AttributeList}.
	 */
	public Profile(AttributeList attRef) {
		this.attRef = attRef;
		init();
	}
	
	
	/**
	 * Initializing empty profile in which all its values are null.
	 */
	private void init() {
		if (!(attValues instanceof Serializable))
			throw new RuntimeException("Value isn't serializable class");
		
		attValues.clear();
		for (int i = 0; i < attRef.size(); i++) {
			attValues.add(null);
		}
	}
	
	
	/**
	 * Recall that, in a profile, a key attribute or a set of some key attributes are used to distinguish two profiles.
	 * If there is only one key attribute, we state that the profile has single key. A single key is also called identification (ID).
	 * If there are a set of key attributes, we state that the profile has complex key.
	 * Single key and complex key are represented by the {@code Keys} class.
	 * This method gets the identification attribute which is the single key.
	 * 
	 * @return identification attribute which is the single key.
	 */
	public Attribute getIdAtt() {
		Keys keys = getKeys();
		if (keys.size() != 1)
			return null;
		
		return keys.get(0);
	}
	
	
	
	/**
	 * Recall that, in a profile, a key attribute or a set of some key attributes are used to distinguish two profiles.
	 * If there is only one key attribute, we state that the profile has single key. A single key is also called identification (ID).
	 * If there are a set of key attributes, we state that the profile has complex key.
	 * Single key and complex key are represented by the {@code Keys} class.
	 * This method gets the value of identification attribute (single key).
	 * 
	 * @return value of identification attribute (single key).
	 */
	public Object getIdValue() {
		Keys keys = getKeys();
		if (keys.size() != 1)
			return null;
		
		Attribute key0 = keys.get(0);
		
		return getValue(key0.getIndex());
	}
	
	
	
	/**
	 * Recall that, in a profile, a key attribute or a set of some key attributes are used to distinguish two profiles.
	 * If there is only one key attribute, we state that the profile has single key. A single key is also called identification (ID).
	 * If there are a set of key attributes, we state that the profile has complex key.
	 * Single key and complex key are represented by the {@code Keys} class.
	 * This method gets the value of identification attribute (single key) as an integer.
	 * 
	 * @return value of identification attribute (single key) as an integer.
	 */
	public int getIdValueAsInt() {
		Keys keys = getKeys();
		if (keys.size() != 1)
			return -1;

		Attribute key = keys.get(0);
		if (key.getType() != Type.integer)
			return -1;
		
		Attribute key0 = keys.get(0);
		return getValueAsInt(key0.getIndex());
	}

	
	/**
	 * Recall that, in a profile, a key attribute or a set of some key attributes are used to distinguish two profiles.
	 * If there is only one key attribute, we state that the profile has single key. A single key is also called identification (ID).
	 * If there are a set of key attributes, we state that the profile has complex key.
	 * Single key and complex key are represented by the {@code Keys} class.
	 * This method sets the value of identification by specified object.
	 * 
	 * @param id specified object.
	 */
	public void setIdValue(Object id) {
		Keys keys = getKeys();
		if (keys.size() == 0)
			throw new RuntimeException("Have more than one key");

		Attribute key0 = keys.get(0);
		setValue(key0.getIndex(), id);
	}
	
	
	/**
	 * Getting the attribute at specified index.
	 * @param index attribute at specified index.
	 * @return {@link Attribute} at specified index.
	 */
	public Attribute getAtt(int index) {
		return attRef.get(index);
	}

	
	
	/**
	 * Recall that, in a profile, a key attribute or a set of some key attributes are used to distinguish two profiles.
	 * If there is only one key attribute, we state that the profile has single key. A single key is also called identification (ID).
	 * If there are a set of key attributes, we state that the profile has complex key.
	 * Single key and complex key are represented by the {@code Keys} class.
	 * This method gets the key of this profile.
	 * 
	 * @return {@link Keys} of this profile.
	 */
	public Keys getKeys() {
		return attRef.getKeys();
	}
	
	
	/**
	 * Recall that, in a profile, a key attribute or a set of some key attributes are used to distinguish two profiles.
	 * If there is only one key attribute, we state that the profile has single key. A single key is also called identification (ID).
	 * If there are a set of key attributes, we state that the profile has complex key.
	 * Single key and complex key are represented by the {@code Keys} class.
	 * This method gets value of the key of this profile.
	 * 
	 * @return key values as {@link Condition}.
	 */
	public Condition getKeyValues() {
		Keys keys = getKeys();
		AttributeList keyAttList = AttributeList.create(keys.getList());
		Condition keyProfile = new Condition(keyAttList);
		
		for (int i = 0; i < keys.size(); i++) {
			Attribute key = keys.get(i);
			int index = attRef.indexOf(key.getName());
			Object value = getValue(index);
			
			int keyIndex = keyAttList.indexOf(key.getName());
			keyProfile.setValue(keyIndex, value);
		}
		
		return keyProfile;
	}
	
	
	/**
	 * Recall that, in a profile, a key attribute or a set of some key attributes are used to distinguish two profiles.
	 * If there is only one key attribute, we state that the profile has single key. A single key is also called identification (ID).
	 * If there are a set of key attributes, we state that the profile has complex key.
	 * Single key and complex key are represented by the {@code Keys} class.
	 * This method sets the attribute having specified name as key attribute.
	 *  
	 * @param attName name of attribute which is set as key attribute.
	 */
	public void setKey(String attName) {
		attRef.setKey(attName);
	}
	
	
	/**
	 * Recall that, in a profile, a key attribute or a set of some key attributes are used to distinguish two profiles.
	 * If there is only one key attribute, we state that the profile has single key. A single key is also called identification (ID).
	 * If there are a set of key attributes, we state that the profile has complex key.
	 * Single key and complex key are represented by the {@code Keys} class.
	 * This method sets the list of attributes as complex key.
	 * These attributes are identified by collection of names.
	 * 
	 * @param attNameList collection of names of attributes which are set as complex key.
	 */
	public void setKeys(Collection<String> attNameList) {
		attRef.setKeys(attNameList);
	}
	
	
	/**
	 * Recall that, in a profile, a key attribute or a set of some key attributes are used to distinguish two profiles.
	 * If there is only one key attribute, we state that the profile has single key. A single key is also called identification (ID).
	 * If there are a set of key attributes, we state that the profile has complex key.
	 * Single key and complex key are represented by the {@code Keys} class.
	 * The method sets the attribute at specified index as key.
	 * 
	 * @param attIndex specified index.
	 */
	public void setKey(int attIndex) {
		attRef.setKey(attIndex);
	}
	
	
	/**
	 * Recall that, in a profile, a key attribute or a set of some key attributes are used to distinguish two profiles.
	 * If there is only one key attribute, we state that the profile has single key. A single key is also called identification (ID).
	 * If there are a set of key attributes, we state that the profile has complex key.
	 * Single key and complex key are represented by the {@code Keys} class.
	 * This method sets the list of attributes as complex key.
	 * Such attributes are at specified indices.
	 * 
	 * @param attIndexes specified indices of attributes which are set as complex key.
	 */
	public void setKeys(int[] attIndexes) {
		attRef.setKeys(attIndexes);
	}

	
	/**
	 * Testing the value at specified index is missing.
	 * @param index specified index.
	 * @return whether missing value at this index.
	 */
	public boolean isMissing(int index) {
		return attValues.get(index) == null;
	}
	
	
	/**
	 * Getting the number of attributes.
	 * @return count for {@link AttributeList}.
	 */
	public int getAttCount() {
		return attRef.size();
	}
	
	
	/**
	 * Replacing the current {@link AttributeList} by reference to specified {@link AttributeList}.
	 * @param attRef reference to specified {@link AttributeList}.
	 */
	public void setAttRef(AttributeList attRef) {
		this.attRef = attRef;
		
		init();
	}
	
	
	/**
	 * Getting the internal {@link AttributeList}.
	 * @return reference of {@link AttributeList}.
	 */
	public AttributeList getAttRef() {
		return attRef;
	}
	
	
	/**
	 * Getting list of not missing value attributes.
	 * @return list of not missing value attributes.
	 */
	public Keys getNotMissingAtts() {
		Keys keys = new Keys();
		for (Attribute att : attRef.list) {
			if (getValue(att.name) != null)
				keys.list.add(att);
		}
		
		return keys;
	}
	
	
	/**
	 * Finding index of the attribute having specified name.
	 * @param attName specified name.
	 * @return index of the attribute having specified name.
	 */
	public int indexOf(String attName) {
		return attRef.indexOf(attName);
	}
	
	
	/**
	 * Creating the value for specified attribute and the initial value.
	 * @param att specified attribute.
	 * @param value initial value.
	 * @return new value for specified attribute and the initial value.
	 * @throws Exception if any error raises.
	 */
	public static Object createValue(Attribute att, Object value) throws Exception {
		if (value == null)
			return null;
		
		Object newValue = null;
		Type attType = att.getType();
		
		switch (attType) {
		
		case bit:
			byte number = 0;
			if (value instanceof Number)
				number = ((Number)value).byteValue();
			else if (value instanceof String || value instanceof Nominal) {
				
				String string = value instanceof Nominal ? 
					((Nominal)value).getValue().trim() : value.toString().trim();
						
				if (string.isEmpty())
					number = 0;
				else
					number = Byte.parseByte(string);
			}
			else if (value instanceof Boolean) {
				if ((Boolean)value)
					number = 1;
				else
					number = 0;
			}
			
			if (number == 0)
				newValue = (byte)0;
			else
				newValue = (byte)1; 
			
			break;
			
		case nominal:
			if (value instanceof String || value instanceof Nominal) {
				int valueIndex = value instanceof Nominal ?
					att.indexOfNominal((Nominal)value) : att.indexOfNominal((String)value);
						
				if (valueIndex != -1)
					newValue = valueIndex;
			}
			else if (value instanceof Integer)
				newValue = (Integer)value;
			else if (value instanceof Number)
				newValue = ((Number)value).intValue();
			
			break;
			
		case integer:
			if (value instanceof Number)
				newValue = ((Number)value).intValue();
			else if (value instanceof String || value instanceof Nominal) {
				String string = value instanceof Nominal ? 
					((Nominal)value).getValue().trim() : ((String)value).trim();
				
				if (!string.isEmpty())
					newValue = Integer.parseInt(string);
			}
			
			break;
			
		case real:
			if (value instanceof Number)
				newValue = ((Number)value).floatValue();
			else if (value instanceof String || value instanceof Nominal) {
				String string = value instanceof Nominal ? 
					((Nominal)value).getValue().trim() : ((String)value).trim();
				
				if (!string.isEmpty())
					newValue = Float.parseFloat(string);
			}
			
			break;
			
		case string:
			if (value instanceof String || value instanceof Nominal) {
				String string = value instanceof Nominal ? 
					((Nominal)value).getValue() : (String)value;
				newValue = string;
			}
			else
				newValue = value.toString();
			
			break;
			
		case date:
			if (value instanceof Date)
				newValue = value;
			else if (value instanceof java.sql.Date) 
				newValue = new Date(((java.sql.Date)value).getTime());
			else if (value instanceof Calendar)
				newValue = ((Calendar)value).getTime();
			else if (value instanceof String || value instanceof Nominal) {
				String string = value instanceof Nominal ? 
					((Nominal)value).getValue().trim() : ((String)value).trim();
				
				if (!string.isEmpty()) {
					try {
						SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);
						newValue = df.parse(string);
					} 
					catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						newValue = null;
					}					
				}
				
			}
			
			break;
			
		case object:
			newValue = value;
			break;
		}
		
		return newValue;
	}

	
	/**
	 * Creating the value for specified attribute and the initial value.
	 * If the value is date time object, it is formatted according to the specified date format.
	 * @param att specified attribute.
	 * @param value specified attribute.
	 * @param df specified date format.
	 * @return new value for specified attribute and the initial value.
	 * @throws Exception if any error raises.
	 */
	public static Object createValue(Attribute att, Object value, DateFormat df) throws Exception {
		if (value == null)
			return null;
		
		Object newValue = null;
		
		if (value instanceof Date)
			newValue = value;
		else if (value instanceof java.sql.Date) 
			newValue = new Date(((java.sql.Date)value).getTime());
		else if (value instanceof Calendar)
			newValue = ((Calendar)value).getTime();
		else if (df == null)
			newValue = createValue(att, value);
		else {
			String string = value instanceof Nominal ? 
					((Nominal)value).getValue().trim() : value.toString().trim();
			
			if (string.isEmpty())
				newValue = createValue(att, value);
			else {
				try {
					newValue = df.parse(string);
				} 
				catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					newValue = null;
				} // end try
				
			} // end if
			
		} // end if
		
		return newValue;
	}
	
	
	/**
	 * Setting the value of profile at specified index by specified value.
	 * @param index specified index.
	 * @param value specified value.
	 */
	public void setValue(int index, Object value) {
		
		Attribute att = attRef.get(index);
		Object newValue = null;
		try {
			newValue = createValue(att, value);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			newValue = null;
		}
		
		if (newValue != null && !(newValue instanceof Serializable))
			throw new RuntimeException("Value isn't serializable class");
		
		attValues.set(index, newValue);
	}
	

	/**
	 * Setting the value of profile at specified index by specified value.
	 * If the value is date time object, the object is formatted according to the specified date format.
	 * @param index specified index.
	 * @param value specified value.
	 * @param df specified date format.
	 */
	public void setValue(int index, Object value, DateFormat df) {
		Attribute att = attRef.get(index);
		Object newValue = null;
		try {
			newValue = createValue(att, value, df);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			newValue = null;
		}
		
		if (newValue != null && !(newValue instanceof Serializable))
			throw new RuntimeException("Value isn't serializable class");
		
		attValues.set(index, newValue);
	}
	
	
	/**
	 * Setting the value of the attribute having specified name by specified value.
	 * @param attName specified name.
	 * @param value specified value.
	 */
	public void setValue(String attName, Object value) {
		int index = indexOf(attName);
		if (index != -1)
			setValue(index, value);
	}
	
	
	/**
	 * Setting missing value at specified index.
	 * @param index specified index.
	 */
	public void setMissing(int index) {
		attValues.set(index, null);
	}
	
	
	/**
	 * Setting values of this profile by specified values.
	 * @param attValues specified list of values.
	 */
	protected void setValues(List<Object> attValues) {
		this.attValues = attValues;
	}
	
	
	/**
	 * Getting the value at specified index.
	 * @param index specified index.
	 * @return value by index.
	 */
	public Object getValue(int index) {
		return attValues.get(index);
	}
	
	
	/**
	 * Getting the value of attribute having the specified name.
	 * @param attName specified attribute name.
	 * @return value by attribute name.
	 */
	public Object getValue(String attName) {
		int index = indexOf(attName);
		if (index != -1)
			return getValue(index);
		else
			return null;
	}
	
	
	/**
	 * Getting value at specified index as string.
	 * @param index specified index.
	 * @return value as string by index
	 */
	public String getValueAsString(int index) {
		Attribute att = attRef.get(index);
		
		if (att.getType() == Type.nominal) {
			int valueIndex = (Integer)attValues.get(index);
			return att.getNominal(valueIndex).getValue();
		}
		else
			return getValue(index).toString();
	}
	
	
	/**
	 * Getting value of attribute having specified name as string.
	 * @param attName specified attribute name.
	 * @return value of attribute having specified name as string.
	 */
	public String getValueAsString(String attName) {
		int index = indexOf(attName);
		if (index != -1)
			return getValueAsString(index);
		else
			return null;
	}

	
	/**
	 * This static method converts the specified value of specified attribute as real number.
	 * @param att specified attribute.
	 * @param value specified value.
	 * @return value as real number.
	 */
	public static float getValueAsReal(Attribute att, Object value) {
		if (value == null)
			return Constants.UNUSED;
			
		Type attType = att.getType();
		switch (attType) {
		
		case bit:
			return ((Byte)value).floatValue();
			
		case nominal:
			return ((Integer)value).floatValue();
			
		case integer:
			return ((Integer)value).floatValue();
			
		case real:
			return (Float)value;
			
		case string:
			try {
				return Float.parseFloat((String)value);
			}
			catch (Throwable e) {
				e.printStackTrace();
				return Constants.UNUSED;
			}
			
		case date:
			return Constants.UNUSED;
			
		case object:
			return Constants.UNUSED;
		}
		
		return Constants.UNUSED;
	}

	
	/**
	 * Getting value at specified index as real number.
	 * @param index specified index.
	 * @return value as real number by index.
	 */
	public float getValueAsReal(int index) {
		if (isMissing(index))
			return Constants.UNUSED;
		
		Attribute att = attRef.get(index);
		Object value = attValues.get(index);
		
		return getValueAsReal(att, value);
	}
	
	
	/**
	 * Getting value of attribute having specified name as real number.
	 * @param attName specified attribute name.
	 * @return value as real number by attribute name.
	 */
	public float getValueAsReal(String attName) {
		int index = indexOf(attName);
		if (index != -1)
			return getValueAsReal(index);
		else
			return Constants.UNUSED;
	}

	
	/**
	 * Getting value at specified index as integer number.
	 * @param index specified index.
	 * @return value as integer by index.
	 */
	public int getValueAsInt(int index) {
		if (isMissing(index))
			return -1;
		
		Attribute att = attRef.get(index);
		Object value = attValues.get(index);
		
		Type attType = att.getType();
		switch (attType) {
		
		case bit:
			return ((Byte)value).intValue();
			
		case nominal:
			return (Integer)value;
			
		case integer:
			return (Integer)value;
			
		case real:
			return ((Float)value).intValue();
			
		case string:
			try {
				return Integer.parseInt((String)value);
			}
			catch (Throwable e) {
				e.printStackTrace();
				return 0;
			}
			
		case date:
			return 0;
			
		case object:
			return 0;
		}
		
		return -1;
	}

	
	/**
	 * Getting value of attribute having specified name as integer.
	 * @param attName specified attribute name.
	 * @return value as integer by attribute name.
	 */
	public int getValueAsInt(String attName) {
		int index = indexOf(attName);
		if (index != -1)
			return getValueAsInt(index);
		else
			return -1;
	}
	
	
	/**
	 * Getting value at specified index as boolean value.
	 * @param index specified index.
	 * @return value as boolean by index.
	 */
	public boolean getValueAsBoolean(int index) {
		if (isMissing(index))
			return false;
		
		Attribute att = attRef.get(index);
		Object value = attValues.get(index);
		
		Type attType = att.getType();
		switch (attType) {
		
		case bit:
			return (Byte)value == 0 ? false : true;
			
		case nominal:
			return (Integer)value == 0 ? false : true;
			
		case integer:
			return (Integer)value == 0 ? false : true;
			
		case real:
			return (Float)value == 0 ? false : true;
			
		case string:
			try {
				return Boolean.parseBoolean((String)value);
			}
			catch (Throwable e) {
				e.printStackTrace();
				return false;
			}
			
		case date:
			return false;
			
		case object:
			return false;
		}
		
		return false;
	}

	
	/**
	 * Getting value of attribute having specified name as boolean.
	 * @param attName specified attribute name.
	 * @return value as boolean by attribute name.
	 */
	public boolean getValueAsBoolean(String attName) {
		int index = indexOf(attName);
		if (index != -1)
			return getValueAsBoolean(index);
		else
			return false;
	}

	
	/**
	 * Getting value at specified index as date-time object. The returned value is formatted according to specified date format.
	 * @param index specified index.
	 * @param df specified date format.
	 * @return {@link Date} value by index.
	 */
	public Date getValueAsDate(int index, DateFormat df) {
		Object value = getValue(index);
		if (value == null)
			return null;
		else if (value instanceof Date)
			return (Date) value;
		else {
			
			try {
				value = createValue(new Attribute("getValueAsDate", Type.date), value, df);
				if (value == null)
					return null;
				else if (value instanceof Date)
					return (Date) value;
				else
					return null;
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
	

	/**
	 * Getting value of attribute having specified name as date-time object.
	 * The returned value is formatted by the specified date format.
	 * @param attName specified attribute name.
	 * @param df specified date format.
	 * @return value of attribute having specified name as date-time object.
	 */
	public Date getValueAsDate(String attName, DateFormat df) {
		int index = indexOf(attName);
		if (index != -1)
			return getValueAsDate(index, df);
		else
			return null;
	}
	
	
	/**
	 * Getting value at specified index as normalized number of nominal.
	 * Nominal indicates discrete and non-number data such as weekdays {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday}.
	 * However, nominal is set as integer in profile. For example, the weekdays nominal is stored as integers {0, 1, 2, 3, 4, 5, 6}.
	 * This method also normalizes such integer value. For examples, the weekdays nominal is normalized as normalized number {0/6, 1/6, 2/6, 3/6, 4/6, 5/6, 6/6}.
	 * 
	 * @param index specified index.
	 * @return normalized nominal value by index.
	 */
	public float getNominalNormalizedValue(int index) {
		Attribute att = attRef.get(index);
		if (att.getType() != Type.nominal)
			throw new RuntimeException("Invalid parameter");
		
		if (isMissing(index))
			return Constants.UNUSED;
		
		float value = getValueAsReal(index);
		MinMax minmax = att.getNominalList().getMinMaxIndex();
		return (value - minmax.min()) / (minmax.max() - minmax.min());
	}
	
	
	/**
	 * Getting value of attribute having specified name as normalized number of nominal.
	 * Nominal indicates discrete and non-number data such as weekdays {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday}.
	 * However, nominal is set as integer in profile. For example, the weekdays nominal is stored as integers {0, 1, 2, 3, 4, 5, 6}.
	 * This method also normalizes such integer value. For examples, the weekdays nominal is normalized as normalized number {0/6, 1/6, 2/6, 3/6, 4/6, 5/6, 6/6}.
	 * @param attName specified attribute name.
	 * @return normalized nominal value by attribute name.
	 */
	public float getNominalNormalizedValue(String attName) {
		int index = indexOf(attName);
		if (index != -1)
			return getNominalNormalizedValue(index);
		else
			return Constants.UNUSED;
	}
	
	
	@Override
	public Object clone() {
		Profile profile = new Profile();
		profile.attRef = this.attRef;
		
		profile.attValues.clear();
		profile.attValues.addAll(this.attValues);
		
		return profile;
	}
	
	
	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		
		List<String> list = TextParserUtil.split(spec, TextParserUtil.MAIN_SEP, null);
		attRef.parseText(list.get(0));
		attValues.clear();
		init();
		
		List<String> values = TextParserUtil.split(list.get(1), ",", null);
		int n = Math.min(attRef.size(), values.size());
		for (int i = 0; i < n; i++) {
			String value = values.get(i);
			if (value.equals(TextParserUtil.NULL_STRING))
				setValue(i, null);
			else
				setValue(i, TextParserUtil.decryptReservedChars(value));
		}
		
	}

	
	@Override
	public String toText() {
		StringBuffer vbuffer = new StringBuffer();
		
		for (int i = 0; i < attValues.size(); i++) {
			if ( i > 0)
				vbuffer.append(", ");

			Object value = attValues.get(i);
			if (value == null)
				vbuffer.append(TextParserUtil.NULL_STRING);
			else if (value instanceof TextParsable)
				vbuffer.append(((TextParsable)value).toText());
			else {
				String string = value.toString();
				string = TextParserUtil.encryptReservedChars(string);
				vbuffer.append(string);
			}
		}
		
		
		return attRef.toText() + " " + TextParserUtil.MAIN_SEP + " " + vbuffer.toString();
	}


	@Override
	public String toString() {
		return toText();
	}
	

}
