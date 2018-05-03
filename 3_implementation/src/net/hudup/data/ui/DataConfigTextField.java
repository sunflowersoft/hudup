package net.hudup.data.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.ClipboardUtil;
import net.hudup.core.logistic.ui.UIUtil;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class DataConfigTextField extends TagTextField {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public DataConfigTextField() {
		super();
		// TODO Auto-generated constructor stub
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if (SwingUtilities.isRightMouseButton(e) && getConfig() != null) {
					JPopupMenu contextMenu = createContextMenu();
					if(contextMenu != null) 
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
			}
			
		});
	}

	
	/**
	 * 
	 * @return this
	 */
	private DataConfigTextField getThis() {
		return this;
		
	}

	
	/**
	 * 
	 * @return {@link JPopupMenu}
	 */
	private JPopupMenu createContextMenu() {
		JPopupMenu contextMenu = new JPopupMenu();
		
		JMenuItem miCopyURI2 = UIUtil.makeMenuItem((String)null, "Copy URL", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					ClipboardUtil.util.setText(getThis().getText());
				}
			});
		contextMenu.add(miCopyURI2);
		
		return contextMenu;
	}

	
	/**
	 * 
	 * @param config
	 */
	public void setConfig(DataConfig config) {
		
		tag = config;
		if (config == null)
			setText("");
		else
			setText(config.getUriId().toString());
	}
	
	
	/**
	 * 
	 * @return {@link DataConfig}
	 */
	public DataConfig getConfig() {
		return (DataConfig)tag;
	}
	
	
}
