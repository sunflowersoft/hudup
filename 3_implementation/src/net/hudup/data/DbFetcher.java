/**
 * 
 */
package net.hudup.data;

import java.rmi.RemoteException;
import java.sql.ResultSet;

import net.hudup.core.data.Fetcher;
import net.hudup.core.data.FetcherMetadata;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;

import org.apache.log4j.Logger;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class DbFetcher<E> implements Fetcher<E> {
	
	
	/**
	 * Logger of this class.
	 */
	protected final static Logger logger = Logger.getLogger(DbFetcher.class);

	
	/**
	 * 
	 */
	protected ResultSet rs = null;
	
	
	/**
	 * 
	 */
	protected FetcherMetadata metadata = null;
	
	
	/**
	 * 
	 * @param rs
	 */
	public DbFetcher(ResultSet rs) {
		this.rs = rs;
		this.metadata = new FetcherMetadata();
		
		try {
			if (rs.last()) {
				this.metadata.setSize(rs.getRow());
				rs.beforeFirst();
			}
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("DbFetcher initialized fail, error " + e.getMessage());
		}
	}
	
	
	
	@Override
	public boolean next() throws RemoteException {
		// TODO Auto-generated method stub
		
		try {
			
			return (rs != null && rs.next());
			
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	
	@Override
	public E pick() throws RemoteException {
		// TODO Auto-generated method stub
		return create(rs);
	}

	
	@Override
	public void reset() throws RemoteException {
		// TODO Auto-generated method stub
		
		try {
			rs.beforeFirst();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	
	@Override
	public FetcherMetadata getMetadata() throws RemoteException {
		// TODO Auto-generated method stub
		
		return metadata;
	}

	
	@Override
	public void close() throws RemoteException {
		// TODO Auto-generated method stub
		if (rs == null)
			return;
		
		DbProviderAssoc.closeResultSet(rs);
		rs = null;
		metadata = null;
		
	}

	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		try {
			close();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}


	/**
	 * 
	 * @param rs
	 * @return created element
	 */
	public abstract E create(ResultSet rs);
	
	
	@Override
	public String toText() {
		// TODO Auto-generated method stub
		StringBuffer buffer = new StringBuffer();
		int i = 0;
		
		try {
			while(next()) {
				if (i > 0)
					buffer.append("\n");
				
				E el = pick();
				if (el == null)
					continue;
				
				String row = el.getClass().toString() + TextParserUtil.LINK_SEP;
				if (el instanceof TextParsable)
					buffer.append( row + ((TextParsable)el).toText());
				else
					buffer.append(row + el.toString());
						
				i++;
			}
			reset();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				reset();
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return buffer.toString();
	}


	
	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not support this method");
	}
	
	
	
	
}
