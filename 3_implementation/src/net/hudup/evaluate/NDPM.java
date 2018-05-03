package net.hudup.evaluate;

import java.util.List;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.RatingVector;
import net.hudup.core.evaluate.CorrelationAccuracy;
import net.hudup.core.evaluate.FractionMetricValue;
import net.hudup.core.evaluate.MetricValue;
import net.hudup.core.logistic.NextUpdate;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
public class NDPM extends CorrelationAccuracy {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */
	public NDPM() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "NDPM";
	}

	
	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Correlation accuracy";
	}


	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return "Normalized distance-based performance";
	}

	
	@Override
	protected MetricValue calc(RatingVector recommended, RatingVector vTesting, Dataset testing) {
		// TODO Auto-generated method stub
		
		if (vTesting == null)
			return null;

		Set<Integer>  fieldIds = recommended.fieldIds(true);
		List<Integer> common = Util.newList();
		for (int fieldId : fieldIds) {
			if (vTesting.isRated(fieldId))
				common.add(fieldId);
		}
		if (common.size() < 2)
			return null;
		
		int Cminus = 0;
		int Cu = 0;
		int Ci = 0;
		for (int i = 0; i < common.size() - 1; i++) {
			int fieldId1 = common.get(i);
			float recommendedRating1 = recommended.get(fieldId1).value;
			float testingRating1 = vTesting.get(fieldId1).value;
			
			for (int j = i + 1; j < common.size(); j++) {
				int fieldId2 = common.get(j);
				float recommendedRating2 = recommended.get(fieldId2).value;
				float testingRating2 = vTesting.get(fieldId2).value;
				
				if ( 
						((recommendedRating1 >= recommendedRating2) && (testingRating1 < testingRating2)) ||
						((recommendedRating1 < recommendedRating2 ) && (testingRating1 >= testingRating2))
					) 
				{
					Cminus++;
				}
				
				if ( 
						(recommendedRating1 == recommendedRating2) && (testingRating1 != testingRating2)
					) 
				{
					Cu++;
				}
				
				if (testingRating1 != testingRating2)
					Ci++;
				
			} // for
			
			
			
		} // for
		
		
		if (Ci > 0)
			return new FractionMetricValue(2 * Cminus + Cu, 2 * Ci);
		else
			return null;
		
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new NDPM();
	}










	
	
}
