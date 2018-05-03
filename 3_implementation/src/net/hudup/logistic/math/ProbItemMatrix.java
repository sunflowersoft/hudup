package net.hudup.logistic.math;

import java.util.List;
import java.util.Map;

import net.hudup.core.Util;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.FetcherUtil;
import net.hudup.core.data.Pair;
import net.hudup.data.DatasetUtil2;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class ProbItemMatrix {
	
	/**
	 * 
	 */
	protected int numberValuesPerItem = 0;
	
	
	/**
	 * 
	 */
	protected Map<Integer, Map<Integer, float[]>> matrix = Util.newMap();
	
	
	/**
	 * 
	 * @param rowId
	 * @param colId
	 * @param probs
	 */
	public void set(int rowId, int colId, float[] probs) {
		Map<Integer, float[]> row = null;
		if (matrix.containsKey(rowId))
			row = matrix.get(rowId);
		else {
			row = Util.newMap();
			matrix.put(rowId, row);
		}
		row.put(colId, probs);
		
		if (rowId != colId) {
			float[] newprobs = new float[probs.length];
			
			for (int i = 0; i < probs.length; i++) {
				int r = i / numberValuesPerItem;
				int c = i % numberValuesPerItem;
				newprobs[c * numberValuesPerItem + r] = probs[i];
			}
			
			Map<Integer, float[]> col = null;
			if (matrix.containsKey(colId))
				col = matrix.get(colId);
			else {
				col = Util.newMap();
				matrix.put(colId, col);
			}
			col.put(rowId, newprobs);
		}
		
	}
	
	
	/**
	 * 
	 * @param rowId
	 * @param colId
	 * @return probability
	 */
	public float[] get(int rowId, int colId) {
		return matrix.get(rowId).get(colId);
	}

	
	/**
	 * 
	 * @param id
	 * @return array of probability
	 */
	public float[] get(int id) {
		return matrix.get(id).get(id);
	}
	
	
	/**
	 * 
	 * @param rowId
	 * @param colId
	 * @return whether contains row id and column id
	 */
	public boolean contains(int rowId, int colId) {
		if (!matrix.containsKey(rowId))
			return false;
		
		Map<Integer, float[]> row = matrix.get(rowId);
		return row.containsKey(colId);
		
	}
	
	
	
	/**
	 * 
	 * @param dataset
	 */
	public void setup(DatasetStatsProcessor processor, Dataset dataset) {
		this.matrix.clear();
		
		List<Integer> itemIdList = Util.newList();
		FetcherUtil.fillCollection(itemIdList, dataset.fetchItemIds(), true);
		
		this.numberValuesPerItem = dataset.getConfig().getNumberRatingsPerItem();
		int minRating = (int)dataset.getConfig().getMinRating();
		
		for (int u = 0; u < itemIdList.size(); u++) {
			int id1 = itemIdList.get(u);
			
			float[] probs0 = new float[numberValuesPerItem];
			for (int i = 0; i < numberValuesPerItem; i++) {
				probs0[i] = processor.probItem(id1, 
						DatasetUtil2.realRatingValueOf(i, minRating));
			}
			set(id1, id1, probs0);
			
			for (int v = u + 1; v < itemIdList.size(); v++) {
				int id2 = itemIdList.get(v);
				
				float[] probs = new float[numberValuesPerItem * numberValuesPerItem];
				for (int i = 0; i < numberValuesPerItem; i++) {
					Pair pair1 = new Pair(id1, 
							DatasetUtil2.realRatingValueOf(i, minRating));
					
					for (int j = 0; j < numberValuesPerItem; j++) {
						Pair pair2 = new Pair(id2, 
								DatasetUtil2.realRatingValueOf(j, minRating));
						
						List<Pair> pairs = Util.newList();
						pairs.add(pair1);
						pairs.add(pair2);
						
						probs[i * numberValuesPerItem + j] = processor.probItem(pairs);
					}
				}
				
				set(id1, id2, probs);
				
			} // End v
			
		} // End u
		
	}

	
	/**
	 * 
	 * @return the number of values per item
	 */
	public int getNumberValuesPerItem() {
		return numberValuesPerItem;
	}
	
	
	
}
