package net.hudup.logistic.inference;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Pair;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.data.bit.BitData;
import net.hudup.data.bit.BitDataUtil;
import net.hudup.logistic.math.BitDatasetStatsProcessor;
import BayesianNetworks.BayesNet;
import BayesianNetworks.DiscreteVariable;
import BayesianNetworks.ProbabilityFunction;
import BayesianNetworks.ProbabilityVariable;
import InferenceGraphs.InferenceGraphNode;
import QuasiBayesianNetworks.QuasiBayesNet;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class BnetBinaryGraphHybrid extends BnetBinaryGraph {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 * @param bn
	 */
	private BnetBinaryGraphHybrid(BayesNet bn) {
		super(bn);
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * 
	 * @param bitData
	 * @param rootBitItemId
	 * @param minprob
	 * @return {@link BnetBinaryGraphHybrid}
	 */
	public static BnetBinaryGraphHybrid create(
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
		
		
		int rootItemId = bitData.get(rootBitItemId).pair().key();
		Profile rootSessionProfile = bitData.getRealSessionProfile(rootItemId);
		
		if (rootSessionProfile != null) {
			AttributeList attRef = rootSessionProfile.getAttRef();
			for (int i = 0; i < attRef.size(); i++) {
				Attribute att = attRef.get(i);
				if (!att.isCategory())
					continue;
				
				int attId = att.getIndex();
				String varName = BnetUtil.createAttNodeName(attId);
				String[] values = att.getCategoryValues();
				
				ProbabilityVariable var = new ProbabilityVariable(
						bn, varName, varList.size(), 
						values, new Vector<String>());
				varList.add(var);
	
				double[] varCPT = new double[values.length * 2];
				for (int j = 0; j < values.length; j++) {
					
					float andProb = processor.probSessionOnBitItem(att, j, rootBitItemId);
					float conditionProb = andProb / rootProb;
					varCPT[j * 2] = conditionProb;
					
					float andNotProb = processor.probSessionOnNotBitItem(att, j, rootBitItemId);
					float rconditionProb = andNotProb / (1f - rootProb);
					varCPT[j * 2 + 1] = rconditionProb;
				}
				
				ProbabilityFunction f = new ProbabilityFunction(
						bn,
						new DiscreteVariable[] {var, root}, 
						varCPT,
						new Vector<String>()); 
				fList.add(f);
				
				
			}
		} // if rootSessionProfile != null
		
		bn.set_probability_variables(varList.toArray(new ProbabilityVariable[0]));
		bn.set_probability_functions(fList.toArray(new ProbabilityFunction[0]));
		bn.set_name("Bayesian network for item " + rootBitItemId);
		Vector<String>  props = Util.newVector();
		bn.set_properties(props);
		
		BnetBinaryGraphHybrid bsb = new BnetBinaryGraphHybrid(bn);
		bsb.rootBitItemId = rootBitItemId;;
		bsb.bitItemMap = bitItemMap;
		
		bsb.updateNodes();
		return bsb;
	}


	@Override
	public float marginalPosterior(RatingVector rating, Profile profile) {
		return marginalPosterior(Pair.toCategoryPairList(rating, profile));
	}

	
	@Override
	@SuppressWarnings("unchecked")
	public float marginalPosterior(List<Pair> evList) {
		Vector<InferenceGraphNode> nodes = this.get_nodes();
		for (InferenceGraphNode node : nodes) {
			node.clear_observation();
			
			String nodeName = node.get_name();
			if (BnetUtil.isItem(nodeName)) {
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
					
				} // End for
			}
			else if (BnetUtil.isAtt(nodeName)){
				int attId = BnetUtil.extractAttId(nodeName);
				for (Pair pair : evList) { 
					if (pair.key() == attId) {
					
						int valIndex = (int)pair.value();
						String[] values = node.get_values();
						node.set_observation_value(values[valIndex]);
					}
				}
				
			} // End else
			
		}
		
		
		return marginalPosterior(rootBitItemId);
		
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected void updateNodes() {
		Vector<InferenceGraphNode> nodes = this.get_nodes();
		
		int countItemChild = 0;
		int countAttChild = 0;
		for (int i = 0; i < nodes.size(); i++) {
			InferenceGraphNode node = nodes.get(i);
			
			String nodeName = node.get_name();
			if (BnetUtil.isAtt(nodeName)) {
				countAttChild ++;
			}
			else {
				int bitId = BnetUtil.extractItemId(nodeName);
				if (bitId == rootBitItemId)
					continue;
				
				countItemChild ++;
			}
		}
		
		
		int xlength = (Math.max(countAttChild, countItemChild) - 1) * XDIS;
		xlength = Math.max(xlength, XDIS);
		
		countItemChild = 0;
		countAttChild = 0;
		for (int i = 0; i < nodes.size(); i++) {
			InferenceGraphNode node = nodes.get(i);
			
			String nodeName = node.get_name();
			if (BnetUtil.isAtt(nodeName)) {
				
				int attId = BnetUtil.extractAttId(nodeName);
				node.add_variable_property(DataConfig.ATTRIBUTE_FIELD + " = " + attId);
				
				int x = countAttChild * XDIS;
				int y = 0;
				node.add_variable_property(DataConfig.POSITION_FIELD + " = (" + x + ", " + y + ")");
				
				countAttChild ++;
			}
			else
			{
				int bitId = BnetUtil.extractItemId(nodeName);
				node.add_variable_property(DataConfig.BITITEMID_FIELD + " = "  + bitId);
				
				Pair pair = getItemPair(bitId);
				node.add_variable_property(DataConfig.ITEMID_FIELD + " = " + pair.key());
				node.add_variable_property(DataConfig.RATING_FIELD + " = " + pair.value());
				
				int x = (bitId == rootBitItemId ? xlength / 2 : countItemChild * XDIS);
				int y = (bitId == rootBitItemId ? YDIS : 2 * YDIS);
				
				node.add_variable_property(DataConfig.POSITION_FIELD + " = (" + x + ", " + y + ")");
				
				if (bitId != rootBitItemId)
					countItemChild++;
			}
			
		} // For
	}

	
	/**
	 * 
	 * @param input
	 * @return {@link BnetBinaryGraphHybrid}
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static BnetBinaryGraphHybrid load(InputStream input) throws Exception {
		
		QuasiBayesNet bn = new QuasiBayesNet(input);
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
		
		BnetBinaryGraphHybrid bsb = new BnetBinaryGraphHybrid(bn);
		bsb.rootBitItemId = rootBitItemId;;
		bsb.bitItemMap = bitItemMap;
		
		
		return bsb;
		
	}

	
	
	
}
