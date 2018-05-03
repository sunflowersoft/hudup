/**
 * 
 */
package net.hudup.temp;

import java.util.List;
import java.util.Set;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.FilterParam;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.cf.ModelBasedCF;
import net.hudup.core.data.Pair;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.NextUpdate;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
public abstract class ModelBasedExtCF extends ModelBasedCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public ModelBasedExtCF() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * 
	 * @author Loc Nguyen
	 * @version 10.0
	 *
	 */
	@NextUpdate
	public static class TagRatingVector {
		
		
		/**
		 * Main recommended vector
		 */
		RatingVector Main = null; 
		
		
		/**
		 * Contains qualitative rating values attached to fields in main recommended vector
		 */
		RatingVector Tag = null;  
		
		
		/**
		 * 
		 * @param main
		 */
		public TagRatingVector(RatingVector main) {
			this.Main = main;
			if (main != null)
				this.Tag = (RatingVector) main.clone();
			else
				this.Tag = null;
		}
		
		
		/**
		 * 
		 * @param from
		 * @return {@link TagRatingVector}
		 */
		public static TagRatingVector newInstance(RatingVector from) {
			return new TagRatingVector(from.newInstance(true));
		}
		
		
		/**
		 * 
		 * @param fieldId
		 * @param value
		 * @param tagValue
		 */
		public void put(int fieldId, float value, float tagValue) {
			Main.put(fieldId, value);
			Tag.put(fieldId, tagValue);
		}
		
		
		/**
		 * 
		 * @return size of main {@link RatingVector}
		 */
		public int size() {
			return Main.size();
		}
		
		
		/**
		 * 
		 * @param descending
		 * @param maxCount
		 * @return {@link RatingVector}
		 */
		public RatingVector sortMainByTag(boolean descending, int maxCount) {
			if (Main == null || Main.size() == 0)
				return null;
			List<Pair> pairList = Util.newList();

			Set<Integer> fieldIds = Main.fieldIds(true);
			for (int fieldId : fieldIds) {
				if (!Tag.isRated(fieldId))
					continue;
				
				float value = Tag.get(fieldId).value;
				Pair pair = new Pair(fieldId, value);
				pairList.add(pair);
			}
			Pair.sort(pairList, descending, maxCount);
			
			RatingVector vRating = Main.newInstance(true);
			for (Pair pair : pairList) {
				int fieldId = pair.key();
				vRating.put(fieldId, Main.get(fieldId));
			}
			
			if (vRating.size() == 0)
				return null;
			else
				return vRating;
		}
		
		
	}
	
	
	/**
	 * 
	 * @param param
	 * @param queryIds
	 * @param filterValue
	 * @return {@link TagRatingVector}
	 */
	protected abstract TagRatingVector estimate(RecommendParam param, Set<Integer> queryIds, float filterValue);
	

	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) {
		// TODO Auto-generated method stub
		if (param == null)
			return null;

		TagRatingVector tag = estimate(param, queryIds, Constants.UNUSED);
		
		if (tag == null)
			return null;
		else {
			if (tag.Main == null || tag.Main.size() == 0)
				return null;
			else
				return tag.Main;
		}
	}


	@Override
	public RatingVector recommend(RecommendParam param, int maxRecommend) {
		param = preprocess(param);
		if (param == null)
			return null;
		
		filterList.prepare(param);
		Set<Integer> itemIds = getItemIds();
		Set<Integer> queryIds = Util.newSet();
		for (int itemId : itemIds) {
			
			if ( !param.ratingVector.isRated(itemId) && filterList.filter(getDataset(), FilterParam.create(itemId)) )
				queryIds.add(itemId);
		}
		
		if (maxRecommend == 0)
			maxRecommend = itemIds.size() - param.ratingVector.fieldIds(true).size();
		
		int maxRating = (int)kb.getConfig().getMaxRating();
		int minRating = (int)kb.getConfig().getMinRating();
		int count = 0;
		List<RatingVector> predictList = Util.newList();
		for (int i = maxRating; i >= minRating && count <= maxRecommend; i--) {
			TagRatingVector estimate = estimate(param, queryIds, i);
			if (estimate == null) continue;
			
			RatingVector predict = estimate.sortMainByTag(true, maxRecommend - count);
			if (predict == null) continue;
			
			count += predict.size();
			predictList.add(predict);
		}
		
		if (predictList.size() == 0)
			return null;
		
		RatingVector result = param.ratingVector.newInstance(true);
		for (RatingVector predict : predictList) {
			
			Set<Integer> ids = predict.fieldIds();
			for (int id : ids) {
				result.put(id, predict.get(id));
				if (result.size() == maxRecommend)
					break;
			}
			
			if (result.size() == maxRecommend)
				break;
		}
		
		if (result.size() == 0)
			return null;
		else
			return result;
		
	}

	
	/**
	 * 
	 * @return set of item id
	 */
	protected abstract Set<Integer> getItemIds();
	

}
