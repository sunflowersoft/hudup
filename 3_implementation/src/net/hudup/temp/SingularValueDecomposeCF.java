package net.hudup.temp;

import java.awt.Component;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.FilterParam;
import net.hudup.core.alg.KBase;
import net.hudup.core.alg.KBaseAbstract;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.cf.ModelBasedCF;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetAssoc;
import net.hudup.core.data.ItemRating;
import net.hudup.core.data.Pair;
import net.hudup.core.data.RatingMatrix;
import net.hudup.core.data.RatingVector;
import net.hudup.core.data.UserRating;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.data.ui.RatingValueTable;
import net.hudup.sparse.Reducer;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
public class SingularValueDecomposeCF extends ModelBasedCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public static final String REDUCE_RATIO = "reduce_ratio";

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "singular_value_decompose";
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new SingularValueDecomposeCF();
	}

	
	@Override
	public KBase createKB() {
		// TODO Auto-generated method stub
		return SvdKB.create(this);
	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) {
		// TODO Auto-generated method stub
		
		SvdKB kb = (SvdKB) getKBase();
		if (kb.isEmpty())
			return null;
		
		RatingVector result = param.ratingVector.newInstance(true);
		List<Integer> userIds = kb.userIdList();
		
		for (int queryId : queryIds) {
			float accum = 0;
			float simTotal = 0;
			
			for (int userId : userIds) {
				RatingVector that = kb.getUserRating(userId);
				if (that == null || !that.isRated(queryId))
					continue;
				
				// computing similarity array
				float sim = param.ratingVector.corr(that);
				if (!Util.isUsed(sim))
					continue;
				
				float thatValue = that.get(queryId).value;
				float thatMean = that.mean();
				float deviate = thatValue - thatMean;
				accum += sim * deviate;
				simTotal += Math.abs(sim);
			}
			
			float mean = param.ratingVector.mean();
			float value = simTotal == 0 ? mean : mean + accum / simTotal;
			result.put(queryId, value);
		}
		
		
		if (result.size() == 0)
			return null;
		else
			return result;
	}

	
	@Override
	public RatingVector recommend(RecommendParam param, int maxRecommend) {
		// TODO Auto-generated method stub
		SvdKB kb = (SvdKB) getKBase();
		if (kb.isEmpty())
			return null;

		param = preprocess(param);
		if (param == null)
			return null;
		
		filterList.prepare(param);
		List<Integer> fieldIds = kb.itemIdList();
		
		List<Pair> pairs = Util.newList();
		Dataset dataset = getDataset();
		float maxRating = dataset.getConfig().getMaxRating();

		int size = fieldIds.size();
		
		int i = 0;
		for (int fieldId : fieldIds) {
			i++;
			
			if (param.ratingVector.isRated(fieldId))
				continue;
			//
			if(!filterList.filter(dataset, FilterParam.create(fieldId)))
				continue;
			
			Set<Integer> queryIds = Util.newSet();
			queryIds.add(fieldId);
			RatingVector predict = estimate(param, queryIds);
			if (predict == null || !predict.isRated(fieldId))
				continue;
			
			// Finding maximum rating
			float value = predict.get(fieldId).value;
			int found = Pair.findIndexOfLessThan(value, pairs);
			Pair pair = new Pair(fieldId, value);
			if (found == -1)
				pairs.add(pair);
			else 
				pairs.add(found, pair);
			
			int n = pairs.size();
			// Always having maxRecommend + 1
			if (maxRecommend > 0 && n >= maxRecommend) {
				
				int lastIndex = pairs.size() - 1;
				Pair last = pairs.get(lastIndex);
				if (last.value() == maxRating || i >= size)
					break;
				else if (n > maxRecommend)
					pairs.remove(lastIndex);
			}
			
			
		} // end for

		if (maxRecommend > 0 && pairs.size() > maxRecommend)
			pairs.remove(pairs.size() - 1);
		
		if (pairs.size() == 0)
			return null;
		
		RatingVector rec = param.ratingVector.newInstance(true);
		Pair.fillRatingVector(rec, pairs);
		return rec;
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
				if (key.equals(REDUCE_RATIO)) {
					
					float reduceRatio = ((Float)value).floatValue();
					if (reduceRatio < 0 || 1 <= reduceRatio)
						return false;
					else
						return true;
				}
				else
					return super.validate(key, value);
			}
			
		};
		config.put(REDUCE_RATIO, new Float(0.9));
		
		xURI store = xURI.create(Constants.KNOWLEDGE_BASE_DIRECTORY).concat(getName());
		config.setStoreUri(store);
		
		return config;
	}


}




/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
abstract class SvdKB extends KBaseAbstract {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public static final String MATRIX_STORE_NAME = "estimated_matrix";
	
	
	/**
	 * 
	 */
	protected RatingMatrix estimatedMatrix = null;

	
	@Override
	public void load() {
		// TODO Auto-generated method stub
		super.load();
		
		estimatedMatrix = null;
		RatingMatrix ratingMatrix = new RatingMatrix();
		
		xURI store = config.getStoreUri();
		xURI matrixUri = store.concat(MATRIX_STORE_NAME);
		UriAdapter adapter = null;
		Reader matrixReader = null;
		try {
			adapter = new UriAdapter(config);
			if (adapter.exists(matrixUri)) {
				matrixReader = adapter.getReader(matrixUri);
				ratingMatrix.load(matrixReader);
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (matrixReader != null)
					matrixReader.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			if (adapter != null)
				adapter.close();
		}
		
		if (!ratingMatrix.isEmpty())
			this.estimatedMatrix = ratingMatrix;
	}


	@Override
	public void learn(Dataset dataset, Alg alg) {
		super.learn(dataset, alg);
		
		
		estimatedMatrix = null;
		
		DatasetAssoc assoc = new DatasetAssoc(dataset);
		List<Integer> userIds = assoc.getUserRatedIds();
		List<Integer> itemIds = assoc.getItemRatedIds();
		boolean user = userIds.size() >= itemIds.size();
		
		RatingMatrix ratingMatrix = user ? dataset.createUserMatrix() : dataset.createItemMatrix();
		float reduceRatio = getConfig().getAsReal(SingularValueDecomposeCF.REDUCE_RATIO);
		
		ratingMatrix = new Reducer(reduceRatio).svdReduce(ratingMatrix, true);
		if (ratingMatrix == null || ratingMatrix.isEmpty())
			return;
		
		if (!user)
			ratingMatrix.transpose();
		this.estimatedMatrix = ratingMatrix;
	}
	

	@Override
	public void export(DataConfig storeConfig) {
		// TODO Auto-generated method stub
		super.export(storeConfig);
		if (isEmpty())
			return;
		
		UriAdapter adapter = new UriAdapter(storeConfig);
		xURI store = storeConfig.getStoreUri();
		
		Writer matrixWriter = null;
		try {
			xURI matrixUri = store.concat(MATRIX_STORE_NAME);
			matrixWriter = adapter.getWriter(matrixUri, false);
			estimatedMatrix.save(matrixWriter);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (matrixWriter != null)
					matrixWriter.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		adapter.close();
	}


	@Override
	public void close() {
		// TODO Auto-generated method stub
		super.close();
		
		estimatedMatrix = null;
	}


	@Override 
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return estimatedMatrix == null || estimatedMatrix.isEmpty();
	}


	/**
	 * 
	 * @param userId
	 * @param itemId
	 * @return rating value of specified user id and item id
	 */
	public float getRating(int userId, int itemId) {
		return estimatedMatrix.getValue(userId, itemId);
	}
	
	
	/**
	 * 
	 * @return user id list
	 */
	public List<Integer> userIdList() {
		return estimatedMatrix.rowIdList;
	}
	
	
	/**
	 * 
	 * @return item id list
	 */
	public List<Integer> itemIdList() {
		return estimatedMatrix.columnIdList;
	}

	
	/**
	 * 
	 * @param userId
	 * @return index of user id
	 */
	public int indexOfUserId(int userId) {
		return estimatedMatrix.rowIdList.indexOf(userId);
	}
	
	
	/**
	 * 
	 * @param itemId
	 * @return index of item id
	 */
	public int indexOfItemId(int itemId) {
		return estimatedMatrix.columnIdList.indexOf(itemId);
	}

	
	/**
	 * 
	 * @param userId
	 * @return user rating vector
	 */
	public RatingVector getUserRating(int userId) {
		float[] v = estimatedMatrix.getRowVector(userId);
		if (v == null || v.length == 0)
			return null;
		
		List<Integer> itemIdList = itemIdList();
		RatingVector vRating = new UserRating(userId);
		for (int i = 0; i < itemIdList.size(); i++) {
			vRating.put(itemIdList.get(i), v[i]);
		}
		
		return vRating;
	}

	
	/**
	 * 
	 * @param itemId
	 * @return item rating vector
	 */
	public RatingVector getItemRating(int itemId) {
		float[] v = estimatedMatrix.getColumnVector(itemId);
		if (v == null || v.length == 0)
			return null;

		List<Integer> userIdList = userIdList();
		RatingVector vRating = new ItemRating(itemId);
		for (int i = 0; i < userIdList.size(); i++) {
			vRating.put(userIdList.get(i), v[i]);
		}
		
		return vRating;
	}

	
	@Override
	public void view(Component comp) {
		// TODO Auto-generated method stub
		if (isEmpty()) {
			JOptionPane.showMessageDialog(
					comp, 
					"Knowledge base empty", 
					"Knowledge base empty", 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		RatingValueTable tblRatingMatrix = new RatingValueTable();
		tblRatingMatrix.update(estimatedMatrix, null);
		tblRatingMatrix.showDlg(comp, true);
	}


	/**
	 * 
	 * @param cf
	 * @return knowledge base {@link SvdKB}
	 */
	public static SvdKB create(final SingularValueDecomposeCF cf) {
		return new SvdKB() {

			
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
