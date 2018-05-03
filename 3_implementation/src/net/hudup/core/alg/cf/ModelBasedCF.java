/**
 * 
 */
package net.hudup.core.alg.cf;

import net.hudup.core.alg.ModelBasedRecommender;
import net.hudup.core.alg.Recommender;


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
 * This abstract class represents the model-based recommendation algorithm with collaborative filtering (CF), called {@code model-based CF recommender}.
 * As mentioned, two main methods of any recommender are {@code estimate(...)} and {@code recommend(...)}.
 * This class does not implement such two methods. It almost do nothing.
 * Therefore it is a flag that any class extends it must define such two methods with use of knowledge base represented by {@code KBase} class for collaborative filtering in recommendation task. 
 * In general, {@code model-based CF recommender} uses {@code KBase} to produce a list of recommended items based on collaborative filtering (CF).
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@CFAnnotation
public abstract class ModelBasedCF extends ModelBasedRecommender {


	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public ModelBasedCF() {
		super();
		// TODO Auto-generated constructor stub
	}


}
