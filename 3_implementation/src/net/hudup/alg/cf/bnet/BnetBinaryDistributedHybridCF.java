/**
 * 
 */
package net.hudup.alg.cf.bnet;

import java.util.List;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.KBase;
import net.hudup.core.data.Pair;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.data.bit.BitData;
import net.hudup.logistic.inference.BnetBinaryGraph;
import net.hudup.logistic.inference.BnetBinaryGraphHybrid;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
public class BnetBinaryDistributedHybridCF extends BnetBinaryDistributedExtCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public BnetBinaryDistributedHybridCF() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public KBase createKB() {
		// TODO Auto-generated method stub
		return BnetBinaryDistributedHybridKB.create(this);
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "bayesnet_binary_distributed_hybrid";
	}
	

	@Override
	protected List<Pair> createEvidencePairList(RatingVector vRat, Profile profile) {
		return Pair.toCategoryPairList(vRat, profile);
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new BnetBinaryDistributedHybridCF();
	}
	
	
	
}


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
abstract class BnetBinaryDistributedHybridKB extends BnetBinaryDistributedExtKB {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	@Override
	protected BnetBinaryGraph createBayesGraph(BitData bitData,
			int bitId, float minprob) {
		// TODO Auto-generated method stub
		return BnetBinaryGraphHybrid.create(bitData, bitId, minprob);
	}
	
	
	
	/**
	 * 
	 * @param cf
	 * @return {@link BnetBinaryDistributedHybridKB}
	 */
	public static BnetBinaryDistributedHybridKB create(final BnetBinaryDistributedHybridCF cf) {
		return new BnetBinaryDistributedHybridKB() {

			
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
