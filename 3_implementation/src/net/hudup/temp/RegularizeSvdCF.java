package net.hudup.temp;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.cf.MemoryBasedCF;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.RatingMatrix;
import net.hudup.core.data.RatingMatrixMetadata;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.Vector;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
public class RegularizeSvdCF extends MemoryBasedCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public static final String SHRINKAGE = "shrinkage";

	
	/**
	 * 
	 */
	public static final String LEARNING_RATE = "learning_rate";

	
	/**
	 * 
	 */
	public static final String QUADRATIC_THRESHOLD = "quadractic_threshold";

	
	/**
	 * 
	 */
	public RegularizeSvdCF() {
		super();
	}
	
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "svd_regularize";
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new RegularizeSvdCF();
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = new DataConfig() {

			
			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			
			@Override
			public boolean validate(String key, Serializable value) {
				// TODO Auto-generated method stub
				if (key.equals(SHRINKAGE) || key.equals(LEARNING_RATE) || key.equals(QUADRATIC_THRESHOLD)) {
					return ((Number)value).floatValue() > 0;
				}
				else
					return super.validate(key, value);
			}

			
		};
		
		config.put(SHRINKAGE, 1f);
		config.put(LEARNING_RATE, 2f);
		config.put(QUADRATIC_THRESHOLD, 0.001f);
		
		return config;
	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) {
		// TODO Auto-generated method stub
		
		float beta = config.getAsReal(SHRINKAGE);
		float lamda = config.getAsReal(LEARNING_RATE);
		float epsilon = config.getAsReal(QUADRATIC_THRESHOLD);
		int userId = param.ratingVector.id();

		RatingVector result = param.ratingVector.newInstance(true);
		for (int queryId : queryIds) {
			RatingVector thisItem = dataset.getItemRating(queryId);
			if (thisItem == null || thisItem.count(true) < 2)
				continue;

			RatingMatrix A = getItemCovarianceMatrix(queryId, userId, beta);
			if (A == null)
				continue;
			
			Vector b = new Vector(A.matrix.length, lamda);  
			Vector solution = solveQuadractic(A, b, epsilon);
			if (solution == null)
				continue;
			
			float accum = 0;
			float simTotal = 0;
			for (int i = 0; i < solution.dim(); i++) {
				int thatItemId = A.columnIdList.get(i);
				float sim = solution.get(i);
				
				RatingVector thatItem = dataset.getItemRating(thatItemId);
				
				
				accum += sim * thatItem.get(userId).value;
				simTotal += sim;
			}
			result.put(queryId, accum / simTotal);
		}
		
		if (result.size() == 0)
			return null;
		else
			return result;
	}

	
	/**
	 * 
	 * @param refItemId
	 * @param userIdRatedOn
	 * @param beta
	 * @return {@link RatingMatrix} that contains the rating covariance between a pair of items with subject to other item  
	 */
	public RatingMatrix getItemCovarianceMatrix(int refItemId, int userIdRatedOn, float beta) {
		RatingVector vRefItemRating = dataset.getItemRating(refItemId);
		if (vRefItemRating == null)
			return null;
		
		Set<Integer> ratingUserIds = vRefItemRating.fieldIds(true);
		if (ratingUserIds.size() == 0)
			return null;
		
		Fetcher<Integer> itemIds = dataset.fetchItemIds();
		List<Integer> estiItemIds = Util.newList();
		try {
			while (itemIds.next()) {
				Integer itemId = itemIds.pick();
				if (itemId == null)
					continue;
				
				RatingVector vItemRating = dataset.getItemRating(itemId);
				if (vItemRating == null)
					continue;
				Set<Integer> userIds = vItemRating.fieldIds(true);
				if (!userIds.contains(userIdRatedOn))
					continue;
				userIds.retainAll(ratingUserIds); 
				if (userIds.size() == 0)
					continue;
				
				boolean intersect = true;
				for (int estiItemId : estiItemIds) {
					RatingVector vEstiRating = dataset.getItemRating(estiItemId);
					Set<Integer> estiUserIds = vEstiRating.fieldIds(true);
					estiUserIds.retainAll(userIds);
					if (estiUserIds.size() == 0) {
						intersect = false;
						break;
					}
				}
				if (!intersect)
					continue;
				
				estiItemIds.add(itemId);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				itemIds.close();
			}
			catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		
		if (estiItemIds.size() < 2)
			return null;
		
		List<Integer> userIdList = Util.newList();
		userIdList.addAll(estiItemIds);
		List<Integer> itemIdList = Util.newList();
		itemIdList.addAll(estiItemIds);
		float[][] A = new float[estiItemIds.size()][estiItemIds.size()];
		float[][] B = new float[estiItemIds.size()][estiItemIds.size()];
		
		double sum = 0;
		int n = estiItemIds.size();
		for (int i = 0; i < n - 1; i++) {
			RatingVector vItemRatingI = dataset.getItemRating(estiItemIds.get(i));
			Set<Integer> userIdsRateItemI = vItemRatingI.fieldIds(true);
			
			for (int j = i; j < n; j++) {
				RatingVector vItemRatingJ = dataset.getItemRating(estiItemIds.get(j));
				Set<Integer> userIdsRateItemJ = vItemRatingJ.fieldIds(true);
				
				Set<Integer> commonUserIds = Util.newSet();
				commonUserIds.addAll(ratingUserIds);
				commonUserIds.retainAll(userIdsRateItemI);
				commonUserIds.retainAll(userIdsRateItemJ);
				
				double a = 0;
				double b = 0;
				for (int userId : commonUserIds) {
					float ratingI = vItemRatingI.get(userId).value;
					float ratingJ = vItemRatingJ.get(userId).value;
					float ratingK = vRefItemRating.get(userId).value;
					
					a += (ratingI - ratingK) * (ratingJ - ratingK);
					b += 1;
				}
				A[i][j] = (float) (a / b);
				A[j][i] = A[i][j];
				if (i == j)
					sum += A[i][j];
				else
					sum += 2 * A[i][j];
				
				B[i][j] = (float) b;
				B[j][i] = B[i][j];
			}
			
		}

		
		float[][] matrix = new float[n][n];
		float avg = (float) (sum / (n *n));
		
		for (int i = 0; i < estiItemIds.size() - 1; i++) {
			
			for (int j = i + 1; j < estiItemIds.size(); j++) {
				matrix[i][j] = (B[i][j] * A[i][j] + beta * avg) / (B[i][j] + beta);
				matrix[j][i] = matrix[i][j];
			}
		}
		
		return RatingMatrix.assign(
				matrix,
				userIdList, 
				itemIdList,
				RatingMatrixMetadata.from(dataset.getConfig().getMetadata(), false) );
	}

	
	/**
	 * 
	 * @param matrix
	 * @param b
	 * @param epsilon
	 * @return the root vector of quadractic equation 
	 */
	public static Vector solveQuadractic(RatingMatrix matrix, Vector b, double epsilon) {
		int n = matrix.matrix.length;
		if (n == 0 || n != b.dim() || n != matrix.matrix[0].length)
			return null;
		
		Vector r = new Vector(b.dim(), 0);
		Vector x = new Vector(b.dim(), 1);
		do {
			r = matrix.apply(x).subtract(b);
			for (int i = 0; i < n; i++) {
				if (x.get(i) == 0 && r.get(i) < 0) {
					r.set(i, 0);
				}
			}
			
			float rproduct = r.product(r); 
			float alpha = rproduct == 0 ? 0 : rproduct / (r.product(matrix.apply(r)));
			for (int i = 0; i < n; i++) {
				if (r.get(i) < 0)
					alpha = Math.min(alpha, -x.get(i) / r.get(i));
			}
			
			x = x.add(x.multiply(alpha));
		}
		while (r.module() < epsilon);
		
		
		return x;
	}
	
	
	
}


