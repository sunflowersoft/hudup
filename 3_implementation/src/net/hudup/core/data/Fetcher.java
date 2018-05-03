package net.hudup.core.data;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.hudup.core.parser.TextParsable;



/**
 * Fetcher is the interface for iterating each item of an associated collection.
 * Fetcher is created by some classes.
 * Following is an example of using fetcher.<br>
 * <br>
 * <code>
 * Fetcher&lt;Object&gt; fetcher = ...<br>
 * while(fetcher.next()) {<br>
 * &nbsp;&nbsp;Object element = fetcher.pick();<br>
 * }<br>
 * fetcher.close();
 * </code>
 * @param <E> type of elements that the fetcher iterates over.<br>
 * @author Loc Nguyen
 * @version 10.0
 */
public interface Fetcher<E> extends Remote, AutoCloseable, TextParsable {

	/**
	 * Moving to next item of associated collection.
	 * @return whether fetcher has element
	 * @throws RemoteException if any remote exception raises. 
	 */
	boolean next() throws RemoteException;
	
	
	/**
	 * Retrieving the current item of associated collection.
	 * @return element
	 * @throws RemoteException if any remote exception raises. 
	 */
	E pick() throws RemoteException;
	
	
	/**
	 * Resetting the fetcher. After reset, fetcher can be reused for new iteration.
	 * @throws RemoteException if any remote exception raises. 
	 */
	void reset() throws RemoteException;

	
	/**
	 * Getting the meta-data of this fetcher.
	 * @return Meta-data of this fetcher, represented by {@link FetcherMetadata}
	 * @throws RemoteException if any remote exception raises. 
	 */
	FetcherMetadata getMetadata() throws RemoteException;

	
	/**
	 * Closing this fetcher. After closed, fetcher cannot be reused.
	 * Fetcher has a mechanism of auto-closing because it extends {@link AutoCloseable} interface. This {@link #close()} method is inherited from {@link AutoCloseable} interface. 
	 * Concretely, if programmer forgets closing this fetcher after using, the {@code finalize()} will call this method to close this fetcher before the Java garbage collector destroys this fetcher.
	 * @throws RemoteException if any remote exception raises. 
	 */
	void close() throws RemoteException;
}
