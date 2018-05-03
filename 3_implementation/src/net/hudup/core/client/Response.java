package net.hudup.core.client;

import net.hudup.core.data.AttributeList;
import net.hudup.core.data.BooleanWrapper;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.ExternalRecord;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.NominalList;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.data.Snapshot;
import net.hudup.core.parser.JsonUtil;


/**
 * {@code Response} class represents result of Hudup services such as recommendation task, updating database, information retrieval.
 * Response has many public variables for storing different results. Each response corresponds with a particular request ({@code Request}) class.
 * {@code Response} uses JSON format as exchangeable means. Note, JSON (JavaScript Object Notation) is a human-read format used for interchange between many protocols, available at <a href="http://www.json.org/">http://www.json.org</a>.
 * <ul>
 * <li>
 * In case of recommendation request, response is the list of recommended items returned from service layer.
 * Exactly, response wraps the returned {@link RatingVector} of methods {@code Recommender.estimate(...)} and {@code Recommender.recommend(...)}.
 * The public variable {@link #vRating} refers to such rating vector.
 * </li>
 * <li>
 * For update request, response is the output variable indicating whether or not update request is successful.
 * </li>
 * <li>
 * For retrieval request, response is the piece of information stored in {@code Dataset} or {@code KBase}. If information is stored in {@code Dataset}, response often wraps a returned {@code Snapshot}.
 * Note, {@code dataset} includes essentially rating matrix, user profiles, item profiles and context information.
 * Snapshot is defined as an image of piece of dataset and knowledge base ({@code KBase}) at certain time point.
 * </li>
 * </ul>
 * Both request (represented by {@code Request} class) and response (represented by {@code Response} class) extend directly protocol parameter specified by abstract class {@link ProtocolParam} and so they have a tight interaction.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Response extends ProtocolParam {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Result as attribute list.
	 * Note, attribute list contains many attributes. Attribute represented by {@code Attribute} class indicates the data type, which is also a wrapper of data type.
	 * {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
	 * Profile uses attribute to specify its data types and so profile owns a reference to an attribute list.
	 * Please see {@code AttributeList} class for details about attribute list.
	 */
	public AttributeList             attributeList       = null;
	
	/**
	 * Returned result is boolean value which wrapped as {@link BooleanWrapper} class.
	 */
	public BooleanWrapper            booleanResult       = null;
	
	/**
	 * Result as configuration.
	 */
	public DataConfig                dataConfig          = null;
	
	/**
	 * Fetcher of identifiers. Such identifiers can be of items or users.
	 * {@link Fetcher} is the interface for iterating each element of an associated collection.
	 */
	public Fetcher<Integer>          fId                 = null;
	
	/**
	 * Fetcher to retrieves many profiles.
	 * {@link Fetcher} is the interface for iterating each element of an associated collection.
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 */
	public Fetcher<Profile>          fProfile            = null;
	
	/**
	 * Fetcher to retrieves many rating vectors.
	 * {@link Fetcher} is the interface for iterating each element of an associated collection.
	 * Note, rating vector represented {@link RatingVector} contains ratings.
	 * If rating vector is user rating vector, it contains all ratings of the same user on many items.
	 * If rating vector is item rating vector, it contains all ratings of many users on the same item. 
	 */
	public Fetcher<RatingVector>     fRating             = null;
	
	/**
	 * Result as nominal list.
	 * Nominal list is a list of many nominal (s), specified by {@link NominalList}.
	 * Nominal data indicates discrete and non-number data such as weekdays {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday}. Nominal data is called {@code nominal}, in brief, represented by {@code Nominal} class.
	 */
	public NominalList               nominalList         = null;
	
	/**
	 * Result as profile.
	 * Note, {@code profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute. Profile uses a attribute list to specify its data types. Every object can be modeled as profile to be stored in archive (file). 
	 */
	public Profile                   profile             = null;
	
	/**
	 * Result as recommendlet.
	 * Recommendlet represented by {@code Recommendlet} class is a service or a small graphic user interface (GUI) that allows users to see and interact with a list of recommended items.
	 * Recommendlet is now deprecated.
	 */
	@Deprecated
	public Recommendlet              recommendlet        = null;
	
	/**
	 * Result as snapshot.
	 * Snapshot is an image of dataset owned by Hudup server at current time point, stored in memory. Snapshot is represented by {@link Snapshot} class.
	 */
	public Snapshot                  snapshot            = null;
	
	/**
	 * Result as string.
	 */
	public String                    stringResult        = null;
	
	/**
	 * Result as rating vector.
	 * Note, rating vector represented {@link RatingVector} contains ratings.
	 * If rating vector is user rating vector, it contains all ratings of the same user on many items.
	 * If rating vector is item rating vector, it contains all ratings of many users on the same item. 
	 */
	public RatingVector              vRating             = null;
	
	/**
	 * Result as external record.
	 * Note, external record represented by {@link ExternalRecord} class is a record (a row in table) which is stored in other database (different from the framework database). It is opposite to internal record represented by {@code InternalRecord} class which is a record stored in the framework database.
	 */
	public ExternalRecord            externalRecord      = null;
	
	
	/**
	 * Default constructor.
	 */
	private Response() {
		
	}
	
	
	/**
	 * Getting the result which is one of public variables, as an object  
	 * @return result which is one of public variables, as an object.
	 */
	public Object getResult() {
		if (attributeList != null)
			return attributeList;
		
		else if (booleanResult != null)
			return booleanResult;
		
		else if (dataConfig != null)
			return dataConfig;
		
		else if (fRating != null)
			return fRating;
		
		else if (fProfile != null)
			return fProfile;
		
		else if (fId != null)
			return fId;

		else if (nominalList != null)
			return nominalList;
		
		else if (profile != null)
			return profile;
		
		else if (recommendlet != null)
			return recommendlet;
		
		else if (snapshot != null)
			return snapshot;
		
		else if (stringResult != null)
			return stringResult;
		
		else if (vRating != null)
			return vRating;
		
		else if (externalRecord != null)
			return externalRecord;

		else
			return null;
	}
	
	
	/**
	 * Creating empty response according protocol. In current implementation, there are two protocols identified by two intergers as follows:
	 * <ul>
	 * <li>Hudup protocol (HDP): specified by integer constant {@code Protocol#HDP_PROTOCOL}.</li>
	 * <li>HTTP protocol (HTTP): specified by integer constant {@code Protocol#HTTP_PROTOCOL}.</li>
	 * <li></li>
	 * </ul>
	 * @param protocol specified protocol identified by an integer.
	 * @return empty response.
	 */
	public static Response createEmpty(int protocol) {
		Response response = new Response();
		response.protocol = protocol;
		
		return response;
	}

	
	/**
	 * Creating result as an attribute list, please see variable {@link #attributeList}.
	 * @param attributeList specified attribute list.
	 * @return {@link AttributeList} response.
	 */
	public static Response create(AttributeList attributeList) {
		Response response = new Response();
		response.attributeList = attributeList;
		
		return response;
	}
	
	
	/**
	 * Creating result (response) as boolean value, please see variable {@link #booleanResult}.
	 * @param booleanResult specified boolean value.
	 * @return boolean response.
	 */
	public static Response create(boolean booleanResult) {
		Response response = new Response();
		response.booleanResult = new BooleanWrapper(booleanResult);
		
		return response;
	}


	/**
	 * Creating result (response) as configuration, please see variable {@link #dataConfig}.
	 * @param dataConfig specified data configuration.
	 * @return {@link DataConfig} response.
	 */
	public static Response create(DataConfig dataConfig) {
		Response response = new Response();
		response.dataConfig = dataConfig;
		
		return response;
	}

	
	/**
	 * Creating result (response) as fetcher of many rating vectors, please see variable {@link #fRating}.
	 * @param fRating fetcher of many rating vectors
	 * @return result (response) as fetcher of many rating vectors.
	 */
	public static Response create(Fetcher<RatingVector> fRating) {
		Response response = new Response();
		response.fRating = fRating;
		
		return response;
	}

	
	/**
	 * Creating result (response) as fetcher of many profiles, please see variable {@link #fProfile}.
	 * @param fProfile specified fetcher of many profiles.
	 * @return result (response) as fetcher of many profiles.
	 */
	public static Response create2(Fetcher<Profile> fProfile) {
		Response response = new Response();
		response.fProfile = fProfile;
		
		return response;
	}

	
	/**
	 * Creating result (response) as fetcher of many identifiers, please see variable {@link #fId}.
	 * @param fId fetcher of many identifiers.
	 * @return result (response) as fetcher of many identifiers.
	 */
	public static Response create3(Fetcher<Integer> fId) {
		Response response = new Response();
		response.fId = fId;
		
		return response;
	}

	
	/**
	 * Creating result (response) as nominal list, please see variable {@link #nominalList}.
	 * @param nominalList specified nominal list.
	 * @return result (response) as nominal list.
	 */
	public static Response create(NominalList nominalList) {
		Response response = new Response();
		response.nominalList = nominalList;
		
		return response;
	}

	
	/**
	 * Creating result (response) as profile, please see variable {@link #profile}.
	 * @param profile specified profile.
	 * @return result (response) as profile.
	 */
	public static Response create(Profile profile) {
		Response response = new Response();
		response.profile = profile;
		
		return response;
	}
	
	
	/**
	 * Creating result (response) as recommendlet, please see variable {@link #recommendlet}.
	 * @param recommendlet specified recommendlet.
	 * @return result (response) as recommendlet.
	 */
	@Deprecated
	public static Response create(Recommendlet recommendlet) {
		Response response = new Response();
		response.recommendlet = recommendlet;
		
		return response;
	}

	
	/**
	 * Creating result (response) as snapshot, please see variable {@link #snapshot}.
	 * @param snapshot specified snapshot.
	 * @return result (response) as snapshot.
	 */
	public static Response create(Snapshot snapshot) {
		Response response = new Response();
		response.snapshot = snapshot;
		
		return response;
	}

	
	/**
	 * Creating result (response) as string, please see variable {@link #stringResult}.
	 * @param stringResult specified string.
	 * @return string response.
	 */
	public static Response create(String stringResult) {
		Response response = new Response();
		response.stringResult = stringResult;
		
		return response;
	}

	
	/**
	 * Creating result (response) as rating vector, please see variable {@link #vRating}.
	 * @param vRating specified rating vector.
	 * @return result (response) as rating vector.
	 */
	public static Response create(RatingVector vRating) {
		Response response = new Response();
		response.vRating = vRating;
		
		return response;
	}

	
	/**
	 * Creating result (response) as external record, please see variable {@link #externalRecord}.
	 * @param externalRecord specified external record.
	 * @return result (response) as external record.
	 */
	public static Response create(ExternalRecord externalRecord) {
		Response response = new Response();
		response.externalRecord = externalRecord;
		
		return response;
	}

	
	@Override
	public String toJson() {
		// TODO Auto-generated method stub
		return JsonUtil.toJson(this);
	}


	/**
	 * Parsing (converting) the specified JSON text (text form of a request) into the response.
	 * JSON (JavaScript Object Notation) is a human-read format used for interchange between many protocols, available at <a href="http://www.json.org/">http://www.json.org</a>.
	 * In this framework, JSON is often used to represent a Java object as a text, which means that JSON text is converted into Java object and vice versa.
	 * @param jsonText specified JSON text (text form of a response).
	 * @return response from JSON text.
	 */
	public static Response parse(String jsonText) {
		return (Response) JsonUtil.parseJson(jsonText);
	}
	
	
}
