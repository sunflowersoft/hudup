/**
 * 
 */
package net.hudup.core.logistic;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.data.RatingTriple;

/**
 * This final class provides utility static methods for processing data structures such as asserting, conversion, parsing, splitting, processing, etc.
 *  
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public final class DSUtil {

	
	/**
	 * Testing whether or not the specified array contains not-a-number (NaN) value.
	 * 
	 * @param array specified array of double values.
	 * @return whether array contains NaN number
	 */
	public static boolean assertNotNaN(double[] array) {
		for (double v : array) {
			if (Double.isNaN(v))
				return false;
		}
		
		return true;
	}
	
	
	/**
	 * Converting a specified array of double values into array of float values.
	 * @param d specified array of double values.
	 * @return float array of float values.
	 */
	public final static float[] doubleToFloat(double[] d) {
		float[] f = new float[d.length];
		
		for (int i = 0; i < d.length; i++)
			f[i] = (float)d[i];
		
		return f;
	}
	
	
	/**
	 * Converting a specified matrix of double values into matrix of float values.
	 * @param d specified matrix of double values.
	 * @return float matrix of float values.
	 */
	public final static float[][] doubleToFloat(double[][] d) {
		float[][] f = new float[d.length][];
		
		for (int i = 0; i < d.length; i++)
			f[i] = doubleToFloat(d[i]);
		
		return f;
	}
	
	
	/**
	 * Converting a specified array of byte values into array of integer values.
	 * @param b specified array of byte values.
	 * @return array of integer values.
	 */
	public final static int[] byteToInt(byte[] b) {
		int[] array = new int[b.length];
		
		for (int i = 0; i < b.length; i++)
			array[i] = b[i];
		
		return array;
	}
	
	
	/**
	 * Converting a specified matrix of byte values into matrix of integer values.
	 * @param b specified matrix of byte values.
	 * @return matrix of integer values.
	 */
	public final static int[][] byteToInt(byte[][] b) {
		int[][] array = new int[b.length][];
		
		for (int i = 0; i < b.length; i++)
			array[i] = byteToInt(b[i]);
		
		return array;
	}

	
	/**
	 * Converting a specified array of float values into array of byte values.
	 * @param f specified array of float values.
	 * @return array of byte values.
	 */
	public final static byte[] floatToByte(float[] f) {
		byte[] b = new byte[f.length];
		
		for (int i = 0; i < f.length; i++)
			b[i] = (byte)f[i];
		
		return b;
	}

	
	/**
	 * Converting a specified array of float values into array of double values.
	 * @param f specified array of float values.
	 * @return array of double values.
	 */
	public final static double[] floatToDouble(float[] f) {
		double[] d = new double[f.length];
		
		for (int i = 0; i < f.length; i++)
			d[i] = f[i];
		
		return d;
	}

	
	/**
	 * Converting a specified matrix of float values into matrix of byte values.
	 * @param f specified matrix of float values.
	 * @return matrix of byte values.
	 */
	public final static byte[][] floatToByte(float[][] f) {
		byte[][] b = new byte[f.length][];
		
		for (int i = 0; i < f.length; i++)
			b[i] = floatToByte(f[i]);
		
		return b;
	}

	
	/**
	 * Converting a specified matrix of float values into matrix of double values.
	 * @param f specified matrix of float values.
	 * @return matrix of double values.
	 */
	public final static double[][] floatToDouble(float[][] f) {
		double[][] d = new double[f.length][];
		
		for (int i = 0; i < f.length; i++)
			d[i] = floatToDouble(f[i]);
		
		return d;
	}

	
	/**
	 * Converting a specified array of integer values into set of integer values.
	 * @param array specified array of integer values.
	 * @return set of integer values.
	 */
	public final static Set<Integer> intToSet(int[] array) {
		Set<Integer> set = Util.newSet();
		for (int v : array) {
			set.add(v);
		}
		return set;
	}
	
	
	
	/**
	 * Converting a specified array of elements (with template E) into list of elements (template E).
	 * @param <E> type of elements in array and list.
	 * @param array array of elements (with template E).
	 * @return {@link List} of elements (with template E).
	 */
	public static <E> List<E> toList(E[] array) {
		List<E> list = Util.newList();
		for (E e : array) {
			list.add(e);
		}
		
		return list;
	}
	
	
	/**
	 * Converting a specified collection of float values into array of float values.
	 * @param collection specified collection of float values.
	 * @return array of float values.
	 */
	public final static float[] toFloatArray(Collection<Float> collection) {
		float[] array = new float[collection.size()];
		
		int i = 0;
		for (Float item : collection) {
			array[i] = item.floatValue();
			i++;
		}
		
		return array;
	}


	/**
	 * Converting a specified collection of float values into array of integer values.
	 * @param collection specified collection of float values.
	 * @return array of integer values.
	 */
	public final static int[] toIntArray(Collection<Integer> collection) {
		int[] array = new int[collection.size()];
		
		int i = 0;
		for (int item : collection) {
			array[i] = item;
			i++;
		}
		
		return array;
	}


	/**
	 * Converting a specified collection of float values into array of integer values.
	 * Moreover such integer values are rounded from float values.
	 * @param collection specified collection of integer values.
	 * @return array of integer values.
	 */
	public final static int[] toRoundIntArray(Collection<Float> collection) {
		int[] array = new int[collection.size()];
		
		int i = 0;
		for (float item : collection) {
			array[i] = (int)(item + 0.5f);
			i++;
		}
		
		return array;
	}


	/**
	 * Deep cloning the specified map of rating triples.
	 * @param map specified map of rating triples.
	 * @return cloned map of rating triples.
	 */
	public final static Map<Integer, RatingTriple> clone(Map<Integer, RatingTriple> map) {
		Map<Integer, RatingTriple> newMap = Util.newMap();
		Set<Integer> keys = map.keySet();
		for (Integer key : keys) {
			Integer newKey = new Integer(key);
			RatingTriple newRat = (RatingTriple) map.get(key).clone();
			newMap.put(newKey, newRat);
		}
		
		return newMap;
	}
	
	
	/**
	 * Deep cloning a specified list of bit sets.
	 * @param bsList specified list of bit sets.
	 * @return {@link List} of {@link BitSet} (s) cloned from the specified list of bit sets. 
	 */
	public final static List<BitSet> clone(List<BitSet> bsList) {
		List<BitSet> result = Util.newList();
		
		for (BitSet bs : bsList) {
			result.add((BitSet)bs.clone());
		}
		return result;
	}
	
	
	/**
	 * Splitting the specified string (source string) into a list of words (tokens). The character (string) that is used for separation is specified by the parameter {@code sep}.
	 * The returned list can be empty.
	 * For each returned word (token), the string that specified by the parameter {@code remove} is removed from such word.
	 * @param source specified string to be split.
	 * @param sep character (string) that is used for separation.
	 * @param remove the string which is removed from each tokens.
	 * @return {@link List} of words (tokens) from splitting the specified string. This list can be empty.
	 */
	public final static List<String> splitAllowEmpty(String source, String sep, String remove) {
		List<String> result = Util.newList();
		
		if (source.isEmpty() || sep.length() > source.length())
			return result;
		
		int begin = 0;
		while (begin < source.length()) {
			int next = source.indexOf(sep, begin);
			
			if (next == -1) {
				String word = source.substring(begin).trim();
				result.add(word);
			}
			else if (next == begin) {
				result.add("");
			}
			else {
				String word = source.substring(begin, next).trim();
				result.add(word);
			}
			
			if (next == -1)
				begin = source.length();
			else {
				begin = next + sep.length();
				if (begin == source.length())
					result.add("");
			}
			
		}
		
		return result;
	}
	
	
	/**
	 * Counting the number of bits in the specified bit set.
	 * 
	 * @param bs specified bit set.
	 * @return the number of bits in the specified bit set.
	 */
	public static int countSetBit(BitSet bs) {
		int count = 0;
		for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
			count ++;
		}
		return count;
	}
	
	
	/**
	 * Testing whether the bit set specified the first parameter {@code container} contains the bit set specified by the second parameter {@code item}.
	 * @param container first bit set as a container.
	 * @param item second bit set as an element.
	 * @return whether the bit set specified the first parameter {@code container} contains the bit set specified by the second parameter {@code item}.
	 */
	public static boolean containsSetBit(BitSet container, BitSet item) {
		BitSet and = (BitSet)container.clone();
		and.and(item);
		return countSetBit(and) >= countSetBit(item);
	}
	
	
	/**
	 * Processing data read from specified {@link Reader} line by line. How to process each line is specified by the input parameter {@code processor}.
	 * @param reader {@link Reader} to read data from archive (file).
	 * @param processor class implementing the interface {@link LineProcessor} specifying how to process data line by line.
	 */
	public static void lineProcess(BufferedReader reader, LineProcessor processor) {
		try {
			String line = null;
			while ( (line = reader.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0)
					continue;
				
				processor.process(line);
			}
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * In the specified list, moving elements in specified range to specified position.
	 * @param <T> type of elements of the specified list.
	 * @param list specified list.
	 * @param start start position of the specified range.
	 * @param end end position of the specified range.
	 * @param to specified position where elements in specified range are moved to.
	 */
	public static <T> void moveRow(List<T> list, int start, int end, int to) {
		
		if (start < 0 || start >= list.size() || 
				end < 0 || end >= list.size() ||
				to < 0 || to >= list.size())
			return;
		
		if (start > end) {
			int temp = start;
			start = end;
			end = temp;
		}
		if (start <= to && to <= end)
			return;
		
		List<T> newList = Util.newList();
		for (int i = 0; i < list.size() && (start < to ? i <= to : i < to); i++) {
			if (i < start || end < i)
				newList.add(list.get(i));
		}
		
		for (int i = start; i <= end; i++) {
			newList.add(list.get(i));
		}
		
		for (int i = (start < to ? to + 1 : to); i < list.size(); i++) {
			if (i < start || end < i)
				newList.add(list.get(i));
		}
		
		list.clear();
		list.addAll(newList);
		newList.clear();
	}
	
	
}
