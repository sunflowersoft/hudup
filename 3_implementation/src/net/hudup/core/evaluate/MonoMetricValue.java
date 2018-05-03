package net.hudup.core.evaluate;

import net.hudup.core.Constants;
import net.hudup.core.Util;


/**
 * This abstract class represents a single number as a {@link MetricValue}.
 * Note, {@link MetricValue} interface represents value of a metric, which can be anything.
 * Therefore, this class is the simplest implementation of {@link MetricValue}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class MonoMetricValue implements MetricValue {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Single number as metric value.
	 */
	protected float value = Constants.UNUSED;
	
	
	/**
	 * Constructor with single number.
	 * @param value specified single number.
	 */
	public MonoMetricValue(float value) {
		this.value = value;
	}
	
	
	@Override
	public float value() {
		// TODO Auto-generated method stub
		return value;
	}

	
	@Override
	public void accum(MetricValue metricValue)  throws Exception {
		// TODO Auto-generated method stub
		if (!metricValue.isUsed())
			return;
		
		if (!isUsed())
			value = metricValue.value();
		else
			value += metricValue.value();
	}


	@Override
	public boolean isUsed() {
		// TODO Auto-generated method stub
		return Util.isUsed(value);
	}


	@Override
	public void reset() {
		// TODO Auto-generated method stub
		value = Constants.UNUSED;
	}


	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return new MonoMetricValue(value);
	}

	
}
