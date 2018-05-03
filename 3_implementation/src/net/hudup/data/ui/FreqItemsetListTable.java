/**
 * 
 */
package net.hudup.data.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import net.hudup.core.data.Pair;
import net.hudup.core.logistic.MathUtil;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.data.MapVector;
import net.hudup.data.bit.BitData;
import net.hudup.data.bit.BitDataUtil;
import net.hudup.data.bit.BitItemset;

/**
 * @author Loc Nguyen
 * @version 10.0
 */
public class FreqItemsetListTable extends JTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
    /**
	 * 
	 * 
	 */
	public FreqItemsetListTable() {
		super();
		setModel(new FreqItemsetListTM());
		
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
	 * @param itemsetList
	 * @param bitData
	 */
	public void update(List<BitItemset> itemsetList, BitData bitData) {
		FreqItemsetListTM model = getTableModel();
		model.update(itemsetList, bitData);
	}
	
	
	/**
	 * 
	 * @param itemsetList
	 * @param bitItemMap has: key is bit item id and value is Pair (real id, rating value)
	 */
	public void update(List<BitItemset> itemsetList, Map<Integer, Pair> bitItemMap) {
		FreqItemsetListTM model = getTableModel();
		model.update(itemsetList, bitItemMap);
	}

	
	/**
	 * 
	 */
	public void clear() {
		FreqItemsetListTM model = getTableModel();
		model.clear();
		
	}
	
	
	/**
	 * 
	 * @return {@link FreqItemsetListTM}
	 */
	public FreqItemsetListTM getTableModel() {
		return (FreqItemsetListTM)super.getModel();
	}
	
	
	/**
	 * 
	 * @return list of {@link BitItemset}
	 */
	public List<BitItemset> getItemsetList() {
		return getTableModel().getItemsetList();
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
		if (selIdx == -1)
			return;
		
		MapVector<Object> row = getTableModel().getRow(selIdx);
		
		List<Integer> columnIds = Util.newList();
		columnIds.addAll(row.fieldIds());
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
		Vector<Object> rowData = new Vector<Object>(row.toList(columnIds));
		rowData.insertElementAt(getTableModel().getValueAt(selIdx, 1), 0);
		rowData.insertElementAt(getTableModel().getValueAt(selIdx, 0), 0);
		data.add(rowData);
		
		Vector<String> columnNames = FreqItemsetListTM.toColumns(columnIds);
		
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
			
		viewer.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		viewer.setSize(600, 200);
		viewer.setLayout(new BorderLayout());
		
		viewer.add(new JScrollPane(tbl), BorderLayout.CENTER);
		
		TxtOutput txtInfo = new TxtOutput();
		txtInfo.setRows(5);
		txtInfo.setColumns(10);
		viewer.add(new JScrollPane(txtInfo), BorderLayout.SOUTH);
		txtInfo.setEditable(false);
		
		String line = row.toString("Frequent itemset ", "");
		line += "\nThe support is " + getTableModel().getValueAt(selIdx, 1);
		
		txtInfo.setText(line);
		txtInfo.setCaretPosition(0);
		
		viewer.setVisible(true);
	}
	
}



/**
 * @author Loc Nguyen
 * @version 10.0
 */
class FreqItemsetListTM extends DefaultTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	protected List<BitItemset> itemsetList = Util.newList();
	
	
	/**
	 * 
	 */
	protected List<Integer> columnIds = Util.newList();
	
			
	/**
	 * 
	 */
	protected Map<Integer, Integer> columnIndexes = Util.newMap();

	
	/**
	 * 
	 */
	public FreqItemsetListTM() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * 
	 * @param columnIds
	 * @return column names
	 */
	public static Vector<String> toColumns(List<Integer> columnIds) {
		Vector<String> columns = new Vector<String>();
		columns.add("");
		columns.add("Support");
		for (int columnId : columnIds) {
			columns.add("Item " + columnId);
		}
		return columns;
	}

	
	/**
	 * 
	 * @param idx
	 * @param itemset
	 * @param columnIds
	 * @param bitData
	 * @return a row
	 */
	public static Vector<Object> toRow(
			int idx,
			BitItemset itemset, 
			List<Integer> columnIds, 
			BitData bitData) {
		
		Vector<Object> row = new Vector<Object>();
		row.add("Frequent itemset " + (idx + 1));
		row.add(MathUtil.round(itemset.getSupport()));
			
		for (int columnId : columnIds) {
			int found = itemset.indexOfReal(columnId, bitData);
			
			if (found != -1) {
				int bitItemId = itemset.get(found);
				float value = bitData.get(bitItemId).pair().value();
				row.add(MathUtil.round(value));
			}
			else {
				row.add("");
			}
		}
		
		return row;
	}
	
	
	/**
	 * 
	 * @param idx
	 * @param itemset
	 * @param columnIds
	 * @param bitItemMap has: key is bit item id and value is Pair (real id, rating value)
	 * @return
	 */
	public static Vector<Object> toRow(
			int idx,
			BitItemset itemset, 
			List<Integer> columnIds, 
			Map<Integer, Pair> bitItemMap) {
		
		Vector<Object> row = new Vector<Object>();
		row.add("Frequent itemset " + (idx + 1));
		row.add(MathUtil.round(itemset.getSupport()));
		
		for (int columnId : columnIds) {
			List<Integer> bitItemIdList = BitDataUtil.findBitItemIdOf(bitItemMap, columnId);
			if (bitItemIdList.size() == 0)
				continue;
			
			int bitItemIdFound = -1;
			for (int bitItemId : bitItemIdList) {
				if (itemset.indexOf(bitItemId) != -1) {
					bitItemIdFound = bitItemId;
					break;
				}
			}
			if (bitItemIdFound == -1)
				continue;
			
			if (bitItemMap.containsKey(columnId))
				row.add(MathUtil.round(bitItemMap.get(bitItemIdFound).value()));
			else
				row.add("");
		}
		
		return row;
	}

	
	/**
	 * 
	 * @param itemsetList
	 * @param bitData
	 */
	public void update(List<BitItemset> itemsetList, BitData bitData) {
		this.itemsetList.clear();
		this.columnIds.clear();
		this.columnIndexes.clear();
		this.setDataVector(new String[0][], new String[0][]);
		this.itemsetList.clear();
		
		if (itemsetList == null || bitData == null)
			return;
		
		this.itemsetList = itemsetList;
		
		Set<Integer> realItemIds = Util.newSet();
		for (BitItemset itemset : itemsetList) {
			List<Pair> pairList = itemset.toItemPairList(bitData);
			
			for (Pair pair : pairList)
				realItemIds.add(pair.key());
		}
		this.columnIds.clear();
		this.columnIds.addAll(realItemIds);
		Collections.sort(this.columnIds);
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		
		for (int i = 0; i < itemsetList.size(); i++) {
			BitItemset itemset = itemsetList.get(i);
			
			Vector<Object> row = toRow(i, itemset, this.columnIds, bitData);
			data.add(row);
		}
		
		for (int i = 0; i < columnIds.size(); i++) {
			int columnId = columnIds.get(i);
			columnIndexes.put(columnId, i + 1);
		}
		this.setDataVector(data, toColumns(this.columnIds));
		
	}
	
	
	/**
	 * 
	 * @param itemsetList
	 * @param bitItemMap has: key is bit item id and value is Pair (real id, rating value)
	 */
	public void update(List<BitItemset> itemsetList, Map<Integer, Pair> bitItemMap) {
		this.itemsetList.clear();
		this.columnIds.clear();
		this.columnIndexes.clear();
		this.setDataVector(new String[0][], new String[0][]);
		this.itemsetList.clear();
		
		if (itemsetList == null || bitItemMap == null)
			return;
		
		this.itemsetList = itemsetList;
		
		Set<Integer> realItemIds = Util.newSet();
		for (BitItemset itemset : itemsetList) {
			List<Pair> pairList = BitDataUtil.toItemPairList(bitItemMap, itemset);
			
			for (Pair pair : pairList)
				realItemIds.add(pair.key());
		}
		this.columnIds.clear();
		this.columnIds.addAll(realItemIds);
		Collections.sort(this.columnIds);
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		
		for (int i = 0; i < itemsetList.size(); i++) {
			BitItemset itemset = itemsetList.get(i);
			
			Vector<Object> row = toRow(i, itemset, this.columnIds, bitItemMap);
			data.add(row);
		}
		
		for (int i = 0; i < columnIds.size(); i++) {
			int columnId = columnIds.get(i);
			columnIndexes.put(columnId, i + 1);
		}
		this.setDataVector(data, toColumns(this.columnIds));
		
	}

	
	/**
	 * 
	 */
	public void clear() {
		update(null, (BitData) null);
	}
	
	
	
	/**
	 * 
	 * @param row
	 * @return a {@link MapVector}
	 */
	public MapVector<Object> getRow(int row) {
		MapVector<Object> vector = new MapVector<Object>(row + 1);
		
		int n = getColumnCount();
		for (int i = 2; i < n; i++) {
			Object value = getValueAt(row, i);
			String strValue = (value != null ? value.toString() : "");
			
			if (strValue.isEmpty()) 
				continue;
			
			int columnId = this.columnIds.get(i - 2);
			vector.put(columnId, value);
		}
		
		return vector;
	}


	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return false;
	}

	
	/**
	 * 
	 * @return list of {@link BitItemset}
	 */
	public List<BitItemset> getItemsetList() {
		return itemsetList;
	}
	
	
}
