package net.hudup.alg.cf.bnet;

import java.io.PrintStream;
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
import net.hudup.core.data.Pair;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.RatingFilter;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.ValueTriple;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.TextParserUtil;
import net.hudup.logistic.inference.BnetBinaryGraph;

/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
public class BnetBinaryDistributedCF extends BnetAbstractCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public final static String MIN_PROB = "min_prob";

	
	/**
	 * 
	 */
	public final static float MIN_PROB_DEFAULT = 0.01f;
	
	
	/**
	 * 
	 */
	public BnetBinaryDistributedCF() {
		super();
	}
	
	
	@Override
	public KBase createKB() {
		return BnetBinaryDistributedKB.create(this);
	}
	

	@Override
	protected List<ValueTriple> bnetEstimate(RecommendParam param, Set<Integer> queryIds, float referredRatingValue, RatingFilter ratingFilter) {
		// TODO Auto-generated method stub
		
		BnetBinaryDistributedKB bbdKb = (BnetBinaryDistributedKB)kb;
		Map<Integer, List<BnetBinaryGraph>> bnetMap = bbdKb.getBnetMap();
		
		List<ValueTriple> result = Util.newList();
		List<Pair> evList = Pair.toPairList(param.ratingVector);
		for (int queryId : queryIds) {
			if (!bnetMap.containsKey(queryId))
				continue;
			
			List<BnetBinaryGraph> bgList = bnetMap.get(queryId);
			int maxIdx = -1;
			float maxPosterior = -1;
			
			for (int i = 0; i < bgList.size(); i++) {
				BnetBinaryGraph bg = bgList.get(i);
				Pair pair = bgList.get(maxIdx).getRootItemPair();
				float rating = pair.value();

				if (Util.isUsed(referredRatingValue) && 
						ratingFilter != null && 
						!ratingFilter.accept(rating, referredRatingValue))
					continue;
				
				float posterior = bg.marginalPosterior(evList);
				if (posterior > maxPosterior) {
					maxIdx = i;
					maxPosterior = posterior;
				}
			}
			
			if (maxPosterior > 0) {
				Pair pair = bgList.get(maxIdx).getRootItemPair();
				result.add(new ValueTriple(pair.key(), pair.value(), maxPosterior));
			}
			
		}
		
		return result;
	}


	@Override
	protected Set<Integer> getItemIds() {
		BnetBinaryDistributedKB bbdKb = (BnetBinaryDistributedKB)kb;
		return bbdKb.itemIds; 
	}
	
	
	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		DataConfig config = super.createDefaultConfig();
		config.put(MIN_PROB, MIN_PROB_DEFAULT);
		
		return config;
	}
	
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "bayesnet_binary_distributed";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new BnetBinaryDistributedCF();
	}

	
	
}



/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
abstract class BnetBinaryDistributedKB extends KBaseAbstract {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public final static String BNET_FILEEXT = "bif";

	
	/**
	 * 
	 */
	protected Set<Integer> itemIds = Util.newSet();
	
	
	/**
	 * 
	 */
	protected Map<Integer, List<BnetBinaryGraph>> bnetMap = Util.newMap();

	
	/**
	 * 
	 */
	protected BnetBinaryDistributedKB() {
		
	}
	
	
	@Override
	public void load() {
		// TODO Auto-generated method stub
		super.load();
		
		UriAdapter adapter = new UriAdapter(config);
		bnetMap = loadBnet(adapter, config.getStoreUri());
		adapter.close();
		
		itemIds = bnetMap.keySet();
	}

	
	@Override
	public void learn(Dataset dataset, Alg alg) {
		// TODO Auto-generated method stub
		super.learn(dataset, alg);
		
		bnetMap = BnetBinaryGraph.create(
				dataset, config.getAsReal(BnetBinaryDistributedCF.MIN_PROB));
		
		itemIds = bnetMap.keySet();
	}

	
	@Override
	public void export(DataConfig storeConfig) {
		// TODO Auto-generated method stub
		
		super.export(storeConfig);

		UriAdapter adapter = new UriAdapter(storeConfig);
		saveBnet(adapter, bnetMap, storeConfig.getStoreUri(), getName());
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
		
		itemIds.clear();
		bnetMap.clear();
	}


	/**
	 * 
	 * @return map of {@link BnetBinaryGraph} list
	 */
	protected Map<Integer, List<BnetBinaryGraph>> getBnetMap() {
		return bnetMap;
	}
	

	/**
	 * 
	 * @param adapter
	 * @param store
	 * @return map of {@link BnetBinaryGraph} list
	 */
	protected Map<Integer, List<BnetBinaryGraph>> loadBnet(UriAdapter adapter, xURI store) {
		Map<Integer, List<BnetBinaryGraph>> bnetMap = Util.newMap();
		
		if (!adapter.exists(store))
			return bnetMap;
		
		List<xURI> uriList = BnetAbstractCF.getUriList(store, BNET_FILEEXT, getName(), false);
		for (xURI uri : uriList) {
			String lastName = uri.getLastName();
			lastName = lastName.substring(getName().length());
			if (lastName == null || lastName.isEmpty())
				continue;

			int idx1 = lastName.indexOf(TextParserUtil.CONNECT_SEP);
			int idx2 = lastName.lastIndexOf(TextParserUtil.CONNECT_SEP);
			
			String snum = lastName.substring(idx1 + 1, idx2);
			int itemId = Integer.parseInt(snum);
			
			List<BnetBinaryGraph> graphList = null;
			if (bnetMap.containsKey(itemId))
				graphList = bnetMap.get(itemId);
			else {
				graphList = Util.newList();
				bnetMap.put(itemId, graphList);
			}
			
			try {
				BnetBinaryGraph graph = BnetBinaryGraph.load(uri);
				if (graph != null)
					graphList.add(graph);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return bnetMap;
	}
	

	/**
	 * 
	 * @param bnetMap
	 * @param store
	 * @param algName
	 */
	protected static void saveBnet(
			UriAdapter adapter,
			Map<Integer, List<BnetBinaryGraph>> bnetMap, 
			xURI store,
			String algName) {
		
		adapter.create(store, true);
		
		Set<Integer> itemIds = bnetMap.keySet();
		for (int itemId : itemIds) {
			List<BnetBinaryGraph> bgList = bnetMap.get(itemId);
			
			for (BnetBinaryGraph bg : bgList) {
				saveBnet(adapter, bg, store, algName);
				
			}
		} // End for
		
	} // End saveBnet
	


	/**
	 * 
	 * @param adapter
	 * @param bg
	 * @param store
	 * @param algName
	 */
	protected static void saveBnet(UriAdapter adapter, BnetBinaryGraph bg, xURI store, String algName) {
		Pair pair = bg.getRootItemPair();
		
		String fileName = algName + 
				TextParserUtil.CONNECT_SEP + pair.key() + TextParserUtil.CONNECT_SEP + ((int)pair.value()) + 
				"." + BNET_FILEEXT;
		
		xURI uri = store.concat(fileName);
		PrintStream out = new PrintStream(adapter.getOutputStream(uri, false));
		bg.save_bif(out);
		
		out.flush();
		out.close();
	}


	
	/**
	 * 
	 * @param cf
	 * @return {@link BnetBinaryDistributedKB}
	 */
	public static BnetBinaryDistributedKB create(final BnetBinaryDistributedCF cf) {
		return new BnetBinaryDistributedKB() {

			
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
