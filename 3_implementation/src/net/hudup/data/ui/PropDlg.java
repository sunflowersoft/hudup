/**
 * 
 */
package net.hudup.data.ui;

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import net.hudup.core.data.PropList;
import net.hudup.core.data.ui.PropPane;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * @author Loc Nguyen
 * @version 10.0
 */
public class PropDlg extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * 
	 */
	protected PropPane paneProp = null;
	
	
	/**
	 * 
	 * @param comp
	 * @param title
	 */
	public PropDlg(Component comp, String title) {
		super(UIUtil.getFrameForComponent(comp), title, true);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		
		paneProp = new PropPane() {

			
			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void close() {
				// TODO Auto-generated method stub
				dispose();
			}
		}; 
	
		add(paneProp);
		
	}
	
	
	/**
	 * 
	 * @param propList
	 */
	public void update(PropList propList) {
		paneProp.update(propList);
	}
	
	
	
	
}
