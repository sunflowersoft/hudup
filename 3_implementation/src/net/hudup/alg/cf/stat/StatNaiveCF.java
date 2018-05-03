/**
 * 
 */
package net.hudup.alg.cf.stat;

import java.util.Collection;
import java.util.Set;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.data.Rating;
import net.hudup.core.data.RatingVector;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class StatNaiveCF extends StatCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public StatNaiveCF() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "stat_naive";
	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) {
		// TODO Auto-generated method stub
		
		StatKB sKb = (StatKB)kb;
		float overMean = sKb.generalStat.mean;
		
		int count = 0;
		float overDevSum = 0;
		Collection<Rating> ratings = param.ratingVector.gets();
		for (Rating rating : ratings) {
			if (!rating.isRated())
				continue;
			
			overDevSum += rating.value - overMean;
			count ++;
		}
		
		float overDev = 0;
		if (count > 0)
			overDev = overDevSum / count;
		
		RatingVector result = param.ratingVector.newInstance(true);
		for (int queryId : queryIds) {
			float itemOverDev = 0;
			
			Stat itemStat = sKb.itemStats.get(queryId);
			if (itemStat != null)
				itemOverDev = itemStat.overDev;
			
			result.put(queryId, overMean + overDev + itemOverDev);
		}
		
		
		if (result.size() == 0)
			return null;
		else
			return result;
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new StatNaiveCF();
	}

	
	
}
