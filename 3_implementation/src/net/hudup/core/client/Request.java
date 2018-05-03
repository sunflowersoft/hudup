package net.hudup.core.client;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.ExternalRecord;
import net.hudup.core.data.InternalRecord;
import net.hudup.core.data.Nominal;
import net.hudup.core.data.Profile;
import net.hudup.core.data.Rating;
import net.hudup.core.data.RatingVector;
import net.hudup.core.parser.JsonUtil;
import net.hudup.core.parser.TextParserUtil;


/**
 * Note, {@code Request} class represents user request. {@code Request} uses JSON format as exchangeable means in client-server network. Note, JSON (JavaScript Object Notation) is a human-read format used for interchange between many protocols, available at <a href="http://www.json.org/">http://www.json.org</a>.
 * User request includes recommendation request, retrieval request, update request.
 * Each request has a so-called {@code action} which is a short description (short string) of such request. Action can be considered as the purpose of request.
 * For example, the request that user requires a list of recommended items has action &quot;recommend_user&quot;.
 * <br>
 * Request also contains many public variables for specifying its purpose (its result). For example, the recommendation request uses its public variable {@link #recommend_param} to store parameter of recommendation.
 * Such information contains user profile, ratings of such user on many items, list of contexts (place, time, accompany) relevant to user ratings.
 * <br>
 * Both request (represented by {@code Request} class) and response (represented by {@code Response} class) extend directly protocol parameter specified by abstract class {@link ProtocolParam} and so they have a tight interaction.
 * There are two types of request as follows:
 * <br>
 * <ul>
 * <li>
 * Recommendation request is that user prefers to get favorite items. Exactly, this request wraps {@link RecommendParam} which is the first input parameter of methods {@code Recommender.estimate(...)} and {@code Recommender.recommend(...)}.
 * </li>
 * <li>
 * Retrieval or update request is that user wants to retrieve or update her/his ratings and profiles.
 * </li>
 * </ul>
 *  
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Request extends ProtocolParam {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Action of request. For example, the request that user requires a list of recommended items has action &quot;recommend_user&quot;.
	 * This public variable is the most important one.
	 */
	public String                           action                        = null;
	
	/**
	 * Account name. Note, account is the information of a user who has access to Hudup server with her/his privileges. Account is modeled and stored in framework database as profile. 
	 * Account is the information of a user who has access to the server with her/his privileges. Account is modeled and stored in framework database as profile. 
	 */
	public String                           account_name                  = null;
	
	/**
	 * Password of account. Note, account is the information of a user who has access to Hudup server with her/his privileges. Account is modeled and stored in framework database as profile. 
	 * Account is the information of a user who has access to the server with her/his privileges. Account is modeled and stored in framework database as profile. 
	 */
	public String                           account_password              = null;
	
	/**
	 * Privileges of account. Note, account is the information of a user who has access to Hudup server with her/his privileges. Account is modeled and stored in framework database as profile. 
	 * Account is the information of a user who has access to the server with her/his privileges. Account is modeled and stored in framework database as profile. 
	 */
	public int                              account_privileges           = DataConfig.ACCOUNT_ACCESS_PRIVILEGE;
	
	/**
	 * Attribute name. Note, attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type.
	 * Each attribute has always a name.
	 */
	public String                           attribute                     = null;
	
	/**
	 * Condition is the special profile which is used in condition clause {@code where} in SQL statement for processing database, for example.
	 */
	public Profile                          condition                     = null;
	
	/**
	 * Item identifier (item ID) is stored in outside database different from the Hudup database.
	 */
	public Serializable                     external_itemid               = null;
	
	/**
	 * External record represented by {@link ExternalRecord} class is a record (a row in table) which is stored in other database (different from the framework database). It is opposite to internal record represented by {@code InternalRecord} class which is a record stored in the framework database.
	 */
	public ExternalRecord                   external_record               = null;
	
	/**
	 * User identifier (user ID) is stored in outside database different from the Hudup database.
	 */
	public Serializable                     external_userid               = null;
	
	/**
	 * File type such as Hudup file type (*.hdp), text file type (*.txt), CSV file type (*.csv), etc.
	 */
	public int                              file_type                     = 0;
	
	/**
	 * File path such as C:/Hudup/datasets/movielens/rating001.test
	 */
	public String                           file_path                     = null;
	
	/**
	 * Internal record represented by {@code InternalRecord} class is a record (a row in table) stored in the framework database.
	 */
	public InternalRecord                   internal_record               = null;
	
	/**
	 * Item identifier (item ID).
	 */
	public int                              itemid                        = -1;
	
	/**
	 * The host where Hudup server is deployed, for examle, &quot;localhost&quot;.
	 */
	public String                           host                          = null;
	
	/**
	 * Recommendation service produces a list of recommended item. This variable specifies the maximum number of recommended items.
	 */
	public int                              max_recommend                 = 0;
	
	/**
	 * Nominal data indicates discrete and non-number data such as weekdays {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday}. Nominal data is called {@code nominal}, in brief, represented by {@code Nominal} class.
	 */
	public Nominal                          nominal                       = null;
	
	/**
	 * The unit name of nominal (s).
	 * Note, nominal data indicates discrete and non-number data such as weekdays {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday}. Nominal data is called {@code nominal}, in brief, represented by {@code Nominal} class.
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 */
	public String                           nominal_ref_unit              = null;
	
	/**
	 * The port where Hudup server serves incoming requests. As ususal, the port of recommendation server is 10151.
	 */
	public int                              port                          = -1;
	
	/**
	 * Specified profile. Note, {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file).
	 */
	public Profile                          profile                       = null;
	
	/**
	 * Specified set of identifiers (IDs). Such IDs can be user IDs or item IDs.
	 */
	public Set<Integer>                     queryids                      = null;
	
	/**
	 * Note, rating vector represented {@link RatingVector} contains ratings.
	 * If rating vector is user rating vector, it contains all ratings of the same user on many items.
	 * If rating vector is item rating vector, it contains all ratings of many users on the same item. 
	 */
	public RatingVector                     rating_vector                 = null;
	
	/**
	 * This is a rating that a user gives on an item, represented by {@link Rating} class.
	 */
	public Rating                           rating                        = null;
	
	/**
	 * Specified recommendation parameter represented by {@link RecommendParam}. Recommendation parameter  contains important information as follows:
	 * <ul>
	 * <li>Initial rating vector which can be user rating vector or item rating vector.</li>
	 * <li>A profile which can be user profile containing user information (ID, name, etc.) or item profile containing item information (ID, name, price, etc.).</li>
	 * <li>A context list relevant to how and where user (s) rates (rate) on item (s).</li>
	 * <li>Additional information must be serializable.</li>
	 * </ul>
	 */
	public RecommendParam                   recommend_param               = null;
	
	/**
	 * Registered name of recommendlet.
	 * Recommendlet represented by {@link Recommendlet} class is service or a small graphic user interface (GUI) that allows users to see and interact with a list of recommended items.
	 * Recommendlet is now deprecated.
	 */
	public String                           reg_name                      = null;
	
	/**
	 * Specified name of the current session in server.
	 * Hudup server (recommendation server) serves all incoming request in a time interval (a period in mili-seconds).
	 * Such time interval is called session. Each session has a number of attributes. Each attribute is a pair of key and value. Key is also called name.
	 * When it is time-out which means that the server has received no request after the time interval then, the session is destroyed, which causes that all attributes are destroyed.
	 */
	public String                           session_attribute_name        = null;
	
	/**
	 * Specified SQL statement.
	 * Note, SQL which is abbreviation of Structured Query Language is a formal language for accessing relation database.
	 */
	public String                           sql                           = null;
	
	/**
	 * Specified unit name.
	 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
	 */
	public String                           unit                          = null;
	
	/**
	 * User identifier (user ID).
	 */
	public int                              userid                        = -1;
	
	
	/**
	 * Default constructor.
	 */
	public Request() {
		
	}
	
	
	/**
	 * Testing whether or not this request is quit request.
	 * Quit request is the one that requires server to quit, which means that server will be stopped and removed from memory.
	 * 
	 * @return whether or not this request is quit request.
	 */
	public boolean isQuitRequest() {
		return action.equals(Protocol.QUIT);
	}
	
	
	/**
	 * Creating a quit request.
	 * Quit request is the one that requires server to quit, which means that server will be stopped and removed from memory.
	 * @return quit request that requires server to quit.
	 */
	public static Request createQuitRequest() {
		Request request = new Request();
		request.action = Protocol.QUIT;
		
		return request;
	}

	
	@Override
	public String toJson() {
		// TODO Auto-generated method stub
		return JsonUtil.toJson(this);
	}

	
	/**
	 * Parsing (converting) the specified JSON text (text form of a request) into the request.
	 * JSON (JavaScript Object Notation) is a human-read format used for interchange between many protocols, available at <a href="http://www.json.org/">http://www.json.org</a>.
	 * In this framework, JSON is often used to represent a Java object as a text, which means that JSON text is converted into Java object and vice versa.
	 * @param jsonText specified JSON text (text form of a request).
	 * @return request from JSON text.
	 */
	public static Request parse(String jsonText) {
		return (Request) JsonUtil.parseJson(jsonText);
	}
	
	
	/**
	 * Parsing (converting) a set of key-value pairs. Such set is represented as a specified map.
	 * Each pair is called a parameter having a key and an associated value. Both key and value are text strings.
	 * The key is name of a public variable (public field). The value (in text string form) is parsed as real value of such variable.
	 * For example, there is an one-entry map {&quot;queryids&quot;:&quot;1,2,3&quot;}.
	 * The key &quot;queryids&quot; is the name of variable {@link #queryids}.
	 * The text value &quot;1,2,3&quot; is parsed as the set {1,2,3}.
	 * Finally, the variable {@link #queryids} is assigned by the set {1,2,3}. 
	 * 
	 * @param params set of key-value pairs.
	 * @return request from map of parameters. 
	 */
	public static Request parse(Map<String, String> params) {
		Request request = new Request();
		
		Field[] fields = Request.class.getFields();
		for (Field field : fields) {
			String name = field.getName();
			Class<?> type = field.getType();
			if (!params.containsKey(name))
				continue;
			
			String paramValue = params.get(name);
			Object value = null;
			try {
				if (name.equals("queryids")) {
					List<Integer> list = TextParserUtil.parseListByClass(paramValue, Integer.class, ",");
					Set<Integer> set = Util.newSet();
					set.addAll(list);
					
					value = set;
				}
				else if (type == Serializable.class)
					value = paramValue;
				else
					value = TextParserUtil.parseObjectByClass(paramValue, type);
				
				if (value != null)
					field.set(request, value);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		return request;
	}
	
	
}
