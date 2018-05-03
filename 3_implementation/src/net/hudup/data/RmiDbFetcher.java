package net.hudup.data;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.ResultSet;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 * @param <E>
 */
public abstract class RmiDbFetcher<E> extends DbFetcher<E> {
	
	
	/**
	 * 
	 * @param rs
	 */
	public RmiDbFetcher(ResultSet rs) {
		super(rs);
		// TODO Auto-generated constructor stub
		try {
			UnicastRemoteObject.exportObject(this, 0);
		} 
		catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	@Override
	public void close() throws RemoteException {
		// TODO Auto-generated method stub
		
		if (rs != null) {
			try {
				UnicastRemoteObject.unexportObject(this, true);
			} 
			catch (NoSuchObjectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("No such object exported, error " + e.getMessage());
			}
		}
		
		super.close();
	}



	
	
}
