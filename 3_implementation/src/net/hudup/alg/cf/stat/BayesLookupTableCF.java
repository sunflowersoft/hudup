package net.hudup.alg.cf.stat;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import net.hudup.alg.cf.mf.SvdGradientKB;
import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.FilterParam;
import net.hudup.core.alg.KBase;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.cf.ModelBasedCF;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Pair;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.xURI;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class BayesLookupTableCF extends ModelBasedCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public final static float DEFAULT_PRECISION = 0.80f; // 20% deviation ratio
	
    
	/**
	 * 
	 */
	public final static int DEFAULT_MAX_ITERATION = 100;

	
	/**
	 * 
	 */
	public BayesLookupTableCF() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public KBase createKB() {
		// TODO Auto-generated method stub
		return BayesLookupTableKB.create(this);
	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) {
		// TODO Auto-generated method stub
		BayesLookupTableKB kb = (BayesLookupTableKB) getKBase();
		if (kb.isEmpty())
			return null;
		
		RatingVector result = param.ratingVector.newInstance(true);
		
		int userId = result.id();
		for (int queryId : queryIds) {
			float ratingValue = kb.estimate(userId, queryId);
			if (Util.isUsed(ratingValue))
				result.put(queryId, ratingValue);
		}
		
		if (result.size() == 0)
			return null;
		else
			return result;
	}


	@Override
	public RatingVector recommend(RecommendParam param, int maxRecommend) {
		// TODO Auto-generated method stub
		BayesLookupTableKB kb = (BayesLookupTableKB) getKBase();
		if (kb.isEmpty())
			return null;

		param = preprocess(param);
		if (param == null)
			return null;
		
		filterList.prepare(param);
		List<Integer> itemIds = kb.getItemIds();
		Set<Integer> queryIds = Util.newSet();
		for (int itemId : itemIds) {
			
			if ( !param.ratingVector.isRated(itemId) && filterList.filter(getDataset(), FilterParam.create(itemId)) )
				queryIds.add(itemId);
		}
		
		float maxRating = config.getMaxRating();
		int userId = param.ratingVector.id();
		
		List<Pair> pairs = Util.newList();
		int size = queryIds.size();
		int i = 0;
		for (int itemId : queryIds) {
			i++;
			
			float value = kb.estimate(userId, itemId);
			if (!Util.isUsed(value))
				continue;
			
			// Finding maximum rating
			int found = Pair.findIndexOfLessThan(value, pairs);
			Pair pair = new Pair(itemId, value);
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
			
		}
		
		if (maxRecommend > 0 && pairs.size() > maxRecommend)
			pairs.remove(pairs.size() - 1);
		if (pairs.size() == 0)
			return null;
		
		RatingVector rec = param.ratingVector.newInstance(true);
		Pair.fillRatingVector(rec, pairs);
		return rec;
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "bayes_lookup_table";
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new BayesLookupTableCF();
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = new DataConfig() {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			
			@Override
			public boolean validate(String key, Serializable value) {
				
				if (value instanceof Number) {
					double valueNumber = ((Number)value).doubleValue();
					
					if (valueNumber < 0)
						return false;
				}
				
				return true;
			}
			
		};
		
		config.put(SvdGradientKB.PRECISION, new Float(DEFAULT_PRECISION));
		config.put(SvdGradientKB.MAX_ITERATION, DEFAULT_MAX_ITERATION);
		
		xURI store = xURI.create(Constants.KNOWLEDGE_BASE_DIRECTORY).concat(getName());
		config.setStoreUri(store);
		
		return config;
	}
	
}





