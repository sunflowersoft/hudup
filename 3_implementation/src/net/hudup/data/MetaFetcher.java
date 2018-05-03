package net.hudup.data;

import java.rmi.RemoteException;

import net.hudup.core.data.Fetcher;
import net.hudup.core.data.FetcherMetadata;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 * @param <U>
 * @param <V>
 */
public abstract class MetaFetcher<U, V> implements Fetcher<V> {

	
	/**
	 * 
	 */
	protected Fetcher<U> fetcher = null;


	/**
	 * 
	 * @param fetcher
	 */
	public MetaFetcher(Fetcher<U> fetcher) {
		super();
		// TODO Auto-generated constructor stub
		
		this.fetcher = fetcher;
	}


	@Override
	public boolean next() throws RemoteException {
		// TODO Auto-generated method stub
		return fetcher.next();
	}

	
	@Override
	public V pick() throws RemoteException {
		// TODO Auto-generated method stub
		U u = fetcher.pick();
		if (u == null)
			return null;
		else
			return create(u);
	}


	@Override
	public void reset() throws RemoteException {
		// TODO Auto-generated method stub
		fetcher.reset();
	}

	
	@Override
	public FetcherMetadata getMetadata() throws RemoteException {
		// TODO Auto-generated method stub
		return fetcher.getMetadata();
	}

	
	@Override
	public void close() throws RemoteException {
		// TODO Auto-generated method stub
		if (fetcher != null)
			fetcher.close();
		
		fetcher = null;
	}
	
	
	/**
	 * 
	 * @param u
	 * @return created element
	 */
	public abstract V create(U u);
	
	
	@Override
	public String toText() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not support this method");
	}

	
	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		
		throw new RuntimeException("Not support this method");
	}

}
