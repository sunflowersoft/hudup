package net.hudup.core.data.ctx;

import java.io.Serializable;

import net.hudup.core.Cloneable;

/**
 * This class represents a value of context template. It has two kinds of value: quantized value (real number) and normal value (object).
 * As a convention, this class is called context value. In general, a context represented by {@link Context} is composed of context template represented by {@link ContextTemplate} and context value represented by {@link ContextValue}.
 * In general, context value is anything.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public interface ContextValue extends Serializable, Cloneable {

	
	/**
	 * There are two kinds of context value such as quantized value (real number) and normal value (object).
	 * This methods indicates whether this context value is quantized value.
	 * @return whether this value is quantized.
	 */
	boolean isQuantized();
	
	
	/**
	 * There are two kinds of context value such as quantized value (real number) and normal value (object).
	 * This methods returns the quantized value (real number). If this context value is not quantized value, this method returns not-a-number specified by {@code Constants.UNUSED}.
	 * @return quantized value (real number) of this context value.
	 */
	float getQuantizedValue();

	
	/**
	 * There are two kinds of context value such as quantized value (real number) and normal value (object).
	 * This methods returns the normal value (object). If this context value is quantized value, this methods returns {@link Float} object which is the wrapper of quantized value.  
	 * @return Normal value of this context value.
	 */
	Serializable getValue();
	
	
	/**
	 * Comparing this context value to specified context value.
	 * @param obj Specified context value.
	 * @return whether this context value is equals to specified context value.
	 */
	boolean equals(Object obj);
}