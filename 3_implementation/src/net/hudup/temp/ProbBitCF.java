package net.hudup.temp;

import java.util.Set;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.KBase;
import net.hudup.core.alg.KBaseAbstract;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.cf.ModelBasedCF;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.data.DatasetUtil2;
import net.hudup.data.bit.BitData;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
public class ProbBitCF extends ModelBasedCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public ProbBitCF() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public KBase createKB() {
		// TODO Auto-generated method stub
		return ProbBitKB.create(this);
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "probability_binary";
	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub

		RatingVector result = param.ratingVector.newInstance(true);
		
		ProbBitKB pbKb = (ProbBitKB)kb; 
		BitData bitData = pbKb.getBitData();
		
		int minRating = (int)pbKb.getConfig().getMinRating();
		int n = pbKb.getConfig().getNumberRatingsPerItem();
		for (int queryId : queryIds) {
			int maxIdx = -1;
			float maxProb = -1;
			for (int i = 0; i < n; i++) {
				float rating = DatasetUtil2.realRatingValueOf(i, minRating);
				int bitId = bitData.findBitItemIdOf(queryId, rating);
				
				if (bitId < 0)
					continue;
				
				float prob = bitData.get(bitId).bitItem().getSupport();
				
				if (prob > maxProb) {
					maxProb = prob;
					maxIdx = i;
				}
			}
			
			if (maxIdx != -1)
				result.put(
						queryId, 
						DatasetUtil2.realRatingValueOf(maxIdx, minRating));
		}
		
		if (result.size() == 0)
			return null;
		else
			return result;
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
		return new ProbBitCF();
	}

	
	
}



/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
abstract class ProbBitKB extends KBaseAbstract {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	protected BitData bitData = null;
	
	
	/**
	 * 
	 */
	private ProbBitKB() {
		
	}
	
	
	@Override
	public void load() {
		// TODO Auto-generated method stub
		super.load();
		
		logger.info("Method ProbBitCF.load() not implement yet");
	}

	
	@Override
	public void learn(Dataset dataset, Alg alg) {
		// TODO Auto-generated method stub
		super.learn(dataset, alg);
		
		bitData = BitData.create(dataset);
		
	}

	
	@Override
	public void export(DataConfig storeConfig) {
		// TODO Auto-generated method stub
		
		super.export(storeConfig);
		
		logger.info("Method ProbBitCF.export(DataConfig) not implement yet");
	}

	
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return bitData == null;
	}

	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		super.close();
		
		if (bitData != null) {
			bitData.clear();
			bitData = null;
		}
	}


	/**
	 * 
	 * @return {@link BitData}
	 */
	protected BitData getBitData() {
		return bitData;
	}
	
	
	/**
	 * 
	 * @param cf
	 * @return {@link ProbBitKB}
	 */
	public static ProbBitKB create(final ProbBitCF cf) {
		return new ProbBitKB() {

			
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