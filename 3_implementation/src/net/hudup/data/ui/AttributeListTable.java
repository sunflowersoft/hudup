package net.hudup.data.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.hudup.core.Util;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.AttributeList;
import net.hudup.core.logistic.ui.UIUtil;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class AttributeListTable extends JTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */
	private final static Type SUPPORT_TYPE [] = {
		Type.bit, Type.nominal, Type.integer, Type.real, Type.string, Type.date};

	
	/**
	 * 
	 */
	public AttributeListTable() {
		super(new AttributeListTM());
		
		this.setDefaultEditor(Type.class, new TypeCellEditor());
		
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (!getAttributeListTM().enabled)
					return;

				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = createContextMenu();
					if(contextMenu != null) 
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
			}
		});
		
		this.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if (!getAttributeListTM().enabled)
					return;

				if (e.getKeyCode() == KeyEvent.VK_DELETE)
					removeRow();
				else if (e.getKeyCode() == KeyEvent.VK_ADD && e.isControlDown())
					addRow();
			}
			
		});
		
	}

	
	/**
	 * 
	 * @return {@link AttributeListTM}
	 */
	public AttributeListTM getAttributeListTM() {
		return (AttributeListTM)getModel();
	}
	
	
	/**
	 * 
	 * @param attList
	 */
	public void set(AttributeList attList) {
		getAttributeListTM().set(attList);
	}
	
	
	@Override
	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		super.setEnabled(enabled);
		
		getAttributeListTM().setEnabled(enabled);
	}
	
	
	/**
	 * 
	 */
	public void clear() {
		getAttributeListTM().clear();
	}
	
	
	/**
	 * 
	 * @return {@link AttributeList}
	 */
	public AttributeList getAttributeList() {
		return getAttributeListTM().getAttributeList();
	}
	
	
	/**
	 * 
	 * @return whether apply action is successfully
	 */
	public boolean apply() {
		return getAttributeListTM().apply();
	}
	
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		AttributeListTM model = getAttributeListTM();
		TableCellRenderer renderer = getDefaultRenderer(
				model.getColumnClass(row, column));
		
		if(renderer == null) 
			return super.getCellRenderer(row, column);
		else
			return renderer;
	}
	
	
	@Override
    public TableCellEditor getCellEditor(int row, int column) {
        TableCellEditor editor = null;
		AttributeListTM model = getAttributeListTM();
		if (column == 1)
			editor = getDefaultEditor(Type.class);
		else
			editor = getDefaultEditor(model.getColumnClass(row, column));
    	
    	if(editor == null)
    		return super.getCellEditor(row, column);
    	else
    		return editor;
    }

	
	/**
	 * 
	 * @return {@link JPopupMenu}
	 */
	private JPopupMenu createContextMenu() {
		JPopupMenu contextMenu = new JPopupMenu();
		
		JMenuItem miAddRow = UIUtil.makeMenuItem((String)null, "Add", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					addRow();
				}
			});
		contextMenu.add(miAddRow);
		
		JMenuItem miInsertRow = UIUtil.makeMenuItem((String)null, "Insert", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					insertRow();
				}
			});
		contextMenu.add(miInsertRow);

		JMenuItem miClearRowData = UIUtil.makeMenuItem((String)null, "Clear data", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					clearRow();
				}
			});
		contextMenu.add(miClearRowData);
			
		JMenuItem miRemoveRow = UIUtil.makeMenuItem((String)null, "Remove", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					removeRow();
				}
			});
		contextMenu.add(miRemoveRow);

		return contextMenu;
	}

	
	/**
	 * 
	 */
	protected void addRow() {
		Vector<Object> emptyRow = getAttributeListTM().emptyRow();
		getAttributeListTM().addRow(emptyRow);
		
	}
	
	
	/**
	 * 
	 */
	protected void insertRow() {
		int selectedRow = getSelectedRow();
		if (selectedRow == -1)
			return;
		
		Vector<Object> emptyRow = getAttributeListTM().emptyRow();
		getAttributeListTM().insertRow(selectedRow, emptyRow);
	}

	
	/**
	 * 
	 */
	protected void clearRow() {
		int selectedRow = getSelectedRow();
		if (selectedRow == -1)
			return;
		
		getAttributeListTM().setValueAt("", selectedRow, 0);
		getAttributeListTM().setValueAt(Type.integer, selectedRow, 1);
		getAttributeListTM().setValueAt(false, selectedRow, 2);
		getAttributeListTM().setValueAt(false, selectedRow, 3);
	}

	
	/**
	 * 
	 */
	protected void removeRow() {
		int selectedRow = getSelectedRow();
		if (selectedRow == -1)
			return;
		
		if (getRowCount() <= 1) {
			JOptionPane.showMessageDialog(
					this, 
					"Can't remove this row due to one-row table", 
					"Can't remove this row", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		getAttributeListTM().removeRow(selectedRow);
	}

	
	/**
	 * 
	 * @author Loc Nguyen
	 * @version 10.0
	 *
	 */
	public static class TypeCellEditor extends DefaultCellEditor {

		
		/**
		 * Serial version UID for serializable class. 
		 */
		private static final long serialVersionUID = 1L;

		
		/**
		 * 
		 */
		public TypeCellEditor() {
			super(createComboBox());
			
		}
		
		
		/**
		 * 
		 * @return {@link JComboBox}
		 */
		private static JComboBox<Type> createComboBox() {
			JComboBox<Type> comb = new JComboBox<Type>();
			
			for (Type type : SUPPORT_TYPE)
				comb.addItem(type);
			
			return comb;
		}
	}
	
	
}



/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class AttributeListTM extends DefaultTableModel {
	
	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * 
	 */
	public final static int MAX_SIZE = 10;
	
	
	/**
	 * 
	 */
	protected AttributeList attList = null;
	
	
	/**
	 * 
	 */
	protected boolean enabled = true;

	
	/**
	 * 
	 */
	public AttributeListTM() {
		super();
		set(new AttributeList());
	}
	
	
	/**
	 * 
	 * @param attList
	 */
	public void set(AttributeList attList) {
		this.attList = attList;
		
		refresh();
	}
	
	
	/**
	 * 
	 * @return column names
	 */
	private Vector<String> createColumns() {
		Vector<String> columns = Util.newVector();
		
		columns.add("Attribute");
		columns.add("Type");
		columns.add("Is Key");
		columns.add("Auto increment");
		
		return columns;
	}
	
	
	/**
	 * 
	 * @return empty row
	 */
	public Vector<Object> emptyRow() {
		Vector<Object> row = Util.newVector();
		row.add("");
		row.add(Type.integer);
		row.add(false);
		row.add(false);
		
		return row;
	}
	
	
	/**
	 * 
	 */
	public void refresh() {
		Vector<String> columns = createColumns();
		
		Vector<Vector<Object>> data = Util.newVector();
		for (int i = 0; i < attList.size(); i++) {
			Attribute att = attList.get(i);
			
			Vector<Object> row = Util.newVector();
			row.add(att.getName());
			row.add(att.getType());
			row.add(att.isKey());
			row.add(att.isAutoInc());
			
			data.add(row);
		}
		
		int n = MAX_SIZE - data.size();
		for (int i = 0; i < n; i++) {
			Vector<Object> row = emptyRow();
			data.add(row);
		}
		
		setDataVector(data, columns);
	}
	

	/**
	 * 
	 * @param row
	 * @param column
	 * @return class of column
	 */
	public Class<?> getColumnClass(int row, int column) {
		Object value = getValueAt(row, column);
		if (value == null)
			return null;
		else
			return value.getClass();
	}
	
	
	/**
	 * 
	 */
	public void clear() {
		
		Vector<String> columns = createColumns();

		int n = MAX_SIZE;
		Vector<Vector<Object>> data = Util.newVector();
		for (int i = 0; i < n; i++) {
			Vector<Object> row = emptyRow();
			data.add(row);
		}
		
		setDataVector(data, columns);
	}
	
	
	/**
	 * 
	 * @return {@link AttributeList}
	 */
	public AttributeList getAttributeList() {
		return attList;
	}
	
	
	/**
	 * 
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	
	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return (enabled);
	}

	
	/**
	 * 
	 * @return whether apply action successfully
	 */
	public boolean apply() {
		
		attList.clear();
		
		int rows = getRowCount();
		for (int i = 0; i < rows; i++) {
			String name = ((String)getValueAt(i, 0)).trim();
			Type type = (Type)getValueAt(i, 1);
			boolean isKey = (Boolean)getValueAt(i, 2);
			boolean autoInc = (Boolean)getValueAt(i, 3);
			
			if (name.isEmpty())
				continue;
			
			Attribute att = new Attribute(name, type);
			att.setKey(isKey);
			att.setAutoInc(autoInc);
			
			attList.add(att);
		}
		
		return true;
	}
	
	
}