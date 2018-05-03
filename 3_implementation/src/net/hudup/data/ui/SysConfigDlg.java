package net.hudup.data.ui;

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import net.hudup.core.data.PropList;
import net.hudup.core.data.ui.SysConfigPane;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * 
 * @author Loc Nguyen
 *
 * @version 10.0
 */
public class SysConfigDlg extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	protected SysConfigPane paneSysConfig = null;

	
	/**
	 * 
	 * @param comp
	 * @param title
	 * @param vars
	 */
	public SysConfigDlg(Component comp, String title, Object...vars) {
		super(UIUtil.getFrameForComponent(comp), title, true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		
		init(vars);
	}

	
	/**
	 * 
	 * @param vars
	 */
	protected void init(Object...vars) {
		paneSysConfig = new SysConfigPane() {

			
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
	
		add(paneSysConfig);
	}
	
	
	/**
	 * 
	 * @param propList
	 */
	public void update(PropList propList) {
		
		paneSysConfig.update(propList);
	}
	
	

}
