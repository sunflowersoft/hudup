/**
 * 
 */
package net.hudup.core.data;

import java.io.Serializable;
import java.util.List;

import net.hudup.core.Cloneable;
import net.hudup.core.Constants;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;

/**
 * {@link DatasetMetadata} stores essential information about dataset represented by {@link Dataset}, for example:
 * minimum rating value, maximum rating value, number of items, number of users, etc.
 * As a convention, {@code DatasetMetadata} is called {@code dataset meta-data}.
 * It is a short description (manifest) of dataset.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class DatasetMetadata implements Serializable, TextParsable, Cloneable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Minimum rating value, for example, the minimum rating value of Movielens data is 1.
	 */
	public float minRating = Constants.DEFAULT_MIN_RATING;
	
	
	/**
	 * Maximum rating value, for example, the minimum rating value of Movielens data is 5.
	 */
	public float maxRating = Constants.DEFAULT_MAX_RATING;


	/**
	 * Number of users available in dataset.
	 */
	public int numberOfUsers = 0;
	
	
	/**
	 * Number of users who rated on items in dataset.
	 */
	public int numberOfUserRatings = 0;
	
	
	/**
	 * Number of users available in dataset.
	 */
	public int numberOfItems = 0;
	
	
	/**
	 * Number of users that are rated in dataset.
	 */
	public int numberOfItemRatings = 0;

	
	/**
	 * Default constructor.
	 */
	public DatasetMetadata() {
		
	}
	
	
	@Override
	public String toText() {
		// TODO Auto-generated method stub
		return minRating + "," + 
				maxRating + ", " + 
				numberOfUsers + ", " + 
				numberOfUserRatings +
				numberOfItems + ", " + 
				numberOfItemRatings;
	}


	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		List<String> textList = TextParserUtil.split(spec, ",", null);
		minRating = Integer.parseInt(textList.get(0));
		maxRating = Integer.parseInt(textList.get(1));
		numberOfUsers = Integer.parseInt(textList.get(2));
		numberOfUserRatings = Integer.parseInt(textList.get(3));
		numberOfItems = Integer.parseInt(textList.get(4));
		numberOfItemRatings = Integer.parseInt(textList.get(5));
	}
	
	
	@Override
	public String toString() {
		return toText();
	}


	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		DatasetMetadata metadata = new DatasetMetadata();
		metadata.minRating = this.minRating;
		metadata.maxRating = this.maxRating;
		metadata.numberOfUsers = this.numberOfUsers;
		metadata.numberOfUserRatings = this.numberOfUserRatings;
		metadata.numberOfItems = this.numberOfItems;
		metadata.numberOfItemRatings = this.numberOfItemRatings;
		
		return metadata;
	}
	
	
	/**
	 * Extracting meta-data about specific dataset.
	 * @param dataset Specific dataset
	 * @return {@link DatasetMetadata}
	 */
	public static DatasetMetadata create(Dataset dataset) {
		DatasetMetadata metadata = dataset.getConfig().getMetadata();
		if (dataset instanceof Pointer)
			return metadata;
		
		Fetcher<Integer> userIds = null;
		try {
			userIds = dataset.fetchUserIds();
			metadata.numberOfUsers = userIds.getMetadata().getSize();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (userIds != null)
					userIds.close();
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Fetcher<RatingVector> vUserRatings = null;
		try {
			vUserRatings = dataset.fetchUserRatings();
			metadata.numberOfUserRatings = vUserRatings.getMetadata().getSize();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (vUserRatings != null)
					vUserRatings.close();
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	
		Fetcher<Integer> itemIds = null;
		try {
			itemIds = dataset.fetchItemIds();
			metadata.numberOfItems = itemIds.getMetadata().getSize();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (itemIds != null)
					itemIds.close();
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Fetcher<RatingVector> vItemRatings = null;
		try {
			vItemRatings = dataset.fetchItemRatings();
			metadata.numberOfItemRatings = vItemRatings.getMetadata().getSize();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (vItemRatings != null)
					vItemRatings.close();
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		return metadata;
	}
	
	
}
