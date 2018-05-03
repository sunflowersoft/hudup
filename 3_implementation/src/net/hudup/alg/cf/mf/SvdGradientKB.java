package net.hudup.alg.cf.mf;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import javax.swing.JOptionPane;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.KBaseIntegrated;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.PropList;
import net.hudup.core.data.RatingMatrix;
import net.hudup.core.data.RatingMatrixMetadata;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.Vector;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.TextParserUtil;
import net.hudup.data.ui.RatingValueTable;

/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class SvdGradientKB extends KBaseIntegrated {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public final static String PRECISION = "precision";
	
    
	/**
	 * 
	 */
	public final static String GAMMA = "gamma";
	
	
	/**
	 * 
	 */
	public final static String LAMDA = "lamda";
	
	
	/**
	 * 
	 */
	public final static String MAX_ITERATION = "max_iteration";

	
	/**
	 * 
	 */
	public final static String MAX_FACTOR = "max_factor";

	
	/**
	 * 
	 */
	public final static String GRADIENT_FACTORS = "gradient_info";
	
	
	/**
	 * 
	 */
	public final static float DEFAULT_PRECISION = 0.9f; // 90% precision
	
    
	/**
	 * 
	 */
	public final static float DEFAULT_GAMMA = 0.005f;
	
	
	/**
	 * 
	 */
	public final static float DEFAULT_LAMDA = 0.02f;
	
	
	/**
	 * 
	 */
	public final static int DEFAULT_MAX_ITERATION = 100;

	
	/**
	 * 
	 */
	public final static int DEFAULT_MAX_FACTOR = 1000;

	
	/**
	 * 
	 */
	protected List<Integer> userIds = Util.newList();
	
	
	/**
	 * 
	 */
	protected List<Integer> itemIds = Util.newList();

	
	/**
	 * 
	 */
	protected float avgRating = Constants.UNUSED;
	
	
	/**
	 * 
	 */
	protected List<Vector> userFactors = Util.newList();
	
	
	/**
	 * 
	 */
	protected List<Vector> itemFactors = Util.newList();
	
	
	/**
	 * 
	 */
	protected List<Float> userBias = Util.newList();

	
	/**
	 * 
	 */
	protected List<Float> itemBias = Util.newList();
	
	
	/**
	 * 
	 */
	public SvdGradientKB() {
		super();
		// TODO Auto-generated constructor stub
		
	}

	
	@Override
	public float estimate(int userId, int itemId) {
		int userIndex = userIds.indexOf(userId);
		if (userIndex == -1)
			return Constants.UNUSED;
		
		int itemIndex = itemIds.indexOf(itemId);
		if (itemIndex == -1)
			return Constants.UNUSED;
		
		return estimateByIndex(userIndex, itemIndex);
	}
	

	/**
	 * 
	 * @param userIndex specified user index.
	 * @param itemIndex specified item index.
	 * @return estimated rating value.
	 */
	protected float estimateByIndex(int userIndex, int itemIndex) {
		Vector Pu = userFactors.get(userIndex);
		Vector Qi = itemFactors.get(itemIndex);
		float estimatedValue = avgRating + userBias.get(userIndex) + itemBias.get(itemIndex) + Pu.product(Qi);
		estimatedValue = Math.min(estimatedValue, config.getMaxRating());
		estimatedValue = Math.max(estimatedValue, config.getMinRating());
		
		return estimatedValue;
	}
	
	
	/**
	 * 
	 * @return list of user id (s)
	 */
	public List<Integer> getUserIds() {
		return userIds;
	}
	

	/**
	 * 
	 * @return list of item id (s)
	 */
	public List<Integer> getItemIds() {
		return itemIds;
	}
	
	
	@Override
	public void learn0(Dataset dataset, Alg alg) {
		destroyDataStructure();
		
		RatingMatrix userMatrix = dataset.createUserMatrix();
		if (learn_initialize(userMatrix))
			learn_main(userMatrix);
		else
			destroyDataStructure();
		
		userMatrix.clear();
	}
	
	
	@Override
	public void learn0(RatingMatrix userRatingMatrix) {
		config.setMetadata(userRatingMatrix.metadata.to());
		config.put(KBASE_NAME, getName());
		
		config.addReadOnly(DataConfig.MIN_RATING_FIELD);
		config.addReadOnly(DataConfig.MAX_RATING_FIELD);
		config.addReadOnly(KBASE_NAME);
		
		datasource.close();
		
		destroyDataStructure();
		
		if (learn_initialize(userRatingMatrix))
			learn_main(userRatingMatrix);
		else
			destroyDataStructure();
	}
	
	
	/**
	 * 
	 * @param userMatrix specified user rating matrix.
	 * @return whether initialization successfully.
	 */
	protected boolean learn_initialize(RatingMatrix userMatrix) {
		
		float[][] ratingMatrix = userMatrix.matrix;
		int nUsers = userMatrix.rowIdList.size();
		int nItems = userMatrix.columnIdList.size();
		if (nUsers == 0 || nItems == 0)
			return false;
		
		int factor = Math.min(nUsers, nItems);
		int maxFactor = getConfig().getAsInt(MAX_FACTOR);
		if (maxFactor > 0)
			factor = Math.min(factor, maxFactor);
		if (factor == 0)
			return false;
		
		
		// Initializing average rating
		float sumRating = 0;
		int countRating = 0;
		for (int u = 0; u < nUsers; u++) {
			for (int i = 0; i < nItems; i++) {
				float value = ratingMatrix[u][i];
				if (Util.isUsed(value)) {
					sumRating += value;
					countRating ++;
				}
			}
		}
		if (countRating == 0)
			return false;
		
		userIds.clear();
		userIds.addAll(userMatrix.rowIdList);
		itemIds.clear();
		itemIds.addAll(userMatrix.columnIdList);
		avgRating = sumRating / (float) countRating;
		
		// Initializing factors
		float coeff = 1.0f / (float) (nUsers * nItems);
		coeff = (float) ((double) coeff * coeff);
		coeff = (float) ((double) coeff * coeff);
		coeff = 0;
		userFactors.clear();
		for (int u = 0; u < nUsers; u++) {
			Vector userFactor = new Vector(factor, 0);
			for (int f = 0; f < factor; f++)
				userFactor.set(f, (f + 1) * coeff);
			
			userFactors.add(userFactor);
		}
		//
		itemFactors.clear();
		for (int i = 0; i < nItems; i++) {
			Vector itemFactor = new Vector(factor, 0);
			for (int f = 0; f < factor; f++)
				itemFactor.set(f, (f + 1) * coeff);
			
			itemFactors.add(itemFactor);
		}
		
		
		// Initializing bias
		itemBias.clear();
		for (int i = 0; i < nItems; i++) {
			
			int countRatedItem = 0;
			float sumOfErrors = 0;
			for (int u = 0; u < nUsers; u++) {
				float value = ratingMatrix[u][i];
				if (Util.isUsed(value)) {
					countRatedItem ++;
					sumOfErrors += value - avgRating;
				}
			}
			
			float bias = sumOfErrors / (avgRating/(float)nItems + countRatedItem);
			itemBias.add(bias);
		}
		
		userBias.clear();
		for (int u = 0; u < nUsers; u++) {
			
			int countRatedUser = 0;
			float sumOfErrors = 0;
			for (int i = 0; i < nItems; i++) {
				float value = ratingMatrix[u][i];
				if (Util.isUsed(value)) {
					countRatedUser ++;
					sumOfErrors += value - avgRating - itemBias.get(i);
				}
			}
			
			float bias = sumOfErrors / (avgRating/(float)nUsers + countRatedUser);
			userBias.add(bias); 
		}
		
		
		return true;
	}
	
	
	/**
	 * 
	 * @param userMatrix specified user rating matrix, represented by {@link RatingMatrix} class.
	 */
	protected void learn_main(RatingMatrix userMatrix) {
		float precision = getConfig().getAsReal(PRECISION);
		if (precision == 0)
			precision = 1;
		precision = Math.min(precision, 1.0f);
		
		float minRating = getConfig().getMetadata().minRating;
		float maxRating = getConfig().getMetadata().maxRating;
		float epsilon = (float) Math.pow( (maxRating - minRating), 2) * (1.0f - precision);
		float gamma = getConfig().getAsReal(GAMMA);
		float lamda = getConfig().getAsReal(LAMDA);
		
		int maxIteration = getConfig().getAsInt(MAX_ITERATION);
		maxIteration = maxIteration == 0 ? Integer.MAX_VALUE : maxIteration;
		
        float sumOfSquare = 0;
		int iteration = 0;
		int nUsers = userIds.size();
		int nItems = itemIds.size();
		float[][] ratingMatrix = userMatrix.matrix;
		
		// Gradient descent
		while (iteration < maxIteration) {

			for (int u = 0; u < nUsers; u++) {
				for (int i = 0; i < nItems; i++) {
					float value = ratingMatrix[u][i];
					if (!Util.isUsed(value))
						continue;
					
					float bu = userBias.get(u);
					float bi = itemBias.get(i);
					Vector Pu = userFactors.get(u);
					Vector Qi = itemFactors.get(i);
					
					float predictedValue = avgRating + userBias.get(u) + itemBias.get(i) + Pu.product(Qi);
					float error = value - predictedValue;
					
                    bu = bu + gamma * (error - lamda * bu);
                    bi = bi + gamma * (error - lamda * bi);
                    Qi = Qi.add( Pu.multiply(error).subtract(Qi.multiply(lamda)).multiply(gamma) );
                    Pu = Pu.add( Qi.multiply(error).subtract(Pu.multiply(lamda)).multiply(gamma) );
                    		
                    userBias.set(u, bu);
                    itemBias.set(i, bi);
                    userFactors.set(u, Pu);
                    itemFactors.set(i, Qi);
				}
			}
			
			
            float newSumOfSquare = 0;
            for (int u = 0; u < nUsers; u++) {
            	
                for (int i = 0; i < nItems; i++) {
					float value = ratingMatrix[u][i];
					if (!Util.isUsed(value))
						continue;
					
					Vector Pu = userFactors.get(u);
					Vector Qi = itemFactors.get(i);
					float predictedValue = avgRating + userBias.get(u) + itemBias.get(i) + Pu.product(Qi);
					float error = value - predictedValue;
					
					newSumOfSquare +=
                            Math.pow(error, 2) + 
                            lamda * (
                            	Math.pow(userBias.get(u), 2) + 
                            	Math.pow(itemBias.get(i), 2) + 
                            	Math.pow(userFactors.get(u).module(), 2) + 
                            	Math.pow(itemFactors.get(i).module(), 2)
                            );
                }
            }
			
            if (Math.abs(newSumOfSquare - sumOfSquare) < epsilon)
            	break;
            
            sumOfSquare = newSumOfSquare;
			iteration ++;
		}
		
	}
	
	
	@Override
	public void load0() {
		destroyDataStructure();
		
		xURI store = config.getStoreUri();
		BufferedReader buffer = null;
		UriAdapter adapter = null;
		try {
			xURI gradientFactorsUri = store.concat(GRADIENT_FACTORS);
			adapter = new UriAdapter(config);
			if (adapter.exists(gradientFactorsUri)) {
				Reader gradientFactorsReader = adapter.getReader(gradientFactorsUri);
				buffer = new BufferedReader(gradientFactorsReader);

				userIds = TextParserUtil.parseListByClass(buffer.readLine(), Integer.class, ",");
				itemIds = TextParserUtil.parseListByClass(buffer.readLine(), Integer.class, ",");
				avgRating = Float.parseFloat(buffer.readLine());
				userBias = TextParserUtil.parseListByClass(buffer.readLine(), Float.class, ",");
				itemBias = TextParserUtil.parseListByClass(buffer.readLine(), Float.class, ",");
				
				int userFactorSize = Integer.parseInt(buffer.readLine());
				for (int i = 0; i < userFactorSize; i++) {
					Vector userFactor = new Vector(new float[0]);
					userFactor.parseText(buffer.readLine());
					
					userFactors.add(userFactor);
				}
				
				int itemFactorSize = Integer.parseInt(buffer.readLine());
				for (int i = 0; i < itemFactorSize; i++) {
					Vector itemFactor = new Vector(new float[0]);
					itemFactor.parseText(buffer.readLine());
					
					itemFactors.add(itemFactor);
				}

			}
		}
		catch (Throwable e) {
			e.printStackTrace();
			destroyDataStructure();
		}
		finally {
			try {
				if (buffer != null)
					buffer.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
			
			adapter.close();
		}
		
		
	}
	
	
	@Override
	public void export0(DataConfig storeConfig) {
		if (isEmpty())
			return;
		
		UriAdapter adapter = null;
		PrintWriter printer = null;
		try {
			xURI store = storeConfig.getStoreUri();
			adapter = new UriAdapter(storeConfig);
			
			xURI gradientFactorsUri = store.concat(GRADIENT_FACTORS);
			Writer gradientFactorsWriter = adapter.getWriter(gradientFactorsUri, false);
			printer = new PrintWriter(gradientFactorsWriter);
			
			printer.println(TextParserUtil.toText(userIds, ","));
			printer.println(TextParserUtil.toText(itemIds, ","));
			printer.println(avgRating);
			printer.println(TextParserUtil.toText(userBias, ","));
			printer.println(TextParserUtil.toText(itemBias, ","));
			
			printer.println(userFactors.size());
			for (Vector userFactor : userFactors)
				printer.println(userFactor.toText());
			
			printer.println(itemFactors.size());
			for (Vector itemFactor : itemFactors)
				printer.println(itemFactor.toText());

		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (printer != null)
					printer.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
			
			if (adapter != null)
				adapter.close();
		}
	}
	
	
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		
		return !Util.isUsed(avgRating);
	}

	
	@Override
	protected void destroyDataStructure() {
		userIds.clear();
		itemIds.clear();
		
		avgRating = Constants.UNUSED;
		userFactors.clear();
		itemFactors.clear();
		userBias.clear();
		itemBias.clear();
	}
	
	
	@Override
	public void view(Component comp) {
		// TODO Auto-generated method stub
		
		RatingMatrix ratingMatrix = createUserRatingMatrix();
		if (ratingMatrix == null) {
			JOptionPane.showMessageDialog(
					comp, 
					"Knowledge base empty", 
					"Knowledge base empty", 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		RatingValueTable tblRatingMatrix = new RatingValueTable();
		tblRatingMatrix.update(ratingMatrix, null);
		tblRatingMatrix.showDlg(comp, true);
	}

	
	/**
	 * 
	 * @return user {@link RatingMatrix}
	 */
	public RatingMatrix createUserRatingMatrix() {
		if (isEmpty())
			return null;
		
		int nUsers = userIds.size();
		int nItems = itemIds.size();
		float[][] newMatrix = new float[nUsers][nItems];
		for (int u = 0; u < nUsers; u++) {
			for (int i = 0; i < nItems; i++) {
				newMatrix[u][i] = estimateByIndex(u, i);
			}
		}
		
		
		RatingMatrixMetadata metadata = RatingMatrixMetadata.from(config.getMetadata(), true);
		metadata.numberOfUsers = nUsers;
		metadata.numberOfUserRatings = nUsers;
		metadata.numberOfItems = nItems;
		metadata.numberOfItemRatings = nItems;
		
		List<Integer> userIdList = Util.newList();
		userIdList.addAll(userIds);
		List<Integer> itemIdList = Util.newList();
		itemIdList.addAll(itemIds);

		return RatingMatrix.assign(
				newMatrix, 
				userIdList, 
				itemIdList, 
				metadata);
	}
	
	
	@Override
	public PropList getDefaultParameters() {
		DataConfig config = new DataConfig();
		config.put(PRECISION, new Float(DEFAULT_PRECISION));
		config.put(GAMMA, new Float(DEFAULT_GAMMA));
		config.put(LAMDA, new Float(DEFAULT_LAMDA));
		config.put(MAX_ITERATION, DEFAULT_MAX_ITERATION);
		config.put(MAX_FACTOR, DEFAULT_MAX_FACTOR);
		
		return config;
	}

	
	/**
	 * 
	 * @param cf specified SVD Gradient Collaborative Filtering (SVD Gradient CF) algorithm.
	 * @return {@link SvdGradientKB}
	 */
	public static SvdGradientKB create(final SvdGradientCF cf) {
		
		return new SvdGradientKB() {

			
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
