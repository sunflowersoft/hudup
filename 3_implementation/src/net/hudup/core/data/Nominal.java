package net.hudup.core.data;

import java.io.Serializable;

import net.hudup.core.Cloneable;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;


/**
 * This class represent nominal data type called {@code nominal} in brief.
 * Nominal indicates discrete and non-number data such as weekdays {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday}.
 * A nominal is always in a list of many nominal (s) called nominal list represented by {@code NominalList}.
 * Nominal list can have hierarchical structure, for example, in the nominal list {fruit, table, apple, orange}, nominal (s) &quot;apple&quot; and &quot;orange&quot; are children of parent nominal &quot;fruit&quot;. 
 * Each nominal has three important properties as follows:
 * <ul>
 * <li>A string represents the value of nominal, stored in the variable {@link #value}.</li>
 * <li>The index of this nominal in nominal list, stored in the variable {@link #index}.</li>
 * <li>In case that nominal list has hierarchical structure, if this nominal has parent nominal, the index of its parent nominal is specified by {@link #parentIndex}</li>
 * </ul>
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Nominal implements Serializable, TextParsable, Cloneable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constant specifying null index. It is -1.
	 */
	public final static int NULL_INDEX = -1;

	
	/**
	 * A string represents the value of nominal.
	 */
	protected String value = "";
	
	
	/**
	 * A nominal is always in a list of many nominal (s) called nominal list represented by {@code NominalList}.
	 * This variable stores the index of this nominal in nominal list.
	 */
	protected int index = 0;
	
	
	/**
	 * In case that nominal list has hierarchical structure, if this nominal has parent nominal, this variable indicates the index of its parent nominal in nominal list.
	 * Otherwise, when nominal list has no hierarchical structure or this nominal has no parent nominal, this variable is set to be {@link #NULL_INDEX}.
	 */
	protected int parentIndex = NULL_INDEX;


	/**
	 * Default constructor.
	 */
	public Nominal() {
		value = "";
		index = 0;
		parentIndex = NULL_INDEX;
	}
	
	
	/**
	 * Constructor with nominal value.
	 * @param value Specified value.
	 */
	public Nominal(String value) {
		this.value = new String(value);
		index = 0;
		parentIndex = NULL_INDEX;
	}
	
	
	/**
	 * Constructor with value, index, and parent index.
	 * @param value Specified value.
	 * @param index Specified index.
	 * @param parentIndex Specified parent index.
	 */
	public Nominal(String value, int index, int parentIndex) {
		this.value = new String(value);
		this.index = index;
		this.parentIndex = parentIndex;
	}
	
	
	/**
	 * Getting the nominal value.
	 * @return value of nominal
	 */
	public String getValue() {
		return value;
	}
	
	
	/**
	 * Setting nominal value.
	 * @param value Specified nominal.
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	
	/**
	 * A nominal is always in a list of many nominal (s) called nominal list.
	 * This method returns the index of this nominal in nominal list.
	 * @return index of this nominal in nominal list.
	 */
	public int getIndex() {
		return index;
	}
	
	
	/**
	 * A nominal is always in a list of many nominal (s) called nominal list.
	 * This method sets the index of this nominal in nominal list by specified index.
	 * @param index Specified index.
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	
	/**
	 * In case that nominal list has hierarchical structure, this nominal can have parent nominal.
	 * This method returns the index of its parent nominal in nominal list.
	 * @return Parent index of this nominal. If nominal list has no hierarchical structure or this nominal has no parent nominal, this method returns {@link #NULL_INDEX}.
	 */
	public int getParentIndex() {
		return parentIndex;
	}
	
	
	/**
	 * Checking whether parent index is null ({@link #NULL_INDEX}).
	 * This method returns {@code true} if this nominal has parent nominal in case that nominal list has hierarchical structure.
	 * @return whether parent index is null
	 */
	public boolean isParentIndexNull() {
		return parentIndex == NULL_INDEX;
	}
	
	
	/**
	 * In case that nominal list has hierarchical structure, this nominal can have parent nominal.
	 * This method sets the index of its parent nominal in nominal list by specified parent index.
	 * @param parentIndex Specified parent index.
	 */
	public void setParentIndex(int parentIndex) {
		this.parentIndex = parentIndex;
	}
	
	
	/**
	 * This method removes the index of parent nominal from this nominal, which means that this nominal has no longer parent nominal after this method was called.
	 */
	public void setNullParentIndex() {
		this.parentIndex = NULL_INDEX;
	}

	
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		Nominal nominal = (Nominal)obj;
		return this.value.equals(nominal.value) && 
				this.index == nominal.index &&
				this.parentIndex == nominal.parentIndex;
	}


	@Override
	public String toText() {
		// TODO Auto-generated method stub
		return TextParserUtil.encryptReservedChars(value) + TextParserUtil.CONNECT_SEP + index + TextParserUtil.CONNECT_SEP + parentIndex;
	}


	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		
		clear();
		int idx = spec.lastIndexOf(TextParserUtil.CONNECT_SEP);
		if (idx == -1 || idx == spec.length() - 1)
			return;
		
		parentIndex = Integer.parseInt(spec.substring(idx + 1));
		
		spec = spec.substring(0, idx);
		idx = spec.lastIndexOf(TextParserUtil.CONNECT_SEP);
		index = Integer.parseInt(spec.substring(idx + 1));
		
		value = TextParserUtil.decryptReservedChars(spec.substring(0, idx));
	}
	
	
	@Override
	public Object clone() {
		return new Nominal(value, index, parentIndex);
	}
	
	
	/**
	 * Clearing this nominal, which means that all internal variables are set to be default values.
	 */
	private void clear() {
		value = "";
		index = 0;
		parentIndex = NULL_INDEX;
	}
	
	
	@Override
	public String toString() {
		return toText();
	}
	
	
}
