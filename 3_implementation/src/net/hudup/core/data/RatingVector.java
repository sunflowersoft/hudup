/**
 * 
 */
package net.hudup.core.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.hudup.core.Cloneable;
import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.ctx.ContextList;
import net.hudup.core.logistic.ListMap;
import net.hudup.core.logistic.ListSet;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;

/**
 * This class represents a vector (an array) of ratings. As a convention, its is called rating vector.
 * Recall that rating represented by {@link Rating} class contains following important information relevant to the event a user rates on an item
 * <ol>
 * <li>Rating value represented by {@link Rating#value} is a float number that a user rates on an item.</li>
 * <li>The date that a user rates on an item, represented by {@link Rating#ratedDate}. Of course it contains year, month, day, hour, minute, second, etc.</li>
 * <li>A list of contexts that a user rates on an item, represented by {@link Rating#contexts}. Context is additional information related to user's rating, for example, place that user makes a rating,
 * companion indicating the persons with whom user makes a rating such as alone, friends, girlfriend/boyfriend, family, co-workers. </li>
 * </ol>
 * As a definition, a rating is called {@code rated} (user rated an item) if its rating value is not {@link Constants#UNUSED}; otherwise such rating is called {@code unrated}.
 * Note that rating does not specify user identification (user ID) and item identification (item ID). This aspect is very important, which derives two kinds of rating vector such as user rating vector and item rating vector.
 * If this class is user rating vector, it contains all ratings of the same user on many items and so the internal variable {@link #id} is user identification (user ID).
 * If this class is item rating vector, it contains all ratings of many users on the same item and so the internal variable {@link #id} is item identification (item ID).
 * As a convention, the internal variable {@link #id} is known as ID of rating vector.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class RatingVector implements Cloneable, TextParsable, Serializable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * If this class is user rating vector which contains all ratings of the same user on many items then, this internal variable is user identification (user ID).
	 * If this class is item rating vector which contains all ratings of many users on the same item then, this internal variable is item identification (item ID).
	 * As a convention, this variable is known as ID of rating vector.
	 */
	protected int id = -1;
	
	
	/**
	 * This map contains all ratings of this rating vector. Each entry of this map has a key and a value.
	 * Value is rating represented by {@link Rating} class.
	 * If this class is user rating vector which contains all ratings of the same user on many items then, key is item identification (item ID).
	 * If this class is item rating vector which contains all ratings of many users on the same item then, key is user identification (user ID).
	 * As a convention, such key is called field id indicating both user id and item id, which is distinguished with regard that this rating vector is user rating vector or item rating vector.
	 * Therefore, every rating is associated with a field ID.  
	 */
	protected Map<Integer, Rating> ratedMap = Util.newMap();
	
	
	/**
	 * Default constructor.
	 */
	public RatingVector() {
		this(false);
	}
	
	
	/**
	 * Default constructor with specified identification (ID).
	 * @param id specified ID of rating vector.
	 */
	public RatingVector(int id) {
		this(id, false);
	}
	
	
	/**
	 * Constructor with the additional parameter {@code sequence}.
	 * If this parameter {@code sequence} of is {@code true} then, the internal variable {@link #ratedMap} containing all ratings will have built-in mechanism of sequence as a list.
	 * @param sequence additional parameter guiding how to create the internal variable {@link #ratedMap}. If it is {@code true} then, such internal variable containing all ratings will have built-in mechanism of sequence as a list.
	 */
	public RatingVector(boolean sequence) {
		super();
		if (sequence)
			ratedMap = new ListMap<Integer, Rating>();
		
		if (!(ratedMap instanceof Serializable))
			throw new RuntimeException("RatingVector isn't serializable class");
	}
	
	
	/**
	 * Constructor with ID and additional parameter {@code sequence}.
	 * If this parameter {@code sequence} of is {@code true} then, the internal variable {@link #ratedMap} containing all ratings will have built-in mechanism of sequence as a list.
	 * @param id specified ID of rating vector.
	 * @param sequence additional parameter guiding how to create the internal variable {@link #ratedMap}. If it is {@code true} then, such internal variable containing all ratings will have built-in mechanism of sequence as a list.
	 */
	public RatingVector(int id, boolean sequence) {
		this(sequence);
		this.id = id;
	}
	
	
	/**
	 * This is the utility method for creating {@link Set}, used by other methods.
	 * @return normal set or list set.
	 */
	private Set<Integer> newSet() {
		if (ratedMap instanceof ListMap)
			return new ListSet<Integer>();
		else
			return Util.newSet();
	}
	
	
	/**
	 * Getting the ID of this rating vector. 
	 * @return ID of rating vector. If this rating vector is user rating vector, it is user ID. Otherwise, it is item ID.
	 */
	public int id() {
		return id;
	}
	
	
	/**
	 * Setting the ID of this rating vector.
	 * @param id ID of rating vector. If this rating vector is user rating vector, it is user ID. Otherwise, it is item ID.
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Returning the size of this rating vector which is the number of ratings.
	 * @return size of this rating vector.
	 */
	public int size() {
		return ratedMap.size();
	}
	
	
	/**
	 * Method is used to get rating at specified field ID.
	 * @param fieldId If this rating vector is user rating vector, the field ID is item ID. Otherwise, it is user ID.
	 * @return rating at specified field ID.
	 */
	public Rating get(int fieldId) {
		return ratedMap.get(fieldId);
	}
	
	
	/**
	 * Getting all ratings in this vector.
	 * @return collection of all ratings in this vector.
	 */
	public Collection<Rating> gets() {
		return ratedMap.values();
	}
	
	
	/**
	 * Method is used to put a specified rating identified by a specified field ID.
	 * @param fieldId if this rating vector is user rating vector, the field ID is item ID. Otherwise, it is user ID.
	 * @param rating specified rating that is put into this vector.
	 */
	public void put(int fieldId, Rating rating) {
		ratedMap.put(fieldId, rating);
	}
	
	
	/**
	 * Putting a rating into this vector by its specified field ID and specified value.
	 * @param fieldId if this rating vector is user rating vector, the field ID is item ID. Otherwise, it is user ID.
	 * @param ratingValue specified rating value.
	 */
	public void put(int fieldId, float ratingValue) {
		Rating rating = null;
		if (ratedMap.containsKey(fieldId)) {
			rating = ratedMap.get(fieldId);
		}
		else {
			rating = new Rating(Constants.UNUSED);
			ratedMap.put(fieldId, rating);
		}
		
		rating.value = ratingValue;
	}

	
	/**
	 * Putting all ratings of other rating vector into this rating vector.
	 * @param vRating other rating vector.
	 */
	public void put(RatingVector vRating) {
		Set<Integer> fieldIds = vRating.fieldIds();
		for (int fieldId : fieldIds) {
			ratedMap.put(fieldId, vRating.get(fieldId));
		}
		
	}
	
	
	/**
	 * Testing whether the rating associated with specified field ID is rated.
	 * As a definition, a rating is called {@code rated} (user rated an item) if its rating value is not {@link Constants#UNUSED}; otherwise such rating is called {@code unrated}.
	 * @param fieldId If this rating vector is user rating vector, the field ID is item ID. Otherwise, it is user ID.
	 * @return whether the rating associated with specified field ID is rated.
	 */
	public boolean isRated(int fieldId) {
		if (!contains(fieldId)) 
			return false;
		
		return ratedMap.get(fieldId).isRated();
	}
	
	
	/**
	 * Testing whether all ratings associated with field ID (s) in specified collection are rated.
	 * As a definition, a rating is called {@code rated} (user rated an item) if its rating value is not {@link Constants#UNUSED}; otherwise such rating is called {@code unrated}.
	 * @param fieldIds specified collection of field IDs. If this rating vector is user rating vector, the field ID is item ID. Otherwise, it is user ID.
	 * @return whether all ratings associated with field ID (s) in specified collection are rated.
	 */
	public boolean isRatedAll(Collection<Integer> fieldIds) {
		for (int fieldId : fieldIds) {
			if (!isRated(fieldId)) return false;
		}
		
		return true;
	}
	
	
	/**
	 * To remove rating at specified field ID.
	 * @param fieldId If this rating vector is user rating vector, the field ID is item ID. Otherwise, it is user ID.
	 */
	public void remove(int fieldId) {
		ratedMap.remove(fieldId);
		
	}

	
	/**
	 * Removing all ratings associated with field ID (s) of specified collection.
	 * @param fieldIds specified collection of field IDs. If this rating vector is user rating vector, the field ID is item ID. Otherwise, it is user ID.
	 */
	public void remove(Collection<Integer> fieldIds) {
		 for (Integer fieldId : fieldIds) {
			 ratedMap.remove(fieldId);
		 }
	}
	
	
	/**
	 * Keeping only ratings whose associated field ID (s) are in the specified collection.
	 * @param fieldIds specified collection of field ID (s). If this rating vector is user rating vector, the field ID is item ID. Otherwise, it is user ID.
	 */
	public void retain(Collection<Integer> fieldIds) {
		Set<Integer> allFields = newSet();
		allFields.addAll(fieldIds());
		
		 for (Integer fieldId : allFields) {
			 if (!fieldIds.contains(fieldId))
				 ratedMap.remove(fieldId);
		 }
	}
	
	
	/**
	 * The internal variable {@link #ratedMap} of this rating vector is replaced by the specified map of ratings.  
	 * This method makes a reference assignment as follows:<br>
	 * <code>
	 * this.ratedMap = ratedMap;
	 * </code>
	 * @param ratedMap specified map of ratings.
	 */
	protected void assign(Map<Integer, Rating> ratedMap) {
		this.ratedMap = ratedMap;
	}
	
	
	/**
	 * Getting a set of all field ID (s) of this vector.
	 * @return set of field IDs contained in this vector. If this rating vector is user rating vector, the field ID is item ID. Otherwise, it is user ID.
	 */
	public Set<Integer> fieldIds() {
		return ratedMap.keySet();
	}

	
	/**
	 * Getting a set of field ID (s) whose associated ratings have values that are equal to specified value.
	 * @param ratingValue specified rating value.
	 * @return set of field ID (s). If this rating vector is user rating vector, the field ID is item ID. Otherwise, it is user ID.
	 */
	public Set<Integer> fieldIds(float ratingValue) {
		Set<Integer> filter = newSet();
		
		boolean isRated = Util.isUsed(ratingValue);
		Set<Integer> fieldIds = isRated ? fieldIds(true) : fieldIds(false);
		for (int fieldId : fieldIds) {
			float value = get(fieldId).value;
			
			if (!isRated)
				filter.add(fieldId);
			else if (value == ratingValue)
				filter.add(fieldId);
		}
		
		return filter;
	}
	
	
	/**
	 * Getting a set of field ID (s) whose associated ratings are rated or not rated according to the specified parameter {@code rated}.
	 * As a definition, a rating is called {@code rated} (user rated an item) if its rating value is not {@link Constants#UNUSED}; otherwise such rating is called {@code unrated}.
	 * @param rated whether or not a rating is rated.
	 * @return set of field ID (s) whose associated ratings are rated or not rated according to the specified parameter {@code rated}. If this rating vector is user rating vector, the field ID is item ID. Otherwise, it is user ID.
	 */
	public Set<Integer> fieldIds(boolean rated) {
		Set<Integer> keys = ratedMap.keySet();
		Set<Integer> ratedIds = newSet();
		for (Integer key : keys) {
			if (isRated(key) == rated)
				ratedIds.add(key);
		}
		
		return ratedIds;
	}

	
	/**
	 * Given a list of field ID (s) as a pattern, this method returns a list of values whose ratings are associated with such pattern.
	 * @param fieldPattern. If this rating vector is user rating vector, the field ID is item ID. Otherwise, it is user ID.
	 * @return {@link List} of rating values according to pattern.
	 */
	public List<Float> toValueList(List<Integer> fieldPattern) {
		List<Float> list = Util.newList();
		for (int field : fieldPattern) {
			if (!ratedMap.containsKey(field))
				list.add(Constants.UNUSED);
			else
				list.add(ratedMap.get(field).value);
		}
		return list;
	}

	
	/**
	 * Checking whether this vector contains the rating associated with specified field ID.
	 * @param fieldId specified field ID. If this rating vector is user rating vector, the field ID is item ID. Otherwise, it is user ID.
	 * @return whether this vector contains the rating associated with specified field ID.
	 */
	public boolean contains(int fieldId) {
		return ratedMap.containsKey(fieldId);
	}

	
	/**
	 * Calculating the sum of all values of ratings in this vector.
	 * @return sum of all values of ratings.
	 */
	public float sum() {
		Set<Integer> fieldIds = fieldIds(true);
		
		float sum = 0;
		for (int fieldId : fieldIds) {
			float value = get(fieldId).value;
			sum += value;
		}
		
		return sum;
	}
	
	
	/**
	 * Calculating the mean value (average value) over all values of ratings in this vector.
	 * @return mean value (average value) over all values of ratings.
	 */
	public float mean() {
		Set<Integer> fieldIds = fieldIds(true);
		
		float accum = 0;
		int count = 0;
		for (int fieldId : fieldIds) {
			float value = get(fieldId).value;
			accum += value;
			count ++;
		}
		
		return accum / count;
	}

	
	/**
	 * Calculating Euclidean distance of this rating vector and the specified rating vector according to their values.
	 * Concretely, this rating vector and the specified rating vectors produce two vectors of their values and this method calculates the distance of such two value vectors.
	 * @param that specified rating vector
	 * @return Euclidean distance of this rating vector and the specified rating vector.
	 */
	public float distance(RatingVector that) {
		float dis = 0;
		
		Set<Integer> fieldIds = fieldIds(true);
		for (int fieldId : fieldIds) {
			if (!that.isRated(fieldId)) continue;
			
			float value1 = ratedMap.get(fieldId).value;
			float value2 = that.ratedMap.get(fieldId).value;
			float deviate =  value1 - value2;
			dis += deviate * deviate;
		}
		
		return (float) Math.sqrt(dis);
	}
	
	
	/**
	 * Calculating the correlation coefficient of this rating vector and the specified rating vector according to their values.
	 * Concretely, this rating vector and the specified rating vector produce two vectors of their values and this method calculates the correlation coefficient of such two value vectors.
	 * @param that specified rating vector.
	 * @return correlation coefficient of this rating vector and the specified rating vector.
	 */
	public float corr(RatingVector that) {
		float mean1 = mean();
		float mean2 = that.mean();
		
		Set<Integer> fieldIds = fieldIds(true);
		float VX = 0, VY = 0;
		float VXY = 0;
		for (int fieldId : fieldIds) {
			if (!that.isRated(fieldId)) 
				continue;
			
			float value1 = ratedMap.get(fieldId).value;
			float value2 = that.ratedMap.get(fieldId).value;
			float deviate1 = value1 - mean1;
			float deviate2 = value2 - mean2;
			
			VX  += deviate1 * deviate1;
			VY  += deviate2 * deviate2;
			VXY += deviate1 * deviate2;
		}
		
		return VXY / (float) Math.sqrt(VX * VY);
	}
	

	/**
	 * Calculating the cosine of this rating vector and the specified rating vector according to their values.
	 * Concretely, this rating vector and the specified rating vector produce two vectors of their values and this method calculates the cosine of such two value vectors.
	 * @param that specified rating vector.
	 * @return cosine of this rating vector and the specified rating vector.
	 */
	public float cosine(RatingVector that) {
		Set<Integer> fieldIds = fieldIds(true);
		
		float product = 0f;
		float length1 = 0f;
		float length2 = 0f;
		for (int fieldId : fieldIds) {
			if (!that.isRated(fieldId)) 
				continue;
			
			float value1 = ratedMap.get(fieldId).value;
			float value2 = that.ratedMap.get(fieldId).value;
			
			length1 += value1 * value1;
			length2 += value2 * value2;
			product += value1 * value2;
		}
		
		if (length1 == 0 || length2 == 0)
			return Constants.UNUSED;
		return product / (float) Math.sqrt(length1 * length2);
	}
	
	
	/**
	 * This method makes ratings associated with the field ID (s) in the specified collection to be not rated.
	 * As a definition, a rating is called {@code rated} (user rated an item) if its rating value is not {@link Constants#UNUSED}; otherwise such rating is called {@code unrated}.
	 * @param fields specified collection of field ID (s). If this rating vector is user rating vector, the field ID is item ID. Otherwise, it is user ID.
	 */
	public void unrate(Collection<Integer> fields) {
		for (int field : fields) {
			if (!ratedMap.containsKey(field))
				continue;
			
			ratedMap.get(field).value = Constants.UNUSED;
		}
	}
	
	
	/**
	 * Removing ratings which are not rated from this vector.
	 * As a definition, a rating is called {@code rated} (user rated an item) if its rating value is not {@link Constants#UNUSED}; otherwise such rating is called {@code unrated}.
	 */
	public void compact() {
		Set<Integer> fields = fieldIds(false);
		for (int field : fields) {
			remove(field);
		}
	}
	

	/**
	 * For ratings which are unrated, this method sets their values by specified filling value.
	 * As a definition, a rating is called {@code rated} (user rated an item) if its rating value is not {@link Constants#UNUSED}; otherwise such rating is called {@code unrated}.
	 * @param fillValue specified filling value which is used to fill in unrated ratings.
	 */
	public void fillForUnrated(float fillValue) {
		Set<Integer> fields = fieldIds(false);
		
		for (int field : fields)
			ratedMap.get(field).value = fillValue;
	}
	
	
	/**
	 * Every rating specified by {@link Rating} has a context list. Context is additional information related to user's rating, for example, place that user makes a rating,
	 * companion indicating the persons with whom user makes a rating such as alone, friends, girlfriend/boyfriend, family, co-workers.
	 * Context A can be inferred from context B if:
	 * <ul>
	 * <li>Two contexts are totally equal (template and value are the same).</li>
	 * <li>Two (context) templates are equal and context A has null value, it means that it is more generic than context B.</li>
	 * <li>Template of context A can be inferred from specified template, it means that template of context A is more generic than template of context B.</li>
	 * </ul>
	 * For example, context &quot;December 2015&quot; can be inferred from context &quot;8th December 2015&quot;.
	 * This method returns a new rating vector derived from this rating vector so that the context list of such new vector can be inferred from the specified context list.
	 * @param contexts specified context list.
	 * @return new rating vector derived from this rating vector so that the context list of such new vector can be inferred from the specified context list.
	 */
	public RatingVector select(ContextList contexts) {
		RatingVector vRating = newInstance();
		
		Set<Integer> fieldIds = fieldIds();
		for (int fieldId : fieldIds) {
			Rating rating = get(fieldId);
			if (rating.contexts.canInferFrom(contexts))
				vRating.put(fieldId, rating);
		}
		
		if (vRating.size() == 0)
			return null;
		else
			return vRating;
	}
	
	
	/**
	 * In case that ratings values are real numbers, this method make round of these values.
	 * @return new rating vector derived from this rating vector so that rating values of the new vector are rounded. 
	 */
	public RatingVector toRoundValues() {
		RatingVector vRating = this.newInstance(ratedMap instanceof ListMap);
		
		Set<Entry<Integer, Rating>> entries = ratedMap.entrySet();
		for (Entry<Integer, Rating> entry : entries) {
			Rating rating = entry.getValue();
			if (rating.isRated()) {
				
				float value = (int)(rating.value + 0.5);
				vRating.ratedMap.put(entry.getKey(), new Rating(value));
			}
		}
		
		return vRating;
	}
	
	
	/**
	 * Clearing this vector, which means that all ratings are removed from this vector.
	 */
	public void clear() {
		ratedMap.clear();
	}
	
	
	@Override
    public Object clone() {
		Map<Integer, Rating> ratedMap = clone(this.ratedMap);
		
    	RatingVector vector = new RatingVector(id);
    	vector.assign(ratedMap);
    	return vector;
    }
	
	
	/**
	 * Creating a new rating vector so that this rating vector plays a role of template.
	 * Note that such new vector is empty (which has no ratings) but its ID is the ID of this vector.
	 * @param sequence additional parameter guiding how to create the internal variable {@link #ratedMap}. If it is {@code true} then, such internal variable containing all ratings will have built-in mechanism of sequence as a list.
	 * @return new vector so that it is empty (which has no ratings) but its ID is the ID of this vector.
	 */
	public RatingVector newInstance(boolean sequence) {
		RatingVector result = null;
		try {
			result = this.getClass().newInstance();
			if (sequence)
				result.ratedMap = new ListMap<Integer, Rating>();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		result.setId(this.id);
		return result;
	}

	
	/**
	 * Creating a new rating vector so that this rating vector plays a role of template.
	 * Note that such new vector is empty (which has no ratings) but its ID is the ID of this vector.
	 * @return new vector so that it is empty (which has no ratings) but its ID is the ID of this vector.
	 */
	public RatingVector newInstance() {
		return newInstance(ratedMap instanceof ListMap);
	}
	
	
	/**
	 * Counting for ratings which are rated or unrated according the parameter {@code rated}.
	 * As a definition, a rating is called {@code rated} (user rated an item) if its rating value is not {@link Constants#UNUSED}; otherwise such rating is called {@code unrated}.
	 * @param rated if true, the method returns the number of rated ratings. Otherwise, the method returns the number of unrated ratings. 
	 * @return the number of ratings which are rated or unrated according the parameter {@code rated}.
	 */
	public int count(boolean rated) {
		int count = 0;
		Set<Integer> keys = ratedMap.keySet();
		for (Integer key : keys) {
			if (isRated(key) == rated)
				count++;
		}
		
		return count;
	}

	
	/**
	 * Given a row of rating values specified by the parameter {@code row}, this method counts for ratings which are rated or unrated according the parameter {@code rated}.
	 * As a definition, a rating value is called {@code rated} if it is not {@link Constants#UNUSED}; otherwise it is call {@code unrated}.
	 * @param row a row of rating values.
	 * @param rated if true, the static method returns the number of rated value. Otherwise, the method returns the number of unrated values.
	 * @return the number of values which are rated or unrated according the parameter {@code rated}.
	 */
	public static int count(float[] row, boolean rated) {
		
		int count = 0;
		for (float v : row) {
			if (Util.isUsed(v) == rated)
				count++;
		}
		
		return count;
	}


	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		clear();
		
		List<String> list = TextParserUtil.split(spec, TextParserUtil.MAIN_SEP, null);
		if (list.size() == 0)
			return;
		
		id = Integer.parseInt(list.get(0));
		if (list.size() == 1)
			return;
		
		list = TextParserUtil.split(list.get(1), ",", null);
		for (String el : list) {
			List<String> pair = TextParserUtil.split(el, "=", null);
			if (pair.size() < 2)
				continue;
			
			int fieldId = Integer.parseInt(pair.get(0));
			Rating rating = new Rating(Constants.UNUSED);
			rating.parseText(pair.get(1));
			put(fieldId, rating);
		}
	}
	
	
	@Override
	public String toText() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(id);
		
		List<Integer> fieldIdList = Util.newList();
		fieldIdList.addAll(fieldIds(true));
		if (fieldIdList.size() == 0)
			return buffer.toString();
		
		buffer.append(" " + TextParserUtil.MAIN_SEP + " ");
		for (int i = 0; i < fieldIdList.size(); i++) {
			if (i > 0)
				buffer.append(", ");
			
			int fieldId = fieldIdList.get(i);
			Rating rating = get(fieldId);
			buffer.append(fieldId + "=" + rating.value);
		}
		
		return buffer.toString();
	}
	
	
	/**
	 * Deep cloning the specified map of ratings.
	 * Note that each rating in this map associated with a field ID (key). Field ID can be user ID or item ID, dependent on application.
	 * @param map specified map of ratings.
	 * @return cloned map of ratings.
	 */
	public final static Map<Integer, Rating> clone(Map<Integer, Rating> map) {
		Map<Integer, Rating> newMap = null;
		if (map instanceof ListMap)
			newMap = new ListMap<Integer, Rating>();
		else
			newMap = Util.newMap();
			
		Set<Integer> keys = map.keySet();
		for (Integer key : keys) {
			int newKey = key.intValue();
			Rating newRating = (Rating) map.get(key).clone();
			newMap.put(newKey, newRating);
		}
		
		return newMap;
	}

	
	/**
	 * This static method iterates all ratings via the fetcher of ratings and put such ratings to a new map.
	 * Note that fetcher is the interface for iterating each item of an associated collection.
	 * @param fetcher specified fetcher of ratings, represented by {@link Fetcher} interface.
	 * @param sequence additional parameter guiding how to create the new map of ratings. If it is {@code true} then, such new map containing all ratings will have built-in mechanism of sequence as a list.
	 * @param autoClose if true, the specified fetcher is closed after the transference is finished.
	 * @return new map contains fetched ratings.
	 */
	public static Map<Integer, RatingVector> transfer(Fetcher<RatingVector> fetcher, boolean sequence, boolean autoClose) {
		Map<Integer, RatingVector> map = null;
		if (sequence)
			map = new ListMap<Integer, RatingVector>();
		else
			map = Util.newMap();
		
		try {
			while (fetcher.next()) {
				RatingVector vRating = fetcher.pick();
				if (vRating == null)
					continue;
				
				map.put(vRating.id(), vRating);
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (autoClose)
					fetcher.close();
				else
					fetcher.reset();
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return map;
		
	}
	
	
	/**
	 * Deep cloning the specified map of rating vectors.
	 * Each entry of this map is a pair of key and value. Value is a rating vector and key is ID of such rating vector.
	 * @param map specified map of rating vectors.
	 * @return cloned map of rating vectors.
	 */
	public static Map<Integer, RatingVector> clone2(Map<Integer, RatingVector> map) {
		Map<Integer, RatingVector> newMap = null;
		if (map instanceof ListMap)
			newMap = new ListMap<Integer, RatingVector>();
		else
			newMap = Util.newMap();
		
		Set<Integer> keys = map.keySet();
		for (Integer key : keys) {
			Integer newKey = new Integer(key);
			RatingVector newvRating = (RatingVector) map.get(key).clone();
			newMap.put(newKey, newvRating);
		}
		
		return newMap;
	}
	
	
	/**
	 * Every rating specified by {@link Rating} has a context list. Context is additional information related to user's rating, for example, place that user makes a rating,
	 * companion indicating the persons with whom user makes a rating such as alone, friends, girlfriend/boyfriend, family, co-workers.
	 * Context A can be inferred from context B if:
	 * <ul>
	 * <li>Two contexts are totally equal (template and value are the same).</li>
	 * <li>Two (context) templates are equal and context A has null value, it means that it is more generic than context B.</li>
	 * <li>Template of context A can be inferred from specified template, it means that template of context A is more generic than template of context B.</li>
	 * </ul>
	 * For example, context &quot;December 2015&quot; can be inferred from context &quot;8th December 2015&quot;.
	 * This static method returns a new map of rating vectors derived from the specified map of rating vectors so that the context list of every rating vector of such new map can be inferred from the specified context list.
	 * @param map specified map of rating vectors
	 * @param contexts specified context list.
	 * @return new map of rating vectors derived from the specified map of rating vectors so that the context list of every rating vector of such new map can be inferred from the specified context list.
	 */
	public static Map<Integer, RatingVector> select(Map<Integer, RatingVector> map, ContextList contexts) {
		Map<Integer, RatingVector> newMap = null;
		if (map instanceof ListMap)
			newMap = new ListMap<Integer, RatingVector>();
		else
			newMap = Util.newMap();
		
		Set<Integer> keys = map.keySet();
		for (Integer key : keys) {
			Integer newKey = new Integer(key);
			RatingVector newvRating = (RatingVector) map.get(key).select(contexts);
			if (newvRating != null)
				newMap.put(newKey, newvRating);
		}
		
		return newMap;
	}


}