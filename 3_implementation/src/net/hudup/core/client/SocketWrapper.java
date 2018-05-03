/**
 * 
 */
package net.hudup.core.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
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
import net.hudup.core.data.UserRating;


/**
 * This class implements the {@link Service} interface according to socket connection in order to send request to server and then receive response (result) from server.
 * {@link SocketWrapper} is really a service when its implemented methods focus on providing recommendation, inserting, updating and getting information such as user ratings, user profiles, and item profiles stored in database.
 * {@link SocketWrapper} can send/receive only one request/response to/from server at one time.
 * So every time users want to send/receive request/response to/from server, they must re-create a new instance of {@link SocketWrapper}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class SocketWrapper extends ProtocolImpl implements Service {

	
	/**
	 * Remote host for socket connection.
	 */
	protected String host = "localhost";
	
	
	/**
	 * Remote port for socket connection.
	 */
	protected int port = 0;
	
	
	/**
	 * Default constructor with remote host and remote port.
	 * @param host remote host for socket connection.
	 * @param port remote port for socket connection.
	 */
	public SocketWrapper(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		Request request = createEstimateRequest(param, queryIds);
		Response response = sendRequest(request);
		if (response == null)
			return null;
		
		return response.vRating;
		
	}
	
	
	@Override
	public RatingVector recommend(RecommendParam param, int maxRecommend) throws RemoteException {
		Request request = createRecommendRequest(param, maxRecommend);
		Response response = sendRequest(request);
		if (response == null)
			return null;
		
		return response.vRating;
		
	}

	
	@Override
	public RatingVector recommend(int userId, int maxRecommend)
			throws RemoteException {
		// TODO Auto-generated method stub
		Request request = createRecommendUserRequest(userId, maxRecommend);
		Response response = sendRequest(request);
		if (response == null)
			return null;
		
		return response.vRating;
	}


	@Override
	@Deprecated
	public Recommendlet recommend(
			String listenerHost, 
			int listenerPort,
			String regName, 
			Serializable externalUserId, 
			Serializable externalItemId,
			int maxRecommend, 
			Rating rating) throws RemoteException {
		// TODO Auto-generated method stub
		
		Request request = createRecommendletRequest(
				listenerHost, 
				listenerPort, 
				regName, 
				externalUserId, 
				externalItemId, 
				maxRecommend, 
				rating);
		Response response = sendRequest(request);
		if (response == null)
			return null;
		
		
		return response.recommendlet;
	}


	@Override
	public boolean updateRating(RatingVector vRating) throws RemoteException {
		Request request = createUpdateRatingRequest(vRating);
		Response response = sendRequest(request);
		if (response == null)
			return false;
		
		return response.booleanResult != null ? response.booleanResult.get() : false;
	}
	
	
	@Override
	public boolean updateRating(int userId, int itemId, Rating rating)
			throws RemoteException {
		// TODO Auto-generated method stub
		RatingVector vRating = new UserRating(userId);
		vRating.put(itemId, rating);
		
		return updateRating(vRating);
	}


	@Override
	public boolean deleteRating(RatingVector vRating) throws RemoteException {
		// TODO Auto-generated method stub

		Request request = createDeleteRatingRequest(vRating);
		Response response = sendRequest(request);
		if (response == null)
			return false;
		
		return response.booleanResult != null ? response.booleanResult.get() : false;
	}


	@Override
	public Fetcher<Integer> getUserIds() throws RemoteException {
		// TODO Auto-generated method stub
		Request request = createGetUserIdsRequest();
		Response response = sendRequest(request);
		if (response == null)
			return null;
		
		return response.fId;
	}


	@Override
	public RatingVector getUserRating(int userId) throws RemoteException {
		// TODO Auto-generated method stub
		Request request = createGetUserRatingRequest(userId);
		Response response = sendRequest(request);
		if (response == null)
			return null;
		
		return response.vRating;
	}

	
	@Override
	public Fetcher<RatingVector> getUserRatings() throws RemoteException {
		// TODO Auto-generated method stub
		
		Request request = createGetUserRatingsRequest();
		Response response = sendRequest(request);
		if (response == null)
			return null;
		
		return response.fRating;
	}


	@Override
	public boolean deleteUserRating(int userId) throws RemoteException {
		// TODO Auto-generated method stub
		Request request = createDeleteUserRatingRequest(userId);
		Response response = sendRequest(request);
		if (response == null)
			return false;
		
		return response.booleanResult != null ? response.booleanResult.get() : false;
	}

	
	@Override
	public Profile getUserProfile(int userId) throws RemoteException {
		Request request = createGetUserProfileRequest(userId);
		Response response = sendRequest(request);
		if (response == null)
			return null;
		
		return response.profile;
		
	}

	
	@Override
	public Profile getUserProfileByExternal(Serializable externalUserId)
			throws RemoteException {
		// TODO Auto-generated method stub
		Request request = createGetUserProfileByExternalRequest(externalUserId);
		Response response = sendRequest(request);
		if (response == null)
			return null;
		
		return response.profile;
	}


	@Override
	public Fetcher<Profile> getUserProfiles() throws RemoteException {
		// TODO Auto-generated method stub
		
		Request request = createGetUserProfilesRequest();
		Response response = sendRequest(request);
		if (response == null)
			return null;
		
		return response.fProfile;
	}


	@Override
	public boolean updateUserProfile(Profile user) throws RemoteException {
		// TODO Auto-generated method stub
		Request request = createUpdateUserProfileRequest(user);
		Response response = sendRequest(request);
		if (response == null)
			return false;
		
		return response.booleanResult != null ? response.booleanResult.get() : false;
	}


	@Override
	public boolean deleteUserProfile(int userId) throws RemoteException {
		// TODO Auto-generated method stub

		Request request = createDeleteUserProfileRequest(userId);
		Response response = sendRequest(request);
		if (response == null)
			return false;
		
		return response.booleanResult != null ? response.booleanResult.get() : false;
	}

	
	@Override
	public AttributeList getUserAttributeList() throws RemoteException {
		
		Request request = createGetUserAttributeListRequest();
		Response response = sendRequest(request);
		if (response == null)
			return null;
		
		return response.attributeList;
	}
	
	
	@Override
	public ExternalRecord getUserExternalRecord(int userId) throws RemoteException {
		// TODO Auto-generated method stub
		Request request = createGetUserExternalRecordRequest(userId);
		Response response = sendRequest(request);
		if (response == null)
			return null;
		
		return response.externalRecord;
	}


	@Override
	public Fetcher<Integer> getItemIds() throws RemoteException {
		// TODO Auto-generated method stub
		Request request = createGetItemIdsRequest();
		Response response = sendRequest(request);
		if (response == null)
			return null;
		
		return response.fId;
	}

	
	@Override
	public RatingVector getItemRating(int itemId) throws RemoteException {
		// TODO Auto-generated method stub
		
		Request request = createGetItemRatingRequest(itemId);
		Response response = sendRequest(request);
		if (response == null)
			return null;
		
		return response.vRating;
	}


	@Override
	public Fetcher<RatingVector> getItemRatings() throws RemoteException {
		// TODO Auto-generated method stub
		Request request = createGetItemRatingsRequest();
		Response response = sendRequest(request);
		if (response == null)
			return null;
		
		return response.fRating;
	}


	@Override
	public boolean deleteItemRating(int itemId) throws RemoteException {
		// TODO Auto-generated method stub
		Request request = createDeleteUserRatingRequest(itemId);
		Response response = sendRequest(request);
		if (response == null)
			return false;
		
		return response.booleanResult != null ? response.booleanResult.get() : false;
	}


	@Override
	public Profile getItemProfile(int itemId) throws RemoteException {
		Request request = createGetItemProfileRequest(itemId);
		Response response = sendRequest(request);
		if (response == null)
			return null;
		
		return response.profile;
		
	}

	
	@Override
	public Profile getItemProfileByExternal(Serializable externalItemId)
			throws RemoteException {
		// TODO Auto-generated method stub
		Request request = createGetItemProfileByExternalRequest(externalItemId);
		Response response = sendRequest(request);
		if (response == null)
			return null;
		
		return response.profile;
	}

	
	@Override
	public Fetcher<Profile> getItemProfiles() throws RemoteException {
		// TODO Auto-generated method stub
		
		Request request = createGetItemProfilesRequest();
		Response response = sendRequest(request);
		if (response == null)
			return null;
		
		return response.fProfile;
	}


	@Override
	public boolean updateItemProfile(Profile item) throws RemoteException {
		// TODO Auto-generated method stub
		Request request = createUpdateItemProfileRequest(item);
		Response response = sendRequest(request);
		if (response == null)
			return false;
		
		return response.booleanResult != null ? response.booleanResult.get() : false;
	}


	@Override
	public boolean deleteItemProfile(int itemId) throws RemoteException {
		// TODO Auto-generated method stub
		
		Request request = createDeleteItemProfileRequest(itemId);
		Response response = sendRequest(request);
		if (response == null)
			return false;
		
		return response.booleanResult != null ? response.booleanResult.get() : false;
	}


	@Override
	public AttributeList getItemAttributeList() throws RemoteException {
		
		Request request = createGetItemAttributeListRequest();
		Response response = sendRequest(request);
		if (response == null)
			return null;
		
		return response.attributeList;
	}

	
	@Override
	public ExternalRecord getItemExternalRecord(int itemId)
			throws RemoteException {
		// TODO Auto-generated method stub

		Request request = createGetItemExternalRecordRequest(itemId);
		Response response = sendRequest(request);
		if (response == null)
			return null;
		
		return response.externalRecord;
	}


	@Override
	public NominalList getNominal(String unitName, String attribute) throws RemoteException {
		// TODO Auto-generated method stub

		Request request = createGetNominalRequest(unitName, attribute);
		Response response = sendRequest(request);
		if (response == null)
			return null;
		
		return response.nominalList;
	}

	
	@Override
	public boolean updateNominal(String unitName, String attribute, Nominal nominal) throws RemoteException {
		
		Request request = createUpdateNominalRequest(unitName, attribute, nominal);
		Response response = sendRequest(request);
		if (response == null)
			return false;
		
		return response.booleanResult != null ? response.booleanResult.get() : false;
	}

	
	@Override
	public boolean deleteNominal(String unitName, String attribute)
			throws RemoteException {
		// TODO Auto-generated method stub

		Request request = createDeleteNominalRequest(unitName, attribute);
		Response response = sendRequest(request);
		if (response == null)
			return false;
		
		return response.booleanResult != null ? response.booleanResult.get() : false;
	}


	@Override
	public ExternalRecord getExternalRecord(InternalRecord internalRecord) throws RemoteException {
		// TODO Auto-generated method stub
		Request request = createGetExternalRecordRequest(internalRecord);
		Response response = sendRequest(request);
		if (response == null)
			return null;
		
		return response.externalRecord;
	}


	@Override
	public boolean updateExternalRecord(InternalRecord internalRecord,
			ExternalRecord externalRecord) throws RemoteException {
		// TODO Auto-generated method stub
		Request request = createUpdateExternalRecordRequest(internalRecord, externalRecord);
		Response response = sendRequest(request);
		if (response == null)
			return false;
		
		return response.booleanResult != null ? response.booleanResult.get() : false;
	}


	@Override
	public boolean deleteExternalRecord(InternalRecord internalRecord)
			throws RemoteException {
		// TODO Auto-generated method stub
		Request request = createDeleteExternalRecordRequest(internalRecord);
		Response response = sendRequest(request);
		if (response == null)
			return false;
		
		return response.booleanResult != null ? response.booleanResult.get() : false;
	}


	@Override
	public boolean validateAccount(String account, String password, int privileges) throws RemoteException {
		Request request = createValidateAccountRequest(account, password, privileges);
		Response response = sendRequest(request);
		if (response == null)
			return false;
		
		return response.booleanResult != null ? response.booleanResult.get() : false;
	}
	
	
	@Override
	public Profile getProfile(String profileUnit, Profile condition)
			throws RemoteException {
		// TODO Auto-generated method stub
		
		Request request = createGetProfileRequest(profileUnit, condition);
		Response response = sendRequest(request);
		if (response == null)
			return null;

		return response.profile;
	}


	@Override
	public AttributeList getProfileAttributeList(String unitName)
			throws RemoteException {
		// TODO Auto-generated method stub
		Request request = createGetProfileAttributeListRequest(unitName);
		Response response = sendRequest(request);
		if (response == null)
			return null;
		
		return response.attributeList;
	}


	@Override
	public boolean updateProfile(String profileUnit, Profile profile)
			throws RemoteException {
		// TODO Auto-generated method stub
		Request request = createUpdateProfileRequest(profileUnit, profile);
		Response response = sendRequest(request);
		if (response == null)
			return false;
		
		return response.booleanResult != null ? response.booleanResult.get() : false;
	}


	@Override
	public boolean deleteProfile(String profileUnit, Profile condition)
			throws RemoteException {
		// TODO Auto-generated method stub
		
		Request request = createDeleteProfileRequest(profileUnit, condition);
		Response response = sendRequest(request);
		if (response == null)
			return false;
		
		return response.booleanResult != null ? response.booleanResult.get() : false;
	}

	
	@Override
	public DataConfig getServerConfig() throws RemoteException {
		Request request = createGetServerConfigRequest();
		Response response = sendRequest(request);
		if (response == null)
			return null;
		
		return response.dataConfig;
	}
	
	
	/**
	 * Retrieving an attribute having the specified name of the current session in server.
	 * Hudup server (recommendation server) serves all incoming request in a time interval (a period in mili-seconds).
	 * Such time interval is called session. Each session has a number of attributes. Each attribute is a pair of key and value. Key is also called name.
	 * When it is time-out which means that the server has received no request after the time interval then, the session is destroyed, which causes that all attributes are destroyed.
	 * @param attribute specified attribute name.
	 * @return text value of the attribute having the specified name of the current session in server.
	 */
	public String getSessionAttribute(String attribute) {
		Request request = createGetSessionAttributeRequest(attribute);
		Response response = sendRequest(request);
		if (response == null)
			return null;
		
		return response.stringResult;
	}
	
	
	@Override
	public Snapshot getSnapshot() throws RemoteException {
		// TODO Auto-generated method stub
		Request request = createGetSnapshotRequest();
		Response response = sendRequest(request);
		if (response == null)
			return null;
		
		return response.snapshot;
	}


	/**
	 * Sending the quit request to server.
	 * Quit request is the one that requires server to quit, which means that server will be stopped and removed from memory.
	 */
	public void sendQuitRequest() {
		Request request = Request.createQuitRequest();
		sendRequest(request);
	}
	
	
	/**
	 * Sending a request to server and waiting for receiving the response from server.
	 * This method is called by most of methods to serve incoming request.
	 * @param request specified request.
	 * @return {@link Response} received from server.
	 */
	protected Response sendRequest(Request request) {
		String responseText = null;
		PrintWriter out = null;
		BufferedReader in = null;
		Socket socket = null;
		try {
			socket = new Socket(host, port);
			
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			
			out.println(request.toJson());
			
			responseText = in.readLine();
			
		}
		catch (Throwable e) {
			e.printStackTrace();
			responseText = null;
			
			logger.error("Socket warapper fail to send request to server, caused by " + e.getMessage());
			
		}
		finally {
			if (out != null) {
				try {
					out.close();
				}
				catch (Throwable e) {
					e.printStackTrace();
					logger.error("Socket wrapper fail to close output stream, causes error " + e.getMessage());
				}
			}
			
			if (in != null) {
				try {
					in.close();
				}
				catch (Throwable e) {
					e.printStackTrace();
					logger.error("Socket wrapper fail to close input stream, causes error " + e.getMessage());
				}
			}

			if (socket != null && !socket.isClosed()) {
				try {
					socket.close();
				}
				catch (Throwable e) {
					e.printStackTrace();
					logger.error("Socket wrapper fail to close socket, causes error " + e.getMessage());
				}
			}
			
		} // end finally
		
		if (responseText == null)
			return null;
		else
			return Response.parse(responseText);
	}
	
	
}
