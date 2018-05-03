package net.hudup.logistic.inference;

import java.util.List;
import java.util.Vector;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.RatingMatrix;
import net.hudup.core.data.Scanner;
import net.hudup.core.data.Snapshot;
import net.hudup.data.DatasetUtil2;
import net.hudup.logistic.mining.Cluster;
import net.hudup.logistic.mining.Clusterer;
import net.hudup.sparse.Reducer;
import net.hudup.sparse.SparseProcessor;
import elvira.Bnet;
import elvira.CaseListMem;
import elvira.NodeList;
import elvira.database.DataBaseCases;
import elvira.learning.DELearning;
import elvira.learning.K2Learning;
import elvira.learning.K2Metrics;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class BnetLearner {
	
	/**
	 * 
	 * @param dataset
	 * @param maxParents
	 * @param bnetNodeNumber
	 * @param dimReduceRatio
	 * @param completeMethod
	 * @return list of {@link Bnet}
	 */
	public static List<Bnet> learning_clustered(Dataset dataset, int maxParents, int bnetNodeNumber, float dimReduceRatio, Alg completeMethod) {
		List<Bnet> bnetList = Util.newList();
		
		List<DataBaseCases> dbcList = loadClusteredDbCases(dataset, bnetNodeNumber, dimReduceRatio, completeMethod);
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
	 * @param dataset
	 * @param maxParents
	 * @param dimReduceRatio
	 * @param completeMethod
	 * @return list of {@link Bnet}
	 */
	public static List<Bnet> learning(Dataset dataset, int maxParents, float dimReduceRatio, Alg completeMethod) {
		Snapshot snapshot = null;
		if (dataset instanceof Snapshot)
			snapshot = (Snapshot) dataset.clone();
		else 
			snapshot = (Snapshot) ((Scanner)dataset).catchup();
			
		new SparseProcessor().algComplete(snapshot, completeMethod);
		
		List<DataBaseCases> dbcList = BnetLearner.loadDbCases(snapshot, dimReduceRatio);

		snapshot.clear();
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
	 * @param dataset
	 * @param reduceRatio
	 * @return list of {@link DataBaseCases}
	 */
	public static List<DataBaseCases> loadDbCases(Dataset dataset, float reduceRatio) {
		List<DataBaseCases> dbcList = Util.newList();
		RatingMatrix userMatrix = dataset.createUserMatrix();
		userMatrix = new Reducer(reduceRatio).simplyReduce(userMatrix, false);
		
		DataBaseCases dbc = BnetLearner.createDbCases(				
				userMatrix, 
				dataset.getConfig().getNumberRatingsPerItem(), 
				(int)dataset.getConfig().getMinRating());
		dbcList.add(dbc);
		
		return dbcList;
	}


	
	/**
	 * 
	 * @param dataset
	 * @param bnetNodeNumber
	 * @param reduceRatio
	 * @param completeMethod
	 * @return list of {@link DataBaseCases}
	 */
	public static List<DataBaseCases> loadClusteredDbCases(Dataset dataset, int bnetNodeNumber, float reduceRatio, Alg completeMethod) {
		List<DataBaseCases> dbcList = Util.newList();
		
		RatingMatrix itemMatrix = dataset.createItemMatrix();
		itemMatrix = new Reducer(reduceRatio).simplyReduce(itemMatrix, false);
		if (itemMatrix.rowIdList.size() == 0)
			return dbcList;
		
		Clusterer clusterer = new Clusterer(itemMatrix.matrix, itemMatrix.rowIdList);
		clusterer.buildClustersByBnetNodeNumber(bnetNodeNumber);
		List<Cluster> clusters = clusterer.getClusters();
		
		RatingMatrix userMatrix = dataset.createUserMatrix();
		for (int i = 0; i < clusters.size(); i++) {
			Cluster cluster = clusters.get(i);
			
			List<Integer> subItemIdList = cluster.getIdList();
			RatingMatrix subUserMatrix = userMatrix.createSub(subItemIdList, false);
			subUserMatrix = new Reducer(reduceRatio).simplyReduce(subUserMatrix, false);
			
			if (subUserMatrix.matrix.length < 2 || subItemIdList.size() < 2)
				continue;
			
			SparseProcessor sparser = new SparseProcessor();
			if (completeMethod == null)
				sparser.columnMeanComplete(subUserMatrix.matrix);
			else
				sparser.algComplete(subUserMatrix, completeMethod, false);
			
			DataBaseCases dbc = createDbCases(				
					subUserMatrix, 
					dataset.getConfig().getNumberRatingsPerItem(), 
					(int)dataset.getConfig().getMinRating());
			
			dbcList.add(dbc);
			
		}
		
		return dbcList;
	}

	
	
	/**
	 * 
	 * @param userMatrix
	 * @param numberRatingsPerItem
	 * @param minRating
	 * @return {@link DataBaseCases}
	 */
	public static DataBaseCases createDbCases(
			RatingMatrix userMatrix,
			int numberRatingsPerItem,
			int minRating) {
		
		CaseListMem caseList = createDbCaseList(
				userMatrix, 
				numberRatingsPerItem, 
				minRating);
		
		return new DataBaseCases("Rating matrix database", caseList);
	}
	
	
	/**
	 * 
	 * @param userMatrix
	 * @param numberRatingsPerItem
	 * @param minRating
	 * @return {@link CaseListMem}
	 */
	protected static CaseListMem createDbCaseList(
			RatingMatrix userMatrix,
			int numberRatingsPerItem,
			int minRating) {
		
		NodeList nodeList = BnetUtil.createItemNodeList(
				userMatrix.columnIdList, 
				numberRatingsPerItem, 
				minRating);

		Vector<int[]> cases = new Vector<int[]>();
		
		for (float[] row : userMatrix.matrix) {
			cases.add(DatasetUtil2.zeroBasedRatingValueOf(row, minRating));
		}
		
		CaseListMem caseList = new CaseListMem(nodeList);
		caseList.setCases(cases);
		
		return caseList;
	}
	
	
	

	

	
}


