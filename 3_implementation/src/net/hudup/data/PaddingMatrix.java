package net.hudup.data;

import java.awt.Point;
import java.util.List;

import net.hudup.core.data.Dataset;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.MathUtil;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public abstract class PaddingMatrix {
	
	
	/**
	 * 
	 */
	protected float[][] matrix = new float[0][];
	
	
	/**
	 * 
	 * @param dataset
	 * @param vRating
	 */
	public PaddingMatrix(Dataset dataset, RatingVector vRating) {
		setup(dataset, vRating);
	}
	
	/**
	 * 
	 * @param dataset
	 * @param vRating
	 */
	public abstract void setup(Dataset dataset, RatingVector vRating);
	
	
	/**
	 * 
	 * @return list of user id (s)
	 */
	public abstract List<Integer> getUserIdList();

	
	/**
	 * 
	 * @return list of item id (s)
	 */
	public abstract List<Integer> getItemIdList();

	
	/**
	 * 
	 * @param align
	 */
	public void sortRows(float[] align) {
		sortRows0(align);
	}
	
	
	/**
	 * 
	 * @param align
	 */
	public void sortColumns(float[] align) {
		matrix = MathUtil.transpose(matrix);
		sortRows(align);
		matrix = MathUtil.transpose(matrix);
	}
	
	
	/**
	 * 
	 * @param row
	 * @param column
	 * @return rating value
	 */
	public float getRatingByIndex(int row, int column) {
		return matrix[row + 1][column + 1];
	}

	
	/**
	 * 
	 * @param vPadding
	 * @param align
	 * @return distance between two rows 
	 */
	private float distance(float[] vPadding, float[] align) {
		float[] vector = new float[vPadding.length - 1];
		for (int i = 0; i < vector.length; i++) {
			vector[i] = vPadding[i + 1];
		}
		
		return MathUtil.manhattanDistance(vector, align);
	}

	
	/**
	 * 
	 * @param align
	 */
	private void sortRows0(float[] align) {
		
		int n = matrix.length;
		
		for (int i = 2; i < n; i++) { 
			float[] vector = matrix[i];
			float dis = distance(vector, align);

			int j = i;

			while (j > 1 && 
					distance(matrix[j - 1], align) > dis) {

				matrix[j] = matrix[j - 1];

				j--; 

			}

			matrix[j] = vector;

		}
		
	}
	
	
	/**
	 * 
	 * @param userId
	 * @param itemId
	 * @return rating value
	 */
	public abstract float getRating(int userId, int itemId);
	
	
	
	/**
	 * 
	 * @param userId
	 * @return user rating vector
	 */
	public abstract float[] getUserRatingVector(int userId);
	

	/**
	 * 
	 * @param idx
	 * @return user rating vector
	 */
	public abstract float[] getUserRatingVectorByIndex(int idx);
	

	/**
	 * 
	 * @param itemId
	 * @return item rating vector
	 */
	public abstract float[] getItemRatingVector(int itemId);
	
	
	/**
	 * 
	 * @param idx
	 * @return item rating vector
	 */
	public abstract float[] getItemRatingVectorByIndex(int idx);

	
	/**
	 * 
	 * @param userId
	 * @param itemId
	 * @return {@link Point}
	 */
	public abstract Point getRowCol(int userId, int itemId);
	
	
	/**
	 * 
	 * @return number of users
	 */
	public abstract int numberOfUsers();
		

	/**
	 * 
	 * @return number of items
	 */
	public abstract int numberOfItems();
	
	
}
