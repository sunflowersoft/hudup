/**
 * 
 */
package net.hudup.data.bit;

import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.hudup.core.Cloneable;
import net.hudup.core.data.ItemRating;
import net.hudup.core.data.Pair;
import net.hudup.core.data.RatingVector;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class BitItem implements Cloneable {

	/**
	 * Bit item {@link BitsetItem}
	 */
	protected BitsetItem bitItem = null;
	
	
	/**
	 * The key of {@link Pair} is the real item id, 
	 * the value is the rating value such as 1, 2, 3, 4, 5 
	 */
	protected Pair pair = null;
	
	
	/**
	 * Item constructor
	 * 
	 * @param bitItem {@link BitsetItem}
	 * @param pair {@link Pair}
	 */
	public BitItem(BitsetItem bitItem, Pair pair) {
		this.bitItem = bitItem;
		this.pair = pair;
	}
	
	
	/**
	 * Getting {@link BitsetItem}
	 * @return {@link BitsetItem}
	 */
	public BitsetItem bitItem() {
		return bitItem;
	}
	
	
	/**
	 * Getting {@link Pair}
	 * @return {@link Pair}
	 */
	public Pair pair() {
		return pair;
	}
	

	@Override
	public Object clone() {
		BitsetItem bitItem = (BitsetItem)this.bitItem.clone();
		Pair pair = (Pair)this.pair.clone();
		return new BitItem(bitItem, pair);
	}


	/**
	 * Getting the support of this item
	 * @return the support of this item (also {@link BitsetItem}
	 */
	public float getSupport() {
		return bitItem.getSupport();
	}
	
	
	/**
	 * Getting bit item id
	 * @return bit item id
	 */
	public int getBitItemId() {
		return bitItem.bitItemId;
	}
	
	
	/**
	 * Create rating item {@link RatingVector} from item {@link BitItem}
	 * 
	 * @param bitData {@link BitData}
	 * @return rating item {@link RatingVector}
	 * 
	 */
	public RatingVector toItemRating(BitData bitData) {
		Pair pair =  this.pair;
		
		int realItemId = pair.key();
		RatingVector itemRat = new ItemRating(realItemId);
		
		float value = pair.value();
		BitSet bs = this.bitItem.bs;
		List<Integer> realSessionIds = bitData.realSessionIds();
		for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
			int sessionId = realSessionIds.get(i);
			itemRat.put(sessionId, value);
		}
		
		return itemRat;
	}
	

	/**
	 * Sorting list of {@link BitItem} (s) according to their support
	 * @param items List of {@link BitItem} (s)
	 * @param descending
	 * <ul>
	 * <li>true: bit items are sorted in descending order.</li>
	 * <li>false: bit items are sorted in ascending order.</li>
	 * </ul>
	 * 
	 */
	public static void sortItems(List<BitItem> items, boolean descending) {
		Comparator<BitItem> comparator = null;
		
		if (descending) {
			comparator = new Comparator<BitItem>() {
					
				@Override
				public int compare(BitItem item1, BitItem item2) {
					float support1 = item1.bitItem.getSupport();
					float support2 = item2.bitItem.getSupport();
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
			comparator = new Comparator<BitItem>() {
				
				@Override
				public int compare(BitItem item1, BitItem item2) {
					float support1 = item1.bitItem.getSupport();
					float support2 = item2.bitItem.getSupport();
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
}
