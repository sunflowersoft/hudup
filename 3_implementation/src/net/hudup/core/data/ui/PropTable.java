/**
 * 
 */
package net.hudup.core.data.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.hudup.core.PluginStorage;
import net.hudup.core.RegisterTable;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgDesc;
import net.hudup.core.alg.AlgDescList;
import net.hudup.core.alg.AlgList;
import net.hudup.core.alg.ui.AlgChooser;
import net.hudup.core.data.CData;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.HiddenText;
import net.hudup.core.data.PropList;
import net.hudup.core.data.ctx.CTSManager;
import net.hudup.core.logistic.ClipboardUtil;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.core.logistic.ui.UriChooser;


/**
 * The graphic user interface (GUI) component as table {@link JTable} shows a properties list represented by {@link PropList}.
 * It also allows user to modify, save, and load the property list.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class PropTable extends JTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor
	 */
	public PropTable() {
		super();
		
		this.setModel(new PropTableModel());
		
		this.setDefaultRenderer(HiddenText.class, new HiddenTextCellRenderer());
		this.setDefaultEditor(HiddenText.class, new HiddenTextCellEditor());
		
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if (SwingUtilities.isRightMouseButton(e)) {
					JPopupMenu contextMenu = new JPopupMenu();
					
					JMenuItem miConfig = UIUtil.makeMenuItem( (String)null, "User edit", 
						new ActionListener() {
							
							public void actionPerformed(ActionEvent e) {
								userEdit();
							}
						});
					contextMenu.add(miConfig);
					
					contextMenu.addSeparator();
					
					JMenuItem miCopyClipboard = UIUtil.makeMenuItem((String)null, "Copy to clipboard", 
						new ActionListener() {
							
							public void actionPerformed(ActionEvent e) {
								copyToClipboard();
							}
						});
					contextMenu.add(miCopyClipboard);

					if(contextMenu != null && getPropTableModel().isEnabled()) 
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
					
				}
				else if (e.getClickCount() >= 2) {
					userEdit();
				}
			}
			
		});
	}
	
	
	/**
	 * This is event-driven method that allows user to edit the internal property list.
	 */
	private void userEdit() {
		if (!getPropTableModel().isEnabled())
			return;
		
		int row = getSelectedRow();
		int column = getSelectedColumn();
		if (row != -1 && column != -1 && !getPropTableModel().isCellEditable(row, column)) {
			try {
				getPropTableModel().userEdit(this, row);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	
	/**
	 * Copying the selected row to clip-board.
	 */
	private void copyToClipboard() {
		if (!getPropTableModel().isEnabled())
			return;
		
		int row = getSelectedRow();
		int column = getSelectedColumn();
		if (row == -1 || column != 0)
			return;
		
		Object value = getValueAt(row, 1);
		if (value != null)
			ClipboardUtil.util.setText(value.toString());
		
	}
	
	
	/**
	 * Updating this table by specified property list.
	 * @param propList specified property list.
	 */
	public void update(PropList propList) {
		getPropTableModel().update(propList);
	}
	
	
	/**
	 * Updating this table by specified property list without changing the internal property list.
	 * @param propList specified property list.
	 */
	public void updateNotSetup(PropList propList) {
		getPropTableModel().updateNotSetup(propList);
	}
	
	
	/**
	 * Updating this table by property list located at specified URI.
	 * @param uri URI of property list.
	 */
	public void update(xURI uri) {
		PropTableModel model = getPropTableModel();
		PropList propList = model.getPropList();
		propList.load(uri);
		
		model.update(propList);
	}
	
	
	@Override
	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		super.setEnabled(enabled);
		
		getPropTableModel().setEnabled(enabled);
	}


	/**
	 * Saving the internal property list to the archive (file) located by specified URI.
	 * @param uri URI of the archive (file) where to save the internal property list.
	 */
	public void save(xURI uri) {
		PropTableModel model = getPropTableModel();
		model.save(uri);
	}
	
	
	
	/**
	 * Applying changes on the graphic user interface (GUI) into the internal property list.
	 * @return whether apply successfully.
	 */
	public boolean apply() {
		PropTableModel model = getPropTableModel();
		return model.apply();
	}
	
	
	/**
	 * Getting model of this {@link PropTable}
	 * @return {@link PropTableModel} of this {@link PropTable}.
	 */
	protected PropTableModel getPropTableModel() {
		return (PropTableModel)getModel();
	}
	
	
	/**
	 * Getting the internal property list.
	 * @return internal property list.
	 */
	public PropList getPropList() {
		return getPropTableModel().getPropList();
	}
	
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		PropTableModel model = getPropTableModel();
		TableCellRenderer renderer = getDefaultRenderer(
				model.getColumnClass(row, column));
		if(renderer == null) return super.getCellRenderer(row, column);
		
		return renderer;
	}
	
	
	@Override
    public TableCellEditor getCellEditor(int row, int column) {
		PropTableModel model = getPropTableModel();
    	TableCellEditor editor = getDefaultEditor(model.getColumnClass(row, column));

    	if(editor == null) return super.getCellEditor(row, column);
        return editor;
    }
	
	
	/**
	 * Testing whether table is modified.
	 * @return whether table is modified.
	 */
	public boolean isModified() {
		return getPropTableModel().isModified();
	}
	
	
	/**
	 * Render of hidden text represented by {@link HiddenText} guides how to show a hidden text.
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
		 * Default constructor.
		 */
		public HiddenTextCellRenderer() {
			super();
			// TODO Auto-generated constructor stub
		}
		
		
		/**
		 * Setting value (hidden text) into the cell.
		 */
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
	
	
	/**
	 * Editor of hidden text represented by {@link HiddenText} guides how to edit a hidden text.
	 * 
	 * @author Loc Nguyen
	 * @version 10.0
	 *
	 */
	private class HiddenTextCellEditor extends DefaultCellEditor {

		
		/**
		 * Serial version UID for serializable class. 
		 */
		private static final long serialVersionUID = 1L;

		
		/**
		 * Default constructor. HiddenText is encrypted.
		 */
		public HiddenTextCellEditor() {
			super(new JPasswordField());
		}
		
		
	}
	
	

}


/**
 * This is the model of {@link PropTable} guides how to transfer data from {@link PropList} into {@link PropTable}. 
 * This model also wraps the property list represented by {@link PropList}.
 * 
 * @author Loc Nguyen
 *
 * @version 10.0
 */
class PropTableModel extends DefaultTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * The internal property list.
	 */
	protected PropList propList = null;
	
	
	/**
	 * Whether or not this model is enabled.
	 */
	protected boolean enabled = true;
	
	
	/**
	 * Whether or not this model was modified.
	 */
	protected boolean modified = false;

	
	/**
	 * Default constructor.
	 */
	public PropTableModel() {
		super();
	}
	
	
	/**
	 * Update this model by specified property list.
	 * @param propList specified property list.
	 */
	public void update(PropList propList) {
		this.propList = propList;
		
		updateNotSetup(propList);
		modified = false;
	}

	
	/**
	 * Update this model by specified property list without changing the internal property list {@link #propList}.
	 * @param propList specified property list.
	 */
	public void updateNotSetup(PropList propList) {
		List<String> keys = propList.sortedKeyList();
		
		Vector<String> columns = Util.newVector();
		columns.add("Key");
		columns.add("Value");
		
		Vector<Vector<Serializable>> data = Util.newVector();
		for (String key : keys) {
			Vector<Serializable> row = Util.newVector();
			
			row.add(key);
			row.add(propList.get(key));
			
			data.add(row);
		}
		
		setDataVector(data, columns);
		modified = true;
	}

	
	/**
	 * Getting the internal property list {@link #propList}.
	 * @return internal property list {@link #propList}.
	 */
	public PropList getPropList() {
		return propList;
	}
	
	
	/**
	 * Testing whether this model is enabled.
	 * @return whether this model is enabled.
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	
	/**
	 * Setting whether this model is enabled.
	 * @param enabled if {@code true}, this model is enabled.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	
	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		String key = (String) getValueAt(row, 0);
		Object value = getValueAt(row, 1);
		return  column == 1 && 
					!key.equals(DataConfig.CTS_MANAGER_NAME_FIELD) &&
					!propList.containsReadOnly(key) && 
					!(value instanceof CData) && 
					!(value instanceof PropList) && 
					!(value instanceof Alg) && 
					!(value instanceof AlgList) && 
					!(value instanceof AlgDesc) && 
					!(value instanceof AlgDescList) && 
					enabled;
	}
	
	
	/**
	 * Testing whether this model is modified.
	 * @return whether model is modified.
	 */
	public boolean isModified() {
		return modified;
	}
	
	
	/**
	 * Getting the class of value at specified row and specified column.
	 * @param row specified row.
	 * @param column specified column.
	 * @return class of value at specified row and specified column.
	 */
	public Class<?> getColumnClass(int row, int column) {
		Object value = getValueAt(row, column);
		
		return value.getClass();
	}
	
	
	@Override
	public void setValueAt(Object value, int row, int column) {
		String key = (String) getValueAt(row, 0);
		
		try {
			value = propList.getValueOf(key, (Serializable) value);
			super.setValueAt(value, row, column);
		}
		catch (Throwable e) {
			JOptionPane.showMessageDialog(
					null, 
					"Invalid format", 
					"Invalid format", 
					JOptionPane.ERROR_MESSAGE);
		}
		
		modified = true;
	}
	
	
	/**
	 * Applying changes in the model into the internal property list.
	 * @return whether apply successfully.
	 */
	public boolean apply() {
		PropList newPropList = getModelPropList();
		propList.clear();
		propList.putAll(newPropList);
		
		modified = false;
		return true;
	}
	
	
	/**
	 * Getting the internal property list.
	 * @return internal property list of this model, as {@link PropList}.
	 */
	public PropList getModelPropList() {
		PropList newPropList = new PropList();
		
		int rows = getRowCount();
		for (int i = 0; i < rows; i++) {
			String key = (String)getValueAt(i, 0);
			Object value = getValueAt(i, 1);
			newPropList.put(key, (Serializable) value);
		}
		
		return newPropList;
	}
	
	
	/**
	 * Saving the internal property list to the archive (file) located by specified URI.
	 * @param uri URI of the archive (file) where to save the internal property list.
	 */
	public void save(xURI uri) {
		propList.save(uri);
	}

	
	/**
	 * This method allows user to edit the internal property list at selected row.
	 * @param comp the graphic user interface (GUI) component of this model, which is {@link PropTable}.
	 * @param selectedRow selected row.
	 */
	protected void userEdit(Component comp, int selectedRow) {
		if (propList == null)
			return;
		
		String key = (String) getValueAt(selectedRow, 0);
		Serializable value = (Serializable) getValueAt(selectedRow, 1);
		
		if (value instanceof PropList) {
			PropList propList = (PropList)value;
			PropDlg propDlg = new PropDlg(comp, propList, key);
			PropList result = propDlg.getResult();
			if (result != null)
				setValueAt(result, selectedRow, 1);
		}
		else if (value instanceof AlgList) {
			AlgList algList = (AlgList)value;
			xURI storeUri = propList instanceof DataConfig ?
					((DataConfig)propList).getStoreUri() : null;
			AlgConfigDlg algDlg = new AlgConfigDlg(comp, algList, storeUri, key);
			AlgList result = algDlg.getResult();
			if (result != null && result.size() > 0)
				setValueAt(result, selectedRow, 1);
		}
		else if (value instanceof Alg) {
			AlgList algList = new AlgList((Alg)value);
			xURI storeUri = propList instanceof DataConfig ?
					((DataConfig)propList).getStoreUri() : null;
			AlgConfigDlg algDlg = new AlgConfigDlg(comp, algList, storeUri, key);
			AlgList result = algDlg.getResult();
			if (result != null && result.size() > 0)
				setValueAt(result.get(0), selectedRow, 1);
		}
		else if (key.equals(DataConfig.STORE_URI_FIELD)) {
			xURI defaultUri = xURI.create(value.toString());
			List<xURI> result = UriChooser.chooseUri(comp, defaultUri);
			if (result == null || result.size() == 0)
				return;
			
			xURI uri = result.get(0);
			UriAdapter flatAdapter = new UriAdapter(uri);
			if (flatAdapter.isStore(uri)) {
				setValueAt(uri.toString(), selectedRow, 1);
			}
			else {
				setValueAt(flatAdapter.getStoreOf(uri).toString(), selectedRow, 1);
			}
			
			flatAdapter.close();
		}
		else if (key.equals(DataConfig.CTS_MANAGER_NAME_FIELD)) {
			AlgChooser chooser = new AlgChooser(
					comp, 
					value, 
					((RegisterTable) PluginStorage.getCTSManagerReg().clone()).getAlgList(), 
					null);
			
			CTSManager ctsm = (CTSManager) chooser.getResult();
			if (ctsm != null)
				setValueAt(ctsm, selectedRow, 1);
		}
		else {
			Serializable result = propList.userEdit(comp, key, value);
			if (result != null)
				setValueAt(result, selectedRow, 1);
		}
		
	}
	
	
}


