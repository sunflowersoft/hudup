package net.hudup.core.client;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.core.alg.RecommendParam;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.ExternalRecord;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.InternalRecord;
import net.hudup.core.data.Nominal;
import net.hudup.core.data.NominalList;
import net.hudup.core.data.Profile;
import net.hudup.core.data.Rating;
import net.hudup.core.data.RatingVector;
import net.hudup.core.data.Snapshot;

/**
 * Service interface specifies methods to serve user requests. These methods focus on providing recommendation, inserting, updating and getting information such as user ratings, user profiles, and item profiles stored in database.
 * Service extends {@link Remote}, which means that service supports RMI (abbreviation of Java Remote Method Invocation) for remote interaction in client-server architecture. The tutorial of RMI is available at <a href="https://docs.oracle.com/javase/tutorial/rmi">https://docs.oracle.com/javase/tutorial/rmi</a>.
 * In the architecture of Hudup server, the service layer is mainly composed of recommendation service and storage service.
 * Recommendation service is responsible to use concrete recommendation algorithms ({@code Recommender}) to produce a list of recommended items according to user request.
 * Storage service allows users to retrieve and update information from/to database. Storage service also creates {@link Snapshot} and {@code Scanner} which are image of dataset at certain time point.
 * However, in the current implementation, these two services are merged into this {@code service} interface whose methods are of both recommendation service and storage service.
 * <br>
 * Programmers are responsible for implementing this service.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface Service extends Remote {
	
	
	/**
	 * Serve to estimate rating values of specified querying identifiers (IDs).	Such IDs can be user IDs or item IDs.
	 * Method {@code estimate(...)} of {@code Recommender} class is responsible to do estimation task really. 
	 * @param param specified recommendation parameter represented by {@link RecommendParam}. Recommendation parameter  contains important information as follows:
	 * <ul>
	 * <li>Initial rating vector which can be user rating vector or item rating vector.</li>
	 * <li>A profile which can be user profile containing user information (ID, name, etc.) or item profile containing item information (ID, name, price, etc.).</li>
	 * <li>A context list relevant to how and where user (s) rates (rate) on item (s).</li>
	 * <li>Additional information must be serializable.</li>
	 * </ul>
	 * Note, rating vector represented {@link RatingVector} contains ratings.
	 * If rating vector is user rating vector, it contains all ratings of the same user on many items.
	 * If rating vector is item rating vector, it contains all ratings of many users on the same item. 
	 * @param queryIds specified set of identifiers (IDs). Such IDs can be user IDs or item IDs.
	 * @return rating vector containing ratings of specified IDs.
	 * @throws RemoteException if any error raises.
	 */
	RatingVector estimate(RecommendParam param, Set<Integer> queryIds) 
			throws RemoteException;

	
	/**
	 * Serve to produce a list of recommended items based on specified recommendation parameter and the specified maximum number of recommended items.
	 * @param param specified recommendation parameter represented by {@link RecommendParam}. Recommendation parameter  contains important information as follows:
	 * <ul>
	 * <li>Initial rating vector which can be user rating vector or item rating vector.</li>
	 * <li>A profile which can be user profile containing user information (ID, name, etc.) or item profile containing item information (ID, name, price, etc.).</li>
	 * <li>A context list relevant to how and where user (s) rates (rate) on item (s).</li>
	 * <li>Additional information must be serializable.</li>
	 * </ul>
	 * Note, rating vector represented {@link RatingVector} contains ratings.
	 * If rating vector is user rating vector, it contains all ratings of the same user on many items.
	 * If rating vector is item rating vector, it contains all ratings of many users on the same item. 
	 * @param maxRecommend specified maximum number of recommended items.
	 * @return rating vector represented by {@link RatingVector}, which contains recommended items together their estimated rating values.
	 * @throws RemoteException if any error raises.
	 */
	RatingVector recommend(RecommendParam param, int maxRecommend) 
			throws RemoteException;
	
	
	/**
	 * Serve to produce a list of recommended items for a specified user.
	 * Note, rating vector represented {@link RatingVector} contains ratings.
	 * If rating vector is user rating vector, it contains all ratings of the same user on many items.
	 * If rating vector is item rating vector, it contains all ratings of many users on the same item. 
	 * @param userId specified user identifier (user ID).
	 * @param maxRecommend specified maximum number of recommended items.
	 * @return rating vector represented by {@link RatingVector} for specified user, which contains recommended items together their estimated rating values.
	 * @throws RemoteException if any error raises.
	 */
	RatingVector recommend(int userId, int maxRecommend) 
			throws RemoteException;
	
	
	/**
	 * Serve to generate a recommendlet, based on many input parameters.
	 * Recommendlet represented by {@link Recommendlet} class is a service or a small graphic user interface (GUI) that allows users to see and interact with a list of recommended items.
	 * Recommendlet is now deprecated.
	 * @param listenerHost specified listener host.
	 * @param listenerPort specified listener port.
	 * @param regName registered name of recommendlet.
	 * @param externalUserId specified external user identifier (user ID).
	 * @param externalItemId specified external item identifier (item ID).
	 * @param maxRecommend the maximum number of recommended items.
	 * @param rating rating value for updating.
	 * @return recommendlet represented by {@link Recommendlet} class is a service or a small graphic user interface (GUI) that allows users to see and interact with a list of recommended items.
	 * @throws RemoteException if any error raises.
	 */
	@Deprecated
	Recommendlet recommend(
			String listenerHost,
			int listenerPort,
			String regName, 
			Serializable externalUserId, 
			Serializable externalItemId, 
			int maxRecommend, 
			Rating rating) throws RemoteException;
	
	
	/**
	 * Updating many ratings specified by rating vector in framework database.
	 * If such ratings are not existent in database, they are inserted into database.
	 * Note, rating vector represented {@link RatingVector} contains ratings.
	 * If rating vector is user rating vector, it contains all ratings of the same user on many items.
	 * If rating vector is item rating vector, it contains all ratings of many users on the same item. 
	 * @param rating specified rating vector represented by {@link RatingVector} class.
	 * @return whether update successfully
	 * @throws RemoteException if any error raises.
	 */
	boolean updateRating(RatingVector rating) throws RemoteException;
	
	
	/**
	 * Updating a rating that a user gives on an item. If such rating does not exists, it is inserted into database. 
	 * @param userId specified user identifier (user ID).
	 * @param itemId specified item identifier (item ID).
	 * @param rating specified rating.
	 * @return whether update successfully.
	 * @throws RemoteException if any error raises.
	 */
	boolean updateRating(int userId, int itemId, Rating rating) throws RemoteException;
	
	
	/**
	 * Deleting many ratings in the specified rating vector from framework database.
	 * Note, rating vector represented {@link RatingVector} contains ratings.
	 * If rating vector is user rating vector, it contains all ratings of the same user on many items.
	 * If rating vector is item rating vector, it contains all ratings of many users on the same item. 
	 * @param rating specified rating vector that contains ratings which are removed from framework database.
	 * @return whether delete successfully.
	 * @throws RemoteException if any error raises.
	 */
	boolean deleteRating(RatingVector rating) throws RemoteException;

	
	/**
	 * Getting user identifiers (user IDs) from framework database. Such user IDs are retrieved via fetcher.
	 * Fetcher is the interface for iterating each element of an associated collection. 
	 * @return {@link Fetcher} of user id (s).
	 * @throws RemoteException if any error raises.
	 */
	Fetcher<Integer> getUserIds() throws RemoteException;
	
	
	/**
	 * Getting rating vector of a specified user from framework database.
	 * Note, rating vector represented {@link RatingVector} contains ratings.
	 * If rating vector is user rating vector, it contains all ratings of the same user on many items.
	 * If rating vector is item rating vector, it contains all ratings of many users on the same item. 
	 * @param userId specified user identifier (user ID).
	 * @return rating vector of a specified user from framework database.
	 * @throws RemoteException if any error raises.
	 */
	RatingVector getUserRating(int userId) throws RemoteException;
	
	
	/**
	 * Getting all user rating vectors from framework database.
	 * Fetcher is the interface for iterating each element of an associated collection. 
	 * Note, rating vector represented {@link RatingVector} contains ratings.
	 * If rating vector is user rating vector, it contains all ratings of the same user on many items.
	 * If rating vector is item rating vector, it contains all ratings of many users on the same item. 
	 * @return {@link Fetcher} of rating vectors.
	 * @throws RemoteException if any error raises.
	 */
	Fetcher<RatingVector> getUserRatings() throws RemoteException;

	
	/**
	 * Deleting ratings of the specified user.
	 * @param userId specified user identifier (user ID).
	 * @return whether delete successfully.
	 * @throws RemoteException if any error raises.
	 */
	boolean deleteUserRating(int userId) throws RemoteException;
	
	
	/**
	 * Getting profile of the specified user.
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * @param userId specified user identifier (user ID).
	 * @return profile of the specified user.
	 * @throws RemoteException if any error raises.
	 */
	Profile getUserProfile(int userId) throws RemoteException;
	
	
	/**
	 * Getting profile of a user by external user identifier.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * @param externalUserId specified user identifier (user ID) and such ID is stored in outside database different from the Hudup database.
	 * @return Profile of a user by external user identifier.
	 * @throws RemoteException if any error raises.
	 */
	Profile getUserProfileByExternal(Serializable externalUserId) throws RemoteException;

	
	/**
	 * Getting profiles of all users. Such user profiles are retrieved via fetcher.
	 * Fetcher is the interface for iterating each element of an associated collection. 
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * @return {@link Fetcher} of user profiles.
	 * @throws RemoteException if any error raises.
	 */
	Fetcher<Profile> getUserProfiles() throws RemoteException;

	
	/**
	 * Updating specified user profile in framework database. If such user profile does not exist in database, it will be inserted into database.
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * @param user specified user profile.
	 * @return whether update successfully
	 * @throws RemoteException if any error raises.
	 */
	boolean updateUserProfile(Profile user) throws RemoteException;
	
	
	/**
	 * Deleting profile of specified user.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * @param userId specified user identifier (user ID).
	 * @return whether delete successfully.
	 * @throws RemoteException if any error raises.
	 */
	boolean deleteUserProfile(int userId) throws RemoteException;
	
	
	/**
	 * Getting attribute list of user profiles.
	 * Note, attribute list contains many attributes. Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type.
	 * {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * Profile uses attribute to specify its data types and so profile owns a reference to an attribute list.
	 * Please see {@code AttributeList} class for details about attribute list.
	 * All profiles of users share the same attribute list.
	 * @return attribute list of user profiles, represented by {@link AttributeList}.
	 * @throws RemoteException if any error raises.
	 */
	AttributeList getUserAttributeList() throws RemoteException;
	
	
	/**
	 * Getting external record of a given user by her/his identifier (user ID).
	 * Note, external record represented by {@code ExternalRecord} class is a record (a row in table) which is stored in other database (different from the framework database). It is opposite to internal record represented by {@code InternalRecord} class which is a record stored in the framework database.
	 * @param userId specified user identifier (user ID).
	 * @return external record of a given user by her/his identifier (user ID).
	 * @throws RemoteException if any error raises.
	 */
	ExternalRecord getUserExternalRecord(int userId) throws RemoteException;
	
	
	/**
	 * Getting all item identifiers (item IDs). These item IDs are retrieved via fetcher.
	 * Fetcher is the interface for iterating each element of an associated collection. 
	 * @return {@link Fetcher} of item id (s).
	 * @throws RemoteException if any error raises.
	 */
	Fetcher<Integer> getItemIds() throws RemoteException;

	
	/**
	 * Getting ratings of specified item, as a rating vector.
	 * Note, rating vector represented {@link RatingVector} contains ratings.
	 * If rating vector is user rating vector, it contains all ratings of the same user on many items.
	 * If rating vector is item rating vector, it contains all ratings of many users on the same item. 
	 * @param itemId specified item identifier (item ID).
	 * @return ratings of specified item, as a rating vector.
	 * @throws RemoteException if any error raises.
	 */
	RatingVector getItemRating(int itemId) throws RemoteException;

	
	/**
	 * Getting all item rating vectors. Each item rating vector contains ratings of many users on the same item. These item rating vectors are retrieved via fetcher.
	 * Fetcher is the interface for iterating each element of an associated collection. 
	 * Note, rating vector represented {@link RatingVector} contains ratings.
	 * If rating vector is user rating vector, it contains all ratings of the same user on many items.
	 * If rating vector is item rating vector, it contains all ratings of many users on the same item. 
	 * @return {@link Fetcher} of item rating vectors.
	 * @throws RemoteException if any error raises.
	 */
	Fetcher<RatingVector> getItemRatings() throws RemoteException;

	
	/**
	 * Deleting ratings of a specified item.
	 * @param itemId specified item identifier (item ID).
	 * @return whether delete successfully.
	 * @throws RemoteException if any error raises.
	 */
	boolean deleteItemRating(int itemId) throws RemoteException;

	
	/**
	 * Getting profile of specified item.
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * @param itemId specified item identifier (item ID).
	 * @return {@link Profile} of specified item.
	 * @throws RemoteException if any error raises.
	 */
	Profile getItemProfile(int itemId) throws RemoteException;
	
	
	/**
	 * Getting profile of an item by external item identifier (item ID).
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * @param externalItemId specified item identifier (item ID) and such ID is stored in outside database different from the Hudup database.
	 * @return profile of an item by external item identifier (item ID).
	 * @throws RemoteException if any error raises.
	 */
	Profile getItemProfileByExternal(Serializable externalItemId) throws RemoteException;

	
	/**
	 * Getting profiles of items. These profiles are retrieved via fetcher.
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * Fetcher is the interface for iterating each element of an associated collection. 
	 * @return {@link Fetcher} of item profiles.
	 * @throws RemoteException if any error raises.
	 */
	Fetcher<Profile> getItemProfiles() throws RemoteException;

	
	/**
	 * Updating a specified item profile. If such profile does not exist in database, it will be inserted into database.
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * @param item specified item profile.
	 * @return whether update successfully.
	 * @throws RemoteException if any error raises.
	 */
	boolean updateItemProfile(Profile item) throws RemoteException;
	
	
	/**
	 * Deleting a specified item profile.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * @param itemId specified item identifier (item ID).
	 * @return whether delete successfully.
	 * @throws RemoteException if any error raises.
	 */
	boolean deleteItemProfile(int itemId) throws RemoteException;

	
	/**
	 * Getting attribute list of items.
	 * Note, attribute list contains many attributes. Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type.
	 * {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * Profile uses attribute to specify its data types and so profile owns a reference to an attribute list.
	 * Note, all item profiles in framework database share the same attribute list.
	 * Please see {@code AttributeList} class for details about attribute list.
	 * @return attribute list of items.
	 * @throws RemoteException if any error raises.
	 */
	AttributeList getItemAttributeList() throws RemoteException;
	
	
	/**
	 * Getting external record of a specified item.
	 * Note, external record represented by {@code ExternalRecord} class is a record (a row in table) which is stored in other database (different from the framework database). It is opposite to internal record represented by {@code InternalRecord} class which is a record stored in the framework database.
	 * @param itemId specified item identifier (item ID).
	 * @return external record of a specified item.
	 * @throws RemoteException if any error raises.
	 */
	ExternalRecord getItemExternalRecord(int itemId) throws RemoteException;
	
	
	/**
	 * Getting nominal list by specified unit and attribute. Nominal list is a list of many nominal (s), specified by {@link NominalList}.
	 * Nominal data indicates discrete and non-number data such as weekdays {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday}. Nominal data is called {@code nominal}, in brief, represented by {@code Nominal} class.
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type.
	 * If unit is database table, an attribute is identical to a field.
	 * @param unitName specified unit name.
	 * @param attribute specified attribute name.
	 * @return nominal list by specified unit and attribute.
	 * @throws RemoteException if any error raises.
	 */
	NominalList getNominal(String unitName, String attribute)  throws RemoteException;
	
	
	/**
	 * Updating a specified nominal into specified unit with specified attribute. If such nominal does not exist in database, it is inserted into database.
	 * Nominal data indicates discrete and non-number data such as weekdays {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday}. Nominal data is called {@code nominal}, in brief, represented by {@code Nominal} class.
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type.
	 * If unit is database table, an attribute is identical to a field.
	 * @param unitName specified unit name.
	 * @param attribute specified attribute name.
	 * @param nominal specified nominal.
	 * @return whether update successfully
	 * @throws RemoteException if any error raises.
	 */
	boolean updateNominal(String unitName, String attribute, Nominal nominal) throws RemoteException;

	
	/**
	 * Deleting a nominal by specified unit and specified attribute.
	 * Nominal data indicates discrete and non-number data such as weekdays {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday}. Nominal data is called {@code nominal}, in brief, represented by {@code Nominal} class.
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type.
	 * If unit is database table, an attribute is identical to a field.
	 * @param unitName specified unit name.
	 * @param attribute specified attribute name.
	 * @return whether delete successfully.
	 * @throws RemoteException if any error raises.
	 */
	boolean deleteNominal(String unitName, String attribute) throws RemoteException;
	
	
	/**
	 * Getting an external record from a specified internal record.
	 * Note, external record represented by {@code ExternalRecord} class is a record (a row in table) which is stored in other database (different from the framework database). It is opposite to internal record represented by {@code InternalRecord} class which is a record stored in the framework database.
	 * @param internalRecord specified internal record.
	 * @return external record from a specified internal record.
	 * @throws RemoteException if any error raises.
	 */
	ExternalRecord getExternalRecord(InternalRecord internalRecord) throws RemoteException;
	
	
	/**
	 * Updating a specified external record with a specified internal record. If such external record does not exist in database, it is inserted into database.
	 * Note, external record represented by {@code ExternalRecord} class is a record (a row in table) which is stored in other database (different from the framework database). It is opposite to internal record represented by {@code InternalRecord} class which is a record stored in the framework database.
	 * @param internalRecord specified internal record.
	 * @param externalRecord specified external record.
	 * @return whether update successfully
	 * @throws RemoteException if any error raises.
	 */
	boolean updateExternalRecord(InternalRecord internalRecord, ExternalRecord externalRecord) throws RemoteException;

	
	/**
	 * Deleting a specified external record with a specified internal record.
	 * Note, external record represented by {@code ExternalRecord} class is a record (a row in table) which is stored in other database (different from the framework database). It is opposite to internal record represented by {@code InternalRecord} class which is a record stored in the framework database.
	 * @param internalRecord specified internal record.
	 * @return whether delete successfully
	 * @throws RemoteException if any error raises.
	 */
	boolean deleteExternalRecord(InternalRecord internalRecord) throws RemoteException;

	
	/**
	 * Checking whether an account is valid with regard to specified password and specified privileges.
	 * Note, account is the information of a user who has access to Hudup server with her/his privileges. Account is modeled and stored in framework database as profile. 
	 * Account is the information of a user who has access to the server with her/his privileges. Account is modeled and stored in framework database as profile. 
	 * @param account specified account.
	 * @param password specified password.
	 * @param privileges specified privileges.
	 * @return whether account is valid.
	 * @throws RemoteException if any error raises.
	 */
	boolean validateAccount(String account, String password, int privileges)
		throws RemoteException ;
	
	
	/**
	 * Getting a profile by specified unit and specified condition.
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * @param profileUnit specified unit name.
	 * @param condition specified condition to select profile to be returned, please see the {@code net.hudup.core.data.Condition}.
	 * @return profile by specified unit and specified condition.
	 * @throws RemoteException if any error raises.
	 */
	Profile getProfile(String profileUnit, Profile condition) throws RemoteException;
	
	
	/**
	 * Getting the attribute list of profiles in the same specified unit.
	 * Note, attribute list contains many attributes. Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type.
	 * {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * Profile uses attribute to specify its data types and so profile owns a reference to an attribute list.
	 * Please see {@code AttributeList} class for details about attribute list.
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * Profiles in the same unit have the same attribute list.
	 * @param unitName specified unit name.
	 * @return attribute list of profiles in the same specified unit.
	 * @throws RemoteException if any error raises.
	 */
	AttributeList getProfileAttributeList(String unitName) throws RemoteException;

	
	/**
	 * Updating a specified profile in a specified unit. If such profile does not exist, it is inserted into such unit.
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * @param profileUnit specified unit name.
	 * @param profile specified profile.
	 * @return whether update successfully
	 * @throws RemoteException if any error raises.
	 */
	boolean updateProfile(String profileUnit, Profile profile) 
			throws RemoteException;
	
	
	/**
	 * Deleting a specified profile in a specified unit.
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * @param profileUnit specified unit name.
	 * @param condition specified condition to select profile to be deleted, please see the {@code net.hudup.core.data.Condition}.
	 * @return whether delete successfully.
	 * @throws RemoteException if any error raises.
	 */
	boolean deleteProfile(String profileUnit, Profile condition)
			throws RemoteException ;
	
	
	/**
	 * Hudup server owns a configuration which includes some server information such as server port, the period in miliseconds that the Hudup server does periodically internal tasks.
	 * This method retrieves such server configuration.
	 * @return configuration of Hudup server, represented by {@link DataConfig}.
	 * @throws RemoteException if any error raises.
	 */
	DataConfig getServerConfig() throws RemoteException;
	
	
	/**
	 * Retrieving an image of dataset owned by Hudup server at current time point.
	 * Such image is snapshot stored in memory. Snapshot is represented by {@code Snapshot} class.
	 * @return {@link Snapshot} of dataset owned by Hudup server at current time point.
	 * @throws RemoteException if any error raises.
	 */
	Snapshot getSnapshot() throws RemoteException;
	

}
