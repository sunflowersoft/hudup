/**
 * 
 */
package net.hudup.core.alg;

import net.hudup.core.alg.Recommender;


/**
 * Two typical inherited classes of {@link Recommender} are {@code MemoryBasedRecomender} and {@code ModelBasedRecommender}
 * which in turn are abstract classes for memory-based recommendation algorithm and model-based recommendation algorithm.
 * As a convention, this class is called memory-based recommender.
 * This memory-based recommender makes recommendation tasks directly on dataset represented by {@code Dataset} class.
 * It often stores rating matrix in memory. Recommendation tasks are executed in memory.
 * Memory-based recommender is often simpler than model-based recommender.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class MemoryBasedRecommender extends Recommender {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public MemoryBasedRecommender() {
		super();
		// TODO Auto-generated constructor stub
	}

	
}
