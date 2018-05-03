package net.hudup.data.ctx.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.ctx.ContextTemplate;
import net.hudup.core.data.ctx.ContextTemplateSchema;
import net.hudup.core.data.ctx.HierContextTemplate;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.data.ui.AttributeListTable;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.tree.TreeModelSupport;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class CTSviewer extends JXTreeTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 * @param cts
	 */
	public CTSviewer(ContextTemplateSchema cts) {
		super(new CTSViewerModel(cts));
		
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
	 * @return this viewer
	 */
	private CTSviewer getThis() {
		return this;
	}
	
	
	/**
	 * 
	 * @return selected template
	 */
	public ContextTemplate getSelectedTemplate() {
		int selectedRow = getSelectedRow();
		if (selectedRow == -1)
			return null;
		
		TreePath path = getPathForRow(selectedRow);
		if (path == null || path.getLastPathComponent() == null || 
				!(path.getLastPathComponent() instanceof ContextTemplate) )
			return null;
		
		return (ContextTemplate)path.getLastPathComponent();
	}
	
	
	/**
	 * 
	 * @return {@link JPopupMenu}
	 */
	private JPopupMenu createContextMenu() {
		final ContextTemplate template = getSelectedTemplate();
		if (template.getProfileAttributes().size() == 0)
			return null;
		
		JPopupMenu contextMenu = new JPopupMenu();
		
		JMenuItem miProfile = UIUtil.makeMenuItem((String)null, "Profile", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					final JDialog dlg = new JDialog(UIUtil.getFrameForComponent(getThis()), "Profile", true);
					dlg.setSize(400, 300);
					dlg.setLocationRelativeTo(UIUtil.getFrameForComponent(getThis()));
					
					dlg.setLayout(new BorderLayout());
					
					AttributeListTable attList = new AttributeListTable();
					attList.setEnabled(false);
					attList.set(template.getProfileAttributes());
					dlg.add(new JScrollPane(attList), BorderLayout.CENTER);
					
					dlg.setVisible(true);
				}
			});
		contextMenu.add(miProfile);
		
		
		return contextMenu;
	}
	
	
	
}



/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class CTSViewerModel extends AbstractTreeTableModel {

	
	/**
	 * 
	 * @param cts
	 */
	public CTSViewerModel(ContextTemplateSchema cts) {
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
		return false;
	}



}
