/**
 * 
 */
package net.hudup.core.data;

import java.util.Map;
import java.util.Set;



/**
 * This class extending the {@link RatingVector} class represents a user rating vector that contains all ratings of the same user on many items and so the internal variable {@link #id} is user identification (user ID).
 * The internal map contains all ratings of this user rating vector. Each entry of this map has a key and a value.
 * Value is rating represented by {@link Rating} class and key is item identification (item ID).
 * As a convention, such key is called field id which is item id, exactly.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class UserRating extends RatingVector {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public UserRating() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * Constructor with specified user identification (user ID).
	 * @param userId specified user ID.
	 */
	public UserRating(int userId) {
		super(userId);
		// TODO Auto-generated constructor stub
	}


	/**
	 * Constructor with the additional parameter {@code sequence}.
	 * If this parameter {@code sequence} of is {@code true} then, the internal variable {@link #ratedMap} containing all ratings will have built-in mechanism of sequence as a list.
	 * @param sequence additional parameter guiding how to create the internal variable {@link #ratedMap}. If it is {@code true} then, such internal variable containing all ratings will have built-in mechanism of sequence as a list.
	 */
	public UserRating(boolean sequence) {
		super(sequence);
	}

	
	/**
	 * Constructor with user ID and additional parameter {@code sequence}.
	 * If this parameter {@code sequence} of is {@code true} then, the internal variable {@link #ratedMap} containing all ratings will have built-in mechanism of sequence as a list.
	 * @param userId specified user ID.
	 * @param sequence additional parameter guiding how to create the internal variable {@link #ratedMap}. If it is {@code true} then, such internal variable containing all ratings will have built-in mechanism of sequence as a list.
	 */
	public UserRating(int userId, boolean sequence) {
		super(userId, sequence);
	}
	
	
	/**
	 * Getting a set of item identifications (item IDs).
	 * @return set of item id (s)
	 */
	public Set<Integer> itemIds() {
		return fieldIds();
	}
	

	
	@Override
    public Object clone() {
		Map<Integer, Rating> ratedMap = clone(this.ratedMap);
    	
    	UserRating vector = new UserRating(id);
    	vector.assign(ratedMap);
    	return vector;
    }
	
	
}
