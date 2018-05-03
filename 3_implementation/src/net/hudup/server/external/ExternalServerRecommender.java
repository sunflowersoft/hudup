package net.hudup.server.external;

import java.util.Set;

import net.hudup.alg.cf.gfall.GreenFallCF;
import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgList;
import net.hudup.core.alg.CompositeRecommender;
import net.hudup.core.alg.Filter;
import net.hudup.core.alg.FilterParam;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.Recommender;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.NextUpdate;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
public class ExternalServerRecommender extends CompositeRecommender {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public ExternalServerRecommender() {
		super();
		// TODO Auto-generated constructor stub
		
		Recommender recommender = new GreenFallCF();
		setInnerRecommenders(new AlgList(recommender));
	}

	
	@Override
	public void setup(Dataset dataset, Object... params) throws Exception {
		// TODO Auto-generated method stub
		super.setup(dataset, params);
		
		initFilters();
	}


	@Override
	public void unsetup() {
		// TODO Auto-generated method stub
		super.unsetup();
		filterList.clear();
	}


	/**
	 * Initializing filters
	 */
	protected void initFilters() {
		filterList.clear();
		
		Filter filter = new Filter() {
			
			
			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;
			
			
			private float itemType = Constants.UNUSED;
			
			
			@Override
			public void prepare(RecommendParam param) {
				// TODO Auto-generated method stub
				if (param.extra == null || !(param.extra instanceof Number)) {
					itemType = ((Number) param.extra).floatValue();
				}
				else {
					itemType = Constants.UNUSED;
				}
			}
			
			
			@Override
			public boolean filter(Dataset dataset, FilterParam param) {
				// TODO Auto-generated method stub
				if (!Util.isUsed(itemType) || itemType < 0)
					return true;
				
				Profile itemProfile = dataset.getItemProfile(param.itemId);
				if (itemProfile == null)
					return true;
				
				int itemType = itemProfile.getValueAsInt(DataConfig.ITEM_TYPE_FIELD);
				if (itemType == -1)
					return true;
				else
					return itemType == (int) this.itemType;
			}
		};
		
		filterList.add(filter);
	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) {
		// TODO Auto-generated method stub
		return ((Recommender)getInnerRecommenders().get(0)).estimate(param, queryIds);
	}

	
	@Override
	public RatingVector recommend(RecommendParam param, int maxRecommend) {
		// TODO Auto-generated method stub
		return ((Recommender)getInnerRecommenders().get(0)).recommend(param, maxRecommend);
	}

	
	@Override
	public Dataset getDataset() {
		// TODO Auto-generated method stub
		AlgList innerRecommenders = getInnerRecommenders();
		if (innerRecommenders.size() > 0)
			return ((Recommender)getInnerRecommenders().get(0)).getDataset();
		else
			return null;
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "external_recommender";
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new ExternalServerRecommender();
	}

	
}
