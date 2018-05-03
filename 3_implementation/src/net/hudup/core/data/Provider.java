/**
 * 
 */
package net.hudup.core.data;

import java.io.Serializable;

import net.hudup.core.Cloneable;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.data.ctx.CTSManager;
import net.hudup.core.logistic.ui.ProgressListener;


/**
 * {@code Dataset} only provides read-only operations via &quot;get&quot; and &quot;fetch&quot; methods.
 * Thus, {@code Provider} interface and its implementations support storage service in service layer to update and modify database via writing operations.
 * {@code Provider} interacts directly with database, which is created in data layer.
 * For example, {@link #updateRating(RatingVector)} method saves rating values that user makes on items to database.
 * {@code Provider} also provides read-only accesses to database. So, in many situations, {@code Scanner} uses {@code Provider} to retrieve information from database because {@code Scanner} does not store information in memory.
 * <br>
 * Moreover, {@code Provider} manipulates context and context template via context template schema manager (CTS manager) represented by {@link CTSManager}.
 * {@code Provider}  provides most of read-write data operations (get, insert, update, delete).
 * Provider uses provider associator represented by {@link ProviderAssoc} for low-level data operators.
 * Every provider own a configuration represented by {@link DataConfig} for initialization (setting up).
 * <br>
 * In general, provider allows programmers to access all data objects stored in archives (files) of entire framework. These data objects constitute framework database including user profile, item profile, rating matrix, context template, context, etc.
 * Each archives (file) is called {@code unit} representing a CSV file, database table, Excel sheet, etc.
 * Such database has the schema. Please distinguish such framework database schema from context template schema because the framework schema contains template schema.  
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface Provider extends AutoCloseable, Cloneable, Serializable {

	
	/**
	 * {@code Provider} manipulates context and context template via context template schema manager (CTS manager) represented by {@link CTSManager}.
	 * This method returns the internal CTS manager of the provider. 
	 * @return {@link CTSManager}
	 */
	CTSManager getCTSManager();
	
	
	/**
	 * {@code Provider} is initialized (set up) based on the configuration.
	 * This method returns the configuration of provider.
	 * @return configuration of provider.
	 */
	DataConfig getConfig();

	
	/**
	 * Provider uses provider associator represented by {@link ProviderAssoc} for low-level data operators.
	 * The method returns the internal provider associator of this provider.
	 * @return internal {@link ProviderAssoc} of this provider.
	 */
	ProviderAssoc getAssoc();
	
	
	/**
	 * Each provider has a configuration and store such configuration in unit as profiles.
	 * This method returns the profile of built-in configuration as profile, given attribute..
	 * Each configuration profile has an associated attribute.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * @param att specified attribute.
	 * @return profile of built-in configuration, given specified attribute.
	 */
	Profile getConfigProfile(String att);
	
	
	/**
	 * Each provider has a configuration and store such configuration in unit as profiles.
	 * This method inserts a new entry into the configuration as profile.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * @param cfgProfile specified new entry of the configuration as profile.
	 * @return whether insert successfully
	 */
	boolean insertConfigProfile(Profile cfgProfile);
	
	
	/**
	 * Each provider has a configuration and store such configuration in unit as profiles.
	 * This method updates the current configuration profile by specified profile.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * @param cfgProfile specified configuration.
	 * @return whether update successfully
	 */
	boolean updateConfigProfile(Profile cfgProfile);
	
	
	/**
	 * Each provider has a configuration and store such configuration in unit as profiles.
	 * This method deletes a configuration profile having specified attribute name.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * @param cfgAttribute specified attribute name.
	 * @return whether delete successfully
	 */
	boolean deleteConfigProfile(String cfgAttribute);
	
	
	/**
	 * Retrieving all ratings from rating matrix of framework database via a fetcher. Such ratings are represented by rating triples.
	 * Note, fetcher is the interface for iterating each item of an associated collection.
	 * Rating triple contains the rating that a user gives on an item.
	 * @return {@link Fetcher} of {@link RatingTriple}.
	 */
	public Fetcher<RatingTriple> getRatings();
	
	
	/**
	 * Inserting a specified rating that a specified user gives on a specified item into rating matrix of framework database.
	 * @param userId specified user identification (user ID).
	 * @param itemId specified item identification (user ID).
	 * @param rating specified rating that a specified user gives on a specified item.
	 * @return whether inserting successfully.
	 */
	boolean insertRating(int userId, int itemId, Rating rating);
	

	/**
	 * Inserting ratings in the specified rating vector into rating matrix of framework database.
	 * Note, rating vector represented {@link RatingVector} contains ratings.
	 * If rating vector is user rating vector, it contains all ratings of the same user on many items.
	 * If rating vector is item rating vector, it contains all ratings of many users on the same item. 
	 * @param vRating specified rating vector.
	 * @return whether insert successfully.
	 */
	boolean insertRating(RatingVector vRating);

	
	/**
	 * Update a specified rating that a specified user gives on a specified item. 
	 * @param userId specified user identification (user ID).
	 * @param itemId specified item identification (item ID).
	 * @param rating specified rating that a specified user gives on a specified item.
	 * @return whether update successfully.
	 */
	boolean updateRating(int userId, int itemId, Rating rating);

	
	/**
	 * Updating ratings of the specified rating vector into rating matrix of framework database.
	 * Note, rating vector represented {@link RatingVector} contains ratings.
	 * If rating vector is user rating vector, it contains all ratings of the same user on many items.
	 * If rating vector is item rating vector, it contains all ratings of many users on the same item. 
	 * @param vRating specified rating vector.
	 * @return whether update successfully
	 */
	boolean updateRating(RatingVector vRating);
	
	
	/**
	 * Deleting ratings of specified rating vector into rating matrix of framework database.
	 * Note, rating vector represented {@link RatingVector} contains ratings.
	 * If rating vector is user rating vector, it contains all ratings of the same user on many items.
	 * If rating vector is item rating vector, it contains all ratings of many users on the same item. 
	 * @param vRating specified rating vector.
	 * @return whether delete successfully
	 */
	boolean deleteRating(RatingVector vRating);

	
	/**
	 * Getting a rating vector (a vector of ratings) of specified user.
	 * Note, rating vector represented {@link RatingVector} contains ratings.
	 * If rating vector is user rating vector, it contains all ratings of the same user on many items.
	 * If rating vector is item rating vector, it contains all ratings of many users on the same item. 
	 * @param userId specified user identification (user ID).
	 * @return rating vector (a vector of ratings) of specified user.
	 */
	RatingVector getUserRatingVector(int userId);
	
	
	/**
	 * Deleting all ratings of a specified user.
	 * @param userId specified user identification (user ID).
	 * @return whether delete successfully
	 */
	boolean deleteUserRating(int userId);
	
	
	/**
	 * Getting profile of a specified user.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * @param userId specified user identification (user ID).
	 * @return profile of a specified user.
	 */
	Profile getUserProfile(int userId);

	
	/**
	 * Getting attribute list of profiles of users in framework database. User profiles share the same list of attributes.
	 * Note, attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type. Attribute list represented by {@link AttributeList} is the list of many attributes. 
	 * {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. 
	 * @return the same {@link AttributeList} that shared by user profiles. 
	 */
	AttributeList getUserAttributes();
	
	
	/**
	 * Some user information is stored in outside database different from the framework database.
	 * This method is to used to retrieve such outside information via {@code interchanged attribute map} represented by {@link InterchangeAttributeMap} class.
	 * Note, {@code interchanged attribute map} includes both internal record and external record.
	 * {@code Internal record} represents a single-value record of a {@code unit} in framework database.
	 * {@code External record} represents a single-value record of a {@code unit} in other database (different from the framework database).
	 * There is a special unit (having name &quot;hdp_attribute_map&quot;, for example) for storing many {@code interchanged attribute maps}; 
	 * @param userId specified user identification (user ID).
	 * @return outside information of specified user, represented by {@link InterchangeAttributeMap} class.
	 */
	InterchangeAttributeMap getUserAttributeMap(int userId);
	
	
	/**
	 * Inserting specified user profile via specified external record.
	 * Note, {@code External record} represents a single-value record of a {@code unit} in other database (different from the framework database).
	 * {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * @param user specified user profile.
	 * @param externalRecord specified external record.
	 * @return whether insert successfully.
	 */
	boolean insertUserProfile(Profile user, ExternalRecord externalRecord);
	
	
	/**
	 * Updating specified user profile.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * @param user specified user profile.
	 * @return whether update successfully
	 */
	boolean updateUserProfile(Profile user);
	
	
	/**
	 * Deleting a user profile via specified user identification (user ID).
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * @param userId specified user identification (user ID).
	 * @return whether delete successfully
	 */
	boolean deleteUserProfile(int userId);
	
	
	/**
	 * Creating a recommendation parameter for a user identified by given user ID.
	 * This method reads framework database to receive ratings, list of contexts, user profile, and other information relevant to the specified user so that
	 * it creates a recommendation parameter for such user. The returned recommendation parameter is input for recommendation tasks; for example, it is input for methods {@code estimate(...)} and {@code recommend(...)} of {@code Recommender} class.
	 * @param userId specified user identification (user ID).
	 * @return recommendation parameter represented by {@link RecommendParam} class which is input for recommendation tasks.
	 */
	RecommendParam getRecommendParam(int userId);
	
	
	/**
	 * Getting a rating vector (a vector of ratings) of specified item.
	 * Note, rating vector represented {@link RatingVector} contains ratings.
	 * If rating vector is user rating vector, it contains all ratings of the same user on many items.
	 * If rating vector is item rating vector, it contains all ratings of many users on the same item. 
	 * @param itemId specified item identification (item ID).
	 * @return rating vector (a vector of ratings) of specified item.
	 */
	RatingVector getItemRatingVector(int itemId);
	
	
	/**
	 * Deleting all ratings of a specified item from framework database.
	 * @param itemId specified item identification (item ID).
	 * @return whether delete successfully
	 */
	boolean deleteItemRating(int itemId);

	
	/**
	 * Getting profile of a specified item.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * @param itemId specified item identification (user ID).
	 * @return profile of a specified user.
	 */
	Profile getItemProfile(int itemId);

	
	/**
	 * Getting attribute list of profiles of items in framework database. Item profiles share the same list of attributes.
	 * Note, attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type. Attribute list represented by {@link AttributeList} is the list of many attributes.
	 * {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. 
	 * @return the same {@link AttributeList} that shared by user profiles.
	 */
	AttributeList getItemAttributes();
	
	
	/**
	 * Some item information is stored in outside database different from the framework database.
	 * This method is to used to retrieve such outside information via {@code interchanged attribute map} represented by {@link InterchangeAttributeMap} class.
	 * Note, {@code interchanged attribute map} includes both internal record and external record.
	 * {@code Internal record} represents a single-value record of a {@code unit} in framework database.
	 * {@code External record} represents a single-value record of a {@code unit} in other database (different from the framework database).
	 * There is a special unit (having name &quot;hdp_attribute_map&quot;, for example) for storing many {@code interchanged attribute maps}; 
	 * @param itemId specified item identification (item ID).
	 * @return outside information of specified user, represented by {@link InterchangeAttributeMap} class.
	 */
	InterchangeAttributeMap getItemAttributeMap(int itemId);
	
	
	/**
	 * Inserting specified item profile via specified external record.
	 * Note, {@code External record} represents a single-value record of a {@code unit} in other database (different from the framework database).
	 * {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * @param item specified item profile.
	 * @param externalRecord specified external record.
	 * @return whether insert successfully
	 */
	boolean insertItemProfile(Profile item, ExternalRecord externalRecord);
	
	
	/**
	 * Updating specified item profile.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * @param item specified item profile.
	 * @return whether update successfully
	 */
	boolean updateItemProfile(Profile item);
	
	
	/**
	 * Deleting a item profile via specified user identification (user ID).
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * @param itemId specified item identification (user ID).
	 * @return whether delete successfully
	 */
	boolean deleteItemProfile(int itemId);

	
	/**
	 * Getting nominal list of specified attribute of specified unit.
	 * Note, nominal list represented by {@link NominalList} contains nominal (s). Nominal data indicates discrete and non-number data such as weekdays {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday}. Nominal data is called {@code nominal}, in brief, represented by {@code Nominal} class.
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type.
	 * If unit is database table, an attribute is identical to a field.
	 * @param unitName specified unit name.
	 * @param attribute specified attribute.
	 * @return nominal list of specified attribute of specified unit.
	 */
	NominalList getNominalList(String unitName, String attribute);
	
	
	/**
	 * Inserting the specified nominal into specified unit with regard to specified attribute name.
	 * Note, nominal represented by {@link Nominal} indicates discrete and non-number data such as weekdays {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday}.
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type.
	 * If unit is database table, an attribute is identical to a field.
	 * @param unitName specified unit name.
	 * @param attName specified attribute name.
	 * @param nominal specified nominal.
	 * @return whether insert successfully
	 */
	boolean insertNominal(String unitName, String attName, Nominal nominal);
	
	
	/**
	 * Updating the specified nominal with regard to specified unit and specified attribute.
	 * Note, nominal represented by {@link Nominal} indicates discrete and non-number data such as weekdays {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday}.
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type.
	 * If unit is database table, an attribute is identical to a field.
	 * @param unitName specified unit name.
	 * @param attName specified attribute name.
	 * @param nominal specified nominal.
	 * @return whether update successfully
	 */
	boolean updateNominal(String unitName, String attName, Nominal nominal);
	
	
	/**
	 * Deleting the nominal belonging specified attribute (like field in database table) from specified unit.
	 * Note, nominal represented by {@link Nominal} indicates discrete and non-number data such as weekdays {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday}.
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type.
	 * If unit is database table, an attribute is identical to a field.
	 * @param unitName specified unit name.
	 * @param attName specified attribute name to which the deleted nominal belongs.
	 * @return whether delete successfully.
	 */
	boolean deleteNominal(String unitName, String attName);

	
	/**
	 * Deleting all nominal (s) in the specified unit.
	 * Note, nominal represented by {@link Nominal} indicates discrete and non-number data such as weekdays {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday}.
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * If unit is database table, an attribute is identical to a field.
	 * @param unitName specified unit name.
	 * @return whether delete successfully.
	 */
	boolean deleteNominal(String unitName);

	
	/**
	 * Getting an {@code interchanged attribute map} by specified internal record.
	 * Note, {@code interchanged attribute map} includes both internal record and external record.
	 * {@code Internal record} represents a single-value record of a {@code unit} in framework database.
	 * {@code External record} represents a single-value record of a {@code unit} in other database (different from the framework database).
	 * There is a special unit (having name &quot;hdp_attribute_map&quot;, for example) for storing many {@code interchanged attribute maps}; 
	 * @param internalRecord specified internal record.
	 * @return {@code interchanged attribute map} by specified internal record.
	 */
	InterchangeAttributeMap getAttributeMap(InternalRecord internalRecord);
	
	
	/**
	 * Getting an {@code interchanged attribute map} by specified external record.
	 * Note, {@code interchanged attribute map} includes both internal record and external record.
	 * {@code Internal record} represents a single-value record of a {@code unit} in framework database.
	 * {@code External record} represents a single-value record of a {@code unit} in other database (different from the framework database).
	 * There is a special unit (having name &quot;hdp_attribute_map&quot;, for example) for storing many {@code interchanged attribute maps}; 
	 * @param externalRecord specified external record.
	 * @return {@code interchanged attribute map} by specified external record.
	 */
	InterchangeAttributeMap getAttributeMapByExternal(ExternalRecord externalRecord);
	
	
	/**
	 * Inserting a specified {@code interchanged attribute map} into framework database.
	 * Note, {@code interchanged attribute map} includes both internal record and external record.
	 * {@code Internal record} represents a single-value record of a {@code unit} in framework database.
	 * {@code External record} represents a single-value record of a {@code unit} in other database (different from the framework database).
	 * There is a special unit (having name &quot;hdp_attribute_map&quot;, for example) for storing many {@code interchanged attribute maps}; 
	 * @param attributeMap specified {@code interchanged attribute map}.
	 * @return whether insert successfully.
	 */
	boolean insertAttributeMap(InterchangeAttributeMap attributeMap);

		
	/**
	 * Updating a specified {@code interchanged attribute map} in framework database.
	 * Note, {@code interchanged attribute map} includes both internal record and external record.
	 * {@code Internal record} represents a single-value record of a {@code unit} in framework database.
	 * {@code External record} represents a single-value record of a {@code unit} in other database (different from the framework database).
	 * There is a special unit (having name &quot;hdp_attribute_map&quot;, for example) for storing many {@code interchanged attribute maps}; 
	 * @param attributeMap specified {@code interchanged attribute map}.
	 * @return whether update successfully.
	 */
	boolean updateAttributeMap(InterchangeAttributeMap attributeMap);
	
	
	/**
	 * Deleting an {@code interchanged attribute map} by specified internal record.
	 * Note, {@code interchanged attribute map} includes both internal record and external record.
	 * {@code Internal record} represents a single-value record of a {@code unit} in framework database.
	 * {@code External record} represents a single-value record of a {@code unit} in other database (different from the framework database).
	 * There is a special unit (having name &quot;hdp_attribute_map&quot;, for example) for storing many {@code interchanged attribute maps}; 
	 * @param internalRecord specified internal record.
	 * @return whether delete successfully.
	 */
	boolean deleteAttributeMap(InternalRecord internalRecord);
	
	
	/**
	 * Deleting all {@code interchanged attribute maps} by specified internal unit.
	 * Note, {@code interchanged attribute map} includes both internal record and external record.
	 * {@code Internal record} represents a single-value record of a {@code unit} in framework database.
	 * {@code External record} represents a single-value record of a {@code unit} in other database (different from the framework database).
	 * There is a special unit (having name &quot;hdp_attribute_map&quot;, for example) for storing many {@code interchanged attribute maps}; 
	 * @param internalUnit specified internal unit.
	 * @return whether delete successfully.
	 */
	boolean deleteAttributeMap(String internalUnit);
	
	
	/**
	 * Getting profile of account by account name.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file).
	 * Account is the information of a user who has access to the server with her/his previleges. Account is modeled and stored in framework database as profile. 
	 * @param accName specified account name.
	 * @return profile of account by specified account name.
	 */
	Profile getAccount(String accName);
	
	
	/**
	 * Checking whether an account is valid with regard to specified password and specified privileges.
	 * Note, account is the information of a user who has access to Hudup server with her/his privileges. Account is modeled and stored in framework database as profile. 
	 * Account is the information of a user who has access to the server with her/his privileges. Account is modeled and stored in framework database as profile. 
	 * @param account specified account name.
	 * @param password specified password.
	 * @param privileges specified privileges.
	 * @return whether the specified account is valid with regard to specified password and specified privileges.
	 */
	boolean validateAccount(String account, String password, int privileges);

	
	/**
	 * Inserting a new account (account profile) into framework database.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * Account is the information of a user who has access to Hudup server with her/his previleges. Account is modeled and stored in framework database as profile. 
	 * @param acc specified account profile.
	 * @return whether insert successfully.
	 */
	boolean insertAccount(Profile acc);
		
	
	/**
	 * Updating the specified account (specified account profile) in framework database.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * Account is the information of a user who has access to Hudup server with her/his previleges. Account is modeled and stored in framework database as profile. 
	 * @param acc specified profile of account.
	 * @return whether update successfully.
	 */
	boolean updateAccount(Profile acc);
	
	
	/**
	 * Deleting the specified account via its name.
	 * Account is the information of a user who has access to Hudup server with her/his previleges. Account is modeled and stored in framework database as profile. 
	 * @param accName specified account name.
	 * @return whether delete successfully.
	 */
	boolean deleteAccount(String accName);

	
	/**
	 * Getting profile from specified unit with specified condition.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * @param profileUnit specified unit name.
	 * @param condition specified condition to select profile to be returned, please see the {@code net.hudup.core.data.Condition}.
	 * @return profile read from specified unit with specified condition.
	 */
	Profile getProfile(String profileUnit, Profile condition);

	
	/**
	 * Getting many profiles from specified unit with specified condition. Such profiles are retrieved via fetcher.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * Fetcher is the interface for iterating each item of an associated collection. 
	 * @param profileUnit specified unit name.
	 * @param condition specified condition to select profile to be returned, please see the {@code net.hudup.core.data.Condition}.
	 * @return {@link Fetcher} of profiles read from specified unit with specified condition.
	 */
	Fetcher<Profile> getProfiles(String profileUnit, Profile condition);

	
	/**
	 * Getting many profiles queried by specified select SQL statement with specified condition. Such profiles are retrieved via fetcher.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * Fetcher is the interface for iterating each item of an associated collection. 
	 * @param selectSql specified select SQL statement represented by {@link ParamSql} class.
	 * @param condition specified condition to select profile to be returned, please see the {@code net.hudup.core.data.Condition}.
	 * @return {@link Fetcher} of profiles queried by SQL statement with specified condition.
	 */
	Fetcher<Profile> getProfiles(ParamSql selectSql, Profile condition);

	
	/**
	 * Getting many profile identifications (IDs) from specified unit. Such profiles are retrieved via fetcher.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * Fetcher is the interface for iterating each item of an associated collection. 
	 * @param profileUnit specified unit name.
	 * @return {@link Fetcher} of profile IDS from specified unit.
	 */
	Fetcher<Integer> getProfileIds(String profileUnit);
	
	
	/**
	 * If the identification (ID) of each profile read from specified unit is integer,
	 * this method returns the maximum ID among many IDs.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * @param profileUnit specified unit name.
	 * @return maximum value of profile id (s).
	 */
	int getProfileMaxId(String profileUnit);
	
	
	/**
	 * Getting attribute list of profiles in specified unit. These profiles share the same list of attributes.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type. Attribute list represented by {@link AttributeList} is the list of many attributes.
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * @param profileUnit specified unit name.
	 * @return attribute list of profiles in specified unit.
	 */
	AttributeList getProfileAttributes(String profileUnit);
	
	
	/**
	 * Getting attribute list of profiles queried by specified select SQL statement and specified unit. These profiles share the same list of attributes.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type. Attribute list represented by {@link AttributeList} is the list of many attributes.
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * @param selectSql specified select SQL statement represented by {@link ParamSql} class.
	 * @param condition specified condition to select profile to be returned, please see the {@code net.hudup.core.data.Condition}.
	 * @return attribute list of profiles queried by specified select SQL statement and specified unit.
	 */
	AttributeList getProfileAttributes(ParamSql selectSql, Profile condition);

	
	/**
	 * Inserting specified profile into specified unit.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * @param profileUnit specified unit name.
	 * @param profile specified profile.
	 * @return whether insert successfully.
	 */
	boolean insertProfile(String profileUnit, Profile profile);
	
	
	/**
	 * Updating specified profile in specified unit.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * @param profileUnit specified unit name.
	 * @param profile specified profile.
	 * @return whether updated successfully
	 */
	boolean updateProfile(String profileUnit, Profile profile);
	
	
	/**
	 * Deleting profiles from specified unit and specified condition.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * @param profileUnit specified unit name.
	 * @param condition specified condition to select profile to be returned, please see the {@code net.hudup.core.data.Condition}.
	 * @return whether delete successfully.
	 */
	boolean deleteProfile(String profileUnit, Profile condition);

	
	/**
	 * Getting the list of units in framework database.
	 * Note, the {@code unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * If {@code unit} is table in database, this method returns a list of tables in framework database.
	 * @return list of units.
	 */
	UnitList getUnitList();
	
	
	/**
	 * Creating a unit specified unit name and specified attribute list.
	 * Note, the {@code unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type. Attribute list represented by {@link AttributeList} is the list of many attributes.
	 * @param unitName specified unit name.
	 * @param attList specified attribute list.
	 * @return whether create successfully.
	 */
	boolean createUnit(String unitName, AttributeList attList);
	
	
	/**
	 * Clearing content of a unit specified by name.
	 * Note, the {@code unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * @param unitName name of specified unit whose content is cleared.
	 * @return whether clear successfully.
	 */
	boolean deleteUnitData(String unitName);

	
	/**
	 * Removing (dropping) the unit specified by name.
	 * Note, the {@code unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * @param unitName specified name of unit which be removed (dropped) from framework database.
	 * @return whether drop successfully
	 */
	boolean dropUnit(String unitName);
	
	
	/**
	 * Each database has a schema as the structure of such database.
	 * This method re-create the framework database by importing the other schema specified by given user attribute list and item attribute list.
	 * Note, provider allows programmers to access all data objects stored in archives (files) of entire framework. These data objects constitute framework database including user profile, item profile, rating matrix, context template, context, etc. Such database has the schema.
	 * @param userAttList specified user attribute list.
	 * @param itemAttList specified item attribute list.
	 * @return whether create schema successfully.
	 */
	boolean createSchema(AttributeList userAttList, AttributeList itemAttList);
	
	
	/**
	 * Each database has a schema as the structure of such database.
	 * So this method drop the schema of current framework database, which means that destroying the framework database. 
	 * Note, provider allows programmers to access all data objects stored in archives (files) of entire framework. These data objects constitute framework database including user profile, item profile, rating matrix, context template, context, etc. Such database has the schema.
	 * @return whether drop successfully.
	 */
	boolean dropSchema();
	
	
	/**
	 * Initializing this framework database by importing data from other database.
	 * The process of importing data is observed by the specified progress listener represented by {@link ProgressListener}.
	 * Programmers need to implement {@code ProgressListener} in order to specify how progress listener observes and does some tasks for the given process (importing data).
	 * @param src other provider that provides the other database.
	 * @param create whether or not re-creating the current framework database.
	 * @param registeredListener registered progress listener that observes the process of importing data.
	 * @return whether import data successfully
	 */
	boolean importData(Provider src, boolean create, ProgressListener registeredListener);
	
	
	/**
	 * Initializing this framework database by importing data from other database.
	 * The process of importing data is observed by the specified progress listener represented by {@link ProgressListener}.
	 * Programmers need to implement {@code ProgressListener} in order to specify how progress listener observes and does some tasks for the given process (importing data).
	 * @param src dataset of other database.
	 * @param create whether or not re-creating the current framework database.
	 * @param registeredListener registered progress listener that observes the process of importing data.
	 * @return whether import data successfully.
	 */
	boolean importData(Dataset src, boolean create, ProgressListener registeredListener);
		
	
	/**
	 * Initializing this framework database by importing data from other database.
	 * The process of importing data is observed by the specified progress listener represented by {@link ProgressListener}.
	 * Programmers need to implement {@code ProgressListener} in order to specify how progress listener observes and does some tasks for the given process (importing data).
	 * @param src configuration of other database.
	 * @param create whether or not re-creating the current framework database.
	 * @param registeredListener registered progress listener that observes the process of importing data.
	 * @return whether import data successfully.
	 * 
	 */
	boolean importData(DataConfig src, boolean create, ProgressListener registeredListener);

	
	/**
	 * Closing provider, in which CTS manager and provider associator are closed too. All resources are released.
	 */
	void close();

	
	@Override
	Object clone();
	
	
}
