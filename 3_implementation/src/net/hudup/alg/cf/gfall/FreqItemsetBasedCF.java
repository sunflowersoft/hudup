/**
 * 
 */
package net.hudup.alg.cf.gfall;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.FilterParam;
import net.hudup.core.alg.KBase;
import net.hudup.core.alg.KBaseAbstract;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.cf.ModelBasedCF;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Pair;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.LineProcessor;
import net.hudup.core.logistic.MinMax;
import net.hudup.core.logistic.RatingFilter;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;
import net.hudup.data.bit.BitData;
import net.hudup.data.bit.BitDataUtil;
import net.hudup.data.bit.BitItem;
import net.hudup.data.bit.BitItemset;
import net.hudup.data.ui.FreqItemsetListTable;

/**
 * @author Loc Nguyen
 * @version 10.0
 * 
 * 
 */
public abstract class FreqItemsetBasedCF extends ModelBasedCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public FreqItemsetBasedCF() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public KBase createKB() {
		return FreqItemsetKB.create(this);
	}

	
	/**
	 * 
	 * @author Loc Nguyen
	 * @version 10.0
	 *
	 */
	protected class Estimate {
		
		/**
		 * Result of frequent bit itemset, represented by {@link FreqResult}.
		 */
		public FreqResult freq = null;
		
		
		/**
		 * Result of recommendation as a list of recommended items represented by {@link RatingVector}.
		 */
		public RatingVector result = null;
		
		
		/**
		 * Constructor with specified result of frequent bit itemset and specified result of recommendation.
		 * @param freq specified result of frequent bit itemset, represented by {@link FreqResult}.
		 * @param result specified result of recommendation as a list of recommended items represented by {@link RatingVector}.
		 */
		public Estimate(FreqResult freq, RatingVector result) {
			this.freq = freq;
			this.result = result;
		}
		
	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) {
		if (param == null)
			return null;
		
		FreqItemsetKB fiKb = (FreqItemsetKB)kb;
		if (fiKb.isEmpty() || queryIds.size() == 0)
			return null;
		

		Estimate estimate = estimate(param, queryIds, Constants.UNUSED, null);
		if (estimate == null)
			return null;
		else
			return estimate.result;
	}

	
	/**
	 * 
	 * @param param recommendation parameter specified by {@link RecommendParam}
	 * @param queryIds query identifiers (IDs). Such IDs can be item IDs or user IDs.
	 * @param referredRatingValue referred rating value.
	 * @param ratingFilter specified rating filter specified by {@link RatingFilter}.
	 * @return {@link Estimate}
	 */
	protected Estimate estimate(RecommendParam param, Set<Integer> queryIds, float referredRatingValue, RatingFilter ratingFilter) {
		if (param == null || param.ratingVector == null)
			return null;
		
		FreqItemsetKB fiKb = (FreqItemsetKB)kb;
		
		BitSet A = fiKb.toItemBitSet(param.ratingVector.toRoundValues()); // A is user rating pattern
		FreqResultMinMax minmax = fiKb.findMinMaxOf(A);
		if (minmax == null) return null;
		// Finding maximum frequent itemset
		FreqResult freq = minmax.max(); // maximum pattern that contains user rating pattern A

		
		BitSet B = (BitSet)freq.bitset().clone();
		// Pay attention to following code line. 
		// It is the most important
		B.andNot(A); // items on which user doesn't rate will be recommended to user
		int countB = DSUtil.countSetBit(B);
		if (countB == 0) return null;
		
		RatingVector result = param.ratingVector.newInstance(true);
		
		// The list of Pair (itemId, rating) which is recommended
		List<Pair> list = fiKb.toItemPairList(B);
		for (Pair pair : list) {

			if (!pair.isUsed()) continue;
			
			Integer itemId = pair.key();
			if (queryIds != null && !queryIds.contains(itemId)) 
				continue;
			if (!filterList.filter(getDataset(), FilterParam.create(itemId)))
				continue;
			
			float value = pair.value();
			if (!Util.isUsed(referredRatingValue) || ratingFilter == null)
				result.put(itemId, value);
			else if (ratingFilter.accept(value, referredRatingValue))
				result.put(itemId, value);
		}
		
		if (result.size() == 0)
			return null;
		else
			return new Estimate(freq, result);
	}

	
	@Override
	public RatingVector recommend(RecommendParam param, int maxRecommend) {
		// maxRecommend = 0: get All
		param = preprocess(param);
		if (param == null)
			return null;
		
		filterList.prepare(param);
		FreqItemsetKB fiKb = (FreqItemsetKB)kb;
		if (fiKb.isEmpty())
			return null;
		
		float avgRating = (config.getMaxRating() + config.getMinRating()) / 2.0f; 
		Estimate estimate = estimate(param, null, avgRating, new RatingFilter() {

			@Override
			public boolean accept(float ratingValue, float referredRatingValue) {
				// TODO Auto-generated method stub
				return ratingValue >= referredRatingValue;
			}
			
		});
		
		if (estimate == null)
			return null;
		else
			return optimizeRecommend(estimate.freq.itemset(), estimate.result, maxRecommend);
		
	}

	
	/**
	 * 
	 * @param itemset specified bit itemset specified by {@link BitItemset} class.
	 * @param rec recommended rating vector.
	 * @param maxRecommend maximum number of items in recommended list.
	 * @return optimized {@link RatingVector}
	 */
	protected RatingVector optimizeRecommend(BitItemset itemset, RatingVector rec, int maxRecommend) {
		// Following code is very complex but it finds optimized items
		// Recommend the item whose support is highest
		// Because items in itemset are sorted according to descending order
		// It is easy to take items in turn

		FreqItemsetKB fiKb = (FreqItemsetKB)kb;

		RatingVector result = rec.newInstance(true);
		for (int i = 0; i < itemset.size(); i++) {
			int bitItemId = itemset.get(i);
			Pair pair = fiKb.bitItemMap.get(bitItemId);
			int itemId = pair.key();
			float value = pair.value();
			
			if (!rec.isRated(itemId))
				continue;
			
			float rating = rec.get(itemId).value;
			if (rating == value)
				result.put(itemId, rating);
			
			if (maxRecommend > 0 && result.size() == maxRecommend)
				break;
		}
		
		if (result.size() == 0)
			return null;
		else
			return result;
	}
	
	
	/**
	 * 
	 * @return frequent itemsets finder {@link FreqItemsetFinder}
	 */
	protected abstract FreqItemsetFinder createFreqItemsetFinder();
	
	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = new DataConfig() {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean validate(String key, Serializable value) {
				if (key.equals(FreqItemsetFinder.MIN_SUP))
					return (((Float)value).floatValue() >= 0);
				else
					return true;
			}
			
		};
		config.put(FreqItemsetFinder.MIN_SUP, FreqItemsetFinder.DEFAULT_MIN_SUP);
		
		xURI store = xURI.create(Constants.KNOWLEDGE_BASE_DIRECTORY).concat(getName());
		config.setStoreUri(store);
		
		return config;
	}

	
}



/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
abstract class FreqItemsetKB extends KBaseAbstract {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Maximum number of frequent itemsets in a pattern
	 */
	protected static final int MAX_FREQ_ITEMSETS = 50;

	
	/**
	 * List of frequent itemsets (list of {@link FreqResult})
	 * @see FreqResult
	 */
	protected List<FreqResult> freqResults = Util.newList();
	
	
	/**
	 * This map has: key is bit item id and value is Pair (real id, rating value)
	 */
	protected Map<Integer, Pair> bitItemMap = Util.newMap();
	
	
	/**
	 * 
	 */
	private FreqItemsetKB() {
		
	}
	
	
	@Override
	public void load() {
		// TODO Auto-generated method stub
		super.load();
		
		UriAdapter adapter = null;
		BufferedReader resultReader = null;
		BufferedReader bitMapReader = null;
		try {
			
			freqResults.clear();
			
			xURI store = config.getStoreUri();
			xURI resultUri = store.concat(getName() + TextParserUtil.CONNECT_SEP + "result");
			adapter = new UriAdapter(config);
			if (adapter.exists(resultUri)) {
				resultReader = new BufferedReader(adapter.getReader(resultUri)); 
				DSUtil.lineProcess(
					resultReader, 
					new LineProcessor() {
						
						@Override
						public void process(String line) {
							// TODO Auto-generated method stub
							FreqResult result = new FreqResult();
							result.parseText(line);
							
							freqResults.add(result);
						}
					});
				
				resultReader.close();
				resultReader = null;
			}
			
			
			bitItemMap.clear();
			xURI bitMapURI = store.concat(getName() + TextParserUtil.CONNECT_SEP + "bitmap");
			if (adapter.exists(bitMapURI)) {
				bitMapReader = new BufferedReader(adapter.getReader(bitMapURI));
				bitItemMap = BitDataUtil.readBitItemMap(bitMapReader);
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (resultReader != null)
					resultReader.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}

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
		
		
	}

	
	@Override
	public void learn(Dataset dataset, Alg alg) {
		// TODO Auto-generated method stub
		super.learn(dataset, alg);
		
		BitData bitData = BitData.create(dataset);
		
		FreqItemsetBasedCF cf = (FreqItemsetBasedCF)alg;
		FreqItemsetFinder finder = cf.createFreqItemsetFinder();
		finder.setup(bitData);
		
		float minsup = config.getAsReal(FreqItemsetFinder.MIN_SUP);
		if (minsup <= 0) {
			MinMax minmax = bitData.getMinMaxItemSupport();
			
			if (minmax != null) {
				//min_sup = (minmax.min() + minmax.max()) / 2f;
				minsup = minmax.min();
			}
		}
		finder.getConfig().put(FreqItemsetFinder.MIN_SUP, new Float(minsup));

		freqResults.clear();
		List<BitItemset> freqItemsets = finder.findFreqItemset();
		for (BitItemset itemset : freqItemsets) {
			FreqResult result = new FreqResult(itemset, bitData.bitItemCount());
			freqResults.add(result);
		}
		
		bitItemMap.clear();
		Set<Integer> bitItemIds = bitData.bitItemIds();
		for (int bitItemId : bitItemIds) {
			BitItem item = bitData.get(bitItemId);
			bitItemMap.put(bitItemId, item.pair());
		}
		
		bitData.clear();
		bitData = null;
	}

	
	@Override
	public void export(DataConfig storeConfig) {
		// TODO Auto-generated method stub
		
		super.export(storeConfig);
		
		UriAdapter adapter = null;
		Writer resultWriter = null;
		PrintWriter bitMapWriter = null;
		try {
			adapter = new UriAdapter(storeConfig);
			xURI store = storeConfig.getStoreUri();
			
			xURI resultUri = store.concat(getName() + TextParserUtil.CONNECT_SEP + "result");
			resultWriter = adapter.getWriter(resultUri, false);
			for (FreqResult result : freqResults) {
				
				resultWriter.write(result.toText() + "\n");
			}
			resultWriter.flush();
			resultWriter.close();
			resultWriter = null;
			
			xURI bitMapUri = store.concat(getName() + TextParserUtil.CONNECT_SEP + "bitmap");
			bitMapWriter = new PrintWriter(adapter.getWriter(bitMapUri, false));
			BitDataUtil.writeBitItemMap(bitItemMap, bitMapWriter);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (resultWriter != null)
					resultWriter.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}

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
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return freqResults.size() == 0;
	}

	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		super.close();
		
		freqResults.clear();
		bitItemMap.clear();
	}


	/**
	 * 
	 * @return list of frequent itemset results {@link FreqResult}
	 */
	protected List<FreqResult> getFreqResults() {
		return freqResults;
	}

	
	/**
	 * 
	 * @param vRating
	 * @return {@link BitSet} of {@link RatingVector}
	 */
	protected BitSet toItemBitSet(RatingVector vRating) {
		return BitDataUtil.toItemBitSet(vRating, bitItemMap);
		
	}
	
	
	/**
	 * 
	 * @param bs
	 * @return {@link Pair} list of {@link BitSet}
	 */
	public List<Pair> toItemPairList(BitSet bs) {
		return BitDataUtil.toItemPairList(bitItemMap, bs);
	}
	
	
	/**
	 * Finding minimum frequent itemset and maximum frequent itemset of {@link RatingVector}
	 * 
	 * @param vRate {@link RatingVector}
	 * @return minimum frequent itemset and maximum frequent itemset
	 * @see FreqResultMinMax
	 */
	protected FreqResultMinMax findMinMaxOf(RatingVector vRate) {
		// A is user rating pattern
		BitSet A = BitDataUtil.toItemBitSet(vRate, bitItemMap);
		return findMinMaxOf(A);
	}
	
	
	/**
	 * Finding minimum frequent itemset and maximum frequent itemset of {@link RatingVector}
	 * 
	 * @param A {@link BitSet}
	 * @return minimum frequent itemset and maximum frequent itemset
	 * @see FreqResultMinMax
	 */
	protected FreqResultMinMax findMinMaxOf(BitSet A) {
		// A is user rating pattern
		int n = Math.min(freqResults.size(), MAX_FREQ_ITEMSETS);
		int maxCount = Integer.MIN_VALUE, maxIdx = -1;
		int minCount = Integer.MAX_VALUE, minIdx = -1;
		for (int i = 0; i < n; i++) {
			FreqResult result = freqResults.get(i); 
			BitSet B = (BitSet)result.bitset().clone();
			
			B.and(A);
			int count = DSUtil.countSetBit(B); // counting common rating values
			
			if (count == 0) continue;
			
			if (maxCount < count) {
				maxCount = count;
				maxIdx = i;
			}
			
			if (minCount > count) {
				minCount = count;
				minIdx = i;
			}
		}
		
		if (maxIdx == -1 || minIdx == -1)
			return null;
	
		return new FreqResultMinMax(
				freqResults.get(minIdx), 
				freqResults.get(maxIdx));
	}

	
	@Override
	public void view(Component comp) {
		// TODO Auto-generated method stub
		final JDialog view = new JDialog(
				UIUtil.getFrameForComponent(comp), "Knowledge base viewer", true);
		view.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		view.setSize(600, 400);
		view.setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		view.setLayout(new BorderLayout());
		
		FreqItemsetListTable tblFreqItemset = new FreqItemsetListTable();
		List<BitItemset> itemsetList = Util.newList();
		for (FreqResult freqResult : freqResults) {
			itemsetList.add(freqResult.itemset);
		}
		tblFreqItemset.update(itemsetList, bitItemMap);
		view.add(new JScrollPane(tblFreqItemset), BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		view.add(footer, BorderLayout.SOUTH);
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				view.dispose();
			}
		});
		footer.add(btnClose);
		
		view.setVisible(true);
	}


	/**
	 * 
	 * @param cf
	 * @return {@link FreqItemsetKB}
	 */
	protected static FreqItemsetKB create(final FreqItemsetBasedCF cf) {
		return new FreqItemsetKB() {

			
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


/**
 * This class represents a frequent bit itemset, called {@code frequent result}.
 * This class also contains bitset of such frequent bit itemset.
 * This is known as the result of mining bit frequent itemsets from bit data. 
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class FreqResult implements TextParsable, Serializable {
	
	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * The frequent bit itemset.
	 */
	protected BitItemset itemset = null;
	
	
	/**
	 * Bitset of the frequent bit itemset.
	 */
	protected BitSet bs = null;
	
	
	/**
	 * Default constructor.
	 */
	public FreqResult() {
		
	}
	
	
	/**
	 * Constructor with specified bit itemset and bitset.
	 * @param itemset specified bit itemset.
	 * @param bs specified bitset.
	 */
	public FreqResult(BitItemset itemset, BitSet bs) {
		this.itemset = itemset;
		this.bs = bs;
	}
	

	/**
	 * Constructor with specified bit itemset and size of bitset.
	 * @param itemset specified bit itemset.
	 * @param bitsetSize size of bitset.
	 */
	public FreqResult(BitItemset itemset, int bitsetSize) {
		this.itemset = itemset;
		this.bs = itemset.toBitSet(bitsetSize);
	}

	
	/**
	 * Getting internal frequent bit itemset.
	 * @return frequent bit itemset.
	 */
	public BitItemset itemset() {
		return itemset;
	}
	
	
	/**
	 * Getting internal bitset.
	 * @return internal bitset represented by {@link BitSet}.
	 */
	public BitSet bitset() {
		return bs;
	}

	
	@Override
	public String toText() {

		return TextParserUtil.toText(this.itemset.getBitItemIdList(), ",") + " " + TextParserUtil.MAIN_SEP + " " +
			this.itemset.getSupport() + " " + TextParserUtil.MAIN_SEP + " " +
			TextParserUtil.toText(this.bs);
		
	}
	
	
	@Override
	public String toString() {
		return toText();
	}
	
	
	@Override
	public void parseText(String spec) {
		List<String> array = TextParserUtil.split(spec, TextParserUtil.MAIN_SEP, null);
		
		List<Integer> bitItemIdList = TextParserUtil.parseListByClass(array.get(0), Integer.class, ",");
		float support = Float.parseFloat(array.get(1));
		BitItemset itemset = new BitItemset(bitItemIdList, support);
		
		BitSet bs = TextParserUtil.parseBitSet(array.get(2));
		
		this.itemset = itemset;
		this.bs = bs;
		
	}
	
	
}


/**
 * This class contains a minimum (shortest) frequent itemset and a maximum (longest) frequent itemset.
 * <ul>
 * <li>The minimum frequent itemset is wrapped by the {@link FreqResult} class, referred by its variable {@code min}.</li>
 * <li>The maximum frequent itemset is wrapped by the {@link FreqResult} class, referred by its variable {@code max}.</li>
 * </ul>
 * This class is also known as the result of mining frequent bit itemsets from bit data.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class FreqResultMinMax {
	
	
	/**
	 * Minimum frequent result containing minimum frequent bit itemset.
	 */
	protected FreqResult min = null;
	

	/**
	 * Maximum frequent result containing maximum frequent bit itemset.
	 */
	protected FreqResult max = null;
	
	
	/**
	 * Constructor with specified minimum frequent result and specified maximum frequent result.
	 *  
	 * @param min specified minimum frequent result containing minimum frequent bit itemset.
	 * @param max specified maximum frequent result containing maximum frequent bit itemset.
	 */
	public FreqResultMinMax(FreqResult min, FreqResult max) {
		this.min = min;
		this.max = max;
	}
	
	
	/**
	 * Getting minimum frequent itemset wrapped by {@link FreqResult}.
	 * @return minimum frequent itemset wrapped by {@link FreqResult}.
	 */
	public FreqResult min() {
		return min;
	}
	
	
	/**
	 * Getting maximum frequent itemset wrapped by {@link FreqResult}.
	 * @return maximum frequent itemset wrapped by {@link FreqResult}.
	 */
	public FreqResult max() {
		return max;
	}
	
	
}


