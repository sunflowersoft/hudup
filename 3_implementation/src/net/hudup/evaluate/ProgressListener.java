package net.hudup.evaluate;

import java.util.EventListener;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface ProgressListener extends EventListener {
	
	
	/**
	 * 
	 * @param evt
	 */
	void receiveProgress(ProgressEvent evt);
	
	
	
}
