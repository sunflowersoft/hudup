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

import org.apache.log4j.Logger;

/**
 * This class is the current implementation of {@code Protocol} interface.
 * It implements all methods of protocol interface for interacting with Hudup server in client-server mechanism.
 * All methods return requests.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ProtocolImpl implements Protocol {

	
	/**
	 * Logger of this class.
	 */
	protected final static Logger logger = Logger.getLogger(Protocol.class);

	
	@Override
	public Request createEstimateRequest(RecommendParam param, Set<Integer> queryIds) {
		Request request = new Request();
		request.action = ESTIMATE;
		request.recommend_param = param;
		request.queryids = queryIds;
		
		return request;
	}
	

	@Override
	public Request createRecommendRequest(RecommendParam param, int maxRecommend) {
		Request request = new Request();
		request.action = RECOMMEND;
		request.recommend_param = param;
		request.max_recommend = maxRecommend;
		
		return request;
	}

	
	@Override
	public Request createRecommendUserRequest(int userId, int maxRecommend) {
		Request request = new Request();
		request.action = RECOMMEND_USER;
		request.userid = userId;
		request.max_recommend = maxRecommend;
		
		return request;
	}
	

	@Override
	public Request createRecommendletRequest(String host, int port,
			String regName, Serializable externalUserId, Serializable externalItemId,
			int maxRecommend, Rating rating) {
		
		Request request = new Request();
		request.action = RECOMMENDLET;
		request.host = host;
		request.port = port;
		request.reg_name = regName;
		request.external_userid =externalUserId;
		request.external_itemid = externalItemId;
		request.max_recommend = maxRecommend;
		request.rating = rating;
		
		return request;
	}

	
	@Override
	public Request createInsertRatingRequest(RatingVector vRating) {
		// TODO Auto-generated method stub
		Request request = new Request();
		request.action = INSERT_RATING;
		request.rating_vector = vRating;
		
		return request;
	}


	@Override
	public Request createUpdateRatingRequest(RatingVector vRating) {
		Request request = new Request();
		request.action = UPDATE_RATING;
		request.rating_vector = vRating;
		
		return request;
		
	}

	
	@Override
	public Request createDeleteRatingRequest(RatingVector vRating) {
		Request request = new Request();
		request.action = DELETE_RATING;
		request.rating_vector = vRating;
		
		return request;
		
	}

	
	@Override
	public Request createGetUserIdsRequest() {
		// TODO Auto-generated method stub
		Request request = new Request();
		request.action = GET_USERIDS;
		
		return request;
	}


	@Override
	public Request createGetUserRatingRequest(int userId) {
		Request request = new Request();
		request.action = GET_USER_RATING;
		request.userid = userId;
		
		return request;
		
	}

	
	@Override
	public Request createGetUserRatingsRequest() {
		Request request = new Request();
		request.action = GET_USER_RATINGS;
		
		return request;
		
	}

	
	@Override
	public Request createGetUserRatingsSqlRequest(String selectSql) {
		Request request = new Request();
		request.action = GET_USER_RATINGS_SQL;
		request.sql = selectSql;
		
		return request;
		
	}
	

	@Override
	public Request createDeleteUserRatingRequest(int userId) {
		Request request = new Request();
		request.action = DELETE_USER_RATING;
		request.userid = userId;
		
		return request;
		
	}

	
	@Override
	public Request createGetUserProfileRequest(int userId) {
		Request request = new Request();
		request.action = GET_USER_PROFILE;
		request.userid = userId;
		
		return request;
		
	}

	
	@Override
	public Request createGetUserProfileByExternalRequest(
			Serializable externalUserId) {
		// TODO Auto-generated method stub
		Request request = new Request();
		request.action = GET_USER_PROFILE_BY_EXTERNAL;
		request.external_userid = externalUserId;

		return request;
	}


	@Override
	public Request createGetUserProfilesRequest() {
		Request request = new Request();
		request.action = GET_USER_PROFILES;
		
		return request;
		
	}
	

	@Override
	public Request createGetUserProfilesSqlRequest(String selectSql) {
		Request request = new Request();
		request.action = GET_USER_PROFILES_SQL;
		request.sql = selectSql;
		
		return request;
		
	}

	
	@Override
	public Request createGetUserAttributeListRequest() {
		Request request = new Request();
		request.action = GET_USER_ATTRIBUTE_LIST;
		
		return request;
	}

	
	@Override
	public Request createInsertUserProfileRequest(Profile profile, ExternalRecord externalRecord) {
		Request request = new Request();
		request.action = INSERT_USER_PROFILE;
		request.profile = profile;
		request.external_record = externalRecord;
		
		return request;
	}

	
	@Override
	public Request createUpdateUserProfileRequest(Profile profile) {
		Request request = new Request();
		request.action = UPDATE_USER_PROFILE;
		request.profile = profile;
		
		return request;
	}
	

	@Override
	public Request createDeleteUserProfileRequest(int userId) {
		Request request = new Request();
		request.action = DELETE_USER_PROFILE;
		request.userid = userId;
		
		return request;
		
	}
	

	@Override
	public Request createGetUserExternalRecordRequest(int userId) {
		Request request = new Request();
		request.action = GET_USER_EXTERNAL_RECORD;
		request.userid = userId;
		
		return request;
	}

	
	@Override
	public Request createGetItemIdsRequest() {
		// TODO Auto-generated method stub
		Request request = new Request();
		request.action = GET_ITEMIDS;
		
		return request;
	}

	
	@Override
	public Request createGetItemRatingRequest(int itemId) {
		Request request = new Request();
		request.action = GET_ITEM_RATING;
		request.itemid = itemId;
		
		return request;
	}

	
	@Override
	public Request createGetItemRatingsRequest() {
		Request request = new Request();
		request.action = GET_ITEM_RATINGS;
		
		return request;
	}
	

	@Override
	public Request createGetItemRatingsSqlRequest(String selectSql) {
		Request request = new Request();
		request.action = GET_ITEM_RATINGS_SQL;
		request.sql = selectSql;
		
		return request;
	}
	

	@Override
	public Request createDeleteItemRatingRequest(int itemId) {
		Request request = new Request();
		request.action = DELETE_ITEM_RATING;
		request.itemid = itemId;
		
		return request;
		
	}
	

	@Override
	public Request createGetItemProfileRequest(int itemId) {
		Request request = new Request();
		request.action = GET_ITEM_PROFILE;
		request.itemid = itemId;
		
		return request;
	}
	

	@Override
	public Request createGetItemProfileByExternalRequest(
			Serializable externalItemId) {
		// TODO Auto-generated method stub
		Request request = new Request();
		request.action = GET_ITEM_PROFILE_BY_EXTERNAL;
		request.external_itemid = externalItemId;

		return request;
	}

	
	@Override
	public Request createGetItemProfilesRequest() {
		Request request = new Request();
		request.action = GET_ITEM_PROFILES;
		
		return request;
		
	}
	

	@Override
	public Request createGetItemProfilesSqlRequest(String selectSql) {
		Request request = new Request();
		request.action = GET_ITEM_PROFILES_SQL;
		request.sql = selectSql;
		
		return request;
		
	}
	

	@Override
	public Request createGetItemAttributeListRequest() {
		Request request = new Request();
		request.action = GET_ITEM_ATTRIBUTE_LIST;
		
		return request;
		
	}
	

	@Override
	public Request createInsertItemProfileRequest(Profile profile, ExternalRecord externalRecord) {
		Request request = new Request();
		request.action = INSERT_ITEM_PROFILE;
		request.profile = profile;
		request.external_record = externalRecord;
		
		return request;
		
	}
	

	@Override
	public Request createUpdateItemProfileRequest(Profile profile) {
		Request request = new Request();
		request.action = UPDATE_ITEM_PROFILE;
		request.profile = profile;
		
		return request;
		
	}
	

	@Override
	public Request createDeleteItemProfileRequest(int itemId) {
		Request request = new Request();
		request.action = DELETE_ITEM_PROFILE;
		request.itemid = itemId;
		
		return request;
		
	}
	

	@Override
	public Request createGetItemExternalRecordRequest(int itemId) {
		Request request = new Request();
		request.action = GET_ITEM_EXTERNAL_RECORD;
		request.itemid = itemId;
		
		return request;
	}

	
	@Override
	public Request createGetNominalRequest(String unit, String attribute) {
		Request request = new Request();
		request.action = GET_NOMINAL;
		request.unit = unit;
		request.attribute = attribute;
		
		return request;
	}
	

	@Override
	public Request createInsertNominalRequest(String unit, String attribute,
			Nominal nominal) {
		// TODO Auto-generated method stub
		Request request = new Request();
		request.action = INSERT_NOMINAL;
		request.unit = unit;
		request.attribute = attribute;
		request.nominal = nominal;
		
		return request;
	}


	@Override
	public Request createUpdateNominalRequest(String unit, String attribute, Nominal nominal) {
		Request request = new Request();
		request.action = UPDATE_NOMINAL;
		request.unit = unit;
		request.attribute = attribute;
		request.nominal = nominal;
		
		return request;
		
	}
	

	@Override
	public Request createDeleteNominalRequest(String unit, String attribute) {
		Request request = new Request();
		request.action = DELETE_NOMINAL;
		request.unit = unit;
		request.attribute = attribute;
		
		return request;
		
	}
	

	@Override
	public Request createGetExternalRecordRequest(InternalRecord internalRecord) {
		// TODO Auto-generated method stub
		Request request = new Request();
		request.action = GET_EXTERNAL_RECORD;
		request.internal_record = internalRecord;
		
		return request;
	}


	@Override
	public Request createInsertExternalRecordRequest(
			InternalRecord internalRecord, ExternalRecord externalRecord) {
		// TODO Auto-generated method stub
		Request request = new Request();
		request.action = INSERT_EXTERNAL_RECORD;
		request.internal_record = internalRecord;
		request.external_record = externalRecord;
		
		return request;
	}


	@Override
	public Request createUpdateExternalRecordRequest(
			InternalRecord internalRecord, ExternalRecord externalRecord) {
		// TODO Auto-generated method stub
		Request request = new Request();
		request.action = UPDATE_EXTERNAL_RECORD;
		request.internal_record = internalRecord;
		request.external_record = externalRecord;
		
		return request;
	}

	
	@Override
	public Request createDeleteExternalRecordRequest(
			InternalRecord internalRecord) {
		// TODO Auto-generated method stub
		Request request = new Request();
		request.action = DELETE_EXTERNAL_RECORD;
		request.internal_record = internalRecord;
		
		return request;
	}

	
	@Override
	public Request createValidateAccountRequest(String account, String password, int privileges) {
		Request request = new Request();
		request.action = VALIDATE_ACCOUNT;
		request.account_name = account;
		request.account_password = password;
		request.account_privileges = privileges;
		
		return request;
		
	}
	

	@Override
	public Request createGetProfileRequest(String unit, Profile condition) {
		Request request = new Request();
		request.action = GET_PROFILE;
		request.unit = unit;
		request.condition = condition;
		
		return request;
		
	}

	
	@Override
	public Request createGetProfilesSqlRequest(String selectSql) {
		Request request = new Request();
		request.action = GET_PROFILES_SQL;
		request.sql = selectSql;
		
		return request;
	}
	

	@Override
	public Request createGetProfileAttributeListRequest(String unit) {
		Request request = new Request();
		request.action = GET_PROFILE_ATTRIBUTE_LIST;
		request.unit = unit;
		
		return request;
		
	}
	

	@Override
	public Request createInsertProfileRequest(String unit, Profile profile) {
		Request request = new Request();
		request.action = INSERT_PROFILE;
		request.unit = unit;
		request.profile = profile;
		
		return request;
		
	}
	

	@Override
	public Request createUpdateProfileRequest(String unit, Profile profile) {
		Request request = new Request();
		request.action = UPDATE_PROFILE;
		request.unit = unit;
		request.profile = profile;
		
		return request;
		
	}
	

	@Override
	public Request createDeleteProfileRequest(String unit, Profile condition) {
		Request request = new Request();
		request.action = DELETE_PROFILE;
		request.unit = unit;
		request.condition = condition;
		
		return request;
		
	}

	
	@Override
	public Request createGetServerConfigRequest() {
		Request request = new Request();
		request.action = GET_SERVER_CONFIG;
		
		return request;
		
	}
	

	@Override
	public Request createGetSessionAttributeRequest(String attribute) {
		Request request = new Request();
		request.action = GET_SESSION_ATTRIBUTE;
		request.attribute = attribute;
		
		return request;
		
	}
	

	@Override
	public Request createGetSnapshotRequest() {
		Request request = new Request();
		request.action = GET_SNAPSHOT;
		
		return request;
		
	}

	
}
