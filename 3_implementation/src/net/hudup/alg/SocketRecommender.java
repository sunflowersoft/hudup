/**
 * 
 */
package net.hudup.alg;

import java.util.Set;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.ServiceRecommender;
import net.hudup.core.client.DriverManager;
import net.hudup.core.client.SocketConnection;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.HiddenText;
import net.hudup.core.data.RatingVector;
import net.hudup.core.data.ServerPointer;
import net.hudup.core.logistic.xURI;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class SocketRecommender extends ServiceRecommender {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	protected SocketConnection connection = null;
	
	
	/**
	 * 
	 */
	public SocketRecommender() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		return new DataConfig();
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "socket_server_query";
	}

	
	@Override
	public Dataset getDataset() {
		// TODO Auto-generated method stub
		try {
			if (connection == null)
				return null;
			else
				return connection.getSnapshot();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public void setup(Dataset dataset, Object... params) {
		// TODO Auto-generated method stub
		unsetup();
		
		if (!(dataset instanceof ServerPointer))
			throw new RuntimeException("Invalid parameter");
		
		xURI serverUri = dataset.getConfig().getStoreUri();
		String username = dataset.getConfig().getStoreAccount();
		HiddenText password = dataset.getConfig().getStorePassword();
		
		connection = DriverManager.getSocketConnection(serverUri, username, password.getText()); 
	}

	
	@Override
	public void unsetup() {
		try {
			if (connection != null) {
				connection.close();
				connection = null;
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		super.unsetup();
	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) {
		// TODO Auto-generated method stub
		
		if (connection == null)
			return null;
		else {
			try {
				return connection.estimate(param, queryIds);
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
	}

	
	@Override
	public RatingVector recommend(RecommendParam param, int maxRecommend) {
		// TODO Auto-generated method stub
		
		if (connection == null)
			return null;
		else {
			try {
				return connection.recommend(param, maxRecommend);
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new SocketRecommender();
	}


	
	
}
