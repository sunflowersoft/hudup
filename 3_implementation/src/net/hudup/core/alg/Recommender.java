/**
 * 
 */
package net.hudup.core.alg;

import java.io.Serializable;
import java.util.Set;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Profile;
import net.hudup.core.data.PropList;
import net.hudup.core.data.RatingVector;


/**
 * This abstract class represents all recommendation algorithms.
 * Its two typical inherited classes are {@code MemoryBasedRecomender} and {@code ModelBasedRecommender}
 * which in turn are abstract classes for memory-based recommendation algorithm and model-based recommendation algorithm.
 * All completed recommendation algorithms must inherit directly this {@link Recommender} or derived indirectly from MemoryBasedRecomender class and ModelBasedRecommender class.
 * These completed recommendation algorithms must implement two main abstract methods {@link #estimate(RecommendParam, Set)} and {@link #recommend(RecommendParam, int)} and two other abstract methods such as {@link #setup(Dataset, Object...)} and {@link #getDataset()}.
 * As a convention, this class and all classes extending it are called {@code recommender}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public abstract class Recommender extends AbstractAlg {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * The filter list contains of filters. Filter specifies tasks which be performed before any actual recommendation tasks.
	 * Concretely, two methods {@link #estimate(RecommendParam, Set)} and {@link #recommend(RecommendParam, int)} require filtering tasks specified by filters of this list.
	 * Suppose every item has types 1, 2, 3, an example of filtering task is to select only type-1 items for recommendation task, which means that the list of recommended items produced by the method {@link #recommend(RecommendParam, int)} later contains only type-1 items.
	 */
	protected FilterList filterList = new FilterList();
	
	
	/**
	 * Default constructor.
	 */
	public Recommender() {
		super();
	}

	
	/**
	 * Setting up this recommender based on specified dataset and additional parameters
	 * 
	 * @param dataset dataset is the main parameter to setup this recommender.
	 * @param params Additional parameters to set up this recommender. This parameter is really an array of sub-parameters.
	 * @exception Exception if any error occurs.
	 */
	public abstract void setup(Dataset dataset, Object... params) throws Exception;
	
	
	/**
	 * Unset this parameter. After this method is called, this recommender cannot be used unless the method {@link #setup(Dataset, Object...)} is called again.
	 */
	public void unsetup() {
		filterList.clear();
		Dataset dataset = getDataset();
		if (dataset != null && dataset.isExclusive())
			dataset.clear();
	}

	
	/**
	 * Getting a list of filters. Filter specifies tasks which be performed before any actual recommendation tasks.
	 * Two methods {@link #estimate(RecommendParam, Set)} and {@link #recommend(RecommendParam, int)} will call this method before estimation task and recommendation task.
	 * Suppose every item have types 1, 2, 3, an example of filtering task is to select only type-1 items for recommendation task, which means that the list of recommended items produced by the method {@link #recommend(RecommendParam, int)} later contains only type-1 items.
	 * @return list of filters.
	 */
	public FilterList getFilterList() {
		return filterList;
	}
	
	
	/**
	 * Any recommender has always a reference to a dataset represented by {@link Dataset} class because recommender is executed on the dataset.
	 * Some recommender (s) store internal dataset but others only have references to dataset.
	 * Anyway, such reference always exists.
	 * @return reference to dataset.
	 */
	public abstract Dataset getDataset();
	
	
	/**
	 * This method is very important, which is used to estimate rating values of given items (users). Any class that extends this abstract class must implement this method.
	 * Note that the role of user and the role of item are exchangeable. Rating vector can be user rating vector or item rating vector. Please see {@link RatingVector} for more details. 
	 * The input parameters are a recommendation parameter and a set of item (user) identifiers.
	 * The output result is a set of predictive or estimated rating values of items (users) specified by the second input parameter.
	 * @param param recommendation parameter. Please see {@link RecommendParam} for more details of this parameter.
	 * @param queryIds set of identifications (IDs) of items that need to be estimated their rating values.
	 * @return rating vector contains estimated rating values of the specified set of IDs of items (users).
	 */
	public abstract RatingVector estimate(RecommendParam param, Set<Integer> queryIds);

	
	/**
	 * This method is the most important one in recommendation task. Any class that extends this abstract class must implement this method.
	 * Note that the role of user and the role of item are exchangeable. Rating vector can be user rating vector or item rating vector. Please see {@link RatingVector} for more details. 
	 * The input parameters are a recommendation parameter and the maximum recommended items (users) in the returned rating vector.
	 * The output result is a list of recommended items (users) which is provided to the user (item).
	 * @param param recommendation parameter. Please see {@link RecommendParam} for more details of this parameter.
	 * @param maxRecommend the maximum recommended items (users) in the returned rating vector.
	 * @return list of recommended items (users) which is provided to the user (item), represented by {@link RatingVector} class. The number of items (users) of such list is specified by the the maximum number.
	 */
	public abstract RatingVector recommend(RecommendParam param, int maxRecommend);

	
	/**
	 * Pre-processing the specified recommendation parameter.
	 * For example, if this recommendation parameter only has user identifier (user ID) but it has no ratings then, this method fills in ratings by reading such ratings from framework database.
	 * @param param recommendation parameter. Please see {@link RecommendParam} for more details of this parameter.
	 * @return new recommendation parameter that is processed from the specified recommendation parameter.
	 */
	protected RecommendParam preprocess(RecommendParam param) {
		if (param == null || param.ratingVector == null)
			return null;
		
		// Pay attention following lines
		Dataset dataset = getDataset();
		if (param.ratingVector.size() == 0) {
			int userId = param.ratingVector.id();
			RatingVector vRating = dataset.getUserRating(userId);
			if (vRating == null || vRating.size() == 0)
				return null;
			param.ratingVector = vRating;
			
		}
		
		if (param.profile == null) {
			Profile profile = dataset.getUserProfile(param.ratingVector.id());
			param.profile = profile;
		}
		
		return param;
	}
	
	
	/**
	 * Getting the parameters of this class as properties list specified by {@link PropList}. 
	 * Such parameters are extracted from the configuration and such configuration in turn is returned by the {@link #getConfig()} method.
	 * @return algorithm parameters extracted from the built-in configuration.
	 */
	public PropList getParameters() {
		DataConfig config = createDefaultConfig();
		config.remove(DataConfig.STORE_URI_FIELD);
		
		PropList parameters = new PropList();
		Set<String> keys = config.keySet();
		for (String key : keys) {
			Serializable value = getConfig().get(key);
			if (value != null)
				parameters.put(key, value);
		}
		
		return parameters;
	}


	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		try {
			unsetup();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
}
