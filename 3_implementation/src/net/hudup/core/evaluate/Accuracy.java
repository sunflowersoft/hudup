package net.hudup.core.evaluate;

import java.util.Set;

import net.hudup.core.alg.Recommender;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.RatingVector;


/**
 * Currently, two default metrics inherited from {@link DefaultMetric} class are {@link TimeMetric} class and {@link Accuracy} class.
 * {@link TimeMetric} measures the speed of algorithm and so it is the time in seconds that methods {@link Recommender#estimate(net.hudup.core.alg.RecommendParam, Set)} and {@link net.hudup.core.alg.Recommender#recommend(net.hudup.core.alg.RecommendParam, int)} execute over {@link Dataset}.
 * {@link Accuracy} reflects goodness and efficiency of recommendation algorithms. There are three types of accuracy.
 * <ul>
 * <li>
 * Predictive accuracy, represented by {@link PredictiveAccuracy} class, measures how close predicted ratings returned from methods {@link Recommender#estimate(net.hudup.core.alg.RecommendParam, Set)} and {@link net.hudup.core.alg.Recommender#recommend(net.hudup.core.alg.RecommendParam, int)} are to the true user ratings.
 * {@link PredictiveAccuracy} class derives directly from {@link Accuracy} class. {@code MAE}, {@code MSE}, {@code RMSE} are typical predictive accuracy metrics.
 * </li>
 * <li>
 * Classification accuracy, represented by {@link ClassificationAccuracy} class, measures the frequency that methods {@link Recommender#estimate(net.hudup.core.alg.RecommendParam, Set)} and {@link net.hudup.core.alg.Recommender#recommend(net.hudup.core.alg.RecommendParam, int)} make correct or incorrect recommendation.
 * {@link ClassificationAccuracy} class derives directly from {@link Accuracy} class. {@code Precision} and {@code Recall} are typical classification accuracy metrics.
 * </li>
 * <li>
 * Correlation accuracy, represented by {@link CorrelationAccuracy} class, measures the ability that method {@link net.hudup.core.alg.Recommender#recommend(net.hudup.core.alg.RecommendParam, int)} makes the ordering of recommended items which is similar to the ordering of user&#39;s favorite items.
 * {@link CorrelationAccuracy} class derives directly from {@link Accuracy} class. {@code NDPM}, {@code Spearman}, {@code Pearson} are typical correlation accuracy metrics.
 * </li>
 * </ul>
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class Accuracy extends DefaultMetric {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	

	/**
	 * Default constructor.
	 */
	public Accuracy() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public boolean recalc(Object... params) throws Exception {
		// TODO Auto-generated method stub
		if (params == null || params.length < 2)
			return false;
		
		MetricValue metricValue = null;
		if ( (params[0] instanceof RatingVector) && (params[1] instanceof Dataset))
			metricValue = calc((RatingVector)params[0], (Dataset)params[1]);
		else if ( (params.length > 2) && (params[0] instanceof RatingVector) && (params[1] instanceof RatingVector) && (params[2] instanceof Dataset))
			metricValue = calc((RatingVector)params[0], (RatingVector)params[1], (Dataset)params[2]);
		
		if (metricValue != null && metricValue.isUsed())
			return recalc0(metricValue);
		else
			return false;
	}


	/**
	 * Calculating metric value at an iteration.
	 * 
	 * @param recommended recommended list of items.
	 * @param testing testing dataset.
	 * @return {@link MetricValue} at an iteration.
	 */
	protected MetricValue calc(RatingVector recommended, Dataset testing) {
		// TODO Auto-generated method stub
		
		RatingVector vTesting = testing.getUserRating(recommended.id());
		return calc(recommended, vTesting, testing);
	}


	/**
	 * Calculating metric value at a iteration of algorithm execution.
	 * The calculation is based on comparing recommended rating vector and testing rating vector.
	 * Because this is abstract method, all classes inherits from {@link Accuracy} must define it.
	 * 
	 * @param recommended list of recommended items known as recommended rating vector which is produced by recommendation process.
	 * @param vTesting the testing rating vector which is used to compare with recommended rating vector.
	 * @param testing testing dataset where the testing rating vector is extracted.
	 * @return {@link MetricValue} at a iteration of algorithm execution.
	 */
	protected abstract MetricValue calc(RatingVector recommended, RatingVector vTesting, Dataset testing);


	/**
	 * Testing whether the specified rating value is relevant.
	 * Note, a rating value is relevant if it is larger than or equal to the average rating (rating mean).
	 * The average rating is a half of the deviation {@code {@link DataConfig#getMaxRating()} - {@link DataConfig#getMinRating()}}.
	 * Such {@link DataConfig} is configuration of the specified testing dataset.
	 * @param rating specified rating value.
	 * @param testing specified testing dataset whose maximum rating value and minium rating value are used to calculate the rating average. 
	 * @return {@code true} if specified rating value is relevant. Otherwise, specified rating value is irrelevant.
	 */
	protected static boolean isRelevant(float rating, Dataset testing) {
		
		DataConfig config = testing.getConfig(); 
		float      interval = config.getMaxRating() - config.getMinRating();
		if (rating < interval / 2.0f)
			return false;
		else
			return true;
	}
	
	
	/**
	 * Extracting relevant (irrelevant) ratings from the specified rating vector. Such relevant (irrelevant) ratings form a new rating vector as a result.
	 * This method calls {@link #isRelevant(float, Dataset)} method to test whether a rating value is relevant.
	 * @param vRating specified rating vector from which relevant (irrelevant) ratings are extracted.
	 * @param relevant if {@code true}, the result is vector of relevant ratings. Otherwise, the result is a vector of irrelevant ratings.
	 * @param testing specified testing dataset whose maximum rating value and minium rating value are used to calculate the rating average for relevant testing in method {@link #isRelevant(float, Dataset)}.
	 * @return new rating vector with relevant (irrelevant) ratings.
	 */
	protected static RatingVector extractRelevant(RatingVector vRating, boolean relevant, Dataset testing) {
		RatingVector newRating = vRating.newInstance();
		
		Set<Integer> fieldIds = vRating.fieldIds(true);
		for (int fieldId : fieldIds) {
			float rating = vRating.get(fieldId).value;
			if (isRelevant(rating, testing) == relevant)
				newRating.put(fieldId, rating);
		}
		
		if (newRating.size() == 0)
			return null;
		else
			return newRating;
	}
	
	
	/**
	 * Counting relevant (irrelevant) ratings from the specified rating vector. Such relevant (irrelevant) ratings form a new rating vector as a result.
	 * This method calls {@link #isRelevant(float, Dataset)} method to test whether a rating value is relevant.
	 * @param vRating specified rating vector from which relevant (irrelevant) ratings are counted.
	 * @param relevant if {@code true}, the result is the count of relevant ratings. Otherwise, the result is the count of irrelevant ratings.
	 * @param testing specified testing dataset whose maximum rating value and minium rating value are used to calculate the rating average for relevant testing in method {@link #isRelevant(float, Dataset)}.
	 * @return number of relevant (irrelevant) ratings.
	 */
	protected static int countForRelevant(RatingVector vRating, boolean relevant, Dataset testing) {
		
		Set<Integer> fieldIds = vRating.fieldIds(true);
		int count = 0;
		for (int fieldId : fieldIds) {
			float rating = vRating.get(fieldId).value;
			if (isRelevant(rating, testing) == relevant)
				count ++;
		}
		
		return count;
	}
	
	
	/**
	 * This method prunes the specified recommended vector by removing items from this vector if such items are not rated in the specified testing dataset. 
	 * @param recommended specified recommended vector of ratings.
	 * @param testing specified testing dataset.
	 * @return new rating vector whose items are rated in the specified testing dataset.
	 */
	protected static RatingVector pruneUnrated(RatingVector recommended, Dataset testing) {
		
		RatingVector prune = recommended.newInstance();
		Set<Integer> fieldIds = recommended.fieldIds(true);
		for (int fieldId : fieldIds) {
			RatingVector vRating = testing.getItemRating(fieldId);
			if (vRating != null && vRating.size() > 0)
				prune.put(fieldId, recommended.get(fieldId));
		}
		
		if (prune.size() == 0)
			return null;
		else
			return prune;
	}


}
