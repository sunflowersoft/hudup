/**
 * 
 */
package net.hudup.temp;

import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.cf.MemoryBasedCF;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.RatingVector;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
public class SimpleCF extends MemoryBasedCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public SimpleCF() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public String getName() {
		return "simple";
	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) {
		Fetcher<RatingVector> ratings = null;
		ratings = dataset.fetchUserRatings();
		
		RatingVector result = param.ratingVector.newInstance(true);
		for (Integer queryId : queryIds) {
			float sum = 0;
			int count = 0;
			
			try {
				while (ratings.next()) {
					RatingVector rating = ratings.pick();
					if (rating == null)
						continue;
					
					if (!rating.isRated(queryId)) 
						continue;
					
					float value = rating.get(queryId).value;
					sum += value;
					count ++;
				}
				ratings.reset();
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
			
			if (count > 0)
				result.put(queryId, sum / (float)count);
		}
		
		try {
			ratings.close();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (result.size() == 0)
			return null;
		else
			return result;
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		return new DataConfig();
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new SimpleCF();
	}

	
}
