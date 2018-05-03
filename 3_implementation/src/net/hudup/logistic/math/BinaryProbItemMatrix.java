package net.hudup.logistic.math;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.data.Pair;
import net.hudup.data.bit.BitData;
import net.hudup.data.bit.BitDataUtil;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class BinaryProbItemMatrix {
	
	/**
	 * Items are translated into bit items. 
	 * For example: item 1 with rating 4 is translated into 9.
	 * So each key is the bit item id. 
	 */
	protected Map<Integer, Pair> bitItemMap = Util.newMap();
	
	
	/**
	 * 
	 */
	protected Map<Integer, Map<Integer, Float>> matrix = Util.newMap();
	
	
	/**
	 * 
	 */
	public BinaryProbItemMatrix() {
		
	}
	
	
	/**
	 * 
	 * @param bitRowId
	 * @param bitColId
	 * @param prob
	 */
	public void set(int bitRowId, int bitColId, float prob) {
		Map<Integer, Float> row = null;
		if (matrix.containsKey(bitRowId))
			row = matrix.get(bitRowId);
		else {
			row = Util.newMap();
			matrix.put(bitRowId, row);
		}
		row.put(bitColId, prob);
		
		if (bitRowId != bitColId) {
			Map<Integer, Float> col = null;
			if (matrix.containsKey(bitColId))
				col = matrix.get(bitColId);
			else {
				col = Util.newMap();
				matrix.put(bitColId, col);
			}
			col.put(bitRowId, prob);
		}
		
	}
	
	
	/**
	 * 
	 * @param bitRowId
	 * @param bitColId
	 * @return probability
	 */
	public float get(int bitRowId, int bitColId) {
		return matrix.get(bitRowId).get(bitColId);
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
	 * @param bitId
	 * @return probability
	 */
	public float get(int bitId) {
		return matrix.get(bitId).get(bitId);
	}
	
	
	/**
	 * 
	 * @param bitData
	 */
	public void setup(BitData bitData) {
		this.clear();
		
		List<Integer> bitIdList = Util.newList();
		bitIdList.addAll(bitData.bitItemIds());
		
		BitDatasetStatsProcessor processor = 
				new BitDatasetStatsProcessor(bitData);
		int n = bitIdList.size();
		for (int i = 0; i < n; i++) {
			int bitId1 = bitIdList.get(i);
			float prob = processor.prob(bitId1);
			if (prob == 0)
				continue;
			
			this.set(bitId1, bitId1, prob);
			
			for (int j = i + 1; j < n; j++) {
				int bitId2 = bitIdList.get(j);
				float probAnd = processor.probAnd(bitId1, bitId2);
				if (probAnd == 0)
					continue;
				
				this.set(bitId1, bitId2, probAnd);
				
			}
			
			// Setting up bitItemMap_
			this.bitItemMap.put(bitId1, bitData.get(bitId1).pair());
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
	
	
	/**
	 * 
	 * @param realItemId
	 * @param rating
	 * @return bit item id of real item id
	 */
	public int findBitItemIdOf(int realItemId, float rating) {
		return BitDataUtil.findBitItemIdOf(bitItemMap, realItemId, rating);
	}
	
	
	
}



