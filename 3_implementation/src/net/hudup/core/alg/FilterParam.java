package net.hudup.core.alg;

import java.io.Serializable;



/**
 * Before recommendation task is performed (for example, producing a list of recommended items), the filtering tasks are performed.
 * Filtering task is specified in {@code Filter} class.
 * Concretely, filtering task is coded in the method {@code filter(...)} of {@code Filter} class.
 * As a convention, this is called {@code filter parameter}. Filter parameter can contain any information.
 * In current implementation, filter parameter is very simple, which only contains item identifier (item ID).
 * Filter parameter need to be improved in the future.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class FilterParam implements Serializable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Item identifier (item ID).
	 */
	public Integer itemId = null;

	
	/**
	 * Default constructor. This is private constructor and so a filter parameter is created by {@link #create(int)} method.
	 */
	private FilterParam() {
		
	}
	
	
	/**
	 * Creating a filter parameter.
	 * Because the only default constructor is private, this method is used to create a filter parameter.
	 * @param itemId specified item identifier (item ID).
	 * @return new filter parameter.
	 */
	public static FilterParam create(int itemId) {
		FilterParam param = new FilterParam();
		param.itemId = itemId;
		
		return param;
	}
	
	
}
