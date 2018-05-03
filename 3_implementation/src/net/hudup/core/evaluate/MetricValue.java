package net.hudup.core.evaluate;

import java.io.Serializable;

import net.hudup.core.Cloneable;


/**
 * This interface represents value of a metric, called {@code metric value}. It can be anything but in current implementation, it only reflects real number (float number) via the method {@link #value()}.
 *  
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface MetricValue extends Serializable, Cloneable {

	
	/**
	 * Getting real value of {@code MetricValue}. Such real value is often mean number (average number).
	 * @return real value of {@code MetricValue}.
	 */
	float value();
	
	
	/**
	 * Testing whether or not this {@code metric value} is used.
	 * @return whether or not this {@code metric value} is used.
	 */
	boolean isUsed();
	
	
	/**
	 * Taking the accumulative operator. For example, the accumulative addition operator will add this {@code MetricValue} to the specified {@code MetricValue} and then,
	 * the result is set back to this {@code MetricValue}. 
	 * @param metricValue specified {@code MetricValue}.
	 * @throws Exception if any error raises.
	 */
	void accum(MetricValue metricValue) throws Exception;
	
	
	/**
	 * Resetting this {@code metric value}. For example, its real number returned by ({@link #value()}) is re-set to be 0.
	 */
	void reset();
	
	
}
