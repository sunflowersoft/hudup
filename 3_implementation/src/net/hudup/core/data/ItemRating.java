/**
 * 
 */
package net.hudup.core.data;

import java.util.Map;
import java.util.Set;



/**
 * This class extending the {@link RatingVector} class represents a item rating vector that contains all ratings of many users on the same item and so the internal variable {@link #id} is item identification (item ID).
 * The internal map contains all ratings of this item rating vector. Each entry of this map has a key and a value.
 * Value is rating represented by {@link Rating} class and key is user identification (user ID).
 * As a convention, such key is called field id which is user id, exactly.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ItemRating extends RatingVector {
	
	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public ItemRating() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * Constructor with specified item identification (item ID).
	 * @param itemId specified item ID.
	 */
	public ItemRating(int itemId) {
		super(itemId);
		// TODO Auto-generated constructor stub
	}


	/**
	 * Constructor with the additional parameter {@code sequence}.
	 * If this parameter {@code sequence} of is {@code true} then, the internal variable {@link #ratedMap} containing all ratings will have built-in mechanism of sequence as a list.
	 * @param sequence additional parameter guiding how to create the internal variable {@link #ratedMap}. If it is {@code true} then, such internal variable containing all ratings will have built-in mechanism of sequence as a list.
	 */
	public ItemRating(boolean sequence) {
		super(sequence);
	}

	
	/**
	 * Constructor with item ID and additional parameter {@code sequence}.
	 * If this parameter {@code sequence} of is {@code true} then, the internal variable {@link #ratedMap} containing all ratings will have built-in mechanism of sequence as a list.
	 * @param itemId specified item ID.
	 * @param sequence additional parameter guiding how to create the internal variable {@link #ratedMap}. If it is {@code true} then, such internal variable containing all ratings will have built-in mechanism of sequence as a list.
	 */
	public ItemRating(int itemId, boolean sequence) {
		super(itemId, sequence);
		
	}

	
	/**
	 * Getting a set of user identifications (user IDs).
	 * @return set of user id (s)
	 */
	public Set<Integer> userIds() {
		return fieldIds();
	}

	
	@Override
    public Object clone() {
		Map<Integer, Rating> ratedMap = clone(this.ratedMap);
    	
    	ItemRating vector = new ItemRating(id);
    	vector.assign(ratedMap);
    	return vector;
    }
	
	
}
