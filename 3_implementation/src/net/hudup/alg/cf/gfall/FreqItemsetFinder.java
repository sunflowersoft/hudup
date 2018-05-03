/**
 * 
 */
package net.hudup.alg.cf.gfall;

import java.util.List;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.data.DataConfig;
import net.hudup.data.bit.BitData;
import net.hudup.data.bit.BitItem;
import net.hudup.data.bit.BitItemset;

/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class FreqItemsetFinder implements Alg {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public final static String MIN_SUP = "min_sup";
	
	
	/**
	 * 
	 */
	public final static float DEFAULT_MIN_SUP = 0f;

	
	/**
	 * 
	 */
	protected DataConfig config = null;

	
	/**
	 * {@link BitData}
	 */
	protected BitData bitData = null;
	
	
	/**
	 * Default constructor
	 */
	public FreqItemsetFinder() {
		this.config = createDefaultConfig();
	}
	
	
	/**
	 * Getting {@link BitData}
	 * @return internal bit data.
	 * 
	 */
	public BitData getBitData() {
		return bitData;
	}

	
	/**
	 * Setting {@link BitData}  
	 * @param dataset {@link BitData}
	 * 
	 */
	public void setup(BitData dataset) {
		bitData = dataset;
	}

	
	/**
	 * Finding a list of frequent {@link BitItemset}
	 * 
	 * 
	 * @return list of frequent {@link BitItemset}
	 * 
	 */
	public abstract List<BitItemset> findFreqItemset();


	/**
	 * Return a list of {@link BitItem} (s) having support &gt;= minimum support
	 * 
	 * @param dataset {@link BitData}
	 * @param minsup minimum support
	 * @return list of {@link BitItem}
	 * 
	 */
	protected static List<BitItem> preprocess(BitData dataset, float minsup) {
		List<BitItem> items = Util.newList();
		Set<Integer> bitItemIds = dataset.bitItemIds();
		
		for (int bitItemId : bitItemIds) {
			BitItem item = dataset.get(bitItemId);
			if (item != null && item.getSupport() >= minsup)
				items.add(item);
		}
		BitItem.sortItems(items, true);
		
		return items;
	}
	
	
	@Override
	public String toString() {
		return getName();
	}


	@Override
	public DataConfig getConfig() {
		// TODO Auto-generated method stub
		return config;
	}


	@Override
	public void resetConfig() {
		// TODO Auto-generated method stub
		config.clear();
		config.putAll(createDefaultConfig());
	}


	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		DataConfig config = new DataConfig();
		
		config.put(MIN_SUP, DEFAULT_MIN_SUP);
		return config;
	}


	
}
