package net.hudup.core.evaluate;

import java.io.Serializable;

import net.hudup.core.alg.Alg;


/**
 * In Hudup framework, there are two kinds of result:
 * <ul>
 * <li>
 * Recommendation result is represented by the output of method {@code Recommender#estimate(...)} or {@code Recommender#recommend(...)}, which is used for recommendation process.
 * The recommendation result is represented by {@code RatingVector} class containing ratings.
 * </li>
 * <li>
 * Evaluation result is represented by this {@code Metric} interface which is the output of evaluation process.
 * It is derived from recommendation result and used for qualifying algorithm. Metric is also called measure.
 * </li>
 * </ul>
 * In general, {@code metric} specifies the final result of algorithm evaluation. For example,
 * MSE, abbreviation of mean squared error, measures the average squared deviation between the predictive rating (from a recommendation algorithm - recommender) and user true rating.
 * MSE is a popular metric. The less the MSE is, the better the recommendation algorithm is.
 * <br>
 * Followings are essential methods of {@code metric}:
 * <ul>
 * <li>
 * Method {@link #recalc(Object...)} is the most important one expressing how to re-calculate a concrete {@code metric}.
 * </li>
 * <li>
 * Methods {@link #getAccumValue()} and {@link #getAccumValue()} return accumulative value and current value of metric.
 * After each time {@code metric} is re-calculated by {@link #recalc(Object...)} method, accumulative value and current value can be changed.
 * For example, a sample {@code metric} receives values 3, 1, 2 at the first, second, and third calculations.
 * At the fourth calculation if {@link #recalc(Object...)} produces value 2, the {@link #getCurrentValue()} will return 2 and the {@link #getAccumValue()} will return 3 + 1 + 2 + 2 = 8.
 * Methods {@link #getAccumValue()} and {@link #getCurrentValue()} can return any thing and so their returned value is represented by {@link MetricValue} interface.
 * How to implement {@code MetricValue} is dependent on concrete application.
 * </li>
 * </ul>
 * The abstract class of {@code Metric} interface is {code AbstractMetric}.
 * Three implemented classes of {@code Metric} which inherit from {@code AbstractMetric} are {@code DefaultMetric}, {@code MetaMetric}, and {@code MetricWrapper} as follows:
 * <ul>
 * <li>{@code DefaultMetric} class is default partial implementation of single {@code Metric}.</li>
 * <li>{@code MetaMetric} class is a complex {@code Metric} which contains other metrics.</li>
 * <li>In some situations, if metric requires complicated implementation, it is wrapped by {@code MetricWrapper} class.</li>
 * </ul>
 * Note, metric is also algorithm because it extends {@link Alg} interface.
 * Therefore it is registered in system register table and identified by its name.
 * Plug-in manager presented by {@code PluginManager} interface discovers automatically all metrics via their names at the booting time.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface Metric extends Alg, Serializable {

	
	/**
	 * Getting description of metric.
	 * @return description of metric.
	 */
	String getDesc();

	
	/**
	 * Getting the type of this metric, for example, &quot;Time&quot;, &quot;Predictive Accuracy&quot;, and &quot;Classification Accuracy&quot; 
	 * @return metric type name
	 */
	String getTypeName();
	
	
	/**
	 * Setting up (Initializing) this metric based on an array of objects known as parameters
	 * @param params specified array of objects known as parameters.
	 */
	void setup(Object... params);
	
	
	/**
	 * Methods {@link #getAccumValue()} and {@link #getAccumValue()} return accumulative value and current value of metric.
	 * After each time {@code metric} is re-calculated by {@link #recalc(Object...)} method, accumulative value and current value can be changed.
	 * For example, a sample {@code metric} receives values 3, 1, 2 at the first, second, and third calculations.
	 * At the fourth calculation if {@link #recalc(Object...)} produces value 2, the {@link #getCurrentValue()} will return 2 and the {@link #getAccumValue()} will return 3 + 1 + 2 + 2 = 8.
	 * Methods {@link #getAccumValue()} and {@link #getCurrentValue()} can return any thing and so their returned value is represented by {@link MetricValue} interface.
	 * How to implement {@code MetricValue} is dependent on concrete application.
	 * @return current {@link MetricValue}
	 */
	MetricValue getCurrentValue();
	
	
	/**
	 * Methods {@link #getAccumValue()} and {@link #getAccumValue()} return accumulative value and current value of metric.
	 * After each time {@code metric} is re-calculated by {@link #recalc(Object...)} method, accumulative value and current value can be changed.
	 * For example, a sample {@code metric} receives values 3, 1, 2 at the first, second, and third calculations.
	 * At the fourth calculation if {@link #recalc(Object...)} produces value 2, the {@link #getCurrentValue()} will return 2 and the {@link #getAccumValue()} will return 3 + 1 + 2 + 2 = 8.
	 * Methods {@link #getAccumValue()} and {@link #getCurrentValue()} can return any thing and so their returned value is represented by {@link MetricValue} interface.
	 * How to implement {@code MetricValue} is dependent on concrete application.
	 * @return accumulated {@link MetricValue}
	 */
	MetricValue getAccumValue();

	
	/**
	 * This is the most important method expressing how to re-calculate a concrete {@code metric} according to specified parameters.
	 * @param params specified array of objects known as parameters. Such parameters varied according to concrete metric.
	 * @return whether calculating successfully
	 * @throws Exception if any error raises.
	 */
	boolean recalc(Object... params) throws Exception;
	
	
	/**
	 * Reseting this metric, which makes this metric in original status.
	 */
	void reset();
	
	
	/**
	 * Testing whether or not this metric is valid.
	 * @return whether or not this metric is valid.
	 */
	boolean isValid();
	
	
}
