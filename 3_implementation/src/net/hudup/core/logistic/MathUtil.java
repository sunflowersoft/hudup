package net.hudup.core.logistic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.hudup.core.Constants;
import net.hudup.core.Util;


/**
 * This final class provides utility static methods for mathematics such as summing, taking logarithm, powering, formatting, rounding, vector operators, matrix operators, etc.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public final class MathUtil {

	
	/**
	 * Firstly, the method creates the resulted vector {@code A}.
	 * Secondly, for each row vector of the specified matrix {@code data}, the method calculates the sum of values of such row vector.
	 * Such each sum is raised to the power of the specified {@code order}, which results a raised sum.
	 * Finally, such raised sum is put in to the resulted vector {@code A}.
	 * So the resulted vector {@code A} which has many raised sums is returned.
	 * @param data specified matrix
	 * @param order each sum is raised to the power.
	 * @return sum vector of raised sums.
	 */
	public static double[] sum(double[][] data, int order) {
		int n = data[0].length;
		double[] result = new double[n];
		for (int i = 0; i < n; i++) {
			result[i] = 0;
			for (int j = 0; j < data.length; j++)
				result[i] += data[j][i];
			
			result[i] = Math.pow(result[i], order);
		}
		
		return result;
	}

	
	/**
	 * Making logarithm of the specified value with regard to specified base.
	 * @param value specified value.
	 * @param base specified base
	 * @return logarithm of the specified value with regard to specified base.
	 */
	public static double logarit(double value, double base) {
		if (base == Math.E)
			return Math.log(value);
		else if (base == 10)
			return Math.log10(value);
		else
			return Double.NaN;
			
	}
	
	
	/**
	 * Making logarithm of all elements of specified vector with regard to specified base.
	 * @param data specified vector.
	 * @param base specified base.
	 * @return array of values which are logarithms of elements of specified vector with regard to specified base.
	 */
	public static double[] logarit(double[] data, double base) {
		double[] result = new double[data.length];
		for (int i = 0; i < data.length; i++) {
			if (base == Math.E)
				result[i] = Math.log(data[i]);
			else if (base == 10)
				result[i] = Math.log10(data[i]);
		}
		
		return result;
	}

	
	/**
	 * Making logarithm of all elements of specified matrix with regard to specified base.
	 * @param data specified matrix.
	 * @param base specified base.
	 * @return matrix of values which are logarithms of elements of specified matrix with regard to specified base.
	 */
	public static double[][] logarit(double[][] data, double base) {
		double[][] result = new double[data.length][];
		
		for (int i = 0; i < data.length; i++) {
			result[i] = logarit(data[i], base);
		}
		
		return result;
	}
	
	
	/**
	 * Raising specified base to the power of every element of specified vector.
	 * @param data specified vector.
	 * @param base specified base.
	 * @return array of values resulted from raising specified base to the power of every element of specified vector.
	 */
	public static double[] pow(double[] data, double base) {
		double[] result = new double[data.length];
		for (int i = 0; i < data.length; i++) {
			result[i] = Math.pow(base, data[i]);
		}
		
		return result;
	}

	
	/**
	 * Setting the name for logarithm with specified base, for example, &quot;log&quot; for natural logarithm,
	 * &quot;log10&quot; for logarithm with base 10, &quot;log2&quot; for logarithm with base 2.
	 * 
	 * @param base specified base.
	 * @return name for logarithm with specified base.
	 */
	public static String logaritName(double base) {
		if (base == Math.E)
			return "log";
		else if (base == 10)
			return "log10";
		else
			return "log" + MathUtil.format(base);
	}
	
	
	/**
	 * Setting the name for power function with specified base, for example, &quot;exp&quot; for natural power, &quot;10^&quot; for 10th power, &quot;2^&quot; for 2th power. 
	 * @param base specified base.
	 * @return name for power function with specified base.
	 */
	public static String powName(double base) {
		if (base == Math.E)
			return "exp";
		else
			return "" + MathUtil.format(base) + "^";
	}

	
	/**
	 * Given a specified list of many sets, this method returns a list of Cartesian products from such many sets.
	 * For example, given two sets {a, b, c} and {1, 2}, this static method returns six Cartesian products such as
	 * {a, 1}, {a, 2}, {b, 1}, {b, 2}, {c, 1}, {c, 2}.
	 * @param <T> type of elements of sets.
	 * @param list specified list of many sets.
	 * @return List of Cartesian products for list of many sets.
	 */
	public static <T> List<Set<T>> cartesianProduct(List<Set<T>> list) {
	    List<Iterator<T>> iterators = new ArrayList<Iterator<T>>(list.size());
	    List<T> elements = new ArrayList<T>(list.size());
	    List<Set<T>> toRet = new ArrayList<Set<T>>();
	    for (int i = 0; i < list.size(); i++) {
	            iterators.add(list.get(i).iterator());
	            elements.add(iterators.get(i).next());
	    }
	    for (int j = 1; j >= 0;) {
	            toRet.add(new HashSet<T>(elements));
	            for (j = iterators.size()-1; j >= 0 && !iterators.get(j).hasNext(); j--) {
	                    iterators.set(j, list.get(j).iterator());
	                    elements.set(j, iterators.get(j).next());
	            }
	            elements.set(Math.abs(j), iterators.get(Math.abs(j)).next());
	    }
	    return toRet;
	}


	/**
	 * Converting the specified number into a string. The number of decimal digits is specified by the parameter {@code decimal}. 
	 * @param number specified number.
	 * @param decimal the number of decimal digits.
	 * @return text format of the specified number.
	 */
	public static String format(double number, int decimal) {
		return Double.toString(round(number, decimal));
	}


	/**
	 * Converting the specified number into a string. The number of decimal digits is specified by {@link Constants#DECIMAL_PRECISION}.
	 * @param number specified number.
	 * @return text format of number of the specified number.
	 */
	public static String format(double number) {
		return Double.toString(round(number, Constants.DECIMAL_PRECISION));
		
	}


	/**
	 * Rounding the specified number with decimal precision specified by the number of decimal digits.
	 * 
	 * @param number specified number.
	 * @param n decimal precision which is the number of decimal digits.
	 * @return number rounded from the specified number.
	 */
	public static double round(double number, int n) {
		if (Double.isNaN(number))
			return Float.NaN;
		
		long d = (long) Math.pow(10, n);
		return (double) Math.round(number * d) / d;
	}
	

	/**
	 * Rounding the specified number with decimal precision specified by {@link Constants#DECIMAL_PRECISION}.
	 * @param number specified number.
	 * @return number rounded from the specified number.
	 */
	public static double round(double number) {
		if (Double.isNaN(number))
			return Float.NaN;
		
		return round(number, Constants.DECIMAL_PRECISION);
	}

	
	/**
	 * Calculating mean of specified vector.
	 * @param v specified vector.
	 * @return mean of specified vector.
	 */
	public static float mean(float[] v) {
		float sum = 0;
		int count = 0;
		for (float value : v) {
			if (Util.isUsed(value)) {
				sum += value;
				count ++;
			}
				
		}
		
		return sum / (float)count;
	}
	
	
	/**
	 * This method returns a vector whose each value is deviation between value of specified vector and mean of specified vector. 
	 * @param v specified vector.
	 * @return deviation vector whose each value is deviation between value of specified vector and mean of specified vector.
	 */
	public static float[] deviation(float[] v) {
		float mean = mean(v);
		float[] dev = new float[v.length];
		
		for (int i = 0; i < v.length; i++)
			dev[i] = v[i] - mean;
		
		return dev;
	}
	
	
	/**
	 * Choosing random elements in the specified list called {@code container}.
	 * The number of random elements is specified by the parameter {@code pickNum}.
	 * @param <T> type of elements.
	 * @param container specified list from which elements are picked.
	 * @param pickNum the number of random elements picked randomly from specified list (container).
	 * @return {@link List} of elements picked randomly from specified list (container).
	 */
	public final static <T extends Object> List<T> pickingRandom(List<T> container, int pickNum) {
		List<T> result = Util.newList();
		Random rnd = new Random();
		int n = container.size();
		
		while (pickNum > 0) {
			int idx = rnd.nextInt(n);
			T value = container.get(idx);
			if (!result.contains(value)) {
				result.add(value);
				pickNum--;
			}
		}
		
		return result;
	}
	
	
	/**
	 * Extracting a sub-list of specified list so that the sub-list has {@code pickNum} first elements of the specified list.
	 * @param <T> type of elements. 
	 * @param container specified list called {@code container}.
	 * @param pickNum the number elements of the returned sub-list.
	 * @return sub-list of specified list. Such sub-list has {@code pickNum} first elements of the specified list. 
	 */
	public final static <T extends Object> List<T> pickingOrdering(List<T> container, int pickNum) {
		return container.subList(0, Math.min(pickNum, container.size()));
	}
	

	/**
	 * Transposing the specified matrix. Note that a matrix was transposed if its rows becomes its columns and vice versa.
	 * @param matrix specified matrix.
	 * @return transposed matrix.
	 */
	public final static float[][] transpose(float[][] matrix) {
		int n = matrix[0].length;
		
		float[][] trans = new float[n][];
		
		for (int i = 0; i < n; i++) {
			trans[i] = getColumnVector(matrix, i);
		}
		
		return trans;
	}
	
	
	/**
	 * Getting column vector of specified float matrix at specified column. In other words, such column vector contains all values of specified matrix at the same column.
	 * @param matrix specified float matrix.
	 * @param column specified column.
	 * @return column vector of specified matrix at specified column.
	 */
	public final static float[] getColumnVector(float[][] matrix, int column) {
		float[] cvector = new float[matrix.length];
		for (int i = 0; i < matrix.length; i++) {
			cvector[i] = matrix[i][column];
		}
		
		return cvector;
	}
	
	
	/**
	 * Getting column vector of specified double matrix at specified column. In other words, such column vector contains all values of specified matrix at the same column.
	 * @param matrix double matrix.
	 * @param column specified column.
	 * @return column vector of specified matrix at specified column.
	 */
	public final static double[] getColumnVector(double[][] matrix, int column) {
		double[] cvector = new double[matrix.length];
		for (int i = 0; i < matrix.length; i++) {
			cvector[i] = matrix[i][column];
		}
		
		return cvector;
	}

	
	/**
	 * Replacing all values of specified float matrix at the specified column by values of specified vector.
	 * The parameter {@code matrix} is both input and output parameter.
	 * @param matrix specified float matrix.
	 * @param column specified column.
	 * @param cvector specified vector.
	 */
	public final static void setColumnVector(float[][] matrix, int column, float[] cvector) {
		for (int i = 0; i < cvector.length; i++) {
			matrix[i][column] = cvector[i];
		}
	}
	
	
	/**
	 * Calculating the Manhattan distance between two specified float arrays.
	 * Manhattan distance is sum of absolute deviations between elements of two arrays.
	 * 
	 * @param a the first array.
	 * @param b the second array.
	 * @return Manhattan distance between two specified float arrays.
	 */
	public final static float manhattanDistance(float[] a, float[] b) {
		int n = Math.min(a.length, b.length);
		
		float dis = 0;
		for (int i = 0; i < n; i++) {
			if ( Util.isUsed(a[i]) && Util.isUsed(b[i]) ) 
				dis += Math.abs(a[i] - b[i]);	
		}
		
		return dis;
	}
	
	
}
