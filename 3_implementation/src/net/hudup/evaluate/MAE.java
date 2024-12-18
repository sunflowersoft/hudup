package net.hudup.evaluate;

import java.util.Set;

import net.hudup.core.alg.Alg;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.RatingVector;
import net.hudup.core.evaluate.FractionMetricValue;
import net.hudup.core.evaluate.MetricValue;
import net.hudup.core.evaluate.PredictiveAccuracy;

/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class MAE extends PredictiveAccuracy {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */
	public MAE() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "MAE";
	}

	
	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Predictive accuracy";
	}
	
	
	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return "Mean Absolute Error";
	}

	
	@Override
	protected MetricValue calc(RatingVector recommended, RatingVector vTesting, Dataset testing) {
		// TODO Auto-generated method stub
		if (vTesting == null)
			return null;

		float      mae = 0;
		int        n = 0;
		Set<Integer> fieldIds = recommended.fieldIds(true);
		
		for (int fieldId : fieldIds) {
			if (!vTesting.isRated(fieldId))
				continue;
			
			float dev = Math.abs(recommended.get(fieldId).value - vTesting.get(fieldId).value);
			mae += dev;
			n++;
		}
		
		if (n > 0)
			return new FractionMetricValue(mae, n);
		else
			return null;
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new MAE();
	}



}
