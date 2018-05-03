package net.hudup.data.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import net.hudup.core.data.Provider;
import net.hudup.core.data.Unit;
import net.hudup.core.data.ui.UnitListBox;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.data.ProviderImpl;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class UnitListBoxExt extends UnitListBox {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */
	public UnitListBoxExt() {
		super();
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
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
	 * @return context menu
	 */
	private JPopupMenu createContextMenu() {
		if (config == null)
			return null;
		
		JPopupMenu ctxMenu = new JPopupMenu();
		Unit selected = getSelectedValue();
		if (selected == null)
			return null;
		
		JMenuItem miClearData = UIUtil.makeMenuItem((String)null, "Clear data", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					clearData();
				}
			});
		ctxMenu.add(miClearData);
		
		ctxMenu.addSeparator();
		
		JMenuItem miModify = UIUtil.makeMenuItem((String)null, "Modify structure", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					modify();
				}
			});
		ctxMenu.add(miModify);

		ctxMenu.addSeparator();
		
		JMenuItem miDrop = UIUtil.makeMenuItem((String)null, "Drop", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					drop();
				}
			});
		ctxMenu.add(miDrop);
		
		return ctxMenu;
	}
	
	
	/**
	 * 
	 */
	public void clearData() {
		Unit selected = getSelectedValue();
		if (selected == null)
			return;
		
		Provider provider = new ProviderImpl(config);
		
		provider.deleteUnitData(selected.getName());
		provider.close();
	}
	
	
	/**
	 * 
	 */
	public void modify() {
		JOptionPane.showMessageDialog(
			null, 
			"Not implement yet", 
			"Not implement yet", 
			JOptionPane.INFORMATION_MESSAGE);
	}
	
	
	/**
	 * 
	 */
	public void drop() {
		Unit selected = getSelectedValue();
		if (selected == null)
			return;
		
		Provider provider = new ProviderImpl(config);
		
		provider.dropUnit(selected.getName());
		provider.close();
		
		connectUpdate(config);
	}
	

	
	
}
