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
import net.hudup.core.logistic.NextUpdate;

/**
 * This class is also the neighbor collaborative filtering (Neighbor CF) algorithm for users because it extends directly {@link NeighborUserBasedCF}.
 * However it tries to calculate a so-called 2D (2-Dimension) similar measure which is the weighted sum of two similar measures.
 * The first similar measure is of two users and second similar measure is of two items.
 * So it is called 2D (2-Dimension) Neighbor CF algorithm. It mainly re-defines the method {@link #estimate(RecommendParam, Set)} for supporting 2D similar measure.
 * It is not perfect and so it is a next-update algorithm.
 * Note, next-update algorithm is the one which needs to be updated in next version of Hudup framework because it is not perfect, for example.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
public class Neighbor2dCF extends NeighborUserBasedCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public Neighbor2dCF() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) {
		// TODO Auto-generated method stub
		
		Fetcher<RatingVector> vUserRatings = null;
		Fetcher<RatingVector> vItemRatings = null;
		vUserRatings = dataset.fetchUserRatings();
		vItemRatings = dataset.fetchItemRatings();
		
		RatingVector result = param.ratingVector.newInstance(true);
		boolean hybrid = config.getAsBoolean(HYBRID);
		for (int queryId : queryIds) {
			float accum = 0;
			float simTotal = 0;
			
			try {
				while (vUserRatings.next()) {
					
					RatingVector user = vUserRatings.pick();
					if (user == null)
						continue;
					
					Profile thisProfile = hybrid ? param.profile : null;
					Profile userProfile = hybrid ? dataset.getUserProfile(user.id()) : null;
					
					float userSim = similar(param.ratingVector, user, thisProfile, userProfile);
					
					try {
						while (vItemRatings.next()) {
							RatingVector item = vItemRatings.pick();
							if (item == null)
								continue;
							//
							if(!user.isRated(item.id())) 
								continue;
							
							RatingVector fieldItem = null;
							Profile fieldProfile = null; 
							Profile itemProfile = null;
							
							if (hybrid) {
								fieldItem = dataset.getItemRating(queryId); 
								fieldProfile = dataset.getItemProfile(queryId);  
								itemProfile = dataset.getItemProfile(item.id());  
							}
							
							float itemSim = 0;
							if (fieldItem != null)
								itemSim = similar(fieldItem, item, fieldProfile, itemProfile);
									
							float gravity = (float)Math.sqrt(userSim * userSim + itemSim * itemSim);
							if (!Util.isUsed(gravity))
								continue;
							
							float rating = user.get(item.id()).value;
							accum += gravity * rating;
							simTotal += Math.abs(gravity);
						}
						vItemRatings.reset();
					}
					catch (RemoteException e) {
						e.printStackTrace();
					}
					
					
				}
				vUserRatings.reset();
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
			
			
			float value = (simTotal == 0 ? param.ratingVector.mean() : accum / simTotal);
			result.put(queryId, value);
		}
		
		
		try {
			vUserRatings.close();
			vItemRatings.close();
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
		return "neighbor_2d";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new Neighbor2dCF();
	}
	
	
}
