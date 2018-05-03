/**
 * 
 */
package net.hudup.data.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import net.hudup.core.logistic.ClipboardUtil;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * @author Loc Nguyen
 * @version 10.0
 */
public class TxtOutput extends JTextArea {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public TxtOutput() {
		super();
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = createOutputContextMenu();
					if(contextMenu != null) 
						contextMenu.show((Component)e.getSource(), 
								e.getX(), e.getY());
				}
			}
			
		});
		
	}

	
	/**
	 * 
	 */
	protected void copyOutputToClipboard() {
		
		ClipboardUtil.util.setText(getText());
	}
	
	
	/**
	 * 
	 */
	protected void save() {
		UriAdapter adapter = new UriAdapter(); 
		xURI uri = adapter.chooseDefaultUri(this, false, null);
		adapter.close();
		
		if (uri == null) {
			JOptionPane.showMessageDialog(
					this, 
					"Not saved URI", 
					"Not saved URI", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		adapter = new UriAdapter(uri); 
		boolean ret = adapter.saveText(uri, getText(), false);
		if (ret) {
			JOptionPane.showMessageDialog(
					this, 
					"URI saved successfully", 
					"URI saved successfully", 
					JOptionPane.INFORMATION_MESSAGE);
		}
		adapter.close();
	}

	
	/**
	 * 
	 * @return {@link JPopupMenu}
	 */
	protected JPopupMenu createOutputContextMenu() {
		JPopupMenu contextMenu = new JPopupMenu();
		
		JMenuItem miClipCopy = UIUtil.makeMenuItem((String)null, "Copy to clipboard", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					copyOutputToClipboard();
				}
			});
		contextMenu.add(miClipCopy);
		
		contextMenu.addSeparator();
		
		JMenuItem miSave = UIUtil.makeMenuItem((String)null, "Save", 
			new ActionListener() {
					
				public void actionPerformed(ActionEvent e) {
					save();
				}
			});
		contextMenu.add(miSave);

		contextMenu.addSeparator();

		JMenuItem miClear = UIUtil.makeMenuItem((String)null, "Clear", 
			new ActionListener() {
					
				public void actionPerformed(ActionEvent e) {
					clear();
				}
			});
		contextMenu.add(miClear);

		return contextMenu;
	}
	
	
	/**
	 * 
	 */
	
	private void clear() {
		setText("");
	}
	
	
	
}
