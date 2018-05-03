package net.hudup.core.evaluate;

import java.util.List;

import net.hudup.core.Util;


/**
 * This abstract class represents an array of real numbers as a {@link MetricValue}.
 * Note, {@link MetricValue} interface represents value of a metric, which can be anything.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class ArrayMetricValue implements MetricValue {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * An array of real numbers as a metric value.
	 */
	protected List<Float> array = Util.newList();
	
	
	/**
	 * Default constructor.
	 */
	public ArrayMetricValue() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with list of real numbers.
	 * @param array specified list of real numbers.
	 */
	public ArrayMetricValue(List<Float> array) {
		super();
		// TODO Auto-generated constructor stub
		
		this.array.addAll(array);
	}

	
	/**
	 * Constructor with array of real numbers.
	 * @param array specified array of real numbers.
	 */
	public ArrayMetricValue(Float... array) {
		super();
		
		for (float value : array)
			this.array.add(value);
	}
	
	
	@Override
	public boolean isUsed() {
		// TODO Auto-generated method stub
		return array.size() > 0;
	}

	
	@Override
	public void accum(MetricValue metricValue) throws Exception {
		// TODO Auto-generated method stub
		if (metricValue == null || !metricValue.isUsed())
			return;
		
		accum(metricValue.value());
	}


	/**
	 * Taking the accumulative operator by adding the specified real number into the internal array of real number {@link #array}.
	 * @param value specified real number.
	 */
	public void accum(float value) {
		if (Util.isUsed(value))
			array.add(value);
	}
	
	
	/**
	 * Getting the value of the internal array of real number {@link #array} at specified index.
	 * @param index specified index.
	 * @return value of the internal array of real number {@link #array} at specified index.
	 */
	public float getElement(int index) {
		return array.get(index);
	}
	
	
	/**
	 * Getting the element count of the internal array of real number {@link #array}.
	 * @return element count of the internal array of real number {@link #array}.
	 */
	public int getElementCount() {
		return array.size();
	}
	
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		array.clear();
	}

	
	@Override
	public abstract Object clone();
	
	
}
