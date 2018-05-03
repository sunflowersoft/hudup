package net.hudup.logistic.inference;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import net.hudup.core.Util;
import net.hudup.core.data.Pair;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.RatingFilter;
import net.hudup.core.logistic.ValueTriple;
import net.hudup.data.bit.BitDataUtil;
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
 *
 */
public class BnetBinaryUtil {

	/**
	 * 
	 * @param bitItemId
	 * @return {@link FiniteStates}
	 */
	public static FiniteStates createBitItemNode(int bitItemId) {
		return new FiniteStates(
				BnetUtil.createItemNodeName(bitItemId), new String[] {"0", "1"}); // Pay attention
		
	}
	
	
	/**
	 * 
	 * @param bitItemIdList
	 * @return {@link NodeList}
	 */
	protected static NodeList createBitItemNodeList(List<Integer> bitItemIdList) {
		
		NodeList nodeList = new NodeList();
		
		for (int bitItemId : bitItemIdList) {
			Node node = createBitItemNode(bitItemId);
			nodeList.insertNode(node);
		}
		
		return nodeList;
		
	}
	
	
	/**
	 * 
	 * @param bitItemMap
	 * @param nodeList
	 * @param userRating
	 * @return {@link Configuration}
	 */
	public static Configuration createBitItemConfiguration(
			Map<Integer, Pair> bitItemMap,
			NodeList nodeList, 
			RatingVector userRating) {
		
		Vector<FiniteStates> vars = new Vector<FiniteStates>();
		Vector<Integer> vals = new Vector<Integer>();
		
		Set<Integer> itemIds = userRating.fieldIds(true);
		for (int itemId : itemIds) {
			float rating = userRating.get(itemId).value;
			
			int bitItemId = BitDataUtil.findBitItemIdOf(
					bitItemMap, itemId, rating);
			if (bitItemId < 0)
				continue;
			
			String nodeName = BnetUtil.createItemNodeName(bitItemId);
			if (nodeList.getId(nodeName) == -1)
				continue;
			
			FiniteStates node = (FiniteStates)nodeList.getNode(nodeName);
			
			vars.addElement(node);
			vals.add(1);
		}
		
		return new Configuration(vars, vals);
		
	}
	
	
	/**
	 * 
	 * @param bitItemMap
	 * @param nodeList
	 * @param userRating
	 * @return {@link Evidence}
	 */
	public static Evidence createBitItemEvidence(
			Map<Integer, Pair> bitItemMap,
			NodeList nodeList, 
			RatingVector userRating) {
		
		Configuration conf = createBitItemConfiguration(
				bitItemMap, nodeList, userRating);
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
	 * @param bitItemMap
	 * @param ev
	 * @param queryItemIds
	 * @param referredRatingValue
	 * @param ratingFilter
	 * @return list of {@link ValueTriple} (s)
	 */
	public static List<ValueTriple> inference(
			Bnet bnet,
			Map<Integer, Pair> bitItemMap,
			Evidence ev,
			Set<Integer> queryItemIds,
			float referredRatingValue,
			RatingFilter ratingFilter) {
		
		VariableElimination ve = new VariableElimination(bnet, ev);
        ve.obtainInterest();
        
        ve.propagate();
        bnet.setCompiledPotentialList(ve.getResults());
        //bnet.showResults(ve);
        
        return getInferenceResult(bnet, bitItemMap, queryItemIds, referredRatingValue, ratingFilter);
	}

	
	/**
	 * 
	 * @param bnet
	 * @param bitItemMap
	 * @param queryItemIds
	 * @param referredRatingValue
	 * @param ratingFilter
	 * @return {@link RatingVector}
	 */
	private static List<ValueTriple> getInferenceResult(
			Bnet bnet, 
			Map<Integer, Pair> bitItemMap,
			Set<Integer> queryItemIds,
			float referredRatingValue,
			RatingFilter ratingFilter) {
		
		List<ValueTriple> triples = Util.newList();

        Vector<?> results = bnet.getCompiledPotentialList();
        
        for (int i = 0 ; i < results.size() ; i++) {
        	PotentialTable pot = (PotentialTable)results.elementAt(i);
        	Vector<?> vars = pot.getVariables();
        	FiniteStates node = (FiniteStates)vars.elementAt(0);
            int bitItemId = BnetUtil.extractItemId(node.getName());
            
            Pair pair = bitItemMap.get(bitItemId);
            int itemId = pair.key();
            if(!queryItemIds.contains(itemId))
            	continue;
            
    		double[] values = pot.getValues();
    		if (!DSUtil.assertNotNaN(values))
    			continue;
    		
    		if (values[1] <= values[0]) // Pay attention here: the recommended item must have high probability (values[1])
    			continue;
    		
            float rating = pair.value();
    		ValueTriple triple = new ValueTriple(itemId, rating, (float) values[1]);
			if (!Util.isUsed(referredRatingValue) || ratingFilter == null)
	        	triples.add(triple);
			else if (ratingFilter.accept(rating, referredRatingValue))
	        	triples.add(triple);
        }
        
        return triples;
	}
	
	

}
