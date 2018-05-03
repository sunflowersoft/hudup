/**
 * 
 */
package net.hudup.core.data;

import java.rmi.RemoteException;
import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.logistic.DSUtil;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public final class DatasetAssoc {

	/**
	 * 
	 */
	protected Dataset dataset = null;
	
	
	/**
	 * Constructor this object with {@link Dataset}
	 * @param dataset is {@link Dataset}
	 */
	public DatasetAssoc(Dataset dataset) {
		this.dataset = dataset;
	}
	
	
	/**
	 * 
	 * @return rating user id (s)
	 */
	public List<Integer> getUserRatedIds() {
		return getRatedIds(true);
	}
	
	
	/**
	 * 
	 * @return rated item id (s)
	 */
	public List<Integer> getItemRatedIds() {
		return getRatedIds(false);
	}

	
	/**
	 * 
	 * @param user
	 * @return list of non-empty id (s)
	 */
	private List<Integer> getRatedIds(boolean user) {
		List<Integer> ids = Util.newList();
		Fetcher<Integer> fetcher = user ? dataset.fetchUserIds() : dataset.fetchItemIds();
		
		try {
			while (fetcher.next()) {
				Integer id = fetcher.pick();
				if (id == null)
					continue;
				
				RatingVector vRating = user ? dataset.getUserRating(id) : dataset.getItemRating(id);
				if (vRating != null && vRating.fieldIds(true).size() > 0)
					ids.add(id);
				
			}
			
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				fetcher.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
	
		return ids;
	}
	
	
	/**
	 * 
	 * @param user user {@link RatingMatrix} or item {@link RatingMatrix} 
	 * @return {@link RatingMatrix}
	 */
	public RatingMatrix createMatrix(boolean user) {
		
		List<Integer> rowIdList = Util.newList();
		List<Integer> columnIdList = Util.newList();
		
		if (user)
			columnIdList = getItemRatedIds();
		else
			columnIdList = getUserRatedIds();
		if (columnIdList.size() == 0)
			return null;
		
		Fetcher<RatingVector> ratings = user ? dataset.fetchUserRatings() : dataset.fetchItemRatings();
		
		float[][] matrix = null;
		try {
			int size = ratings.getMetadata().getSize();
			matrix = new float[size][];
			int i = 0;
			while (ratings.next()) {
				RatingVector rating = ratings.pick();
				if (rating == null || rating.fieldIds(true).size() == 0)
					continue;
				
				List<Float> values = rating.toValueList(columnIdList);
				float[] row = DSUtil.toFloatArray(values);
				
				matrix[i] = row;
				
				rowIdList.add(rating.id());
				
				i++;
			}
			ratings.close();
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		
		if (rowIdList.size() == 0)
			return null;
		else
			return RatingMatrix.assign(
				matrix, 
				rowIdList, 
				columnIdList,
				RatingMatrixMetadata.from(dataset.getConfig().getMetadata(), user) );
	}

	
}
