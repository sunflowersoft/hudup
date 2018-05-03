package net.hudup.logistic.inference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import net.hudup.core.Util;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Pair;
import net.hudup.core.data.RatingTriple;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.LineProcessor;
import net.hudup.core.logistic.RatingFilter;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.ValueTriple;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.TextParserUtil;
import net.hudup.data.DatasetUtil2;
import elvira.Bnet;
import elvira.Configuration;
import elvira.Evidence;
import elvira.FiniteStates;
import elvira.Node;
import elvira.NodeList;
import elvira.inference.elimination.VariableElimination;
import elvira.potential.PotentialTable;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class BnetUtil {

	
	/**
	 * 
	 */
	public final static String ITEM_PREFIX = DataConfig.ITEM_FIELD + TextParserUtil.CONNECT_SEP;
	
	
	/**
	 * 
	 */
	public final static String ATT_PREFIX = DataConfig.ATTRIBUTE_FIELD + TextParserUtil.CONNECT_SEP;
	
	
	/**
	 * 
	 * @param itemId
	 * @return item node name
	 */
	public static String createItemNodeName(int itemId) {
		return ITEM_PREFIX + itemId;
	}

	
	/**
	 * 
	 * @param itemNodeName
	 * @return item id
	 */
	public static int extractItemId(String itemNodeName) {
		String sid = itemNodeName.substring(itemNodeName.lastIndexOf(TextParserUtil.CONNECT_SEP) + 1);
		return Integer.parseInt(sid);
	}

	
	/**
	 * 
	 * @param itemNodeName
	 * @return whether is item
	 */
	public static boolean isItem(String itemNodeName) {
		return itemNodeName.startsWith(ITEM_PREFIX);
	}
	
	
	/**
	 * 
	 * @param attId
	 * @return attribute node name
	 */
	public static String createAttNodeName(int attId) {
		return ATT_PREFIX + attId;
	}

	
	/**
	 * 
	 * @param attNodeName
	 * @return attribute node id
	 */
	public static int extractAttId(String attNodeName) {
		String sid = attNodeName.substring(attNodeName.lastIndexOf(TextParserUtil.CONNECT_SEP) + 1);
		return Integer.parseInt(sid);
	}

	
	/**
	 * 
	 * @param attNodeName
	 * @return whether is attribute
	 */
	public static boolean isAtt(String attNodeName) {
		return attNodeName.startsWith(ATT_PREFIX);
	}

	
	/**
	 * 
	 * @param nodeList
	 * @return list of item id (s)
	 */
	public static List<Integer> itemIdListOf(NodeList nodeList) {
		
		List<Integer> itemIdList = Util.newList();
		
		for (int i = 0; i < nodeList.size(); i++) {
			Node node = nodeList.elementAt(i);
			String name = node.getName();
			
			int id = extractItemId(name);
			itemIdList.add(id);
		}
		
		return itemIdList;
	}

	
	
	/**
	 * 
	 * @param itemId
	 * @param numberRatingsPerItem
	 * @param minRating
	 * @return {@link FiniteStates}
	 */
	public static FiniteStates createItemNode(
			int itemId, 
			int numberRatingsPerItem,
			int minRating) {
		int n = numberRatingsPerItem;
		String[] states = new String[n];
		int count = 0;
		for (int i = minRating; i < minRating + numberRatingsPerItem; i++) {
			states[count] = String.valueOf(DatasetUtil2.zeroBasedRatingValueOf(i, minRating));
			count ++;
		}
		
		return new FiniteStates(createItemNodeName(itemId), states);
	}

	
	/**
	 * 
	 * @return {@link NodeList}
	 */
	protected static NodeList createItemNodeList(
			List<Integer> itemIdList,
			int numberRatingsPerItem,
			int minRating) {
	
		NodeList nodeList = new NodeList();
		
		for (int itemId : itemIdList) {
			Node node = createItemNode(
					itemId, 
					numberRatingsPerItem, 
					minRating);
			nodeList.insertNode(node);
		}
		
		return nodeList;
	}

	
	/**
	 * 
	 * @param nodeList
	 * @param userRating
	 * @return {@link Configuration}
	 */
	public static Configuration createItemConfiguration(
			NodeList nodeList, 
			RatingVector userRating, 
			float minRating) {
		Vector<FiniteStates> vars = new Vector<FiniteStates>();
		Vector<Integer> vals = new Vector<Integer>();
		
		List<RatingTriple> triples = RatingTriple.getUserRatings(userRating);
		for (RatingTriple triple : triples) {
			int itemId = triple.itemId();
			String nodeName = createItemNodeName(itemId);
			if (nodeList.getId(nodeName) == -1)
				continue;
			
			FiniteStates node = (FiniteStates)nodeList.getNode(nodeName);
			
			int value = (int)DatasetUtil2.zeroBasedRatingValueOf(triple.getRating().value, minRating);
			String svalue = String.valueOf(value);
			
			String[] states = node.getStringStates();
			int found = -1;
			for (int i = 0; i < states.length; i++) {
				if (svalue.equals(states[i])) {
					found = i;
					break;
				}
			}
			
			if (found != -1) {
				vars.addElement(node);
				vals.add(value);
			}
		}
		
		return new Configuration(vars, vals);
	}

	
	/**
	 * 
	 * @param nodeList
	 * @param userRating
	 * @return {@link Evidence}
	 */
	public static Evidence createItemEvidence(
			NodeList nodeList, 
			RatingVector userRating,
			float minRating) {
		Configuration conf = createItemConfiguration(nodeList, userRating, minRating);
		Evidence evidence = new Evidence();
		
		Vector<?> vars = conf.getVariables();
		Vector<?> vals = conf.getValues();
		
		for (int i = 0; i < vars.size(); i++) {
			FiniteStates node = (FiniteStates)vars.get(i);
			int value = (Integer)vals.get(i);
			evidence.insert(node, value);
		}
		
		return evidence;
	}


	/**
	 * 
	 * @param bnet
	 * @param ev
	 * @param queryItemIds
	 * @param minRating
	 * @param referredRatingValue
	 * @param ratingFilter
	 * @return list of {@link ValueTriple}
	 */
	public static List<ValueTriple> inference(
			Bnet bnet,
			Evidence ev,
			Set<Integer> queryItemIds,
			float minRating,
			float referredRatingValue,
			RatingFilter ratingFilter) {
		
		VariableElimination ve = new VariableElimination(bnet, ev);
        ve.obtainInterest();
        
        ve.propagate();
        bnet.setCompiledPotentialList(ve.getResults());
        //bnet.showResults(ve);
        
        return getInferenceResult(bnet, queryItemIds, minRating, referredRatingValue, ratingFilter);
	}
	
	
	/**
	 * 
	 * @param bnet
	 * @param queryItemIds
	 * @param minRating
	 * @param referredRatingValue
	 * @param ratingFilter
	 * @return list of {@link ValueTriple}
	 */
	private static List<ValueTriple> getInferenceResult(
			Bnet bnet, 
			Set<Integer> queryItemIds,
			float minRating,
			float referredRatingValue,
			RatingFilter ratingFilter) {
		
		List<ValueTriple> triples = Util.newList();

        Vector<?> results = bnet.getCompiledPotentialList();
        
        for (int i = 0 ; i < results.size() ; i++) {
        	PotentialTable pot = (PotentialTable)results.elementAt(i);
        	Vector<?> vars = pot.getVariables();
        	FiniteStates node = (FiniteStates)vars.elementAt(0);
            int itemId = BnetUtil.extractItemId(node.getName());
            
            if(!queryItemIds.contains(itemId))
            	continue;

    		double[] values = pot.getValues();
    		if (!DSUtil.assertNotNaN(values))
    			continue;
    		
        	int total = (int)FiniteStates.getSize(vars);
    		int maxIndex = -1;
    		double maxProp = -1;
        	for (int j = 0 ; j < total; j++) {
        		if (maxProp < values[j]) {
        			maxProp = values[j];
        			maxIndex = j;
        		}
        	}
        	
        	if (maxIndex == -1)
        		continue;
        	
    		String srating = node.getStates().elementAt(maxIndex).toString();
    		int value = Integer.parseInt(srating);
        	float rating = DatasetUtil2.realRatingValueOf(value, minRating);
        	ValueTriple triple = new ValueTriple(itemId, rating, (float) maxProp);
        	
			if (!Util.isUsed(referredRatingValue) || ratingFilter == null)
	        	triples.add(triple);
			else if (ratingFilter.accept(rating, referredRatingValue))
	        	triples.add(triple);
        }
        
        return triples;
	}
	

	/**
	 * 
	 * @param bnet
	 * @param ev
	 * @param queryItemList
	 * @param minRating
	 * @return list of {@link ValueTriple}
	 */
	public static List<ValueTriple> inference(
			Bnet bnet,
			Evidence ev,
			List<Pair> queryItemList,
			float minRating) {
		
		VariableElimination ve = new VariableElimination(bnet, ev);
        ve.obtainInterest();
        
        ve.propagate();
        bnet.setCompiledPotentialList(ve.getResults());
        //bnet.showResults(ve);
        
        return getInferenceResult(bnet, queryItemList, minRating);
	}

	
	/**
	 * 
	 * @param bnet
	 * @param queryItemList
	 * @param minRating
	 * @return list of {@link ValueTriple}
	 */
	private static List<ValueTriple> getInferenceResult(
			Bnet bnet, 
			List<Pair> queryItemList,
			float minRating) {
		
		List<ValueTriple> triples = Util.newList();

        Vector<?> results = bnet.getCompiledPotentialList();
        
        List<Integer> queryItemIdList = Pair.getKeyList(queryItemList);
        for (int i = 0 ; i < results.size() ; i++) {
        	PotentialTable pot = (PotentialTable)results.elementAt(i);
        	Vector<?> vars = pot.getVariables();
        	FiniteStates node = (FiniteStates)vars.elementAt(0);
            
        	int itemId = BnetUtil.extractItemId(node.getName());
        	int itemIndex = queryItemIdList.indexOf(itemId);
            if(itemIndex == -1)
            	continue;

    		double[] values = pot.getValues();
    		if (!DSUtil.assertNotNaN(values))
    			continue;
    		
    		float itemValue = queryItemList.get(itemIndex).value();
    		int index = DatasetUtil2.zeroBasedRatingValueOf(itemValue, minRating);
        	triples.add(new ValueTriple(itemId, itemValue, (float) values[index]));
    				
        }
        
        return triples;
	}
	
	
	/**
	 * 
	 * @param adapter
	 * @param bnet
	 * @param uri
	 */
	public static void saveBnet(UriAdapter adapter, Bnet bnet, xURI uri) {
		try {
			Writer writer = adapter.getWriter(uri, false);
			bnet.save(new PrintWriter(writer));
			writer.close();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 
	 * @param adapter
	 * @param uri
	 * @return {@link Bnet}
	 */
	public static Bnet loadBnet(UriAdapter adapter, xURI uri) {
		try {
			return new Bnet(uri.toURL());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return null;
	}

	
	/**
	 * 
	 * @param bnetList
	 * @return MT map of {@link BitSet}
	 */
	public static Map<Integer, BitSet> createMT(List<Bnet> bnetList) {
		
		Set<Integer> totalItemIdSet = Util.newSet();
		
		for (Bnet bnet : bnetList) {
		    NodeList nodelList = bnet.getNodeList();
		    List<Integer> idList = BnetUtil.itemIdListOf(nodelList);
		    totalItemIdSet.addAll(idList);
		}
		
		Map<Integer, BitSet> MT = Util.newMap();
		for (int itemId : totalItemIdSet) {
			BitSet bs = new BitSet(bnetList.size());
			MT.put(itemId, bs);
		}
		
		for (int i = 0; i < bnetList.size(); i++) {
			
		    NodeList nodelList = bnetList.get(i).getNodeList();
		    
		    List<Integer> idList = BnetUtil.itemIdListOf(nodelList);
		    for (int id : idList) {
		    	BitSet bs = MT.get(id);
		    	bs.set(i);
		    }
		}
		
		return MT;
	}
	
	
	
	/**
	 * 
	 * @param adapter
	 * @param store
	 * @param prefixName
	 * @return MT map of {@link BitSet}
	 */
	public static Map<Integer, BitSet> loadMT(UriAdapter adapter, xURI store, String prefixName) {
		final Map<Integer, BitSet> MT = Util.newMap();
		
		prefixName = (prefixName == null ? "" : prefixName);
		xURI uri = store.concat(prefixName + TextParserUtil.CONNECT_SEP + "MT");
		
		BufferedReader reader = new BufferedReader(adapter.getReader(uri));
		DSUtil.lineProcess(reader, new LineProcessor() {
			
			@Override
			public void process(String line) {
				String[] comps = line.split("\\s");
				
				if (comps.length < 2)
					return;;
				
				int itemId = Integer.parseInt(comps[0]);
				BitSet bs = TextParserUtil.parseBitSet(comps[1]);
				MT.put(itemId, bs);
			}
		});
		
		try {
			reader.close();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return MT;
	}
	
	
	/**
	 * 
	 * @param adapter
	 * @param store
	 * @param MT
	 * @param prefixName
	 */
	public static void saveMT(UriAdapter adapter, xURI store, Map<Integer, BitSet> MT, String prefixName) {
		adapter.create(store, true);
		
		prefixName = (prefixName == null ? "" : prefixName);
		xURI uri = store.concat(prefixName + TextParserUtil.CONNECT_SEP + "MT");

		if (adapter.exists(uri))
			adapter.delete(uri, null);
		
		Writer writer = null;
		try {
			writer = adapter.getWriter(uri, false);
			
			Set<Integer> itemIds = MT.keySet();
			for (int itemId : itemIds) {
				BitSet bs = MT.get(itemId);
				String line = "" + itemId + " " + TextParserUtil.toText(bs);
				
				writer.write(line + "\n");
			}
			
			writer.flush();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				if (writer != null)
					writer.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	} //saveMT
	
	
	
	/**
	 * 
	 * @param MT
	 * @param bnetIdx
	 * @param itemIds
	 * @return count for Bnet index
	 */
	public static int countForBnetIdx(Map<Integer, BitSet> MT, int bnetIdx, Collection<Integer> itemIds) {
		int count = 0;
		for (int itemId : itemIds) {
			if (!MT.containsKey(itemId))
				continue;
			
			BitSet bs = MT.get(itemId);
			if (bs.get(bnetIdx))
				count++;
		}
		
		return count;
	}
	
	
}
