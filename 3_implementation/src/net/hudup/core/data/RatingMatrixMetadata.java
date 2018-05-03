package net.hudup.core.data;

import java.io.Serializable;
import java.util.List;

import net.hudup.core.Cloneable;
import net.hudup.core.Constants;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;

/**
 * This class represents meta-data of rating matrix for storing additional information of rating matrix.
 * It is called {@code rating matrix meta-data}, which is always associated with a rating matrix.
 * Note that rating matrix is represented by {@code RatingMatrix} class, containing rating values that many users give on many items.
 * For example, additional information stored in this meta-data includes maximum rating value, minimum rating value,
 * whether or not the associated rating matrix is user rating matrix, the number of users, the number of items, the number of ratings that users give, the number of ratings that items are received.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class RatingMatrixMetadata implements Serializable, TextParsable, Cloneable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Minimum rating value.
	 */
	public float minRating = Constants.DEFAULT_MIN_RATING;
	
	
	/**
	 * Maximum rating value.
	 */
	public float maxRating = Constants.DEFAULT_MAX_RATING;


	/**
	 * If this variable is {@code true}, this meta-data is of user rating matrix in which each row is a vector of rating values that a user gives on many items.
	 * Otherwise, this meta-data is of item rating matrix in which each row is a vector of rating values that an item is received from many users.
	 */
	public boolean isUser = true;
	
	
	/**
	 * The number of users.
	 */
	public int numberOfUsers = 0;

	
	/**
	 * The number of ratings that users give. Such ratings are called user ratings.
	 */
	public int numberOfUserRatings = 0;
	
	
	/**
	 * The number of items.
	 */
	public int numberOfItems = 0;
	
	
	/**
	 * The number of ratings that items are received. Such ratings are called item ratings.
	 */
	public int numberOfItemRatings = 0;

	
	/**
	 * Default constructor.
	 */
	public RatingMatrixMetadata() {
		
	}
	
	
	@Override
	public String toText() {
		// TODO Auto-generated method stub
		return minRating + "," + 
				maxRating + ", " + 
				isUser + ", " + 
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
		isUser = Boolean.parseBoolean(textList.get(2));
		numberOfUsers = Integer.parseInt(textList.get(3));
		numberOfUserRatings = Integer.parseInt(textList.get(4));
		numberOfItems = Integer.parseInt(textList.get(5));
		numberOfItemRatings = Integer.parseInt(textList.get(6));
	}
	
	
	@Override
	public String toString() {
		return toText();
	}


	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		RatingMatrixMetadata metadata = new RatingMatrixMetadata();
		metadata.minRating = this.minRating;
		metadata.maxRating = this.maxRating;
		metadata.isUser = this.isUser;
		metadata.numberOfUsers = this.numberOfUsers;
		metadata.numberOfUserRatings = this.numberOfUserRatings;
		metadata.numberOfItems = this.numberOfItems;
		metadata.numberOfItemRatings = this.numberOfItemRatings;
		
		
		return metadata;
	}
	
	
	/**
	 * Extracting rating matrix meta-data from the specified dataset meta-data.
	 * Recalled that dataset meta-data represented by {@link DatasetMetadata} class stores essential information about dataset.
	 * @param datasetMetadata specified dataset meta-data
	 * @param isUser if {@code true}, the returned meta-data is of user rating matrix in which each row is a vector of rating values that a user gives on many items.
	 * Otherwise, the returned meta-data is of item rating matrix in which each row is a vector of rating values that an item is received from many users.
	 * @return rating matrix meta-data extracted from the specified dataset meta-data.
	 */
	public static RatingMatrixMetadata from(DatasetMetadata datasetMetadata, boolean isUser) {
		RatingMatrixMetadata metadata = new RatingMatrixMetadata();
		
		metadata.minRating = datasetMetadata.minRating;
		metadata.maxRating = datasetMetadata.maxRating;
		metadata.isUser = isUser;
		metadata.numberOfUsers = datasetMetadata.numberOfUsers;
		metadata.numberOfUserRatings = datasetMetadata.numberOfUserRatings;
		metadata.numberOfItems = datasetMetadata.numberOfItems;
		metadata.numberOfItemRatings = datasetMetadata.numberOfItemRatings;
		
		return metadata;
	}
	
	
	/**
	 * Converting the this rating matrix meta-data into the dataset meta-data.
	 * Recalled that dataset meta-data represented by {@link DatasetMetadata} class stores essential information about dataset.
	 * @return dataset meta-data converted from this rating matrix meta-data.
	 */
	public DatasetMetadata to() {
		DatasetMetadata metadata = new DatasetMetadata();
		
		metadata.minRating = this.minRating;
		metadata.maxRating = this.maxRating;
		metadata.numberOfUsers = this.numberOfUsers;
		metadata.numberOfUserRatings = this.numberOfUserRatings;
		metadata.numberOfItems = this.numberOfItems;
		metadata.numberOfItemRatings = this.numberOfItemRatings;
		
		return metadata;
	}

	
}

