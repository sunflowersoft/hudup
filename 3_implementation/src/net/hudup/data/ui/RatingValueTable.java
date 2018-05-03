package net.hudup.data.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import net.hudup.core.Util;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.FetcherUtil;
import net.hudup.core.data.RatingMatrix;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.data.MapVector;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class RatingValueTable extends JTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 * 
	 */
	public RatingValueTable() {
		super();
		setModel(newTableModel());
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = createContextMenu();
					if(contextMenu != null) 
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
				else if (e.getClickCount() >= 2) {
					viewRow();
				}
			}
		});
		
		setAutoResizeMode(AUTO_RESIZE_OFF);
	}
	
	
	/**
	 * 
	 * @param dataset
	 * @param moreIds
	 */
	public void update(Dataset dataset, Collection<Integer> moreIds) {
		RatingValueTM model = getRatingValueTM();
		model.update(dataset, moreIds);
		
	}
	
	
	/**
	 * 
	 * @param matrix
	 * @param moreIds
	 */
	public void update(RatingMatrix matrix, Collection<Integer> moreIds) {
		RatingValueTM model = getRatingValueTM();
		model.update(matrix, moreIds);
		
	}

	
	/**
	 * 
	 */
	public void clear() {
		RatingValueTM model = getRatingValueTM();
		model.clear();
		
	}
	
	
	/**
	 * 
	 * @return {@link RatingValueTM}
	 */
	public RatingValueTM getRatingValueTM() {
		return (RatingValueTM)super.getModel();
	}
	
	
	/**
	 * 
	 * @return {@link RatingValueTM}
	 */
	public RatingValueTM newTableModel() {
		return new RatingValueTM();
	}
	
	
	/**
	 * 
	 * @return {@link JPopupMenu}
	 */
	private JPopupMenu createContextMenu() {
		JPopupMenu contextMenu = new JPopupMenu();
		
		JMenuItem miViewRow = UIUtil.makeMenuItem((String)null, "View row", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					viewRow();
				}
			});
		contextMenu.add(miViewRow);
		
		return contextMenu;
	}
	
	
	/**
	 * 
	 */
	protected void viewRow() {
		int selIdx = getSelectedRow();
		if (selIdx == -1) {
			JOptionPane.showMessageDialog(
					this, 
					"No selected row", 
					"No selected row", 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		List<MapVector<Object>> rows = getRatingValueTM().getRow(selIdx);
		MapVector<Object> row0 = rows.get(0);
		
		List<Integer> columnIds = Util.newList();
		columnIds.addAll(row0.fieldIds());
		Collections.sort(columnIds);
		if (columnIds.size() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"No row", 
					"No row", 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		if (rows.size() > 1) {
			MapVector<Object> row1 = rows.get(1);
			Vector<Object> rowData0 = new Vector<Object>(row0.toList(columnIds));
			Vector<Object> rowData1 = new Vector<Object>(row1.toList(columnIds));
			rowData0.insertElementAt(getRatingValueTM().getRowName() + " " + row0.id(), 0);
			rowData1.insertElementAt("", 0);
			
			data.add(rowData0);
			data.add(rowData1);
		}
		else {
			Vector<Object> rowData0 = new Vector<Object>(row0.toList(columnIds));
			rowData0.insertElementAt(getRatingValueTM().getRowName() + " " + row0.id(), 0);
			
			data.add(rowData0);
		}
		
		Vector<String> columnNames = Util.newVector();
		columnNames.add("");
		for (int columnId : columnIds) {
			columnNames.add(getRatingValueTM().getColumnName() + " "  + columnId);
		}
		
		DefaultTableModel dm = new DefaultTableModel(data, columnNames) {

			
			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}
			
			
		};
		JTable tbl = new JTable(dm);
		tbl.setAutoResizeMode(AUTO_RESIZE_OFF);
		
		JDialog viewer = null;
		Dialog dlg = UIUtil.getDialogForComponent(this);
		if (dlg != null)
			viewer = new JDialog(dlg, "Row values", false);
		else
			viewer = new JDialog(UIUtil.getFrameForComponent(this), "Row values", false);

		viewer.setLayout(new BorderLayout());
		viewer.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		viewer.setSize(600, 200);
		
		viewer.add(new JScrollPane(tbl), BorderLayout.CENTER);
		
		TxtOutput txtInfo = new TxtOutput();
		txtInfo.setRows(5);
		txtInfo.setColumns(10);
		viewer.add(new JScrollPane(txtInfo), BorderLayout.SOUTH);
		txtInfo.setEditable(false);
		
		StringBuffer lines = new StringBuffer();
		for (MapVector<Object> row : rows) {
			String line = row.toString(getRatingValueTM().getRowName(), getRatingValueTM().getColumnName());
			lines.append(line + "\n\n");
			
			txtInfo.setText(lines.toString());
		}
		txtInfo.setCaretPosition(0);
		viewer.toFront();
		viewer.setVisible(true);
	}

	
	/**
	 * 
	 * @param comp
	 * @param modal
	 */
	public void showDlg(Component comp, boolean modal) {
		JDialog dlg = new JDialog(UIUtil.getFrameForComponent(comp), "Rating matrix dialog", modal);
		dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dlg.setSize(400, 300);
		dlg.setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		
		dlg.setLayout(new BorderLayout());
		dlg.add(new JScrollPane(this), BorderLayout.CENTER);
		dlg.setVisible(true);
	}
	
	
}



/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class RatingValueTM extends DefaultTableModel {


	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */
	protected List<Integer> columnIds = Util.newList();
	
	
	/**
	 * 
	 */
	protected Map<Integer, Integer> rowIndexes = Util.newMap();
		
	
	/**
	 * 
	 */
	protected Map<Integer, Integer> columnIndexes = Util.newMap();

	
	/**
	 * 
	 */
	public RatingValueTM() {
		super();
	}
	
	
	/**
	 * 
	 * @return row name
	 */
	public String getRowName() {
		return "User";
	}
	
	
	/**
	 * 
	 * @return column name
	 */
	public String getColumnName() {
		return "Item";
	}
	
	
	/**
	 * 
	 * @param vRating
	 * @return a row from {@link RatingVector}
	 */
	public Vector<Object> toRow(RatingVector vRating) {
		Vector<Object> row = new Vector<Object>();
		row.add(getRowName() + " " + vRating.id());
			
		for (int columnId : columnIds) {
			if (!vRating.contains(columnId))
				row.add("");
			else {
				float value = vRating.get(columnId).value;
				if (!Util.isUsed(value))
					row.add("");
				else
					row.add(value);
			}
		}
		
		return row;
	}

	
	/**
	 * 
	 * @param matrix
	 * @param rowId
	 * @return row vector
	 */
	public Vector<Object> toRow(RatingMatrix matrix, int rowId) {
		Vector<Object> row = new Vector<Object>();
		row.add(getRowName() + " "  + rowId);
		
		for (int columnId : columnIds) {
			float value = matrix.getValue(rowId, columnId);
			if (!Util.isUsed(value))
				row.add("");
			else
				row.add(value);
		}
		
		return row;
	}
	
	
	/**
	 * 
	 * @return empty row
	 */
	protected Vector<Object> toRowEmpty() {
		Vector<Object> row = new Vector<Object>();
		row.add("");
		for (int i = 0; i < columnIds.size(); i++) {
			row.add("");
		}
		
		return row;
	}

	
	/**
	 * 
	 * @return column names by column id (s)
	 */
	public Vector<String> toColumns() {
		Vector<String> columns = new Vector<String>();
		columns.add("");
		for (int columnId : columnIds) {
			columns.add(getColumnName() + " "  + columnId);
		}
		return columns;
	}
	
	
	/**
	 * 
	 * @return list of column id (s)
	 */
	public List<Integer> getColumnIds(){
		return columnIds;
	}
	
	
	/**
	 * 
	 * @param dataset
	 * @param moreColumnIds
	 */
	public void update(Dataset dataset, Collection<Integer> moreColumnIds) {
		this.rowIndexes.clear();
		this.columnIndexes.clear();
		this.columnIds.clear();
		this.setDataVector(new String[0][], new String[0][]);
		if (dataset == null)
			return;
		
		FetcherUtil.fillCollection(this.columnIds, dataset.fetchItemIds(), true);
		if (moreColumnIds != null)
			this.columnIds.addAll(moreColumnIds);
		Collections.sort(this.columnIds);
		
		Vector<Vector<Object>> data = Util.newVector();
		
		List<Integer> rowIds = Util.newList();
		FetcherUtil.fillCollection(rowIds, dataset.fetchUserIds(), true);
		Collections.sort(rowIds);
		
		for (int i = 0; i < rowIds.size(); i++) {
			int rowId = rowIds.get(i);
			this.rowIndexes.put(rowId, i);
			RatingVector vRating = dataset.getUserRating(rowId);
			
			Vector<Object> row = vRating != null ? toRow(vRating) : toRowEmpty();
			data.add(row);
		}
		
		for (int i = 0; i < columnIds.size(); i++) {
			int columnId = columnIds.get(i);
			columnIndexes.put(columnId, i + 1);
		}
		this.setDataVector(data, toColumns());
	}
	
	
	/**
	 * 
	 * @param matrix
	 * @param moreColumnIds
	 */
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
			this.rowIndexes.put(rowId, i);
			Vector<Object> row = toRow(matrix, rowId);
			
			data.add(row);
		}
		
		for (int i = 0; i < columnIds.size(); i++) {
			int columnId = columnIds.get(i);
			columnIndexes.put(columnId, i + 1);
		}
		this.setDataVector(data, toColumns());
	}
	
	
	/**
	 * 
	 */
	public void clear() {
		update((Dataset)null, null);
	}
	
	
	/**
	 * 
	 * @param vRat
	 * @param unused
	 */
	public void setMoreRatingVector(RatingVector vRat, Set<Integer> unused) {
		Integer rowIndex = this.rowIndexes.get(vRat.id());
		Object value = null;
		if (rowIndex == null) 
			return;		
		
		Set<Integer> fieldIds = vRat.fieldIds();
		for (int fieldId : fieldIds) {
			Integer columnIndex = columnIndexes.get(fieldId);
			if (columnIndex == null)
				continue;
			value = "[" + vRat.get(fieldId) + "]";
				
			setValueAt(value, rowIndex , columnIndex);
		}
	}
	
	
	/**
	 * 
	 * @param rowIdx
	 * @return a row as list of {@link MapVector}
	 */
	public List<MapVector<Object>> getRow(int rowIdx) {
		
		int rowIdFound = -1;
		Set<Integer> rowIds = this.rowIndexes.keySet();
		for (int rowId : rowIds) {
			if (this.rowIndexes.get(rowId) == rowIdx) {
				rowIdFound = rowId;
				break;
			}
				
		}
		
		if (rowIdFound == -1)
			return Util.newList();
		
		int n = getColumnCount();
		List<MapVector<Object>> result = Util.newList();
		
		MapVector<Object> vector = new MapVector<Object>(rowIdFound);
		for (int i = 1; i < n; i++) {
			Object value = getValueAt(rowIdx, i);
			String strValue = (value != null ? value.toString() : "");
			
			if (strValue.isEmpty()) 
				continue;
			
			int columnId = this.columnIds.get(i - 1);
			vector.put(columnId, value);
		}
		
		result.add(vector);
		
		return result;
	}
	
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}


}
