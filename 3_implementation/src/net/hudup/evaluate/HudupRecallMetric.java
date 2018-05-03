package net.hudup.evaluate;

import net.hudup.core.alg.Alg;
import net.hudup.core.evaluate.DefaultMetric;
import net.hudup.core.evaluate.FractionMetricValue;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class HudupRecallMetric extends DefaultMetric {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public HudupRecallMetric() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return "Hudup recall";
	}

	
	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Hudup";
	}

	
	@Override
	public boolean recalc(Object... params) throws Exception {
		// TODO Auto-generated method stub
		if (params == null || params.length != 1 || !(params[0] instanceof FractionMetricValue))
			return false;

		FractionMetricValue fraction = (FractionMetricValue)params[0];
		if (!fraction.isUsed())
			return false;
		
		return recalc0(fraction);
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Hudup recall";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new HudupRecallMetric();
	}



	
	
}
