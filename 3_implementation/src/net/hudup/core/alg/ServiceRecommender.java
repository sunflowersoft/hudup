package net.hudup.core.alg;



/**
 * This is not real recommender (recommendation algorithm). Actually, it points to another real recommender.
 * This service recommender is very useful in client-server architecture.
 * For example, you define a service recommender referring to another real recommendation algorithm in order to receive recommended items remotely. 
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class ServiceRecommender extends Recommender {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor
	 */
	public ServiceRecommender() {
		// TODO Auto-generated constructor stub
	}

}
