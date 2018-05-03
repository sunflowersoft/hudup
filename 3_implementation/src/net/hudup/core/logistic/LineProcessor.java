/**
 * 
 */
package net.hudup.core.logistic;

/**
 * Any class implementing this interface must define the main method {@link #process(String)} to specify how to process a line string.
 * This interface is often used as an input for a method that needs to process a file line by line.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public interface LineProcessor {
	
	/**
	 * Any class implementing this interface must define the main method {@link #process(String)} to specify how to process a line string.
	 * @param line specified string line.
	 */
	void process(String line);
	
}
