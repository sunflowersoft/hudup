/**
 * 
 */
package net.hudup.core.data;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.logistic.MathUtil;
import net.hudup.core.logistic.Vector;
import net.hudup.core.parser.TextParserUtil;

/**
 * This class represents a rating matrix contains a set of rows and a set of columns in table form.
 * Each cell is a rating value that a user gives on an item. The real matrix is stored in its public variable {@link #matrix}.
 * If this is a user rating matrix, each row is a vector of rating values that a user gives on many items.
 * In other words, rows in user rating matrix correspond with users and columns in user rating matrix correspond with items. 
 * If this is an item rating matrix, each row is a vector of rating values that an item is received from many users.
 * In other words, rows in item rating matrix correspond with items and columns in item rating matrix correspond with users. 
 * In a rating matrix, all values at the same row constitute a row vector and all values at the same column constitute a column vector.
 * Row vectors and column vectors are extracted from the internal real matrix {@link #matrix}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class RatingMatrix {

	
	/**
	 * The public variable for fast access, representing a matrix of rating values.
	 * In case of user rating matrix, each row of the matrix is a vector of rating values that a user gives on many items.
	 * In case of item rating matrix, each row is a vector of rating values that an item received from many users.
	 * This variable is called data matrix or internal matrix.
	 */
	public float[][] matrix = new float[0][];
	
	
	/**
	 * Essentially, this variable is a mapping from a column to an ID. It is public for fast access.
	 * If this is a user rating matrix, this variable is a vector of user identifications (user IDs)
	 * ;for example {@code rowIdList.get(0)} returns the same user ID of all rating values at the first row.
	 * If this is an item rating matrix, this variable is a vector of item identifications (item IDs).
	 * ;for example {@code rowIdList.get(0)} returns the same item ID of all rating values at the first row.
	 * As a convention, this variable is called {@code row ID list} and every its element is called {@code row ID}.
	 */
	public List<Integer> rowIdList = Util.newList();
	
	
	/**
	 * Essentially, this variable is a mapping from a column to an ID. It is public for fast access.
	 * If this is a user rating matrix, this variable is a vector of item identifications (item IDs)
	 * ;for example {@code rowIdList.get(0)} returns the same item ID of all rating values at the first column.
	 * If this is an item rating matrix, this variable is a vector of user identifications (user IDs).
	 * ;for example {@code rowIdList.get(0)} returns the same user ID of all rating values at the first column.
	 * As a convention, this variable is called {@code column ID list} and every its element is called {@code column ID}.
	 */
	public List<Integer> columnIdList = Util.newList();
	
	
	/**
	 * This variable is meta-data of rating matrix which stores additional information of rating matrix.
	 * Rating matrix meta-data is represented by {@link RatingMatrixMetadata} class.
	 * In general, every rating matrix is always associated with a meta-data.
	 */
	public RatingMatrixMetadata metadata = new RatingMatrixMetadata();
	
	
	/**
	 * Creating a sub-matrix of this rating matrix. The column ID list of the sub-matrix (specified by the parameter {@code columnIdList} is sub-set of the column ID list of this rating matrix.
	 * In other words, this static method extracts a child matrix of this rating matrix by shrinking this rating matrix according to columns. 
	 * @param columnIdList column ID list of the sub-matrix which is sub-set of the column ID list of this rating matrix.
	 * @param updateMetadata whether or not updating the meta-data of the sub-matrix.
	 * @return sub rating matrix which is extracted from this rating matrix.
	 */
	public RatingMatrix createSub(List<Integer> columnIdList, boolean updateMetadata) {
		if (columnIdList.size() == 0)
			return new RatingMatrix();
		
		int n = Math.min(columnIdList.size(), this.columnIdList.size());
		
		List<Integer> newRowIdList = Util.newList();
		List<Integer> newColumnIdList = Util.newList();
		newColumnIdList.addAll(columnIdList.subList(0, n));
		
		List<float[]> matrix = Util.newList();
		for (int i = 0; i < this.matrix.length; i++) {
			float[] row = this.matrix[i];
			
			float[] newRow = new float[n];
			
			for (int j = 0; j < n; j++) {
				int id = newColumnIdList.get(j);
				int idx = this.columnIdList.indexOf(id);
				
				if (idx == -1)
					newRow[j] = Constants.UNUSED;
				else
					newRow[j] = row[idx];
			}
			
			matrix.add(newRow);
			newRowIdList.add(this.rowIdList.get(i));
		}
		
		
		RatingMatrixMetadata metadata = (RatingMatrixMetadata)this.metadata.clone();
		
		RatingMatrix subMatrix = RatingMatrix.assign(
				matrix.toArray(new float[0][]),
				newRowIdList,
				newColumnIdList,
				metadata);
		
		if (updateMetadata)
			subMatrix.updateMetadata();
		
		return subMatrix;
	}
	
	
	/**
	 * This static method firstly creates a new rating matrix and then assigns all variables of such new rating matrix by specified parameters.
	 * Such variables are the internal matrix, row ID list, column ID list, meta-data.
	 * Note that there is no deep copying, just only reference assignments.
	 * This method is often used as a constructor of rating matrix.
	 * @param matrix specified data matrix assigned to the data matrix of the returned rating matrix.
	 * @param rowIdList specified row ID list assigned to the row ID list of the returned rating matrix.
	 * @param columnIdList specified column ID list assigned to the column ID list of the returned rating matrix.
	 * @param metadata specified meta-data assigned to the meta-data of the returned rating matrix.
	 * @return new rating matrix whose variables are assigned by specified parameters.
	 */
	public static RatingMatrix assign(float[][] matrix, List<Integer> rowIdList, List<Integer> columnIdList, RatingMatrixMetadata metadata) {
		RatingMatrix rMatrix = new RatingMatrix();
		rMatrix.matrix = matrix;
		rMatrix.rowIdList = rowIdList;
		rMatrix.columnIdList = columnIdList;
		rMatrix.metadata = metadata;
		
		return rMatrix;
	}
	
	
	/**
	 * Assigning all variables of this rating matrix by variables of specified rating matrix.
	 * @param that specified rating matrix.
	 */
	public void assign(RatingMatrix that) {
		this.matrix = that.matrix;
		this.rowIdList = that.rowIdList;
		this.columnIdList = that.columnIdList;
		this.metadata = that.metadata;
	}
	
	
	/**
	 * Checking whether this rating matrix is empty.
	 * A rating matrix is called empty if it has no data.
	 * @return whether matrix is empty.
	 */
	public boolean isEmpty() {
		return rowIdList.size() == 0 || columnIdList.size() == 0 || matrix.length == 0;
	}
	
	
	/**
	 * Clearing this rating matrix, which means that making this rating matrix empty.
	 * A rating matrix is called empty if it has no data.
	 */
	public void clear() {
		rowIdList.clear();
		columnIdList.clear();
		matrix = new float[0][];
	}
	
	
	/**
	 * Extracting a row vector at the specified row ID. Note, all values at the same row constitute a row vector.
	 * @param rowId specified row ID.
	 * @return row vector attached to the specified row ID.
	 */
	public float[] getRowVector(int rowId) {
		int index = rowIdList.indexOf(rowId);
		if (index == -1)
			return null;
		else
			return matrix[index];
	}
	
	
	/**
	 * Extracting a column vector at the specified column ID. Note, all values at the same column constitute a column vector.
	 * @param columnId specified column ID.
	 * @return column vector attached to the specified column ID.
	 */
	public float[] getColumnVector(int columnId) {
		int index = columnIdList.indexOf(columnId);
		if (index == -1)
			return null;
		
		float[] columnVector = new float[matrix.length];
		for (int i = 0; i < matrix.length; i++) {
			float[] row = matrix[i];
			columnVector[i] = row[index];
		}
		return columnVector;
	}
	
	
	/**
	 * Getting the value at specified row ID and specified column ID.
	 * @param rowId specified row ID.
	 * @param columnId specified column ID.
	 * @return value at cell(row id, column id).
	 */
	public float getValue(int rowId, int columnId) {
		int rowIndex = rowIdList.indexOf(rowId);
		int columnIndex = columnIdList.indexOf(columnId);
		if (rowIndex == -1 || columnIndex == -1)
			return Constants.UNUSED;
		else
			return matrix[rowIndex][columnIndex];
		
	}
	
	
	/**
	 * Transposing this rating matrix. Note that a matrix was transposed if its rows becomes its columns and vice versa.
	 */
	public void transpose() {
		matrix = MathUtil.transpose(matrix);
		
		List<Integer> temp = Util.newList();
		temp.addAll(rowIdList);
		rowIdList.clear();
		rowIdList.addAll(columnIdList);
		
		columnIdList.clear();
		columnIdList.addAll(temp);
		
		metadata.isUser = !metadata.isUser;
		
		int t = metadata.numberOfUsers;
		metadata.numberOfUsers = metadata.numberOfItems;
		metadata.numberOfItems = t;
		
		t = metadata.numberOfUserRatings;
		metadata.numberOfUserRatings = metadata.numberOfItemRatings;
		metadata.numberOfItemRatings = t;
	}
	
	
	/**
	 * Multiplying (applying) this rating matrix with the specified vector.
	 * Of course, the result is a vector.
	 * @param vector specified vector.
	 * @return new vector resulted from multiplying this matrix with the specified vector.
	 */
	public Vector apply(Vector vector) {
		
		Vector result = new Vector(matrix.length, 0);
		for (int i = 0; i < matrix.length; i++) {
			float[] row = matrix[i];
			
			float product = 0;
			for (int j = 0; j < row.length; j++) {
				product += row[j] * vector.get(j);
			}
			
			result.set(i, product);
		}
		
		return result;
	}
	
	
	/**
	 * Saving this rating matrix to specified {@link Writer}.
	 * @param writer specified {@link Writer}.
	 */
	public void save(Writer writer) {
		PrintWriter printer = new PrintWriter(writer);
		try {
			printer.println(metadata.toText());
			printer.println(TextParserUtil.toText(rowIdList, ","));
			printer.println(TextParserUtil.toText(columnIdList, ","));
			
			for (float[] row : matrix) {
				printer.println(TextParserUtil.toText(row, ","));
				
			}
			printer.flush();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * Reading (loading) this rating matrix from the specified {@link Reader}.
	 * @param reader specified {@link Reader}.
	 */
	public void load(Reader reader) {
		clear();
		
		BufferedReader buffer = new BufferedReader(reader);
		
		RatingMatrixMetadata metadata = new RatingMatrixMetadata();
		List<Integer> rowIdList = Util.newList();
		List<Integer> columnIdList = Util.newList();
		List<float[]> rowList = Util.newList();
		
		try {
			metadata.parseText(buffer.readLine());
			
			List<String> rowIdTextList = TextParserUtil.parseTextList(buffer.readLine(), ",");
			for (String rowIdText : rowIdTextList) {
				rowIdList.add(Integer.parseInt(rowIdText));
			}
			
			List<String> columnIdTextList = TextParserUtil.parseTextList(buffer.readLine(), ",");
			for (String columnIdText : columnIdTextList) {
				columnIdList.add(Integer.parseInt(columnIdText));
			}
			
			String line = null;
			while ( (line = buffer.readLine()) != null ) {
				List<String> rowTextList = TextParserUtil.parseTextList(line, ",");
				if (rowTextList.size() != columnIdList.size())
					continue;
				
				float[] row = new float[rowTextList.size()];
				for (int i = 0; i < row.length; i++) {
					row[i] = Float.parseFloat(rowTextList.get(i));
				}
				
				rowList.add(row);
			}
			
			this.metadata = metadata;

			this.rowIdList.clear();
			this.rowIdList.addAll(rowIdList);
			
			this.columnIdList.clear();
			this.columnIdList.addAll(columnIdList);
			
			this.matrix = new float[rowList.size()][];
			for (int i = 0; i < rowList.size(); i++) {
				this.matrix[i] = rowList.get(i);
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
	/**
	 * This method updates the meta-data of this rating matrix. Updating is only effective if this matrix is modified.
	 * Note, meta-data of rating matrix stores additional information of rating matrix.
	 */
	public void updateMetadata() {
		metadata.numberOfUsers = metadata.isUser ? rowIdList.size() : columnIdList.size();
		metadata.numberOfItems = metadata.isUser ? columnIdList.size() : rowIdList.size();
		
		int ratedUserCount = 0;
		int ratedItemCount = 0;
		
		for (int i = 0; i < rowIdList.size(); i++) {
			
			boolean rated = false;
			for (int j = 0; j < columnIdList.size(); j++) {
				if (Util.isUsed(matrix[i][j])) {
					rated = true;
					break;
				}
			}
			
			if (!rated)
				continue;
			
			if (metadata.isUser)
				ratedUserCount ++;
			else
				ratedItemCount ++;
		}
		
		for (int j = 0; j < columnIdList.size(); j++) {
			
			boolean rated = false;
			for (int i = 0; i < rowIdList.size(); i++) {
				if (Util.isUsed(matrix[i][j])) {
					rated = true;
					break;
				}
			}
			
			if (!rated)
				continue;
			
			if (metadata.isUser)
				ratedItemCount ++;
			else
				ratedUserCount ++;
		}
		
		metadata.numberOfUserRatings = ratedUserCount;
		metadata.numberOfItemRatings = ratedItemCount;
	}
	
	
}