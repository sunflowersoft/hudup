package net.hudup.alg.cf.mf;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.PropList;
import net.hudup.core.data.RatingMatrix;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.Vector;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.TextParserUtil;

/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class SvdGradientPlusKB extends SvdGradientKB {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public final static String LAMDA2 = "lamda2";

	
	/**
	 * 
	 */
	public final static String GRADIENT_FACTORS_EXT = "gradient_info_ext";

	
	/**
	 * 
	 */
	public final static float DEFAULT_GAMMA = 0.007f;
	
	
	/**
	 * 
	 */
	public final static float DEFAULT_LAMDA = 0.005f;

	
	/**
	 * 
	 */
	public final static float DEFAULT_LAMDA2 = 0.015f;

	
	/**
	 * 
	 */
	protected Map<Integer, List<Integer>> userRatedIndexes = Util.newMap();
	
	
	/**
	 * 
	 */
	protected List<Vector> itemImplicitFactors = Util.newList();

	
	/**
	 * 
	 */
	public SvdGradientPlusKB() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	protected float estimateByIndex(int userIndex, int itemIndex) {
		// TODO Auto-generated method stub
		Vector Pu = userFactors.get(userIndex);
		Vector Qi = itemFactors.get(itemIndex);
		Vector Ru = userImplicitRatedVector(userIndex);
		float estimatedValue = avgRating + userBias.get(userIndex) + itemBias.get(itemIndex) + Qi.product(Pu.add(Ru));
		estimatedValue = Math.min(estimatedValue, config.getMaxRating());
		estimatedValue = Math.max(estimatedValue, config.getMinRating());
		
		return estimatedValue;
	}


	@Override
	protected boolean learn_initialize(RatingMatrix userMatrix) {
		// TODO Auto-generated method stub
		if (!super.learn_initialize(userMatrix))
			return false;
		
		float[][] ratingMatrix = userMatrix.matrix;
		int nUsers = userMatrix.rowIdList.size();
		int nItems = userMatrix.columnIdList.size();
		int factor = userFactors.get(0).dim();
		
		userRatedIndexes.clear();
		for (int u = 0; u < nUsers; u++) {
			List<Integer> ratedItemIndexes = Util.newList();
			
			for (int i = 0; i < nItems; i++) {
				float value = ratingMatrix[u][i];
				if (Util.isUsed(value)) {
					ratedItemIndexes.add(i);
				}
			}
			
			userRatedIndexes.put(u, ratedItemIndexes);
		}

		
		float coeff = 1.0f / (float) (nUsers * nItems);
		coeff = (float) ((double) coeff * coeff);
		coeff = (float) ((double) coeff * coeff);
		coeff = 0;
		itemImplicitFactors.clear();
		for (int i = 0; i < nItems; i++) {
			Vector itemImplicitFactor = new Vector(factor, 0);
			for (int f = 0; f < factor; f++)
				itemImplicitFactor.set(f, coeff);
			
			itemImplicitFactors.add(itemImplicitFactor);
		}
		
		
		return true;
	}


	@Override
	protected void learn_main(RatingMatrix userMatrix) {
		// TODO Auto-generated method stub
		float precision = getConfig().getAsReal(PRECISION);
		if (precision == 0)
			precision = 1;
		precision = Math.min(precision, 1.0f);
		
		float minRating = getConfig().getMetadata().minRating;
		float maxRating = getConfig().getMetadata().maxRating;
		float epsilon = (float) Math.pow( (maxRating - minRating), 2) * (1.0f - precision);
		float gamma = getConfig().getAsReal(GAMMA);
		float lamda = getConfig().getAsReal(LAMDA);
		float lamda2 = getConfig().getAsReal(LAMDA2);
		
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
					Vector Ru = userImplicitRatedVector(u);
					
					float predictedValue = avgRating + userBias.get(u) + itemBias.get(i) + Qi.product(Pu.add(Ru));
					float error = value - predictedValue;
					
                    bu = bu + gamma * (error - lamda * bu);
                    bi = bi + gamma * (error - lamda * bi);
                    Qi = Qi.add( Pu.add(Ru).multiply(error).subtract(Qi.multiply(lamda2)).multiply(gamma) );
                    Pu = Pu.add( Qi.multiply(error).subtract(Pu.multiply(lamda2)).multiply(gamma) );
                    		
                    userBias.set(u, bu);
                    itemBias.set(i, bi);
                    userFactors.set(u, Pu);
                    itemFactors.set(i, Qi);
                    
            		List<Integer> ratedItemIndexes = userRatedIndexes.get(u);
            		float k = (float) Math.sqrt(ratedItemIndexes.size());
            		for (int ratedItemIndex : ratedItemIndexes) {
            			Vector Y = itemImplicitFactors.get(ratedItemIndex);
            			
            			if (Y != null) {
            				Y = Y.add( Qi.multiply(error * k).subtract(Y.multiply(lamda2)).multiply(gamma) );
            				itemImplicitFactors.set(ratedItemIndex, Y);
            			}
            		}

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
					Vector Ru = userImplicitRatedVector(u);
					float predictedValue = avgRating + userBias.get(u) + itemBias.get(i) + Qi.product(Pu.add(Ru));
					float error = value - predictedValue;
					
					newSumOfSquare +=
                            Math.pow(error, 2) + 
                            lamda * (
                            	Math.pow(userBias.get(u), 2) + 
                            	Math.pow(itemBias.get(i), 2) + 
                            	Math.pow(Pu.add(Ru).module(), 2) + 
                            	Math.pow(Qi.module(), 2)
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
	protected void destroyDataStructure() {
		// TODO Auto-generated method stub
		super.destroyDataStructure();
		
		userRatedIndexes.clear();
		itemImplicitFactors.clear();
	}


	@Override
	public void load0() {
		// TODO Auto-generated method stub
		super.load0();
		
		UriAdapter adapter = null;
		BufferedReader buffer = null;
		try {
			xURI store = config.getStoreUri();
			xURI gradientFactorsUri = store.concat(GRADIENT_FACTORS_EXT);
			adapter = new UriAdapter(config);
			if (adapter.exists(gradientFactorsUri)) {
				Reader gradientFactorsReader = adapter.getReader(gradientFactorsUri);
				buffer = new BufferedReader(gradientFactorsReader);

				int userRatedIndexesSize = Integer.parseInt(buffer.readLine());
				for (int i = 0; i < userRatedIndexesSize; i++) {
					List<String> list = TextParserUtil.split(buffer.readLine(), TextParserUtil.MAIN_SEP, null);
					
					int userId = Integer.parseInt(list.get(0));
					List<Integer> indexes = TextParserUtil.parseListByClass(list.get(1), Integer.class, ",");
					userRatedIndexes.put(userId, indexes);
				}
				

				int itemImplicitFactorSize = Integer.parseInt(buffer.readLine());
				for (int i = 0; i < itemImplicitFactorSize; i++) {
					Vector itemImplicitFactor = new Vector(new float[0]);
					itemImplicitFactor.parseText(buffer.readLine());
					
					itemImplicitFactors.add(itemImplicitFactor);
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
			
			if (adapter != null)
				adapter.close();
		}
		
	}

	
	@Override
	public void export0(DataConfig storeConfig) {
		// TODO Auto-generated method stub
		super.export0(storeConfig);
		
		if (isEmpty())
			return;

		UriAdapter adapter = null;
		PrintWriter printer = null;
		try {
			xURI store = storeConfig.getStoreUri();
			adapter = new UriAdapter(storeConfig);
			
			xURI gradientFactorsUri = store.concat(GRADIENT_FACTORS_EXT);
			Writer gradientFactorsWriter = adapter.getWriter(gradientFactorsUri, false);
			printer = new PrintWriter(gradientFactorsWriter);
			
			Set<Integer> userIds = userRatedIndexes.keySet();
			printer.println(userIds.size());
			for (int userId : userIds) {
				printer.println(
					userId + TextParserUtil.MAIN_SEP + 
					TextParserUtil.toText(userRatedIndexes.get(userId), ","));
			}
			
			printer.println(itemImplicitFactors);
			for (Vector itemImplicitFactor : itemImplicitFactors) {
				printer.println(itemImplicitFactor.toText());
			}

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


	/**
	 * 
	 * @param userIndex
	 * @return implicit rated vector
	 */
	private Vector userImplicitRatedVector(int userIndex) {
		List<Integer> ratedItemIndexes = userRatedIndexes.get(userIndex);
		if (ratedItemIndexes.size() == 0)
			return new Vector(userFactors.get(0).dim(), 0);
		
		int n = ratedItemIndexes.size();
		Vector sum = null;
		for (int i = 0; i < n; i++) {
			Vector factor = itemImplicitFactors.get(ratedItemIndexes.get(i));
			if (sum == null)
				sum = factor;
			else
				sum.add(factor);
		}
		
		
		return sum.divide( (float) Math.sqrt(n));
	}
	
	
	@Override
	public PropList getDefaultParameters() {
		PropList superParameters = super.getDefaultParameters();
		
		DataConfig parameters = new DataConfig();
		parameters.putAll(superParameters);
		
		parameters.put(GAMMA, new Float(DEFAULT_GAMMA));
		parameters.put(LAMDA, new Float(DEFAULT_LAMDA));
		parameters.put(LAMDA2, new Float(DEFAULT_LAMDA2));
		
		return parameters;
	}
	
	/**
	 * 
	 * @param cf specified advanced SVD Gradient Collaborative Filtering (SVD Gradient Plus) algorithm.
	 * @return {@link SvdGradientPlusKB}
	 */
	public static SvdGradientPlusKB create(final SvdGradientPlusCF cf) {
		
		return new SvdGradientPlusKB() {

			
			/**
			 * 
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



