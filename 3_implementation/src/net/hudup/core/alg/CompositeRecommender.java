package net.hudup.core.alg;

import net.hudup.core.Constants;
import net.hudup.core.alg.cf.NeighborItemBasedCF;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.logistic.Composite;
import net.hudup.core.logistic.xURI;


/**
 * Recommendation strategy is defined as the co-ordination of recommendation algorithms such as collaborative filtering and content-based filtering in accordance with coherent process so as to achieve the best result of recommendation. In simplest form, strategy is identified with a recommendation algorithm. Recommender service is the most complex service because it implements both algorithms and strategies and applies these strategies in accordance with concrete situation.
 * {@link CompositeRecommender} represents the recommendation strategy, called {@code composite recommender}. {@link CompositeRecommender} is combination of other {@link Recommender} algorithms in order to produce the best list of recommended items.
 * So {@link CompositeRecommender} stores a list of internal recommender (s) in its configuration returned from {@link #getConfig()} method.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Composite
public abstract class CompositeRecommender extends Recommender {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * {@link CompositeRecommender} stores a list of internal recommender (s) in its configuration returned from {@link #getConfig()} method.
	 * Such list has a key specified by this constant.
	 */
	public final static String INNER_RECOMMENDER = "inner_recommender";
	
	
	/**
	 * This constant specifies the class of default internal recommender.
	 */
	public final static Class<? extends Recommender> DEFAULT_RECOMMENDER_CLASS = NeighborItemBasedCF.class;

	
	/**
	 * Default constructor.
	 */
	public CompositeRecommender() {
		super();
		
	}
	
	
	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		DataConfig config = super.createDefaultConfig();
		xURI store = xURI.create(Constants.KNOWLEDGE_BASE_DIRECTORY).concat(getName());
		config.setStoreUri(store);
		
		try {
			Recommender defaultRecommender = DEFAULT_RECOMMENDER_CLASS.newInstance();
			defaultRecommender.getConfig().setStoreUri(config.getStoreUri().concat(defaultRecommender.getName()));
			
			config.put(INNER_RECOMMENDER, new AlgList(defaultRecommender));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return config;
	}


	@Override
	public void setup(Dataset dataset, Object... params) throws Exception {
		// TODO Auto-generated method stub
		unsetup();
		
		AlgList recommenders = getInnerRecommenders();
		for (int i = 0; i < recommenders.size(); i++) {
			Recommender recommender = (Recommender) recommenders.get(i);
			recommender.setup(dataset, params);
		}
	}


	@Override
	public void unsetup() {
		// TODO Auto-generated method stub
		super.unsetup();
		
		AlgList recommenders = getInnerRecommenders();
		for (int i = 0; i < recommenders.size(); i++) {
			Recommender recommender = (Recommender) recommenders.get(i);
			recommender.unsetup();
		}
	}

	
	/**
	 * {@link CompositeRecommender} stores a list of internal recommender (s) in its configuration returned from {@link #getConfig()} method.
	 * This method sets such recommender list into the configuration of {@link CompositeRecommender}. 
	 * @param recommenders specified list of recommender (s).
	 */
	protected void setInnerRecommenders(AlgList recommenders) {
		unsetup();
		
		for (int i = 0; i < recommenders.size(); i++) {
			Recommender recommender = (Recommender) recommenders.get(i);
			recommender.getConfig().setStoreUri(getConfig().getStoreUri().concat(recommender.getName()));
		}
		getConfig().put(INNER_RECOMMENDER, recommenders);
	}
	
	
	/**
	 * {@link CompositeRecommender} stores a list of internal recommender (s) in its configuration returned from {@link #getConfig()} method.
	 * This method gets such recommender list from the configuration.
	 * @return list of inner recommender (s).
	 */
	public AlgList getInnerRecommenders() {
		AlgList recommenderList = new AlgList();
		AlgList algList = (AlgList) getConfig().get(INNER_RECOMMENDER);
		if (algList == null || algList.size() == 0)
			return recommenderList;
		
		for (int i = 0; i < algList.size(); i++) {
			Alg alg = algList.get(i);
			if (alg instanceof Recommender)
				recommenderList.add((Recommender)alg);
		}
		
		return recommenderList;
	}
	
	
}





