package net.hudup.core.parser;


/**
 * Any class that implements this interface, called HTML parsable class, is able to transform (parse or convert) itself into text form in HTML format.
 * The main method {@link #toHtml()} is responsible for parsing itself into HTML text.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface HtmlParsable {

	
	/**
	 * Any class that implements {@code HtmlParsable} interface must define this method to parse itself into HTML text.
	 * @return HTML text of this HTML parsable object.
	 */
	String toHtml();
	
	
}
