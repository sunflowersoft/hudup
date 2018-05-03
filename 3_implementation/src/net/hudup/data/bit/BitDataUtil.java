package net.hudup.data.bit;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetMetadata;
import net.hudup.core.data.Pair;
import net.hudup.core.data.RatingTriple;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.LineProcessor;
import net.hudup.core.parser.TextParserUtil;
import net.hudup.data.SnapshotImpl;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public final class BitDataUtil {

	
	/**
	 * Transform {@link BitData} to {@link Dataset} 
	 * @return {@link Dataset}
	 * 
	 */
	@Deprecated
	public static Dataset transform(BitData bitData) {
		List<RatingTriple> ratList = Util.newList();
		Collection<BitItem> items = bitData.gets();
		
		for (BitItem item : items) {
			RatingVector vRat = item.toItemRating(bitData);
			
			ratList.addAll(RatingTriple.getItemRatings(vRat));
		}
		
		Dataset dataset = SnapshotImpl.create(ratList, new DatasetMetadata()); 
		return dataset;
	}

	
	
	/**
	 * Finding bit item id of original (real) item id with its value
	 * (Example: item 2 rated value 5 has bit id 10)
	 * @param bitItemMap has: key is bit item id and value is Pair (real id, rating value)
	 * @param originItemId
	 * @param ratingValue
	 * @return bit item id
	 */
	public static int findBitItemIdOf(
			Map<Integer, Pair> bitItemMap, 
			int originItemId, 
			float ratingValue) {
		
		Set<Integer> bitItemIds = bitItemMap.keySet();
		for (Integer bitItemId : bitItemIds) {
			Pair pair = bitItemMap.get(bitItemId);
			if (pair.key() == originItemId && pair.value() == ratingValue)
				return bitItemId.intValue();
		}
		
		return -1;
	}

	
	/**
	 * Finding bit item id (s) of original (real) item id
	 * 
	 * @param bitItemMap has: key is bit item id and value is Pair (real id, rating value)
	 * @param originItemId
	 * @return list of bit item id (s)
	 */
	public static List<Integer> findBitItemIdOf(
			Map<Integer, Pair> bitItemMap, 
			int originItemId) {
		
		List<Integer> realItemIdList = Util.newList();
		Set<Integer> bitItemIds = bitItemMap.keySet();
		for (Integer bitItemId : bitItemIds) {
			Pair pair = bitItemMap.get(bitItemId);
			if (pair.key() == originItemId)
				realItemIdList.add(bitItemId);
		}
		
		return realItemIdList;
	}

	
	/**
	 * Getting a bit bit set from real rating vector.
	 * Each {@link BitSet} represents an list of rated bit items
	 * @param vRate {@link RatingVector}
	 * @param bitItemMap {@link Map} has: key is bit item id and value is Pair (real id, rating value)
	 * @return {@link BitSet} of rating vector
	 */
	public static BitSet toItemBitSet(RatingVector vRate,
			Map<Integer, Pair> bitItemMap) {
		List<Pair> inputList = Pair.toPairList(vRate);
		BitSet bs = new BitSet(bitItemMap.size());
	
		for (Pair pair : inputList) {
			if (!pair.isUsed()) continue;
			
			// Finding bit item id
			int bitItemId = findBitItemIdOf(bitItemMap, pair.key(), pair.value());
			if (bitItemId >= 0) bs.set(bitItemId);
		}
		
		return bs;
	}

	
	/**
	 * Getting a list of original items based on bit bit set.
	 * Each {@link Pair} represents a original (real) item with its value.
	 * Each {@link BitSet} represents an list of rated bit items
	 * @param bitItemMap is a map of {@link Pair} having: key is bit item id and value is Pair (real id, rating value)
	 * @param bs is {@link BitSet}
	 * @return list of {@link Pair}
	 */
	public static List<Pair> toItemPairList(Map<Integer, Pair> bitItemMap, BitSet bs) {
		List<Pair> result = Util.newList();
		for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
			Pair pair = bitItemMap.get(i);
			if (pair != null)
				result.add(pair);
		}
		
		return result;
	}

	
	/**
	 * Getting a list of original items based on bit item set.
	 * Each {@link Pair} represents a original (real) item with its value.
	 * 
	 * @param bitItemMap is a map of {@link Pair} having: key is bit item id and value is Pair (real id, rating value)
	 * @param itemset
	 * @return list of {@link Pair}
	 */
	public static List<Pair> toItemPairList(Map<Integer, Pair> bitItemMap, BitItemset itemset) {
		return toItemPairList(bitItemMap, itemset.toBitSet(bitItemMap.size()));
	}
	
	
	/**
	 * 
	 * @param reader
	 * @return map of {@link Pair} has: key is bit item id and value is Pair (real id, rating value)
	 */
	public static Map<Integer, Pair> readBitItemMap(BufferedReader reader) {
		final Map<Integer, Pair> bitItemMap = Util.newMap();
		
		DSUtil.lineProcess(reader, new LineProcessor() {
			
			@Override
			public void process(String line) {
				// TODO Auto-generated method stub
				List<String> list = TextParserUtil.split(line, TextParserUtil.MAIN_SEP, "[\\s]");
				if (list.size() != 2)
					return;
				
				int bitId = Integer.parseInt(list.get(0));
				
				list = TextParserUtil.split(list.get(1), "=", "[\\s]");
				if (list.size() != 2)
					return;
				
				int id = Integer.parseInt(list.get(0));
				float rating = Float.parseFloat(list.get(1));
				bitItemMap.put(bitId, new Pair(id, rating));
			}
		});
		
		return bitItemMap;
	}
	
	
	/**
	 * 
	 * @param bitItemMap is map of {@link Pair} has: key is bit item id and value is Pair (real id, rating value)
	 * @param writer is {@link Writer}
	 */
	public static void writeBitItemMap(Map<Integer, Pair> bitItemMap, PrintWriter writer) {
		Set<Integer> bitIds = bitItemMap.keySet();
		
		for (int bitId : bitIds) {
			Pair pair = bitItemMap.get(bitId);
			writer.println(bitId + " " + TextParserUtil.MAIN_SEP + " " + pair.key() + "=" + pair.value());
		}
		
		writer.flush();
	}
	
	
	
}
