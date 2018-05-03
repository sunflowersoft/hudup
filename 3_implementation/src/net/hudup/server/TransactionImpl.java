package net.hudup.server;

import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class TransactionImpl implements Transaction {

	
	/**
	 * 
	 */
	protected ReentrantReadWriteLock lock = null;
	
	
	/**
	 * 
	 */
	public TransactionImpl() {
		// TODO Auto-generated constructor stub
		lock = new ReentrantReadWriteLock();
	}

	
	/**
	 * 
	 * @param fair
	 */
	public TransactionImpl(boolean fair) {
		// TODO Auto-generated constructor stub
		
		lock = new ReentrantReadWriteLock(fair);
	}

	
	@Override
	public void lockRead() {
		lock.readLock().lock();
	}

	
	@Override
	public void unlockRead() {
		lock.readLock().unlock();
	}
	
	
	@Override
	public void lockWrite() {
		lock.writeLock().lock();
	}

	
	@Override
	public void unlockWrite() {
		lock.writeLock().unlock();
	}


	@Override
	public boolean isWriteLockedByCurrentThread() {
		// TODO Auto-generated method stub
		
		return lock.isWriteLockedByCurrentThread();
	}


	
	
	
}
