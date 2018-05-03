/**
 * 
 */
package net.hudup.logistic.math;

import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import net.hudup.core.data.Attribute;
import net.hudup.core.data.Profile;
import net.hudup.core.logistic.DSUtil;
import net.hudup.data.bit.BitData;
import net.hudup.data.bit.BitItem;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public final class BitDatasetStatsProcessor {

	
	/**
	 * 
	 */
	protected BitData bitData = null;
	
	
	/**
	 * 
	 * @param bitData
	 */
	public BitDatasetStatsProcessor(BitData bitData) {
		this.bitData = bitData;
	}
	
	
	/**
	 * 
	 * @param bitItemId
	 * @return probability
	 */
	public float prob(int bitItemId) {
		return bitData.get(bitItemId).getSupport();
		
	}
	
	
	/**
	 * 
	 * @param bitItemIds
	 * @return and probability
	 */
	public float probAnd(Collection<Integer> bitItemIds) {
		BitSet bs = null;
		
		for (int bitItemId : bitItemIds) {
			BitItem item1 = bitData.get(bitItemId);
			BitSet bs1 = item1.bitItem().getBitSet();
			
			if (bs == null)
				bs = (BitSet)bs1.clone();
			else
				bs.and(bs1);
		}
		
		return (float)DSUtil.countSetBit(bs) / (float)
				bitData.realSessionIds().size();
	}
	
	
	/**
	 * 
	 * @param bitItemId1
	 * @param bitItemId2
	 * @return and probability
	 */
	public float probAnd(int bitItemId1, int bitItemId2) {
		BitItem item1 = bitData.get(bitItemId1);
		BitSet bs1 = (BitSet)item1.bitItem().getBitSet().clone();
		
		BitItem item2 = bitData.get(bitItemId2);
		BitSet bs2 = item2.bitItem().getBitSet();
		
		bs1.and(bs2);
		
		return (float)DSUtil.countSetBit(bs1) / (float)
				bitData.realSessionIds().size();
	}
	
	
	/**
	 * 
	 * @param bitItemId1
	 * @param bitItemId2
	 * @return and-not probability
	 */
	public float probAndNot(int bitItemId1, int bitItemId2) {
		BitItem item1 = bitData.get(bitItemId1);
		BitSet bs1 = (BitSet)item1.bitItem().getBitSet().clone();
		
		BitItem item2 = bitData.get(bitItemId2);
		BitSet bs2 = item2.bitItem().getBitSet();
		
		bs1.andNot(bs2);
		return (float)DSUtil.countSetBit(bs1) / (float)
				bitData.realSessionIds().size();
	}
	
	


	/**
	 * 
	 * @param sessionNominalAtt
	 * @param sessionNominalAttValue
	 * @param bitItemId
	 * @param occur
	 * @return probability
	 */
	public float probSessionOnBitItem(
			Attribute sessionNominalAtt, 
			int sessionNominalAttValue, 
			int bitItemId,
			boolean occur) {
		
		if (!sessionNominalAtt.isCategory())
			throw new RuntimeException("Attribute not category");
		
		BitItem item = bitData.get(bitItemId);
		BitSet bs = (BitSet)item.bitItem().getBitSet();
		
		int count = 0;
		List<Integer> realSessionIds = bitData.realSessionIds();
		int n = realSessionIds.size();
		for (int i = 0; i < n; i++) {
			if (bs.get(i) != occur)
				continue;
			
			int realSessionId = realSessionIds.get(i);
			Profile realSessionProfile = bitData.getRealSessionProfile(realSessionId);
			if (realSessionProfile == null)
				continue;
			
			int value = realSessionProfile.getValueAsInt(sessionNominalAtt.getIndex());
			
			if (value == sessionNominalAttValue)
				count ++;
		}
		
		return (float)count / (float)n;
	}
	
	
	
	/**
	 * 
	 * @param sessionNominalAtt
	 * @param sessionNominalAttValue
	 * @param bitItemId
	 * @return probability
	 */
	public float probSessionOnBitItem(
			Attribute sessionNominalAtt, 
			int sessionNominalAttValue, 
			int bitItemId) {
		
		return probSessionOnBitItem(sessionNominalAtt, sessionNominalAttValue, bitItemId, true);
	}
	
	
	/**
	 * 
	 * @param sessionNominalAtt
	 * @param sessionNominalAttValue
	 * @param bitItemId
	 * @return probability
	 */
	public float probSessionOnNotBitItem(
			Attribute sessionNominalAtt, 
			int sessionNominalAttValue, 
			int bitItemId) {
		
		return probSessionOnBitItem(sessionNominalAtt, sessionNominalAttValue, bitItemId, false);
	}




}
