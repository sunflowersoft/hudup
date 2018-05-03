/**
 * 
 */
package net.hudup.core.logistic;

/**
 * This utility class contains a minimum value and maximum value. It inherits from the {@link Dual} class.
 * The minimum value and the maximum value are represented by the variable {@link Dual#one} and the variable {@link Dual#other} of its parent class {@link Dual}, respectively.
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class MinMax extends Dual {

	
	/**
	 * Constructor with specified minimum value and maximum value.
	 * @param min specified minimum value.
	 * @param max specified maximum value.
	 */
	public MinMax(float min, float max) {
		super(min, max);
	}
	
	
	/**
	 * Getting the minimum value.
	 * @return minimum value.
	 */
	public float min() {
		return one();
	}


	/**
	 * Getting the maximum value.
	 * @return maximum value.
	 */
	public float max() {
		return other();
	}
	
	
	/**
	 * Setting the minimum value.
	 * @param min minimum value.
	 */
	public void setMin(float min) {
		this.one = min;
	}
	
	
	/**
	 * Getting the maximum value.
	 * @param max maximum value.
	 */
	public void setMax(float max) {
		this.other = max;
	}
	
	
}
