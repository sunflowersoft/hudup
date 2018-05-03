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
 * Roller algorithm.<br>
 * 
 * Our mining frequent itemsets method is based on the assumption: 
 * “The larger the support of an item is, the higher it’s likely that, 
 * this item occurs in some itemsets”. In other words, items with the high 
 * support tend to combine together so as to form a frequent itemset. 
 * So our method  is the heuristic algorithm so-called Roller algorithm. 
 * The basic idea is similar to that of a white-wash task. Suppose you imagine that 
 * there is a wall and there is the dataset (namely, rating matrix) containing all items.
 * Such dataset is modeled as this wall. On the wall, all items are shown 
 * in a descending ordering of their supports; it means that 
 * the higher frequent item is followed by the lower frequent item. 
 * Moreover, we have a roller and we roll it on the wall, from item to item, 
 * with respect to the descending ordering. If an item is found, satisfied at a 
 * minimum support (min_sup), it is, then added to the frequent itemset and the 
 * rolling task is continued to keep moving on, until there is no item that meets 
 * minimum support. The next times, all items in this frequent itemset 
 * (also previous itemset) are removed from the meant wall and 
 * the next rolling task will be performed.
 * 
 * @author Loc Nguyen
 * @version 10.0
 * 
 */
public class YRoller extends FreqItemsetFinder {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor
	 */
	public YRoller() {
		
	}
	
	
	@Override
	public List<BitItemset> findFreqItemset() {
		float minsup = config.getAsReal(MIN_SUP);
		
		// Filtering items which have support lager than minimum support
		List<BitItem> items = FreqItemsetFinder.preprocess(bitData, minsup);
		
		// Result: the list of frequent itemsets
		List<BitItemset> result = Util.newList();
		
		// Whole list of filtered item ids, algorithm performs on this list
		List<Integer> itemIds = getItemIds(items);
		
		while (true) {
			// Only doing when item id list has more than one id
			if (itemIds.size() < 2)
				break;
			
			// Begin with first item id
			int itemId = itemIds.get(0);
			// Initialize with rolling step
			RollingStep step = usingRollingStep();
			step.setup(bitData.get(itemId), minsup);
			
			// Rolling is executed
			itemIds.remove(0); // because of removing beginning item id
			// Frequent itemset
			BitItemset freqItemset = step.roll(bitData, itemIds);
			// Itemset has at least two items
			if (freqItemset == null || freqItemset.size() < 2)
				break;
			
			// Adding frequent itemset into result list
			result.add(freqItemset); 

			// The next rolling step will when the previous frequent itemset 
			// is removed from list of filtered item ids  
			List<Integer> excludeItemIds = freqItemset.getBitItemIdList();
			itemIds.removeAll(excludeItemIds);
			
		}
		
		items.clear();
		items = null;
		
		return result;
	}
	
	
	/**
	 * Which rolling step {@link RollingStep} to be used
	 * @return rolling step {@link RollingStep} to be used
	 */
	public RollingStep usingRollingStep() {
		return new RollingStep();
	}
	

	/**
	 * Getting a list of ids given the list of {@link BitItem}
	 * @param items list of {@link BitItem}
	 * @return list of ids
	 */
	private static List<Integer> getItemIds(List<BitItem> items) {
		List<Integer> ids = Util.newList();
		for (BitItem item : items) {
			ids.add(item.bitItem().getBitItemId());
		}
		
		return ids;
	}


	@Override	
	public String getName() {
		return "yroller";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new YRoller();
	}
	

}



/**
 * Rolling through {@link BitData} to find out the frequent item set
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class RollingStep {
	
	
	/**
	 * Minimum support
	 */
	protected float minsup = 0;

	
	/**
	 * Seed {@link BitItem}
	 * 
	 */
	protected BitItem seedItem = null;
	
	
	/**
	 *
	 * Default constructor
	 */
	public RollingStep() {
		
	}

	
	/**
	 * Constructor with seed {@link BitItem} and minimum support
	 * @param seedItem seed {@link BitItem}
	 * @param minsup minimum support
	 * 
	 */
	public RollingStep(BitItem seedItem, float minsup) {
		setup(seedItem, minsup);
	}
	
	
	/**
	 * Setting up {@link RollingStep} with seed {@link BitItem} and minimum support
	 * 
	 * @param seedItem seed {@link BitItem}
	 * @param minsup minimum support
	 */
	public void setup(BitItem seedItem, float minsup) {
		this.seedItem = seedItem;
		this.minsup = minsup;
	}
	
	
	/**
	 * The main method of YRoller algorithm
	 * Finding an frequent {@link BitItemset} by rolling on {@link BitData}
	 * If BitItem is the same support, it will be added to Itemset 
	 * 
	 * @param dataset {@link BitData}
	 * @param nextItemIds the next list of item ids on which YRoller algorithm perform
	 * @return frequent {@link BitItemset}
	 * 
	 */
	public BitItemset roll(BitData dataset, List<Integer> nextItemIds) {
		if (seedItem.bitItem().getSupport() < minsup)
			return null;
		
		// Frequent itemset is initialized by seed item
		BitItemset freqItemset = new BitItemset();
		freqItemset.add(seedItem.getBitItemId());
		freqItemset.setSupport(seedItem.getSupport());
		
		// Accumulated bit item after each rolling is initialized by seed bit item
		BitsetItem accumBitItem = (BitsetItem)seedItem.bitItem();
		
		// Rolling on the next list of item ids
		int n = dataset.realSessionIds().size();
		for (int nextItemId : nextItemIds) {
			BitsetItem nextItem = dataset.get(nextItemId).bitItem();
			
			BitSet accumBs = (BitSet)accumBitItem.getBitSet().clone();
			accumBs.and(nextItem.getBitSet());
			
			float support = (float)DSUtil.countSetBit(accumBs) / (float)n;
			if (support >= minsup) {
				freqItemset.add(nextItem.getBitItemId());
				freqItemset.setSupport(support);
				
				// Each step updates accumulated item 
				accumBitItem = BitsetItem.create(nextItemId, accumBs, support);
			}
		}
		
		return freqItemset;
	}
	
	
}
