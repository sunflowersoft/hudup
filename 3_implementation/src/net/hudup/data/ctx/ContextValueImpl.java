package net.hudup.data.ctx;

import java.io.Serializable;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.ctx.ContextValue;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ContextValueImpl implements ContextValue {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	private Serializable value = null;
			
	
	/**
	 * 
	 */
	private float quantizedValue = Constants.UNUSED;
	
	
	/**
	 * 
	 * @param value
	 */
	private ContextValueImpl(Serializable value, float quantizedValue) {
		this.value = value;
		this.quantizedValue = quantizedValue;
	}
	
	
	@Override
	public Serializable getValue() {
		// TODO Auto-generated method stub
		return value;
	}


	@Override
	public boolean isQuantized() {
		// TODO Auto-generated method stub
		return Util.isUsed(quantizedValue);
	}


	@Override
	public float getQuantizedValue() {
		// TODO Auto-generated method stub
		return quantizedValue;
	}


	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return new ContextValueImpl( (Serializable) Util.clone(value), quantizedValue);
	}


	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ContextValue))
			return false;
		
		ContextValue that = (ContextValue)obj;
		if (this.isQuantized()) {
			if (that.isQuantized())
				return this.getValue().equals(that.getValue()) && 
						this.getQuantizedValue() == that.getQuantizedValue();
			else
				return false;
		}
		else {
			if (that.isQuantized())
				return false;
			else
				return this.getValue().equals(that.getValue());
		}
		
	}
	
	
	/**
	 * 
	 * @param attribute
	 * @param value
	 * @return {@link ContextValue}
	 */
	public static ContextValue create(Attribute attribute, Serializable value) {
		
		Serializable newValue = null;
		try {
			newValue = (Serializable) Profile.createValue(attribute, value);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			newValue = null;
		}
		if (newValue == null)
			return null;
		
		final float quantizedValue = attribute.isShownAsNumber() ? 
				Profile.getValueAsReal(attribute, newValue) : Constants.UNUSED;

				
		return new ContextValueImpl(newValue, quantizedValue);
		
	}
	
	
	/**
	 * 
	 * @param type
	 * @param value
	 * @return {@link ContextValue}
	 */
	public static ContextValue create(Type type, Serializable value) {
		Attribute attribute = new Attribute(DataConfig.CTX_VALUE_FIELD, type);
		return create(attribute, value);
	}
	
	
	/**
	 * 
	 * @param value
	 * @return {@link ContextValue}
	 */
	public static ContextValue create(Serializable value) {
		if (value == null)
			return null;
		
		Type type = Attribute.fromObject(value);
		return create(type, value);
	}
	
	
	
}
