package net.hudup.data.ui;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * 
 * @author Loc Nguyen
 *
 * @version 10.0
 */
public class StatusBar extends JPanel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	private JLabel[] paneList = new JLabel[5];
	
	
	/**
	 * 
	 */
	public StatusBar() {
		super();
		
		setLayout(new FlowLayout(FlowLayout.LEFT));
		for (int i = 0; i < paneList.length; i++) {
			if ( i > 0)
				add(new JLabel("  "));
			paneList[i] = new JLabel();
			add(paneList[i]);
		}
		
		
	}
	

	/**
	 * 
	 * @param text
	 */
	public void setTextPane0(String text) {
		paneList[0].setText(text);
	}
	
	
	/**
	 * 
	 * @param text
	 */
	public void setTextPane1(String text) {
		paneList[1].setText(text);
	}
	
	
	/**
	 * 
	 * @param text
	 */
	public void setTextPane2(String text) {
		paneList[2].setText(text);
	}

	
	/**
	 * 
	 * @param text
	 */
	public void setTextPane3(String text) {
		paneList[3].setText(text);
	}

	
	/**
	 * 
	 * @param text
	 */
	public void setTextPane4(String text) {
		paneList[4].setText(text);
	}

	
	/**
	 * 
	 * @param text
	 */
	public void setTextPaneLast(String text) {
		setTextPane4(text);
	}
	
	
	/**
	 * 
	 */
	public void clearText() {
		for (JLabel textPane : paneList) {
			textPane.setText("");
		}
	}
	
	
	/**
	 * 
	 * @return pane 0
	 */
	public JLabel getPane0() {
		return paneList[0];
	}
	
	
	/**
	 * 
	 * @return pane 1
	 */
	public JLabel getPane1() {
		return paneList[1];
	}


	/**
	 * 
	 * @return pane 2
	 */
	public JLabel getPane2() {
		return paneList[2];
	}


	/**
	 * 
	 * @return pane 3
	 */
	public JLabel getPane3() {
		return paneList[3];
	}
	
	
	/**
	 * 
	 * @return pane 4
	 */
	public JLabel getPane4() {
		return paneList[4];
	}
	
	
	/**
	 * 
	 * @return last pane
	 */
	public JLabel getLastPane() {
		return getPane4();
	}

	
	
}
