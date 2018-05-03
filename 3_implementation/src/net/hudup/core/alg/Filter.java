package net.hudup.core.alg;

import java.io.Serializable;

import net.hudup.core.data.Dataset;



/**
 * This interface represents a filter which is performed before any recommendation tasks.
 * As a convention, this interface is called {@code filter}.
 * Any class that implements this interface must implements two its methods as follows:
 * <br>
 * <ul>
 * <li>
 * Method {@link #filter(Dataset, FilterParam)} is responsible for performing actually filtering tasks before any recommendations.
 * For example, the method {@code Recommender.recommend(...)} will call this method before producing a list of recommended items.
 * Suppose every item have types 1, 2, 3, an example of filtering task is to select only type-1 items for recommendation task, which means that the list of recommended items produced by the method {@code Recommender.recommend(...)} later contains only type-1 items.
 * </li>
 * <li>
 * Method {@link #prepare(RecommendParam)} is responsible for setting up (preparing) the recommendation parameter represented by {@link RecommendParam} before doing the filtering tasks in {@link #filter(Dataset, FilterParam)} method.
 * </li>
 * </ul>
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface Filter extends Serializable {
	
	
	/**
	 * This method is responsible for performing actually filtering tasks before any recommendations.
	 * For example, the method {@code Recommender.recommend(...)} will call this method before producing a list of recommended items.
	 * The filtering tasks are based on specified dataset and specified filter parameter represented by {@link FilterParam} class.
	 * @param dataset specified dataset.
	 * @param param specified filter parameter represented by {@link FilterParam} class.
	 * @return whether or not filtering tasks are performed successfully.
	 */
	boolean filter(Dataset dataset, FilterParam param);
	
	
	/**
	 * This method is responsible for setting up (preparing) the recommendation parameter represented by {@link RecommendParam} class before doing the filtering tasks in {@link #filter(Dataset, FilterParam)} method.
	 * The preparation is based on the input parameter as {@code param}.
	 * @param param recommendation parameter specified by {@link RecommendParam} class.
	 */
	void prepare(RecommendParam param);
	
	
}
