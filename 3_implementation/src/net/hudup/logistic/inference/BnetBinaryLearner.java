/**
 * 
 */
package net.hudup.logistic.inference;

import java.util.List;
import java.util.Vector;

import net.hudup.core.Util;
import net.hudup.core.logistic.DSUtil;
import net.hudup.data.bit.BitData;
import net.hudup.data.bit.BitMatrix;
import net.hudup.logistic.mining.Cluster;
import net.hudup.logistic.mining.Clusterer;
import elvira.Bnet;
import elvira.CaseListMem;
import elvira.NodeList;
import elvira.database.DataBaseCases;
import elvira.learning.DELearning;
import elvira.learning.K2Learning;
import elvira.learning.K2Metrics;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public final class BnetBinaryLearner {

	
	/**
	 * 
	 * @param bitData
	 * @param maxParents
	 * @return list of {@link Bnet}
	 */
	public static List<Bnet> learning_clustered(BitData bitData, int maxParents) {
		List<Bnet> bnetList = Util.newList();
		
		List<DataBaseCases> dbcList = loadClusteredBitDbCases(bitData);
		for (DataBaseCases dbc : dbcList) {
			K2Learning k2 = new K2Learning(
					dbc, 
					dbc.getNodeList(),
					maxParents,
					new K2Metrics(dbc));
			k2.learning();
			
			DELearning de = new DELearning(dbc, k2.getOutput());
		    de.learning();
		    
		    Bnet bnet = de.getOutput();
		    NodeList nodelList = bnet.getNodeList();
		    if (nodelList.size() < 2)
		    	continue;
	    
		    bnetList.add(bnet);
		    
		}
		
		return bnetList;
	}


	/**
	 * 
	 * @param bitData
	 * @param maxParents
	 * @return list of {@link Bnet}
	 */
	public static List<Bnet> learning(BitData bitData, int maxParents) {
		List<DataBaseCases> dbcList = loadBitDbCases(bitData);

		return learning(dbcList, maxParents);

	}
	
	
	/**
	 * 
	 * @param dbcList
	 * @param maxParents
	 * @return list of {@link Bnet}
	 */
	public static List<Bnet> learning(List<DataBaseCases> dbcList, int maxParents) {
		List<Bnet> bnetList = Util.newList();
		
		for (DataBaseCases dbc : dbcList) {
			K2Learning k2 = new K2Learning(
					dbc, 
					dbc.getNodeList(),
					maxParents,
					new K2Metrics(dbc));
			k2.learning();
			
			DELearning de = new DELearning(dbc, k2.getOutput());
		    de.learning();
		    
		    Bnet bnet = de.getOutput();
	    
		    NodeList nodelList = bnet.getNodeList();
		    if (nodelList.size() >= 2)
		    	bnetList.add(bnet);
		}
		
		
		return bnetList;
	}

	
	/**
	 * 
	 * @param bitData
	 * @return list of {@link DataBaseCases}
	 */
	public static List<DataBaseCases> loadBitDbCases(BitData bitData) {
		List<DataBaseCases> dbcList = Util.newList();
		
		BitMatrix bitSessionMatrix = bitData.createBitSessionMatrix(); 
		DataBaseCases dbc = createBitDbCases(bitSessionMatrix);
		
		dbcList.add(dbc);
		
		return dbcList;
	}
	
	
	
	/**
	 * 
	 * @param bitData
	 * @return list of {@link DataBaseCases}
	 */
	public static List<DataBaseCases> loadClusteredBitDbCases(BitData bitData) {
		List<DataBaseCases> dbcList = Util.newList();
		
		BitMatrix bitItemMatrix = bitData.createBitItemMatrix();
		
		Clusterer clusterer = new Clusterer(bitItemMatrix.matrix, bitItemMatrix.rowIdList);
		clusterer.buildClusters();
		List<Cluster> clusters = clusterer.getClusters();
		
		for (int i = 0; i < clusters.size(); i++) {
			Cluster cluster = clusters.get(i);
			
			List<Integer> subBitItemIdList = cluster.getIdList();
			if (subBitItemIdList.size() == 0)
				continue;
			
			BitData subBitData = bitData.getSub(subBitItemIdList);
			BitMatrix subBitSessionMatrix = subBitData.createBitSessionMatrix(); 
			
			DataBaseCases dbc = createBitDbCases(subBitSessionMatrix);
			
			dbcList.add(dbc);
			
		}
		
		return dbcList;
	}

	
	/**
	 * 
	 * @param bitSessionMatrix
	 * @return {@link DataBaseCases}
	 */
	public static DataBaseCases createBitDbCases(BitMatrix bitSessionMatrix) {
		
		CaseListMem caseList = createBitDbCaseList(bitSessionMatrix);
		
		return new DataBaseCases("Rating matrix database", caseList);
	}

	
	/**
	 * 
	 * @param bitSessionMatrix
	 * @return {@link CaseListMem}
	 */
	protected static CaseListMem createBitDbCaseList(
			BitMatrix bitSessionMatrix) {
		
		NodeList nodeList = BnetBinaryUtil.createBitItemNodeList(
				bitSessionMatrix.columnIdList);

		Vector<int[]> cases = new Vector<int[]>();
		
		for (byte[] row : bitSessionMatrix.matrix) {
			cases.add(DSUtil.byteToInt(row));
		}
		
		CaseListMem caseList = new CaseListMem(nodeList);
		caseList.setCases(cases);
		
		return caseList;
	}


}
