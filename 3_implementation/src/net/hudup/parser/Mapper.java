package net.hudup.parser;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public interface Mapper {
	
	/**
	 * 
	 * @param value
	 * @return value
	 */
	float map(float value);
	
	
	/**
	 * 
	 * @param value
	 * @return inverse value
	 */
	float imap(float value);
}
