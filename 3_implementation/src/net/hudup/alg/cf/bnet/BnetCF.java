package net.hudup.alg.cf.bnet;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.hudup.alg.cf.stat.MeanItemCF;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.KBase;
import net.hudup.core.alg.KBaseAbstract;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.Recommender;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.RatingFilter;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.ValueTriple;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.TextParserUtil;
import net.hudup.logistic.inference.BnetLearner;
import net.hudup.logistic.inference.BnetUtil;
import elvira.Bnet;
import elvira.Evidence;

/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
@BaseClass
public class BnetCF extends BnetAbstractCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public final static int DEFAULT_MAX_PARENTS = 20;
	
	
	/**
	 * 
	 */
	public final static float DEFAULT_DIM_REDUCE_RATIO = 0.9f;
	

	/**
	 * 
	 */
	public final static Class<? extends Recommender> DEFAULT_COMPLETE_METHOD_CLASS = MeanItemCF.class;

	
	/**
	 * 
	 */
	public BnetCF() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "bayesnet";
	}

	
	@Override
	public KBase createKB() {
		// TODO Auto-generated method stub
		return BnetKB.create(this);
	}


	/**
	 * 
	 * @param itemIds specified collection of item identifiers (item IDs).
	 * @return {@link Bnet}
	 */
	protected Bnet chooseBnet(Collection<Integer> itemIds) {
		BnetKB bKb = (BnetKB)kb;
		return bKb.getBnetList().get(0);
	}

	
	@Override
	protected List<ValueTriple> bnetEstimate(RecommendParam param, Set<Integer> queryIds, float referredRatingValue, RatingFilter ratingFilter) {
		// TODO Auto-generated method stub
		Set<Integer> itemIdSet = Util.newSet();
		itemIdSet.addAll(param.ratingVector.fieldIds());
		itemIdSet.addAll(queryIds);
		
		Bnet bnet = this.chooseBnet(itemIdSet);
		List<ValueTriple> result = Util.newList();
		
		if (bnet == null)
			return result;
		
		float minRating = config.getMinRating();
		Evidence ev = BnetUtil.createItemEvidence(
				bnet.getNodeList(), 
				param.ratingVector, 
				minRating);
		
        result = BnetUtil.inference(
        		bnet, 
        		ev, 
        		queryIds, 
        		minRating,
        		referredRatingValue,
        		ratingFilter);
        
        if (result.size() == 0)
            result = BnetUtil.inference(
            	bnet, 
            	new Evidence(), 
            	queryIds,
            	minRating,
        		referredRatingValue,
        		ratingFilter);
        
        return result;
	}


	@Override
	protected Set<Integer> getItemIds() {
		// TODO Auto-generated method stub
		return ((BnetKB)kb).itemIds;
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		
		config.put(BnetKB.MAX_PARENTS, new Integer(DEFAULT_MAX_PARENTS));
		config.put(BnetKB.DIM_REDUCE_RATIO, new Float(DEFAULT_DIM_REDUCE_RATIO));
		
		try {
			Alg completeMethod = DEFAULT_COMPLETE_METHOD_CLASS.newInstance();
			DataConfig completeMethodConfig = completeMethod.getConfig();
			xURI subStore = config.getStoreUri().concat(completeMethod.getName());
			completeMethodConfig.setStoreUri(subStore);
			
			config.put(BnetKB.COMPLETE_METHOD, completeMethod);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return config;
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new BnetCF();
	}

	
}


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
abstract class BnetKB extends KBaseAbstract {

	
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
	public final static String MAX_PARENTS = "max_parent";
	
	
	/**
	 * 
	 */
	public final static String DIM_REDUCE_RATIO = "dim_reduce_ratio";
	
	
	/**
	 * 
	 */
	public final static String COMPLETE_METHOD = "complete_method";

	
	/**
	 * 
	 */
	protected List<Bnet>  bnetList = Util.newList();

	
	/**
	 * 
	 */
	protected Set<Integer> itemIds = Util.newSet();
	
	
	/**
	 * 
	 */
	protected BnetKB() {
		
	}
	
	
	@Override
	public void load() {
		// TODO Auto-generated method stub
		super.load();
		
		UriAdapter adapter = new UriAdapter(config);
		bnetList = loadBnet(adapter, config.getStoreUri(), getName());
		adapter.close();
		
		itemIds.clear();
		for (Bnet bnet : bnetList) {
			List<Integer> ids = BnetUtil.itemIdListOf(bnet.getNodeList());
			itemIds.addAll(ids);
		}
	}

	
	@Override
	public void learn(Dataset dataset, Alg alg) {
		// TODO Auto-generated method stub
		super.learn(dataset, alg);
		
		learnBnet(dataset);
		
	}


	/**
	 * 
	 * @param dataset
	 */
	protected void learnBnet(Dataset dataset) {
		bnetList = BnetLearner.learning(
				dataset, 
				config.getAsInt(MAX_PARENTS),
				config.getAsReal(DIM_REDUCE_RATIO),
				getCompleteMethod());
		
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
		saveBnet(adapter, bnetList, storeConfig.getStoreUri(), getName());
		adapter.close();
	}

	
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return bnetList.size() == 0;
	}

	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		super.close();
		
		bnetList.clear();
		itemIds.clear();
	}


	/**
	 * 
	 * @return list of {@link Bnet}
	 */
	public List<Bnet> getBnetList() {
		return bnetList;
	}
	
	
	/**
	 * 
	 * @return {@link CF} as complete method
	 */
	protected Alg getCompleteMethod() {
		return (Alg) config.get(COMPLETE_METHOD);
	}
	
	
	/**
	 * 
	 * @param cf
	 * @return {@link BnetKB}
	 */
	public static BnetKB create(final BnetCF cf) {
		return new BnetKB() {

			
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
	 * @return list of {@link Bnet}
	 */
	protected static List<Bnet> loadBnet(UriAdapter adapter, xURI store, String prefixName) {
		
		List<Bnet> bnetList = Util.newList();
		List<xURI> uriList = BnetAbstractCF.getUriList(store, BNET_FILEEXT, prefixName, false);
		
		
		for(xURI uri : uriList) {
			Bnet bnet = BnetUtil.loadBnet(adapter, uri);
			if (bnet != null)
				bnetList.add(bnet);
		}
		
		return bnetList;
	}


	/**
	 * 
	 * @param bnetList
	 * @param store
	 * @param prefixName
	 */
	protected static void saveBnet(UriAdapter adapter, List<Bnet> bnetList, xURI store, String prefixName) {
		adapter.create(store, true);

		for (int i = 0; i < bnetList.size(); i++) {
			Bnet bnet = bnetList.get(i);
			String filename = prefixName + TextParserUtil.CONNECT_SEP + i + "." + BNET_FILEEXT;
			xURI uri = store.concat(filename);
			
			BnetUtil.saveBnet(adapter, bnet, uri);
		}
		adapter.close();
	}

	
	
	
	
}


