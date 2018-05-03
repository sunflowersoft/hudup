package net.hudup.core.data;

import java.io.Serializable;
import java.util.Date;

import net.hudup.core.Cloneable;
import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.ctx.ContextList;
import net.hudup.core.parser.TextParsable;


/**
 * This class represents a rating that a user gives on an item. As a convention this class is called {@code rating}.
 * In current implementation, a rating includes three important parts as follows:
 * <ol>
 * <li>Rating value represented by {@link #value} is a float number that a user rates on an item.</li>
 * <li>The date that a user rates on an item, represented by {@link #ratedDate}. Of course it contains year, month, day, hour, minute, second, etc.</li>
 * <li>A list of contexts that a user rates on an item, represented by {@link #contexts}. Context is additional information related to user's rating, for example, place that user makes a rating,
 * companion indicating the persons with whom user makes a rating such as alone, friends, girlfriend/boyfriend, family, co-workers. </li>
 * </ol>
 * As a definition, a rating is called {@code rated} (user rated an item) if its rating value is not {@link Constants#UNUSED}; otherwise such rating is called {@code unrated}.
 * Note that rating does not specify user identification (user ID) and item identification (item ID).
 * A set of rating that the same user gives on many item makes a so-called rating vector represented by {@link RatingVector}.
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Rating implements Cloneable, Serializable, TextParsable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Rating value is a float number that a user rates on an item. This variable is public for fast accessing.
	 * If user does not rate on an item, this rating value is {@link Constants#UNUSED}.
	 */
	public float value = Constants.UNUSED;
	
	
	/**
	 * A list of contexts that a user rates on an item. Context is additional information related to user's rating, for example, place that user makes a rating,
	 * companion indicating the persons with whom user makes a rating such as alone, friends, girlfriend/boyfriend, family, co-workers.
	 * This variable is public for fast accessing.
	 */
	public ContextList contexts = new ContextList();
	
	
	/**
	 * The date that a user rates on an item. Of course it contains year, month, day, hour, minute, second, etc.
	 * This variable is public for fast accessing.
	 */
	public Date ratedDate = new Date();
	
	
	/**
	 * Default constructor.
	 */
	protected Rating() {
		
	}

	
	/**
	 * Constructor with specified rating value.
	 * @param ratingValue specified rating value.
	 */
	public Rating(float ratingValue) {
		this.value = ratingValue;
	}
	
	
	/**
	 * Testing whether user rates on an item. The methods returns {@code true} if user rated. Otherwise, it returns {@code false}.
	 * @return whether user rates on an item.
	 */
	public boolean isRated() {
		return Util.isUsed(value);
	}
	
	
	/**
	 * Setting that user does not rate on an item yet, which means that the variable {@link #value} is set to be {@link Constants#UNUSED}.
	 */
	public void unrate() {
		value = Constants.UNUSED;
	}
	
	
	/**
	 * Assigning the specified rating to this rating. Note that every variable of the specified rating are assigned to every variable of this rating.
	 * There is no deep copying (cloning) of variables. Following is the code of this method:<br>
	 * <code>
	 * if (that != null) {<br>
	 * this.contexts = that.contexts;<br>
	 * this.ratedDate = that.ratedDate;<br>
	 * this.value = that.value;<br>
	 * }
	 * </code>
	 * @param that specified rating which assigned to this rating.
	 */
	public void assign(Rating that) {
		if (that != null) {
			this.contexts = that.contexts;
			this.ratedDate = that.ratedDate;
			this.value = that.value;
		}
	}
	
	
	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		Rating rating = new Rating(this.value);
		rating.contexts = (ContextList) this.contexts.clone();
		rating.ratedDate = new Date(this.ratedDate.getTime());
		
		return rating;
	}


	@Override
	public String toText() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not support this method yet");
	}

	
	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not support this method yet");
	}

	
}
