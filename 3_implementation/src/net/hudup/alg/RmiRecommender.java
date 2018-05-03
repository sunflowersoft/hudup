package net.hudup.alg;

import java.util.Set;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.ServiceRecommender;
import net.hudup.core.client.DriverManager;
import net.hudup.core.client.Service;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.xURI;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class RmiRecommender extends ServiceRecommender {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	protected Service service = null;
	
	
	/**
	 * 
	 */
	public RmiRecommender() {
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
		return "rmi_server_query";
	}

	
	@Override
	public Dataset getDataset() {
		// TODO Auto-generated method stub
		try {
			if (service == null)
				return null;
			else
				return service.getSnapshot();
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
		
		xURI uri = dataset.getConfig().getStoreUri(); 
		service = DriverManager.getRemoteService(
				uri.getHost(),
				uri.getPort(),
				dataset.getConfig().getStoreAccount(), 
				dataset.getConfig().getStorePassword().getText());
	}

	
	@Override
	public void unsetup() {
		// TODO Auto-generated method stub
		service = null;

		super.unsetup();
	}


	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) {
		// TODO Auto-generated method stub
		
		try {
			return service.estimate(param, queryIds);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	
	@Override
	public RatingVector recommend(RecommendParam param, int maxRecommend) {
		// TODO Auto-generated method stub

		try {
			return service.recommend(param, maxRecommend);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new RmiRecommender();
	}

	
	
	
}



