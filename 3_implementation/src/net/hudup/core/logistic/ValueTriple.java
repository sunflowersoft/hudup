package net.hudup.core.logistic;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.hudup.core.Cloneable;
import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.RatingVector;


/**
 * This class represents a triple which has one integer key {@link #key} and two float values such as {@link #value1} and {@link #value2}.
 * The @link #value1} and {@link #value2} are also called 1-value and 2-value, respectively.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ValueTriple implements Cloneable {
	
	
	/**
	 * The key
	 */
	protected int key = -1;
	
	
	/**
	 * The first value, also called 1-value.
	 */
	protected float value1 = Constants.UNUSED;
	
	
	/**
	 * The second value, also called 2-value.
	 */
	protected float value2 = Constants.UNUSED;
	
	
	/**
	 * Constructor with a key and two values.
	 * @param key specified key.
	 * @param value1 first value.
	 * @param value2 second value.
	 */
	public ValueTriple(int key, float value1, float value2) {
		this.key = key;
		this.value1 = value1;
		this.value2 = value2;
	}
	
	
	/**
	 * Getting the key of this triple.
	 * @return key of this triple.
	 */
	public int key() {
		return this.key;
	}
	
	
	/**
	 * Getting the first value.
	 * @return value 1.
	 */
	public float getValue1() {
		return this.value1;
	}
	
	
	/**
	 * Setting the first value by specified value.
	 * @param value1 specified value.
	 */
	public void setValue1(float value1) {
		this.value1 = value1;
	}
	
	
	/**
	 * Getting the second value.
	 * @return second value (value 2).
	 */
	public float getValue2() {
		return this.value2;
	}
	
	
	/**
	 * Setting the second value by specified value.
	 * @param value2 specified value.
	 */
	public void setValue2(float value2) {
		this.value2 = value2;
	}

	
	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return new ValueTriple(key, value1, value2);
	}


	/**
	 * Given a list of value triples, this static method find the triple that has the specified key.
	 * @param triples {@link List} of value triples. 
	 * @param key specified key.
	 * @return {@link ValueTriple} that has the specified key.
	 */
	public static ValueTriple getByKey(List<ValueTriple> triples, int key) {
		for (ValueTriple triple : triples) {
			if (triple.key == key)
				return triple;
		}
		
		return null;
	}
	
	
	/**
	 * This static method fills in the specified rating vector by 1-values of the specified list of triples.
	 * @param vRating specified rating vector. This is output parameter filled in by 1-values of the specified list of triples.
	 * @param triples specified list of triples.
	 */
	public static void fillByValue1(RatingVector vRating, List<ValueTriple> triples) {
		for (ValueTriple triple : triples) {
			vRating.put(triple.key, triple.value1);
		}
	}

	
	/**
	 * This static method fills in the specified rating vector by 2-values of the specified list of triples.
	 * @param vRating specified rating vector. This is output parameter filled in by 2-values of the specified list of triples.
	 * @param triples specified list of triples.
	 */
	public static void fillByValue2(RatingVector vRating, List<ValueTriple> triples) {
		for (ValueTriple triple : triples) {
			vRating.put(triple.key, triple.value2);
		}
	}

	
	/**
	 * This static method sorts the specified list of triples according to its 1-values.
	 * @param tripleList specified list of triples. This is both input and output parameter.
	 * @param descending if {@code true}, the specified list sorted according to descending order. Otherwise, the list is sorted according ascending order.
	 */
	public static void sortByValue1(List<ValueTriple> tripleList, boolean descending) {
		Comparator<ValueTriple> comparator = null;
		
		if (descending) {
			comparator = new Comparator<ValueTriple>() {
					
				@Override
				public int compare(ValueTriple triple1, ValueTriple triple2) {
					float value1 = triple1.value1;
					float value2 = triple2.value1;
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
			comparator = new Comparator<ValueTriple>() {
				
				@Override
				public int compare(ValueTriple triple1, ValueTriple triple2) {
					float value1 = triple1.value1;
					float value2 = triple2.value1;
					if (value1 < value2)
						return -1;
					else if (value1 == value2)
						return 0;
					else
						return 1;
				}
			};
		}
		
		Collections.sort(tripleList, comparator);
		
	}

	
	/**
	 * This static method sorts the specified list of triples according to its 2-values.
	 * @param tripleList specified list of triples. This is both input and output parameter.
	 * @param descending if {@code true}, the specified list sorted according to descending order. Otherwise, the list is sorted according ascending order.
	 */
	public static void sortByValue2(List<ValueTriple> tripleList, boolean descending) {
		Comparator<ValueTriple> comparator = null;
		
		if (descending) {
			comparator = new Comparator<ValueTriple>() {
					
				@Override
				public int compare(ValueTriple triple1, ValueTriple triple2) {
					float value1 = triple1.value2;
					float value2 = triple2.value2;
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
			comparator = new Comparator<ValueTriple>() {
				
				@Override
				public int compare(ValueTriple triple1, ValueTriple triple2) {
					float value1 = triple1.value2;
					float value2 = triple2.value2;
					if (value1 < value2)
						return -1;
					else if (value1 == value2)
						return 0;
					else
						return 1;
				}
			};
		}
		
		Collections.sort(tripleList, comparator);
		
	}

	
	/**
	 * This static method sorts the specified list of triples according to its 1-values.
	 * The resulted sorted list is truncated by the specified maximum count.
	 * @param tripleList specified list of triples. This is both input and output parameter.
	 * @param descending if {@code true}, the specified list sorted according to descending order. Otherwise, the list is sorted according ascending order.
	 * @param maxCount the maximum number of elements in the sorted list. If the number of elements in the original list is larger than this maximum count,
	 * the sorted list is truncated by this maximum count.
	 */
	public static void sortByValue1(List<ValueTriple> tripleList, boolean descending, int maxCount) {
		sortByValue1(tripleList, descending);
		if (maxCount > 0 && tripleList.size() > 0) {
			List<ValueTriple> subList = Util.newList();
			subList.addAll(
					tripleList.subList(0, Math.min(maxCount, tripleList.size()))
				);
			
			tripleList.clear();
			tripleList.addAll(subList);
		}
		
	}
	
	
	/**
	 * This static method sorts the specified list of triples according to its 2-values.
	 * The resulted sorted list is truncated by the specified maximum count.
	 * @param tripleList specified list of triples. This is both input and output parameter.
	 * @param descending if {@code true}, the specified list sorted according to descending order. Otherwise, the list is sorted according ascending order.
	 * @param maxCount the maximum number of elements in the sorted list. If the number of elements in the original list is larger than this maximum count,
	 * the sorted list is truncated by this maximum count.
	 */
	public static void sortByValue2(List<ValueTriple> tripleList, boolean descending, int maxCount) {
		sortByValue2(tripleList, descending);
		if (maxCount > 0 && tripleList.size() > 0) {
			List<ValueTriple> subList = Util.newList();
			subList.addAll(
					tripleList.subList(0, Math.min(maxCount, tripleList.size()))
				);
			
			tripleList.clear();
			tripleList.addAll(subList);
		}
		
	}
	
	
}
