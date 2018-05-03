/**
 * 
 */
package net.hudup.alg.cf.stat;

import java.util.Set;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.data.RatingVector;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class MeanItemCF extends StatCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "mean_item";
	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) {
		// TODO Auto-generated method stub
		StatKB sKb = (StatKB)kb;
		
		RatingVector result = param.ratingVector.newInstance(true);
		for (int queryId : queryIds) {
			Stat stat = null;
			
			stat = sKb.itemStats.get(queryId);
			
			if (stat != null)
				result.put(queryId, stat.mean);
		}
		
		
		if (result.size() == 0)
			return null;
		else
			return result;
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new MeanItemCF();
	}

	
	
	
}
