package net.hudup.core.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.hudup.core.Cloneable;
import net.hudup.core.Util;


/**
 * This class is the pair of key and value. Key is an object indicating anything but value is float number.
 * The key is unusual and so this class is mainly used for sorting objects (as keys) by their values (as float numbers).
 * 
 * @author Loc Nguyen
 *
 * @param <T> type of key.
 * @version 10.0
 */
public class ObjectPair<T> implements Cloneable {
	
	
	/**
	 * Key is an object indicating anything.
	 */
	private Object key = null;
	
	
	/**
	 * Value is float number.
	 */
	private double value = 0;
	
	
	/**
	 * Constructors with specified key and value.
	 * 
	 * @param key specified key.
	 * @param value specified value.
	 */
	public ObjectPair(T key, double value) {
		this.key = key;
		this.value = value;
	}
	
	
	/**
	 * Getting key.
	 * @return key of this pair.
	 */
    @SuppressWarnings("unchecked")
	public T key() {
		return (T)key;
	}
	
	
    /**
     * Getting value associated to key.
     * @return value associated to key.
     */
	public double value() {
		return value;
	}
	
	
	/**
	 * Testing whether this pair is used (valid).
	 * A pair is valid if its key is not null.
	 * @return whether pair is used.
	 */
	public boolean isUsed() {
		return (key != null); 
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object clone() {
		return new ObjectPair(Util.clone(key), value);
	}
	
	
	/**
	 * Getting keys of specified list of pairs.
	 * @param <T> type of keys of pair list.
	 * @param pairList specified list of pairs.
	 * @return list of keys. 
	 */
	public static <T> ArrayList<T> getKeyList(List<ObjectPair<T>> pairList) {
		ArrayList<T> keyList = new ArrayList<T>();
		for (ObjectPair<T> pair : pairList) {
			keyList.add(pair.key());
		}
		
		return keyList;
	}
	
	
	/**
	 * Sorting the list of pairs. In fact, this method sorts keys (as objects) of pairs according to the associated values (as float numbers).
	 * @param <T> type of keys in pairs.
	 * @param pairList list of pairs to be sorted. This is an input and output parameter.
	 * @param maxNumber the maximum number of pairs in the sorted list as result.
	 * If this number is smaller than the number of the original list, the final sorted list is truncated till to this number.
	 * @param descending if {@code true}, the list is sorted according to descending order.
	 */
	public static <T> void sort(List<ObjectPair<T>> pairList, int maxNumber, boolean descending) {
		sort(pairList, descending);
		
		List<ObjectPair<T>> newPairList = new ArrayList<ObjectPair<T>>();
		newPairList.addAll(
				pairList.subList(0, Math.min(maxNumber, pairList.size()))
				);
		
		pairList.clear();
		pairList.addAll(newPairList);
		
	}
	
	
	/**
	 * Sorting the list of pairs. In fact, this method sorts keys (as objects) of pairs according to the associated values (as float numbers).
	 * @param <T> type of keys in pairs.
	 * @param pairList list of pairs to be sorted. This is an input and output parameter.
	 * @param descending if {@code true}, the list is sorted according to descending order.
	 */
	public static <T> void sort(List<ObjectPair<T>> pairList, boolean descending) {
		Comparator<ObjectPair<T>> comparator = null;
		
		if (descending) {
			comparator = new Comparator<ObjectPair<T>>() {
					
				@Override
				public int compare(ObjectPair<T> pair1, ObjectPair<T> pair2) {
					double value1 = pair1.value;
					double value2 = pair2.value;
					
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
			comparator = new Comparator<ObjectPair<T>>() {
				
				@Override
				public int compare(ObjectPair<T> pair1, ObjectPair<T> pair2) {
					double value1 = pair1.value;
					double value2 = pair2.value;
					
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
	

}

