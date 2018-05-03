/**
 * 
 */
package net.hudup.alg.cf.bnet;

import java.util.List;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.KBase;
import net.hudup.core.alg.KBaseAbstract;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Pair;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.MinMax;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.RatingFilter;
import net.hudup.core.logistic.UriFilter;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.ValueTriple;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.TextParserUtil;
import net.hudup.data.bit.BitData;
import net.hudup.logistic.inference.BnetBinaryGraph;



/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
public class BnetBinaryDistributedExtCF extends BnetBinaryDistributedCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public BnetBinaryDistributedExtCF() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public KBase createKB() {
		// TODO Auto-generated method stub
		return BnetBinaryDistributedExtKB.create(this);
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "bayesnet_binary_distributed_ext";
	}
	

	/**
	 * 
	 * @param itemId specified item identifier (item ID).
	 * @return list of {@link BnetBinaryGraph} (s).
	 */
	protected List<BnetBinaryGraph> readBayesList(int itemId) {
		
		List<BnetBinaryGraph> graphList = Util.newList();
		
		BnetBinaryDistributedExtKB bbdKb = (BnetBinaryDistributedExtKB)kb;
		
		xURI store = bbdKb.getConfig().getStoreUri();
		List<xURI> uriList = BnetAbstractCF.getUriList(store, 
					BnetBinaryDistributedKB.BNET_FILEEXT, 
					getName() + TextParserUtil.CONNECT_SEP + itemId + TextParserUtil.CONNECT_SEP,
					false);
		
		for (xURI uri : uriList) {
			try {
				BnetBinaryGraph graph = BnetBinaryGraph.load(uri);
				if (graph != null)
					graphList.add(graph);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return graphList;
		
	}

	
	@Override
	protected List<ValueTriple> bnetEstimate(RecommendParam param, Set<Integer> queryIds, float referredRatingValue, RatingFilter ratingFilter) {
		// TODO Auto-generated method stub
		
		List<ValueTriple> result = Util.newList();
		
		List<Pair> evList = createEvidencePairList(param.ratingVector, param.profile);
		for (int queryId : queryIds) {

			List<BnetBinaryGraph> bgList = readBayesList(queryId);
			if (bgList.size() == 0)
				continue;
			
			int maxIdx = -1;
			float maxPosterior = -1;
			
			for (int i = 0; i < bgList.size(); i++) {
				BnetBinaryGraph bg = bgList.get(i);
				Pair pair = bgList.get(i).getRootItemPair();
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
		
		if (result.size() == 0)
			return null;
		else
			return result;
	}

	
	/**
	 * 
	 * @param vRating specified rating vector.
	 * @param profile specified profile.
	 * @return list of {@link Pair} (s).
	 */
	protected List<Pair> createEvidencePairList(RatingVector vRating, Profile profile) {
		return Pair.toPairList(vRating);
	}
	

	@Override
	protected Set<Integer> getItemIds() {
		BnetBinaryDistributedExtKB bbdKb = (BnetBinaryDistributedExtKB)kb;
		return bbdKb.itemIds; 
	}
	
	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new BnetBinaryDistributedExtCF();
	}

	
}


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
abstract class BnetBinaryDistributedExtKB extends KBaseAbstract {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	protected Set<Integer> itemIds = Util.newSet();

	
	@Override
	public void load() {
		// TODO Auto-generated method stub
		
		super.load();
		
		itemIds.clear();
		xURI store = config.getStoreUri();
		List<xURI> uriList = BnetAbstractCF.getUriList(store, 
				BnetBinaryDistributedKB.BNET_FILEEXT, 
				getName(),
				false);
		for (xURI uri : uriList) {
			try {
				String lastName = uri.getLastName();
				lastName = lastName.substring(getName().length());
				if (lastName == null || lastName.isEmpty())
					continue;
	
				int idx1 = lastName.indexOf(TextParserUtil.CONNECT_SEP);
				int idx2 = lastName.lastIndexOf(TextParserUtil.CONNECT_SEP);
				
				String snum = lastName.substring(idx1 + 1, idx2);
				int itemId = Integer.parseInt(snum);
				
				if (itemId >= 0)
					itemIds.add(itemId);
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	
	@Override
	public void learn(Dataset dataset, Alg alg) {
		// TODO Auto-generated method stub
		clear();
		
		super.learn(dataset, alg);
		
		UriAdapter adapter = new UriAdapter(config);
		xURI store = config.getStoreUri();
		adapter.create(store, true);
		
		BitData bitData = BitData.create(dataset);
		float minprob = config.getAsReal(BnetBinaryDistributedExtCF.MIN_PROB);
		if (minprob <= 0) {
			MinMax minmax = bitData.getMinMaxItemSupport();
			
			if (minmax != null)
				minprob = minmax.min();
		}
		config.put(BnetBinaryDistributedExtCF.MIN_PROB, new Float(minprob));

		Set<Integer> bitIds = bitData.bitItemIds();
		for (int bitId : bitIds) {
			BnetBinaryGraph bgraph = createBayesGraph(bitData, bitId, minprob);
			if (bgraph == null)
				continue;
			
			Pair pair = bgraph.getRootItemPair();
			itemIds.add(pair.key());
			
			BnetBinaryDistributedKB.saveBnet(adapter, bgraph, store, getName());
		}

		xURI cfgUri = store.concat(KBASE_CONFIG);
		config.save(cfgUri);
		
		bitData.clear();
		bitData = null;
		adapter.close();
	}

	
	@Override
	public void export(DataConfig storeConfig) {
		// TODO Auto-generated method stub

		xURI exportStore = storeConfig.getStoreUri();
		xURI thisStore = config.getStoreUri();
		if (exportStore.equals(thisStore)) {
			logger.info("Export the same place");
			return;
		}
		
		UriAdapter adapter = new UriAdapter(config);
		UriFilter filter = new UriFilter() {
			
			@Override
			public boolean accept(xURI uri) {
				// TODO Auto-generated method stub
				return uri.getLastName().startsWith(getName());
			}

			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return "No description";
			}
			
			
		};
		
		adapter.clearContent(exportStore, filter);
		adapter.create(exportStore, true);
		adapter.copy(thisStore, exportStore, false, filter);
		adapter.close();
	}

	
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		
		xURI store = config.getStoreUri();
		xURI uri = store.concat(KBASE_CONFIG);
		
		UriAdapter adapter = new UriAdapter(config);
		boolean existed = !adapter.exists(uri);
		adapter.close();
		
		return existed;
	}

	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		super.close();
		
		itemIds.clear();
	}


	/**
	 * 
	 * @param bitData
	 * @param bitId
	 * @param minprob
	 * @return {@link BnetBinaryGraph}
	 */
	protected BnetBinaryGraph createBayesGraph(
			BitData bitData, int bitId, float minprob) {
		return BnetBinaryGraph.create(bitData, bitId, minprob);
	}
	
	
	/**
	 * 
	 * @param cf
	 * @return {@link BnetBinaryDistributedExtKB}
	 */
	public static BnetBinaryDistributedExtKB create(final BnetBinaryDistributedExtCF cf) {
		return new BnetBinaryDistributedExtKB() {

			
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
