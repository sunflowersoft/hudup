/**
 * 
 */
package net.hudup.data.bit;

import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.hudup.core.Cloneable;
import net.hudup.core.Util;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.DSUtil;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class BitsetItem implements Cloneable {

	/**
	 * Bit item id of this {@link BitsetItem}
	 */
	protected int bitItemId = -1;
	
	
	/**
	 * {@link BitSet} of {@link BitsetItem}, each bit of it represents a session
	 */
	protected BitSet bs = new BitSet(0);
	
	
	/**
	 * Support of {@link BitsetItem}. 
	 * It is the ratio between the count of set bit (s) and total bits.
	 */
	protected float support = 0;
	
	
	/**
	 * {@link BitsetItem} constructor
	 * @param bitItemId bit item id
	 * @param bs {@link BitSet}
	 * @param support Support of this {@link BitSet}
	 */
	private BitsetItem(int bitItemId, BitSet bs, float support) {
		this.bitItemId = bitItemId;
		this.bs = bs;
		this.support = support;
	}
	
	
	/**
	 * Getting bit item id
	 * @see #bitItemId
	 * @return Bit item id
	 */
	public int getBitItemId() {
		return bitItemId;
	}
	
	
	/**
	 * Getting {@link BitSet}
	 * @see BitsetItem#bs
	 * @return {@link BitSet}
	 */
	public BitSet getBitSet() {
		return bs;
	}
	
	
	/**
	 * Getting the support
	 * @see #support
	 * @return The support of this
	 */
	public float getSupport() {
		return support;
	}
	
	
	/**
	 * Calculate number of set bit of {@link BitSet}
	 * @return number of occurrences of BitItem BitSet 
	 * 
	 */
	public int countSetBit() {
		return DSUtil.countSetBit(bs);
	}
	

	@Override
	public Object clone() {
		return new BitsetItem(bitItemId, (BitSet)bs.clone(), support);
	}


	
	/**
	 * Sorting list of {@link BitsetItem} (s) according to their support.
	 * @param items List of {@link BitsetItem} (s).
	 * @param descending
	 * <ul>
	 * <li>true: bitset items are sorted in descending order.</li>
	 * <li>false: bitset items are sorted in ascending order.</li>
	 * </ul>
	 * 
	 */
	public static void sortItems(List<BitsetItem> items, boolean descending) {
		Comparator<BitsetItem> comparator = null;
		
		if (descending) {
			comparator = new Comparator<BitsetItem>() {
					
				@Override 
				public int compare(BitsetItem item1, BitsetItem item2) {
					float support1 = item1.getSupport();
					float support2 = item2.getSupport();
					if (support1 < support2)
						return 1;
					else if (support1 == support2)
						return 0;
					else
						return -1;
				}
			};
		}
		else {
			comparator = new Comparator<BitsetItem>() {
				
				@Override
				public int compare(BitsetItem item1, BitsetItem item2) {
					float support1 = item1.getSupport();
					float support2 = item2.getSupport();
					if (support1 < support2)
						return -1;
					else if (support1 == support2)
						return 0;
					else
						return 1;
				}
			};
		}
		
		Collections.sort(items, comparator);
	}


	/**
	 * Creating a new one
	 * 
	 * @param bitItemId bit item id
	 * @param bs {@link BitSet}
	 * @param support the support of this
	 * 
	 * @return new one
	 */
	public static BitsetItem create(int bitItemId, BitSet bs, float support) {
		return new BitsetItem(bitItemId, bs, support);
	}
	
	
	/**
	 * Creating one based on item rating vector {@link RatingVector}
	 * 
	 * @param bitItemId bit item id
	 * @param vRating item rating vector {@link RatingVector}
	 * @param columnPattern the pattern of bit set because bit set has fixed length
	 * @param ratingValue rating values such as 1, 2, 3, 4, 5
	 * @return new one
	 * 
	 */
	public static BitsetItem create(
			int bitItemId, 
			RatingVector vRating, 
			List<Integer> columnPattern, 
			float ratingValue) {
		
		List<Float> list = vRating.toValueList(columnPattern);
		BitSet bs = new BitSet(list.size());
		int count = 0;
		for (int i = 0; i < list.size(); i++) {
			float value = list.get(i);
			
			if (Util.isUsed(value) && value == ratingValue) {
				bs.set(i);
				count ++;
			}
		}
		
		if (count == 0)
			return null;
		else
			return new BitsetItem(bitItemId, bs, (float)count / (float)list.size());
	}
	
	
	
}
