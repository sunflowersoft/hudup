package net.hudup.alg.hybrid;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.alg.KBase;
import net.hudup.core.alg.KBaseAbstract;
import net.hudup.core.alg.cf.ModelBasedCF;
import net.hudup.core.logistic.NextUpdate;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
public abstract class BayesFunctionalHybrid extends ModelBasedCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public BayesFunctionalHybrid() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public KBase createKB() {
		// TODO Auto-generated method stub
		return BayesFunctionalHybridKB.create(this);
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "bayes_functional";
	}

	
}



/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
abstract class BayesFunctionalHybridKB extends KBaseAbstract {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public static final int MAX_USER_FEATURES = 10;
	
	
	/**
	 * 
	 */
	public static final int MAX_ITEM_FEATURES = 10;

	
	/**
	 * 
	 */
	protected Map<Integer, List<Integer>> uFeatureValues = Util.newMap();

	
	/**
	 * 
	 */
	protected Map<Integer, List<Integer>> iFeatureValues = Util.newMap();

	
	/**
	 * 
	 */
	protected Map<Integer, Map<Integer, Map<Integer, Float>>> uFeatureParams = Util.newMap();
	
	
	/**
	 * 
	 */
	protected Map<Integer, Map<Integer, Map<Integer, Float>>> iFeatureParams = Util.newMap();


	/**
	 * 
	 */
	protected Map<Integer, Float> rParams = Util.newMap();
	
	
	/**
	 * 
	 */
	public BayesFunctionalHybridKB() {
		
	}
	
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		super.close();
		destroyDataStructure();
	}

	
	/**
	 * 
	 * @return list user feature id (s)
	 */
	protected Set<Integer> uFeatureIds() {
		return uFeatureValues.keySet();
	}
	
	
	/**
	 * 
	 * @return list item feature id (s)
	 */
	protected Set<Integer> iFeatureIds() {
		return iFeatureValues.keySet();
	}

	
	/**
	 * 
	 * @return possible rating (s)
	 */
	protected Set<Integer> ratings() {
		return rParams.keySet();
	}
	
	
	/**
	 * 
	 */
	private void destroyDataStructure() {
		uFeatureValues.clear();
		iFeatureValues.clear();
		uFeatureParams.clear();
		iFeatureParams.clear();
		rParams.clear();
	}


	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return uFeatureParams.size() == 0 || iFeatureParams.size() == 0;
	}
	
	
	/**
	 * 
	 * @param innerCf
	 * @return {@link BayesFunctionalHybrid}
	 */
	public static BayesFunctionalHybridKB create(final BayesFunctionalHybrid recommender) {
		
		return new BayesFunctionalHybridKB() {

			
			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			
			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return recommender.getName();
			}
		};
	}


}
