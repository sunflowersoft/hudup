package net.hudup.listener.ui;

import java.util.Vector;

import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import net.hudup.core.Util;
import net.hudup.core.data.HiddenText;
import net.hudup.core.logistic.ui.SortableTable;
import net.hudup.core.logistic.ui.SortableTableModel;
import net.hudup.listener.RemoteInfo;
import net.hudup.listener.RemoteInfoList;



/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class RemoteInfoTable extends SortableTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */
	public RemoteInfoTable() {
		super(new RemoteInfoTM());
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		this.setDefaultRenderer(HiddenText.class, new HiddenTextCellRenderer());
	}
	
	
	/**
	 * 
	 * @return {@link RemoteInfoTM}
	 */
	public RemoteInfoTM getRemoteInfoTM() {
		return (RemoteInfoTM)this.getModel();
		
	}
	
	
	@Override
	protected void init() {
		// TODO Auto-generated method stub
		super.init();
		
	}

	
	/**
	 * 
	 * @param rInfoList
	 */
	public void update(RemoteInfoList rInfoList) {
		getRemoteInfoTM().update(rInfoList);
		init();
	}
	
	
	/**
	 * 
	 * @return {@link RemoteInfo}
	 */
	public RemoteInfo getSelectedRemoteInfo() {
		int row = getSelectedRow();
		if (row == -1)
			return null;
		
		return getRemoteInfoTM().getRemoteInfo(row);
	}


	/**
	 * 
	 * @param host
	 * @param port
	 */
	public void selectRemoteInfo(String host, int port) {
		int n = getRowCount();
		int selected = -1;
		for (int i = 0; i < n; i++) {
			String h = (String)getValueAt(i, 1);
			int p = (Integer)getValueAt(i, 2);
			
			if (h.compareToIgnoreCase(host) == 0 && p == port) {
				selected = i;
				break;
			}
		}
		
		if (selected != -1)
			this.getSelectionModel().addSelectionInterval(selected, selected);
	}
	
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		RemoteInfoTM model = getRemoteInfoTM();
		TableCellRenderer renderer = getDefaultRenderer(
				model.getColumnClass(row, column));
		if(renderer == null) 
			return super.getCellRenderer(row, column);
		
		return renderer;
	}
	
	
	/**
	 * 
	 * @author Loc Nguyen
	 * @version 10.0
	 *
	 */
	private class HiddenTextCellRenderer extends DefaultTableCellRenderer.UIResource {

		
		/**
		 * Serial version UID for serializable class. 
		 */
		private static final long serialVersionUID = 1L;

		
		/**
		 * 
		 */
		public HiddenTextCellRenderer() {
			super();
			// TODO Auto-generated constructor stub
		}
		
		
		@Override
        public void setValue(Object value) {
            if (value == null) {
                setText("");
            }
            else if (value instanceof HiddenText) {
                setText( ((HiddenText)value).getMask() );
            }
            else {
            	HiddenText hidden = new HiddenText(value.toString());
            	setText(hidden.getMask());
            }
        }
		
	}
	
	
	
}


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class RemoteInfoTM extends SortableTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public RemoteInfoTM() {
		super();
	}
	
	
	
	/**
	 * 
	 * @return vector of column names
	 */
	private Vector<String> createColumns() {
		Vector<String> columns = Util.newVector();
		
		columns.add("No");
		columns.add("Host");
		columns.add("Port");
		columns.add("Account");
		columns.add("Password");
		
		return columns;
	}
	
	
	/**
	 * 
	 * @param rInfoList
	 */
	public void update(RemoteInfoList rInfoList) {
		Vector<String> columns = createColumns();
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		for (int i = 0; i < rInfoList.size(); i++) {
			RemoteInfo rInfo = rInfoList.get(i);
			
			Vector<Object> row = Util.newVector();
			
			row.add(i + 1);
			row.add(rInfo.host);
			row.add(rInfo.port);
			row.add(rInfo.account);
			row.add(rInfo.password);
				
			data.add(row);
		}
		
		setDataVector(data, columns);
	}
	
	
	
	/**
	 * 
	 * @param row
	 * @return {@link RemoteInfo}
	 */
	public RemoteInfo getRemoteInfo(int row) {
		String host = (String) getValueAt(row, 1);
		int port = (Integer) getValueAt(row, 2);
		String account = (String) getValueAt(row, 3);
		HiddenText password = (HiddenText) getValueAt(row, 4);
		return new RemoteInfo(host, port, account, password);
		
	}
	
	
	/**
	 * 
	 * @param host
	 * @param port
	 * @return {@link RemoteInfo}
	 */
	public RemoteInfo getRemoteInfo(String host, int port) {
		int n = getRowCount();
		for (int i = 0; i < n; i++) {
			RemoteInfo rInfo = getRemoteInfo(i);
			if (rInfo.host.compareToIgnoreCase(host) == 0 && rInfo.port == port)
				return rInfo;
		}
		
		return null;
	}
	
	
	/**
	 * 
	 * @return {@link RemoteInfoList}
	 */
	public RemoteInfoList getRemoteInfoList() {
		RemoteInfoList rInfoList = new RemoteInfoList();
		int n = getRowCount();
		for (int i = 0; i < n; i++) {
			RemoteInfo rInfo = getRemoteInfo(i);
			rInfoList.add(rInfo);
		}
		
		return rInfoList;
	}
	
	
	
	/**
	 * 
	 * @param row
	 * @param column
	 * @return class of column value
	 */
	public Class<?> getColumnClass(int row, int column) {
		Object value = getValueAt(row, column);
		
		return value.getClass();
	}
	
	
	
	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isSortable(int column) {
		// TODO Auto-generated method stub
		return true;
	}


}
