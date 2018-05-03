/**
 * 
 */
package net.hudup.core.data;

import java.io.Serializable;

import net.hudup.core.Cloneable;
import net.hudup.core.parser.TextParsable;


/**
 * This class is a wrapper of boolean value and so its main internal variable {@link #value} is a boolean value.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class BooleanWrapper implements TextParsable, Serializable, Cloneable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * The main internal variable of this wrapper is a boolean value.
	 */
	private boolean value = false;
	
	
	/**
	 * Default constructor with {@code false} value.
	 */
	public BooleanWrapper() {
		this(false);
	}

	
	/**
	 * Constructor with specified boolean value.
	 * @param value specified boolean value.
	 */
	public BooleanWrapper(boolean value) {
		this.value = value;
	}
	
	
	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		value = Boolean.parseBoolean(spec);
	}

	
	@Override
	public String toText() {
		return Boolean.toString(value);
	}
	
	
	/**
	 * Getting the internal boolean value.
	 * @return internal boolean value.
	 */
	public boolean get() {
		return value;
	}
	
	
	@Override
	public String toString() {
		return toText();
	}
	
	
	/**
	 * Setting the internal boolean value by specified value.
	 * @param value specified boolean value.
	 */
	public void set(boolean value) {
		this.value = value;
	}


	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return new BooleanWrapper(value);
	}
	
	
}
