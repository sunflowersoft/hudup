/**
 * 
 */
package net.hudup.core.logistic;

import net.hudup.core.Constants;


/**
 * This utility class represents a pair of values represented by its two variables {@link #one} and {@link #other}.
 * Such two variables are dual. 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Dual {

	/**
	 * This variable represents the first value. Note that two variables {@link #one} and {@link #other} are dual.
	 */
	protected float one = Constants.UNUSED;
	
	
	/**
	 * This variable represents the second value. Note that two variables {@link #one} and {@link #other} are dual.
	 */
	protected float other = Constants.UNUSED;
	
	
	/**
	 * Constructor with the first value and the second value.
	 * @param one The first value.
	 * @param other The second value.
	 */
	public Dual(float one, float other) {
		this.one = one;
		this.other = other;
	}
	
	
	/**
	 * Getting the first value.
	 * @return the first value.
	 */
	public float one() {
		return one;
	}
	
	
	/**
	 * Getting the second value.
	 * @return the second value
	 */
	public float other() {
		return other;
	}
	
	
	/**
	 * Setting the first value.
	 * @param one The first value.
	 */
	public void setOne(float one) {
		this.one = one;
	}
	

	/**
	 * Setting the second value.
	 * @param other The second value.
	 */
	public void setOther(float other) {
		this.other = other;
	}
	
	
}
