/**
 * 
 */
package net.hudup.core.data;

import java.io.Serializable;
import java.util.Map;

import net.hudup.core.PluginStorage;
import net.hudup.core.Util;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.DatasetParser;
import net.hudup.core.parser.TextParserUtil;


/**
 * Configuration utility class stores configuration properties.
 * Each property is a pair of key and value.
 * A popular key is <i>unit</i> which represents a CSV file, database table, Excel sheet, etc.
 * Unit is used to store object such as user profile, item profile, rating matrix, etc.
 * This class is very necessary to configure many objects.
 * Moreover it contains many constants and static utility methods for processing data.
 * This class inherits directly from the class {@link PropList}. As a convention, an {@link DataConfig} object is called configuration.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class DataConfig extends PropList {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Parser field name
	 */
	public final static String PARSER_FIELD                   = changeCase("parser");
	
	/**
	 * Data driver field name
	 */
	public final static String DATA_DRIVER_NAME_FIELD         = changeCase("data_driver_name");
	
	/**
	 * User ID field name
	 */
	public final static String USERID_FIELD                   = changeCase("userid");
	
	/**
	 * External user ID field name
	 */
	public final static String EXTERNAL_USERID_FIELD          = changeCase("external_userid");
	
	/**
	 * User type field name
	 */
	public final static String USER_TYPE_FIELD                = changeCase("user_type");
	
	/**
	 * Item field name
	 */
	public final static String ITEM_FIELD                     = changeCase("item");
	
	/**
	 * Item ID field name
	 */
	public final static String ITEMID_FIELD                   = changeCase("itemid");
	
	/**
	 * External item ID field name
	 */
	public final static String EXTERNAL_ITEMID_FIELD          = changeCase("external_itemid");
	
	/**
	 * Bit item ID field name
	 */
	public final static String BITITEMID_FIELD                = changeCase("bititemid");
	
	/**
	 * Item type field name
	 */
	public final static String ITEM_TYPE_FIELD                = changeCase("item_type");
	
	/**
	 * Rating user ID field name
	 */
	public final static String RATING_USERID_FIELD            = changeCase("rating_userid");
	
	/**
	 * Rating item ID field name
	 */
	public final static String RATING_ITEMID_FIELD            = changeCase("rating_itemid");
	
	/**
	 * Rating field name
	 */
	public final static String RATING_FIELD                   = changeCase("rating");
	
	/**
	 * Rating date field name
	 */
	public final static String RATING_DATE_FIELD              = changeCase("rating_date");
	
	/**
	 * Attribute field name
	 */
	public final static String ATTRIBUTE_FIELD                = changeCase("attribute");
	
	/**
	 * Attribute value field name
	 */
	public final static String ATTRIBUTE_VALUE_FIELD          = changeCase("attribute_value");
	
	/**
	 * Nominal field name
	 */
	public final static String NOMINAL_FIELD                  = changeCase("nominal");
	
	/**
	 * Nominal reference unit field name
	 */
	public final static String NOMINAL_REF_UNIT_FIELD         = changeCase("nominal_ref_unit");
	
	/**
	 * Nominal index field name
	 */
	public final static String NOMINAL_INDEX_FIELD            = changeCase("nominal_index");
	
	/**
	 * Nominal value field name
	 */
	public final static String NOMINAL_VALUE_FIELD            = changeCase("nominal_value");
	
	/**
	 * Nominal parent index field name
	 */
	public final static String NOMINAL_PARENT_INDEX_FIELD     = changeCase("nominal_parent_index");
	
	/**
	 * Account field name
	 */
	public final static String ACCOUNT_NAME_FIELD             = changeCase("account_name");
	
	/**
	 * Account password field name
	 */
	public final static String ACCOUNT_PASSWORD_FIELD         = changeCase("account_password");
	
	/**
	 * Account privileges field name
	 */
	public final static String ACCOUNT_PRIVILEGES_FIELD      = changeCase("account_privs");
	
	/**
	 * Minimum rating field name
	 */
	public final static String MIN_RATING_FIELD               = changeCase("min_rating");
	
	/**
	 * Maximum rating field name
	 */
	public final static String MAX_RATING_FIELD               = changeCase("max_rating");
	
	/**
	 * Maximum recommendation field name
	 */
	public final static String MAX_RECOMMEND_FIELD            = changeCase("max_recommend");
	
	/**
	 * Position field name
	 */
	public final static String POSITION_FIELD                 = changeCase("position");
	
	/**
	 * Storage URI field name. 
	 * Note that URI, abbreviation of Uniform Resource Identifier, is the string of characters used to identify a resource on Internet.
	 */
	public final static String STORE_URI_FIELD                = changeCase("store_uri");
	
	/**
	 * Storage account field name
	 */
	public final static String STORE_ACCOUNT_FIELD            = changeCase("store_account");
	
	/**
	 * Storage password field name
	 */
	public final static String STORE_PASSWORD_FIELD           = changeCase("store_password");
	
	/**
	 * Session attribute field name
	 */
	public final static String SESSION_ATTRIBUTE_NAME_FIELD   = changeCase("session_attribute");
	
	/**
	 * External record field name
	 */
	public final static String EXTERNAL_RECORD_FIELD          = changeCase("external_record");
	
	/**
	 * External unit field name
	 */
	public final static String EXTERNAL_UNIT_FIELD            = changeCase("external_unit");
	
	/**
	 * External attribute field name
	 */
	public final static String EXTERNAL_ATTRIBUTE_NAME_FIELD  = changeCase("external_attribute_name");
	
	/**
	 * External attribute value field name
	 */
	public final static String EXTERNAL_ATTRIBUTE_VALUE_FIELD = changeCase("external_attribute_value");
	
	/**
	 * Internal unit field name
	 */
	public final static String INTERNAL_UNIT_FIELD            = changeCase("internal_unit");
	
	/**
	 * Internal attribute field name
	 */
	public final static String INTERNAL_ATTRIBUTE_NAME_FIELD  = changeCase("internal_attribute_name");
	
	/**
	 * Internal attribute value field name
	 */
	public final static String INTERNAL_ATTRIBUTE_VALUE_FIELD = changeCase("internal_attribute_value");
	
	/**
	 * Profile field name
	 */
	public final static String PROFILE_FIELD                  = changeCase("profile");
	
	/**
	 * Context template ID field name
	 */
	public final static String CTX_TEMPLATEID_FIELD           = changeCase("ctx_templateid");
	
	/**
	 * Context field name
	 */
	public final static String CTX_NAME_FIELD                 = changeCase("ctx_name");
	
	/**
	 * Context type field name
	 */
	public final static String CTX_TYPE_FIELD                 = changeCase("ctx_type");
	
	/**
	 * Context value field name
	 */
	public final static String CTX_VALUE_FIELD                = changeCase("ctx_value");
	
	/**
	 * Context parent field name
	 */
	public final static String CTX_PARENT_FIELD               = changeCase("ctx_parent");
	
	/**
	 * Context template schema field name
	 */
	public final static String CTS_MANAGER_NAME_FIELD         = changeCase("cts_manager_name");

	
	/**
	 * Configuration unit name
	 */
	public final static String CONFIG_UNIT                    = changeCase("hdp_config");
	
	/**
	 * Rating unit name
	 */
	public final static String RATING_UNIT                    = changeCase("hdp_rating");
	
	/**
	 * User unit name
	 */
	public final static String USER_UNIT                      = changeCase("hdp_user");
	
	/**
	 * Item unit name
	 */
	public final static String ITEM_UNIT                      = changeCase("hdp_item");
	
	/**
	 * Nominal unit name
	 */
	public final static String NOMINAL_UNIT                   = changeCase("hdp_nominal");
	
	/**
	 * Attribute map unit name
	 */
	public final static String ATTRIBUTE_MAP_UNIT             = changeCase("hdp_attribute_map");
	
	/**
	 * Account unit name
	 */
	public final static String ACCOUNT_UNIT                   = changeCase("hdp_account");
	
	/**
	 * Context template unit name
	 */
	public final static String CONTEXT_TEMPLATE_UNIT          = changeCase("hdp_context_template");
	
	/**
	 * Context unit name
	 */
	public final static String CONTEXT_UNIT                   = changeCase("hdp_context");

	
	/**
	 * Normal user privilege for access account
	 */
	public final static int ACCOUNT_ACCESS_PRIVILEGE = 1;
	
	/**
	 * Administration privilege for access account
	 */
	public final static int ACCOUNT_ADMIN_PRIVILEGE = 3;
	
	
	/**
	 * Default constructor
	 */
	public DataConfig() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with specified URI represented by {@link xURI} class.
	 * URI, abbreviation of Uniform Resource Identifier, is the string of characters used to identify a resource on Internet.
	 * @param uri Specified URI via {@link xURI} which is a wrapper of URI.
	 */
	public DataConfig(xURI uri) {
		super(uri);
		// TODO Auto-generated constructor stub
	}


	/**
	 * Change text case. For example, given text <i>AbCd</i>, this method returns <i>abcd</i> by default.
	 * @param text Specified text.
	 * @return Changed text (default: lower case).
	 */
	public static String changeCase(String text) {
		return text.toLowerCase();
	}
	
	
	/**
	 * Getting value of the key specified by {@link #CONFIG_UNIT}, which is the real name of configuration unit.
	 * @return The real name of configuration unit
	 */
	public String getConfigUnit() {
		return getAsString(CONFIG_UNIT);
	}

	
	/**
	 * Setting value of the key specified by {@link #CONFIG_UNIT}, which is the real name of configuration unit.
	 * @param configUnit The real name of configuration unit
	 */
	public void setConfigUnit(String configUnit) {
		put(CONFIG_UNIT, configUnit);
	}
	
	
	/**
	 * Getting value of the key specified by {@link #RATING_UNIT}, which is the real name of rating unit.
	 * @return The real name of rating unit
	 */
	public String getRatingUnit() {
		return getAsString(RATING_UNIT);
	}
	
	
	/**
	 * Setting value of the key specified by {@link #RATING_UNIT}, which is the real name of rating unit.
	 * @param ratingUnit The real name of rating unit
	 */
	public void setRatingUnit(String ratingUnit) {
		put(RATING_UNIT, ratingUnit);
	}
	
	
	/**
	 * Getting value of the key specified by {@link #USER_UNIT}, which is the real name of user unit.
	 * @return The real name of rating unit
	 */
	public String getUserUnit() {
		return getAsString(USER_UNIT);
	}
	
	
	/**
	 * Setting value of the key specified by {@link #USER_UNIT}, which is the real name of user unit.
	 * @param userUnit The real name of rating unit
	 */
	public void setUserUnit(String userUnit) {
		put(USER_UNIT, userUnit);
	}

	
	/**
	 * Getting value of the key specified by {@link #ITEM_UNIT}, which is the real name of item unit.
	 * @return The real name of user unit
	 */
	public String getItemUnit() {
		return getAsString(ITEM_UNIT);
	}

	
	/**
	 * Setting value of the key specified by {@link #ITEM_UNIT}, which is the real name of item unit.
	 * @param itemUnit The real name of item unit
	 */
	public void setItemUnit(String itemUnit) {
		put(ITEM_UNIT, itemUnit);
	}

	
	/**
	 * Getting value of the key specified by {@link #NOMINAL_UNIT}, which is the real name of nominal unit.
	 * @return The real name of nominal unit
	 */
	public String getNominalUnit() {
		return getAsString(NOMINAL_UNIT);
	}

	
	/**
	 * Setting value of the key specified by {@link #NOMINAL_UNIT}, which is the real name of nominal unit.
	 * @param nominalUnit The real name of nominal unit
	 */
	public void setNominalUnit(String nominalUnit) {
		put(NOMINAL_UNIT, nominalUnit);
	}
	
	
	/**
	 * Getting value of the key specified by {@link #ATTRIBUTE_MAP_UNIT}, which is the real name of attribute map unit.
	 * @return The real name of attribute map unit
	 */
	public String getAttributeMapUnit() {
		return getAsString(ATTRIBUTE_MAP_UNIT);
	}

	
	/**
	 * Setting value of the key specified by {@link #ATTRIBUTE_MAP_UNIT}, which is the real name of attribute map unit.
	 * @param attributeMapUnit The real name of attribute map unit
	 */
	public void setAttributeMapUnit(String attributeMapUnit) {
		put(ATTRIBUTE_MAP_UNIT, attributeMapUnit);
	}

	
	/**
	 * Getting value of the key specified by {@link #ACCOUNT_UNIT}, which is the real name of account unit.
	 * @return The real name of account unit
	 */
	public String getAccountUnit() {
		return getAsString(ACCOUNT_UNIT);
	}

	
	/**
	 * Setting value of the key specified by {@link #ACCOUNT_UNIT}, which is the real name of account unit.
	 * @param accountUnit The real name of account unit
	 */
	public void setAccountUnit(String accountUnit) {
		put(ACCOUNT_UNIT, accountUnit);
	}
	
	
	/**
	 * Getting value of the key specified by {@link #CONTEXT_TEMPLATE_UNIT}, which is the real name of context template unit.
	 * @return The real name of context template unit
	 */
	public String getContextTemplateUnit() {
		return getAsString(CONTEXT_TEMPLATE_UNIT);
	}

	
	/**
	 * Setting value of the key specified by {@link #CONTEXT_TEMPLATE_UNIT}, which is the real name of context template unit.
	 * @param contextTemplateUnit The real name of context template unit
	 */
	public void setContextTemplateUnit(String contextTemplateUnit) {
		put(CONTEXT_TEMPLATE_UNIT, contextTemplateUnit);
	}

	
	/**
	 * Each template owns a table so-called template profile table, each row of table corresponds to a value assigned to template and 
	 * such value is id of row. Such profile table is called unit.
	 * For example, the name of this table has format: <code>hdp_context_template_1_profile</code> where "1" is template id.
	 * This method gets the name of unit of template profile having specified identifier.
	 * @param ctxTemplateId specified identifier of template.
	 * @return name of unit of template profile having specified identifier.
	 */
	public String getContextTemplateProfileUnit(int ctxTemplateId) {
		String templateUnit = getContextTemplateUnit();
		if (templateUnit == null || ctxTemplateId < 0)
			return null;
		else
			return templateUnit + TextParserUtil.CONNECT_SEP + ctxTemplateId + TextParserUtil.CONNECT_SEP + PROFILE_FIELD;
	}
	
	
	/**
	 * Getting value of the key specified by {@link #CONTEXT_UNIT}, which is the real name of context unit.
	 * @return The real name of context unit
	 */
	public String getContextUnit() {
		return getAsString(CONTEXT_UNIT);
	}

	
	/**
	 * Setting value of the key specified by {@link #CONTEXT_UNIT}, which is the real name of context unit.
	 * @param contextUnit The real name of context unit
	 */
	public void setContextUnit(String contextUnit) {
		put(CONTEXT_UNIT, contextUnit);
	}

	
	/**
	 * Setting value specified by parameter <i>unit</i> for the unit specified by parameter <i>unitType</i>.
	 * Common unit types are {@link #ACCOUNT_UNIT}, {@link #ATTRIBUTE_MAP_UNIT}, {@link #CONFIG_UNIT},
	 * {@link #CONTEXT_TEMPLATE_UNIT}, {@link #CONTEXT_UNIT}, {@link #ITEM_UNIT}, {@link #NOMINAL_UNIT},
	 * {@link #RATING_UNIT}, {@link #USER_UNIT}.
	 * @param unitType Unit type (unit name)
	 * @param unit Unit value
	 */
	public void setUnit(String unitType, String unit) {
		if (unitType.equals(DataConfig.ACCOUNT_UNIT))
			setAccountUnit(unit);
		else if (unitType.equals(DataConfig.ATTRIBUTE_MAP_UNIT))
			setAttributeMapUnit(unit);
		else if (unitType.equals(DataConfig.CONFIG_UNIT))
			setConfigUnit(unit);
		else if (unitType.equals(DataConfig.CONTEXT_TEMPLATE_UNIT))
			setContextTemplateUnit(unit);
		else if (unitType.equals(DataConfig.CONTEXT_UNIT))
			setContextUnit(unit);
		else if (unitType.equals(DataConfig.ITEM_UNIT))
			setItemUnit(unit);
		else if (unitType.equals(DataConfig.NOMINAL_UNIT))
			setNominalUnit(unit);
		else if (unitType.equals(DataConfig.RATING_UNIT))
			setRatingUnit(unit);
		else if (unitType.equals(DataConfig.USER_UNIT))
			setUserUnit(unit);
		else
			logger.info("DataConfig#setUnit not valid with inexistent #unitType " + unitType);
	}

	
	/**
	 * Getting list of units specified by {@link UnitList}.
	 * Common unit types are {@link #ACCOUNT_UNIT}, {@link #ATTRIBUTE_MAP_UNIT}, {@link #CONFIG_UNIT},
	 * {@link #CONTEXT_TEMPLATE_UNIT}, {@link #CONTEXT_UNIT}, {@link #ITEM_UNIT}, {@link #NOMINAL_UNIT},
	 * {@link #RATING_UNIT}, {@link #USER_UNIT}.
	 * @return {@link UnitList} represents list of units.
	 */
	public UnitList getUnitList() {
		UnitList unitList = new UnitList();
		
		if (getAccountUnit() != null)
			unitList.add(new Unit(getAccountUnit()));
		
		if (getAttributeMapUnit() != null)
			unitList.add(new Unit(getAttributeMapUnit()));
		
		if (getConfigUnit() != null)
			unitList.add(new Unit(getConfigUnit()));
		
		if (getContextTemplateUnit() != null)
			unitList.add(new Unit(getContextTemplateUnit()));

		if (getContextUnit() != null)
			unitList.add(new Unit(getContextUnit()));

		if (getRatingUnit() != null)
			unitList.add(new Unit(getRatingUnit()));
		
		if (getUserUnit() != null)
			unitList.add(new Unit(getUserUnit()));
		
		if (getItemUnit() != null)
			unitList.add(new Unit(getItemUnit()));
		
		if (getNominalUnit() != null)
			unitList.add(new Unit(getNominalUnit()));
		
		return unitList;
	}


	/**
	 * Getting list of modifiable units specified by {@link UnitList}.
	 * Modifiable units are ones that can be changed frequently.
	 * Common modifiable unit types are {@link #USER_UNIT}, {@link #ITEM_UNIT}, {@link #CONTEXT_TEMPLATE_UNIT}.
	 * @return {@link UnitList} represents a list of modifiable units
	 */
	public UnitList getModifiableUnitList() {
		UnitList unitList = new UnitList();
		
		if (getUserUnit() != null)
			unitList.add(new Unit(getUserUnit()));
		
		if (getItemUnit() != null)
			unitList.add(new Unit(getItemUnit()));
		
		if (getContextTemplateUnit() != null)
			unitList.add(new Unit(getContextTemplateUnit()));

		return unitList;
	}

	
	/**
	 * Getting list of default units specified by {@link UnitList}. This is static method.
	 * Default unit types are {@link #ACCOUNT_UNIT}, {@link #ATTRIBUTE_MAP_UNIT}, {@link #CONFIG_UNIT},
	 * {@link #CONTEXT_TEMPLATE_UNIT}, {@link #CONTEXT_UNIT}, {@link #ITEM_UNIT}, {@link #NOMINAL_UNIT},
	 * {@link #RATING_UNIT}, {@link #USER_UNIT}.
	 * @return {@link UnitList} represents a list of default units
	 */
	public static UnitList getDefaultUnitList() {
		UnitList unitList = new UnitList();
		
		unitList.add(new Unit(ACCOUNT_UNIT));
		
		unitList.add(new Unit(ATTRIBUTE_MAP_UNIT));

		unitList.add(new Unit(CONFIG_UNIT));
		
		unitList.add(new Unit(CONTEXT_TEMPLATE_UNIT));

		unitList.add(new Unit(CONTEXT_UNIT));

		unitList.add(new Unit(ITEM_UNIT));

		unitList.add(new Unit(NOMINAL_UNIT));
		
		unitList.add(new Unit(RATING_UNIT));
		
		unitList.add(new Unit(USER_UNIT));
		
		return unitList;
	}

	
	/**
	 * Filling in the internal unit list by specified unit list <i>unitList</i>.
	 * Concretely, if unit &quot;item&quot; does not exist in the internal unit list but
	 * it exists in the specified parameter <i>unitList</i> then &quot;item&quot; is put into the internal unit list.
	 * @param unitList The specified unit list
	 */
	public void fillUnitList(UnitList unitList) {
		
		if (unitList.contains(ACCOUNT_UNIT) && getAccountUnit() == null)
			setAccountUnit(ACCOUNT_UNIT);
		
		if (unitList.contains(ATTRIBUTE_MAP_UNIT) && getAttributeMapUnit() == null)
			setAttributeMapUnit(ATTRIBUTE_MAP_UNIT);
		
		if (unitList.contains(CONFIG_UNIT) && getConfigUnit() == null)
			setConfigUnit(CONFIG_UNIT);

		if (unitList.contains(CONTEXT_TEMPLATE_UNIT) && getContextTemplateUnit() == null)
			setContextTemplateUnit(CONTEXT_TEMPLATE_UNIT);
		
		if (unitList.contains(CONTEXT_UNIT) && getContextUnit() == null)
			setContextUnit(CONTEXT_UNIT);
		
		if (unitList.contains(ITEM_UNIT) && getItemUnit() == null)
			setItemUnit(DataConfig.ITEM_UNIT);
		
		if (unitList.contains(NOMINAL_UNIT) && getNominalUnit() == null)
			setNominalUnit(NOMINAL_UNIT);
		
		if (unitList.contains(RATING_UNIT) && getRatingUnit() == null)
			setRatingUnit(RATING_UNIT);

		if (unitList.contains(USER_UNIT) && getUserUnit() == null)
			setUserUnit(USER_UNIT);

	}

	
	/**
	 * Force to fill in the internal unit list by specified unit list <i>unitList</i>.
	 * Concretely, if unit &quot;item&quot; exists in the specified parameter <i>unitList</i>,
	 * it is put into the internal unit list instead of whether or not it exists in the internal unit list.
	 * @param unitList The specified unit list
	 */
	public void putUnitList(UnitList unitList) {
		
		if (unitList.contains(ACCOUNT_UNIT))
			setAccountUnit(ACCOUNT_UNIT);
		
		if (unitList.contains(ATTRIBUTE_MAP_UNIT))
			setAttributeMapUnit(ATTRIBUTE_MAP_UNIT);
		
		if (unitList.contains(CONFIG_UNIT))
			setConfigUnit(CONFIG_UNIT);

		if (unitList.contains(CONTEXT_TEMPLATE_UNIT))
			setContextTemplateUnit(CONTEXT_TEMPLATE_UNIT);
		
		if (unitList.contains(CONTEXT_UNIT))
			setContextUnit(CONTEXT_UNIT);
		
		if (unitList.contains(ITEM_UNIT))
			setItemUnit(DataConfig.ITEM_UNIT);
		
		if (unitList.contains(NOMINAL_UNIT))
			setNominalUnit(NOMINAL_UNIT);
		
		if (unitList.contains(RATING_UNIT))
			setRatingUnit(RATING_UNIT);

		if (unitList.contains(USER_UNIT))
			setUserUnit(USER_UNIT);

	}
	
	
	/**
	 * Filling in the internal list with default units such as
	 * {@link #ACCOUNT_UNIT}, {@link #ATTRIBUTE_MAP_UNIT}, {@link #CONFIG_UNIT},
	 * {@link #CONTEXT_TEMPLATE_UNIT}, {@link #CONTEXT_UNIT}, {@link #ITEM_UNIT}, {@link #NOMINAL_UNIT},
	 * {@link #RATING_UNIT}, {@link #USER_UNIT}. The method also sets the store URI by calling method {@link #setStoreUri(xURI)}.
	 * @param storeUri The URI indicates where to store this {@link DataConfig}.
	 */
	public void putDefaultUnitList(xURI storeUri) {
		
		putUnitList(getDefaultUnitList());
		if (storeUri != null)
			setStoreUri(storeUri);
	}


	/**
	 * Removing units that exist in the specified list <i>removeList</i> from the internal list.
	 * For example, if unit &quot;item&quot; exits in parameter <i>removeList</i>, it is removed from the internal list.
	 * @param removeList The specified list whose units are removed from the internal list.
	 */
	public void removeUnitList(UnitList removeList) {
		
		if (removeList.contains(ACCOUNT_UNIT))
			remove(ACCOUNT_UNIT);
		
		if (removeList.contains(ATTRIBUTE_MAP_UNIT))
			remove(ATTRIBUTE_MAP_UNIT);
		
		if (removeList.contains(CONFIG_UNIT))
			remove(CONFIG_UNIT);
		
		if (removeList.contains(CONTEXT_TEMPLATE_UNIT))
			remove(CONTEXT_TEMPLATE_UNIT);
		
		if (removeList.contains(CONTEXT_UNIT))
			remove(CONTEXT_UNIT);

		if (removeList.contains(ITEM_UNIT))
			remove(DataConfig.ITEM_UNIT);
		
		if (removeList.contains(NOMINAL_UNIT))
			remove(NOMINAL_UNIT);
		
		if (removeList.contains(RATING_UNIT))
			remove(RATING_UNIT);

		if (removeList.contains(USER_UNIT))
			remove(USER_UNIT);

	}
	
	
	/**
	 * Converting the {@link DataConfig} into a URI. URI is abbreviation of Uniform Resource Identifier.
	 * It is a string of characters that identifies a resource on internet.
	 * In this framework, URI is wrapped by class {@link xURI}.
	 * @return {@link xURI} specifies the URI representing the whole {@link DataConfig}
	 */
	public xURI toUri() {
		Map<String, String> params = Util.newMap();
		
		if (getAccountUnit() != null)
			params.put(ACCOUNT_UNIT, getAccountUnit());
		
		if (getAttributeMapUnit() != null)
			params.put(ATTRIBUTE_MAP_UNIT, getAttributeMapUnit());
		
		if (getConfigUnit() != null)
			params.put(CONFIG_UNIT, getConfigUnit());
		
		if (getContextTemplateUnit() != null)
			params.put(CONTEXT_TEMPLATE_UNIT, getContextTemplateUnit());

		if (getContextUnit() != null)
			params.put(CONTEXT_UNIT, getContextUnit());

		if (getItemUnit() != null)
			params.put(ITEM_UNIT, getItemUnit());
		
		if (getNominalUnit() != null)
			params.put(NOMINAL_UNIT, getNominalUnit());
		
		if (getRatingUnit() != null)
			params.put(RATING_UNIT, getRatingUnit());
		
		if (getUserUnit() != null)
			params.put(USER_UNIT, getUserUnit());
		
		if (getStoreAccount() != null)
			params.put(STORE_ACCOUNT_FIELD, getStoreAccount());
		
		if (getStorePassword() != null)
			params.put(STORE_PASSWORD_FIELD, getStorePassword().getText());
		
		if (getParser() != null)
			params.put(PARSER_FIELD, getParser().getName());
		
		if (getDataDriverName() != null)
			params.put(DATA_DRIVER_NAME_FIELD, getDataDriverName());
		
		String query = xURI.toParameterText(params, false);
		
		xURI storeUri = getStoreUri();
		if (storeUri == null)
			return null;
		
		return xURI.create(
				storeUri.getScheme(), 
				storeUri.getHost(), 
				storeUri.getPort(), 
				storeUri.getPath(), 
				query,
				null);
	}

	
	/**
	 * Creating the {@link DataConfig} from specified URI. This static method is dual one of method {@link #toUri()}.
	 * URI is abbreviation of Uniform Resource Identifier.
	 * It is a string of characters that identifies a resource on internet.
	 * In this framework, URI is wrapped by class {@link xURI}.
	 * @param uri The URI used to create {@link DataConfig}.
	 * @return {@link DataConfig} is the data configuration created from specified URI.
	 */
	public static DataConfig create(xURI uri) {
		DataConfig config = new DataConfig();
		xURI storeUri = xURI.create(uri.getScheme(), uri.getHost(), uri.getPort(), uri.getPath());
		if (storeUri == null)
			return null;
		
		config.setStoreUri(storeUri);
		
		Map<String, String> params = xURI.parseParameterText(uri.getQuery());
		
		if (params.containsKey(ACCOUNT_UNIT))
			config.setAccountUnit(params.get(ACCOUNT_UNIT));
		
		if (params.containsKey(ATTRIBUTE_MAP_UNIT))
			config.setAttributeMapUnit(params.get(ATTRIBUTE_MAP_UNIT));
		
		if (params.containsKey(CONFIG_UNIT))
			config.setConfigUnit(params.get(CONFIG_UNIT));
		
		if (params.containsKey(CONTEXT_TEMPLATE_UNIT))
			config.setContextTemplateUnit(params.get(CONTEXT_TEMPLATE_UNIT));

		if (params.containsKey(CONTEXT_UNIT))
			config.setContextUnit(params.get(CONTEXT_UNIT));

		if (params.containsKey(ITEM_UNIT))
			config.setItemUnit(params.get(ITEM_UNIT));
		
		if (params.containsKey(NOMINAL_UNIT))
			config.setNominalUnit(params.get(NOMINAL_UNIT));
		
		if (params.containsKey(RATING_UNIT))
			config.setRatingUnit(params.get(RATING_UNIT));
		
		if (params.containsKey(USER_UNIT))
			config.setUserUnit(params.get(USER_UNIT));
		
		if (params.containsKey(STORE_ACCOUNT_FIELD))
			config.setStoreAccount(params.get(STORE_ACCOUNT_FIELD));
		
		if (params.containsKey(STORE_PASSWORD_FIELD))
			config.setStorePassword(new HiddenText(params.get(STORE_PASSWORD_FIELD)));
		
		if (params.containsKey(PARSER_FIELD)) {
			DatasetParser parser = (DatasetParser) PluginStorage.getParserReg().query(params.get(PARSER_FIELD));
			if (parser != null)
				config.setParser(parser);
		}
		
		if (params.containsKey(DATA_DRIVER_NAME_FIELD))
			config.setDataDriverName(params.get(DATA_DRIVER_NAME_FIELD));
	
		return config;
	}
	
	
	/**
	 * Getting the store URI of the data configuration. Store URI indicates where to store this {@link DataConfig}.
	 * URI is abbreviation of Uniform Resource Identifier. It is a string of characters that identifies a resource on internet.
	 * @return The store {@link xURI} indicates where to store this {@link DataConfig}.
	 */
	public xURI getStoreUri() {
		if (!containsKey(STORE_URI_FIELD))
			return null;
			
		return xURI.create(getAsString(STORE_URI_FIELD));
	}
	
	
	/**
	 * Setting the store URI of the data configuration. Store URI indicates where to store this {@link DataConfig}.
	 * URI is abbreviation of Uniform Resource Identifier. It is a string of characters that identifies a resource on internet.
	 * Please refer to https://en.wikipedia.org/wiki/Uniform_Resource_Identifier in order to comprehend URI.
	 * In this framework, URI is wrapped by class {@link xURI}.
	 * @param storeUri The URI indicates where to store this {@link DataConfig}.
	 */
	public void setStoreUri(xURI storeUri) {
		put(STORE_URI_FIELD, storeUri.toString());
	}

	
	/**
	 * Getting the URI specifying the ID of this {@link DataConfig}.
	 * This URI is the rating URI returned by {@link #getRatingUri()} by default.
	 * This URI is often in the store URI returned by {@link #getStoreUri()}. 
	 * In this framework, URI is wrapped by class {@link xURI}.
	 * @return The URI specifying the ID of this {@link DataConfig}. 
	 */
	public xURI getUriId() {
		return getRatingUri();
	}
	
	
	/**
	 * Getting the URI which indicates where to store the rating unit ({@link #RATING_UNIT}).
	 * This URI is often in the store URI returned by {@link #getStoreUri()}.
	 * By default, this URI is the concatenation of he store URI returned by {@link #getStoreUri()} and the rating unit returned by {@link #getRatingUnit()}.
	 * In this framework, URI is wrapped by class {@link xURI}.
	 * @return The URI of the rating unit
	 */
	public xURI getRatingUri() {
		xURI store = getStoreUri();
		
		if (store == null)
			return null;
		else if (getRatingUnit() != null)
			return store.concat(getRatingUnit());
		else
			return getStoreUri();
	}
	
	
	/**
	 * Getting the account name of this {@link DataConfig}
	 * @return Account name of this {@link DataConfig}
	 */
	public String getStoreAccount() {
		return getAsString(STORE_ACCOUNT_FIELD);
	}
	
	
	/**
	 * Setting the account name of this {@link DataConfig}
	 * @param account Account name of this {@link DataConfig}
	 */
	public void setStoreAccount(String account) {
		put(STORE_ACCOUNT_FIELD, account);
		
	}
	
	
	/**
	 * Getting the account password of this {@link DataConfig}
	 * @return Account password of this {@link DataConfig} returned as hidden text {@link HiddenText}
	 */
	public HiddenText getStorePassword() {
		return getAsHidden(STORE_PASSWORD_FIELD);
	}

	
	/**
	 * Setting the account password of this {@link DataConfig}
	 * @param password Account password of this {@link DataConfig} is put into {@link DataConfig} as hidden text {@link HiddenText}.
	 */
	public void setStorePassword(HiddenText password) {
		put(STORE_PASSWORD_FIELD, password);
		
	}

	
	/**
	 * Getting the parser {@link DatasetParser} of this {@link DataConfig}.
	 * An {@link DatasetParser} is responsible for reading and parsing coarse data such as CSV file, Excel file, database table into {@link Dataset}.
	 * @return parser {@link DatasetParser}
	 */
	public DatasetParser getParser() {
		return (DatasetParser) get(PARSER_FIELD);
	}
	
	
	/**
	 * Setting the parser {@link DatasetParser} of this {@link DataConfig}.
	 * An {@link DatasetParser} is responsible for reading and parsing coarse data such as CSV file, Excel file, database table into {@link Dataset}.
	 * @param parser {@link DatasetParser}
	 */
	public void setParser(DatasetParser parser) {
		if (parser != null)
			put(PARSER_FIELD, parser);
	}
	
	
	/**
	 * Setting the parser {@link DatasetParser} via parser name.
	 * The {@link PluginStorage} will discover the built-in parser {@link DatasetParser} via the parameter as parser name.
	 * @param parserName The parser name
	 */
	public void setParser(String parserName) {
		DatasetParser parser = (DatasetParser) PluginStorage.getParserReg().query(parserName);
		setParser(parser);
	}

	
	/**
	 * Getting data driver name. Each data driver has a name.
	 * Please see {@link DataDriver} for more details about data driver.
	 * @return Data driver name
	 */
	public String getDataDriverName() {
		return getAsString(DATA_DRIVER_NAME_FIELD);
	}
	
	
	/**
	 * Setting data driver name. Each data driver has a name.
	 * Please see {@link DataDriver} for more details about data driver.
	 * @param dataDriverName Specific data driver name
	 */
	public void setDataDriverName(String dataDriverName) {
		if (dataDriverName != null && !dataDriverName.isEmpty())
			put(DATA_DRIVER_NAME_FIELD, dataDriverName);
	}

	
	/**
	 * Getting the minimum rating value (default: 1)
	 * @return Minimum rating value (default: 1)
	 */
	public float getMinRating() {
		return getAsReal(MIN_RATING_FIELD);
	}
	
	
	/**
	 * Getting the maximum rating value (default: 5)
	 * @return Maximum rating value (default: 5)
	 */
	public float getMaxRating() {
		return getAsReal(MAX_RATING_FIELD);
	}
	
	
	/**
	 * Getting the number of rating values per item, example: 5.
	 * The returned value is only valid in case of discrete rating values.
	 * By default, this value is the subtraction: {@link #getMaxRating()} - {@link #getMinRating()} + 1.
	 * @return The number of rating values per item
	 */
	public int getNumberRatingsPerItem() {
		// TODO Auto-generated method stub
		return (int) (getMaxRating() - getMinRating() + 1);
	}
	
	
	/**
	 * Storing meta-data of a dataset in this class. {@link DatasetMetadata} stores essential information about dataset.
	 * minimum rating value, maximum rating value, number of items, number of users, etc.
	 * Currently, this method only stores minimum rating value and maximum rating value.
	 * @param metadata Specific meta-data of a dataset
	 */
	public void setMetadata(DatasetMetadata metadata) {
		if (metadata == null) {
			remove(MIN_RATING_FIELD);
			remove(MAX_RATING_FIELD);
		}
		else {
			put(MIN_RATING_FIELD, metadata.minRating);
			put(MAX_RATING_FIELD, metadata.maxRating);
		}
	}

	
	/**
	 * Retrieving meta-data of a dataset stored previously in this class. {@link DatasetMetadata} stores essential information about dataset.
	 * minimum rating value, maximum rating value, number of items, number of users, etc.
	 * Currently, this method only retrieves minimum rating value and maximum rating value.
	 * @return {@link DatasetMetadata}
	 */
	public DatasetMetadata getMetadata() {
		DatasetMetadata metadata = new DatasetMetadata();
		if (containsKey(MIN_RATING_FIELD))
			metadata.minRating = getMinRating();
		if (containsKey(MAX_RATING_FIELD))
			metadata.maxRating = getMaxRating();
		
		return metadata;
	}
	
	
	@Override
	public Object clone() {
		DataConfig cfg = new DataConfig();
		cfg.putAll(this);
		
		return cfg;
	}


	@Override
	protected Serializable preprocessValue(String key, Serializable value) {
		// TODO Auto-generated method stub
		if (key == null || value == null)
			return null;
		else if (key.equals(STORE_URI_FIELD) && value instanceof String) {
			try {
				String temp = (String) value;
				if (temp.indexOf(":") == -1) {
					xURI uri = xURI.create(temp);
					value = uri.toString();
				}
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
			
			return value;
		}
		else
			return super.preprocessValue(key, value);
	}
	
	
}
