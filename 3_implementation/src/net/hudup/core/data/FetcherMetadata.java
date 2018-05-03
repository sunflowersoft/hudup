/**
 * 
 */
package net.hudup.core.data;

import java.io.Serializable;

/**
 * This class contains additional information about {@link Fetcher}. Recall that fetcher is the interface for iterating each item of an associated collection.
 * As a convention, this class is called fetcher meta-data.
 * In current implementation, this class only contains the size of fetcher which is the number of elements that fetcher can browse.
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class FetcherMetadata implements Serializable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * The size which is the number of elements that fetcher can iterate over.
	 */
	protected int size = 0;
	
	
	/**
	 * Default constructor.
	 */
	public FetcherMetadata() {
		
	}
	
	
	/**
	 * Getting size of fetcher which is the number of elements that fetcher can iterate over.
	 * @return size of fetcher which is the number of elements that fetcher can iterate over.
	 */
	public int getSize() {
		return size;
	}
	
	
	/**
	 * Setting the size of this fetcher by specified size.
	 * @param size specified size.
	 */
	public void setSize(int size) {
		this.size = size;
	}
	
	
}
