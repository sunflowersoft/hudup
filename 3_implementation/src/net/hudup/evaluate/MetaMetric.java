package net.hudup.evaluate;

import java.util.Arrays;
import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.evaluate.DefaultMetric;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.logistic.BaseClass;


/**
 * Note, the abstract class of {@code Metric} interface is {code AbstractMetric}.
 * Three implemented classes of {@code Metric} which inherit from {@code AbstractMetric} are {@code DefaultMetric}, {@code MetaMetric}, and {@code MetricWrapper} as follows:
 * <ul>
 * <li>The {@code DefaultMetric} class is default partial implementation of single {@code Metric}.</li>
 * <li>This {@code MetaMetric} class is a complex {@code Metric} which contains other metrics.</li>
 * <li>In some situations, if metric requires complicated implementation, it is wrapped by {@code MetricWrapper} class.</li>
 * </ul>
 * As a convention, this {@code MetaMetric} is called meta metric. It contains an array of metrics referred by its internal variables {@link #meta}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@BaseClass
public abstract class MetaMetric extends DefaultMetric {
	
	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Array of internal metrics.
	 */
	protected Metric[] meta = null;
	
	
	/**
	 * Default constructor. This protected constructor implies that inherited class will define exactly the constructor.
	 */
	protected MetaMetric() {
		
	}
	
	
	@Override
	public void setup(Object... params) {
		List<Metric> list = Util.newList();
		for (Object param : params) {
			if (param != null && param instanceof Metric)
				list.add((Metric)param);
		}
		
		this.meta = list.toArray(new Metric[] { });
	}
	
	
	/**
	 * Getting the array of internal metrics.
	 * @return array of {@link Metric} (s).
	 */
	public Metric[] getMeta() {
		return meta;
	}


	/**
	 * Getting names of internal metrics.
	 * @return names of internal metrics.
	 */
	public String[] getMetaNameList() {
		String[] nameList = new String[meta.length];
		
		for (int i = 0; i < meta.length; i++) {
			nameList[i] = meta[i].getName();
		}
		
		return nameList;
	}
	
	
	/**
	 * Checking whether this meta metric contains (refer to) an internal metric having specified name.
	 * @param metricName specified name.
	 * @return whether this meta metric contains (refer to) an internal metric having specified name.
	 */
	public boolean referTo(String metricName) {
		return Arrays.asList(getMetaNameList()).contains(metricName);
	}
	
	
}
