/**
 * 
 */
package net.hudup.data.bit;

import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Cloneable;
import net.hudup.core.Util;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.FetcherUtil;
import net.hudup.core.data.Pair;
import net.hudup.core.data.Profile;
import net.hudup.core.data.Profiles;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.MinMax;
import net.hudup.data.MemProfiles;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class BitData implements Cloneable, net.hudup.core.data.AutoCloseable {
	
	
	/**
	 * Items are translated into bit items. 
	 * For example: item 1 with rating 4 is translated into 9.
	 * So each key is the bit item id (is 9). 
	 */
	private Map<Integer, BitItem> bitItemMap = Util.newMap();

	
	/**
	 * 
	 */
	private Profiles bitItemProfiles = MemProfiles.createEmpty();
	
	
	/**
	 * Real (original) item id list in rating matrix
	 */
	private List<Integer> realItemIds = Util.newList();
	

	/**
	 * Real (original) session (user) id list in rating matrix
	 */
	private List<Integer> realSessionIds = Util.newList();

	
	/**
	 * 
	 */
	private Profiles realSessionProfiles = MemProfiles.createEmpty();
	
	
	/**
	 * Default constructor
	 */
	private BitData() {
		
	}

	
	/**
	 * 
	 * @return set of bit item id (s)
	 */
	public Set<Integer> bitItemIds() {
		// Bit item Id List
		return bitItemMap.keySet();
	}
	
	
	/**
	 * Getting real item id list ({@link Dataset#fetchItemIds()})
	 * @return {@link List}
	 */
	public List<Integer> realItemIdList() {
		return realItemIds;
	}
	
	
	/**
	 * Getting real session (user) user id list ({@link Dataset#fetchUserIds()})
	 * @return list of real session id (s)
	 */
	public List<Integer> realSessionIds() {
		return realSessionIds;
	}

	
	/**
	 * Getting size of {@link BitData}
	 * @return size of bit item (s)
	 */
	public int bitItemCount() {
		return bitItemMap.size();
	}
	
	
	/**
	 * Getting a Item with a specified bit item id
	 * 
	 * @param bitItemId bit item id
	 * 
	 * @return a {@link BitItem} with a specified bit item id
	 * 
	 */
	public BitItem get(int bitItemId) {
		return bitItemMap.get(bitItemId);
	}

	
	/**
	 * Return a Item Collection of itemMap_
	 * 
	 * @return a collection of {@link BitItem}
	 * 
	 */
	public Collection<BitItem> gets() {
		return bitItemMap.values();
	}

	
	/**
	 * 
	 * @param bitItemId bit item id
	 * @return {@link Profile} associated with a bit item id
	 */
	public Profile getBitItemProfile(int bitItemId) {
		return bitItemProfiles.get(bitItemId);
	}
	
	
	/**
	 * 
	 * @param realSessionId real session id
	 * @return {@link Profile} associated with a real session id
	 */
	public Profile getRealSessionProfile(int realSessionId) {
		return realSessionProfiles.get(realSessionId);
	}
	
	
	/**
	 * Finding bit item id of original (real) item id with its value 
	 * (Example: item 2 rated value 5 has bit id 10)
	 * @param originItemId
	 * @param ratingValue
	 * @return bit item id
	 */
	public int findBitItemIdOf(int originItemId, float ratingValue) {
		Set<Integer> bitItemIds = bitItemMap.keySet();
		for (Integer bitItemId : bitItemIds) {
			Pair pair = bitItemMap.get(bitItemId).pair;
			if (pair.key() == originItemId && pair.value() == ratingValue)
				return bitItemId.intValue();
		}
		
		return -1;
	}
	
	
	/**
	 * 
	 */
	public void clear() {
		bitItemMap.clear();
		realItemIds.clear();
		realSessionIds.clear();
		
		bitItemProfiles.clear();
		realSessionProfiles.clear();
	}
	
	
	/**
	 * Getting a list of original items based on bit bit set.
	 * Each {@link Pair} represents a original item with its value.
	 * Each {@link BitSet} represents an list of rated bit items
	 * @param bs
	 * @return list of {@link Pair}
	 */
	public List<Pair> toItemPairList(BitSet bs) {
		List<Pair> result = Util.newList();
		for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
			Pair pair = bitItemMap.get(i).pair;
			result.add(pair);
		}
		
		return result;
	}
	
	
	/**
	 * Getting a bit bit set from real rating vector.
	 * Each {@link BitSet} represents an list of rated bit items
	 * @param vRate
	 * @return {@link BitSet}
	 */
	public BitSet toItemBitSet(RatingVector vRate) {
		List<Pair> inputList = Pair.toPairList(vRate);
		BitSet bs = new BitSet(bitItemMap.size());
	
		for (Pair pair : inputList) {
			if (!pair.isUsed()) continue;
			
			// Finding bit item id
			int bitItemId = findBitItemIdOf(pair.key(), pair.value());
			if (bitItemId >= 0) bs.set(bitItemId);
		}
		
		return bs;
	}
	

	@Override
	public Object clone() {
		BitData bitData = new BitData();
		
		Set<Integer> bitItemIds = this.bitItemIds();
		for (int bitItemId : bitItemIds) {
			BitItem item = this.bitItemMap.get(bitItemId);
			
			BitItem clone = (BitItem)item.clone();
			bitData.bitItemMap.put(bitItemId, clone);
		}
		
		bitData.realItemIds.addAll(this.realItemIds);
		bitData.realSessionIds.addAll(this.realSessionIds);
		
		bitData.bitItemProfiles = (Profiles)this.bitItemProfiles.clone();
		bitData.realSessionProfiles = (Profiles)this.realSessionProfiles.clone();
		
		return bitData;
	}
	
	
	/**
	 * Setting up {@link BitData} based on {@link Dataset}
	 * @param dataset {@link Dataset}
	 */
	private void setup(Dataset dataset) {
		clear();
		
		int autoItemId = 0;
		
		FetcherUtil.fillCollection(this.realItemIds, dataset.fetchItemIds(), true);
		Collections.sort(this.realItemIds);
		
		FetcherUtil.fillCollection(this.realSessionIds, dataset.fetchUserIds(), true);
		Collections.sort(this.realSessionIds);
		
		int minRating = (int)dataset.getConfig().getMinRating();
		int maxRating = (int)dataset.getConfig().getMaxRating();
		for (int originItemId : this.realItemIds) {
			RatingVector vRating = dataset.getItemRating(originItemId);
			if (vRating == null)
				continue;
			
			for (int j= minRating; j <= maxRating; j++) {
				Pair pair = new Pair(originItemId, j);
				
				BitsetItem bitItem = BitsetItem.create(autoItemId, vRating.toRoundValues(), this.realSessionIds, j);
				if (bitItem != null) {
					BitItem item = new BitItem(bitItem, pair);
					this.bitItemMap.put(autoItemId, item);
					
					Profile itemProfile = dataset.getItemProfile(originItemId);
					if (itemProfile != null)
						((MemProfiles)this.bitItemProfiles).put(autoItemId, itemProfile);
					
					autoItemId++;
				}
				
			}
		}
		
		
		for (int realSessionId : this.realSessionIds) {
			Profile userProfile = dataset.getUserProfile(realSessionId);
			if (userProfile != null)
				((MemProfiles)this.realSessionProfiles).put(realSessionId, userProfile);
		}
	}
	
	
	/**
	 * Creating {@link BitData} based on {@link Dataset}
	 * @param dataset {@link Dataset}
	 * @return {@link BitData}
	 */
	public static BitData create(Dataset dataset) {
		BitData result = new BitData();
		result.setup(dataset);
		result.enhance();
		
		return result;
	}


	/**
	 * Getting min and max support
	 * @return {@link MinMax} support
	 */
	public MinMax getMinMaxItemSupport() {
		Collection<BitItem> items = gets();
		if (items.size() == 0) return null;
		
		MinMax minmax = null;
		for (BitItem item : items) {
			float support = item.bitItem.getSupport(); 
			if (minmax == null)
				minmax = new MinMax(support, support);
			else if (support < minmax.min())
				minmax.setMin(support);
			else if (support > minmax.max())
				minmax.setMax(support);
		}
		
		return minmax;
	}

	
	/**
	 * Enhancing speed for later using this data set because 
	 * all internal data is initialized
	 */
	public void enhance() {
		bitItemIds();
		gets();
	}

	
	/**
	 * 
	 * @return {@link BitMatrix}
	 */
	public BitMatrix createBitSessionMatrix() {
		
		List<Integer> rowIdList = Util.newList();
		rowIdList.addAll(realSessionIds);
		
		List<Integer> columnIdList = Util.newList();
		
		byte[][] matrix = new byte[realSessionIds.size()][bitItemCount()];
		Collection<BitItem> items = gets();
		int j = 0;
		for (BitItem item : items) {
			BitSet bs = item.bitItem().getBitSet();
			for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
				matrix[i][j] = 1;
			}
			
			columnIdList.add(item.getBitItemId());
			
			j++;
		}
		
		return BitMatrix.assign(matrix, rowIdList, columnIdList);
	}
	
	
	/**
	 * 
	 * @return {@link BitMatrix}
	 */
	public BitMatrix createBitItemMatrix() {
		
		List<Integer> rowIdList = Util.newList();
		rowIdList.addAll(bitItemIds());
		
		List<Integer> columnIdList = Util.newList();
		columnIdList.addAll(realSessionIds);
		
		byte[][] matrix = new byte[bitItemCount()][realSessionIds.size()];
		for (int i = 0; i < rowIdList.size(); i++) {
			int bitItemId = rowIdList.get(i);
			BitItem item = get(bitItemId);
			
			BitSet bs = item.bitItem().getBitSet();
			for (int j = bs.nextSetBit(0); j >= 0; j = bs.nextSetBit(j + 1)) {
				matrix[i][j] = 1;
			}
		}
		
		return BitMatrix.assign(matrix, rowIdList, columnIdList);
	}

	
	/**
	 * 
	 * @param bitItemIds collection of bit item id (s)
	 * @return {@link BitData}
	 */
	public BitData getSub(Collection<Integer> bitItemIds) {
		Map<Integer, BitItem> bitItemMap = Util.newMap();
		List<Integer> realItemIds = Util.newList();
		List<Integer> realSessionIds = Util.newList();
		
		for (int bitItemId : bitItemIds) {
			BitItem item = this.bitItemMap.get(bitItemId);
			bitItemMap.put(bitItemId, item);
			
			realItemIds.add(item.pair().key());
		}
		
		realSessionIds.addAll(this.realSessionIds);
		
		BitData bitData = new BitData();
		bitData.bitItemMap = bitItemMap;
		bitData.realItemIds = realItemIds;
		bitData.bitItemProfiles = ((MemProfiles)this.bitItemProfiles).getSub(bitItemIds);
		bitData.realSessionIds = realSessionIds;
		bitData.realSessionProfiles = ((MemProfiles)this.realSessionProfiles).getSub(realSessionIds);
		
		return bitData;
	}


	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		clear();
	}


	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		try {
			clear();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	
}

