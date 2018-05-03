/**
 * 
 */
package net.hudup.core.logistic;

/**
 * This class is the thread to start a runnable object called {@link Runner}. This class is used to set {@link Runner} on the thread so as to start such {@link Runner}.
 * The thread starts by calling its method {@link #start()}.
 * It also contains such {@link Runner}.
 * Note, {@link Runner} interface specifies that any object that implements it can start, pause, resume, and stop by itself.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class RunnerThread extends Thread {
	
	
	/**
	 * The internal runnable object called {@link Runner}.
	 */
	protected Runner runner = null;
	
	
	/**
	 * Constructor with runnable object called {@link Runner}.
	 * @param runner specified runnable object called {@link Runner}.
	 */
	public RunnerThread(Runner runner) {
		super(runner);
		// TODO Auto-generated constructor stub
		
		this.runner = runner;
	}

	
	/**
	 * Getting the internal runnable object called {@link Runner}.
	 * @return internal runnable object called {@link Runner}.
	 */
	public Runner getRunner() {
		return runner;
	}
	
	
}
