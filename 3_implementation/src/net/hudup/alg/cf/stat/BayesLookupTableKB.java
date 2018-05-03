package net.hudup.alg.cf.stat;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.KBaseAbstract;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Rating;
import net.hudup.core.data.RatingVector;

/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class BayesLookupTableKB extends KBaseAbstract {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public final static String PRECISION = "precision";
	
    
	/**
	 * 
	 */
	public final static String MAX_ITERATION = "max_iteration";

	
	/**
	 * 
	 */
	protected List<Integer> itemIds = Util.newList();
	
	
	/**
	 * 
	 */
	protected Map<Integer, Map<Integer, Map<Integer, Float>>> parameters = Util.newMap(); 
	
	
	/**
	 * 
	 */
	public BayesLookupTableKB() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * 
	 * @param userId specified user identifier (user ID).
	 * @param itemId specified item identifier (item ID).
	 * @return estimated rating value.
	 */
	public float estimate(int userId, int itemId) {
		if (!parameters.containsKey(userId))
			return Constants.UNUSED;
		
		Map<Integer, Map<Integer, Float>> itemMap = parameters.get(userId);
		if (!itemMap.containsKey(itemId))
			return Constants.UNUSED;
		
		Map<Integer, Float> probs = itemMap.get(itemId);
		Set<Integer> ratings = probs.keySet();
		float maxProb = -1;
		float bestRating = Constants.UNUSED;
		for (int rating : ratings) {
			float prob = probs.get(rating);
			if (prob > maxProb) {
				maxProb = prob;
				bestRating = rating;
			}
		}
		
		return bestRating;
	}
	
	
	/**
	 * 
	 * @return set of user id (s)
	 */
	public Set<Integer> getUserIds() {
		return parameters.keySet();
	}

	
	/**
	 * 
	 * @return list of item id (s)
	 */
	public List<Integer> getItemIds() {
		return itemIds;
	}
	
	
	@Override
	public void learn(Dataset dataset, Alg alg) {
		// TODO Auto-generated method stub
		super.learn(dataset, alg);
		
		destroyDataStructure();

		Fetcher<RatingVector> userRatings = dataset.fetchUserRatings();
		Map<Integer, Integer> ratingCounts = Util.newMap(); // key: rating value, value: count of such rating value
		try {
			
			while (userRatings.next()) {
				RatingVector userRating = userRatings.pick();
				if (userRating == null)
					continue;
				
				Collection<Rating> ratings = userRating.gets();
				for (Rating rating : ratings) {
					if (!rating.isRated())
						continue;
					
					int ratingValue = (int) (rating.value + 0.5);
					if (ratingCounts.containsKey(ratingValue))
						ratingCounts.put(ratingValue, ratingCounts.get(ratingValue) + 1);
					else
						ratingCounts.put(ratingValue, 1);
				}
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				userRatings.close();
			} 
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (ratingCounts.size() == 0)
			return;
		List<Integer> ratings = Util.newList();
		ratings.addAll(ratingCounts.keySet());
		
		int sumCountRatings = 0;
		for (int rating : ratings) {
			sumCountRatings += ratingCounts.get(rating);
		}
		Map<Integer, Float> ratingParameters = Util.newMap();// key: rating value, value: probability of such rating value
		for (int rating : ratings) {
			ratingParameters.put(rating, (float) ratingCounts.get(rating) / (float) sumCountRatings);
		}

		
		Map<Integer, Map<Integer, Integer>> userRatingCounts = Util.newMap(); // key: user id, value: counts of ratings
		userRatings = dataset.fetchUserRatings();
		try {
			
			while (userRatings.next()) {
				RatingVector userRating = userRatings.pick();
				if (userRating == null)
					continue;
				
				int userId = userRating.id();
				Set<Integer> keys = userRating.fieldIds(); // item id (s)
				List<Integer> values = Util.newList(); // ratings given by user
				for (int key : keys) {
					if (!userRating.isRated(key))
						continue;
					
					int value = (int) (userRating.get(key).value + 0.5);
					values.add(value);
				}
				
				Map<Integer, Integer> map = null; // key: rating value, value: count of such rating value
				if (userRatingCounts.containsKey(userId))
					map = userRatingCounts.get(userId);
				else {
					map = Util.newMap();
				}
				
				for (int r : values) {
					
					if (map.containsKey(r))
						map.put(r, map.get(r) + 1);
					else
						map.put(r, 1);
				}
				
				if (map.size() > 0)
					userRatingCounts.put(userId, map);
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				userRatings.close();
			} 
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		List<Integer> userIds = Util.newList();
		userIds.addAll(userRatingCounts.keySet());
		if (userIds.size() == 0)
			return;
		//
		Map<Integer, Map<Integer, Float>> userParameters = Util.newMap();
		for (int userId : userIds) {
			Map<Integer, Float> probs = Util.newMap();
			userParameters.put(userId, probs);
			
			for (int rating : ratings) {
				if (userRatingCounts.get(userId).containsKey(rating))
					probs.put(rating, (float) userRatingCounts.get(userId).get(rating) / (float) ratingCounts.get(rating));
				else
					probs.put(rating, 0.0f);
			}
		}
		userRatingCounts.clear();

		
		Map<Integer, Map<Integer, Integer>> itemRatingCounts = Util.newMap(); // key: user id, value: counts of ratings
		Fetcher<RatingVector> itemRatings = dataset.fetchItemRatings();
		try {
			
			while (itemRatings.next()) {
				RatingVector itemRating = itemRatings.pick();
				if (itemRating == null)
					continue;
				
				int itemId = itemRating.id();
				Set<Integer> keys = itemRating.fieldIds(); // user id (s)
				List<Integer> values = Util.newList(); // ratings giving to item
				for (int key : keys) {
					if (!itemRating.isRated(key))
						continue;
					
					int value = (int) (itemRating.get(key).value + 0.5);
					values.add(value);
				}
				
				Map<Integer, Integer> map = null; // key: rating value, value: count of such rating value
				if (itemRatingCounts.containsKey(itemId))
					map = itemRatingCounts.get(itemId);
				else {
					map = Util.newMap();
				}
				
				for (int r : values) {
					
					if (map.containsKey(r))
						map.put(r, map.get(r) + 1);
					else
						map.put(r, 1);
				}
				
				if (map.size() > 0)
					itemRatingCounts.put(itemId, map);
				
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				itemRatings.close();
			} 
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		List<Integer> itemIds = Util.newList();
		itemIds.addAll(itemRatingCounts.keySet());
		if (itemIds.size() == 0)
			return;
		//
		Map<Integer, Map<Integer, Float>> itemParameters = Util.newMap();
		for (int itemId : itemIds) {
			Map<Integer, Float> probs = Util.newMap();
			itemParameters.put(itemId, probs);
			
			for (int rating : ratings) {
				if (itemRatingCounts.get(itemId).containsKey(rating))
					probs.put(rating, (float) itemRatingCounts.get(itemId).get(rating) / (float) ratingCounts.get(rating));
				else
					probs.put(rating, 0.0f);
			}
		}
		itemRatingCounts.clear();


		parameters.clear();
		for (int userId : userIds) {
			Map<Integer, Map<Integer, Float>> itemMap = Util.newMap();
			parameters.put(userId, itemMap);
			
			for (int itemId : itemIds) {
				Map<Integer, Float> probs = Util.newMap();
				itemMap.put(itemId, probs);
				
				for (int rating : ratings) {
					float prob = ratingParameters.get(rating);
					probs.put(rating, prob);
				}
			}
		}
		ratingParameters.clear();
		
		float precision = getConfig().getAsReal(PRECISION);
		int maxIteration = getConfig().getAsInt(MAX_ITERATION);
		maxIteration = maxIteration == 0 ? Integer.MAX_VALUE : maxIteration;
		
		int iteration = 0;
		// Pay attention to this line, it is terminated condition
		float epsilon = userIds.size() * itemIds.size() * ratings.size() * (1f - precision); 
        
		while (iteration < maxIteration) {

            float terminatedCondition = 0;
			for (int userId : userIds) {
				Map<Integer, Float> userProbs = userParameters.get(userId);
				
				for (int itemId : itemIds) {
					Map<Integer, Float> itemProbs = itemParameters.get(itemId);
					Map<Integer, Float> probs = parameters.get(userId).get(itemId);
					
					float denominator = 0;
					for (int rating : ratings) {
						denominator += userProbs.get(rating) * itemProbs.get(rating) * probs.get(rating);
					}
					
					for (int rating : ratings) {
						float prob = probs.get(rating);
						float numerator = userProbs.get(rating) * itemProbs.get(rating) * prob;
						
						float newProb = 0;
						if (numerator != 0)
							newProb = numerator / denominator;
						
						probs.put(rating, newProb);
						
						// Pay attention to this line, it is terminated condition
						terminatedCondition += Math.abs(newProb - prob) / prob;
						
					} // End for ratings
					
				} // End for items
				
			} // End for users
			
            if (terminatedCondition < epsilon)
            	break;
            
			iteration ++;
			
		}

		this.itemIds.clear();
		this.itemIds.addAll(itemIds);
		this.parameters.keySet();
		
		ratings.clear();
		ratingCounts.clear();
		userIds.clear();
		itemIds.clear();
		userParameters.clear();
		itemParameters.clear();
		ratingParameters.clear();
		
	}


	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return parameters.size() == 0;
	}


	@Override
	public void close() {
		// TODO Auto-generated method stub
		super.close();
		destroyDataStructure();
	}
	
	
	/**
	 * 
	 */
	protected void destroyDataStructure() {
		itemIds.clear();
		parameters.clear();
	}
	
	
	/**
	 * 
	 * @param cf specified Bayesian Lookup Table Collaborative Filtering (Bayesian Lookup Table CF) algorithm.
	 * @return {@link BayesLookupTableKB}
	 */
	public static BayesLookupTableKB create(final BayesLookupTableCF cf) {
		
		return new BayesLookupTableKB() {

			
			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			
			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return cf.getName();
			}
		};
	}
	
	
	
}
