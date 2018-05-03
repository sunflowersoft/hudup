/**
 * 
 */
package net.hudup.core.data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import net.hudup.core.Cloneable;
import net.hudup.core.Constants;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;


/**
 * As a convention this class is called attribute. Attribute indicates the data type, which is also a wrapper of data type.
 * Such type is represented by the internal {@link Type} of this class. In current implementation, there are seven data types as follows:
 * <ul>
 * <li>{@code bit}: binary, boolean.</li>
 * <li>{@code nominal}: discrete and non-number data such as weekdays (Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday).</li>
 * <li>{@code integer}: integer number.</li>
 * <li>{@code real}: real number.</li>
 * <li>{@code string}: text data.</li>
 * <li>{@code date}: date and time.</li>
 * <li>{@code object}: any type.</li>
 * </ul>
 * Attribute provides many methods for processing data type and retrieving information from data type.
 * {@code Profile} is one of important data structures, like record of table in database, uses attribute. Profile has a list of values. Each value belongs to a particular attribute.
 * Profile uses attribute to specify its data types but applications of attribute are very large.
 * For convenience, attribute can be considered as field in database table.
 * The concept of attribute is derived from the concept of attribute in Weka - Data Mining Software in Java, available at <a href="http://www.cs.waikato.ac.nz/ml/weka">http://www.cs.waikato.ac.nz/ml/weka</a>
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class Attribute implements Cloneable, TextParsable, Serializable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * This enum represents data type of this attribute. In other words, this attribute is an advanced wrapper of data type.
	 * In current implementation, there are seven data types as follows:
	 * <ul>
	 * <li>{@code bit}: binary, boolean.</li>
	 * <li>{@code nominal}: discrete and non-number data such as weekdays (Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday).</li>
	 * <li>{@code integer}: integer number.</li>
	 * <li>{@code real}: real number.</li>
	 * <li>{@code string}: text data.</li>
	 * <li>{@code date}: date and time.</li>
	 * <li>{@code object}: any type.</li>
	 * </ul>
	 * 
	 * @author Loc Nguyen
	 * @version 10.0
	 *
	 */
	public static enum Type {
		bit, nominal, integer, real, string, date, object
	};
	
	
	/**
	 * Name of attribute.
	 */
	protected String name = "";
	
	
	/**
	 * Internal data type of this attribute. Default value of this type is {@link Type#object} (anything).
	 */
	protected Type type = Type.object;

	
	/**
	 * The index of this attribute. This index is only meaningful if this attribute is a member of attribute list. Note that attribute list is represented by {@link AttributeList}.
	 * For example, if this attribute is the first element in attribute list, its index is 0.
	 * When the index is -1, this attribute is not attached with any attribute list.
	 * Default value of this index is -1.
	 */
	protected int index = -1;

	
	/**
	 * If this value is {@code true}, this attribute is key attribute used to distinguish two records (two profiles).
	 * Exactly, in a profile, a key attribute or a set of some key attributes are used to distinguish two profiles.
	 * If there is only one key attribute, we state that the profile has single key. A integer single key is also called identification (ID). 
	 * If there are a set of key attributes, we state that the profile has complex key.
	 * Single key and complex key are represented by the {@code Keys} class.
	 * Default value of this variable is {@code false}. 
	 */
	protected boolean isKey = false;
	
	
	/**
	 * If this value is {@code true}, this attribute is auto-increment attribute.
	 * For example, suppose a record (profile) whose first value is an integer and has auto-increment attribute; when a new record (profile) is inserted, the value of such record is increased automatically.
	 * Default value of this variable is {@code false}.
	 * Note that the auto-increment is only effective if this attribute has integer type; otherwise, the auto-increment is ignored.
	 */
	protected boolean autoInc = false;
	
	
	/**
	 * In case that type of this attribute indicates nominal type called {@code nominal} in brief, this variable is the list of nominal (s).
	 * Nominal indicates discrete and non-number data such as weekdays (Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday).
	 * Nominal is represented by {@link Nominal} and nominal list is represented by {@link NominalList}.
	 * This variable is the empty nominal list.
	 */
	protected NominalList nominalList = new NominalList();

	
	/**
	 * Default constructor.
	 */
	public Attribute() {
		if (!(nominalList instanceof Serializable))
			throw new RuntimeException("RatingVector isn't serializable class");
	}
	
	
	/**
	 * Constructor with specified attribute.
	 * @param att Specified attribute
	 */
	public Attribute(Attribute att) {
		this();
		
		this.name = att.name;
		this.type = att.type;
		this.index = att.index;
		this.isKey = att.isKey;
		this.autoInc = att.autoInc;
		
		this.nominalList = (NominalList) att.nominalList.clone();
	}
	
	
	/**
	 * Constructor with specified name and specified type. Other internal variables of this attribute have default values.
	 * @param name Specified name.
	 * @param type Specified type.
	 */
	public Attribute(String name, Type type) {

		this.name = name;
		this.type = type;
	}
	
	 
	/**
	 * Constructor with specified name and nominal list, which implies that this attribute is nominal attribute.
	 * Recall that nominal is discrete and non-number data such as weekdays (Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday).
	 * Nominal list is represented by {@link NominalList}.
	 * @param name Specified name.
	 * @param nominalList Specified nominal list.
	 */
	public Attribute(String name, NominalList nominalList) {
		
		this.name = name;
		this.type = Type.nominal;
		
		this.nominalList = (NominalList)nominalList.clone();
	}


	/**
	 * Getting the index of this attribute. This index is only meaningful if this attribute is a member of attribute list. Note that attribute list is represented by {@link AttributeList}.
	 * For example, if this attribute is the first element in attribute list, its index is 0.
	 * When the index is -1, this attribute is not attached with any attribute list.
	 * @return index of this attribute
	 */
	public int getIndex() {

		return index;
	}
	
	
	/**
	 * Setting the index of this attribute. This index is only meaningful if this attribute is a member of attribute list. Note that attribute list is represented by {@link AttributeList}.
	 * For example, if this attribute is the first element in attribute list, its index is 0.
	 * When the index is -1, this attribute is not attached with any attribute list.
	 * @param index Specified index.
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	
	
	/**
	 * If this this method returns {@code true}, this attribute is key attribute used to distinguish two records (two profiles).
	 * Recall that, in a profile, a key attribute or a set of some key attributes are used to distinguish two profiles.
	 * If there is only one key attribute, we state that the profile has single key. A integer single key is also called identification (ID).
	 * If there are a set of key attributes, we state that the profile has complex key.
	 * Single key and complex key are represented by the {@code Keys} class.
	 * @return whether this attribute is key.
	 */
	public boolean isKey() {
		return isKey;
	}
	
	
	/**
	 * Setting that whether this attribute is key attribute. If the specified input parameter is {@code true}, this attribute becomes key attribute used to distinguish two records (two profiles).
	 * Recall that, in a profile, a key attribute or a set of some key attributes are used to distinguish two profiles.
	 * If there is only one key attribute, we state that the profile has single key. A integer single key is also called identification (ID).
	 * If there are a set of key attributes, we state that the profile has complex key.
	 * Single key and complex key are represented by the {@code Keys} class.
	 * @param isKey whether this attribute is key attribute.
	 */
	public void setKey(boolean isKey) {
		this.isKey = isKey;
	}
	
	
	/**
	 * If this method returns {@code true}, this attribute is auto-increment attribute.
	 * For example, suppose a record (profile) whose first value is an integer and has auto-increment attribute; when a new record (profile) is inserted, the value of such record is increased automatically.
	 * Note that the auto-increment is only effective if this attribute has integer type; otherwise, the auto-increment is ignored.
	 * @return whether this attribute has mechanism of auto-increment.
	 */
	public boolean isAutoInc() {
		return autoInc;
	}
	
	
	/**
	 * Setting that whether this attribute is auto-increment attribute. If the specified parameter is {@code true}, this attribute becomes auto-increment attribute.
	 * For example, suppose a record (profile) whose first value is an integer and has auto-increment attribute; when a new record (profile) is inserted, the value of such record is increased automatically.
	 * Note that the auto-increment is only effective if this attribute has integer type; otherwise, the auto-increment is ignored.
	 * @param autoInc whether this attribute is auto-increment attribute.
	 */
	public void setAutoInc(boolean autoInc) {
		if (type == Type.integer)
			this.autoInc = autoInc && Constants.SUPPORT_AUTO_INCREMENT_ID;
	}
	
	
	/**
	 * Each nominal represented by {@link Nominal} class has a value and an index in the nominal list referred by variable {@link #nominalList}. Nominal value is a text string representing such nominal.
	 * This method looks up the index of given nominal value in case that this attribute has nominal type.
	 * @param nominalValue Specified nominal value.
	 * @return Index of specified nominal value.
	 */
	public int indexOfNominal(String nominalValue) {
		return nominalList.indexOfValue(nominalValue);
	}
	
	
	/**
	 * This method looks up the index of the specified nominal in the nominal list referred by variable {@link #nominalList}.
	 * This method is only meaningful if this attribute has nominal type.
	 * @param nominal Specified nominal.
	 * @return Index of specified nominal.
	 */
	public int indexOfNominal(Nominal nominal) {
		return nominalList.indexOf(nominal);
	}
	
	
	/**
	 * Counting nominal (s) in the nominal list referred by variable {@link #nominalList} in case that this attribute has nominal type.
	 * @return The number of nominal (s) in nominal list referred by variable {@link #nominalList}.
	 */
	public int getNominalCount() {
		return nominalList.size();
	}
	

	/**
	 * Each nominal represented by {@link Nominal} class has a value and an index in the nominal list referred by variable {@link #nominalList}.
	 * This methods returns a nominal having specified index in nominal list referred by variable {@link #nominalList} in case that this attribute has nominal type.
	 * @param nominalIdx Specified index.
	 * @return The nominal having specified index in nominal list referred by variable {@link #nominalList}.
	 */
	public Nominal getNominal(int nominalIdx) {
		return nominalList.get(nominalIdx);
		
	}
	
	
	/**
	 * Recall that each nominal represented by {@link Nominal} class has a value and an index in the nominal list referred by variable {@link #nominalList}. Nominal value is a text string representing such nominal.
	 * This methods returns value of the nominal having specified index in nominal list referred by variable {@link #nominalList} in case that this attribute has nominal type.
	 * @param nominalIdx Specified index.
	 * @return Value of the nominal having specified index in nominal list referred by variable {@link #nominalList}.
	 */
	public String getNominalValue(int nominalIdx) {
		return getNominal(nominalIdx).getValue();
		
	}
	
	
	/**
	 * Retrieving the built-in nominal list in case that this attribute has nominal type.
	 * @return The built-in nominal list represented by {@link NominalList} class.
	 */
	public NominalList getNominalList() {
		return nominalList;
	}
	
	
	/**
	 * Retrieving a list of string values of the built-in nominal list in case that this attribute has nominal type.
	 * @return The {@link List} of string values of the built-in nominal list.
	 */
	public List<String> getNominalValueList() {
		return nominalList.getValueList();
	}

	
	/**
	 * Adding a specified nominal into the built-in nominal list referred by the variable {@link #nominalList} in case that this attribute has nominal type.
	 * @param nominal Specified nominal represented by {@link Nominal} class.
	 */
	public void addNominal(Nominal nominal) {
		nominalList.add(nominal);
	}
	
	
	/**
	 * Setting (replacing) the current built-in nominal list by the specified nominal list in case that this attribute has nominal type.
	 * @param nominalList Specified nominal list.
	 */
	public void setNominalList(NominalList nominalList) {
		if (nominalList != null)
			this.nominalList = nominalList;
		else
			this.nominalList.clear();
	}

	
	/**
	 * Clearing the built-in nominal list referred by the variable {@link #nominalList}, which means that all nominal (s) are removed from such nominal list.
	 */
	public void clearNominalList() {
		nominalList.clear();
	}

	
	/**
	 * In case that this attribute has nominal type, this method adds a nominal composed of the into the built-in nominal list.
	 * Please see {@link Nominal} class for more details about information of nominal.
	 * @param nominalValue Specified nominal value.
	 * @param nominalIndex Specified nominal index.
	 * @param parentIndex Specified nominal parent index.
	 */
	public void addNominal(String nominalValue, int nominalIndex, int parentIndex) {
		nominalList.add(new Nominal(nominalValue, nominalIndex, parentIndex));
	}

	
	/**
	 * Recall that each nominal represented by {@link Nominal} class has a value and an index in the nominal list referred by variable {@link #nominalList}.
	 * This method removes a nominal from the built-in nominal list at specified index.
	 * @param nominalIndex Specified nominal index.
	 */
	public void removeNominal(int nominalIndex) {
		nominalList.remove(nominalIndex);
	}

	
	/**
	 * Getting the type of this attribute.
	 * @return The type of this attribute represented by {@link Type} enum.
	 */
	public Type getType() {
		return type;
	}
	
	
	/**
	 * The category type indicates discrete and non-number data.
	 * In current implementation, category type includes {@code bit} type represented by the enum {@link Type#bit} and {@code nominal} type represented by {@link Type#nominal}.
	 * This method checks whether or not this attribute is category attribute.
	 * This method calls the static method {@link #isCategory()}.
	 * @return whether category attribute is.
	 */
	public boolean isCategory() {
		return isCategory(type);
	}

	
	/**
	 * The category type indicates discrete and non-number data.
	 * In current implementation, category type includes {@code bit} type represented by the enum {@link Type#bit} and {@code nominal} type represented by {@link Type#nominal}.
	 * This method checks whether or not the specified type is category type.
	 * @param type Specified type.
	 * @return whether type is category.
	 */
	public static boolean isCategory(Type type) {
		return type == Type.bit || type == Type.nominal;
	}
	
	
	/**
	 * The number type indicates number. In current implementation, number type includes {@code integer} type represented by {@link Type#integer} and {@code real} type represented by {@link Type#real}. 
	 * This method checks whether or not this attribute is number attribute.
	 * @return Whether or not this attribute is number attribute.
	 */
	public boolean isNumber() {
		return isNumber(type); 
	}
	
	
	/**
	 * The number type indicates number. In current implementation, number type includes {@code integer} type represented by {@link Type#integer} and {@code real} type represented by {@link Type#real}. 
	 * This method checks whether or not the specified type is number type.
	 * @param type Specified type.
	 * @return Whether or not the specified type is number type.
	 */
	public static boolean isNumber(Type type) {
		return type == Type.integer || 
				type == Type.real;
	}

	
	/**
	 * In this current implementation, finite number is not floating-point number (not decimal number). In other words, it is integer.
	 * This method returns {@code true} if this attribute indicates finite number. Otherwise, this methods return {@code false}. 
	 * @return Whether or not this attribute indicates finite number.
	 */
	public boolean isInteger() {
		return isInteger(type);
	}
	
	
	/**
	 * In this current implementation, finite number is not floating-point number (not decimal number). In other words, it is integer.
	 * This method returns {@code true} if the specified type indicates finite number. Otherwise, this methods return {@code false}.
	 * @param type Specified type. 
	 * @return Whether or not the specified type indicates finite number.
	 */
	public static boolean isInteger(Type type) {
		return isNumber(type) && type != Type.real;
	}

	
	/**
	 * Checking whether this attribute is nominal attribute.
	 * Nominal type indicates discrete and non-number data such as weekdays (Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday).
	 * Nominal is represented by {@link Nominal} class.
	 * @return whether this attribute is nominal attribute.
	 */
	public boolean isNominal() {
		return isNominal(type);
	}

	
	/**
	 * Checking whether the specified type is nominal type.
	 * Nominal type indicates discrete and non-number data such as weekdays (Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday).
	 * Nominal is represented by {@link Nominal} class.
	 * @param type Specified type.
	 * @return whether the specified type is nominal type.
	 */
	public static boolean isNominal(Type type) {
		return type == Type.nominal;
	}
	
	
	/**
	 * This method checks whether or not this attribute is shown as a number.
	 * Types such as {@link Type#bit}, {@link Type#nominal}, and {@link Type#bit} are shown as a number.
	 * Note that {@link Type#bit} and {@link Type#nominal} does not indicate a number.
	 * @return Whether or not this attribute is shown as a number.
	 */
	public boolean isShownAsNumber() {
		return isShownAsNumber(type);
	}
	
	
	/**
	 * This static method checks whether or not the specified type is shown as a number.
	 * Types such as {@link Type#bit}, {@link Type#nominal}, and {@link Type#bit} are shown as a number.
	 * Note that {@link Type#bit} and {@link Type#nominal} does not indicate a number.
	 * @param type Specified type.
	 * @return whether or not the specified type is shown as a number.
	 */
	public static boolean isShownAsNumber(Type type) {
		return type == Type.bit || 
				type == Type.nominal || 
				type == Type.integer || 
				type == Type.real;

	}

	
	/**
	 * Retrieving a string array of discrete values when this attribute has category type.
	 * For example, if this attribute has binary (bit) type, the method returns the array <code>{&quot;0&quot;, &quot;1&quot;}</code>.
	 * If this attribute indicates weekdays (nominal type), the method returns the array <code>{&quot;Monday&quot;, &quot;Tuesday&quot;, &quot;Wednesday&quot;, &quot;Thursday&quot;, &quot;Friday&quot;, &quot;Saturday&quot;, &quot;Sunday&quot;}</code>.
	 * @return String array of discrete values when this attribute has category type.
	 */
	public String[] getCategoryValues() {
		if (type == Type.bit)
			return new String[] {"0", "1"};
		else if (type == Type.nominal)
			return getNominalValueList().toArray(new String[0]);
		else
			return new String[0];
	}
	
	
	/**
	 * Getting the name of this attribute.
	 * @return Name of this attribute.
	 */
	public String getName() {
		return name;
	}
		
	
	@Override
	public Object clone() {
		return new Attribute(this);
	}
	
	
	/**
	 * This static method returns the type represented by enum {@link Type} of the specified object.
	 * @param obj Specified object.
	 * @return The type of specified object.
	 */
	public static Type fromObject(Object obj) {
		if (obj instanceof Boolean)
			return Type.bit;
		else if (obj instanceof Byte)
			return Type.integer;
		else if (obj instanceof Short)
			return Type.integer;
		else if (obj instanceof Integer)
			return Type.integer;
		else if (obj instanceof Long)
			return Type.integer;
		else if (obj instanceof Float)
			return Type.real;
		else if (obj instanceof Double)
			return Type.real;
		else if (obj instanceof Character)
			return Type.string;
		else if (obj instanceof String)
			return Type.string;
		else if (obj instanceof Date)
			return Type.date;
		else
			return Type.object;
		
	}


	/**
	 * This static method returns the type represented by enum {@link Type} of the specified object indicated by its class.
	 * @param objClass Class of specified object, represented by {@link Class} class.
	 * @return Type of the specified object indicated by its class. 
	 */
	public static Type fromObjectClass(Class<?> objClass) {
		if (objClass.equals(Boolean.class) || objClass.equals(boolean.class))
			return Type.bit;
		else if (objClass.equals(Byte.class) || objClass.equals(byte.class))
			return Type.integer;
		else if (objClass.equals(Short.class) || objClass.equals(short.class))
			return Type.integer;
		else if (objClass.equals(Integer.class) || objClass.equals(int.class))
			return Type.integer;
		else if (objClass.equals(Long.class) || objClass.equals(long.class))
			return Type.integer;
		else if (objClass.equals(Float.class) || objClass.equals(float.class))
			return Type.real;
		else if (objClass.equals(Double.class) || objClass.equals(double.class))
			return Type.real;
		else if (objClass.equals(Character.class) || objClass.equals(char.class))
			return Type.string;
		else if (objClass.equals(String.class))
			return Type.string;
		else if (objClass.equals(Date.class))
			return Type.date;
		else
			return Type.object;
		
	}

	
	/**
	 * This static method returns the type represented by enum {@link Type} from the specified index of a type.
	 * Please see the order of types in the enum {@link Type}.
	 * For example, the type of index 0 is {@link Type#bit}, the type of index 1 is {@link Type#nominal}.
	 * @param itype Specified index of a type.
	 * @return Type of the specified index.
	 */
	public static Type fromInt(int itype) {
		
		switch (itype) {
		
		case 0:
			return Type.bit;
		case 1:
			return Type.nominal;
		case 2:
			return Type.integer;
		case 3:
			return Type.real;
		case 4:
			return Type.string;
		case 5:
			return Type.date;
		case 6:
			return Type.object;
		default:
			return Type.object;
		}
		
	}

	
	/**
	 * This static method returns the type represented by enum {@link Type} from the specified string (name) of a type.
	 * For example, the type from specified string &quot;bit&quot; is {@link Type#bit} and the type from specified string &quot;nominal&quot; is {@link Type#nominal}.
	 * @param stype Specified string (name) of a type.
	 * @return Type from the specified string (name).
	 */
	public static Type fromString(String stype) {
		stype = stype.toLowerCase();
		if (stype.equals("bit") || stype.equals("binary"))
			return Type.bit;
		else if (stype.equals("bool") || stype.equals("boolean"))
			return Type.bit;
		else if (stype.equals("nominal"))
			return Type.nominal;
		else if (stype.equals("byte") || stype.equals("integer") || stype.equals("int") || 
				stype.equals("long") || stype.equals("short"))
			return Type.integer;
		else if (stype.equals("real") || stype.equals("decimal") || 
				stype.equals("float") || stype.equals("double"))
			return Type.real;
		else if (stype.equals("character") || stype.equals("char"))
			return Type.string;
		else if (stype.equals("string"))
			return Type.string;
		else if (stype.equals("date"))
			return Type.date;
		else if (stype.equals("object"))
			return Type.object;
		else
			return Type.object;
	}
	
	
	/**
	 * This static method converts the specified type into its index. For example, the index of {@link Type#bit} is 0.
	 * Please see the order of types in the enum {@link Type}.
	 * @param type Specified type
	 * @return The index of specified type in enum {@link Type} as an integer.
	 */
	public static int toInt(Type type) {
		
		switch (type) {
		
		case bit:
			return 0;
		case nominal:
			return 1;
		case integer:
			return 2;
		case real:
			return 3;
		case string:
			return 4;
		case date:
			return 5;
		case object:
			return 6;
		default:
			return 6;
		}
		
	}
	

	/**
	 * This static method converts the specified type into its name as a string. For example, the name (string form) of {@link Type#bit} is &quot;bit&quot;.
	 * @param type Specified type.
	 * @return String form of specified type. It is really the name of such specified type.
	 */
	public static String toTypeString(Type type) {
		
		switch (type) {
		
		case bit:
			return "bit";
		case nominal:
			return "nominal";
		case integer:
			return "integer";
		case real:
			return "real";
		case string:
			return "string";
		case date:
			return "date";
		case object:
			return "object";
		default:
			return "object";
		}
		
	}

	
	/**
	 * This static method converts the specified type into the class of such type. For example, the class of {@link Type#bit} is {@code Boolean.class}.
	 * @param type Specified type.
	 * @return Class of specified type, represented by {@link Class} class.
	 */
	public static Class<?> toObjectClass(Type type) {
		
		switch (type) {
		
		case bit:
			return Boolean.class;
		case nominal:
			return Integer.class;
		case integer:
			return Integer.class;
		case real:
			return Float.class;
		case string:
			return String.class;
		case date:
			return Date.class;
		case object:
			return Object.class;
		default:
			return Object.class;
		}
		
	}

	
	/**
	 * Clearing all internal variables so that this attribute becomes empty.
	 */
	private void empty() {
		name = "";
		type = Type.object;
		index = -1;
		isKey = false;
		autoInc = false;
		nominalList.clear();
	}
	
	
	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		
		empty();
		
		List<String> list = TextParserUtil.parseTextList(spec, TextParserUtil.EXTRA_SEP);
		if (list.size() == 0)
			return;
		
		List<String> mainList = TextParserUtil.parseTextList(list.get(0), TextParserUtil.LINK_SEP);
		if (mainList.size() < 5)
			return;
		
		name = TextParserUtil.decryptReservedChars(mainList.get(0));
		type = fromString(
				TextParserUtil.decryptReservedChars(mainList.get(1)));
		index = Integer.parseInt(
				TextParserUtil.decryptReservedChars(mainList.get(2)));
		isKey = Boolean.parseBoolean(mainList.get(3));
		autoInc = Boolean.parseBoolean(mainList.get(4)); 
				
		if (list.size() < 2)
			return;
		
		nominalList = NominalList.parseNominalList(list.get(1), TextParserUtil.LINK_SEP); 
	}
	
	
	
	@Override
	public String toText() {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(
				TextParserUtil.encryptReservedChars(name) + TextParserUtil.LINK_SEP + 
				TextParserUtil.encryptReservedChars(toTypeString(type)) + TextParserUtil.LINK_SEP + 
				index + TextParserUtil.LINK_SEP +
				isKey + TextParserUtil.LINK_SEP +
				autoInc);
		
		if (nominalList.size() > 0) {
			buffer.append(TextParserUtil.EXTRA_SEP);
			
			buffer.append(NominalList.toText(nominalList, TextParserUtil.LINK_SEP));
		}
		
		return buffer.toString();
	}
	
	
	@Override
	public String toString() {
		return toText();
	}

	
	/**
	 * Checking whether this attribute identifies with specified attribute, which means that checking if two attributes are identical.
	 * @param att Specified attribute.
	 * @return whether this attribute identifies with specified attribute.
	 */
	public boolean identity(Attribute att) {
		// TODO Auto-generated method stub
		if (att == null)
			return false;

		return this.name.equals(att.name) &&
				this.type == att.type &&
				this.index == att.index &&
				this.isKey == att.isKey &&
				this.autoInc == att.autoInc &&
				this.nominalList.identity(att.nominalList);
	}
	
	
}
