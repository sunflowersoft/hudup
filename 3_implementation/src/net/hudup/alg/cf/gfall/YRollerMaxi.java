/**
 * 
 */
package net.hudup.alg.cf.gfall;

import java.util.BitSet;
import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.logistic.DSUtil;
import net.hudup.data.bit.BitData;
import net.hudup.data.bit.BitItem;
import net.hudup.data.bit.BitItemset;
import net.hudup.data.bit.BitsetItem;

/**
 * YRoller algorithm may lose some frequent itemsets because 
 * there is a case in that some frequent items don’t have 
 * so high a support (they are not excellent items) and 
 * they are in the last of descending ordering. 
 * So they don’t have many chances to join to frequent itemsets. 
 * However they really contribute themselves into some frequent itemset 
 * because they can combine together to build up frequent itemset, 
 * but they don’t make the support of such itemset decreased much. 
 * It is difficult to discover their usefulness. 
 * In order to overcome this drawback, the Roller algorithm is modified 
 * so that such useful items are not ignored. <br>
 * 
 * We called enhanced YRoller algorithm as YRollerMaxi.
 * @see YRoller
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class YRollerMaxi extends YRoller {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor
	 */
	public YRollerMaxi() {
		super();
	}

	
	@Override
	public String getName() {
		return "yrollermaxi";
	}


	@Override
	public RollingStep usingRollingStep() {
		return new RollingStepMaxi();
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new YRollerMaxi();
	}
	
	
}


/**
 * Rolling through {@link BitData} to find out the frequent item set. <br>
 * 
 * However there is enhancement in which the next item is chosen if it contributes 
 * most to frequent itemset. It means that it combines to the previous to get an
 * maximum support
 * 
 * @see RollingStep
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class RollingStepMaxi extends RollingStep {


	/**
	 *
	 * Default constructor
	 */
	public RollingStepMaxi() {
		super();
	}


	/**
	 * Constructor with seed {@link BitItem} and minimum support
	 * @param seedItem seed {@link BitItem}
	 * @param minsup minimum support
	 * 
	 */
	public RollingStepMaxi(BitItem seedItem, float minsup) {
		super(seedItem, minsup);
	}
	
	
	@Override
	public BitItemset roll(BitData dataset, List<Integer> nextItemIds) {
		if (seedItem.bitItem().getSupport() < minsup)
			return null;
		
		// Frequent itemset is initialized by seed item
		BitItemset freqItemset = new BitItemset();
		freqItemset.add(seedItem.getBitItemId());
		freqItemset.setSupport(seedItem.getSupport());
		
		// Accumulated bit item after each rolling is initialized by seed bit item
		BitsetItem accumBitItem = (BitsetItem)seedItem.bitItem();
		
		List<Integer> itemIds = Util.newList();
		itemIds.addAll(nextItemIds);
		// Rolling on the list of item ids
		while (itemIds.size() > 0) {
			// Finding a {@link BitItem} which combined to current {@link BitItem} to 
			// get a maximum support
			BitsetItem max = findMaxSupportBuddy(accumBitItem, dataset, itemIds, minsup);
			if (max == null) break;
			
			// Adding maximum bit item into frequent itemset
			freqItemset.add(max.getBitItemId());
			freqItemset.setSupport(max.getSupport());
			accumBitItem = max;

			itemIds.remove(new Integer(max.getBitItemId()));
		}
		
		return freqItemset;
	}

	
	/**
	 * Finding a {@link BitsetItem} which combined to current {@link BitsetItem} to 
	 * get a maximum support
	 * 
	 * @param currentBitItem current {@link BitsetItem}
	 * @param dataset {@link BitData}
	 * @param itemIdList list of item id
	 * @param minsup minimum support
	 * @return a {@link BitsetItem} which combined to current {@link BitsetItem} to 
	 * get a maximum support
	 */
	protected static BitsetItem findMaxSupportBuddy(
			BitsetItem currentBitItem, 
			BitData dataset, 
			List<Integer> itemIdList, 
			float minsup) {

		int maxItemId = -1;
		float maxSupport = -1;
		BitSet maxAccumBs = null;
		
		int n = dataset.realSessionIds().size();
		for (Integer itemId : itemIdList) {
			BitsetItem nextBitItem = dataset.get(itemId).bitItem();
			
			// Accumulated bit set is computed 
			BitSet accumBs = (BitSet)currentBitItem.getBitSet().clone();
			accumBs.and(nextBitItem.getBitSet());
			float support = (float)DSUtil.countSetBit(accumBs) / (float)n;
			if (support < minsup) continue;
			
			if (maxAccumBs == null || maxSupport < support) {
				maxAccumBs = accumBs;
				maxSupport = support;
				maxItemId = itemId;
			}
		}
		if (maxAccumBs == null) return null;
		
		return BitsetItem.create(maxItemId, maxAccumBs, maxSupport);
	}
	
	
}