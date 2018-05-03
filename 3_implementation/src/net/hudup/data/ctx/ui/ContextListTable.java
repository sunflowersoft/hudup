package net.hudup.data.ctx.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
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
import net.hudup.core.data.ctx.Context;
import net.hudup.core.data.ctx.ContextList;
import net.hudup.core.data.ctx.ContextTemplate;
import net.hudup.core.data.ctx.ContextTemplateSchema;
import net.hudup.core.data.ctx.ContextValue;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.data.ctx.ContextValueImpl;
import net.hudup.data.ui.AttributeListTable.TypeCellEditor;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ContextListTable extends JTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 * @param cts
	 */
	public ContextListTable(ContextTemplateSchema cts) {
		super(new ContextListTM(cts));
		
		this.setDefaultEditor(Type.class, new TypeCellEditor());
		this.setDefaultEditor(ContextTemplate.class, new ContextTemplateCellEditor(cts));
		
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (!getContextListTM().enabled)
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
				if (!getContextListTM().enabled)
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
	 * @return {@link ContextListTM}
	 */
	public ContextListTM getContextListTM() {
		return (ContextListTM) getModel();
	}
	
	
	/**
	 * 
	 * @param ctxList
	 */
	public void set(ContextList ctxList) {
		getContextListTM().set(ctxList);
	}
	
	
	@Override
	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		super.setEnabled(enabled);
		
		getContextListTM().setEnabled(enabled);
	}
	
	
	/**
	 * 
	 */
	public void clear() {
		getContextListTM().clear();
	}
	
	
	/**
	 * 
	 * @return {@link ContextList}
	 */
	public ContextList getContextList() {
		return getContextListTM().getContextList();
	}
	
	
	/**
	 * 
	 * @return whether apply action is successfully
	 */
	public boolean apply() {
		return getContextListTM().apply();
	}
	
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		ContextListTM model = getContextListTM();
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
        
		ContextListTM model = getContextListTM();
		if (column == 0)
			editor = getDefaultEditor(ContextTemplate.class);
		else if (column == 1)
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

		contextMenu.addSeparator();
		
		JMenuItem miSelectTemplate = UIUtil.makeMenuItem((String)null, "Select template", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					selectTemplate();
				}
			});
		contextMenu.add(miSelectTemplate);
		
		
		return contextMenu;
	}

	
	/**
	 * 
	 */
	protected void addRow() {
		Vector<Object> emptyRow = getContextListTM().emptyRow();
		getContextListTM().addRow(emptyRow);
		
	}
	
	
	/**
	 * 
	 */
	protected void insertRow() {
		int selectedRow = getSelectedRow();
		if (selectedRow == -1)
			return;
		
		Vector<Object> emptyRow = getContextListTM().emptyRow();
		getContextListTM().insertRow(selectedRow, emptyRow);
	}

	
	/**
	 * 
	 */
	protected void clearRow() {
		int selectedRow = getSelectedRow();
		if (selectedRow == -1)
			return;
		
		getContextListTM().setValueAt(null, selectedRow, 0);
		getContextListTM().setValueAt(Type.real, selectedRow, 1);
		getContextListTM().setValueAt(null, selectedRow, 2);
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
		
		getContextListTM().removeRow(selectedRow);
	}

	
	/**
	 * 
	 */
	protected void selectTemplate() {
		int selectedRow = getSelectedRow();
		if (selectedRow == -1)
			return;
		
		CTselector selector = new CTselector(this, getContextListTM().getCTS());
		ContextTemplate selectedTemplate = selector.getSelectedTemplate();
		if (selectedTemplate == null)
			return;
		
		getContextListTM().setValueAt(selectedTemplate, selectedRow, 0);
	}

	
	/**
	 * 
	 * @author Loc Nguyen
	 * @version 10.0
	 *
	 */
	public static class ContextTemplateCellEditor extends DefaultCellEditor {

		
		/**
		 * Serial version UID for serializable class. 
		 */
		private static final long serialVersionUID = 1L;

		
		/**
		 * 
		 */
		public ContextTemplateCellEditor(ContextTemplateSchema cts) {
			super(createComboBox(cts));
			
		}
		
		
		/**
		 * 
		 * @return {@link JComboBox}
		 */
		private static JComboBox<ContextTemplate> createComboBox(ContextTemplateSchema cts) {
			ContextTemplate[] templates = cts.getAllTemplates();
			Arrays.sort(templates, new Comparator<ContextTemplate>() {

				@Override
				public int compare(ContextTemplate template1, ContextTemplate template2) {
					// TODO Auto-generated method stub
					return template1.getName().compareTo(template2.getName());
				}
				
			});
			
			JComboBox<ContextTemplate> comb = new JComboBox<ContextTemplate>(templates);
			
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
class ContextListTM extends DefaultTableModel {

	
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
	protected ContextList ctxList = null;
	
	
	/**
	 * 
	 */
	protected ContextTemplateSchema cts = null;
	
	
	/**
	 * 
	 */
	protected boolean enabled = true;

	
	/**
	 * 
	 */
	public ContextListTM(ContextTemplateSchema cts) {
		super();
		this.cts = cts;
		
		set(new ContextList());
	}
	
	
	/**
	 * 
	 * @param ctxList
	 */
	public void set(ContextList ctxList) {
		this.ctxList = ctxList;
		
		refresh();
	}
	
	
	/**
	 * 
	 * @return column names
	 */
	private Vector<String> createColumns() {
		Vector<String> columns = Util.newVector();
		
		columns.add("Context template");
		columns.add("Value type");
		columns.add("Value");
		
		return columns;
	}
	
	
	/**
	 * 
	 * @return empty row
	 */
	public Vector<Object> emptyRow() {
		Vector<Object> row = Util.newVector();
		row.add(null);
		row.add(Type.real);
		row.add(null);
		
		return row;
	}
	
	
	/**
	 * 
	 */
	public void refresh() {
		Vector<String> columns = createColumns();
		
		Vector<Vector<Object>> data = Util.newVector();
		for (int i = 0; i < ctxList.size(); i++) {
			Context context = ctxList.get(i);
			
			Vector<Object> row = Util.newVector();
			row.add(context.getTemplate());
			if (context.isNullValue()) {
				row.add(Type.real);
				row.add(null);
			}
			else {
				ContextValue ctxValue = context.getValue(); 
				row.add(Attribute.fromObject(ctxValue));
				row.add(ctxValue.getValue());
			}
			
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
	 * @return {@link ContextList}
	 */
	public ContextList getContextList() {
		return ctxList;
	}
	
	
	/**
	 * 
	 * @return {@link ContextTemplateSchema}
	 */
	public ContextTemplateSchema getCTS() {
		return cts;
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
		
		ctxList.clear();
		
		int rows = getRowCount();
		for (int i = 0; i < rows; i++) {
			ContextTemplate template = (ContextTemplate)getValueAt(i, 0);
			if (template == null)
				continue;
			
			Type type = (Type)getValueAt(i, 1); 
			Object value = getValueAt(i, 2); 
			Context context = null;
			if (value == null) {
				context = Context.create(template);
			}
			else if (value instanceof Serializable) {
				ContextValue ctxValue = ContextValueImpl.create(type, (Serializable) value); 
				context = Context.create(template, ctxValue);
			}
			else
				continue;
			
			ctxList.add(context);
		}
		
		
		return true;
	}
	
	
}