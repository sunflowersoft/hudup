package net.hudup.core.data.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.table.TableCellRenderer;

import net.hudup.core.Util;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.DataDriverList;
import net.hudup.core.logistic.ui.SortableTable;
import net.hudup.core.logistic.ui.SortableTableModel;
import net.hudup.core.logistic.ui.UIUtil;


/**
 * This graphic user interface (GUI) component as a table shows a list of data drivers ({@link DataDriverList}).
 * This table, called data driver table, can be sorted because it extends the {@link SortableTable} class.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class DataDriverListTable extends SortableTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public DataDriverListTable() {
		super(new DataDriverListTM());
	}
	
	
	/**
	 * Updating this table by specified {@link DataDriverList}.
	 * @param dataDriverList specified {@link DataDriverList}.
	 */
	public void update(DataDriverList dataDriverList) {
		getDataDriverListTM().update(dataDriverList);
		init();
	}
	
	
	/**
	 * Getting data model of this table represented by {@link DataDriverListTM}.
	 * @return data model of this table.
	 */
	public DataDriverListTM getDataDriverListTM() {
		return (DataDriverListTM)getModel();
	}
	
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		DataDriverListTM model = getDataDriverListTM();
		TableCellRenderer renderer = getDefaultRenderer(
				model.getColumnClass(row, column));
		if(renderer == null) return super.getCellRenderer(row, column);
		
		return renderer;
	}

	
	/**
	 * This static method creates a GUI component {@link JPanel} that contains a {@link DataDriverListTable}.
	 * Such {@link DataDriverListTable} contains the specified list of data drivers.
	 * @param dataDriverList specified data driver list.
	 * @return {@link JPanel} that contains {@link DataDriverListTable}.
	 */
	public static JPanel createPane(DataDriverList dataDriverList) {
		
		JPanel result = new JPanel(new BorderLayout());
		
		JPanel body = new JPanel(new BorderLayout());
		result.add(body, BorderLayout.CENTER);
		
		final DataDriverListTable tblDataDriverList = new DataDriverListTable();
		tblDataDriverList.update(dataDriverList);
		body.add(new JScrollPane(tblDataDriverList), BorderLayout.CENTER);
		
		return result;
	}
	
	
	/**
	 * This static method shows a GUI component as dialog that contains a {@link DataDriverListTable}.
	 * Such {@link DataDriverListTable} contains the specified list of data drivers.
	 * @param comp parent component.
	 * @param dataDriverList specified data driver list.
	 * @param modal if {@code true}, the dialog blocks user inputs.
	 */
	public static void showDlg(Component comp, DataDriverList dataDriverList, boolean modal) {
		JDialog dlg = new JDialog(UIUtil.getFrameForComponent(comp), "Register table", modal);
		dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dlg.setSize(600, 400);
		dlg.setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		
		dlg.setLayout(new BorderLayout());
		dlg.add(createPane(dataDriverList), BorderLayout.CENTER);
		
		dlg.setVisible(true);
	}


}


/**
 * This class is the data model of {@link DataDriverListTable}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class DataDriverListTM extends SortableTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public DataDriverListTM() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * Updating this table by specified {@link DataDriverList}.
	 * @param dataDriverList specified {@link DataDriverList}.
	 */
	public void update(DataDriverList dataDriverList) {
		Vector<Vector<Object>> data = Util.newVector();
		
		for (int i = 0; i < dataDriverList.size(); i++) {
			DataDriver dataDriver = dataDriverList.get(i);
			
			Vector<Object> row = Util.newVector();
			row.add(dataDriver.getName());
			row.add(dataDriver.getInnerClass());
			row.add(dataDriver.isFlatServer());
			row.add(dataDriver.isDbServer());
			row.add(dataDriver.isRecommendServer());
			
			data.add(row);
		}
		
		setDataVector(data, toColumns());
	}
	
	
	/**
	 * Creating header names (column names) of the {@link DataDriverListTable}.
	 * @return {@link Vector} of header names. 
	 */
	protected Vector<String> toColumns() {
		Vector<String> columns = Util.newVector();
		columns.add("Name");
		columns.add("Underlying driver class");
		columns.add("Flat");
		columns.add("Database");
		columns.add("Recommender");
		
		return columns;
	}

	
	/**
	 * Getting the class of value at specified row and columns.
	 * @param row specified row.
	 * @param column specified column.
	 * @return class of value at specified row and columns.
	 */
	public Class<?> getColumnClass(int row, int column) {
		Object value = getValueAt(row, column);
		
		return value.getClass();
	}

	
	@Override
	public boolean isSortable(int column) {
		// TODO Auto-generated method stub
		return true;
	}

	
	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}


