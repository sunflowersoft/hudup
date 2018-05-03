package net.hudup.alg.cf.bnet;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.FilterParam;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.cf.ModelBasedCF;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.RatingFilter;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.UriFilter;
import net.hudup.core.logistic.ValueTriple;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.TextParserUtil;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class BnetAbstractCF extends ModelBasedCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public BnetAbstractCF() {
		super();
	}
	
	
	/**
	 * 
	 * @param param specified recommendation parameter. 
	 * @param queryIds specified query identifiers (IDs). Such IDs can be item IDs or user IDs.
	 * @param referredRatingValue referred rating value.
	 * @param ratingFilter specified rating filter.
	 * @return list of {@link ValueTriple} (s).
	 */
	protected abstract List<ValueTriple> bnetEstimate(RecommendParam param, Set<Integer> queryIds, float referredRatingValue, RatingFilter ratingFilter);
	
	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) {
		
		List<ValueTriple> triples = bnetEstimate(param, queryIds, Constants.UNUSED, null);
		if (triples == null || triples.size() == 0)
			return null;
		
		RatingVector result = param.ratingVector.newInstance(true);
		ValueTriple.fillByValue1(result, triples);
		
		return result;
	}
	
	
	@Override
	public RatingVector recommend(RecommendParam param, int maxRecommend) {
		// TODO Auto-generated method stub
		param = preprocess(param);
		if (param == null)
			return null;
		
		filterList.prepare(param);
		Set<Integer> totalItemIds = getItemIds();
		Set<Integer> queryIds = Util.newSet();
		for (int itemId : totalItemIds) {
			
			if ( !param.ratingVector.isRated(itemId) && filterList.filter(getDataset(), FilterParam.create(itemId)) )
				queryIds.add(itemId);
		}
		
		float avgRating = (config.getMaxRating() + config.getMinRating()) / 2.0f; 
		List<ValueTriple> triples = bnetEstimate(param, queryIds, avgRating, new RatingFilter() {

			@Override
			public boolean accept(float ratingValue, float referredRatingValue) {
				// TODO Auto-generated method stub
				return ratingValue >= referredRatingValue;
			}
			
		});
		if (triples == null || triples.size() == 0)
			return null;
		
		ValueTriple.sortByValue1(triples, true, maxRecommend);
		
		RatingVector result = param.ratingVector.newInstance(true);
		ValueTriple.fillByValue1(result, triples);
		
		return result;
	}

	
	/**
	 * 
	 * @return list of item id (s)
	 */
	protected abstract Set<Integer> getItemIds();
	
	
	@Override
	public DataConfig createDefaultConfig() {
		
		DataConfig config = new DataConfig() {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			
			@Override
			public boolean validate(String key, Serializable value) {
				// TODO Auto-generated method stub
				
				if (value instanceof Number) {
					double valueNumber = ((Number)value).doubleValue();
					return valueNumber >= 0;
				}
				else
					return true;
			}
			
			
			
		};
		
		xURI store = xURI.create(Constants.KNOWLEDGE_BASE_DIRECTORY).concat(getName());
		config.setStoreUri(store);
		
		return config;
	}
	
	
	/**
	 * 
	 * @param store specified store (directory) where to get list of URI (s).
	 * @param EXT specified extension of files inside returned URI (s).
	 * @param prefixName prefix of names of files inside returned URI (s).
	 * @param sorted whether or not the returned list of URI (s) is sorted.
	 * @return list of {@link xURI} (s)
	 */
	public static List<xURI> getUriList(xURI store, final String EXT, final String prefixName, boolean sorted) {
		UriAdapter adapter = new UriAdapter(store);
		
		List<xURI> uriList = adapter.getUriList(store, new UriFilter() {
			
			@Override
			public boolean accept(xURI uri) {
				// TODO Auto-generated method stub
				if (EXT != null && !EXT.isEmpty()) {
					String ext = uri.getLastNameExtension(); 
					if (ext == null || ext.isEmpty())
						return false;
					
					ext = ext.toLowerCase();
					if(!ext.equals(EXT.toLowerCase()))
						return false;
				}
				
				String prefix = (prefixName == null ? "" : prefixName);
				if (!prefix.isEmpty() && !uri.getLastName().startsWith(prefix))
					return false;
				
				return true;
			}

			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return "No description";
			}
			
			
		});
		adapter.close();
		
		if (sorted) {
			Collections.sort(uriList, new Comparator<xURI>() {
			
				@Override
				public int compare(xURI uri1, xURI uri2) {
					String name1 = uri1.getLastName();
					String name2 = uri2.getLastName();
					
					String snum1 = null;
					if (name1.lastIndexOf(".") != -1)
						snum1 = name1.substring(name1.lastIndexOf(TextParserUtil.CONNECT_SEP) + 1, name1.lastIndexOf("."));
					else
						snum1 = name1.substring(name1.lastIndexOf(TextParserUtil.CONNECT_SEP) + 1);
						
					String snum2 = null;
					if (name2.lastIndexOf(".") != -1)
						snum2 = name2.substring(name2.lastIndexOf(TextParserUtil.CONNECT_SEP) + 1, name2.lastIndexOf("."));
					else
						snum2 = name2.substring(name2.lastIndexOf(TextParserUtil.CONNECT_SEP) + 1);
					
					return snum1.compareTo(snum2);
				}
				
			});
			
		}
		
		return uriList;
	}
	
	
}
