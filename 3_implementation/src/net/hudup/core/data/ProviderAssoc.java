package net.hudup.core.data;

import net.hudup.core.data.AttributeList;
import net.hudup.core.data.AutoCloseable;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.NominalList;
import net.hudup.core.data.ParamSql;
import net.hudup.core.data.Profile;
import net.hudup.core.data.UnitList;


/**
 * Any class that implements this interface is called {@code provider associator} which assists the provider specified by interface {@code Provider} to performs read-write operations.
 * Exactly, provider associator provides low-level data operators such as processing (creating and removing) unit and processing profile.
 * Note, the {@code unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
 * {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses attributes to specify its data types.
 * In current implementation of Hudup framework, provider associator is created by the factory class.
 * Such factory will create provider associator according to specific configuration, for example, the associator for flat archive system having no structure like file system is different from the associator for database system.
 * For instance, the provider associator for flat archive system uses {@code UriAdapter} for low-level data operators.
 * Provider associator for database system uses {@code java.sql.Connection} for low-level data operators.
 * The work flow relevant to provider associator is that {@code Provider} uses {@code ProviderAssoc} which in turn uses adapters ({@code UriAdapter}, {@code java.sql.Connection}).
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface ProviderAssoc extends AutoCloseable {

	
	/**
	 * {@code Provider associator} is initialized (set up) based on the configuration.
	 * This method returns the configuration of provider associator.
	 * @return configuration of provider associator.
	 */
	DataConfig getConfig();
	
	
	/**
	 * Creating a unit specified unit name and specified attribute list.
	 * Note, the {@code unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type. Attribute list represented by {@link AttributeList} is the list of many attributes.
	 * @param unitName specified unit name.
	 * @param attList specified attribute list.
	 * @return whether creating unit successfully.
	 */
	boolean createUnit(String unitName, AttributeList attList);

	
	/**
	 * Clearing content of a unit specified by name.
	 * Note, the {@code unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * @param unitName name of specified unit whose content is cleared.
	 * @return whether deleting unit data successfully.
	 */
	boolean deleteUnitData(String unitName);

	
	/**
	 * Removing (dropping) the unit specified by name.
	 * Note, the {@code unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * @param unitName specified name of unit which be removed (dropped) from framework database.
	 * @return whether dropping unit successfully.
	 */
	public boolean dropUnit(String unitName);

		
	/**
	 * Getting the list of units in framework database.
	 * Note, the {@code unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * If {@code unit} is table in database, this method returns a list of tables in framework database.
	 * @return list of units.
	 */
	UnitList getUnitList();
		
		
	/**
	 * Getting nominal list of specified attribute of specified unit.
	 * Note, nominal list represented by {@link NominalList} contains nominal (s). Nominal data indicates discrete and non-number data such as weekdays {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday}. Nominal data is called {@code nominal}, in brief, represented by {@code Nominal} class.
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type.
	 * If unit is database table, an attribute is identical to a field.
	 * @param filterUnit specified unit name.
	 * @param attName specified attribute.
	 * @return nominal list of specified attribute of specified unit.
	 */
	NominalList getNominalList(String filterUnit, String attName);

	
	/**
	 * Getting attribute list of profiles in specified unit. These profiles share the same list of attributes.
	 * Note, attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type. Attribute list represented by {@link AttributeList} is the list of many attributes.
	 * {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. 
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * @param profileUnit specified unit name.
	 * @return attribute list of profiles in specified unit.
	 */
	AttributeList getAttributes(String profileUnit);

	
	/**
	 * Getting attribute list of profiles queried by specified select SQL statement and specified unit. These profiles share the same list of attributes.
	 * Note, attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type. Attribute list represented by {@link AttributeList} is the list of many attributes. 
	 * {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. 
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * @param selectSql specified select SQL statement represented by {@link ParamSql} class.
	 * @param condition specified condition to select profile to be returned, please see the {@code net.hudup.core.data.Condition}.
	 * @return attribute list of profiles queried by specified select SQL statement and specified unit.
	 */
	AttributeList getAttributes(ParamSql selectSql, Profile condition);
	
	
	/**
	 * Checking whether the profile in specified unit exists.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. 
	 * Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type. Attribute list represented by {@link AttributeList} is the list of many attributes. 
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * @param profileUnit specified unit name. of specified profile.
	 * @param profile specified profile.
	 * @return whether the profile in specified unit exists.
	 */
	boolean containsProfile(String profileUnit, Profile profile);
	
	
	/**
	 * Getting profile from specified unit with specified condition.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. 
	 * Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type. Attribute list represented by {@link AttributeList} is the list of many attributes. 
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * @param profileUnit specified unit name.
	 * @param condition specified condition to select profile to be returned, please see the {@code net.hudup.core.data.Condition}.
	 * @return profile read from specified unit with specified condition.
	 */
	Profile getProfile(String profileUnit, Profile condition);
	
	
	/**
	 * Getting many profiles from specified unit with specified condition. Such profiles are retrieved via fetcher.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types.
	 * Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type. Attribute list represented by {@link AttributeList} is the list of many attributes. 
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * Fetcher is the interface for iterating each item of an associated collection. 
	 * @param profileUnit specified unit name.
	 * @param condition specified condition to select profile to be returned, please see the {@code net.hudup.core.data.Condition}.
	 * @return {@link Fetcher} of profiles read from specified unit with specified condition.
	 */
	Fetcher<Profile> getProfiles(String profileUnit, Profile condition);

	
	/**
	 * Getting many profiles queried by specified select SQL statement with specified condition. Such profiles are retrieved via fetcher.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. 
	 * Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type. Attribute list represented by {@link AttributeList} is the list of many attributes. 
	 * Fetcher is the interface for iterating each item of an associated collection. 
	 * @param selectSql specified select SQL statement represented by {@link ParamSql} class.
	 * @param condition specified condition to select profile to be returned, please see the {@code net.hudup.core.data.Condition}.
	 * @return {@link Fetcher} of profiles queried by SQL statement with specified condition.
	 */
	Fetcher<Profile> getProfiles(ParamSql selectSql, Profile condition);
	
	
	/**
	 * Getting many profile identifications (IDs) from specified unit. Such profiles are retrieved via fetcher.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types.
	 * Each profile has an identification (ID). 
	 * Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type. Attribute list represented by {@link AttributeList} is the list of many attributes. 
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * Fetcher is the interface for iterating each item of an associated collection. 
	 * @param profileUnit specified unit name.
	 * @return {@link Fetcher} of profile IDS from specified unit.
	 */
	Fetcher<Integer> getProfileIds(String profileUnit);
	
	
	/**
	 * If the identification (ID) of each profile read from specified unit is integer,
	 * this method returns the maximum ID among many IDs.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types.
	 * Each profile has an identification (ID). 
	 * Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type. Attribute list represented by {@link AttributeList} is the list of many attributes. 
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * @param profileUnit specified unit name.
	 * @return maximum value of profile id (s).
	 */
	int getProfileMaxId(String profileUnit);
	
	
	/**
	 * Inserting specified profile into specified unit.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. 
	 * Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type. Attribute list represented by {@link AttributeList} is the list of many attributes. 
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * @param profileUnit specified unit name.
	 * @param profile specified profile.
	 * @return whether insert successfully.
	 */
	boolean insertProfile(String profileUnit, Profile profile);
	
	
	/**
	 * Updating specified profile in specified unit.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. 
	 * Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type. Attribute list represented by {@link AttributeList} is the list of many attributes. 
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * @param profileUnit specified unit name.
	 * @param profile specified profile.
	 * @return whether update successfully.
	 */
	boolean updateProfile(String profileUnit, Profile profile);
	
	
	/**
	 * Deleting profiles from specified unit and specified condition.
	 * Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. 
	 * Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type. Attribute list represented by {@link AttributeList} is the list of many attributes. 
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 * @param profileUnit specified unit name.
	 * @param condition specified condition to select profile to be returned, please see the {@code net.hudup.core.data.Condition}.
	 * @return whether delete successfully.
	 */
	boolean deleteProfile(String profileUnit, Profile condition);
	
	
}