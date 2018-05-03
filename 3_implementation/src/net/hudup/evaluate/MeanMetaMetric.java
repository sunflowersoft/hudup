package net.hudup.evaluate;

import net.hudup.core.alg.Alg;
import net.hudup.core.evaluate.MeanMetricValue;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.evaluate.MetricValue;
import net.hudup.core.logistic.BaseClass;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@BaseClass
public class MeanMetaMetric extends MetaMetric {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * 
	 */
	public MeanMetaMetric() {
		super();
	}
	
	
	/**
	 * 
	 * @param point
	 */
	public void setup(Metric point) {
		// TODO Auto-generated method stub
		super.setup(new Object[] { point });
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return meta[0].getName();
	}

	
	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return meta[0].getDesc();
	}

	
	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return meta[0].getTypeName();
	}

	
	@Override
	public boolean recalc(Object... params) throws Exception {
		// TODO Auto-generated method stub
		if ( params == null || params.length != 1 || !(params[0] instanceof Metric) )
			return false;
		
		Metric metric = (Metric) params[0];
		if ( !metric.getName().equals(meta[0].getName()) || !metric.isValid() )
			return false;
		
		MetricValue metricValue = metric.getAccumValue();
		if (!metricValue.isUsed())
			return false;
		
		MeanMetricValue mean = new MeanMetricValue(metricValue.value());
		return recalc0(mean);
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new MeanMetaMetric();
	}

	

	

}
