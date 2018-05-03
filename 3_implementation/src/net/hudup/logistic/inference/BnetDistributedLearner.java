package net.hudup.logistic.inference;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import net.hudup.core.Util;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.FetcherUtil;
import net.hudup.core.data.Pair;
import net.hudup.data.DatasetUtil2;
import net.hudup.logistic.math.DatasetStatsProcessor;
import elvira.Bnet;
import elvira.Link;
import elvira.LinkList;
import elvira.Node;
import elvira.NodeList;
import elvira.Relation;
import elvira.potential.PotentialTable;

/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class BnetDistributedLearner {

	
	/**
	 * 
	 * @param dataset
	 * @return map of {@link Bnet}
	 */
	public static Map<Integer, Bnet> createDistributedBnet(Dataset dataset) {
		Map<Integer, Bnet> bnetMap = Util.newMap();
		DatasetStatsProcessor util = new DatasetStatsProcessor(dataset);
		
		Set<Integer> itemIds = Util.newSet();
		FetcherUtil.fillCollection(itemIds, dataset.fetchItemIds(), true);
		for (int itemId : itemIds) {
			Bnet bnet = createDistributedBnet(util, itemIds, itemId, 
					dataset.getConfig().getNumberRatingsPerItem(), 
					(int)dataset.getConfig().getMinRating());
			
			if (bnet != null)
				bnetMap.put(itemId, bnet);
		}
		
		return bnetMap;
	}
	
	
	/**
	 * 
	 * @param util
	 * @param itemIds
	 * @param itemId
	 * @param numberRatingsPerItem
	 * @param minRating
	 * @return {@link Bnet}
	 */
	private static Bnet createDistributedBnet(DatasetStatsProcessor util, Collection<Integer> itemIds, int itemId, 
			int numberRatingsPerItem, int minRating) {
		
		NodeList nodeList = new NodeList();
		
		Node root = BnetUtil.createItemNode(
				itemId, numberRatingsPerItem, minRating);
		
		for (int id : itemIds) {
			if (id == itemId)
				continue;
			
			Node node = BnetUtil.createItemNode(id, numberRatingsPerItem, minRating);
			LinkList linkList = new LinkList();
			Link link = new Link(root, node);
			linkList.insertLink(link);
			
			node.setParents(linkList);
			
			nodeList.insertNode(node);
		}
		
		Bnet bnet = new Bnet(nodeList);
		computeCPT(bnet, util, numberRatingsPerItem, minRating);
		
		
		return bnet;
	}
	
	
	
	/**
	 * 
	 * @param bnet
	 * @param util
	 * @param numberRatingsPerItem
	 * @param minRating
	 */
	private static void computeCPT(Bnet bnet, DatasetStatsProcessor util, int numberRatingsPerItem, int minRating) {
		
		@SuppressWarnings("unchecked")
		Vector<Relation> relList = bnet.getRelationList();
		for (Relation rel : relList) {
			PotentialTable pot = (PotentialTable)rel.getValues();
			NodeList nlist = rel.getVariables();
			if (nlist.size() == 1) {
				int n = numberRatingsPerItem;
				double[] values = new double[n];
				
				Node node = nlist.elementAt(0);
				int nodeId = BnetUtil.extractItemId(node.getName());
				
				for (int i = 0; i < numberRatingsPerItem; i++) {
					float value = DatasetUtil2.realRatingValueOf(i, minRating);
					values[i] = util.probItem(nodeId, value);
				}
				
				pot.setValues(values);
			}
			else if (nlist.size() == 2) {
				int n = numberRatingsPerItem * numberRatingsPerItem;
				double[] values = new double[n];
				
				Node node = nlist.elementAt(0);
				int nodeId = BnetUtil.extractItemId(node.getName());

				Node parent = nlist.elementAt(1);
				int parentId = BnetUtil.extractItemId(parent.getName());
				
				for (int i = 0; i < numberRatingsPerItem; i++) {
					float nodeValue = DatasetUtil2.realRatingValueOf(i, minRating);
					Pair nodePair = new Pair(nodeId, nodeValue);
					
					for (int j = 0; j < numberRatingsPerItem; j++) {
						float parentValue = DatasetUtil2.realRatingValueOf(j, minRating);
						float parentProp = util.probItem(parentId, parentValue);
						
						Pair parentPair = new Pair(parentId, parentValue);
						List<Pair> pairs = Util.newList();
						pairs.add(nodePair);
						pairs.add(parentPair);
						float prop = util.probItem(pairs);
						
						values[i * numberRatingsPerItem + j] = prop / parentProp;
					}
				}
				
				
			}
			
		} // End Relation
		
	}
	
	
	
}
