/**
 * 
 */
package net.hudup.temp;

import java.util.BitSet;
import java.util.List;
import java.util.Map;

import net.hudup.alg.cf.gfall.FreqItemsetBasedCF;
import net.hudup.alg.cf.gfall.FreqItemsetFinder;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.data.bit.BitData;
import net.hudup.data.bit.BitItem;
import net.hudup.data.bit.BitItemset;
import net.hudup.data.bit.BitsetItem;


/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
public class AprioriCF extends FreqItemsetBasedCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public AprioriCF() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	protected FreqItemsetFinder createFreqItemsetFinder() {
		Apriori apriori = new Apriori();
		return apriori;
	}

	
	/**
	 * 
	 * @return {@link String}
	 */
	@Override
	public String getName() {
		return "apriori";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new AprioriCF();
	}


}


/**
 * Apriori class uses classic Apriori algorithm to find itemsets in dataset  

 * @author Loc Nguyen
 * @version 10.0
 * 
 */
@NextUpdate
class Apriori extends FreqItemsetFinder {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor
	 */
	public Apriori() {
		super();
	}

	
	/**
	 * Return a list of {@link BitItemset}.<br>
	 * This class override from abstract class FreqItemSetFinder.
	 * 
	 * 
	 * @return list freqItemset
	 * 
	 */
	@Override
	public List<BitItemset> findFreqItemset() {
		// Giai Tran do here begin Oct 31 2011
		float minsup = config.getAsReal(MIN_SUP);

		List<BitItem> items = preprocess(bitData, minsup);
		List<BitItemset> result = Util.newList();

		AprioriProcess aprioriProcess = new AprioriProcess(bitData, items,
				minsup);
		Map<Integer, List<BitItemset>> mapItemsets = aprioriProcess.process();
		
		for (int i = aprioriProcess.getIterationCount(); i > 1; i--) {
			result.addAll(mapItemsets.get(i));
		}
		return result;
	}

	
	/**
	 * 
	 * @author Giai Tran
	 * 
	 * @version 10.0
	 */
	public class AprioriProcess {

		/**
		 * {@link BitData}
		 */
		private BitData bitData;


		/**
		 * Total frequent {@link BitItemset} with support >= minSup
		 */
		private List<BitItemset> freqItemsets = Util.newList();
	
		
		/**
		 * List of transaction ( each transaction is a session)
		 */
		private List<Integer> originSessionIds = Util.newList();

		
		/**
		 * Map store list of {@link BitItemset} i.e : 1 element 
		 * 			  list {@link BitItemset} 2 elements.<br>
		 * Map key is number of elements in an {@link BitItemset}.<br>
		 * Type map value is list of {@link BitItemset}
		 */
		private Map<Integer, List<BitItemset>> mapFreqItemsets = Util.newMap();

		
		/**
		 * List of elements have support >= min support
		 */
		private List<BitItem> items = Util.newList();

		
		/**
		 * Key of {@link #mapFreqItemsets} in while loop.
		 */
		private int iterationCount;

		
		/**
		 * Check when returning {@link #mapFreqItemsets} is empty and ending while loop
		 */
		private boolean check;

		
		/**
		 * Min suppport
		 */
		private float minSup;

		
		/**
		 * Construct and create map {@link #mapFreqItemsets} which 
		 * having {@link BitItemset} contain one element
		 * 
		 * @param bitData {@link BitData}
		 * @param items	BitItem list of {@link BitItem}
		 * @param minSup min support
		 * 
		 */
		protected AprioriProcess(BitData bitData, List<BitItem> items,
				float minSup) {
			this.bitData = bitData;
			this.originSessionIds = bitData.realSessionIds();
			this.items = items;
			this.iterationCount = 1;
			this.check = true;
			this.minSup = minSup;

			for (BitItem item : items) {

				List<Integer> itemsetIds = Util.newList();
				itemsetIds.add(item.bitItem().getBitItemId());
				BitItemset itemset = new BitItemset(itemsetIds, item.bitItem().getSupport());
				freqItemsets.add(itemset);
			}
			// put list of Itemset with 1 element into mapFreqItemsets
			mapFreqItemsets.put(iterationCount, freqItemsets);
		}

		
		/**
		 * Count set bit of an {@link BitItemset}
		 * 
		 * @param itemset {@link BitItemset}
		 * @return Count set bit of an {@link BitItemset}
		 * 
		 */
		public int countBitset(BitItemset itemset) {
			int countBitset = 0;

			BitSet bitset1 = null;
			BitSet bitset2 = null;
			List<Integer> itemsetIds = itemset.getBitItemIdList();
			int count = 0;
			for (int itemsetId : itemsetIds) {

				BitsetItem bitItem = bitData.get(itemsetId).bitItem();
				bitset2 = bitItem.getBitSet();
				if (count == 0) {

					bitset1 = (BitSet) bitItem.getBitSet().clone();
					count++;
				} else
					bitset1.and(bitset2);

			}
			
			countBitset = DSUtil.countSetBit(bitset1);
			return countBitset;
		}

		
		/**
		 * Calculate and return support of an {@link BitItemset}
		 * 
		 * @param itemset {@link BitItemset}
		 * @return support of an {@link BitItemset}
		 * 
		 */
		public float calculateSupport(BitItemset itemset) {

			return (float) countBitset(itemset) / originSessionIds.size();
		}
		

		/**
		 * Return a list of frequent {@link BitItemset} (s)
		 * 
		 * @return map of frequent {@link BitItemset} (s) 
		 * 
		 */
		public Map<Integer, List<BitItemset>> process() {

			while (check) {
				List<BitItemset> itemsets = mapFreqItemsets.get(iterationCount);

				freqItemsets = Util.newList();

				for (int i = 0; i < itemsets.size(); i++) {

					for (int j = i + 1; j < itemsets.size(); j++) {

						if (iterationCount == 1) {
							List<Integer> itemSetIds = Util.newList();
							itemSetIds.add(items.get(i).bitItem().getBitItemId());
							itemSetIds.add(items.get(j).bitItem().getBitItemId());
							
							BitItemset freqItemset = new BitItemset(itemSetIds);
							float itemsetSupport = calculateSupport(freqItemset);

							if (itemsetSupport >= minSup) {
								freqItemset.setSupport(itemsetSupport);
								freqItemsets.add(freqItemset);
							}
						} else {

							BitItemset itemset1 = itemsets.get(i);
							BitItemset itemset2 = itemsets.get(j);

							List<Integer> element1 = itemset1.getBitItemIdList();
							List<Integer> element2 = itemset2.getBitItemIdList();

							List<Integer> compare1 = element1.subList(0,
									iterationCount - 1);
							List<Integer> compare2 = element2.subList(0,
									iterationCount - 1);

							if (compare(compare1, compare2)) {
								List<Integer> freqItemsetIds = Util.newList();
								freqItemsetIds.addAll(element1);
								freqItemsetIds.add(element2
										.get(iterationCount - 1));

								BitItemset freqItemset = new BitItemset(
										freqItemsetIds);
								float itemsetSupport = calculateSupport(freqItemset);
								if (itemsetSupport >= minSup) {
									freqItemset.setSupport(itemsetSupport);
									freqItemsets.add(freqItemset);
								}
							}
						}

					} // end for j
				}// end for i

				if (!freqItemsets.isEmpty()) {
					iterationCount++;
					mapFreqItemsets.put(iterationCount, freqItemsets);

				} else {
					check = false;
				}

			} // end while

			return mapFreqItemsets;
		}

		
		/**
		 * Return a boolean value after comparing two list of Itemset Ids
		 * 
		 * @param compare1 list of Itemset Ids
		 * @param compare2 list of Itemset Ids
		 * 
		 * @return true when compare1 equal compare2.<br>
		 * 		   false when compare1 not equal compare2
		 */
		public boolean compare(List<Integer> compare1, List<Integer> compare2) {

			boolean compare = false;

			if (compare1.equals(compare2))
				compare = true;

			return compare;
		}

		/**
		 * Return total keys of mapFreqItemsets_ map
		 * 
		 * @return total keys of mapFreqItemsets_ map
		 */
		public int getIterationCount() {
			return iterationCount;
		}

	}


	/**
	 * Return apriori algorithm
	 * @return apriori algorithm
	 */
	@Override
	public String getName() {
		return "apriori";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new Apriori();
	}

	
	
}
