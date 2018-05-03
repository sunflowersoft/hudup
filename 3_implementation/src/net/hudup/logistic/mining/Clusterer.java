/**
 * 
 */
package net.hudup.logistic.mining;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.parser.TextParserUtil;
import weka.clusterers.EM;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Clusterer {
	
	/**
	 * 
	 */
	public final static int MAX_ITERS = 100;
	
	/**
	 * 
	 */
	protected Instances instances = null;
	
	
	/**
	 * 
	 */
	protected List<Integer> rowIdList = Util.newList();
	
	
	/**
	 * 
	 */
	protected Map<Integer, Cluster> clusterMap = Util.newMap();
	
	
	/**
	 * 
	 * @param instances
	 */
	public Clusterer(Instances instances) {
		this.instances = instances;
	}
	
	
	
	/**
	 * 
	 * @param matrix
	 * @param rowIdList
	 */
	public Clusterer(float[][] matrix, List<Integer> rowIdList) {
		this.rowIdList.clear();
		this.rowIdList.addAll(rowIdList);
		
		FastVector attrs = new FastVector();
		int columns = matrix[0].length;
		for (int i = 0; i < columns; i++) {
			String nodeName = DataConfig.ATTRIBUTE_FIELD + TextParserUtil.CONNECT_SEP + i;
			Attribute attr = new Attribute(nodeName);
			
			attrs.addElement(attr);
		}
		
		Instances instances = new Instances("bayesinsts", attrs, 100);
		for (int i = 0; i < matrix.length; i++) {
			float[] row = matrix[i];
			
			Instance instance = new Instance(attrs.size());
			for (int j = 0; j < row.length; j++) {
				float value = row[j];
				if (Util.isUsed(value))
					instance.setValue(j, value);
				else
					instance.setMissing(j);
			}
			
			instances.add(instance);
		}
		
		this.instances = instances;
	}

	
	
	/**
	 * 
	 * @param bitMatrix
	 * @param bitRowIdList
	 */
	public Clusterer(byte[][] bitMatrix, List<Integer> bitRowIdList) {
		this.rowIdList.clear();
		this.rowIdList.addAll(bitRowIdList);
		
		FastVector attrs = new FastVector();
		int columns = bitMatrix[0].length;
		for (int i = 0; i < columns; i++) {
			String nodeName = DataConfig.ATTRIBUTE_FIELD + TextParserUtil.CONNECT_SEP + i;
			Attribute attr = new Attribute(nodeName);
			
			attrs.addElement(attr);
		}
		
		Instances instances = new Instances("bayesinsts", attrs, 100);
		for (int i = 0; i < bitMatrix.length; i++) {
			byte[] row = bitMatrix[i];
			
			Instance instance = new Instance(attrs.size());
			for (int j = 0; j < row.length; j++) {
				byte value = row[j];
				instance.setValue(j, value);
			}
			
			instances.add(instance);
		}
		
		this.instances = instances;
	}

	
	/**
	 * 
	 * @param bnetNodeNumber maximum node number per Bayesian network
	 */
	public void buildClustersByBnetNodeNumber(int bnetNodeNumber) {
		if (bnetNodeNumber <= 0)
			buildClusters();
		else {
			int n = this.instances.numInstances();
			buildClusters(n / bnetNodeNumber);
		}
	}
	
	
	/**
	 * 
	 */
	public void buildClusters() {
		buildClusters(-1);
	}
	
	
	/**
	 * 
	 */
	public void buildClusters(int numCluster) {
		EM em = new EM();
		this.clusterMap.clear();
		
		try {
			em.setMaxIterations(MAX_ITERS);
			em.setNumClusters(numCluster);
			
			em.buildClusterer(this.instances);
			numCluster = em.numberOfClusters();
			if (numCluster == 0)
				return;
			
			int n = this.instances.numInstances();
			for (int i = 0; i < n; i++) {
				Instance instance = this.instances.instance(i);
				Integer clusterIdx = em.clusterInstance(instance);
				
				Cluster cluster = null;
				if (!this.clusterMap.containsKey(clusterIdx)) {
					cluster = new Cluster();
					this.clusterMap.put(clusterIdx, cluster);
				}
				else {
					cluster = this.clusterMap.get(clusterIdx);
				}
				
				int rowId = this.rowIdList.get(i);
				cluster.getIdList().add(rowId);
				cluster.addInstance(toRow(instance));
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 
	 * @param instance
	 * @return row
	 */
	private static float[] toRow(Instance instance) {
		double[] drow = instance.toDoubleArray();
		
		for (int i = 0; i < drow.length; i++) {
			if (instance.isMissing(i))
				drow[i] = Constants.UNUSED;
		}
		return DSUtil.doubleToFloat(drow);
	}
	

	/**
	 * 
	 * @return list of {@link Cluster}
	 */
	public List<Cluster> getClusters() {
		Collection<Cluster> clusters = clusterMap.values();
		
		List<Cluster> result = Util.newList();
		for (Cluster cluster : clusters) {
			if (cluster.rowIdList.size() >= 2 && cluster.instances.size() >= 2)
				result.add(cluster);
		}
		
		return result;
	}
	
	
	/**
	 * 
	 * @param row
	 * @param numAttr
	 * @return instance of row
	 */
	public static Instance toInstance(float[] row, int numAttr) {
		Instance instance = new Instance(numAttr);
		for (int j = 0; j < row.length; j++) {
			float value = row[j];
			if (Util.isUsed(value))
				instance.setValue(j, value);
			else
				instance.setMissing(j);
		}
		
		return instance;
	}
	
	
}

