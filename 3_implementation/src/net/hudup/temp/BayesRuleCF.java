package net.hudup.temp;

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
import net.hudup.core.data.FetcherUtil;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.MathUtil;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.parser.TextParserUtil;
import net.hudup.data.DatasetUtil2;
import net.hudup.logistic.math.DatasetStatsProcessor;
import net.hudup.logistic.math.ProbItemMatrix;

/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 * 
 * 
 */
@Deprecated
public class BayesRuleCF extends ModelBasedExtCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "bayes_rule";
	}

	
	@Override
	public KBase createKB() {
		// TODO Auto-generated method stub
		return BayesRuleKB.create(this);
	}


	@Override
	protected TagRatingVector estimate(RecommendParam param, Set<Integer> queryIds, float filterRatingValue) {
		// TODO Auto-generated method stub
		
		BayesRuleKB brKb = (BayesRuleKB)kb;

		Map<Integer, Integer> valueMap = Util.newMap();
		Set<Integer> fieldIds = param.ratingVector.fieldIds(true);
		for (int fieldId : fieldIds) {
			float value = param.ratingVector.get(fieldId).value;
			
			valueMap.put(fieldId, 
					(int)DatasetUtil2.zeroBasedRatingValueOf(value, brKb.getConfig().getMinRating()));
		}
		
		if (valueMap.size() == 0)
			return null;
		
		RatingVector result = param.ratingVector.newInstance(true);
		
		for (int queryId : queryIds) {
			BayesRule rule = brKb.getRule(queryId);
			if (rule == null)
				continue;
			
			int value = rule.getMaxPosteriorIdx(valueMap);
			float realValue = DatasetUtil2.realRatingValueOf(value, 
					brKb.getConfig().getMinRating());
			
        	if (Util.isUsed(filterRatingValue) && realValue != filterRatingValue)
        		continue;
        	else
        		result.put(queryId, realValue);
		}
		
		if (result.size() == 0)
			return null;
		else
			return new TagRatingVector(result);
	}


	@Override
	protected Set<Integer> getItemIds() {
		// TODO Auto-generated method stub
		BayesRuleKB brKb = (BayesRuleKB)kb;
		return brKb.itemIds;
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new BayesRuleCF();
	}

	
	
	
}



/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
abstract class BayesRuleKB extends KBaseAbstract {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	protected Map<Integer, BayesRule> ruleMap = Util.newMap();
	
	
	/**
	 * 
	 */
	protected Set<Integer> itemIds = Util.newSet();

	
	/**
	 * 
	 */
	private BayesRuleKB() {
		
	}
	
	
	@Override
	public void load() {
		// TODO Auto-generated method stub
		super.load();
		
		logger.info("Loading Bayesian rule not implement yet");
	}

	
	@Override
	public void learn(Dataset dataset, Alg alg) {
		// TODO Auto-generated method stub
		super.learn(dataset, alg);
		
		ruleMap = BayesRule.create(dataset);
		itemIds = ruleMap.keySet();
		
		
	}

	
	@Override
	public void export(DataConfig storeConfig) {
		// TODO Auto-generated method stub
		
		super.export(storeConfig);
		
		logger.info("Saving Bayesian rules not implement yet");
	}

	
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return ruleMap.size() == 0;
	}

	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		super.close();
		
		ruleMap.clear();
	}


	/**
	 * @param id
	 * @return {@link BayesRule}
	 */
	protected BayesRule getRule(int id) {
		return ruleMap.get(id);
	}
	
	
	/**
	 * 
	 * @param cf
	 * @return {@link BayesRuleKB}
	 */
	public static BayesRuleKB create(final BayesRuleCF cf) {
		return new BayesRuleKB() {

			
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
@NextUpdate
final class BayesRule {
	
	/**
	 * The number of values per node
	 */
	protected int nValues = -1;
	
	
	/**
	 * 
	 */
	protected int parentId = -1;
	
	
	/**
	 * 
	 */
	protected List<Integer> childIdList = Util.newList();
	
	
	/**
	 * 
	 */
	protected Map<Integer, float[]> cptList = Util.newMap();
	
	
	/**
	 * 
	 * @param nValues
	 * @param parentId
	 * @param childIdList
	 * @param cptList
	 */
	private BayesRule(int nValues, int parentId, List<Integer> childIdList, Map<Integer, float[]> cptList) {
		this.nValues = nValues;
		this.parentId = parentId;
		this.childIdList = childIdList;
		this.cptList = cptList;
	}
	
	
	/**
	 * 
	 * @param itemValueMap
	 * @return posterior probabilities
	 */
	public float[] posterior(Map<Integer, Integer> itemValueMap) {
		
		Map<Integer, Integer> valueMap = Util.newMap();
		valueMap.putAll(itemValueMap);
		valueMap.remove(parentId);
		
		float[] posterior = new float[nValues];
		
		float denom = propEval(valueMap);
		for (int i = 0; i < nValues; i++) {
			valueMap.put(parentId, i);
			float numer = propEval(valueMap);
			
			posterior[i] = numer / denom;
		}
		
		return posterior;
	}
	
	
	/**
	 * 
	 * @param itemValueMap
	 * @return index of maximum posterior probability
	 */
	public int getMaxPosteriorIdx(Map<Integer, Integer> itemValueMap) {
		float[] posterior = posterior(itemValueMap);
		
		int maxIdx = -1;
		float maxProp = -1;
		
		for (int i = 0; i < posterior.length; i++) {
			if (posterior[i] > maxProp) {
				maxIdx = i;
				maxProp = posterior[i];
			}
		}
		
		return maxIdx;
	}
	
	
	/**
	 * 
	 * @param itemValueMap
	 * @return accumulated probability of Bayesian formulation given item
	 */
	public float propEval(Map<Integer, Integer> itemValueMap) {
		
		Set<Integer> itemIds = itemValueMap.keySet();
		
		List<Integer> remains = Util.newList();
		remains.add(parentId);
		remains.addAll(childIdList);
		remains.removeAll(itemIds);
		
		if (remains.size() == 0)
			return joinProp(itemValueMap);
		
		float sumProp = 0;
		if (remains.size() == 1) {
			Map<Integer, Integer> valueMap = Util.newMap();
			valueMap.putAll(itemValueMap);
			for (int i = 0; i < nValues; i++) {
				valueMap.put(remains.get(0), i);
				sumProp += joinProp(valueMap);
			}
		}
		else { // Compute sigma
			List<Set<String>> list = Util.newList();
			for (int itemId : remains) {
				Set<String> set = Util.newSet();
				for (int i = 0; i < nValues; i++)
					set.add(itemId + TextParserUtil.CONNECT_SEP + i);
				list.add(set);
			}
			
			List<Set<String>> cartesian = MathUtil.cartesianProduct(list);
			
			for (Set<String> set : cartesian) {
				Map<Integer, Integer> valueMap = Util.newMap();
				valueMap.putAll(itemValueMap);
				
				for (String string : set) {
					String[] array = string.split(TextParserUtil.CONNECT_SEP);
					int itemId = Integer.parseInt(array[0]);
					int value = Integer.parseInt(array[1]);
					valueMap.put(itemId, value);
				}
				
				sumProp += joinProp(valueMap);
			}
		}
		
		return sumProp;
	}
	
	
	/**
	 * Joint probability evaluation
	 * @param itemValueMap
	 * @return joint probability of Bayesian formulation given item
	 */
	private float joinProp(Map<Integer, Integer> itemValueMap) {
		Set<Integer> itemIds = itemValueMap.keySet();
		if (itemIds.size() != childIdList.size() + 1)
			throw new RuntimeException("Invalid parameters");
		
		float propSum = 1;
		
		int parentValue = itemValueMap.get(parentId);
		float[] pCPT = cptList.get(parentId);
		propSum *= pCPT[parentValue]; 
		
		for (int childId : childIdList) {
			int childValue = itemValueMap.get(childId);
			float[] CPT = cptList.get(childId);
			propSum *= CPT[childValue * nValues + parentValue]; 
		}
		
		return propSum;
	}
	
	
	/**
	 * 
	 * @param dataset
	 * @return map of {@link BayesRule}
	 */
	public static Map<Integer, BayesRule> create(Dataset dataset) {
		DatasetStatsProcessor util = new DatasetStatsProcessor(dataset);
		ProbItemMatrix probMatrix = new ProbItemMatrix();
		probMatrix.setup(util, dataset);
		
		Set<Integer> itemIds = Util.newSet();
		FetcherUtil.fillCollection(itemIds, dataset.fetchItemIds(), true);
		Map<Integer, BayesRule> result = Util.newMap();
		for (int itemId : itemIds) {
			BayesRule rule = create(probMatrix, itemId, itemIds);
			
			if (rule != null)
				result.put(itemId, rule);
		}
		
		return result;
	}
	

	
	/**
	 * 
	 * @param probMatrix
	 * @param itemId
	 * @param totalIds
	 * @return {@link BayesRule}
	 */
	private static BayesRule create(ProbItemMatrix probMatrix, int itemId, Set<Integer> totalIds) {
		int n = probMatrix.getNumberValuesPerItem();
		int parentId = itemId;
		List<Integer> childIdList = Util.newList();
		Map<Integer, float[]> cptList = Util.newMap();
		
		float[] parentProbs = probMatrix.get(parentId, parentId);
		cptList.put(parentId, parentProbs);
		
		for (int childId : totalIds) {
			if (childId == parentId)
				continue;
			
			float[] childCPT = probMatrix.get(childId, parentId);
			for (int i = 0; i < childCPT.length; i++) {
				int c = i % n;
				childCPT[i] = childCPT[i] / parentProbs[c]; 
			}
			cptList.put(childId, childCPT);
			
			childIdList.add(childId);
		}
		
		
		return new BayesRule(n, parentId, childIdList, cptList);
	}
	
	
	
	
	
}
