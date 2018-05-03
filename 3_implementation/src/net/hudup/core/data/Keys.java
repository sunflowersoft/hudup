package net.hudup.core.data;

import java.io.Serializable;
import java.util.List;

import net.hudup.core.Cloneable;
import net.hudup.core.Util;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;


/**
 * This class represents a list of key attributes. This class has a built-in variable {@link #list} containing all key attributes.
 * Recall that attribute represented by {@link Attribute} class indicates the data type, which is also a wrapper of data type.
 * {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
 * Profile uses attributes to specify its data types.
 * In a profile, a key attribute or a set of some key attributes are used to distinguish two profiles.
 * If there is only one key attribute, we state that the profile has single key. A integer single key is also called identification (ID).
 * If there are a set of key attributes, we state that the profile has complex key.
 * In general, single key and complex key are represented by this {@link Keys} class.
 * As a convention, we call this class as {@code key list}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Keys implements Cloneable, TextParsable, Serializable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * The built-in variable contains all key attributes.
	 */
	protected List<Attribute> list = Util.newList();
	
	
	/**
	 * Default constructor.
	 */
	public Keys() {
		super();
		// TODO Auto-generated constructor stub
		
		if (!(list instanceof Serializable))
			throw new RuntimeException("Keys isn't serializable class");
	}


	/**
	 * Getting the size of this key list.
	 * @return size of this key list.
	 */
	public int size() {
		return list.size();
	}
	
	
	/**
	 * Getting a key attribute at specified index.
	 * 
	 * @param idx Specified index.
	 * @return The key attribute at specified index.
	 */
	public Attribute get(int idx) {
		return list.get(idx);
	}
	
	
	/**
	 * Testing whether or not the specified key attribute is contained in this key list.
	 * @param key Specified key attribute.
	 * @return Whether or not the specified key attribute is contained in this key list.
	 */
	public boolean contains(Attribute key) {
		return list.contains(key);
	}
	
	
	/**
	 * Clearing this key list, which means that all key attributes are removed from this key list.
	 */
	public void clear() {
		list.clear();
	}
	
	
	/**
	 * Getting the internal list of key attributes.
	 * @return The internal {@link List} of key attributes.
	 */
	public List<Attribute> getList() {
		return list;
	}
	
	
	/**
	 * Getting the list of indices of key attributes in this key list.
	 * 
	 * @return The {@link List} of indices of key attributes in this key list.
	 */
	public List<Integer> getIndexes() {
		List<Integer> indexes = Util.newList();
		for (Attribute att : list) {
			indexes.add(att.index);
		}
		return indexes;
	} 
	
	
	
	@Override
	public Object clone() {
		Keys newKey = new Keys();
		
		for (int i = 0; i < this.size(); i++) {
			Attribute att = (Attribute) this.get(i).clone();
			newKey.list.add(att);
		}
		
		return newKey;
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

	
}
