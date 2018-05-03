/**
 * 
 */
package net.hudup.alg.cf.bnet;

import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.KBase;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Pair;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.data.bit.BitData;
import net.hudup.data.bit.BitItem;
import net.hudup.logistic.SystemUtil;
import net.hudup.logistic.inference.BnetBinaryLearner;
import net.hudup.logistic.inference.BnetUtil;
import elvira.Bnet;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
public class BnetBinaryClusteredCF extends BnetBinaryCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public BnetBinaryClusteredCF() {
		
	}
	
	
	@Override
	public KBase createKB() {
		// TODO Auto-generated method stub
		return BnetBinaryClusteredKB.create(this);
	}
	

	@Override
	protected Bnet chooseBnet(Collection<Integer> bitItemIds) {
		// TODO Auto-generated method stub
		
		BnetBinaryClusteredKB bbcKb = (BnetBinaryClusteredKB)kb; 
		List<Bnet> bnetList =  bbcKb.getBnetList();
		
		int maxBnetCount = 0;
		int maxBnetIdx = -1;
		for (int bnetIdx = 0; bnetIdx < bnetList.size(); bnetIdx++) {
			int count = BnetUtil.countForBnetIdx(bbcKb.MT, bnetIdx, bitItemIds);
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
	public String getName() {
		// TODO Auto-generated method stub
		return "bayesnet_binary_clustered";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new BnetBinaryClusteredCF();
	}

	
	
}



/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
abstract class BnetBinaryClusteredKB extends BnetBinaryKB {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
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
		
		BitData bitData = BitData.create(dataset);
		
		bnetList = BnetBinaryLearner.learning_clustered(
				bitData, 
				config.getAsInt(MAX_PARENTS));
		
		bitItemMap.clear();
		itemIds.clear();
		Set<Integer> bitItemIds = bitData.bitItemIds();
		for (int bitItemId : bitItemIds) {
			BitItem item = bitData.get(bitItemId);
			Pair pair = item.pair();
			
			bitItemMap.put(bitItemId, pair);
			itemIds.add(pair.key());
		}
		
		MT = BnetUtil.createMT(bnetList);
		
		
		bitData.clear();
		bitData = null;
		SystemUtil.enhance();
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
	 * @return {@link BnetBinaryClusteredKB}
	 */
	public static BnetBinaryClusteredKB create(final BnetBinaryClusteredCF cf) {
		return new BnetBinaryClusteredKB() {

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
