/**
 * 
 */
package net.hudup.alg.cf.stat;

import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Cloneable;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.FilterParam;
import net.hudup.core.alg.KBase;
import net.hudup.core.alg.KBaseAbstract;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.cf.ModelBasedCF;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Pair;
import net.hudup.core.data.Rating;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;


/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class StatCF extends ModelBasedCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public StatCF() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public KBase createKB() {
		// TODO Auto-generated method stub
		return StatKB.create(this);
	}


	@Override
	public RatingVector recommend(RecommendParam param, int maxRecommend) {
		// TODO Auto-generated method stub
		param = preprocess(param);
		if (param == null)
			return null;
		filterList.prepare(param);
		
		StatKB sKb = (StatKB)kb;
		Set<Integer> queryIds = Util.newSet();
		Set<Integer> itemIds = sKb.getItemIds();
		
		for (int itemId : itemIds) {
			if (!param.ratingVector.isRated(itemId) && filterList.filter(getDataset(), FilterParam.create(itemId)) )
				queryIds.add(itemId);
		}
		
		RatingVector predict = estimate(param, queryIds);
		if (predict == null) return null;
		
		List<Pair> pairs = Pair.toPairList(predict);
		Pair.sort(pairs, true, maxRecommend);
		
		RatingVector rec = param.ratingVector.newInstance(true);
		Pair.fillRatingVector(rec, pairs);
		return rec;
	}

	
	
	
}



/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
abstract class StatKB extends KBaseAbstract {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	protected GeneralStat generalStat = new GeneralStat();
	
	
	/**
	 * 
	 */
	protected Map<Integer, Stat> userStats = Util.newMap();

	
	/**
	 * 
	 */
	protected Map<Integer, Stat> itemStats = Util.newMap();

	
	@Override
	public void load() {
		super.load();
		
		UriAdapter adapter = new UriAdapter(config);
		xURI store = config.getStoreUri();
		
		try {
			
			xURI generalStatUri = store.concat(getName() + TextParserUtil.CONNECT_SEP + "generalstat");
			if (adapter.exists(generalStatUri)) {
				String text = adapter.readText(generalStatUri).toString();
				text = text.trim();
				
				this.generalStat.parseText(text);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
		try {
			
			xURI userStatsUri = store.concat(getName() + TextParserUtil.CONNECT_SEP + "userstats");
			if (adapter.exists(userStatsUri)) {
				Reader userStatsUriReader = adapter.getReader(userStatsUri);
				
				userStats = TextParserUtil.parseTextParsableMap(userStatsUriReader, Integer.class, Stat.class);
				
				userStatsUriReader.close();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		
		try {
			
			xURI itemStatsUri = store.concat(getName() + TextParserUtil.CONNECT_SEP + "itemstats");
			if (adapter.exists(itemStatsUri)) {
				Reader itemStatsUriReader = adapter.getReader(itemStatsUri);
				
				itemStats = TextParserUtil.parseTextParsableMap(itemStatsUriReader, Integer.class, Stat.class);
				
				itemStatsUriReader.close();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		adapter.close();
	}

	
	@Override
	public void learn(Dataset dataset, Alg alg) {
		super.learn(dataset, alg);
		
		Fetcher<RatingVector> users = null;
		Fetcher<RatingVector> items = null;
		
		try {
			super.learn(dataset, alg);
			
			users = dataset.fetchUserRatings();
			while (users.next()) {
				RatingVector user = users.pick();
				if (user == null)
					continue;
				
				userStats.put(user.id(), new Stat());
				
			}
			users.reset();
			
			items = dataset.fetchItemRatings();
			while (items.next()) {
				RatingVector item = items.pick();
				if (item == null)
					continue;
				
				itemStats.put(item.id(), new Stat());
			}
			items.reset();
			
			float overSum = 0;
			int count = 0;
			while (users.next()) {
				RatingVector user = users.pick();
				if (user == null)
					continue;
				
				overSum += user.sum();
				count += user.count(true);
				Stat stat = userStats.get(user.id());
				stat.mean = user.mean();
			}
			generalStat.mean = overSum / count;
			users.reset();
			
			while (users.next()) {
				RatingVector user = users.pick();
				if (user == null)
					continue;
				
				Stat stat = userStats.get(user.id());
				
				count = 0;
				float mean = stat.mean;
				float devSum = 0;
				float overDevSum = 0;
				Collection<Rating> ratings = user.gets();
				for (Rating rating : ratings) {
					if (!rating.isRated())
						continue;
					
					devSum += rating.value - mean;
					overDevSum += rating.value - generalStat.mean;
					count ++;
				}
				
				if (count > 0) {
					stat.dev = devSum / count;
					stat.overDev = overDevSum / count;
				}
			}
			users.reset();
			
			
			while (items.next()) {
				RatingVector item = items.pick();
				if (item == null)
					continue;
				
				Stat stat = itemStats.get(item.id());
				stat.mean = item.mean();
			}
			items.reset();
			
			while (items.next()) {
				RatingVector item = items.pick();
				if (item == null)
					continue;
				
				Stat stat = itemStats.get(item.id());
				
				count = 0;
				float mean = stat.mean;
				float devSum = 0;
				float overDevSum = 0;
				Collection<Rating> ratings = item.gets();
				for (Rating rating : ratings) {
					if (!rating.isRated())
						continue;
					
					devSum += rating.value - mean;
					overDevSum += rating.value - generalStat.mean;
					count ++;
				}
				
				if (count > 0) {
					stat.dev = devSum / count;
					stat.overDev = overDevSum / count;
				}
			}
			items.reset();
			
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			if (users != null) {
				try {
					users.close();
				}
				catch (Throwable e) {
					e.printStackTrace();
				}
			}
			
			if (items != null) {
				try {
					items.close();
				}
				catch (Throwable e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}
	

	@Override
	public void export(DataConfig storeConfig) {
		super.export(storeConfig);
		
		UriAdapter adapter = new UriAdapter(storeConfig);
		
		xURI store = storeConfig.getStoreUri();
		try {
			xURI generalStatUri = store.concat(getName() + TextParserUtil.CONNECT_SEP + "generalstat");
			Writer generalStatWriter = adapter.getWriter(generalStatUri, false);
			generalStatWriter.write(generalStat.toText());
			generalStatWriter.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
		try {
			
			xURI userStatsUri = store.concat(getName() + TextParserUtil.CONNECT_SEP + "userstats");
			Writer userStatsWriter = adapter.getWriter(userStatsUri, false);
			TextParserUtil.toText(userStats, userStatsWriter);
			userStatsWriter.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		
		try {
			
			xURI itemStatsUri = store.concat(getName() + TextParserUtil.CONNECT_SEP + "itemstats");
			Writer itemStatsWriter = adapter.getWriter(itemStatsUri, false);
			TextParserUtil.toText(itemStats, itemStatsWriter);
			itemStatsWriter.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		adapter.close();
	}
	
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		super.close();
		
		generalStat.clear();
		userStats.clear();
		itemStats.clear();
	}


	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return userStats.size() == 0 || itemStats.size() == 0;
	}

	
	/**
	 * 
	 * @return set of item id
	 */
	protected Set<Integer> getItemIds() {
		return itemStats.keySet();
	}
	
	
	/**
	 * 
	 * @param cf
	 * @return {@link StatKB}
	 */
	public static StatKB create(final StatCF cf) {
		return new StatKB() {

			
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
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class GeneralStat implements Cloneable, TextParsable {
	
	/**
	 * 
	 */
	public float mean = 0;
	
	
	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		
		List<String> list = TextParserUtil.split(spec, ",", null);
		
		this.mean = Float.parseFloat(list.get(0));
	}
	
	
	/**
	 * 
	 */
	public String toText() {
		return mean + "";
	}
	
	
	@Override
	public String toString() {
		return toText();
	}
	
	
	@Override
	public Object clone() {
		GeneralStat stat = new GeneralStat();
		stat.mean = this.mean;
		
		return stat;
	}
	
	
	/**
	 * 
	 */
	public void clear() {
		
	}
	
	
}


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class Stat implements Cloneable, TextParsable {
	
	
	/**
	 * 
	 */
	public float mean = 0;
	
	
	/**
	 * 
	 */
	public float dev = 0;
	
	
	/**
	 * 
	 */
	public float overDev = 0;

	
	/**
	 * 
	 */
	public Stat() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		
		List<String> list = TextParserUtil.split(spec, ",", null);
		
		this.mean = Float.parseFloat(list.get(0));
		this.dev = Float.parseFloat(list.get(1));
		this.overDev = Float.parseFloat(list.get(2));
	}
	
	
	@Override
	public String toText() {
		return mean + ", " + dev + ", " + overDev;
	}
	
	
	@Override
	public String toString() {
		return toText();
	}
	
	
	@Override
	public Object clone() {
		Stat stat = new Stat();
		stat.mean = this.mean;
		stat.dev = this.dev;
		stat.overDev = this.overDev;
		
		return stat;
	}
	
	
}


