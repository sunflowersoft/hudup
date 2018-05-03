package net.hudup.evaluate;

import java.util.EventListener;

import net.hudup.core.logistic.HudupException;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface EvaluatorListener extends EventListener {
	
	/**
	 * 
	 * @param evt
	 * @throws HudupException
	 */
	void receivedEvaluation(EvaluatorEvent evt) throws HudupException;
	
	
}
