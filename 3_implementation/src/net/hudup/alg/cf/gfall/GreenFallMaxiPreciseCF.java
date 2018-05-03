/**
 * 
 */
package net.hudup.alg.cf.gfall;

import java.util.BitSet;
import java.util.List;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.data.Pair;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.RatingFilter;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
public class GreenFallMaxiPreciseCF extends GreenFallMaxiCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public GreenFallMaxiPreciseCF() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public String getName() {
		return "gfallmaxi_precise";
	}


	@Override
	protected Estimate estimate(RecommendParam param, Set<Integer> queryIds, float filterRatingValue, RatingFilter ratingFilter) {
		FreqItemsetKB fiKb = (FreqItemsetKB)kb;
		
		RatingVector result = param.ratingVector.newInstance(true);

		BitSet A = fiKb.toItemBitSet(param.ratingVector);
		
		List<FreqResult> freqResults = fiKb.getFreqResults();
		int n = Math.min(freqResults.size(), FreqItemsetKB.MAX_FREQ_ITEMSETS);
		for (int i = 0; i < n; i++) {
			FreqResult freq = freqResults.get(i);
			BitSet B = (BitSet)freq.bitset().clone();
			
			// Different only one line here because rating pattern B must contain user rating A instead of finding maximum pattern
			if (!DSUtil.containsSetBit(B, A)) continue;
			B.andNot(A);
			
			int countB = DSUtil.countSetBit(B);
			if (countB == 0) continue;
			
			List<Pair> list = fiKb.toItemPairList(B);
			for (Pair pair : list) {
				if (!pair.isUsed()) continue;
				
				Integer itemId = pair.key();
				if (queryIds != null && !queryIds.contains(itemId)) continue;
				
				float value = pair.value();
				if (!Util.isUsed(filterRatingValue) || ratingFilter == null)
					result.put(itemId, value);
				else if (ratingFilter.accept(value, filterRatingValue))
					result.put(itemId, value);
			}
			
			if (result.size() > 0) {
				return new Estimate(freq, result);
			}
		}
		
		return null;
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new GreenFallMaxiPreciseCF();
	}

	
	
}
