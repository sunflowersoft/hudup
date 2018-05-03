/**
 * 
 */
package net.hudup.data.ui;

import javax.swing.JTextField;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class TagTextField extends JTextField {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	protected Object tag = null;
	
	
	/**
	 * 
	 */
	public TagTextField() {
		super();
	}
	
	
	/**
	 * 
	 * @return tag
	 */
	public Object getTag() {
		return tag;
	}
	
	
	/**
	 * 
	 * @param tag
	 */
	public void setTag(Object tag) {
		this.tag = tag;
	}
	
	
	/**
	 * 
	 * @param text
	 * @param tag
	 */
	public void setText(String text, Object tag) {
		super.setText(text);
		this.tag = tag;
	}
	
	
	
}
