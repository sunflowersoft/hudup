package net.hudup.sparse;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import net.hudup.alg.cf.mf.SvdGradientCF;
import net.hudup.alg.cf.mf.SvdGradientKB;
import net.hudup.alg.cf.stat.BayesLookupTableCF;
import net.hudup.alg.cf.stat.BayesLookupTableKB;
import net.hudup.alg.cf.stat.MeanItemCF;
import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.Recommender;
import net.hudup.core.alg.cf.CFAnnotation;
import net.hudup.core.alg.cf.MemoryBasedCF;
import net.hudup.core.alg.cf.NeighborUserBasedCF;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.FetcherUtil;
import net.hudup.core.data.Rating;
import net.hudup.core.data.RatingMatrix;
import net.hudup.core.data.RatingVector;
import net.hudup.core.data.Snapshot;
import net.hudup.data.SnapshotImpl;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class SparseProcessor {

	
	/**
	 * 
	 */
	public SparseProcessor() {
		
	}
	
	
	/**
	 * 
	 * @param vector
	 * @return mean value
	 */
	private static float mean(float[] vector) {
		float sum = 0;
		float count = 0;
		int n = vector.length;
		for (int i = 0; i < n; i++) {
			float v = vector[i];
			if (Util.isUsed(v)) {
				sum += v;
				count++;
			}
		}
		
		return sum / count;
	}
	
	
	/**
	 * 
	 * @param matrix
	 * @return whether estimating successfully
	 */
	public boolean heuristicComplete(float[][] matrix) {
		int m = matrix.length;
		
		for (int i = 0; i < m; i++) {
			float[] row = matrix[i];
			
			int n = row.length;
			for (int j = 0; j < n; j++) {
				float value = row[j];
				if (Util.isUsed(value))
					continue;
				
				float sum = 0;
				float count = 0;
				for (int k = 0; k < m; k++) {
					float v = matrix[k][j];
					if (Util.isUsed(v)) {
						sum += v;
						count++;
					}
				}
				
				float mean = 0;
				if (count == 0)
					mean = mean(row);
				else
					mean = sum / count;
				
				row[j] = mean;
				
			} // End columns
		
		} // End rows
		
		
		return true;
	}
	

	/**
	 * 
	 * @param matrix
	 * @return whether estimating successfully
	 */
	public boolean columnMeanComplete(float[][] matrix) {
		int m = matrix.length;
		if (m == 0)
			return false;
		
		int n = matrix[0].length;
		float[] means = new float[n];
		Arrays.fill(means, Constants.UNUSED);
		
		for (int j = 0; j < n; j++) {
			float sum = 0;
			int count = 0;
			for (int i = 0; i < m; i++) {
				float value = matrix[i][j];
				
				if (Util.isUsed(value)) {
					sum += value;
					count ++;
				}
					
			}
			if (count != 0)
				means[j] = sum / (float)count;
		}
		
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				float value = matrix[i][j];
				
				if (!Util.isUsed(value))
					matrix[i][j] = means[j];
			}
		}
		
		return true;
	}
	
	
	/**
	 * 
	 * @param matrix
	 * @param value
	 * @return whether filling successfully
	 */
	public boolean fillValueComplete(float[][] matrix, float value) {
		int m = matrix.length;
		if (m == 0)
			return false;

		for (int i = 0; i < m; i++) {
			int n = matrix[i].length;
			for (int j = 0; j < n; j++) {
				float v = matrix[i][j];
				if (!Util.isUsed(v))
					matrix[i][j] = value;
			}
		}
		
		return true;
	}

	
	/**
	 * 
	 * @param snapshot
	 * @param completeMethod
	 * @return whether estimating successfully
	 */
	public boolean algComplete(Snapshot snapshot, Alg completeMethod) {
		
		if (completeMethod == null)
			completeMethod = new NeighborUserBasedCF();
		else if (!(completeMethod instanceof Recommender) ||
				completeMethod.getClass().getAnnotation(CFAnnotation.class) == null) {
			throw new RuntimeException("Not implement yet for none-collaborative filtering algorithm");
		}
		else if (completeMethod instanceof SvdGradientCF) {
			return svdComplete(snapshot, (SvdGradientCF)completeMethod);
		}
		else if (completeMethod instanceof BayesLookupTableCF) {
			return bayesLutComplete(snapshot, (BayesLookupTableCF)completeMethod);
		}
		
		Recommender recommender = (Recommender) completeMethod;
		try {
			if (recommender instanceof MemoryBasedCF)
				recommender.setup((Snapshot)snapshot.clone());
			else
				recommender.setup(snapshot);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			recommender.unsetup();
			
			return false;
		}
		
		
		Set<Integer> totalItemIds = Util.newSet(); // total item id (s)
		FetcherUtil.fillCollection(totalItemIds, snapshot.fetchItemIds(), true);
		Fetcher<RatingVector> userRatings = snapshot.fetchUserRatings();
		
		try {
			while (userRatings.next()) {
				RatingVector userRating = userRatings.pick();
				if (userRating == null || userRating.size() == 0)
					continue;
				
				Set<Integer> ratedItemIds = userRating.fieldIds(true);
				List<Integer> unratedItemIds = Util.newList();
				for (int itemId : totalItemIds) {
					if (!ratedItemIds.contains(itemId))
						unratedItemIds.add(itemId);
				}
				
				if (unratedItemIds.size() == 0)
					continue;
				
				Set<Integer> queryIds = Util.newSet();
				queryIds.addAll(unratedItemIds); // query items are unrated items
				
				RatingVector result = recommender.estimate(new RecommendParam(userRating), queryIds);
				Set<Integer> resultIds = Util.newSet();
				if (result != null)
					resultIds = result.fieldIds();
				
				for (int resultId : resultIds) {
					Rating rating = new Rating(Constants.UNUSED);
					snapshot.putRating(userRating.id(), resultId, rating);
						
					float ratingValue = result.get(resultId).value;
					rating.value = ratingValue;
				}
				
				// Filling mean value for items not estimated
				float mean = userRating.mean();
				queryIds.removeAll(resultIds);
				for (int queryId : queryIds) {
					Rating rating = new Rating(Constants.UNUSED);
					snapshot.putRating(userRating.id(), queryId, rating);
						
					rating.value = mean;
				}
				
			}
			
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (userRatings != null)
					userRatings.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
			
			recommender.unsetup();
		}
		
		
		return true;
	}
	
	
	/**
	 * 
	 * @param userMatrix
	 * @param completeMethod
	 * @param updateMetadata
	 * @return whether estimating successfully
	 */
	public boolean algComplete(RatingMatrix userMatrix, Alg completeMethod, boolean updateMetadata) {
		if (completeMethod == null)
			completeMethod = new NeighborUserBasedCF();
		else if (!(completeMethod instanceof Recommender) ||
				completeMethod.getClass().getAnnotation(CFAnnotation.class) == null) {
			throw new RuntimeException("Not implement yet for none-collaborative filtering algorithm");
		}
		else if (completeMethod instanceof MeanItemCF) {
			boolean result = columnMeanComplete(userMatrix.matrix);
			if (updateMetadata)
				userMatrix.updateMetadata();
			return result;
		}
		else if (completeMethod instanceof SvdGradientCF) {
			return svdComplete(userMatrix, (SvdGradientCF) completeMethod);
		}
		else if (completeMethod instanceof BayesLookupTableCF) {
			throw new RuntimeException("Not implement yet");
		}
		
		SnapshotImpl snapshot = SnapshotImpl.create(userMatrix, true);
		if (snapshot == null)
			return false;
		if (!algComplete(snapshot, completeMethod))
			return false;
		
		RatingMatrix newMatrix = snapshot.createUserMatrix();
		snapshot.clear();
		if (newMatrix == null)
			return false;
		
		userMatrix.clear();
		userMatrix.assign(newMatrix);
		return true;
	}
	
	
	/**
	 * 
	 * @param snapshot
	 * @param svdCf
	 * @return whether estimating successfully
	 */
	private boolean svdComplete(Snapshot snapshot, SvdGradientCF svdCf) {
		
		boolean result = true;
		try {
			svdCf.setup(snapshot);
			
			SvdGradientKB gradientKb = (SvdGradientKB)svdCf.getKBase();
			List<Integer> userIds = gradientKb.getUserIds();
			List<Integer> itemIds = gradientKb.getItemIds();
			
			for (int userId : userIds) {
				
				for (int itemId : itemIds) {
					Rating rating = snapshot.getRating(userId, itemId);
					if (rating != null && rating.isRated())
						continue;
					
					float ratingValue = gradientKb.estimate(userId, itemId);
					if (!Util.isUsed(ratingValue)) {
						RatingVector vRating = snapshot.getItemRating(itemId);
						ratingValue = vRating.mean();
					}
					
					if (rating == null) {
						rating = new Rating(ratingValue);
						snapshot.putRating(userId, itemId, rating);
					}
					else
						rating.value = ratingValue;
				}
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = false;
		}
		finally {
			svdCf.unsetup();
		}
		
		return result;
	}
	
	
	/**
	 * 
	 * @param userMatrix
	 * @param svdCf
	 * @param updateMetadata
	 * @return whether estimating successfully
	 */
	private boolean svdComplete(RatingMatrix userMatrix, SvdGradientCF svdCf) {
		
		boolean result = true;
		try {
			svdCf.setup0(userMatrix);
			
			SvdGradientKB gradientKb = (SvdGradientKB)svdCf.getKBase();
			RatingMatrix newMatrix = gradientKb.createUserRatingMatrix();
			if (newMatrix == null)
				result = false;
			else
				userMatrix.assign(newMatrix);
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = false;
		}
		finally {
			svdCf.unsetup();
		}
		
		return result;

	}


	/**
	 * 
	 * @param snapshot
	 * @param bayesLutCf
	 * @return whether estimating successfully
	 */
	private boolean bayesLutComplete(Snapshot snapshot, BayesLookupTableCF bayesLutCf) {
		
		boolean result = true;
		try {
			bayesLutCf.setup(snapshot);
			
			BayesLookupTableKB bayesLutKb = (BayesLookupTableKB)bayesLutCf.getKBase();
			Set<Integer> userIds = bayesLutKb.getUserIds();
			List<Integer> itemIds = bayesLutKb.getItemIds();
			
			for (int userId : userIds) {
				
				for (int itemId : itemIds) {
					Rating rating = snapshot.getRating(userId, itemId);
					if (rating != null && rating.isRated())
						continue;
					
					float ratingValue = bayesLutKb.estimate(userId, itemId);
					if (!Util.isUsed(ratingValue)) {
						RatingVector vRating = snapshot.getItemRating(itemId);
						ratingValue = vRating.mean();
					}
					
					if (rating == null) {
						rating = new Rating(ratingValue);
						snapshot.putRating(userId, itemId, rating);
					}
					else
						rating.value = ratingValue;
				}
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = false;
		}
		finally {
			bayesLutCf.unsetup();
		}
		
		return result;
	}


}
