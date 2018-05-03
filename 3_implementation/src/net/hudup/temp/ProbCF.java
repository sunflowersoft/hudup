package net.hudup.temp;

import java.util.Set;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.cf.MemoryBasedCF;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.RatingVector;
import net.hudup.data.DatasetUtil2;
import net.hudup.logistic.math.DatasetStatsProcessor;

/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
public class ProbCF extends MemoryBasedCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public ProbCF() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "probability";
	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) {
		// TODO Auto-generated method stub
		RatingVector result = param.ratingVector.newInstance(true);
		
		DatasetStatsProcessor statProcessor = new DatasetStatsProcessor(dataset);
		
		int minRating = (int)dataset.getConfig().getMinRating();
		int n = dataset.getConfig().getNumberRatingsPerItem();
		for (int queryId : queryIds) {
			int maxIdx = -1;
			float maxProb = -1;
			for (int i = 0; i < n; i++) {
				float rating = DatasetUtil2.realRatingValueOf(i, minRating);
				float prob = statProcessor.probItem(queryId, (float)rating);
				
				if (prob > maxProb) {
					maxProb = prob;
					maxIdx = i;
				}
			}
			
			if (maxProb > 0) {
				result.put(
						queryId, 
						DatasetUtil2.realRatingValueOf(maxIdx, minRating));
			}
		}
		
		if (result.size() == 0)
			return null;
		else
			return result;
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		return new DataConfig();
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new ProbCF();
	}

	
	
	
}
