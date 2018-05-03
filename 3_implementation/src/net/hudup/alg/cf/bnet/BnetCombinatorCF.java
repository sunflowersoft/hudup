package net.hudup.alg.cf.bnet;

import java.util.List;
import java.util.Set;

import net.hudup.alg.cf.mf.SvdGradientCF;
import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgList;
import net.hudup.core.alg.CompositeRecommender;
import net.hudup.core.alg.FilterParam;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.Recommender;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.RatingFilter;
import net.hudup.core.logistic.ValueTriple;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class BnetCombinatorCF extends CompositeRecommender {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public final static Class<? extends BnetAbstractCF> BNET_CF_CLASS = BnetClusteredCF.class;

	
	/**
	 * 
	 */
	public final static Class<? extends Recommender> OTHER_RECOMMENDER_CLASS = SvdGradientCF.class;

	
	/**
	 * 
	 */
	public BnetCombinatorCF() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * 
	 * @param param specified recommendation parameter.
	 * @param queryIds specified query identifiers (IDs). Such IDs can be item IDs or user IDs.
	 * @param referredRatingValue referred rating value.
	 * @param ratingFilter specified rating filter.
	 * @return list of {@link ValueTriple} (s).
	 */
	protected List<ValueTriple> estimate(RecommendParam param, Set<Integer> queryIds, float referredRatingValue, RatingFilter ratingFilter) {
		// TODO Auto-generated method stub
		
		List<ValueTriple> triples = getBnetCF().bnetEstimate(param, queryIds, referredRatingValue, ratingFilter);
		if (triples.size() == 0)
			return Util.newList();
		
		Recommender recommender = getOtherRecommender();
		RatingVector vRating = recommender.estimate(param, queryIds);
		for (ValueTriple triple : triples) {
			float estimatedValue = Constants.UNUSED;
			if (vRating != null) {
				int itemId = triple.key();
				estimatedValue = vRating.contains(itemId) ? vRating.get(itemId).value : Constants.UNUSED;
			}
			
			float value = Util.isUsed(estimatedValue) ? (triple.getValue1() + estimatedValue) / 2 : triple.getValue1();
			triple.setValue1(value);
		}
		
		return triples;
	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) {
		
		List<ValueTriple> triples = estimate(param, queryIds, Constants.UNUSED, null);
		if (triples == null || triples.size() == 0)
			return null;
		
		RatingVector result = param.ratingVector.newInstance(true);
		ValueTriple.fillByValue1(result, triples);
		
		return result;
	}
	
	
	@Override
	public RatingVector recommend(RecommendParam param, int maxRecommend) {
		// TODO Auto-generated method stub
		param = preprocess(param);
		if (param == null)
			return null;
		
		filterList.prepare(param);
		Set<Integer> totalItemIds = getBnetCF().getItemIds();
		Set<Integer> queryIds = Util.newSet();
		for (int itemId : totalItemIds) {
			
			if ( !param.ratingVector.isRated(itemId) && filterList.filter(getDataset(), FilterParam.create(itemId)) )
				queryIds.add(itemId);
		}
		
		float avgRating = (config.getMaxRating() + config.getMinRating()) / 2.0f; 
		List<ValueTriple> triples = estimate(param, queryIds, avgRating, new RatingFilter() {

			@Override
			public boolean accept(float ratingValue, float referredRatingValue) {
				// TODO Auto-generated method stub
				return ratingValue >= referredRatingValue;
			}
			
		});
		if (triples == null || triples.size() == 0)
			return null;
		
		ValueTriple.sortByValue1(triples, true, maxRecommend);
		
		RatingVector result = param.ratingVector.newInstance(true);
		ValueTriple.fillByValue1(result, triples);
		
		return result;
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		DataConfig config = super.createDefaultConfig();
		
		try {
			AlgList algList = new AlgList();

			BnetAbstractCF bnetCF = BNET_CF_CLASS.newInstance();
			bnetCF.getConfig().setStoreUri(config.getStoreUri().concat(bnetCF.getName()));
			algList.add(bnetCF);
			
			Recommender otherRecommender = OTHER_RECOMMENDER_CLASS.newInstance();
			otherRecommender.getConfig().setStoreUri(config.getStoreUri().concat(otherRecommender.getName()));
			algList.add(otherRecommender);

			config.put(INNER_RECOMMENDER, algList);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		
		return config;
	}


	/**
	 * 
	 * @return {@link BnetAbstractCF} algorithm
	 */
	private BnetAbstractCF getBnetCF() {
		AlgList innerRecommenders = getInnerRecommenders();
		for (int i = 0; i < innerRecommenders.size(); i++) {
			Recommender recommender = (Recommender)innerRecommenders.get(i);
			if (recommender instanceof BnetAbstractCF)
				return (BnetAbstractCF) recommender;
		}
		
		return null;
	}
	
	
	/**
	 * 
	 * @return other recommender algorithm
	 */
	private Recommender getOtherRecommender() {
		AlgList innerRecommenders = getInnerRecommenders();
		for (int i = 0; i < innerRecommenders.size(); i++) {
			Recommender recommender = (Recommender)innerRecommenders.get(i);
			if (!(recommender instanceof BnetAbstractCF))
				return recommender;
		}
		
		return null;
	}
	
	
	@Override
	public Dataset getDataset() {
		// TODO Auto-generated method stub
		BnetAbstractCF bnetCF = getBnetCF();
		if (bnetCF != null)
			return bnetCF.getDataset();
		else
			return null;
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new BnetCombinatorCF();
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "bayesnet_combinator";
	}


	
}
