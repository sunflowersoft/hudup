package net.hudup.alg.cf.bnet;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.KBase;
import net.hudup.core.alg.KBaseAbstract;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.RatingFilter;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.ValueTriple;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.TextParserUtil;
import net.hudup.logistic.inference.BnetDistributedLearner;
import net.hudup.logistic.inference.BnetUtil;
import elvira.Bnet;
import elvira.Evidence;

/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 * 
 * 
 */
@NextUpdate
public class BnetDistributedCF extends BnetAbstractCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public BnetDistributedCF() {
		super();
	}
	
	
	@Override
	protected List<ValueTriple> bnetEstimate(RecommendParam param, Set<Integer> queryIds, float referredRatingValue, RatingFilter ratingFilter) {
		// TODO Auto-generated method stub
		
		List<ValueTriple> result = Util.newList();
		
		BnetDistributedKB bdKb = (BnetDistributedKB)kb;
		float minRating = config.getMinRating();
		
		for (int queryId : queryIds) {
			Bnet bnet = bdKb.getBnet(queryId);
			Set<Integer> newQueryId = Util.newSet();
			newQueryId.add(queryId);
			
			Evidence ev = BnetUtil.createItemEvidence(
					bnet.getNodeList(), 
					param.ratingVector, 
					minRating);
			
			List<ValueTriple> r = BnetUtil.inference(
	        		bnet, 
	        		ev, 
	        		newQueryId, 
	        		minRating,
	        		referredRatingValue,
	        		ratingFilter);
	        
	        if (r.size() == 0)
	            r = BnetUtil.inference(
	            	bnet, 
	            	new Evidence(), 
	            	newQueryId,
	        		minRating,
	        		referredRatingValue,
	        		ratingFilter);
	        
	        ValueTriple triple = ValueTriple.getByKey(r, queryId);
	        if (triple != null)
	        	result.add(triple);
	        
		}
		
		if (result.size() == 0)
			return null;
		else
			return result;
	}


	@Override
	protected Set<Integer> getItemIds() {
		// TODO Auto-generated method stub
		
		BnetDistributedKB bdKb = (BnetDistributedKB)kb;
		
		return bdKb.itemIds;
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new BnetDistributedCF();
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "bayesnet_distributed";
	}

	
	@Override
	public KBase createKB() {
		// TODO Auto-generated method stub
		return BnetDistributedKB.create(this);
	}


	
}



/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
abstract class BnetDistributedKB extends KBaseAbstract {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public final static String BNET_FILEEXT = "elv";

	
	/**
	 * 
	 */
	protected Map<Integer, Bnet> bnetMap = Util.newMap();

	
	/**
	 * 
	 */
	protected Set<Integer> itemIds = Util.newSet();

	
	/**
	 * 
	 */
	private BnetDistributedKB() {
		
	}
	
	
	@Override
	public void load() {
		// TODO Auto-generated method stub
		super.load();
		
		UriAdapter adapter = new UriAdapter(config);
		bnetMap = loadBnetMap(adapter, config.getStoreUri(), getName());
		adapter.close();
		
		itemIds = bnetMap.keySet();
	}

	
	@Override
	public void learn(Dataset dataset, Alg alg) {
		// TODO Auto-generated method stub
		super.learn(dataset, alg);
		
		bnetMap = BnetDistributedLearner.createDistributedBnet(dataset);
		itemIds = bnetMap.keySet();
		
	}

	
	@Override
	public void export(DataConfig storeConfig) {
		// TODO Auto-generated method stub
		
		super.export(storeConfig);
		
		UriAdapter adapter = new UriAdapter(storeConfig);
		saveBnetMap(adapter, bnetMap, storeConfig.getStoreUri(), getName());
		adapter.close();
	}

	
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return bnetMap.size() == 0;
	}

	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		super.close();
		
		bnetMap.clear();
		itemIds.clear();
	}


	/**
	 * 
	 * @param id
	 * @return {@link Bnet}
	 */
	protected Bnet getBnet(int id) {
		return bnetMap.get(id);
	}
	
	
	/**
	 * 
	 * @param cf
	 * @return {@link BnetDistributedKB}
	 */
	public static BnetDistributedKB create(final BnetDistributedCF cf) {
		return new BnetDistributedKB() {

			
			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			
			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return cf.getName();
			}
			
		};
	}
	

	
	/**
	 * 
	 * @param adapter
	 * @param store
	 * @param prefixName
	 * @return map of {@link Bnet}
	 */
	protected static Map<Integer, Bnet> loadBnetMap(UriAdapter adapter, xURI store, String prefixName) {
		
		List<xURI> uriList = BnetAbstractCF.getUriList(store, BNET_FILEEXT, prefixName, false);
		
		Map<Integer, Bnet> bnetMap = Util.newMap();
		for(xURI uri : uriList) {
			String name = uri.getLastName();
			String snum = name.substring(name.lastIndexOf(TextParserUtil.CONNECT_SEP) + 1, name.lastIndexOf("."));
			int id = Integer.parseInt(snum);
			
			Bnet bnet = BnetUtil.loadBnet(adapter, uri);
			if (bnet != null)
				bnetMap.put(id, bnet);
		}

		return bnetMap;
	}


	
	/**
	 * 
	 * @param adapter
	 * @param bnetMap
	 * @param store
	 * @param prefixName
	 */
	protected static void saveBnetMap(UriAdapter adapter, Map<Integer, Bnet> bnetMap, xURI store, String prefixName) {
		adapter.create(store, true);

		Set<Integer> ids = bnetMap.keySet();
		for (int id : ids) {
			Bnet bnet = bnetMap.get(id);
			String filename = prefixName + TextParserUtil.CONNECT_SEP + id + "." + BNET_FILEEXT;
			xURI uri = store.concat(filename);
			
			BnetUtil.saveBnet(adapter, bnet, uri);
					
		}
		
	} // saveBnet
	
	

	
	
}