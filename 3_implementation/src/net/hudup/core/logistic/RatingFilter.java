package net.hudup.core.logistic;

/**
 * This interface, called {@code rating filter} specifies how to select (filter) a rating value based on comparing such rating value with a referred rating value.
 * Any class that implements this interface must define the method {@link #accept(float, float)} to make such comparison or filtering).
 * 
 * @author Loc Nguyen
 * @version 11.0
 *
 */
public interface RatingFilter {
	
	
	/**
	 * Any class that implements this interface must define this method to select (filter) a rating value based on comparing such rating value with a referred rating value..
	 * 
	 * @param ratingValue specified rating value.
	 * @param referredRatingValue referred rating value.
	 * 
	 * @return whether specified value accepted as filter value
	 */
	boolean accept(float ratingValue, float referredRatingValue);
	
	
}


