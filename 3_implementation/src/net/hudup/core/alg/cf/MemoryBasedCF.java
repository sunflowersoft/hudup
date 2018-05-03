/**
 * 
 */
package net.hudup.core.alg.cf;

import java.util.List;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.alg.FilterParam;
import net.hudup.core.alg.MemoryBasedRecommender;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.Recommender;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Pair;
import net.hudup.core.data.RatingVector;



/**
 * Note, the abstract class {@code Recommender} represents all recommendation algorithm.
 * Two typical inherited classes of {@link Recommender} are {@code MemoryBasedRecomender} and {@code ModelBasedRecommender}
 * which in turn are abstract classes for memory-based recommendation algorithm and model-based recommendation algorithm.
 * There are two common trends: content-base filtering (CBF) and collaborative filtering (CF) in building up a recommendation algorithms as follows:
 * <ul>
 * <li>
 * The CBF recommends an item to user if such item has similarities in contents to other items that he like most in the past (his rating for such item is high). Note that each item has contents which are its properties and so all items will compose a matrix, called item content matrix
 * </li>
 * <li>
 * The CF, on the other hand, recommends an item to user if his neighbors which are other users similar to him are interested in such item. User’s rating on any item expresses his interest on that item. All ratings on items will also compose a matrix, called rating matrix.
 * </li>
 * </ul>
 * Both CBF and CF have their own strong and weak points.
 * <br>
 * This abstract class represents the memory-based recommendation algorithm with collaborative filtering (CF), called {@code memory-based CF recommender}.
 * {@code Memory-based CF recommender} uses rating matrix (as a part of the internal dataset {@link #dataset}) stored in memory for filtering task in recommendation.
 * As mentioned, two main methods of any recommender are {@code estimate(...)} and {@code recommend(...)}.
 * This class only defines method {@code recommend(...)} so that classes that extend it must defines the other one {@code estimate(...)}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@CFAnnotation
public abstract class MemoryBasedCF extends MemoryBasedRecommender {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal dataset.
	 */
	protected Dataset dataset = null;

	
	/**
	 * Default constructor.
	 */
	public MemoryBasedCF() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public void setup(Dataset dataset, Object... params) throws Exception {
		// TODO Auto-generated method stub
		unsetup();
		
		this.dataset = dataset;
	}
	
	
	@Override
	public void unsetup() {
		// TODO Auto-generated method stub
		super.unsetup();
		
		this.dataset = null;
	}

	
	@Override
	public Dataset getDataset() {
		return dataset;
	}
	

	@Override
	public RatingVector recommend(RecommendParam param, int maxRecommend) {
		param = preprocess(param);
		if (param == null)
			return null;
		
		filterList.prepare(param);
		Fetcher<Integer> fieldIds = dataset.fetchItemIds();
		
		List<Pair> pairs = Util.newList();
		float maxRating = dataset.getConfig().getMaxRating();
		try {
			int size = fieldIds.getMetadata().getSize();
			
			int i = 0;
			while (fieldIds.next()) {
				i++;
				
				Integer fieldId = fieldIds.pick();
				if (fieldId == null || fieldId < 0 || param.ratingVector.isRated(fieldId))
					continue;
				//
				if(!filterList.filter(dataset, FilterParam.create(fieldId)))
					continue;
				
				Set<Integer> queryIds = Util.newSet();
				queryIds.add(fieldId);
				RatingVector predict = estimate(param, queryIds);
				if (predict == null || !predict.isRated(fieldId))
					continue;
				
				// Finding maximum rating
				float value = predict.get(fieldId).value;
				int found = Pair.findIndexOfLessThan(value, pairs);
				Pair pair = new Pair(fieldId, value);
				if (found == -1)
					pairs.add(pair);
				else 
					pairs.add(found, pair);
				
				int n = pairs.size();
				// Always having maxRecommend + 1
				if (maxRecommend > 0 && n >= maxRecommend) {
					
					int lastIndex = pairs.size() - 1;
					Pair last = pairs.get(lastIndex);
					if (last.value() == maxRating || i >= size)
						break;
					else if (n > maxRecommend)
						pairs.remove(lastIndex);
				}
				
				
			} // end while
	
			
			if (maxRecommend > 0 && pairs.size() > maxRecommend)
				pairs.remove(pairs.size() - 1);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				fieldIds.close();
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		if (pairs.size() == 0)
			return null;
		
		RatingVector rec = param.ratingVector.newInstance(true);
		Pair.fillRatingVector(rec, pairs);
		return rec;
		
	}


	@Override
	public DataConfig createDefaultConfig() {
		return new DataConfig();
	}
	
	
	
}
