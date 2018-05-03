package net.hudup.logistic.math;

import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.data.Pair;
import net.hudup.core.logistic.DSUtil;
import net.hudup.data.bit.BitData;
import net.hudup.data.bit.BitItem;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class BinaryAndNotProbItemMatrix {

	
	/**
	 * 
	 * @author Loc Nguyen
	 * @version 10.0
	 */
	public class Prob {
		
		/**
		 * 
		 */
		public float and = 0;
		
		
		/**
		 * 
		 */
		public float andNot = 0;
		
		
		/**
		 * 
		 * @param and
		 * @param andNot
		 */
		public Prob(float and, float andNot) {
			this.and = and;
			this.andNot = andNot;
		}
	}
	
	
	/**
	 * Items are translated into bit items. 
	 * For example: item 1 with rating 4 is translated into 9.
	 * So each key is the bit item id. 
	 */
	protected Map<Integer, Pair> bitItemMap = Util.newMap();
	
	
	/**
	 * 
	 */
	protected Map<Integer, Map<Integer, Prob>> matrix = Util.newMap();
	
	
	/**
	 * 
	 */
	public BinaryAndNotProbItemMatrix() {
		
	}
	
	
	/**
	 * 
	 * @param bitRowId
	 * @param bitColId
	 * @param prob
	 */
	public void set(int bitRowId, int bitColId, Prob prob) {
		Map<Integer, Prob> row = null;
		if (matrix.containsKey(bitRowId))
			row = matrix.get(bitRowId);
		else {
			row = Util.newMap();
			matrix.put(bitRowId, row);
		}
		row.put(bitColId, prob);
		
		
	}
	
	
	/**
	 * 
	 * @param bitRowId
	 * @param bitColId
	 * @return {@link Prob}
	 */
	public Prob get(int bitRowId, int bitColId) {
		return matrix.get(bitRowId).get(bitColId);
	}

	
	/**
	 * 
	 * @param bitRowId
	 * @param bitColId
	 * @return probability
	 */
	public float getAndProb(int bitRowId, int bitColId) {
		return get(bitRowId, bitColId).and;
	}
	
	
	/**
	 * 
	 * @param bitRowId
	 * @param bitColId
	 * @return probability
	 */
	public float getAndNotProb(int bitRowId, int bitColId) {
		return get(bitRowId, bitColId).andNot;
	}

	
	/**
	 * 
	 * @param bitId
	 * @return {@link Prob}
	 */
	public Prob get(int bitId) {
		return matrix.get(bitId).get(bitId);
	}
	
	
	/**
	 * 
	 * @param bitId
	 * @return probability
	 */
	public float getProb(int bitId) {
		return get(bitId).and;
	}
	
	
	/**
	 * 
	 * @param bitRowId
	 * @param bitColId
	 * @return whether contains bit row id and bit column id
	 */
	public boolean contains(int bitRowId, int bitColId) {
		return (matrix.containsKey(bitRowId) && 
				matrix.get(bitRowId).containsKey(bitColId));
	}
	
	
	/**
	 * 
	 * @param bitData
	 */
	public void setup(BitData bitData) {
		this.clear();
		
		List<Integer> bitIdList = Util.newList();
		bitIdList.addAll(bitData.bitItemIds());
		
		int n = bitIdList.size();
		int sessionSize = bitData.realSessionIds().size();
		for (int i = 0; i < n; i++) {
			int bitId1 = bitIdList.get(i);
			BitItem item1 = bitData.get(bitId1);
			BitSet bs1 = item1.bitItem().getBitSet();
			float prob = item1.getSupport();
			if (prob == 0)
				continue;
			
			this.set(bitId1, bitId1, new Prob(prob, 0));
			
			for (int j = 0; j < n; j++) {
				if (j == i)
					continue;
				
				int bitId2 = bitIdList.get(j);
				BitItem item2 = bitData.get(bitId2);
				BitSet bs2 = item2.bitItem().getBitSet();
				
				float andProb = 0;
				if (contains(bitId2, bitId1)) {
					andProb = getAndProb(bitId2, bitId1);
				}
				else {
					BitSet bs = (BitSet)bs1.clone();
					bs.and(bs2);
					andProb = (float)DSUtil.countSetBit(bs) / (float)sessionSize;
				}
				if (andProb == 0)
					continue;
				
				BitSet bs = (BitSet)bs1.clone();
				bs.andNot(bs2);
				float andNotProb = (float)DSUtil.countSetBit(bs) / (float)sessionSize;
				
				this.set(bitId1, bitId2, new Prob(andProb, andNotProb));
				
			}
			
			// Setting up bitItemMap_
			this.bitItemMap.put(bitId1, item1.pair());
		}
		
		
	}

	
	/**
	 * 
	 */
	public void clear() {
		this.bitItemMap.clear();
		this.matrix.clear();
	}
	
	
	/**
	 * 
	 * @return set of bit id (s)
	 */
	public Set<Integer> bitIds() {
		return bitItemMap.keySet();
	}
	
	
	/**
	 * 
	 * @param bitId
	 * @return {@link Pair}
	 */
	public Pair getPair(int bitId) {
		return bitItemMap.get(bitId);
	}
	
	
	/**
	 * 
	 * @return map of {@link Pair}
	 */
	public Map<Integer, Pair> transferBitItemMap() {
		Map<Integer, Pair> bitItemMap = Util.newMap();
		bitItemMap.putAll(this.bitItemMap);
		
		return bitItemMap;
	}
	
	

}
