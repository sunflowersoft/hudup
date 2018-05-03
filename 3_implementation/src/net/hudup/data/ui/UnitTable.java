package net.hudup.data.ui;

import java.awt.Component;

import net.hudup.core.data.Provider;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface UnitTable {

	
	/**
	 * 
	 * @param provider
	 * @param unit
	 */
	void update(Provider provider, String unit);
	
	
	/**
	 * 
	 */
	void clear();
	
	
	/**
	 * 
	 */
	void refresh();
	
	
	/**
	 * 
	 */
	void first();
	
	
	/**
	 * 
	 */
	void last();
	
	
	/**
	 * 
	 * @return {@link Component}
	 */
	Component getComponent();
	
}
