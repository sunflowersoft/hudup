package net.hudup.evaluate;

import net.hudup.core.alg.Alg;
import net.hudup.core.evaluate.MetricValue;
import net.hudup.core.evaluate.MonoMetricValue;



/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class F1 extends MetaMetric {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public F1() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * 
	 * @param precision
	 * @param recall
	 */
	public void setup(Precision precision, Recall recall) {
		// TODO Auto-generated method stub
		super.setup(new Object[] { precision, recall });
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "F1";
	}

	
	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Classification accuracy";
	}

	
	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return "F1";
	}

	
	@Override
	public boolean recalc(Object... params) throws Exception {
		// TODO Auto-generated method stub
		
		if ( meta == null || meta.length != 2 || 
				(!(meta[0] instanceof Precision)) || 
				(!(meta[1] instanceof Recall)) )
			return false;
		
		Precision p = (Precision) meta[0];
		Recall r = (Recall) meta[1];
		
		MetricValue pCurrentValue = p.getCurrentValue();
		MetricValue rCurrentValue = r.getCurrentValue();
		if (pCurrentValue != null && pCurrentValue.isUsed() && 
				rCurrentValue != null && rCurrentValue.isUsed()) {
			
			this.currentValue = new MonoMetricValue(
					(2 * pCurrentValue.value() * rCurrentValue.value()) / 
					(pCurrentValue.value() + rCurrentValue.value()));
		}
		else
			this.currentValue = null;
		
		MetricValue pAccumValue = p.getAccumValue();
		MetricValue rAccumValue = r.getAccumValue();
		if (pAccumValue != null && pAccumValue.isUsed() && 
				rAccumValue != null && rAccumValue.isUsed()) {
			
			this.accumValue = new MonoMetricValue(
					(2 * pAccumValue.value() * rAccumValue.value()) / 
					(pAccumValue.value() + rAccumValue.value()));
		}
		else
			this.accumValue = null;
		
		return this.currentValue != null && this.currentValue.isUsed() &&
				this.accumValue != null && this.accumValue.isUsed();

	}


	@Override
	protected boolean recalc0(MetricValue metricValue) throws Exception {
		// TODO Auto-generated method stub
		throw new Exception("Not implement this method");
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new F1();
	}











	
}
