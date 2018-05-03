package net.hudup.core.alg.cf;

import java.awt.Component;
import java.io.Serializable;
import java.util.List;

import javax.swing.JOptionPane;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.Vector;


/**
 * This class sets up the neighbor collaborative filtering (Neighbor CF) algorithm. It is memory-based CF because it extends directly {@link MemoryBasedCF} class.
 * Neighbor CF algorithm finds out the nearest neighbor of active user (or active item).
 * Later on, Neighbor CF algorithm recommends items that the nearest neighbor likes (rates with high value) to the active user, with assumption that the nearest neighbor and the active user (active item) share the same interests.
 * <br>
 * The most important feature of any Neighbor CF algorithm is how to calculate the similarity between two rating vectors or between two profiles so as to find out the nearest neighbor.
 * The nearest neighbor is the one who has highest similar measure with the active user (active item).
 * This class provides the method {@link #similar(RatingVector, RatingVector, Profile, Profile)} to calculate the similarity measure between two pairs.
 * The first pair includes the first rating vector and the first profile.
 * The second pair includes the second rating vector and the second profile.
 * If you only want to calculate the similarity between two rating vectors, two in put profiles are set to be null.
 * If you only want to calculate the similarity between two profiles, two in put rating vectors are set to be null.
 * In current implementation, only three similarity measures are supported such as Pearson, cosine, and hybrid.
 * Hybrid measure means that profile is merged into rating vector as a unified vector for calculating Pearson measure or cosine measure.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class NeighborCF extends MemoryBasedCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * In the configuration, the entry of similarity measure has the name specified by this constant.
	 */
	public static final String SIMILAR = "similar";
	
	
	/**
	 * Name of hybrid measure.
	 */
	public static final String HYBRID = "hybrid";

	
	/**
	 * Name of cosine measure.
	 */
	public static final String COSINE = "cosine";

	
	/**
	 * Name of Pearson measure.
	 */
	public static final String PEARSON = "pearson";

	
	/**
	 * Default constructor.
	 */
	public NeighborCF() {
		// TODO Auto-generated constructor stub
		
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = new DataConfig() {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Serializable userEdit(Component comp, String key, Serializable defaultValue) {
				// TODO Auto-generated method stub
				
				if (key.equals(SIMILAR)) {
					return (Serializable) JOptionPane.showInputDialog(
							comp, 
							"Please choose one similar measure", 
							"Choosing similar measure", 
							JOptionPane.INFORMATION_MESSAGE, 
							null, 
							getSupportedSimilarMeasures().toArray(), 
							getDefaultSimilarMeasure());
				}
				else 
					return super.userEdit(comp, key, defaultValue);
			}
			
		};
		
		config.put(SIMILAR, getDefaultSimilarMeasure());
		config.put(HYBRID, false);
		
		return config;
	}

	
	/**
	 * Getting the list of supported similar measures in names.
	 * @return supported similar measures.
	 */
	public List<String> getSupportedSimilarMeasures() {
		List<String> list = Util.newList();
		list.add(COSINE);
		list.add(PEARSON);
		return list;
	}
	
	
	/**
	 * Getting the default similarity measure.
	 * @return default similar measure.
	 */
	public String getDefaultSimilarMeasure() {
		return PEARSON;
	}

	
	/**
	 * Calculating the similarity measure between two pairs.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * If you only want to calculate the similarity between two rating vectors, two in put profiles are set to be null.
	 * If you only want to calculate the similarity between two profiles, two in put rating vectors are set to be null.
	 * In current implementation, only three similarity measures are supported such as Pearson, cosine, and hybrid.
	 * Hybrid measure means that profile is merged into rating vector as a unified vector for calculating Pearson measure or cosine measure.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return similarity between both two {@link RatingVector} (s) and two {@link Profile} (s).
	 */
	public float similar(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		String similar = config.getAsString(SIMILAR);
		
		if (similar == null)
			return Constants.UNUSED;
		
		boolean hybrid = config.getAsBoolean(HYBRID);
		if (!hybrid) {
			profile1 = null;
			profile2 = null;
		}
		
		if (similar.equals(COSINE))
			return cosine(vRating1, vRating2, profile1, profile2);
		else if (similar.equals(PEARSON))
			return corr(vRating1, vRating2, profile1, profile2);
		else
			return Constants.UNUSED;
			
	}
	
	
	/**
	 * Calculating the cosine measure between two pairs.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return the cosine between both two {@link RatingVector} (s) and two {@link Profile} (s).
	 */
	public float cosine(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		
		if (profile1 == null || profile2 == null) 
			return vRating1.cosine(vRating2);
		
		Vector[] vectors = toNormVector(vRating1, vRating2, profile1, profile2);
		Vector vector1 = vectors[0];
		Vector vector2 = vectors[1];
		
		return vector1.cosine(vector2);
	}

	
	/**
	 * Calculating the correlation coefficient between two pairs.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return correlation coefficient between both two {@link RatingVector} (s) and two {@link Profile} (s).
	 */
	public float corr(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		
		if (profile1 == null || profile2 == null) 
			return vRating1.corr(vRating2);

		Vector[] vectors = toNormVector(vRating1, vRating2, profile1, profile2);
		Vector vector1 = vectors[0];
		Vector vector2 = vectors[1];
		
		return vector1.corr(vector2);
	}

	
	/**
	 * Normalizing two pairs into two normalized vectors.
	 * The first pair includes the first rating vector and the first profile.
	 * The second pair includes the second rating vector and the second profile.
	 * Note, all elements of normalized vectors are &gt;=0 and &lt;=1 and sum of them is 1.
	 * However, for the result of this method, each normalized vector corresponding to each pair has two parts such as part of rating vector and part of profile.
	 * Part of rating vector is normalized partially and part of profile is normalized partially.
	 * For example, suppose the first pair is (1, 2, 3, 4, 5, 6) in which part of rating vector is (1, 2, 3) and part of profile is (4, 5, 6).
	 * Such pair is normalized as vector (1/6, 2/6, 3/6, 4/15, 5/15, 6/15) because the normalized rating vector (1/6, 2/6, 3/6) is and the normalized profile is (4/15, 5/15, 6/15).
	 * If profile does not contains numbers, there is a conversion.
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return array of two normalized vectors.
	 */
	private Vector[] toNormVector(
			RatingVector vRating1, RatingVector vRating2, 
			Profile profile1, Profile profile2) {
		
		Vector vector1 = new Vector(0, 0);
		Vector vector2 = new Vector(0, 0);
		
		Vector[] vectors = toNormVector(vRating1, vRating2);
		if (vectors.length != 0) {
			vector1.concat(vectors[0]);
			vector2.concat(vectors[1]);
		}
		
		vectors = toNormDiscreteVectors(profile1, profile2);
		if (vectors.length != 0) {
			vector1.concat(vectors[0]);
			vector2.concat(vectors[1]);
		}
		
		return new Vector[] { vector1, vector2 };
	}
	
	
	/**
	 * Normalizing two rating vectors into two normalized vectors.
	 * For example, two rating vectors (1, 3, 5) and (2, 4, 6) are normalized into (1/9, 3/9, 5/9) and (2/12, 4/12, 6/12).
	 * 
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @return array of two normalized {@link Vector} (s) from two rating vectors.
	 */
	private Vector[] toNormVector(RatingVector vRating1, RatingVector vRating2) {
		
		List<Integer> common = Util.newList();
		common.addAll(vRating1.fieldIds(true));
		common.retainAll(vRating2.fieldIds(true));
		if (common.size() == 0)
			return new Vector[0];
		
		float n = dataset.getConfig().getMaxRating() - dataset.getConfig().getMinRating();
		
		float[] data1 = new float[common.size()];
		float[] data2 = new float[common.size()];
		for (int i = 0; i < common.size(); i++) {
			int id = common.get(i);
			
			data1[i] = vRating1.get(id).value / n; 
			data2[i] = vRating2.get(id).value / n; 
		}
		
		return new Vector[] { new Vector(data1), new Vector(data2)};
	}
	
	
	/**
	 * Normalizing two profiles into two normalized vector.
	 * Note, values in such two profiles are binary or nominal. Nominal indicates discrete and non-number data such as weekdays {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday}.
	 * 
	 * @param profile1 first profiles.
	 * @param profile2 second profiles.
	 * @return array of two normalized {@link Vector} (s) from two profiles.
	 */
	private Vector[] toNormDiscreteVectors(Profile profile1, Profile profile2) {
		if (profile1 == null || profile2 == null)
			return new Vector[0];
		
		List<Float> data1 = Util.newList();
		List<Float> data2 = Util.newList();

		AttributeList attRef = profile1.getAttRef();
		for (int i = 0; i < attRef.size(); i++) {
			Attribute att = attRef.get(i);
			if (!att.isCategory() || att.isKey())
				continue;
			if (profile1.isMissing(i) || profile2.isMissing(i))
				continue;
			
			float value1 = profile1.getValueAsReal(i);
			float value2 = profile2.getValueAsReal(i);
			if (att.getType() == Type.nominal) {
				value1 = profile1.getNominalNormalizedValue(i);
				value2 = profile2.getNominalNormalizedValue(i);
			}
			data1.add(value1);
			data2.add(value2);
		}
		
		if (data1.size() == 0 || data2.size() == 0)
			return new Vector[0];
		
		return new Vector[] { new Vector(data1), new Vector(data2)};
	}

	
}
