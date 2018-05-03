package net.hudup.core.evaluate;

import net.hudup.core.Util;


/**
 * This abstract class represents a average number called mean in form (total sum / count) as a {@link MetricValue}.
 * The total number (total sum) is referred by the internal variable {@link #value}.
 * The count is referred by the internal variable {@link #count}.
 * Note, {@link MetricValue} interface represents value of a metric, which can be anything.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class MeanMetricValue implements MetricValue {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * The total sum for calculating the mean.
	 */
	protected float value = 0;
	
	
	/**
	 * The count for calculating the mean.
	 */
	protected int count = 0;
	
	
	/**
	 * Constructor with specified total sum (value). The count is automatically set to be 1.
	 * @param value specified total sum (value).
	 */
	public MeanMetricValue(float value) {
		// TODO Auto-generated constructor stub
		this.value = value;
		this.count = 1;
	}


	@Override
	public float value() {
		// TODO Auto-generated method stub
		return value;
	}

	
	@Override
	public boolean isUsed() {
		// TODO Auto-generated method stub
		return Util.isUsed(value) && Util.isUsed(count) && count > 0;
	}

	
	@Override
	public void accum(MetricValue metricValue) throws Exception {
		// TODO Auto-generated method stub
		if (metricValue == null || !metricValue.isUsed() || 
				!(metricValue instanceof MeanMetricValue))
			return;
		
		MeanMetricValue mean = (MeanMetricValue)metricValue;
		if (!isUsed()) {
			this.value = mean.value;
			this.count = 1;
		}
		else {
			this.value = (this.value() * this.count) / (this.count + 1) + 
						   metricValue.value() / (this.count + 1); 
			this.count ++;
		}
	}


	@Override
	public void reset() {
		// TODO Auto-generated method stub
		value = 0;
		count = 0;
	}


	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		MeanMetricValue mean = new MeanMetricValue(value);
		mean.count = this.count;
		return mean;
	}

	
}
