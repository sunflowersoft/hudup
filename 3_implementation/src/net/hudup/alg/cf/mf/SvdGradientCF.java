package net.hudup.alg.cf.mf;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.FilterParam;
import net.hudup.core.alg.KBase;
import net.hudup.core.alg.KBaseIntegrated;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.cf.ModelBasedCF;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Pair;
import net.hudup.core.data.RatingMatrix;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.xURI;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class SvdGradientCF extends ModelBasedCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public SvdGradientCF() {
		super();
		// TODO Auto-generated constructor stub
		
		if (kb != null)
			config.putAll( ((KBaseIntegrated) kb).getDefaultParameters() );
	}

	
	@Override
	public KBase createKB() {
		// TODO Auto-generated method stub
		return SvdGradientKB.create(this);
	}

	
	/**
	 * 
	 * @param userMatrix specified user rating matrix, represented by {@link RatingMatrix}.
	 * @throws Exception if any error raises.
	 */
	public void setup0(RatingMatrix userMatrix) throws Exception {
		// TODO Auto-generated method stub
		unsetup();
		
		((SvdGradientKB) kb).learn0(userMatrix);
	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) {
		// TODO Auto-generated method stub
		SvdGradientKB kb = (SvdGradientKB) getKBase();
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
		
		SvdGradientKB kb = (SvdGradientKB) getKBase();
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
		return "svd_gradient";
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new SvdGradientCF();
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
		
		xURI store = xURI.create(Constants.KNOWLEDGE_BASE_DIRECTORY).concat(getName());
		config.setStoreUri(store);
		
		if (kb != null)
			config.putAll( ((KBaseIntegrated) kb).getDefaultParameters() );
		return config;
	}
	
}


