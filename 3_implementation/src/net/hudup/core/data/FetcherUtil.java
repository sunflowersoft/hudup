/**
 * 
 */
package net.hudup.core.data;

import java.util.Collection;

/**
 * This utility class provides utility methods to process on {@link Fetcher}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class FetcherUtil {
	
	
	/**
	 * Filling the specified collection by elements from specified fetcher.
	 * 
	 * @param <E> type of elements retrieved from fetcher.
	 * @param collection specified collection which is filled in by elements from specified fetcher.
	 * @param fetcher specified fetcher which is iterated to retrieve its elements which are filled in the specified collection.
	 * @param autoClose if {@code true}, the fetcher is automatically closed after the filling task is finished.
	 */
	public static <E> void fillCollection(Collection<E> collection, Fetcher<E> fetcher, boolean autoClose) {
		try {
			while (fetcher.next()) {
				E el = fetcher.pick();
				if (el != null)
					collection.add(el);
			}
			
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (autoClose)
					fetcher.close();
				else
					fetcher.reset();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
}
