/**
 * 
 */
package net.hudup.alg.cf.bnet;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.KBase;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Pair;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.RatingFilter;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.ValueTriple;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.TextParserUtil;
import net.hudup.data.bit.BitData;
import net.hudup.data.bit.BitDataUtil;
import net.hudup.data.bit.BitItem;
import net.hudup.logistic.SystemUtil;
import net.hudup.logistic.inference.BnetBinaryLearner;
import net.hudup.logistic.inference.BnetBinaryUtil;
import elvira.Bnet;
import elvira.Evidence;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
public class BnetBinaryCF extends BnetCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	@Override
	public KBase createKB() {
		// TODO Auto-generated method stub
		return BnetBinaryKB.create(this);
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "bayesnet_binary";
	}


	@Override
	protected Bnet chooseBnet(Collection<Integer> bitItemIds) {
		BnetBinaryKB bbKb = (BnetBinaryKB)kb;
		return bbKb.getBnetList().get(0);
	}
	
	
	@Override
	protected List<ValueTriple> bnetEstimate(RecommendParam param, Set<Integer> queryIds, float referredRatingValue, RatingFilter ratingFilter) {
		
		Set<Integer> itemIdSet = Util.newSet();
		itemIdSet.addAll(param.ratingVector.fieldIds());
		itemIdSet.addAll(queryIds);
		
		BnetBinaryKB bbKb = (BnetBinaryKB)kb;
		int minRating = (int) (config.getMinRating() + 0.5f);
		int maxRating = (int) (config.getMaxRating() + 0.5f);
		Map<Integer, Pair> bitItemMap = bbKb.bitItemMap;
		Set<Integer> bitItemIdSet = Util.newSet();
		for (int itemId : itemIdSet) {
			
			for (int i = minRating; i <= maxRating; i++) {
				int bitItemId = BitDataUtil.findBitItemIdOf(bitItemMap, itemId, i);
				if (bitItemId >= 0)
					bitItemIdSet.add(bitItemId);
			}
		}

		Bnet bnet = this.chooseBnet(bitItemIdSet);
		List<ValueTriple> result = Util.newList();
		if (bnet == null)
			return result;
		
		Evidence ev = BnetBinaryUtil.createBitItemEvidence(
				bitItemMap,
				bnet.getNodeList(), 
				param.ratingVector);
		
        result = BnetBinaryUtil.inference(
        		bnet, 
        		bitItemMap,
        		ev, 
        		queryIds, 
        		referredRatingValue,
        		ratingFilter);
        
        if (result.size() == 0)
            result = BnetBinaryUtil.inference(
            	bnet, 
        		bitItemMap,
            	new Evidence(), 
            	queryIds,
        		referredRatingValue,
        		ratingFilter);
        
        return result;
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		
		config.remove(BnetKB.DIM_REDUCE_RATIO);
		config.remove(BnetKB.COMPLETE_METHOD);
		
		return config;
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new BnetBinaryCF();
	}

	
	
	
	
}


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
abstract class BnetBinaryKB extends BnetKB {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	protected Map<Integer, Pair> bitItemMap = Util.newMap();

	
	
	@Override
	public void load() {
		// TODO Auto-generated method stub
		super.load();
		
		BufferedReader bitMapReader = null;
		UriAdapter adapter = null;
		try {
			adapter = new UriAdapter(config);
			
			xURI store = config.getStoreUri();
			bitItemMap.clear();
			xURI bitMapUri = store.concat(getName() + TextParserUtil.CONNECT_SEP + "bitmap");
			if (adapter.exists(bitMapUri)) {
				bitMapReader = new BufferedReader(adapter.getReader(bitMapUri));
				bitItemMap = BitDataUtil.readBitItemMap(bitMapReader);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (bitMapReader != null)
					bitMapReader.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			if (adapter != null)
				adapter.close();
		}
		
		itemIds.clear();
		Collection<Pair> pairs = bitItemMap.values();
		for (Pair pair : pairs)
			itemIds.add(pair.key());
	}


	@Override
	protected void learnBnet(Dataset dataset) {
		
		BitData bitData = BitData.create(dataset);
		
		bnetList = BnetBinaryLearner.learning(
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
		
		
		bitData.clear();
		bitData = null;
		SystemUtil.enhance();
	}
	
	
	@Override
	public void export(DataConfig storeConfig) {
		// TODO Auto-generated method stub
		super.export(storeConfig);
		
		UriAdapter adapter = null;
		PrintWriter bitMapWriter = null;
		try {
			adapter = new UriAdapter(config);
			
			xURI store = storeConfig.getStoreUri();
			xURI bitMapUri = store.concat(getName() + TextParserUtil.CONNECT_SEP + "bitmap");
			bitMapWriter = new PrintWriter(adapter.getWriter(bitMapUri, false));
			BitDataUtil.writeBitItemMap(bitItemMap, bitMapWriter);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (bitMapWriter != null)
					bitMapWriter.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			if (adapter != null)
				adapter.close();
		}
		
	}


	@Override
	public void close() {
		// TODO Auto-generated method stub
		super.close();
		
		bitItemMap.clear();
	}


	/**
	 * 
	 * @param cf
	 * @return {@link BnetBinaryKB}
	 */
	public static BnetBinaryKB create(final BnetBinaryCF cf) {
		return new BnetBinaryKB() {

			
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
