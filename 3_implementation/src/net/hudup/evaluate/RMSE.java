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
public class RMSE extends MetaMetric {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public RMSE() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * 
	 * @param mse
	 */
	public void setup(MSE mse) {
		super.setup( new Object[] { mse } );
	}
	
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "RMSE";
	}

	
	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return "Root Mean Squared Error";
	}

	
	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Predictive accuracy";
	}

	
	@Override
	public boolean recalc(Object... params) throws Exception {
		// TODO Auto-generated method stub
		if ( meta == null || meta.length != 1 || 
				(!(meta[0] instanceof MSE)) )
			throw new Exception("Invalid parameter");
		
		MSE mse = (MSE)meta[0];
		
		MetricValue currentValue = mse.getCurrentValue();
		if (currentValue != null && currentValue.isUsed())
			this.currentValue = new MonoMetricValue( (float) Math.sqrt(currentValue.value()));
		else
			this.currentValue = null;
		
		MetricValue accumValue = mse.getAccumValue();
		if (accumValue != null && accumValue.isUsed())
			this.accumValue = new MonoMetricValue( (float) Math.sqrt(accumValue.value()));
		else
			this.accumValue = null;
		
		return this.currentValue != null && this.currentValue.isUsed() &&
				this.accumValue != null && this.accumValue.isUsed();
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new RMSE();
	}



	
}
