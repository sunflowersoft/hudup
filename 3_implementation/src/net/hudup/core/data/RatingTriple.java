/**
 * 
 */
package net.hudup.core.data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import net.hudup.core.Cloneable;
import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.parser.TextParsable;


/**
 * This class contains the rating that a user gives on an item, including three following parts:
 * <ul>
 * <li>The rating represented by {@link Rating} class.</li>
 * <li>User identification (user ID).</li>
 * <li>Item identification (item ID).</li>
 * </ul>
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class RatingTriple implements Cloneable, TextParsable, Serializable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Identification (ID) of the user who rates on an item.
	 */
	protected int userId = -1;
	
	
	/**
	 * Identification (ID) of the item which is received rating from a user.
	 */
	protected int itemId = -1;
	
	
	/**
	 * The rating that the user gives on the item.
	 */
	protected Rating rating = new Rating(Constants.UNUSED);
	
	
	/**
	 * Constructor with specified user ID, specified item ID, and specified rating. 
	 * @param userId specified user ID.
	 * @param itemId specified item ID.
	 * @param rating specified rating.
	 */
	public RatingTriple(int userId, int itemId, Rating rating) {
		this.userId = userId;
		this.itemId = itemId;
		this.rating = rating;
	}

	
	/**
	 * Getting user identification (user ID).
	 * @return user id.
	 */
	public int userId() {
		return userId;
	}
	
	
	/**
	 * Getting item identification (item ID).
	 * @return item id.
	 */
	public int itemId() {
		return itemId;
	}
	
	
	/**
	 * Getting the rating that the user gives on the item.
	 * @return rating that the user gives on the item.
	 */
	public Rating getRating() {
		return rating;
	}
	
	
	/**
	 * This method indicates that the user gave the specified rating on the item.
	 * @param rating specified rating that the user gives on the item.
	 */
	public void setRating(Rating rating) {
		this.rating = rating;
	}
	
	
	/**
	 * Checking whether or not the user rated on the user.
	 * @return whether or not the user rated on the user.
	 */
	public boolean isRated() {
		return rating.isRated();
	}


	@Override
    public Object clone() {
    	return new RatingTriple(userId, itemId, (Rating) rating.clone());
    }
    
    
    /**
	 * Extracting rating triples from the specified user rating vector.
	 * User rating vector is a vector of ratings that a user gives on many items.
	 * @param user specified user rating vector.
	 * @return list of rating triples extracted from the specified user rating vector.
	 */
	public static List<RatingTriple> getUserRatings(RatingVector user) {
		List<RatingTriple> triples = Util.newList();
		int userId = user.id();
		Set<Integer> fieldIds = user.fieldIds(true);
		for (int fieldId : fieldIds) {
			Rating rating = user.get(fieldId);
			RatingTriple triple = new RatingTriple(userId, fieldId, rating);
			triples.add(triple);
		}
		
		return triples;
	}

	
	/**
	 * Extracting rating triples from the specified item rating vector.
	 * Item rating vector is a vector of ratings that an item are received from many users.
	 * @param item item rating vector.
	 * @return list of rating triples extracted from the specified item rating vector.
	 */
	public static List<RatingTriple> getItemRatings(RatingVector item) {
		List<RatingTriple> triples = Util.newList();
		int itemId = item.id();
		Set<Integer> fieldIds = item.fieldIds(true);
		for (int fieldId : fieldIds) {
			Rating rating = item.get(fieldId);
			RatingTriple triple = new RatingTriple(fieldId, itemId, rating);
			triples.add(triple);
		}
		
		return triples;
	}


	@Override
	public String toString() {
		return toText();
	}
	
	
	@Override
	public String toText() {
		// TODO Auto-generated method stub
		
		throw new RuntimeException("Not support this method");
	}


	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		clear();
		
		throw new RuntimeException("Not support this method");
	}
	
	
	/**
	 * Clearing this rating triple.
	 */
	private void clear() {
		userId = -1;
		itemId = -1;
		rating = new Rating(Constants.UNUSED);
	}
	
	
}
