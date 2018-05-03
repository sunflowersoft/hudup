/**
 * 
 */
package net.hudup.core.client;

import java.io.Serializable;
import java.util.Set;

import net.hudup.core.alg.RecommendParam;
import net.hudup.core.data.ExternalRecord;
import net.hudup.core.data.InternalRecord;
import net.hudup.core.data.Nominal;
import net.hudup.core.data.Profile;
import net.hudup.core.data.Rating;
import net.hudup.core.data.RatingVector;



/**
 * {@code Protocol} interface specifies methods to create request represented by {@link Request} of Hudup client-server network in many possible cases.
 * Note, {@code Request} class represents user request. {@code Request} uses JSON format as exchangeable means in client-server network. Note, JSON (JavaScript Object Notation) is a human-read format used for interchange between many protocols, available at <a href="http://www.json.org/">http://www.json.org</a>.
 * User request includes recommendation request, retrieval request, update request.
 * Each request has a so-called {@code action} which is a short description (short string) of such request. Action can be considered as the purpose of request.
 * For example, the request that user requires a list of recommended items has action &quot;recommend_user&quot;.
 * <br>
 * {@code Protocol} establishes an interaction protocol of Hudup client-server network, which is named &quot;<b>hdp</b>&quot;.
 * Each method of protocol creates a particular request, for example, the method {@link #createRecommendRequest(RecommendParam, int)} will create a request that user requires a list of recommended items.
 * {@code Protocol} also defines actions of requests as constants.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface Protocol {
	
	
	/**
	 * Identifier of Hudup protocol (hdp).
	 */
	static final int HDP_PROTOCOL  = 0;
	
	/**
	 * Identifier of HTTP protocol.
	 */
	static final int HTTP_PROTOCOL = 1;
	
	
	/**
	 * Unknown file type.
	 */
	static final int UNKNOWN_FILE_TYPE = -1;
	
	/**
	 * HTML file type.
	 */
	static final int HTML_FILE_TYPE = 0;
	
	/**
	 * JPEG file type.
	 */
	static final int JPEG_FILE_TYPE = 1;
	
	/**
	 * GIF file type.
	 */
	static final int GIF_FILE_TYPE = 2;
	
	/**
	 * Text file type.
	 */
	static final int TEXTPLAIN_FILE_TYPE = 3;
	
	/**
	 * JSON file type. Note, JSON (JavaScript Object Notation) is a human-read format used for interchange between many protocols, available at <a href="http://www.json.org/">http://www.json.org</a>.
	 */
	static final int JSON_FILE_TYPE = 4;
	
	/**
	 * Compressed file type.
	 */
	static final int XZIP_FILE_TYPE = 5;
	
	
	/**
	 * Name of gateway of Hudup server (recommendation server).
	 */
	static final String GATEWAY                    = "gateway";


	/**
	 * Name of quit command.
	 */
	static final String QUIT =                            "quit";
	
	/**
	 * Action of reading file request.
	 */
	static final String READ_FILE =                       "read_file";
	
	/**
	 * Action of estimation request created by method {@link #createEstimateRequest(RecommendParam, Set)}.
	 */
	static final String ESTIMATE =                        "estimate";
	
	/**
	 * Action of recommendation request created by method {@link #createRecommendRequest(RecommendParam, int)}.
	 */
	static final String RECOMMEND =                       "recommend";
	
	/**
	 * Action of user recommendation request created by method {@link #createRecommendRequest(RecommendParam, int)}.
	 */
	static final String RECOMMEND_USER =                  "recommend_user";
	
	/**
	 * Action of recommendlet request created by method {@link #createRecommendletRequest(String, int, String, Serializable, Serializable, int, Rating)}.
	 * Recommendlet represented by {@code Recommendlet} class is service or a small graphic user interface (GUI) that allows users to see and interact with a list of recommended items.
	 * Recommendlet is not supported fully in the current implementation of Hudup framework.
	 */
	static final String RECOMMENDLET =                    "recommendlet";
	
	/**
	 * Action of inserting rating request created by method {@link #createInsertRatingRequest(RatingVector)}.
	 */
	static final String INSERT_RATING =                   "insert_rating";
	
	/**
	 * Action of updating rating request created by method {@link #createUpdateRatingRequest(RatingVector)}.
	 */
	static final String UPDATE_RATING =                   "update_rating";
	
	/**
	 * Action of deleting rating request created by method {@link #createDeleteRatingRequest(RatingVector)}.
	 */
	static final String DELETE_RATING =                   "delete_rating";
	
	/**
	 * Action of request of getting user identifiers. Such request is created by method {@link #createGetUserIdsRequest()}.
	 */
	static final String GET_USERIDS =                     "get_userids";
	
	/**
	 * Action of request of getting user rating, given an specified user. Such request is created by method {@link #createGetUserRatingRequest(int)}.
	 */
	static final String GET_USER_RATING =                 "get_user_rating";
	
	/**
	 * Action of request of getting user ratings. Such request is created by method {@link #createGetUserRatingsRequest()}.
	 */
	static final String GET_USER_RATINGS =                "get_user_ratings";
	
	/**
	 * Action of request of getting user ratings from specified SQL statement. Such request is created by method {@link #createGetUserRatingsSqlRequest(String)}.
	 */
	static final String GET_USER_RATINGS_SQL =            "get_user_ratings_sql";
	
	/**
	 * Action of request of deleting user rating. Such request is created by method {@link #createDeleteUserRatingRequest(int)}.
	 */
	static final String DELETE_USER_RATING =              "delete_user_rating";
	
	/**
	 * Action of request of getting user profile. Such request is created by method {@link #createGetUserProfileRequest(int)}.
	 */
	static final String GET_USER_PROFILE =                "get_user_profile";
	
	/**
	 * Action of request of getting user profile by external record. Such request is created by method {@link #createGetUserProfileByExternalRequest(Serializable)}.
	 */
	static final String GET_USER_PROFILE_BY_EXTERNAL =    "get_user_profile_by_external";
	
	/**
	 * Action of request of getting many user profiles. Such request is created by method {@link #createGetUserProfilesRequest()}.
	 */
	static final String GET_USER_PROFILES =               "get_user_profiles";
	
	/**
	 * Action of request of getting user profiles from SQL statement. Such request is created by method {@link #createGetUserProfilesRequest()}.
	 */
	static final String GET_USER_PROFILES_SQL =           "get_user_profiles_sql";
	
	/**
	 * Action of request of getting list of user attributes. Such request is created by method {@link #createGetUserAttributeListRequest()}.
	 */
	static final String GET_USER_ATTRIBUTE_LIST =         "get_user_attribute_list";
	
	/**
	 * Action of request of inserting user profile. Such request is created by method {@link #createInsertUserProfileRequest(Profile, ExternalRecord)}.
	 */
	static final String INSERT_USER_PROFILE =             "insert_user_profile";
	
	/**
	 * Action of request of updating user profile. Such request is created by method {@link #createUpdateUserProfileRequest(Profile)}.
	 */
	static final String UPDATE_USER_PROFILE =             "update_user_profile";
	
	/**
	 * Action of request of deleting user profile. Such request is created by method {@link #createDeleteUserProfileRequest(int)}.
	 */
	static final String DELETE_USER_PROFILE =             "delete_user_profile";
	
	/**
	 * Action of request of getting user external record. Such request is created by method {@link #createGetUserExternalRecordRequest(int)}.
	 */
	static final String GET_USER_EXTERNAL_RECORD =        "get_user_external_record";
	
	/**
	 * Action of request of getting item identifiers. Such request is created by method {@link #createGetItemIdsRequest()}.
	 */
	static final String GET_ITEMIDS =                     "get_itemids";
	
	/**
	 * Action of request of getting item rating. Such request is created by method {@link #createGetItemRatingRequest(int)}.
	 */
	static final String GET_ITEM_RATING =                 "get_item_rating";
	
	/**
	 * Action of request of getting item ratings. Such request is created by method {@link #createGetItemRatingsRequest()}.
	 */
	static final String GET_ITEM_RATINGS =                "get_item_ratings";
	
	/**
	 * Action of request of getting item ratings from SQL statement. Such request is created by method {@link #createGetItemRatingsSqlRequest(String)}.
	 */
	static final String GET_ITEM_RATINGS_SQL =            "get_item_ratings_sql";
	
	/**
	 * Action of request of getting item profile. Such request is created by method {@link #createGetItemProfileRequest(int)}.
	 */
	static final String GET_ITEM_PROFILE =                "get_item_profile";
	
	/**
	 * Action of request of getting item profile by external record. Such request is created by method {@link #createGetItemProfileByExternalRequest(Serializable)}.
	 */
	static final String GET_ITEM_PROFILE_BY_EXTERNAL =    "get_item_profile_by_external";
	
	/**
	 * Action of request of getting item profiles. Such request is created by method {@link #createGetItemProfilesRequest()}.
	 */
	static final String GET_ITEM_PROFILES =               "get_item_profiles";
	
	/**
	 * Action of request of getting item profiles from SQL statement. Such request is created by method {@link #createGetItemProfilesSqlRequest(String)}.
	 */
	static final String GET_ITEM_PROFILES_SQL =           "get_item_profiles_sql";
	
	/**
	 * Action of request of getting list of item attributes. Such request is created by method {@link #createGetItemAttributeListRequest()}.
	 */
	static final String GET_ITEM_ATTRIBUTE_LIST =         "get_item_attribute_list";
	
	/**
	 * Action of request of inserting item profile. Such request is created by method {@link #createInsertItemProfileRequest(Profile, ExternalRecord)}.
	 */
	static final String INSERT_ITEM_PROFILE =             "insert_item_profile";
	
	/**
	 * Action of request of updating item profile. Such request is created by method {@link #createUpdateItemProfileRequest(Profile)}.
	 */
	static final String UPDATE_ITEM_PROFILE =             "update_item_profile";
	
	/**
	 * Action of request of deleting item rating. Such request is created by method {@link #createDeleteItemRatingRequest(int)}.
	 */
	static final String DELETE_ITEM_RATING =              "delete_item_rating";
	
	/**
	 * Action of request of deleting item profile. Such request is created by method {@link #createDeleteItemProfileRequest(int)}.
	 */
	static final String DELETE_ITEM_PROFILE =             "delete_item_profile";
	
	/**
	 * Action of request of getting item external record. Such request is created by method {@link #createGetItemExternalRecordRequest(int)}.
	 */
	static final String GET_ITEM_EXTERNAL_RECORD =        "get_item_external_record";
	
	/**
	 * Action of request of getting nominal. Such request is created by method {@link #createGetNominalRequest(String, String)}.
	 */
	static final String GET_NOMINAL =                     "get_nominal";
	
	/**
	 * Action of request of inserting nominal. Such request is created by method {@link #createInsertNominalRequest(String, String, Nominal)}.
	 */
	static final String INSERT_NOMINAL =                  "insert_nominal";
	
	/**
	 * Action of request of updating nominal. Such request is created by method {@link #createUpdateNominalRequest(String, String, Nominal)}.
	 */
	static final String UPDATE_NOMINAL =                  "update_nominal";
	
	/**
	 * Action of request of deleting nominal. Such request is created by method {@link #createDeleteNominalRequest(String, String)}.
	 */
	static final String DELETE_NOMINAL =                  "delete_nominal";
	
	/**
	 * Action of request of getting external record. Such request is created by method {@link #createGetExternalRecordRequest(InternalRecord)}.
	 */
	static final String GET_EXTERNAL_RECORD =             "get_external_record";
	
	/**
	 * Action of request of inserting external record. Such request is created by method {@link #createInsertExternalRecordRequest(InternalRecord, ExternalRecord)}.
	 */
	static final String INSERT_EXTERNAL_RECORD =          "insert_external_record";
	
	/**
	 * Action of request of updating external record. Such request is created by method {@link #createUpdateExternalRecordRequest(InternalRecord, ExternalRecord)}.
	 */
	static final String UPDATE_EXTERNAL_RECORD =          "update_external_record";
	
	/**
	 * Action of request of deleting external record. Such request is created by method {@link #createDeleteExternalRecordRequest(InternalRecord)}.
	 */
	static final String DELETE_EXTERNAL_RECORD =          "delete_external_record";
	
	/**
	 * Action of request of getting profile. Such request is created by method {@link #createGetProfileRequest(String, Profile)}.
	 */
	static final String GET_PROFILE =                     "get_profile";
	
	/**
	 * Action of request of getting profiles from SQL statement. Such request is created by method {@link #createGetProfilesSqlRequest(String)}.
	 */
	static final String GET_PROFILES_SQL =                "get_profiles_sql";
	
	/**
	 * Action of request of getting attribute list of given profile. Such request is created by method {@link #createGetProfileAttributeListRequest(String)}.
	 */
	static final String GET_PROFILE_ATTRIBUTE_LIST =      "get_profile_attribute_list";
	
	/**
	 * Action of request of inserting profile. Such request is created by method {@link #createInsertProfileRequest(String, Profile)}.
	 */
	static final String INSERT_PROFILE =                  "insert_profile";
	
	/**
	 * Action of request of updating profile. Such request is created by method {@link #createUpdateProfileRequest(String, Profile)}.
	 */
	static final String UPDATE_PROFILE =                  "update_profile";
	
	/**
	 * Action of request of deleting profile. Such request is created by method {@link #createDeleteProfileRequest(String, Profile)}.
	 */
	static final String DELETE_PROFILE =                  "delete_profile";
	
	/**
	 * Action of request of getting snapshot. Such request is created by method {@link #createGetSnapshotRequest()}.
	 */
	static final String GET_SNAPSHOT =                    "get_snapshot";
	
	/**
	 * Action of request of getting configuration of Hudup server. Such request is created by method {@link #createGetServerConfigRequest()}.
	 */
	static final String GET_SERVER_CONFIG =               "get_server_config";
	
	/**
	 * Action of request of validating user account. Such request is created by method {@link #createValidateAccountRequest(String, String, int)}.
	 */
	static final String VALIDATE_ACCOUNT =                "validate_account";
	
	/**
	 * Action of request of getting session attribute. Such request is created by method {@link #createGetSessionAttributeRequest(String)}.
	 */
	static final String GET_SESSION_ATTRIBUTE =           "get_session_attribute";

	
	/**
	 * Creating estimation request based on specified recommendation parameter and specified set of identifiers (IDs).
	 * Such IDs can be user IDs or item IDs. This request is requirement of estimating rating values of such IDs.
	 * Method {@code estimate(...)} of {@code Recommender} class is responsible to satisfy this request. 
	 * @param param specified recommendation parameter. Recommendation parameter  contains important information as follows:
	 * <ul>
	 * <li>Initial rating vector which can be user rating vector or item rating vector.</li>
	 * <li>A profile which can be user profile containing user information (ID, name, etc.) or item profile containing item information (ID, name, price, etc.).</li>
	 * <li>A context list relevant to how and where user (s) rates (rate) on item (s).</li>
	 * <li>Additional information must be serializable.</li>
	 * </ul>
	 * @param queryIds specified set of identifiers (IDs). Such IDs can be user IDs or item IDs.
	 * @return estimation request based on specified recommendation parameter and specified set of identifiers (IDs).
	 */
	Request createEstimateRequest(RecommendParam param, Set<Integer> queryIds);
	
	
	/**
	 * Creating recommendation request based on the specified recommendation parameter and the specified maximum number of recommended items.
	 * This request asks the Hudup server (recommendation server) about producing a list of recommended items.
	 * Method {@code recommend(...)} of {@code Recommender} class is responsible for producing such list.
	 * @param param specified recommendation parameter. Recommendation parameter  contains important information as follows:
	 * <ul>
	 * <li>Initial rating vector which can be user rating vector or item rating vector.</li>
	 * <li>A profile which can be user profile containing user information (ID, name, etc.) or item profile containing item information (ID, name, price, etc.).</li>
	 * <li>A context list relevant to how and where user (s) rates (rate) on item (s).</li>
	 * <li>Additional information must be serializable.</li>
	 * </ul>
	 * @param maxRecommend specified maximum number of recommended items.
	 * @return recommendation request based on the specified recommendation parameter and the specified maximum number of recommended items.
	 */
	Request createRecommendRequest(RecommendParam param, int maxRecommend);
	
	
	/**
	 * Creating recommendation request for the specified user.
	 * This request asks the Hudup server (recommendation server) about producing a list of recommended items for specified user.
	 * The maximum number of recommended items is specified the input parameter {@code maxRecommend}.
	 * Method {@code recommend(...)} of {@code Recommender} class is responsible for producing such list.
	 * @param userId identifier (ID) of specified user.
	 * @param maxRecommend maximum number of recommended items.
	 * @return recommendation request for the specified user.
	 */
	Request createRecommendUserRequest(int userId, int maxRecommend);

	
	/**
	 * This method is responsible for creating a request for generating a recommendlet, based on many input parameters.
	 * Recommendlet represented by {@code Recommendlet} class is a service or a small graphic user interface (GUI) that allows users to see and interact with a list of recommended items.
	 * Recommendlet is now deprecated.
	 * @param host specified host.
	 * @param port specified port.
	 * @param regName registered name of recommendlet.
	 * @param externalUserId specified external user identifier (user ID).
	 * @param externalItemId specified external item identifier (item ID).
	 * @param maxRecommend the maximum number of recommended items.
	 * @param rating rating value for updating.
	 * @return request for generating a recommendlet, based on many input parameters.
	 */
	Request createRecommendletRequest(
			String host,
			int port,
			String regName, 
			Serializable externalUserId, 
			Serializable externalItemId, 
			int maxRecommend, 
			Rating rating);
	
	
	/**
	 * Creating a request for inserting many ratings specified by rating vector into framework database.
	 * @param vRating specified rating vector.
	 * @return request for insert many ratings specified by rating vector into framework database.
	 */
	Request createInsertRatingRequest(RatingVector vRating);
	
	
	/**
	 * Creating a request for updating many ratings specified by rating vector in framework database.
	 * @param vRating specified rating vector.
	 * @return request for updating many ratings specified by rating vector in framework database.
	 */
	Request createUpdateRatingRequest(RatingVector vRating);
	
	
	/**
	 * Creating a request for deleting many ratings specified by rating vector in framework database.
	 * @param vRating specified rating vector.
	 * @return request for deleting many ratings specified by rating vector in framework database.
	 */
	Request createDeleteRatingRequest(RatingVector vRating);

	
	/**
	 * Creating a request for getting a list of user identifiers (user IDs) from framework database.
	 * @return request for getting a list of user identifiers (user IDs) from framework database.
	 */
	Request createGetUserIdsRequest();

	
	/**
	 * Creating a request for getting rating vector of a specified user from framework database.
	 * @param userId specified user identifier (user ID).
	 * @return request for getting rating vector of a specified user from framework database.
	 */
	Request createGetUserRatingRequest(int userId);
	
	
	/**
	 * Creating a request for getting all user rating vectors from framework database.
	 * @return request for getting all user rating vectors from framework database.
	 */
	Request createGetUserRatingsRequest();

	
	/**
	 * Creating a request for getting many ratings of a user from specified SQL statement.
	 * @param selectSql specified select SQL statement. SQL which is abbreviation of Structured Query Language is a formal language for accessing relation database.
	 * @return request for getting many ratings of a user from specified SQL statement.
	 */
	Request createGetUserRatingsSqlRequest(String selectSql);
	
	
	/**
	 * Creating a request for deleting ratings of the specified user.
	 * @param userId specified user identifier (user ID).
	 * @return request for deleting ratings of the specified user.
	 */
	Request createDeleteUserRatingRequest(int userId);
	
	
	/**
	 * Creating a request for getting profile of the specified user.
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * @param userId specified user identifier (user ID).
	 * @return request for getting profile of the specified user.
	 */
	Request createGetUserProfileRequest(int userId);
	
	
	/**
	 * Creating a request for getting profile of a user by external user identifier.
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * @param externalUserId specified user identifier (user ID) and such ID is stored in outside database different from the Hudup database.
	 * @return request for getting profile of a user by external user identifier.
	 */
	Request createGetUserProfileByExternalRequest(Serializable externalUserId);

	
	/**
	 * Creating a request for getting profiles of all users.
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * @return request for getting profiles of all users.
	 */
	Request createGetUserProfilesRequest();

	
	/**
	 * Creating a request for getting profiles of users by specified SQL statement.
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * @param selectSql specified select SQL. SQL which is abbreviation of Structured Query Language is a formal language for accessing relation database.
	 * @return {@link Request}
	 */
	Request createGetUserProfilesSqlRequest(String selectSql);

	
	/**
	 * Creating a request for getting attribute list of user profiles.
	 * Note, attribute list contains many attributes. Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type.
	 * {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * Profile uses attribute to specify its data types and so profile owns a reference to an attribute list.
	 * Please see {@code AttributeList} class for details about attribute list.
	 * All profiles of users share the same attribute list.
	 * @return request for getting attribute list of user profile.
	 */
	Request createGetUserAttributeListRequest();
	
	
	/**
	 * Creating a request for inserting user profile based on specified profile and external record.
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * External record represented by {@code ExternalRecord} class is a record (a row in table) which is stored in other database (different from the framework database). It is opposite to internal record represented by {@code InternalRecord} class which is a record stored in the framework database.
	 * @param profile specified user profile.
	 * @param externalRecord specified external record.
	 * @return request for inserting user profile based on specified profile and external record.
	 */
	Request createInsertUserProfileRequest(Profile profile, ExternalRecord externalRecord);
	

	/**
	 * Creating a request for updating specified user profile.
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * @param profile specified user profile.
	 * @return request for updating specified user profile.
	 */
	Request createUpdateUserProfileRequest(Profile profile);

	
	/**
	 * Creating a request for deleting profile of specified user.
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * @param userId specified user identifier (user ID).
	 * @return request for deleting profile of specified user.
	 */
	Request createDeleteUserProfileRequest(int userId);
	
	
	/**
	 * Creating a request for getting external record of a given user by her/his identifier (user ID).
	 * Note, external record represented by {@code ExternalRecord} class is a record (a row in table) which is stored in other database (different from the framework database). It is opposite to internal record represented by {@code InternalRecord} class which is a record stored in the framework database.
	 * @param userId specified user identifier (user ID).
	 * @return request for getting external record of a given user by her/his identifier (user ID).
	 */
	Request createGetUserExternalRecordRequest(int userId);

	
	/**
	 * Creating a request for getting all item identifiers (item IDs).
	 * @return request for getting all item identifiers (item IDs).
	 */
	Request createGetItemIdsRequest();

	
	/**
	 * Creating a request for getting ratings of specified item.
	 * @param itemId specified item identifier (item ID).
	 * @return request for getting ratings of specified item.
	 */
	Request createGetItemRatingRequest(int itemId);
	
	
	/**
	 * Creating a request for getting all ratings of items.
	 * @return request for getting all ratings of items.
	 */
	Request createGetItemRatingsRequest();

	
	/**
	 * Creating a request for ratings of an item by SQL statement.
	 * @param selectSql specified select SQL statement. SQL which is abbreviation of Structured Query Language is a formal language for accessing relation database.
	 * @return request for ratings of an item by SQL statement.
	 */
	Request createGetItemRatingsSqlRequest(String selectSql);
	
	
	/**
	 * Creating a request for deleting ratings of a specified item.
	 * @param itemId specified item identifier (item ID).
	 * @return request for deleting ratings of a specified item.
	 */
	Request createDeleteItemRatingRequest(int itemId);
	
	
	/**
	 * Creating a request for getting profile of specified item.
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * @param itemId specified item identifier (item ID).
	 * @return request for getting profile of specified item.
	 */
	Request createGetItemProfileRequest(int itemId);

	
	/**
	 * Creating a request for getting profile of an item by external item identifier (item ID).
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * @param externalItemId specified item identifier (item ID) and such ID is stored in outside database different from the Hudup database.
	 * @return request for getting profile of an item by external item identifier (item ID).
	 */
	Request createGetItemProfileByExternalRequest(Serializable externalItemId);

	
	/**
	 * Creating a request for getting profiles of items.
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * @return request for getting profiles of items.
	 */
	Request createGetItemProfilesRequest();

	
	/**
	 * Creating a request for getting profiles of items by SQL statement.
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * @param selectSql specified select SQL statement. SQL which is abbreviation of Structured Query Language is a formal language for accessing relation database.
	 * @return request for getting profiles of items by SQL statement.
	 */
	Request createGetItemProfilesSqlRequest(String selectSql);

	
	/**
	 * Creating a request for getting attribute list of items.
	 * Note, attribute list contains many attributes. Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type.
	 * {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * Profile uses attribute to specify its data types and so profile owns a reference to an attribute list.
	 * Note, all item profiles in framework database share the same attribute list.
	 * Please see {@code AttributeList} class for details about attribute list.
	 * @return request for getting attribute list of items.
	 */
	Request createGetItemAttributeListRequest();
	
	
	/**
	 * Creating a request for inserting a specified item profile together with a specified external record.
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * External record represented by {@code ExternalRecord} class is a record (a row in table) which is stored in other database (different from the framework database). It is opposite to internal record represented by {@code InternalRecord} class which is a record stored in the framework database.
	 * @param profile specified item profile.
	 * @param externalRecord specified external record.
	 * @return request for inserting a specified item profile together with a specified external record.
	 */
	Request createInsertItemProfileRequest(Profile profile, ExternalRecord externalRecord);
	
	
	/**
	 * Creating a request for updating a specified item profile.
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * @param profile specified item profile.
	 * @return request for updating a specified item profile.
	 */
	Request createUpdateItemProfileRequest(Profile profile);

	
	/**
	 * Creating a request for deleting a specified item profile.
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * @param itemId specified item identifier (item ID).
	 * @return request for deleting a specified item profile.
	 */
	Request createDeleteItemProfileRequest(int itemId);
	
	
	/**
	 * Creating a request for getting external record of a specified item.
	 * Note, external record represented by {@code ExternalRecord} class is a record (a row in table) which is stored in other database (different from the framework database). It is opposite to internal record represented by {@code InternalRecord} class which is a record stored in the framework database.
	 * @param itemId specified item identifier (item ID).
	 * @return request for getting external record of a specified item.
	 */
	Request createGetItemExternalRecordRequest(int itemId);

	
	/**
	 * Creating a request for getting nominal (s) by specified unit and attribute.
	 * Nominal data indicates discrete and non-number data such as weekdays {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday}. Nominal data is called {@code nominal}, in brief, represented by {@code Nominal} class.
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type.
	 * If unit is database table, an attribute is identical to a field.
	 * @param unit specified unit name.
	 * @param attribute specified attribute name.
	 * @return request for getting nominal (s) by specified unit and attribute.
	 */
	Request createGetNominalRequest(String unit, String attribute);
	
	
	/**
	 * Creating a request for inserting a specified nominal into specified unit with specified attribute.
	 * Nominal data indicates discrete and non-number data such as weekdays {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday}. Nominal data is called {@code nominal}, in brief, represented by {@code Nominal} class.
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type.
	 * If unit is database table, an attribute is identical to a field.
	 * @param unit specified unit name.
	 * @param attribute specified attribute name.
	 * @param nominal specified nominal.
	 * @return request for inserting a specified nominal into specified unit with specified attribute.
	 */
	Request createInsertNominalRequest(String unit, String attribute, Nominal nominal);

	
	/**
	 * Creating a request for updating a specified nominal into specified unit with specified attribute.
	 * Nominal data indicates discrete and non-number data such as weekdays {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday}. Nominal data is called {@code nominal}, in brief, represented by {@code Nominal} class.
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type.
	 * If unit is database table, an attribute is identical to a field.
	 * @param unit specified unit name.
	 * @param attribute specified attribute name.
	 * @param nominal specified nominal.
	 * @return request for updating a specified nominal into specified unit with specified attribute.
	 */
	Request createUpdateNominalRequest(String unit, String attribute, Nominal nominal);
	
	
	/**
	 * Creating a request for deleting a nominal by specified unit and specified attribute.
	 * Nominal data indicates discrete and non-number data such as weekdays {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday}. Nominal data is called {@code nominal}, in brief, represented by {@code Nominal} class.
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type.
	 * If unit is database table, an attribute is identical to a field.
	 * @param unit specified unit name.
	 * @param attribute specified attribute name.
	 * @return request for deleting a nominal by specified unit and specified attribute.
	 */
	Request createDeleteNominalRequest(String unit, String attribute);
	
	
	/**
	 * Creating a request for getting an external record from a specified internal record.
	 * Note, external record represented by {@code ExternalRecord} class is a record (a row in table) which is stored in other database (different from the framework database). It is opposite to internal record represented by {@code InternalRecord} class which is a record stored in the framework database.
	 * @param internalRecord specified internal record.
	 * @return request for getting an external record from a specified internal record.
	 */
	Request createGetExternalRecordRequest(InternalRecord internalRecord);

	
	/**
	 * Creating a request for inserting a specified external record with a specified internal record.
	 * Note, external record represented by {@code ExternalRecord} class is a record (a row in table) which is stored in other database (different from the framework database). It is opposite to internal record represented by {@code InternalRecord} class which is a record stored in the framework database.
	 * @param internalRecord specified internal record.
	 * @param externalRecord specified external record.
	 * @return request for inserting a specified external record with a specified internal record.
	 */
	Request createInsertExternalRecordRequest(InternalRecord internalRecord, ExternalRecord externalRecord);

	
	/**
	 * Creating a request for updating a specified external record with a specified internal record.
	 * Note, external record represented by {@code ExternalRecord} class is a record (a row in table) which is stored in other database (different from the framework database). It is opposite to internal record represented by {@code InternalRecord} class which is a record stored in the framework database.
	 * @param internalRecord specified internal record.
	 * @param externalRecord specified external record.
	 * @return updating a specified external record with a specified internal record.
	 */
	Request createUpdateExternalRecordRequest(InternalRecord internalRecord, ExternalRecord externalRecord);

	
	/**
	 * Creating a request for deleting a specified external record with a specified internal record.
	 * Note, external record represented by {@code ExternalRecord} class is a record (a row in table) which is stored in other database (different from the framework database). It is opposite to internal record represented by {@code InternalRecord} class which is a record stored in the framework database.
	 * @param internalRecord specified internal record.
	 * @return request for deleting a specified external record with a specified internal record.
	 */
	Request createDeleteExternalRecordRequest(InternalRecord internalRecord);
	
	
	/**
	 * Creating a request for checking whether an account is valid with regard to specified password and specified privileges.
	 * Note, account is the information of a user who has access to Hudup server with her/his privileges. Account is modeled and stored in framework database as profile. 
	 * Account is the information of a user who has access to the server with her/his privileges. Account is modeled and stored in framework database as profile. 
	 * @param account specified account.
	 * @param password specified password.
	 * @param privileges specified privileges.
	 * @return request for checking whether an account is valid with regard to specified password and specified privileges.
	 */
	Request createValidateAccountRequest(String account, String password, int privileges);
	
	
	/**
	 * Creating a request for getting a profile by specified unit and specified condition.
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * @param unit specified unit name.
	 * @param condition specified condition to select profile to be returned, please see the {@code net.hudup.core.data.Condition}.
	 * @return {@link Request}
	 */
	Request createGetProfileRequest(String unit, Profile condition);
	
	
	/**
	 * Creating a request for getting profiles by SQL statement.
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * @param selectSql specified select SQL statement. SQL which is abbreviation of Structured Query Language is a formal language for accessing relation database.
	 * @return request for getting profiles by SQL statement.
	 */
	Request createGetProfilesSqlRequest(String selectSql);

	
	/**
	 * Creating a request for getting the attribute list of profiles in the same specified unit.
	 * Note, attribute list contains many attributes. Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type.
	 * {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * Profile uses attribute to specify its data types and so profile owns a reference to an attribute list.
	 * Please see {@code AttributeList} class for details about attribute list.
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * Profiles in the same unit have the same attribute list.
	 * @param unit specified unit name.
	 * @return {@link Request}
	 */
	Request createGetProfileAttributeListRequest(String unit);
	
	
	/**
	 * Creating a request for inserting a specified profile in a specified unit.
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * @param unit specified unit name.
	 * @param profile specified profile.
	 * @return request for inserting a specified profile in a specified unit.
	 */
	Request createInsertProfileRequest(String unit, Profile profile);

	
	/**
	 * Creating a request for updating a specified profile in a specified unit.
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * @param unit specified unit name.
	 * @param profile specified profile.
	 * @return request for updating a specified profile in a specified unit.
	 */
	Request createUpdateProfileRequest(String unit, Profile profile);
	
	
	/**
	 * Creating a request for deleting a specified profile in a specified unit.
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * @param unit specified unit name.
	 * @param condition specified condition to select profile to be deleted, please see the {@code net.hudup.core.data.Condition}.
	 * @return request for deleting a specified profile in a specified unit.
	 */
	Request createDeleteProfileRequest(String unit, Profile condition);
	
	
	/**
	 * Hudup server owns a configuration which includes some server information such as server port, the period in miliseconds that the Hudup server does periodically internal tasks.
	 * This method creates a request for retrieving such server configuration.
	 * @return request for retrieving server configuration.
	 */
	Request createGetServerConfigRequest();
	
	
	/**
	 * Creating a request for retrieving an attribute having the specified name of the current session in server.
	 * Hudup server (recommendation server) serves all incoming request in a time interval (a period in mili-seconds).
	 * Such time interval is called session. Each session has a number of attributes. Each attribute is a pair of key and value. Key is also called name.
	 * When it is time-out which means that the server has received no request after the time interval then, the session is destroyed, which causes that all attributes are destroyed.
	 * @param attribute specified attribute name.
	 * @return request for retrieving an attribute having the specified name of the current session in server.
	 */
	Request createGetSessionAttributeRequest(String attribute);
	
	
	/**
	 * Creating a request for retrieving an image of dataset owned by Hudup server at current time point.
	 * Such image is snapshot stored in memory. Snapshot is represented by {@code Snapshot} class.
	 * @return a request for retrieving an image (called snapshot) of dataset owned by Hudup server
	 */
	Request createGetSnapshotRequest();


}
