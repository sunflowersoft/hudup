/**
 * 
 */
package net.hudup.core.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import net.hudup.core.Cloneable;
import net.hudup.core.Constants;
import net.hudup.core.Transfer;
import net.hudup.core.Util;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;


/**
 * This class represents a list of attribute, called {@code attribute list}, as a convention.
 * It has a built-in variable {@link #list} containing all attribute. It provides utility methods processing such list (adding an element, setting an element, removing an element, etc.) as the Java {@link List} and more advanced methods.
 * Recall that attribute represented by {@link Attribute} class indicates the data type, which is also a wrapper of data type.
 * 
 * {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
 * Profile uses attribute to specify its data types and so profile owns a reference to an attribute list.
 * In general, attribute list is very important to profile.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class AttributeList implements Cloneable, TextParsable, Serializable, Transfer {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Built-in variable which is the {@link List} containing all attributes.
	 */
	protected List<Attribute> list = Util.newList();
	
	
	/**
	 * Default constructor.
	 */
	public AttributeList() {
		super();
		// TODO Auto-generated constructor stub
		
		if (!(list instanceof Serializable))
			throw new RuntimeException("RatingVector isn't serializable class");
	}


	/**
	 * Getting the size of attribute list.
	 * @return size of this list
	 */
	public int size() {
		return list.size();
	}
	
	
	/**
	 * Getting the attribute at specified index.
	 * 
	 * @param idx Specified index.
	 * @return The attribute at specified index.
	 */
	public Attribute get(int idx) {
		return list.get(idx);
	}
	
	
	
	/**
	 * Getting the internal list of attributes.
	 * 
	 * @return The internal {@link List} of attributes.
	 */
	public List<Attribute> getList() {
		return list;
	}
	
	
	/**
	 * Adding the specified attribute into this attribute list.
	 * 
	 * @param att Specified attribute added at the end of this attribute list.
	 * @return this {@link AttributeList}.
	 */
	public AttributeList add(Attribute att) {
		att.setIndex(list.size());
		list.add(att);
		
		return this;
	}
	
	
	/**
	 * Looking up an attribute by the specified attribute name. The method returns the index of the attribute having such name.
	 * If no attribute is found, the method returns -1.
	 *  
	 * @param attName Specified attribute name
	 * @return Index of the attribute having such name in this attribute list. If no attribute is found, the method returns -1.
	 */
	public int indexOf(String attName) {
		for (int i = 0; i < list.size(); i++) {
			Attribute att = list.get(i);
			
			if (att.getName().equals(attName))
				return i;
		}
		
		return -1;
	}
	
	
	/**
	 * Clearing this attribute list, which means that all attributes are removed from this attribute list.
	 */
	public void clear() {
		list.clear();
	}
	
	
	@Override
	public Object clone() {
		AttributeList newAttList = new AttributeList();
		
		for (int i = 0; i < this.size(); i++) {
			Attribute att = (Attribute) this.get(i).clone();
			newAttList.add(att);
		}
		
		return newAttList;
	}
	
	
	@Override
	public Object transfer() {
		AttributeList attList = new AttributeList();
		attList.list.addAll(this.list);
		
		return attList;
	}
	
	
	/**
	 * Creating an attribute list from the specified array of attributes.
	 * 
	 * @param array Specified array of attributes.
	 * @return Attribute list from the a specified array of attributes.
	 */
	public static AttributeList create(Attribute[] array) {
		AttributeList attList = new AttributeList();
		
		for (Attribute att : array) {
			attList.add(att);
		}
		
		return attList;
	}


	/**
	 * Creating an attribute list from the specified collection of attributes.
	 * 
	 * @param attributes Specified collection of attributes.
	 * @return Attribute list from the specified collection of attributes.
	 */
	public static AttributeList create(Collection<Attribute> attributes) {
		AttributeList attList = new AttributeList();
		
		for (Attribute att : attributes) {
			attList.add(att);
		}
		
		return attList;
	}

	
	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		clear();
		
		list = TextParserUtil.parseTextParsableList(spec, Attribute.class, ",");
	}
	
	
	@Override
	public String toText() {
		return TextParserUtil.toText(list, ",");
	}
	
	
	@Override
	public String toString() {
		return toText();
	}
	
	
	/**
	 * Normalizing this attribute list. In current implementation, this method only set the identification (ID) attribute of this attribute list is key and auto-increment (according to the parameter {@code autoInc}) attribute.
	 * The ID attribute is established according to input parameters such as {@code idName}, {@code type}, and {@code autoInc}.
	 * 
	 * @param idName Name of identification (ID) attribute.
	 * @param type Type of identification (ID) attribute.
	 * @param autoInc Whether or not the identification (ID) attribute has auto-increment mechanism.
	 * @return {@link AttributeList}
	 */
	public AttributeList nomalizeId(String idName, Type type, boolean autoInc) {
		List<Attribute> list = Util.newList();
		list.addAll(this.list);
		
		for (Attribute att : list) {
			if (att.getName().equals(idName)) {
				list.remove(att);
				break;
			}
		}
		
		AttributeList result = new AttributeList();
		Attribute id = new Attribute(idName, type);
		id.setKey(true);
		id.setAutoInc(autoInc);
		result.add(id);
		for (Attribute att : list) {
			att.setKey(false);
			result.add(att);
		}
		
		return result;
	}
	
	
	
	/**
	 * Recall that {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * In a profile, a key attribute or a set of some key attributes are used to distinguish two profiles.
	 * If there is only one key attribute, we state that the profile has single key. A integer single key is also called identification (ID).
	 * If there are a set of key attributes, we state that the profile has complex key.
	 * Single key and complex key are represented by the {@link Keys} class.
	 * This method returns a list of key attribute in this attribute list represented by {@link Keys} class.
	 * Note that the method {@link Attribute#isKey()} of a key attribute returns {@code true}.
	 * 
	 * @return {@link Keys} of attribute list
	 */
	public Keys getKeys() {
		Keys keys = new Keys();
		for (Attribute att : list) {
			if (att.isKey)
				keys.list.add(att);
		}
		
		return keys;
	}
	
	
	
	/**
	 * Recall that {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * In a profile, a key attribute or a set of some key attributes are used to distinguish two profiles.
	 * If there is only one key attribute, we state that the profile has single key. A integer single key is also called identification (ID).
	 * If there are a set of key attributes, we state that the profile has complex key.
	 * Single key and complex key are represented by the {@code Keys} class.
	 * This method establishes that the attribute having specified name becomes key attribute.
	 * Note that the method {@link Attribute#isKey()} of a key attribute returns {@code true}.
	 * 
	 * @param attName Name of specified attribute.
	 */
	public void setKey(String attName) {
		int idx = indexOf(attName);
		if (idx != -1)
			list.get(idx).setKey(true);
	}
	
	
	
	/**
	 * Recall that {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * In a profile, a key attribute or a set of some key attributes are used to distinguish two profiles.
	 * If there is only one key attribute, we state that the profile has single key. A integer single key is also called identification (ID).
	 * If there are a set of key attributes, we state that the profile has complex key.
	 * Single key and complex key are represented by the {@code Keys} class.
	 * This method establishes that the attributes having specified names becomes key attributes.
	 * Note that the method {@link Attribute#isKey()} of a key attribute returns {@code true}.
	 * 
	 * @param attNameList Names of specified attributes.
	 */
	public void setKeys(Collection<String> attNameList) {
		for (String attName : attNameList) {
			setKey(attName);
		}
	}
	
	
	/**
	 * Recall that {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * In a profile, a key attribute or a set of some key attributes are used to distinguish two profiles.
	 * If there is only one key attribute, we state that the profile has single key. A integer single key is also called identification (ID).
	 * If there are a set of key attributes, we state that the profile has complex key.
	 * Single key and complex key are represented by the {@code Keys} class.
	 * This method establishes that the attribute having specified index becomes key attribute.
	 * Note that the method {@link Attribute#isKey()} of a key attribute returns {@code true}.
	 * 
	 * @param attIndex Index of specified attribute.
	 */
	public void setKey(int attIndex) {
		list.get(attIndex).setKey(true);
	}
	
	
	
	/**
	 * Recall that {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * In a profile, a key attribute or a set of some key attributes are used to distinguish two profiles.
	 * If there is only one key attribute, we state that the profile has single key. A integer single key is also called identification (ID).
	 * If there are a set of key attributes, we state that the profile has complex key.
	 * Single key and complex key are represented by the {@code Keys} class.
	 * This method establishes that the attributes having specified indices becomes key attributes.
	 * Note that the method {@link Attribute#isKey()} of a key attribute returns {@code true}.
	 * 
	 * @param attIndexList {@link Collection} of indices of specified attributes.
	 */
	public void setKeysByIndexList(Collection<Integer> attIndexList) {
		for (int attIndex : attIndexList) {
			setKey(attIndex);
		}
		
	}
	
	
	/**
	 * Recall that {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * In a profile, a key attribute or a set of some key attributes are used to distinguish two profiles.
	 * If there is only one key attribute, we state that the profile has single key. A integer single key is also called identification (ID).
	 * If there are a set of key attributes, we state that the profile has complex key.
	 * Single key and complex key are represented by the {@code Keys} class.
	 * This method establishes that the attributes having specified indices becomes key attributes.
	 * Note that the method {@link Attribute#isKey()} of a key attribute returns {@code true}.
	 * 
	 * @param attIndexes Array of indices of specified attributes.
	 */
	public void setKeys(int[] attIndexes) {
		for (int attIndex : attIndexes) {
			setKey(attIndex);
		}
	}


	/**
	 * Recall that {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * In a profile, a key attribute or a set of some key attributes are used to distinguish two profiles.
	 * If there is only one key attribute, we state that the profile has single key. A integer single key is also called identification (ID).
	 * If there are a set of key attributes, we state that the profile has complex key.
	 * Single key and complex key are represented by the {@code Keys} class.
	 * This method returns the ID attribute in this attribute list.
	 * 
	 * @return ID attribute in this attribute list.
	 */
	public Attribute getId() {
		Keys keys = getKeys();
		if (keys.size() != 1)
			return null;
					
		Attribute key = keys.get(0);
		if (key.getType() == Type.integer)
			return key;
		else
			return null;
	}
	
	
	/**
	 * Recall that {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * In a profile, a key attribute or a set of some key attributes are used to distinguish two profiles.
	 * If there is only one key attribute, we state that the profile has single key. A integer single key is also called identification (ID).
	 * If there are a set of key attributes, we state that the profile has complex key.
	 * Single key and complex key are represented by the {@code Keys} class.
	 * Condition is the special profile which is used in condition clause {@code where} in SQL statement for processing database, for example.
	 * However condition can be used in any necessary situation.
	 * This method creates an empty condition of key attributes. All values of empty condition are {@code null}.
	 * 
	 * @return Empty condition of key attributes.
	 */
	public Condition getEmptyKeyCondition() {
		Keys keys = getKeys();
		AttributeList keyAttList = AttributeList.create(keys.getList());
		return new Condition(keyAttList);
	}
	
	
	/**
	 * Creating the default attribute list of a configuration in which the key attribute has name specified by {@link DataConfig#ATTRIBUTE_FIELD} and the value attribute has name specified by {@link DataConfig#ATTRIBUTE_VALUE_FIELD}.
	 * @return Default attribute list of a configuration. Such attribute list forms a structure of a configuration storage such as a table of configuration in database.
	 */
	public static AttributeList defaultConfigAttributeList() {
		AttributeList attributes = new AttributeList();
		//
		Attribute cfgAtt = new Attribute(DataConfig.ATTRIBUTE_FIELD, Type.string);
		cfgAtt.setKey(true);
		attributes.add(cfgAtt);
		//
		Attribute attValue = new Attribute(DataConfig.ATTRIBUTE_VALUE_FIELD, Type.string);
		attributes.add(attValue);
		
		return attributes;
	}
	
	
	/**
	 * Creating the default attribute list of user information. Such attribute list has two attributes whose names are specified by constants as follows:
	 * <ul>
	 * <li>{@link DataConfig#USERID_FIELD}</li>
	 * <li>{@link DataConfig#USER_TYPE_FIELD}</li>
	 * </ul>
	 * @return Default attribute list of user information. Such attribute list forms a structure of a user storage such as a table of user in database.
	 */
	public static AttributeList defaultUserAttributeList() {
		AttributeList userAttList = new AttributeList();
		Attribute att = new Attribute(DataConfig.USERID_FIELD, Type.integer);
		att.setKey(true);
		att.setAutoInc(Constants.SUPPORT_AUTO_INCREMENT_ID);
		userAttList.add(att);
		
		userAttList.add(new Attribute(DataConfig.USER_TYPE_FIELD, Type.integer));

		return userAttList;
	}
	
	
	/**
	 * Creating the default attribute list of item information. Item may be books, goods, etc. Such attribute list has two attributes whose names are specified by constants as follows:
	 * <ul>
	 * <li>{@link DataConfig#ITEMID_FIELD}</li>
	 * <li>{@link DataConfig#ITEM_TYPE_FIELD}</li>
	 * </ul>
	 * @return Default attribute list of item information. Such attribute list forms a structure of a item storage such as a table of item in database.
	 */
	public static AttributeList defaultItemAttributeList() {
		AttributeList itemAttList = new AttributeList();
		Attribute att = new Attribute(DataConfig.ITEMID_FIELD, Type.integer);
		att.setKey(true);
		att.setAutoInc(Constants.SUPPORT_AUTO_INCREMENT_ID);
		itemAttList.add(att);
		
		itemAttList.add(new Attribute(DataConfig.ITEM_TYPE_FIELD, Type.integer));
		
		return itemAttList;
	}


	/**
	 * Creating the default attribute list of a rating that a user gives on an item. Such attribute list has four attributes whose names are specified by constants as follows:
	 * <ul>
	 * <li>{@link DataConfig#USERID_FIELD}</li>
	 * <li>{@link DataConfig#ITEMID_FIELD}</li>
	 * <li>{@link DataConfig#RATING_FIELD}</li>
	 * <li>{@link DataConfig#RATING_DATE_FIELD}</li>
	 * </ul>
	 * @return Default attribute list of rating. Such attribute list forms a structure of a rating storage such as a table of rating in database.
	 */
	public static AttributeList defaultRatingAttributeList() {
		AttributeList attributes = new AttributeList();
		//
		Attribute userId = new Attribute(DataConfig.USERID_FIELD, Type.integer);
		userId.setKey(true);
		attributes.add(userId);
		//
		Attribute itemId = new Attribute(DataConfig.ITEMID_FIELD, Type.integer);
		itemId.setKey(true);
		attributes.add(itemId);
		//
		Attribute rating = new Attribute(DataConfig.RATING_FIELD, Type.real);
		attributes.add(rating);
		//
		Attribute ratingDate = new Attribute(DataConfig.RATING_DATE_FIELD, Type.date);
		attributes.add(ratingDate);
		
		return attributes;
	}
	
	
	/**
	 * Creating the default attribute list of nominal. Recall that nominal indicates discrete and non-number data such as weekdays {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday}.
	 * Such attribute list has five attributes whose names are specified by constants as follows:
	 * <ul>
	 * <li>{@link DataConfig#NOMINAL_REF_UNIT_FIELD}</li>
	 * <li>{@link DataConfig#ATTRIBUTE_FIELD}</li>
	 * <li>{@link DataConfig#NOMINAL_INDEX_FIELD}</li>
	 * <li>{@link DataConfig#NOMINAL_VALUE_FIELD}</li>
	 * <li>{@link DataConfig#NOMINAL_PARENT_INDEX_FIELD}</li>
	 * </ul>
	 * @return Default attribute list of nominal. Such attribute list forms a structure of a nominal storage such as a table of nominal in database.
	 */
	public static AttributeList defaultNominalAttributeList() {
		AttributeList attributes = new AttributeList();
		//
		Attribute nominalRefUnit = new Attribute(DataConfig.NOMINAL_REF_UNIT_FIELD, Type.string);
		nominalRefUnit.setKey(true);
		attributes.add(nominalRefUnit);
		//
		Attribute nominalAtt = new Attribute(DataConfig.ATTRIBUTE_FIELD, Type.string);
		nominalAtt.setKey(true);
		attributes.add(nominalAtt);
		//
		Attribute nominalIndex = new Attribute(DataConfig.NOMINAL_INDEX_FIELD, Type.integer);
		nominalIndex.setKey(true);
		attributes.add(nominalIndex);
		//
		Attribute nominalValue = new Attribute(DataConfig.NOMINAL_VALUE_FIELD, Type.string);
		attributes.add(nominalValue);
		//
		Attribute nominalParentIndex = new Attribute(DataConfig.NOMINAL_PARENT_INDEX_FIELD, Type.integer);
		attributes.add(nominalParentIndex);
		
		return attributes;
	}
	
	
	/**
	 * Creating the default attribute list of account information. Account contains access information of a user for logging into server according to user privileges. 
	 * Such attribute list has three attributes whose names are specified by constants as follows:
	 * <ul>
	 * <li>{@link DataConfig#ACCOUNT_NAME_FIELD}</li>
	 * <li>{@link DataConfig#ACCOUNT_PASSWORD_FIELD}</li>
	 * <li>{@link DataConfig#ACCOUNT_PRIVILEGES_FIELD}</li>
	 * </ul>
	 * @return Default attribute list of user account. Such attribute list forms a structure of a user account storage such as a table of user account in database.
	 */
	public static AttributeList defaultAccountAttributeList() {
		AttributeList attributes = new AttributeList();
		//
		Attribute accountName = new Attribute(DataConfig.ACCOUNT_NAME_FIELD, Type.string);
		accountName.setKey(true);
		attributes.add(accountName);
		//
		Attribute accountPassword = new Attribute(DataConfig.ACCOUNT_PASSWORD_FIELD, Type.string);
		attributes.add(accountPassword);
		//
		Attribute accountPrivs = new Attribute(DataConfig.ACCOUNT_PRIVILEGES_FIELD, Type.string);
		attributes.add(accountPrivs);
		
		return attributes;
	}

	
	/**
	 * Creating the default attribute list of attribute map. Attribute map is used to mapping additional internal information to external information.
	 * For example, given the internal table {@code Item} in Hudup database contains only item identification (item ID) and item name.
	 * There is an external table {@code Product} in outside database contains product price and product vendor.
	 * The attribute map allows to mapping product price and product vendor into Hudup database as item price and item vendor.
	 * Such attribute list has three attributes whose names are specified by constants as follows:
	 * <ul>
	 * <li>{@link DataConfig#INTERNAL_UNIT_FIELD}, for example, the internal table &quot;Item&quot; in Hudup database.</li>
	 * <li>{@link DataConfig#INTERNAL_ATTRIBUTE_NAME_FIELD}, for example, the field &quot;item price&quot; in Hudup database.</li>
	 * <li>{@link DataConfig#INTERNAL_ATTRIBUTE_VALUE_FIELD}, for example, the value field &quot;12 USD&quot; in Hudup database.</li>
	 * <li>{@link DataConfig#EXTERNAL_UNIT_FIELD}, for example, the external table &quot;Product&quot; in outside database.</li>
	 * <li>{@link DataConfig#EXTERNAL_ATTRIBUTE_NAME_FIELD}, for example, the field &quot;product price&quot; in outside database.</li>
	 * <li>{@link DataConfig#EXTERNAL_ATTRIBUTE_VALUE_FIELD}, for example, the value field &quot;10 Euro&quot; in outside database.</li>
	 * </ul>
	 * @return Default attribute list of attribute map. Such attribute list forms a structure of a attribute map storage such as a table of attribute map in database.
	 */
	public static AttributeList defaultAttributeMapAttributeList() {
		AttributeList attributes = new AttributeList();
		//
		Attribute internalUnit = new Attribute(DataConfig.INTERNAL_UNIT_FIELD, Type.string);
		internalUnit.setKey(true);
		attributes.add(internalUnit);
		//
		Attribute internalAttributeName = new Attribute(DataConfig.INTERNAL_ATTRIBUTE_NAME_FIELD, Type.string);
		internalAttributeName.setKey(true);
		attributes.add(internalAttributeName);
		//
		Attribute internalAttributeValue = new Attribute(DataConfig.INTERNAL_ATTRIBUTE_VALUE_FIELD, Type.string);
		internalAttributeValue.setKey(true);
		attributes.add(internalAttributeValue);
		//
		Attribute externalUnit = new Attribute(DataConfig.EXTERNAL_UNIT_FIELD, Type.string);
		attributes.add(externalUnit);
		//
		Attribute externalAttributeName = new Attribute(DataConfig.EXTERNAL_ATTRIBUTE_NAME_FIELD, Type.string);
		attributes.add(externalAttributeName);
		//
		Attribute externalAttributeValue = new Attribute(DataConfig.EXTERNAL_ATTRIBUTE_VALUE_FIELD, Type.string);
		attributes.add(externalAttributeValue);
		
		return attributes;
	}

	
	/**
	 * Creating the default attribute list of context template.
	 * {@code ContextTemplate} is template for a context represented by the class {@code Context}.
	 * {@code Context} includes time, location, companion, etc. associated with a user when she/he is buying, studying, etc.
	 * Such attribute list has four attributes whose names are specified by constants as follows:
	 * <ul>
	 * <li>{@link DataConfig#CTX_TEMPLATEID_FIELD}</li>
	 * <li>{@link DataConfig#CTX_NAME_FIELD}</li>
	 * <li>{@link DataConfig#CTX_TYPE_FIELD}</li>
	 * <li>{@link DataConfig#CTX_PARENT_FIELD}</li>
	 * </ul>
	 * @return Default attribute list of context template. Such attribute list forms a structure of a context template storage such as a table of context template in database.
	 */
	public static AttributeList defaultContextTemplateAttributeList() {
		AttributeList attributes = new AttributeList();
		
		Attribute ctxTemplateId = new Attribute(DataConfig.CTX_TEMPLATEID_FIELD, Type.integer);
		ctxTemplateId.setKey(true);
		attributes.add(ctxTemplateId);
		
		Attribute ctxName = new Attribute(DataConfig.CTX_NAME_FIELD, Type.string);
		attributes.add(ctxName);

		Attribute ctxType = new Attribute(DataConfig.CTX_TYPE_FIELD, Type.integer);
		attributes.add(ctxType);

		Attribute ctxParent = new Attribute(DataConfig.CTX_PARENT_FIELD, Type.integer);
		attributes.add(ctxParent);
		
		return attributes;
	}

	
	/**
	 * Creating the default attribute list of context.
	 * Such attribute list has four attributes whose names are specified by constants as follows:
	 * <ul>
	 * <li>{@link DataConfig#USERID_FIELD}</li>
	 * <li>{@link DataConfig#ITEMID_FIELD}</li>
	 * <li>{@link DataConfig#CTX_TEMPLATEID_FIELD}</li>
	 * <li>{@link DataConfig#CTX_VALUE_FIELD}</li>
	 * </ul>
	 * @return Default attribute list of context. Such attribute list forms a structure of a context storage such as a table of context in database.
	 */
	public static AttributeList defaultContextAttributeList() {
		AttributeList attributes = new AttributeList();
		//
		Attribute ctxUserId = new Attribute(DataConfig.USERID_FIELD, Type.integer);
		ctxUserId.setKey(true);
		attributes.add(ctxUserId);
		//
		Attribute ctxItemId = new Attribute(DataConfig.ITEMID_FIELD, Type.integer);
		ctxItemId.setKey(true);
		attributes.add(ctxItemId);
		//
		Attribute ctxTemplateId = new Attribute(DataConfig.CTX_TEMPLATEID_FIELD, Type.integer);
		ctxTemplateId.setKey(true);
		attributes.add(ctxTemplateId);
		//
		Attribute ctxValue = new Attribute(DataConfig.CTX_VALUE_FIELD, Type.integer);
		attributes.add(ctxValue);
		
		return attributes;
	}

	
	/**
	 * Testing whether this attribute list is identical to specified attribute list.
	 * @param list Specified attribute list.
	 * @return Whether this attribute list is identical to specified attribute list.
	 */
	public boolean identity(AttributeList list) {
		// TODO Auto-generated method stub
		if (list == null || list.size() != size())
			return false;
		
		for (int i = 0; i < size(); i++) {
			if (!get(i).identity(list.get(i)))
				return false;
		}
		
		return true;
	}
	

}