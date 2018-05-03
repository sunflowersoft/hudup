package net.hudup.core.evaluate;

import net.hudup.core.Util;


/**
 * This abstract class represents fraction (a/b) as a {@link MetricValue}.
 * Note, {@link MetricValue} interface represents value of a metric, which can be anything.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class FractionMetricValue implements MetricValue {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Numerator of the fraction.
	 */
	private float a = 0;
	
	
	/**
	 * Denominator of the fraction.
	 */
	private float b = 0;
	
	
	/**
	 * Constructor with the numerator and denominator.
	 * @param a specified numerator.
	 * @param b specified denominator.
	 */
	public FractionMetricValue(float a, float b) {
		this.a = a;
		this.b = b;
	}
	
	
	/**
	 * Getting the numerator of this fraction.
	 * @return numerator of this fraction.
	 */
	public float a() {
		return a;
	}
	
	
	/**
	 * Getting the denominator of this fraction.
	 * @return denominator of this fraction.
	 */
	public float b() {
		return b;
	}
	
	
	@Override
	public float value() {
		return a / b;
	}
	
	
	@Override
	public void accum(MetricValue metricValue) throws Exception {
		// TODO Auto-generated method stub
		if (metricValue == null || !metricValue.isUsed() || 
				!(metricValue instanceof FractionMetricValue))
			return;
		
		
		FractionMetricValue fraction = (FractionMetricValue)metricValue;
		if (!isUsed()) {
			this.a = fraction.a();
			this.b = fraction.b();
		}
		else {
			this.a += fraction.a();
			this.b += fraction.b();
		}
	}


	@Override
	public boolean isUsed() {
		return Util.isUsed(a) && Util.isUsed(b) && b != 0 ;
	}


	@Override
	public void reset() {
		// TODO Auto-generated method stub
		a = 0;
		b = 0;
	}


	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return new FractionMetricValue(a, b);
	}
	
	
}
