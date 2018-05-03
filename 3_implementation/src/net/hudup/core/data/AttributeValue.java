package net.hudup.core.data;

import java.io.Serializable;
import java.util.List;

import net.hudup.core.Cloneable;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;


/**
 * This class represents the pair of attribute and value.
 * The internal variable {@link #attribute} indicates the attribute and the interval variable {@link #value} indicating the value of attribute.
 * Such value is an object, which means that the value can be anything.
 * They are public variables for fast access.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class AttributeValue implements Serializable, TextParsable, Cloneable {
	
	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * The public attribute.
	 */
	public Attribute attribute = null;
	
	
	/**
	 * The public value of attribute {@link #attribute}.
	 */
	public Object value = null;
	
	
	/**
	 * Constructor with specified attribute and value.
	 * @param attribute specified attribute.
	 * @param value specified value.
	 */
	public AttributeValue(Attribute attribute, Object value) {
		this.attribute = attribute;
		this.value = value;
		
		if (!(value instanceof Serializable))
			throw new RuntimeException("Not serialozable object");
	}


	@Override
	public String toText() {
		// TODO Auto-generated method stub
		return attribute.toText() + "=" + value.toString();
	}

	
	/**
	 * Converting this pair of attribute and value as condition text.
	 * For example, if the attribute has name &quot;itemID&quot; and value is &quot;1&quot; then
	 * the condition text is &quot;itemID=&#39;1&#39;&quot;
	 * @return condition text of this pair of attribute and value.
	 */
	public String toConditionText() {
		String condition = attribute.getName() + "=";
		if (attribute.isShownAsNumber())
			condition += value.toString();
		else
			condition += "'" + value.toString() + "'";
		
		return condition;
	}
	
	
	@Override
	public String toString() {
		return toText();
	}


	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		clear();
		
		List<String> list = TextParserUtil.parseTextList(spec, "=");
		if (list.size() < 2)
			return;
		
		attribute = new Attribute();
		attribute.parseText(list.get(0));
		
		value = list.get(1);
	}
	
	
	@Override
	public Object clone() {
		return new AttributeValue((Attribute)attribute.clone(), value.toString());
	}
	
	
	/**
	 * Clearing this {@link AttributeValue} in which both attribute and value are set to be {@code null}.
	 */
	private void clear() {
		attribute = null;
		value = null;
	}

	
}
