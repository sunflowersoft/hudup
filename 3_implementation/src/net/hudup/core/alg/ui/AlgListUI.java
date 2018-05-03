package net.hudup.core.alg.ui;

import net.hudup.core.alg.Alg;


/**
 * This interface specifies user interface (UI) component showing algorithms for selection and configuration.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public interface AlgListUI {

	/**
	 * Returning selected algorithm.
	 * @return selected algorithm.
	 */
	Alg getSelectedAlg();
	
	
	/**
	 * Testing whether this component is enabled.
	 * @return whether this component is enabled.
	 */
	boolean isEnabled();
	
}
