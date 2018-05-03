/**
 * 
 */
package net.hudup.alg.cf.bnet;

import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.KBase;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.logistic.inference.BnetLearner;
import net.hudup.logistic.inference.BnetUtil;
import elvira.Bnet;

/**
 * @author Loc Nguyen
 * @version 10.0
 * 
 * 
 */
public class BnetClusteredCF extends BnetCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public final static int DEFAULT_BNET_NODE_NUMBER = 0;


	/**
	 * 
	 */
	public BnetClusteredCF() {
		super();
	}
	
	
	@Override
	public KBase createKB() {
		// TODO Auto-generated method stub
		return BnetClusteredKB.create(this);
	}
	

	@Override
	protected Bnet chooseBnet(Collection<Integer> itemIds) {
		// TODO Auto-generated method stub
		
		BnetClusteredKB bcKb = (BnetClusteredKB)kb; 
		List<Bnet> bnetList =  bcKb.getBnetList();
		
		int maxBnetCount = 0;
		int maxBnetIdx = -1;
		for (int bnetIdx = 0; bnetIdx < bnetList.size(); bnetIdx++) {
			int count = BnetUtil.countForBnetIdx(bcKb.MT, bnetIdx, itemIds);
			if (maxBnetCount < count) {
				maxBnetCount = count;
				maxBnetIdx = bnetIdx;
			}
				
		}
		
		if (maxBnetCount == 0)
			return null;
		else
			return bnetList.get(maxBnetIdx);
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		
		config.put(BnetClusteredKB.BNET_NODE_NUMBER, new Integer(DEFAULT_BNET_NODE_NUMBER));
		
		return config;
		
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "bayesnet_clustered";
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new BnetClusteredCF();
	}


	
}


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
abstract class BnetClusteredKB extends BnetKB {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public final static String BNET_NODE_NUMBER = "bnet_node_number";
	
	
	/**
	 * 
	 */
	protected Map<Integer, BitSet> MT = Util.newMap();

	
	@Override
	public void load() {
		// TODO Auto-generated method stub
		super.load();
		
		UriAdapter adapter = new UriAdapter(config);
		MT = BnetUtil.loadMT(adapter, config.getStoreUri(), getName());
		adapter.close();
	}

	
	@Override
	protected void learnBnet(Dataset dataset) {
		bnetList = BnetLearner.learning_clustered(
				dataset, 
				config.getAsInt(MAX_PARENTS),
				config.getAsInt(BNET_NODE_NUMBER),
				config.getAsReal(DIM_REDUCE_RATIO),
				getCompleteMethod());
		
		MT = BnetUtil.createMT(bnetList);
		
		itemIds.clear();
		for (Bnet bnet : bnetList) {
			List<Integer> ids = BnetUtil.itemIdListOf(bnet.getNodeList());
			itemIds.addAll(ids);
			
		}
		
	}
	
	
	@Override
	public void export(DataConfig storeConfig) {
		// TODO Auto-generated method stub
		super.export(storeConfig);
		
		UriAdapter adapter = new UriAdapter(storeConfig);
		BnetUtil.saveMT(adapter, storeConfig.getStoreUri(), MT, getName());
		adapter.close();
	}

	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		super.close();
		
		MT.clear();
	}


	/**
	 * 
	 * @param cf
	 * @return {@link BnetClusteredKB}
	 */
	public static BnetClusteredKB create(final BnetClusteredCF cf) {
		return new BnetClusteredKB() {

			
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
	
	
}



