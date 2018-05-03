package net.hudup.core.logistic;


/**
 * This interface specifies that any object that implements it can start, pause, resume, and stop by itself.
 * Such object called {@code runner} like system thread.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface Runner extends Runnable {
	
	
	@Override
	void run();

	
	/**
	 * Runner starts running.
	 */
	public void start();

	
	/**
	 * Runner pauses.
	 */
	void pause();
	
	
	/**
	 * Runner resumes after runner pauses.
	 */
	public void resume();
	
	
	/**
	 * Runner stops. If runner wants to run again, runner must start again.
	 */
	void stop();
	
	
	/**
	 * Testing whether runner started.
	 * @return whether runner started
	 */
	boolean isStarted();
	
	
	/**
	 * Testing whether runner paused. 
	 * @return whether runner paused.
	 */
	boolean isPaused();
	
	
	/**
	 * Testing whether runner is running.
	 * @return whether runner is running.
	 */
	boolean isRunning();


}
