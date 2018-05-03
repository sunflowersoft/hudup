/**
 * 
 */
package net.hudup.core.alg.cf;

import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;

/**
 * This class sets up the neighbor collaborative filtering (Neighbor CF) algorithm for users. It extends directly {@link NeighborCF} class.
 * It is often called Neighbor User-Based CF because the similar measure is calculated between two user rating vectors (possibly, plus two user profiles).
 * Note, user rating vector contains all ratings of the same user on many items.
 * This class is completed because it defines the {@link #estimate(RecommendParam, Set)} method.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class NeighborUserBasedCF extends NeighborCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public NeighborUserBasedCF() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) {
		if (param.ratingVector.count(true) < 2)
			return null;
		
		Fetcher<RatingVector> vRatings = null;
		vRatings = dataset.fetchUserRatings();
		
		RatingVector result = param.ratingVector.newInstance(true);
		boolean hybrid = config.getAsBoolean(HYBRID);
		float minValue = dataset.getConfig().getMinRating();
		float maxValue = dataset.getConfig().getMaxRating();
		for (int queryId : queryIds) {
			float accum = 0;
			float simTotal = 0;
			try {
				while (vRatings.next()) {
					RatingVector that = vRatings.pick();
					if (that == null || !that.isRated(queryId))
						continue;
					
					Profile profile1 = hybrid ? param.profile : null;
					Profile profile2 = hybrid ? dataset.getUserProfile(that.id()) : null;
					
					// computing similarity array
					float sim = similar(param.ratingVector, that, profile1, profile2);
					if (!Util.isUsed(sim))
						continue;
					
					float thatValue = that.get(queryId).value;
					float thatMean = that.mean();
					float deviate = thatValue - thatMean;
					accum += sim * deviate;
					simTotal += Math.abs(sim);
				}
				vRatings.reset();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
			
			float mean = param.ratingVector.mean();
			float value = simTotal == 0 ? mean : mean + accum / simTotal;
			value = Math.min(value, maxValue);
			value = Math.max(value, minValue);
			
			result.put(queryId, value);
		}
		
		try {
			vRatings.close();
		} 
		catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (result.size() == 0)
			return null;
		else
			return result;

	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "neighbor_userbased";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new NeighborUserBasedCF();
	}


}
