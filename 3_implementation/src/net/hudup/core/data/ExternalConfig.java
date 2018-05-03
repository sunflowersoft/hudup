package net.hudup.core.data;

import net.hudup.core.logistic.Cipher;
import net.hudup.core.logistic.xURI;


/**
 * This class specified the configuration including external information stored in outside database different from Hudup framework database.
 * As a convention, this class is called {@code external configuration} which is often used by {@link ExternalQuery}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ExternalConfig extends SysConfig {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * External user SQL field name. SQL, abbreviation of Structured Query Language, of is the formal language for accessing and operating on relation database.
	 * External user SQL is the SQL statement for retrieving users at outside database. 
	 */
	public static final String EXTERNAL_USERSQL_FIELD   = changeCase("external_usersql");
	
	/**
	 * External item SQL field name. SQL, abbreviation of Structured Query Language, of is the formal language for accessing and operating on relation database. 
	 * External item SQL is the SQL statement for retrieving items at outside database. 
	 */
	public static final String EXTERNAL_ITEMSQL_FIELD   = changeCase("external_itemsql");
	
	/**
	 * External rating SQL field name. SQL, abbreviation of Structured Query Language, of is the formal language for accessing and operating on relation database. 
	 * External rating SQL is the SQL statement for retrieving ratings at outside database. 
	 */
	public static final String EXTERNAL_RATINGSQL_FIELD = changeCase("external_ratingsql");

	
	/**
	 * Default constructor.
	 */
	public ExternalConfig() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * Constructor with specified URI pointing to where to locate this configuration.
	 * @param uri specified URI pointing to where to locate this configuration.
	 */
	public ExternalConfig(xURI uri) {
		super(uri);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void reset() {
		// TODO Auto-generated method stub
		super.reset();
		
		setItemUnit(ITEM_UNIT);
		setItemIdField(ITEMID_FIELD);
		setItemTypeField(ITEM_TYPE_FIELD);
		
		setUserUnit(USER_UNIT);
		setUserIdField(USERID_FIELD);
		setUserTypeField(USER_TYPE_FIELD);
		
		setRatingUnit(RATING_UNIT);
		setRatingUserIdField(RATING_USERID_FIELD);
		setRatingItemIdField(RATING_ITEMID_FIELD);
		setRatingField(RATING_FIELD);
	}


	/**
	 * Getting item identifier (item ID).
	 * @return item id field.
	 */
	public String getItemIdField() {
		return getAsString(ITEMID_FIELD);
	}

	
	/**
	 * Setting item identifier (item ID) by specified ID.
	 * @param itemIdField specified ID.
	 */
	public void setItemIdField(String itemIdField) {
		put(ITEMID_FIELD, itemIdField);
	}
	
	
	/**
	 * Getting item type.
	 * @return item type field.
	 */
	public String getItemTypeField() {
		return getAsString(ITEM_TYPE_FIELD);
	}

	
	/**
	 * Setting item type by specified type.
	 * @param itemTypeField specified item type.
	 */
	public void setItemTypeField(String itemTypeField) {
		put(ITEM_TYPE_FIELD, itemTypeField);
	}
	
	
	/**
	 * Getting user identifier (user ID).
	 * @return user id field.
	 */
	public String getUserIdField() {
		return getAsString(USERID_FIELD);
	}

	
	/**
	 * Setting user identifier (user ID) by specified ID.
	 * @param userIdField specified user ID.
	 */
	public void setUserIdField(String userIdField) {
		put(USERID_FIELD, userIdField);
	}
	
	
	/**
	 * Getting user type.
	 * @return user type field.
	 */
	public String getUserTypeField() {
		return getAsString(USER_TYPE_FIELD);
	}

	
	/**
	 * Setting user type by specified type.
	 * @param userTypeField specified user type.
	 */
	public void setUserTypeField(String userTypeField) {
		put(USER_TYPE_FIELD, userTypeField);
	}

	
	/**
	 * Getting user ID in rating unit (rating table).
	 * @return user id field in rating unit (rating table).
	 */
	public String getRatingUserIdField() {
		return getAsString(RATING_USERID_FIELD);
	}

	
	/**
	 * Setting user ID in rating unit (rating table) by specified ID.
	 * @param ratingUserIdField specified ID.
	 */
	public void setRatingUserIdField(String ratingUserIdField) {
		put(RATING_USERID_FIELD, ratingUserIdField);
	}
	
	
	/**
	 * Getting item ID in rating unit (rating table).
	 * @return item id field in rating unit (rating table).
	 */
	public String getRatingItemIdField() {
		return getAsString(RATING_ITEMID_FIELD);
	}

	
	/**
	 * Setting user ID in rating unit (rating table) by specified ID.
	 * @param ratingItemIdField specified ID.
	 */
	public void setRatingItemIdField(String ratingItemIdField) {
		put(RATING_ITEMID_FIELD, ratingItemIdField);
	}


	/**
	 * Getting rating field in rating unit (rating table).
	 * @return rating field in rating unit (rating table).
	 */
	public String getRatingField() {
		return getAsString(RATING_FIELD);
	}

	
	/**
	 * Getting rating field in rating unit (rating table) by specified rating field.
	 * @param ratingField specified rating field.
	 */
	public void setRatingField(String ratingField) {
		put(RATING_FIELD, ratingField);
	}


	/**
	 * Getting user SQL statement which is the SQL statement for retrieving users at outside database.
	 * @return user SQL statement which is the SQL statement for retrieving users at outside database.
	 */
	public String getUserSql() {
		return getAsString(EXTERNAL_USERSQL_FIELD);
	}
	
	
	/**
	 * Setting user SQL statement by specified SQL statement.
	 * Note, user SQL statement is the SQL statement for retrieving users at outside database.
	 * @param userSql specified SQL statement.
	 */
	public void setUserSql(String userSql) {
		put(EXTERNAL_USERSQL_FIELD, userSql);
	}
	
	
	/**
	 * Getting item SQL statement which is the SQL statement for retrieving items at outside database.
	 * @return item SQL statement which is the SQL statement for retrieving items at outside database.
	 */
	public String getItemSql() {
		return getAsString(EXTERNAL_ITEMSQL_FIELD);
	}
	
	
	/**
	 * Setting item SQL statement by specified SQL statement.
	 * Note, item SQL statement is the SQL statement for retrieving items at outside database.
	 * @param itemSql specified SQL statement.
	 */
	public void setItemSql(String itemSql) {
		put(EXTERNAL_ITEMSQL_FIELD, itemSql);
	}

	
	/**
	 * Getting rating SQL statement which is the SQL statement for retrieving ratings at outside database.
	 * @return rating SQL statement which is the SQL statement for retrieving ratings at outside database.
	 */
	public String getRatingSql() {
		return getAsString(EXTERNAL_RATINGSQL_FIELD);
	}
	
	
	/**
	 * Setting rating SQL statement by specified SQL statement.
	 * Note, rating SQL statement is the SQL statement for retrieving ratings at outside database.
	 * @param ratingSql specified SQL statement.
	 */
	public void setRatingSql(String ratingSql) {
		put(EXTERNAL_RATINGSQL_FIELD, ratingSql);
	}

	
	@Override
	public Object clone() {
		ExternalConfig cfg = new ExternalConfig();
		cfg.putAll(this);
		
		return cfg;
	}


	@Override
	protected String encrypt(HiddenText hidden) {
		// TODO Auto-generated method stub
		Cipher cipher = new Cipher();
		return cipher.encrypt(hidden.getText());
	}


	@Override
	protected HiddenText decrypt(String text) {
		// TODO Auto-generated method stub
		Cipher cipher = new Cipher();
		return new HiddenText(cipher.decrypt(text));
	}


}
