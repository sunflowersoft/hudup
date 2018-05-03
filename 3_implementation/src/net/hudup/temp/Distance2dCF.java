package net.hudup.temp;

import java.awt.Point;
import java.util.List;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.cf.MemoryBasedCF;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.RatingVector;
import net.hudup.data.UserPaddingMatrix;

/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 * 
 * 
 */
@Deprecated
public class Distance2dCF extends MemoryBasedCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "distance2d";
	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) {
		// TODO Auto-generated method stub

		UserPaddingMatrix matrix = new UserPaddingMatrix(dataset, param.ratingVector);
		
		int userId = param.ratingVector.id();
		float[] user = matrix.getUserRatingVector(userId);
		if (user == null)
			return null;
		matrix.sortRows(user);
		
		List<Integer> userIdList = matrix.getUserIdList();
		List<Integer> itemIdList = matrix.getItemIdList();
		RatingVector result = param.ratingVector.newInstance(true);
		for (int queryId : queryIds) {
			float[] item = matrix.getItemRatingVector(queryId);
			if (item == null)
				continue;
			
			matrix.sortColumns(item);
			
			Point point = matrix.getRowCol(userId, queryId);
			int cur_row = point.y;
			int cur_col = point.x;
			float maxDis = Math.max(cur_row, userIdList.size() - 1 - cur_row) + 
					Math.max(cur_col, itemIdList.size() - 1 - cur_col);
			
			float sum = 0;
			float sumDis = 0;
			for (int row = 0; row < userIdList.size(); row++) {
				
				for (int col = 0; col < itemIdList.size(); col++) {
					float value = matrix.getRatingByIndex(row, col);
					if (!Util.isUsed(value))
						continue;
					
					float dis = (float)(Math.abs(row - cur_row) + Math.abs(col - cur_col)) / maxDis;
					sum += (1 - dis) * value;
					sumDis += (1 - dis);
				}
				
			}
			if (sumDis > 0)
				result.put(queryId, sum / sumDis);
		}
		
		if (result.size() == 0)
			return null;
		
		return result;
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		return new DataConfig();
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new Distance2dCF();
	}


	
	
	
	
}
