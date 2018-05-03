package net.hudup.data.ctx.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.tree.TreePath;

import net.hudup.core.Constants;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.BooleanWrapper;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.ctx.ContextTemplate;
import net.hudup.core.data.ctx.ContextTemplateSchema;
import net.hudup.core.data.ctx.HierContextTemplate;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.data.ui.AttributeListTable;
import net.hudup.data.ui.AttributeListTable.TypeCellEditor;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.tree.TreeModelSupport;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class CTStable extends JXTreeTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	protected boolean isModified = false;
	
	
	/**
	 * 
	 * @param cts
	 */
	public CTStable(ContextTemplateSchema cts) {
		super(new CTSTableModel(cts));
		
		setDefaultEditor(Type.class, new TypeCellEditor());

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = createContextMenu();
					if(contextMenu != null) 
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
			}
		});
		
	}
	
	
	/**
	 * 
	 * @return {@link JPopupMenu}
	 */
	private JPopupMenu createContextMenu() {
		JPopupMenu contextMenu = new JPopupMenu();
		
		JMenuItem miAddTemplate = UIUtil.makeMenuItem((String)null, "Add", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					addDefaultTemplate();
				}
			});
		contextMenu.add(miAddTemplate);
		
		JMenuItem miRemoveTemplate = UIUtil.makeMenuItem((String)null, "Remove", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					removeSelectedTemplate();
				}
			});
		contextMenu.add(miRemoveTemplate);

		contextMenu.addSeparator();
		
		JMenuItem miConfigProfile = UIUtil.makeMenuItem((String)null, "Configure profile", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					configProfile();
				}
			});
		contextMenu.add(miConfigProfile);
		
		return contextMenu;
	}
	
	
	/**
	 * 
	 */
	public void addDefaultTemplate() {
		CTSTableModel model = getCTSTableModel();
		ContextTemplate child = model.cts().defaultTemplate();

		int selectedRow = getSelectedRow();
		if (selectedRow == -1) {
			isModified |= model.cts().addRoot(child);
		}
		else {
			Object object = getValueAt(selectedRow, 0);
			if (object == model.cts())
				isModified |= model.cts().addRoot(child);
			else if (object instanceof ContextTemplate) {
				ContextTemplate parent = (ContextTemplate)object;
				isModified |= ((HierContextTemplate)parent).addChild(child);
			}
			else {
				JOptionPane.showMessageDialog(this, "Invalid node", "Invalid node", JOptionPane.WARNING_MESSAGE);
				child = null;
			}
		}
		
		if (selectedRow == -1 || child == null)
			reload();
		else {
			TreePath path = getPathForRow(selectedRow);
			ContextTemplate parent = ((HierContextTemplate)child).getParent();
			int index = ((HierContextTemplate)parent).getChildIndex(child.getId());
			model.getModelSupport().fireChildAdded(path, index, child);
		}
	}
	
	
	/**
	 * 
	 */
	public void addTopMostTemplate() {
		CTSTableModel model = getCTSTableModel();
		
		ContextTemplate template = model.cts().defaultTemplate();
		isModified |= model.cts().addRoot(template);
		
		reload();
	}
	
	
	/**
	 * 
	 */
	public void removeSelectedTemplate() {
		int selectedRow = getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(
					this, 
					"Please choose a template", 
					"Please choose a template", 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		Object object = getValueAt(selectedRow, 0);
		if (!(object instanceof ContextTemplate))
			return;
		
		CTSTableModel model = getCTSTableModel();
		ContextTemplate child = (ContextTemplate)object;
		ContextTemplate parent = ((HierContextTemplate)child).getParent();
		int index = -1;
		if (parent == null)
			child = model.cts().removeRootById(child.getId());
		else {
			index = ((HierContextTemplate)parent).getChildIndex(child.getId());
			child = ((HierContextTemplate)parent).removeChildById(child.getId()); 
		}
		
		TreePath path = getPathForRow(selectedRow);
		TreePath parentPath = path.getParentPath();
		if (child == null || parentPath == null || index == -1) {
			reload();
		}
		else {
			model.getModelSupport().fireChildRemoved(parentPath, index, child);
		}
		
		isModified |= (child != null);
	}
	
	
	/**
	 * 
	 */
	public void configProfile() {
		int selectedRow = getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(
					this, 
					"Please choose a template", 
					"Please choose a template", 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		Object object = getValueAt(selectedRow, 0);
		if (!(object instanceof ContextTemplate))
			return;
		
		ContextTemplate template = (ContextTemplate)object;
		
		final JDialog createAttDlg = new JDialog(
				UIUtil.getFrameForComponent(this), "Configure profile", true);
		
		createAttDlg.setLayout(new BorderLayout());
		createAttDlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		createAttDlg.setSize(600, 400);
		
		JPanel body = new JPanel(new BorderLayout());
		createAttDlg.add(body, BorderLayout.CENTER);
		
		final AttributeListTable attributeTable = new AttributeListTable();
		attributeTable.set( (AttributeList) template.getProfileAttributes().clone());
		body.add(new JScrollPane(attributeTable), BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		createAttDlg.add(footer, BorderLayout.SOUTH);
		
		final BooleanWrapper flag = new BooleanWrapper(false);

		JButton create = new JButton("Create");
		create.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				attributeTable.apply();
				flag.set(true);
				createAttDlg.dispose();
			}
		});
		footer.add(create);
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				createAttDlg.dispose();
			}
		});
		footer.add(cancel);

		createAttDlg.setVisible(true);
		if (!flag.get())
			return;
		
		AttributeList attributes = attributeTable.getAttributeList();
		if (attributes.size() > 0) {
			attributes = attributes.nomalizeId(
				DataConfig.CTX_VALUE_FIELD, 
				Attribute.Type.integer, Constants.SUPPORT_AUTO_INCREMENT_ID);
		}
		
		template.setProfileAttributes(attributes);
		isModified |= true;
		reload();
	}
	
	
	/**
	 * 
	 * @return {@link CTSTableModel}
	 */
	public CTSTableModel getCTSTableModel() {
		return (CTSTableModel) getTreeTableModel();
	}
	
	
	/**
	 * 
	 */
	public void reload() {
		int selectedRow = getSelectedRow();
		
		CTSTableModel model = getCTSTableModel();
		TreePath path = selectedRow == -1 ? null : getPathForRow(selectedRow);
		model.getModelSupport().fireNewRoot();
		if (path != null)
			this.expandPath(path);
		
	}
	
	
	/**
	 * 
	 * @return whether model is modified
	 */
	public boolean isModified() {
		return isModified;
	}
	
	
	/**
	 * 
	 * @param isModified
	 */
	public void setModified(boolean isModified) {
		this.isModified = isModified;
	}
	
}



/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class CTSTableModel extends AbstractTreeTableModel {

	
	/**
	 * 
	 * @param cts
	 */
	public CTSTableModel(ContextTemplateSchema cts) {
		super(cts);
	}
	
	
	/**
	 * 
	 * @param cts
	 */
	public void setRoot(ContextTemplateSchema cts) {
		this.root = cts;
		this.modelSupport.fireNewRoot();
	}
	
	
	/**
	 * 
	 * @return {@link ContextTemplateSchema}
	 */
	public ContextTemplateSchema cts() {
		return (ContextTemplateSchema)root;
		
	}
	
	
	/**
	 * 
	 * @param node
	 * @return whether node is {@link ContextTemplate}
	 */
	private boolean isContextTemplate(Object node) {
		if (node instanceof ContextTemplate)
			return true;
		else
			return false;
	}
	
	
	/**
	 * 
	 * @return {@link TreeModelSupport}
	 */
	public TreeModelSupport getModelSupport() {
		return modelSupport;
	}
	
	
	@Override
    public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Name";
		case 1:
			return "Type";
		case 2:
			return "Has profile";
		}
		
		return "";
    }
    
	
    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
        case 0:
            return String.class;
        case 1:
            return Type.class;
        case 2:
            return Boolean.class;
        default:
            return super.getColumnClass(column);
        }
    }

    
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 3;
	}

	
	@Override
	public Object getValueAt(Object node, int column) {
		// TODO Auto-generated method stub
		
		if (!isContextTemplate(node)) {
			if (column == 0)
				return node;
			else
				return "";
		}
		
		ContextTemplate template = (ContextTemplate)node;
		switch (column) {
		case 0:
			return template;
		case 1:
			return template.getAttribute().getType();
		case 2:
			return template.hasProfile();
		}
		
		return null;
	}

	
	@Override
	public Object getChild(Object parent, int index) {
		// TODO Auto-generated method stub
		
		if (!isContextTemplate(parent)) {
			if (parent == cts())
				return cts().getRoot(index);
			else
				return null;
		}
		
		ContextTemplate template = (ContextTemplate)parent;
		return ((HierContextTemplate)template).getChild(index);
	}

	
	@Override
	public int getChildCount(Object parent) {
		// TODO Auto-generated method stub

		if (!isContextTemplate(parent)) {
			if (parent == cts())
				return cts().rootSize();
			else
				return 0;
		}
		
		ContextTemplate template = (ContextTemplate)parent;
		return ((HierContextTemplate)template).getChildSize();
	}

	
	@Override
	public int getIndexOfChild(Object parent, Object child) {
		// TODO Auto-generated method stub
		
		if (!isContextTemplate(parent)) {
			if (parent == cts() && isContextTemplate(child))
				return cts().indexOfRoot( ((ContextTemplate)child).getId() );
			else
				return -1;
		}
		else if (isContextTemplate(child))
			return ((HierContextTemplate)parent).
					 getChildIndex( ((ContextTemplate)child).getId() );
		else
			return -1;
	}

	
	/**
	 * 
	 * @param row
	 * @param column
	 * @return class of column
	 */
	public Class<?> getColumnClass(int row, int column) {
		Object value = getValueAt(row, column);
		return value.getClass();
	}


	@Override
	public boolean isCellEditable(Object node, int column) {
		// TODO Auto-generated method stub
		return column < 2;
	}


	@Override
	public void setValueAt(Object value, Object node, int column) {
		// TODO Auto-generated method stub
		if (!(node instanceof ContextTemplate))
			return;
		
		ContextTemplate template = (ContextTemplate)node;
		switch (column) {
		case 0:
			template.setName(value.toString());
			break;
		case 1:
			template.setAttribute(
					value instanceof Type ? (Type)value : Attribute.fromString(value.toString()));
			break;
		}
	}


	
	
	
}
