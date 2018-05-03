package net.hudup.data.ui;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import net.hudup.core.Util;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.FetcherUtil;
import net.hudup.core.data.RatingMatrix;
import net.hudup.core.data.RatingVector;
import net.hudup.data.MapVector;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class RatingValue2RowsTable extends RatingValueTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	@Override
	public RatingValueTM newTableModel() {
		// TODO Auto-generated method stub
		return new RatingTwoRowsTableModel();
	}
	
	
}



/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
class RatingTwoRowsTableModel extends RatingValueTM {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	@Override
	public void update(Dataset dataset, Collection<Integer> moreIds) {
		this.rowIndexes.clear();
		this.columnIndexes.clear();
		this.columnIds.clear();
		this.setDataVector(new String[0][], new String[0][]);
		if (dataset == null)
			return;
		
		FetcherUtil.fillCollection(this.columnIds, dataset.fetchItemIds(), true);
		if (moreIds != null)
			this.columnIds.addAll(moreIds);
		Collections.sort(this.columnIds);

		Vector<Vector<Object>> data = Util.newVector();
		
		List<Integer> rowIds = Util.newList();
		FetcherUtil.fillCollection(rowIds, dataset.fetchUserIds(), true);
		Collections.sort(rowIds);
		
		for (int i = 0; i < rowIds.size(); i++) {
			int rowId = rowIds.get(i);
			this.rowIndexes.put(rowId, 2 * i);
			RatingVector vRating = dataset.getUserRating(rowId);
			
			Vector<Object> row = toRow(vRating);
			data.add(row);
			
			Vector<Object> row2 = toRowEmpty();
			data.add(row2);
		}
		
		for (int i = 0; i < columnIds.size(); i++) {
			int columnId = columnIds.get(i);
			columnIndexes.put(columnId, i + 1);
		}
		this.setDataVector(data, toColumns());
	}

	
	@Override
	public void update(RatingMatrix matrix, Collection<Integer> moreColumnIds) {
		this.rowIndexes.clear();
		this.columnIndexes.clear();
		this.columnIds.clear();
		this.setDataVector(new String[0][], new String[0][]);
		if (matrix == null)
			return;
		
		this.columnIds.addAll(matrix.columnIdList);
		if (moreColumnIds != null)
			this.columnIds.addAll(moreColumnIds);
		Collections.sort(this.columnIds);
		
		Vector<Vector<Object>> data = Util.newVector();
		
		List<Integer> rowIds = Util.newList();
		rowIds.addAll(matrix.rowIdList);
		Collections.sort(rowIds);
		
		for (int i = 0; i < rowIds.size(); i++) {
			int rowId = rowIds.get(i);
			this.rowIndexes.put(rowId, 2 * i);
			
			Vector<Object> row = toRow(matrix, rowId);
			data.add(row);
			
			Vector<Object> row2 = toRowEmpty();
			data.add(row2);
		}
		
		for (int i = 0; i < columnIds.size(); i++) {
			int columnId = columnIds.get(i);
			columnIndexes.put(columnId, i + 1);
		}
		this.setDataVector(data, toColumns());
	}

	
	@Override
	public void setMoreRatingVector(RatingVector vRating, Set<Integer> queryIds) {
		Integer rowIndex = this.rowIndexes.get(vRating.id());
		Object value = null;
		if (rowIndex == null) 
			return;		
		
		for (int queryId : queryIds) {
			Integer columnIndex = columnIndexes.get(queryId);
			if (columnIndex == null)
				continue;
			if (!vRating.contains(queryId))
				value = "[none]";
			else
				value = "[" + vRating.get(queryId) + "]";
				
			setValueAt(value, rowIndex+1, columnIndex);
		}
	}


	/**
	 * 
	 */
	public List<MapVector<Object>> getRow(int rowIdx) {
		int row = (rowIdx % 2 == 0) ? rowIdx : rowIdx - 1;
		int rowIdFound = -1;
		Set<Integer> rowIds = this.rowIndexes.keySet();
		for (int rowId : rowIds) {
			if (this.rowIndexes.get(rowId) == row) {
				rowIdFound = rowId;
				break;
			}
				
		}
		
		if (rowIdFound == -1)
			return Util.newList();
		
		int n = getColumnCount();
		List<MapVector<Object>> result = Util.newList();
		
		if (rowIdx % 2 == 0) {
			MapVector<Object> vector = new MapVector<Object>(rowIdFound);
			for (int i = 1; i < n; i++) {
				Object value = getValueAt(row, i);
				String strValue = (value != null ? value.toString() : "");
				
				if (strValue.isEmpty()) 
					continue;
				
				int columnId = this.columnIds.get(i - 1);
				vector.put(columnId, value);
			}
			
			result.add(vector);
		}
		else {
			MapVector<Object> vector1 = new MapVector<Object>(rowIdFound);
			MapVector<Object> vector2 = new MapVector<Object>(rowIdFound);
			for (int i = 1; i < n; i++) {
				Object value1 = getValueAt(row, i);
				String strValue1 = (value1 != null ? value1.toString() : "");
				
				Object value2 = getValueAt(rowIdx, i);
				String strValue2 = (value2 != null ? value2.toString() : "");

				if (strValue1.isEmpty() || strValue2.isEmpty()) 
					continue;
				
				int columnId = this.columnIds.get(i - 1);
				
				vector1.put(columnId, value1);
				vector2.put(columnId, value2);
			}
			
			result.add(vector1);
			result.add(vector2);
		}
		
		return result;
	}
	
	
}
