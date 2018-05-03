package net.hudup.logistic.inference;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Pair;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.xURI;
import net.hudup.data.bit.BitData;
import net.hudup.data.bit.BitDataUtil;
import net.hudup.logistic.math.BinaryAndNotProbItemMatrix;
import net.hudup.logistic.math.BitDatasetStatsProcessor;
import BayesianNetworks.BayesNet;
import BayesianNetworks.DiscreteVariable;
import BayesianNetworks.ProbabilityFunction;
import BayesianNetworks.ProbabilityVariable;
import InferenceGraphs.InferenceGraph;
import InferenceGraphs.InferenceGraphNode;
import QuasiBayesianInferences.QBInference;
import QuasiBayesianNetworks.QuasiBayesNet;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class BnetBinaryGraph extends InferenceGraph implements Serializable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public final static int WIDTH = 50;

	
	/**
	 * 
	 */
	public final static int HEIGHT = 20;

	
	/**
	 * 
	 */
	public final static int XDIS = WIDTH * 2;
	
	
	/**
	 * 
	 */
	public final static int YDIS = HEIGHT * 8;
	
	
	
	/**
	 * 
	 */
	protected int rootBitItemId = 0;
	
	
	/**
	 * 
	 */
	protected Map<Integer, Pair> bitItemMap = null;

	
	/**
	 * 
	 * @param bn
	 */
	protected BnetBinaryGraph(BayesNet bn) {
		super(bn);
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * 
	 * @param dataset
	 * @param minprob
	 * @return map of {@link BnetBinaryGraph} list
	 */
	public static Map<Integer, List<BnetBinaryGraph>> create(Dataset dataset, float minprob) {
		BitData bitData = BitData.create(dataset);
		
		BinaryAndNotProbItemMatrix matrix = new BinaryAndNotProbItemMatrix();
		matrix.setup(bitData);
		
		Map<Integer, List<BnetBinaryGraph>> bgraphMap = create(matrix, minprob);
		
		bitData.clear();
		matrix.clear();
		bitData = null;
		matrix = null;
		
		return bgraphMap;
	}
	
	
	/**
	 * 
	 * @param dataset
	 * @param minprob
	 * @return map of {@link BnetBinaryGraph} list
	 */
	public static Map<Integer, List<BnetBinaryGraph>> createNotUseMatrix(Dataset dataset, float minprob) {
		BitData bitData = BitData.create(dataset);
		
		Map<Integer, List<BnetBinaryGraph>> bgraphMap = create(bitData, minprob);
		
		bitData.clear();
		bitData = null;
		
		return bgraphMap;
	}

	
	/**
	 * 
	 * @param matrix
	 * @param minprob
	 * @return map of {@link BnetBinaryGraph} list
	 */
	private static Map<Integer, List<BnetBinaryGraph>> create(
			BinaryAndNotProbItemMatrix matrix,
			float minprob) {
		Map<Integer, List<BnetBinaryGraph>> result = Util.newMap();
		
		Set<Integer> bitIds = matrix.bitIds();
		for (int bitId : bitIds) {
			BnetBinaryGraph bgraph = create(matrix, bitId, minprob);
			if (bgraph == null)
				continue;
			
			Pair pair = bgraph.bitItemMap.get(bitId);
			int itemId = pair.key();
			
			List<BnetBinaryGraph> group = null;
			if (result.containsKey(itemId)) {
				group = result.get(itemId);
			}
			else {
				group = Util.newList();
				result.put(itemId, group);
			}
			
			group.add(bgraph);
		}
		
		return result;
	}
	
	
	/**
	 * 
	 * @param bitData
	 * @param minprob
	 * @return map of {@link BnetBinaryGraph} list
	 */
	private static Map<Integer, List<BnetBinaryGraph>> create(
			BitData bitData, 
			float minprob) {
		Map<Integer, List<BnetBinaryGraph>> result = Util.newMap();
		
		Set<Integer> bitIds = bitData.bitItemIds();
		for (int bitId : bitIds) {
			BnetBinaryGraph bgraph = create(bitData, bitId, minprob);
			if (bgraph == null)
				continue;
			
			Pair pair = bgraph.bitItemMap.get(bitId);
			int itemId = pair.key();
			
			List<BnetBinaryGraph> group = null;
			if (result.containsKey(itemId)) {
				group = result.get(itemId);
			}
			else {
				group = Util.newList();
				result.put(itemId, group);
			}
			
			group.add(bgraph);
		}
		
		return result;
	}

	
	/**
	 * 
	 * @param matrix
	 * @param rootBitItemId
	 * @param minprob
	 * @return {@link BnetBinaryGraph}
	 */
	public static BnetBinaryGraph create(
			BinaryAndNotProbItemMatrix matrix, 
			int rootBitItemId,
			float minprob) {
		if (!matrix.contains(rootBitItemId, rootBitItemId))
			return null;
		float rootProb = matrix.getProb(rootBitItemId);
		if (rootProb < minprob)
			return null;
		
		QuasiBayesNet bn = new QuasiBayesNet();
		Map<Integer, Pair> bitItemMap = Util.newMap();
		
		List<ProbabilityVariable> varList = Util.newList();
		List<ProbabilityFunction> fList = Util.newList();
		
		String rootName = BnetUtil.createItemNodeName(rootBitItemId);
		ProbabilityVariable root = new ProbabilityVariable(
				bn, rootName, varList.size(), 
				new String[] {"1", "0"}, new Vector<String>());
		varList.add(root);

		double[] rootCPT = new double[] {rootProb, 1- rootProb};
		ProbabilityFunction rootf = new ProbabilityFunction(
				bn,
				new DiscreteVariable[] {root}, 
				rootCPT,
				new Vector<String>()); 
		fList.add(rootf);
		
		bitItemMap.put(rootBitItemId, matrix.getPair(rootBitItemId));
		
		Set<Integer> bitIds = matrix.bitIds();
		
		for (int bitId : bitIds) {
			if (bitId == rootBitItemId)
				continue;
			if (!matrix.contains(bitId, rootBitItemId))
				continue;
			
			float andProb = matrix.getAndProb(bitId, rootBitItemId);
			float conditionProb = andProb / rootProb;
			if (andProb < minprob)
				continue;
			//if (conditionProb < minprob)
			//	continue;
			float andNotProb = matrix.getAndNotProb(bitId, rootBitItemId);
			
			String varName = BnetUtil.createItemNodeName(bitId);
			ProbabilityVariable var = new ProbabilityVariable(
					bn, varName, varList.size(), 
					new String[] {"1", "0"}, new Vector<String>());
			varList.add(var);
			
			double[] varCPT = new double[4];
			varCPT[0] = conditionProb;
			varCPT[1] = andNotProb / (1f - rootProb);
			varCPT[2] = 1f - varCPT[0];
			varCPT[3] = 1f - varCPT[1];
			
			ProbabilityFunction f = new ProbabilityFunction(
					bn,
					new DiscreteVariable[] {var, root}, 
					varCPT,
					new Vector<String>()); 
			fList.add(f);
			
			bitItemMap.put(bitId, matrix.getPair(bitId));
		}
		
		bn.set_probability_variables(varList.toArray(new ProbabilityVariable[0]));
		bn.set_probability_functions(fList.toArray(new ProbabilityFunction[0]));
		bn.set_name("Bayesian network for item " + rootBitItemId);
		
		BnetBinaryGraph bsb = new BnetBinaryGraph(bn);
		bsb.rootBitItemId = rootBitItemId;;
		bsb.bitItemMap = bitItemMap;
		
		bsb.updateNodes();
		return bsb;
	}
	
	
	/**
	 * 
	 * @param bitData
	 * @param rootBitItemId
	 * @param minprob
	 * @return {@link BnetBinaryGraph}
	 */
	public static BnetBinaryGraph create(
			BitData bitData, 
			int rootBitItemId,
			float minprob) {
		
		BitDatasetStatsProcessor processor = new BitDatasetStatsProcessor(bitData);
		
		float rootProb = processor.prob(rootBitItemId);
		if (rootProb < minprob)
			return null;
		
		QuasiBayesNet bn = new QuasiBayesNet();
		Map<Integer, Pair> bitItemMap = Util.newMap();
		
		List<ProbabilityVariable> varList = Util.newList();
		List<ProbabilityFunction> fList = Util.newList();
		
		String rootName = BnetUtil.createItemNodeName(rootBitItemId);
		ProbabilityVariable root = new ProbabilityVariable(
				bn, rootName, varList.size(), 
				new String[] {"1", "0"}, new Vector<String>()); // Luu y
		varList.add(root);

		double[] rootCPT = new double[] {rootProb, 1- rootProb};
		ProbabilityFunction rootf = new ProbabilityFunction(
				bn,
				new DiscreteVariable[] {root}, 
				rootCPT,
				new Vector<String>()); 
		fList.add(rootf);
		
		bitItemMap.put(rootBitItemId, bitData.get(rootBitItemId).pair());
		
		Set<Integer> bitIds = bitData.bitItemIds();
		
		for (int bitId : bitIds) {
			if (bitId == rootBitItemId)
				continue;
			
			float andProb = processor.probAnd(bitId, rootBitItemId);
			float conditionProb = andProb / rootProb;
			if (andProb < minprob)
				continue;
			//if (conditionProb < minprob)
			//	continue;
			float andNotProb = processor.probAndNot(bitId, rootBitItemId);
			float rconditionProb = andNotProb / (1f - rootProb);
			
			String varName = BnetUtil.createItemNodeName(bitId);
			ProbabilityVariable var = new ProbabilityVariable(
					bn, varName, varList.size(), 
					new String[] {"1", "0"}, new Vector<String>());
			varList.add(var);
			
			double[] varCPT = new double[4];
			varCPT[0] = conditionProb;
			varCPT[1] = rconditionProb;
			varCPT[2] = 1f - varCPT[0];
			varCPT[3] = 1f - varCPT[1];
			
			ProbabilityFunction f = new ProbabilityFunction(
					bn,
					new DiscreteVariable[] {var, root}, 
					varCPT,
					new Vector<String>()); 
			fList.add(f);
			
			bitItemMap.put(bitId, bitData.get(bitId).pair());
		}
		
		bn.set_probability_variables(varList.toArray(new ProbabilityVariable[0]));
		bn.set_probability_functions(fList.toArray(new ProbabilityFunction[0]));
		bn.set_name("Bayesian network for item " + rootBitItemId);
		
		BnetBinaryGraph bsb = new BnetBinaryGraph(bn);
		bsb.rootBitItemId = rootBitItemId;;
		bsb.bitItemMap = bitItemMap;
		
		bsb.updateNodes();
		return bsb;
	}


	
	/**
	 * 
	 * @param rating
	 * @param profile
	 * @return marginal posterior probability
	 */
	public float marginalPosterior(RatingVector rating, Profile profile) {
		return marginalPosterior(Pair.toPairList(rating));
	}
	
	
	
	
	/**
	 * 
	 * @param evList
	 * @return marginal posterior probability
	 */
	@SuppressWarnings("unchecked")
	public float marginalPosterior(List<Pair> evList) {
		Vector<InferenceGraphNode> nodes = this.get_nodes();
		for (InferenceGraphNode node : nodes) {
			node.clear_observation();

			String nodeName = node.get_name();
			int bitItemId = BnetUtil.extractItemId(nodeName);
			if (bitItemId == rootBitItemId)
				continue;
			
			for (Pair pair : evList) {
				int bitId = 
					BitDataUtil.findBitItemIdOf(bitItemMap, pair.key(), pair.value());
				
				if (bitId >= 0 && bitId == bitItemId) {
					node.set_observation_value("1");
					break;
				}
				
			}
		}
		
		
		return marginalPosterior(rootBitItemId);
		
	}
	
	
	/**
	 * 
	 * @param bitItemId
	 * @return marginal posterior probability
	 */
	public float marginalPosterior(int bitItemId) {
		QBInference qbi = new QBInference(get_bayes_net(), true);
        qbi.inference(BnetUtil.createItemNodeName(bitItemId));
        ProbabilityFunction result = qbi.get_result();
		
		double[] values = result.get_values();
		return (float)values[0];
	}
	
	
	/**
	 * 
	 * @param pair
	 * @return marginal posterior probability
	 */
	public float mariginalPosterior(Pair pair) {
		int bitItemId = BitDataUtil.findBitItemIdOf(
				bitItemMap, pair.key(), pair.value());
		
		return marginalPosterior(bitItemId);
	}
	
	
	
	/**
	 * 
	 */
	public void clearObservations() {
		@SuppressWarnings("unchecked")
		Vector<InferenceGraphNode> nodes = this.get_nodes();
		for (InferenceGraphNode node : nodes) {
			if (node.is_observed())
				node.clear_observation();
		}
	}
	
	
	/**
	 * 
	 * @param pair {@link Pair} of item id and its rating value
	 * @return whether contains specified {@link Pair}
	 */
	public boolean contains(Pair pair) {
		return BitDataUtil.findBitItemIdOf(bitItemMap, pair.key(), pair.value()) >= 0;
	}
	
	
	/**
	 * 
	 * @param bitItemId binary item id
	 * @return whether contains specified bit item id
	 */
	public boolean contains(int bitItemId) {
		return bitItemMap.containsKey(bitItemId);
	}
	
	
	/**
	 * 
	 * @return item id and is rating value of root node
	 */
	public Pair getRootItemPair() {
		return bitItemMap.get(rootBitItemId);
	}
	
	
	/**
	 * 
	 * @return binary item id of root node
	 */
	public int getRootBitItemId() {
		return rootBitItemId;
	}
	
	
	/**
	 * 
	 * @param bitItemId binary item id
	 * @return item id and is rating value of arbitrary node
	 */
	public Pair getItemPair(int bitItemId) {
		return bitItemMap.get(bitItemId);
	}
	
	
	/**
	 * 
	 * @param bitItemId
	 * @return {@link InferenceGraphNode}
	 */
	public InferenceGraphNode getBitItemNode(int bitItemId) {
		@SuppressWarnings("unchecked")
		Vector<InferenceGraphNode> nodes = this.get_nodes();
		for (InferenceGraphNode node : nodes) {
			String nodeName = node.get_name();
			if (BnetUtil.isAtt(nodeName))
				continue;
			
			int bitId = BnetUtil.extractItemId(nodeName);
			if (bitId == bitItemId)
				return node;
			
		}
		
		return null;
	}
	
	
	/**
	 * 
	 * @param pair
	 * @return {@link InferenceGraphNode}
	 */
	public InferenceGraphNode getBitItemNode(Pair pair) {
		int bitItemId = BitDataUtil.findBitItemIdOf(
				bitItemMap, pair.key(), pair.value());
		if (bitItemId == -1)
			return null;
		
		return getBitItemNode(bitItemId);
	}
	
	
	/**
	 * 
	 * @return {@link InferenceGraphNode}
	 */
	public InferenceGraphNode getRootNode() {
		return getBitItemNode(rootBitItemId);
	}
	
	
	/**
	 * 
	 * @return set of bit item id (s)
	 */
	public Set<Integer> bitItemIds() {
		return bitItemMap.keySet();
	}
	
	
	@SuppressWarnings("unchecked")
	protected void updateNodes() {
		Vector<InferenceGraphNode> nodes = this.get_nodes();
		
		int xlength = (nodes.size() - 1) * XDIS;
		xlength = Math.max(xlength, XDIS);

		int countChild = 0;
		for (int i = 0; i < nodes.size(); i++) {
			InferenceGraphNode node = nodes.get(i);
			
			String nodeName = node.get_name();
			int bitId = BnetUtil.extractItemId(nodeName);
			node.add_variable_property(DataConfig.BITITEMID_FIELD + " = " + bitId);
			
			Pair pair = getItemPair(bitId);
			node.add_variable_property(DataConfig.ITEMID_FIELD + " = " + pair.key());
			node.add_variable_property(DataConfig.RATING_FIELD + " = " + pair.value());
			
			int x = (bitId == rootBitItemId ? xlength / 2 : countChild * XDIS);
			int y = (bitId == rootBitItemId ? 0 : YDIS);
			
			node.add_variable_property(DataConfig.POSITION_FIELD + " = (" + x + ", " + y + ")");
			
			if (bitId != rootBitItemId)
				countChild++;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public static BnetBinaryGraph load(xURI uri) throws Exception {
		
		QuasiBayesNet bn = new QuasiBayesNet(uri.toURL());
		ProbabilityVariable[] vars = bn.get_probability_variables();
		int rootBitItemId = -1;
		Map<Integer, Pair> bitItemMap = Util.newMap();
		for (ProbabilityVariable var : vars) {
			Vector<String> props = var.get_properties();
			
			int bitItemId = -1;
			int itemId = -1;
			float rating = Constants.UNUSED;
			
			for (String prop : props) {
				prop.replaceAll("\\s", "");
				String[] array = prop.split("=");
				String attr = array[0].trim().toLowerCase();
				String value = array[1].trim().toLowerCase();
				
				if (attr.equals(DataConfig.BITITEMID_FIELD.toLowerCase())) {
					bitItemId = Integer.parseInt(value);
					
					ProbabilityFunction f = bn.get_probability_function(var.get_index());
					if (f.get_values().length == 2)
						rootBitItemId = bitItemId;
				}
				else if(attr.equals(DataConfig.ITEMID_FIELD.toLowerCase()))
					itemId = Integer.parseInt(value);
				else if(attr.equals(DataConfig.RATING_FIELD.toLowerCase()))
					rating = Float.parseFloat(value);
				
				
			}
			
			if (bitItemId != -1 && itemId != -1 && Util.isUsed(rating))
				bitItemMap.put(bitItemId, new Pair(itemId, rating));
			
			
		}
		
		BnetBinaryGraph bsb = new BnetBinaryGraph(bn);
		bsb.rootBitItemId = rootBitItemId;;
		bsb.bitItemMap = bitItemMap;
		
		return bsb;
		
	}
	
	
	
}


