package net.hudup.evaluate;

import java.util.List;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.RatingVector;
import net.hudup.core.evaluate.CorrelationAccuracy;
import net.hudup.core.evaluate.MeanMetricValue;
import net.hudup.core.evaluate.MetricValue;
import net.hudup.core.logistic.Vector;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Pearson extends CorrelationAccuracy {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */
	public Pearson() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Pearson correlation";
	}

	
	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Correlation accuracy";
	}


	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return "Pearson correlation";
	}

	
	@Override
	protected MetricValue calc(RatingVector recommended, RatingVector vTesting, Dataset testing) {
		// TODO Auto-generated method stub
		
		if (vTesting == null)
			return null;
		
		List<Float> list1 = Util.newList();
		List<Float> list2 = Util.newList();
		Set<Integer> fieldIds = recommended.fieldIds(true);
		for (int fieldId : fieldIds) {
			if (!vTesting.isRated(fieldId))
				continue;
			
			float rating1 = vTesting.get(fieldId).value;
			list1.add(rating1);
			float rating2 = recommended.get(fieldId).value;
			list2.add(rating2);
		}
		if (list1.size() == 0)
			return new MeanMetricValue(0);
		
		Vector v1 = new Vector(list1);
		Vector v2 = new Vector(list2);
		float corr = v1.corr(v2);
		
		if (Util.isUsed(corr))
			return new MeanMetricValue(corr);
		else
			return null;
		
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new Pearson();
	}







	
	
	
}
