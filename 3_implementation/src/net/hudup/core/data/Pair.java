/**
 * 
 */
package net.hudup.core.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import net.hudup.core.Cloneable;
import net.hudup.core.Constants;
import net.hudup.core.Util;


/**
 * This class represents a pair of key and value. Key is integer and value is float number.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Pair implements Cloneable, Serializable {
	
	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Key is integer.
	 */
	protected int key = -1;
	
	
	/**
	 * Value is float number..
	 */
	protected float value = Constants.UNUSED;
	
	
	/**
	 * Constructor with specified key and specified value.
	 * @param key specified key.
	 * @param value specified value.
	 */
	public Pair(int key, float value) {
		this.key = key;
		this.value = value;
	}
	
	
	/**
	 * Getting key of this pair.
	 * @return key of this pair.
	 */
	public int key() {
		return key;
	}
	
	
	/**
	 * Getting value of this pair.
	 * @return value of this pair.
	 */
	public float value() {
		return value;
	}
	
	
	/**
	 * Testing whether this pair is used. The pair is used if its key is not -1 and its value is not {@link Constants#UNUSED}.
	 * @return whether this pair is used.
	 */
	public boolean isUsed() {
		return ( key != -1 && Util.isUsed(value) ); 
	}
	
	
	@Override
	public Object clone() {
		return new Pair(key, value);
	}
	

	/**
	 * This static method finds the index of specified key in the specified list of pairs.
	 * @param key specified key.
	 * @param pairs specified list of pairs.
	 * @return index of specified key in the specified list of pairs.
	 */
	public static int indexOfKey(int key, List<Pair> pairs) {
		for (int i = 0; i < pairs.size(); i++) {
			if (pairs.get(i).key == key)
				return i;
		}
		
		return -1;
	}
	
	
	/**
	 * This static method finds the index of the first pair (in the specified list of pairs)
	 * whose value is less than the specified value.
	 * @param value specified value.
	 * @param pairs specified list of pairs.
	 * @return index of the first pair (in the specified list of pairs) whose value is less than the specified value.
	 */
	public static int findIndexOfLessThan(float value, List<Pair> pairs) {
		for (int j = 0; j < pairs.size(); j++) {
			Pair pair = pairs.get(j);
			if (pair.value() < value)
				return j;
		}
		
		return -1;
	}
	
	
	/**
	 * This static method converts the specified rating vector into the list of pairs.
	 * Each pair contains a rating value together the field ID (item ID or user ID).
	 * @param vRating specified rating vector.
	 * @return {@link List} of {@link Pair} (s) converted from the specified rating vector. 
	 */
	public static List<Pair> toPairList(RatingVector vRating) {
		List<Pair> inputList = Util.newList();

		Set<Integer> fields = vRating.fieldIds(true);
		for (int field : fields) {
			float value = vRating.get(field).value;
			Pair pair = new Pair(field, value);
			inputList.add(pair);
		}
		
		return inputList;
	}
	
	
	/**
	 * Firstly, this static method calls method {@link #toPairList(RatingVector)} to convert the specified rating vector into a pair list {@code L1}.
	 * Secondly, this method converts category values (nominal values of binary values) of the specified profile into the pair list {@code L2}.
	 * Finally the returned list is concatenation of {@code L1} and {@code L2}.
	 *  
	 * @param vRating specified rating vector.
	 * @param profile specified profile.
	 * @return {@link List} of {@link Pair} (s) converted from specified rating vector and specified profile. 
	 */
	public static List<Pair> toCategoryPairList(RatingVector vRating, Profile profile) {
		List<Pair> list = Pair.toPairList(vRating);
		if (profile == null)
			return list;
		
		int n = profile.getAttCount();
		for (int i = 0; i < n; i++) {
			Attribute att = profile.getAtt(i);
			if (!att.isCategory())
				continue;
			
			int index = att.getIndex();
			Pair pair = new Pair(index, (Integer)profile.getValue(index));
			list.add(pair);
		}
		return list;
	}
	
	
	/**
	 * This static method converts the specified rating vector into the list of pairs.
	 * Each pair contains a rating value together the field ID (item ID or user ID).
	 * so that such field ID exists in both the specified rating vector and the specified collection of field IDs.
	 * 
	 * @param vRating specified rating vector
	 * @param commonFields specified collection of field IDs.
	 * @return {@link List} of {@link Pair} (s) converted from the specified rating vector with regard to specified collection of field IDs.
	 */
	public static List<Pair> toPairList(RatingVector vRating, Collection<Integer> commonFields) {
		List<Pair> inputList = Util.newList();

		Set<Integer> fields = vRating.fieldIds(true);
		for (int field : fields) {
			if (commonFields.contains(field)) {
				float value = vRating.get(field).value;
				Pair pair = new Pair(field, value);
				inputList.add(pair);
			}
		}
		
		return inputList;
	}

	
	/**
	 * This static method converts the specified rating vector into the list of pairs.
	 * Each pair contains a rating value together the field ID (item ID or user ID).
	 * so that such rating value is equal to the specified filtering rating value.
	 * @param vRating specified rating vector.
	 * @param filterRatingValue the specified filtering rating value.
	 * @return {@link List} of {@link Pair} converted from the specified rating vector with regard to the specified filtering rating value.
	 */
	public static List<Pair> toPairList(RatingVector vRating, float filterRatingValue) {
		List<Pair> inputList = Util.newList();

		Set<Integer> fields = vRating.fieldIds(true);
		for (int field : fields) {
			float value = vRating.get(field).value;
			if (value == filterRatingValue) {
				Pair pair = new Pair(field, value);
				inputList.add(pair);
			}
		}
		
		return inputList;
	}

	
	/**
	 * This method fills in the specified rating vector by keys and values of the specified pair list.
	 * @param vRating specified rating vector which is output parameter.
	 * @param pairs specified pair list.
	 */
	public static void fillRatingVector(RatingVector vRating, List<Pair> pairs) {
		for (int i = 0; i < pairs.size(); i++) {
			Pair pair = pairs.get(i);
			vRating.put(pair.key(), pair.value());
		}
	}

	
	/**
	 * Getting keys of the specified pair list.
	 * @param pairs specified pair list.
	 * @return set of keys.
	 */
	public static Set<Integer> getKeys(List<Pair> pairs) {
		Set<Integer> keys = Util.newSet();
		
		for (int i = 0; i < pairs.size(); i++)
			keys.add(pairs.get(i).key);
		
		return keys;
	}
	
	
	/**
	 * Getting keys of the specified pair list.
	 * @param pairs specified pair list.
	 * @return {@link List} of keys.
	 */
	public static List<Integer> getKeyList(List<Pair> pairs) {
		List<Integer> keyList = Util.newList();
		
		for (int i = 0; i < pairs.size(); i++)
			keyList.add(pairs.get(i).key);
		
		return keyList;
	}
	
	
	/**
	 * This static method sorts the specified pair list.
	 * @param pairList specified pair list which is both input and output parameter.
	 * @param descending if {@code true}, the specified pair list is sorted according to descending order. Otherwise, it is sorted according ascending order. 
	 */
	public static void sort(List<Pair> pairList, boolean descending) {
		Comparator<Pair> comparator = null;
		
		if (descending) {
			comparator = new Comparator<Pair>() {
					
				@Override
				public int compare(Pair pair1, Pair pair2) {
					float value1 = pair1.value;
					float value2 = pair2.value;
					if (value1 < value2)
						return 1;
					else if (value1 == value2)
						return 0;
					else
						return -1;
				}
			};
		}
		else {
			comparator = new Comparator<Pair>() {
				
				@Override
				public int compare(Pair pair1, Pair pair2) {
					float value1 = pair1.value;
					float value2 = pair2.value;
					if (value1 < value2)
						return -1;
					else if (value1 == value2)
						return 0;
					else
						return 1;
				}
			};
		}
		
		Collections.sort(pairList, comparator);
		
	}

	
	/**
	 * This static method sorts the specified pair list.
	 * Later on, the sorted pair list is truncated by the specified maximum count.
	 * @param pairList specified pair list which is both input and output parameter.
	 * @param descending if {@code true}, the specified pair list is sorted according to descending order. Otherwise, it is sorted according ascending order. 
	 * @param maxCount specified maximum count. If the number of pairs in the final sorted list is more than this maximum count,
	 * this list is truncated so that the number of its elements is equal to this maximum count.
	 */
	public static void sort(List<Pair> pairList, boolean descending, int maxCount) {
		sort(pairList, descending);
		if (maxCount > 0 && pairList.size() > 0) {
			List<Pair> subList = Util.newList();
			subList.addAll(
					pairList.subList(0, Math.min(maxCount, pairList.size()))
				);
			
			pairList.clear();
			pairList.addAll(subList);
		}
		
	}

	
}

