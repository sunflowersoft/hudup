/**
 * 
 */
package net.hudup.core.parser;

/**
 * This interface {@link TextParsable} indicates that any class which implements {@link TextParsable} have ability to convert itself into text and vice versa.
 * A class that implements {@link TextParsable} is called TextParsable class.
 * The method {@link #toText()} converts TextParsable object into text and the method {@link #parseText(String)} converts text into TextParsable object. 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface TextParsable {

	
	/**
	 * Converting TextParsable object into text.
	 * @return Text presentation of object.
	 */
	String toText();
	
	
	/**
	 * Converting text into TextParsable object.
	 * @param spec Specified text.
	 */
	void parseText(String spec);
	
	
}




