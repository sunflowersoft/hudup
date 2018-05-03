/**
 * 
 */
package net.hudup.temp;

import java.io.Serializable;
import java.util.Set;

import net.hudup.core.Constants;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.KBase;
import net.hudup.core.alg.KBaseAbstract;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.cf.ModelBasedCF;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.xURI;
import net.hudup.data.DatasetUtil2;
import net.hudup.data.bit.BitData;
import net.hudup.logistic.math.BinaryProbItemMatrix;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
public class ProbBitBayesCF extends ModelBasedCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public final static String MIN_PROB = "min_prob";

	
	/**
	 * 
	 */
	public final static float MIN_PROB_DEFAULT = 0.01f;

	
	/**
	 * 
	 */
	public ProbBitBayesCF() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public KBase createKB() {
		// TODO Auto-generated method stub
		return ProbBitBayesKB.create(this);
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "probability_binary_bayes";
	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub

		RatingVector result = param.ratingVector.newInstance(true);
		
		ProbBitBayesKB pbbKb = (ProbBitBayesKB)kb; 
		BinaryProbItemMatrix matrix = pbbKb.getMatrix();
		
		int minRating = (int)pbbKb.getConfig().getMinRating();
		int n = pbbKb.getConfig().getNumberRatingsPerItem();
		Set<Integer> itemIdSet = param.ratingVector.fieldIds(true);
		for (int queryId : queryIds) {
			int maxIdx = -1;
			float maxProb = -1;
			for (int i = 0; i < n; i++) {
				float queryRating = DatasetUtil2.realRatingValueOf(i, minRating);
				int bitQueryId = matrix.findBitItemIdOf(queryId, queryRating);
				
				if (bitQueryId < 0)
					continue;
				
				float estimateProb = matrix.get(bitQueryId);
				float a = 1;
				for (int itemId :itemIdSet) {
					int bitItemId = matrix.findBitItemIdOf(itemId, queryRating);
					if (bitItemId < 0 || bitItemId == bitQueryId)
						continue;
					if (!matrix.contains(bitQueryId, bitItemId)) {
						a = 0;
						break;
					}
					else
						a *= matrix.get(bitQueryId, bitItemId) / estimateProb;
				}
				
				
				float prob = a / estimateProb;
				
				if (prob > maxProb) {
					maxProb = prob;
					maxIdx = i;
				}
			}
			
			if (maxIdx != -1) {
				result.put(queryId, DatasetUtil2.realRatingValueOf(maxIdx, minRating));
			}
		}
		
		if (result.size() == 0)
			return null;
		else
			return result;
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		DataConfig config = new DataConfig() {

			
			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean validate(String key, Serializable value) {
				if (key.equals(MIN_PROB))
					return (((Number)value).floatValue() >= 0);
				else
					return super.validate(key, value);
			}
			
		};
		
		config.put(MIN_PROB, MIN_PROB_DEFAULT);
		
		xURI store = xURI.create(Constants.KNOWLEDGE_BASE_DIRECTORY).concat(getName());
		config.setStoreUri(store);
		
		return config;
	}


	@Override
	public RatingVector recommend(RecommendParam param, int maxRecommend) {
		// TODO Auto-generated method stub
		param = preprocess(param);
		if (param == null)
			return null;

		filterList.prepare(param);

		throw new RuntimeException("Not implement yet");
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new ProbBitBayesCF();
	}
	

	
}



/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
abstract class ProbBitBayesKB extends KBaseAbstract {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	protected BinaryProbItemMatrix matrix = null;
	
	
	/**
	 * 
	 */
	private ProbBitBayesKB() {
		
	}
	
	
	@Override
	public void load() {
		// TODO Auto-generated method stub
		super.load();
		
		logger.info("Method ProbBitBayesCF.load() not implement yet");
	}

	
	@Override
	public void learn(Dataset dataset, Alg alg) {
		// TODO Auto-generated method stub
		
		super.learn(dataset, alg);
		
		BitData bitData = BitData.create(dataset);
		matrix = new BinaryProbItemMatrix();
		matrix.setup(bitData);
		
		bitData.clear();
		bitData = null;
	}

	
	@Override
	public void export(DataConfig storeConfig) {
		// TODO Auto-generated method stub
		
		super.export(storeConfig);
		
		logger.info("Method ProbBitBayesCF.export not implement yet");
	}

	
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return matrix == null;
	}

	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		super.close();
		
		if (matrix != null) {
			matrix.clear();
			matrix = null;
		}
	}


	/**
	 * 
	 * @return {@link BinaryProbItemMatrix}
	 */
	protected BinaryProbItemMatrix getMatrix() {
		return matrix;
	}
	
	
	/**
	 * 
	 * @param cf
	 * @return {@link ProbBitBayesKB}
	 */
	public static ProbBitBayesKB create(final ProbBitBayesCF cf) {
		return new ProbBitBayesKB() {

			
			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			
			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return cf.getName();
			}
			
		};
	}
	
	
	
}