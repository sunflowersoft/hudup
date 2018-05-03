/**
 * 
 */
package net.hudup.core.data;

import java.io.Serializable;
import java.util.Random;

/**
 * This class represents a hidden text. It wraps the real content by a mask.
 * The real content is stored in its main private variable {@link #text}.
 * The mask is a string of characters * shown by returned value of method {@link #getMask()}.
 * This class is often used to store password.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class HiddenText implements Serializable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * This is main variable of hidden text. It represents the real content.
	 */
	private String text = null;
	
	
	/**
	 * Default constructor.
	 */
	public HiddenText() {
		
	}

	
	/**
	 * Constructor with real text. The specified text will be assigned to the real content {@link #text}.
	 * 
	 * @param text Specified text
	 */
	public HiddenText(String text) {
		this.text = text;
	}
	
	
	/**
	 * Showing the appearance of the real content {@link #text}.
	 * Concretely, the method returns a string of characters *, called mask.
	 * The length of mask ranges from 0 to 7.
	 * @return Mask of real content {@link #text}, which is a string of characters *
	 */
	public String getMask() {
		StringBuffer hidden = new StringBuffer();
		Random rnd = new Random();
		int n = rnd.nextInt(8);
		for (int i = 0; i < n; i++)
			hidden.append("*");
		
		return hidden.toString();
	}
	
	
	/**
	 * Getting real content {@link #text}.
	 * @return Real content text
	 */
	public String getText() {
		return text;
	}
	
	
	/**
	 * Setting real content. The specified text will be assigned to the real content {@link #text}.
	 * @param text Specified text
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	
	
	@Override
	public String toString() {
		return getText();
	}
	
	
	
}
