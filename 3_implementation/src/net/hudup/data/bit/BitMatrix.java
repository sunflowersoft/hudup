/**
 * 
 */
package net.hudup.data.bit;

import java.util.List;

import net.hudup.core.Util;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class BitMatrix {

	/**
	 * 
	 */
	public byte[][] matrix = new byte[0][];
	
	
	/**
	 * 
	 */
	public List<Integer> rowIdList = Util.newList();
	
	
	/**
	 * 
	 */
	public List<Integer> columnIdList = Util.newList();

	
	/**
	 * 
	 * @param matrix
	 * @param rowIdList
	 * @param columnIdList
	 * @return {@link BitMatrix}
	 */
	public static BitMatrix assign(byte[][] matrix, List<Integer> rowIdList, List<Integer> columnIdList) {
		BitMatrix bMatrix = new BitMatrix();
		bMatrix.matrix = matrix;
		bMatrix.rowIdList = rowIdList;
		bMatrix.columnIdList = columnIdList;
		
		return bMatrix;
	}
	
	
}
