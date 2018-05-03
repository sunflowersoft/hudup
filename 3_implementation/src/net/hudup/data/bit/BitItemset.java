/**
 * 
 */
package net.hudup.data.bit;

import java.io.Serializable;
import java.util.BitSet;
import java.util.List;

import net.hudup.core.Cloneable;
import net.hudup.core.Util;
import net.hudup.core.data.Pair;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class BitItemset implements Cloneable, TextParsable, Serializable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * List of bit item id. 
	 * An {@link BitItemset} contains many {@link BitsetItem}) and 
	 * each {@link BitsetItem} has an bit item id
	 * 
	 */
	protected List<Integer> bitItemIdList = Util.newList();
	
	
	/**
	 * The support of {@link BitItemset}
	 */
	protected float support = 0;
	
	
	/**
	 * Default constructor
	 */
	public BitItemset() {
		
	}

	
	/**
	 * Constructor with a list of bit item id (s)
	 * @param bitItemIdList list of bit item id (s)
	 */
	public BitItemset(List<Integer> bitItemIdList) {
		this.bitItemIdList.addAll(bitItemIdList);
	}
	
	/**
	 * Constructor with a list of bit item id (s) and support
	 * @param bitItemIdList list of bit item id (s)
	 * @param support the support of this item
	 */
	public BitItemset(List<Integer> bitItemIdList, float support) {
		this.bitItemIdList.addAll(bitItemIdList);
		this.support = support;
	}
	
	/**
	 * Getting a list of bit item ids
	 * 
	 * @return a list of bit item ids 
	 */
	public List<Integer> getBitItemIdList() {
		return bitItemIdList;
	}
	
	
	/**
	 * Getting the support of this item
	 * 
	 * @return the support of this item
	 */
	public float getSupport() {
		return support;
	}
	
	
	/**
	 * Setting the support of this item
	 * 
	 * @param support is the of this item
	 */
	public void setSupport(float support) {
		this.support = support;
	}
	
	
	/**
	 * Appends the specified bit item id to the end of bit item id list 
	 * 
	 * @param bitItemId bit item id
	 * 
	 */
	public void add(int bitItemId) {
		if (bitItemIdList.contains(bitItemId))
			return;
		bitItemIdList.add(bitItemId);
	}
	
	
	/**
	 * Getting the bit item id at the specified position in bit item id list
	 * 
	 * @param idx the specified index in bit item id list
	 * 
	 * @return the bit item id at the specified index in bit item id list
	 * 
	 */
	public int get(int idx) {
		return bitItemIdList.get(idx);
	}
	
	
	/**
	 * Replaces the bit item at the specified position in bit item id list 
	 * with the specified id 
	 * 
	 * @param idx the specified index in bit item id list
	 * 
	 * @param bitItemId bit item id to be stored at the specified position
	 * 
	 */
	public void set(int idx, int bitItemId) {
		bitItemIdList.set(idx, bitItemId);
	}
	
	
	/**
	 * Removes the bit item id at the specified position in bit item id list
	 * 
	 * @param idx the index of the bit item id to be removed
	 * 
	 */
	public void remove(int idx) {
		bitItemIdList.remove(idx);
	}
	
	
	/**
	 * Getting the size of this item
	 * 
	 * @return the size of this item
	 */
	public int size() {
		return bitItemIdList.size();
	}
	
	
	/**
	 * Clearing this item
	 */
	public void clear() {
		bitItemIdList.clear();
		support = 0;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		BitItemset other = (BitItemset)obj;
		if (other.bitItemIdList.size() != this.bitItemIdList.size())
			return false;
		
		for (Integer bitItem : this.bitItemIdList) {
			if (!other.bitItemIdList.contains(bitItem))
				return false;
		}
		
		return true;
	}


	/**
	 * Converting this item to {@link BitSet}
	 * @param bitsetSize size of {@link BitSet}
	 * @return the {@link BitSet} of this item
	 * 
	 */
	public BitSet toBitSet(int bitsetSize) {
		BitSet bs = new BitSet(bitsetSize);
		
		for (int bitItemId : bitItemIdList) {
			bs.set(bitItemId);
		}
		
		return bs;
	}
	
	
	/**
	 * Getting index of bit item id
	 * @param bitItemId is bit item id
	 * @return index of bit item id
	 */
	public int indexOf(int bitItemId) {
		return bitItemIdList.indexOf(new Integer(bitItemId));
	}
	
	
	/**
	 * Getting index of real item id in list of bit item id (s) bitItemIdList_ of {@link BitData}
	 * @param realItemId is real item id
	 * @param bitData binary dataset {@link BitData}
	 * @return index of real item id
	 */
	public int indexOfReal(int realItemId, BitData bitData) {
		for (int i = 0; i < bitItemIdList.size(); i++) {
			int bitItemId = bitItemIdList.get(i);
			
			BitItem item = bitData.get(bitItemId);
			if (item == null)
				continue;
			
			if (item.pair.key() == realItemId)
				return i;
		}
		
		return -1;
	}
	
	
	/**
	 * Converting this item to list of Pair(real item id, value)
	 * 
	 * @param bitData {@link BitData}
	 * @return list of {@link Pair}
	 * 
	 */
	public List<Pair> toItemPairList(BitData bitData) {
		List<Pair> result = Util.newList();
		
		for (int bitItemId : bitItemIdList) {
			BitItem item = bitData.get(bitItemId);
			if (item != null)
				result.add(item.pair);
		}
		
		return result;
	}

		
	@Override
	public Object clone() {
		BitItemset is = new BitItemset(bitItemIdList);
		is.setSupport(support);
		return is;
	}

	
	
	@Override
	public String toString() {
		return toText();
	}
	
	
	
	@Override
	public String toText() {
		return TextParserUtil.toText(bitItemIdList, ",") + " " + TextParserUtil.MAIN_SEP + " " + support;
	}
	
	
	@Override
	public void parseText(String spec) {
		/*
		 * Parsing string description into {@link BitItemset}
		 * In form "bit item id list : support"
		 * @param spec {@link BitItemset} specification
		 * @return {@link BitItemset}
		 */
		
		String[] array = spec.split(TextParserUtil.MAIN_SEP);
		
		List<Integer> bitItemIdList = TextParserUtil.parseListByClass(array[0], Integer.class, ",");
		float support = Float.parseFloat(array[1]);
		
		this.bitItemIdList = bitItemIdList;
		this.support = support;
	}
	
	
}
