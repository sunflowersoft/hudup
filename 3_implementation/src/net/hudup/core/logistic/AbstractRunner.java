/**
 * 
 */
package net.hudup.core.logistic;

import org.apache.log4j.Logger;




/**
 * This class is a {@code runner} that implements partially the interface {@link Runner}.
 * Most necessary methods are implemented except that any completed class which extends this class must defines method {@link #task()} and method {@link #clear()}.
 * Method {@link #task()} defines actual works when runner is running.
 * Method {@link #clear()} clears all resources after runner run.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class AbstractRunner implements Runner {
	
	
	/**
	 * Logger of this class.
	 */
	protected final static Logger logger = Logger.getLogger(Runner.class);
	
	
	/**
	 * Built-in thread which is the base of this runner to run.
	 */
	protected volatile RunnerThread thread = null;
	
	
	/**
	 * This variable is a flag of whether this runner paused.
	 * Note, a runner paused if the variable {@link #thread} is not null and this variable is true.
	 */
	protected volatile boolean paused = false;

	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		Thread current = Thread.currentThread();
		
		while (current == thread) {
			
			try {
				task();
				
				synchronized (this) {
					while (paused) {
						notifyAll();
						wait();
					}
				}
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		synchronized (this) {
			thread = null;
			paused = false;
			clear();
			
			notifyAll();
		}
	}

	
	/**
	 * The actual tasks (works) which are performed when runner is running.
	 */
	public abstract void task();
	
	
	/**
	 * Clearing all resources after runner run (stopped).
	 */
	protected abstract void clear();
	
	
	@Override
	public synchronized void start() {
		if (isStarted())
			return;
		
		thread = new RunnerThread(this);
		thread.start();
	}

	
	@Override
	public synchronized void pause() {
		if (isRunning()) {
		
			paused  = true;
			
			try {
				wait();
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	
	@Override
	public synchronized void resume() {
		if (isPaused()) {
		
			paused = false;
			notifyAll();
		}
	}
	
	
	@Override
	public synchronized void stop() {
		if (!isStarted())
			return;
		
		thread = null;
		
		if (paused) {
			paused = false;
			notifyAll();
		}
		
		try {
			wait();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * Forcing this runner to stop immediately. This method stops the thread of this runner and so it is unsafe.
	 * Please see {@link Thread#stop()} for more details.
	 */
	@SuppressWarnings("deprecation")
	@NextUpdate
	public synchronized void forceStop() {
		if (!isStarted())
			return;

		try {
			if (thread != null)
				thread.stop();
		}
		catch (Throwable e) {
			logger.error("Calling thread destroy() in AbstractRunner#forceStop causes error " + e.getMessage());
		}
		
		thread = null;
		paused = false;
		clear();
		
		try {
			notifyAll();
		}
		catch (Throwable e) {
			logger.error("Calling notifyAll in Evaluator#forceStop causes error " + e.getMessage());
		}
	}

	
	@Override
	public boolean isStarted() {
		return thread != null;
	}
	
	
	@Override
	public boolean isPaused() {
		return thread != null && paused;
	}
	
	
	@Override
	public boolean isRunning() {
		return thread != null && !paused;
	}
	
	
}
