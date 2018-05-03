package net.hudup.listener;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.util.Map;

import net.hudup.core.client.PowerServer;
import net.hudup.core.client.Protocol;
import net.hudup.core.client.Request;
import net.hudup.core.client.Response;
import net.hudup.core.client.Service;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.HtmlParsable;

/**
 * This class is full implementation of delegator. It inherits directly {@link AbstractDelegator}.
 * Delegator is responsible for handling and processing user request represented by {@link Request}. Each time {@link Listener} receives a user request, it creates a respective delegator and passes such request to delegator.
 * After that delegator processes and dispatches the request to the proper binding server. The result of recommendation process from server, represented by {@link Response}, is sent back to users/applications by delegator.
 * In fact, delegator interacts directly with server. However, delegator is a part of {@link Listener} and the client-server interaction is always ensured.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Delegator extends AbstractDelegator {

	/**
	 * 
	 */
	protected PowerServer server = null;

	
	/**
	 * 
	 */
	protected Service service = null;
	
	
	/**
	 * 
	 * @param socket
	 * @param server
	 */
	public Delegator(Socket socket, PowerServer server) {
		super(socket);
		
		this.server = server;
		
		try {
			this.service = server.getService();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.service = null;
			
			logger.error("Delegator fail to be constructed in constructor method, causes error " + e.getMessage());
		}
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			server.incRequest();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Delegator fail to increase server request, causes error " + e.getMessage());
		}
		
		super.run();
		
		try {
			server.decRequest();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Delegator fail to decrease server request, causes error " + e.getMessage());
		}
	}
	
	
	@Override
	protected Request parseRequest(String requestText) {
		Request request = null;
		try {
			String triple = requestText.substring(0, Math.min(3, requestText.length()));
			triple = triple.toUpperCase();
			
			if (triple.equals("GET")) {
				int fileType = HttpUtil.getFileType(requestText);
				
				if (fileType == UNKNOWN_FILE_TYPE) {
					Map<String, String> params = HttpUtil.getParameters(requestText);
					String action = HttpUtil.getAction(requestText);
					if (action != null)
						params.put("action", action);
					else
						params.put("action", Protocol.READ_FILE);
					
					request = Request.parse(params);
					request.protocol = HTTP_PROTOCOL;
					request.file_type = fileType;
					
					String path = HttpUtil.getPath(requestText);
					if (path != null) {
						UriAdapter adapter = new UriAdapter();
						request.file_path = adapter.newPath(path).toString();
						adapter.close();
					}
				}
				else {
					request = new Request();
					request.protocol = HTTP_PROTOCOL;
					request.action = Protocol.READ_FILE;
					request.file_type = fileType;
					
					String path = HttpUtil.getPath(requestText);
					if (path != null) {
						UriAdapter adapter = new UriAdapter();
						request.file_path = adapter.newPath(path).toString();
						adapter.close();
					}
				}
			}
			else {
				request = Request.parse(requestText);
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
			request = null;
			
			logger.error("Delegator fail to parse request, causes error " + e.getMessage());
		}
		
		return request;
	}
	
	
	@Override
	protected boolean handleRequest(Request request, DataOutputStream out) {
		int protocol = request.protocol;
		
		try {
			if (protocol == HDP_PROTOCOL) {
				Response response = processRequest(request);
				out.write( (response.toJson() + "\n").getBytes() );
			}
			else if (protocol == HTTP_PROTOCOL) {
				String action = request.action;
				
				if (action != null && !action.equals(READ_FILE)) {
					Response response = processRequest(request);
					Object result = response.getResult();
					
					if (result != null && result instanceof HtmlParsable) {
						out.write(HttpUtil.createHeader(200, HTML_FILE_TYPE).getBytes());
						String html = ((HtmlParsable)result).toHtml();
						out.write( (html + "\n").getBytes());
					}
					else {
						
						out.write(HttpUtil.createHeader(200, JSON_FILE_TYPE).getBytes());
						out.write( (response.toJson() + "\n").getBytes());
					}
					
				}
				else {
					int type = request.file_type;
					out.write(HttpUtil.createHeader(200, type).getBytes());
					
					xURI uri = xURI.create(request.file_path);
					UriAdapter adapter = new UriAdapter(uri);
					InputStream is = adapter.getInputStream(uri);
					
					while (true) {
						byte[] bytes = new byte[1024];
						int length = is.read(bytes);
						if (length == -1)
							break;
						
						out.write(bytes, 0, length);
					}
					
					is.close();
					adapter.close();
				}
				
			} // else if (protocol.equals(HTTP_PROTOCOL))
			
			return true;
		}
		catch (Throwable e) {
			e.printStackTrace();
			logger.error("Delegator fail to handle request, error is " + e.getMessage());
		}
		
		return false;
	}

	
	/**
	 * Processing {@link Request} directly.
	 * Firstly, it requires remote {@link Service} to process {@link Request} and receives results.
	 * After that it translates results into {@link Response}.
	 * 
	 * This method is called by {@link #handleRequest(Request, DataOutputStream)} method
	 * 
	 * @param request
	 * @return {@link Response}
	 */
	@SuppressWarnings("deprecation")
	private Response processRequest(Request request) {
		// TODO Auto-generated method stub
		
		try {
			String action = request.action;
			
			if (action.equals(Protocol.ESTIMATE))
				return Response.create(service.estimate(request.recommend_param, request.queryids));
			
			else if (action.equals(RECOMMEND))
				return Response.create(service.recommend(request.recommend_param, request.max_recommend));
			
			else if (action.equals(RECOMMEND_USER))
				return Response.create(service.recommend(request.userid, request.max_recommend));
			
			else if (action.equals(RECOMMENDLET))
				return Response.create(service.recommend(request.host, request.port, request.reg_name, 
						request.external_userid, request.external_itemid, request.max_recommend, request.rating));

			else if (action.equals(UPDATE_RATING))
				return Response.create(service.updateRating(request.rating_vector));
			
			else if (action.equals(DELETE_RATING))
				return Response.create(service.deleteRating(request.rating_vector));

			else if (action.equals(GET_USERIDS))
				return Response.create3(new MemFetcher<Integer>(service.getUserIds(), true));

			else if (action.equals(GET_USER_RATING))
				return Response.create(service.getUserRating(request.userid));
			
			else if (action.equals(GET_USER_RATINGS))
				return Response.create(service.getUserRatings());
			
			else if (action.equals(DELETE_USER_RATING))
				return Response.create(service.deleteUserRating(request.userid));

			else if (action.equals(GET_USER_PROFILE))
				return Response.create(service.getUserProfile(request.userid));

			else if (action.equals(GET_USER_PROFILE_BY_EXTERNAL))
				return Response.create(service.getUserProfileByExternal(request.external_userid));

			else if (action.equals(GET_USER_PROFILES))
				return Response.create2(service.getUserProfiles());
			
			else if (action.equals(GET_USER_ATTRIBUTE_LIST))
				return Response.create(service.getUserAttributeList());
			
			else if (action.equals(UPDATE_USER_PROFILE))
				return Response.create(service.updateUserProfile(request.profile));

			else if (action.equals(DELETE_USER_PROFILE))
				return Response.create(service.deleteUserProfile(request.userid));

			else if (action.equals(GET_USER_EXTERNAL_RECORD))
				return Response.create(service.getUserExternalRecord(request.userid));

			else if (action.equals(GET_ITEMIDS))
				return Response.create3(new MemFetcher<Integer>(service.getItemIds(), true));
			
			else if (action.equals(GET_ITEM_RATING))
				return Response.create(service.getItemRating(request.itemid));
			
			else if (action.equals(GET_ITEM_RATINGS))
				return Response.create(service.getItemRatings());
			
			else if (action.equals(DELETE_ITEM_RATING))
				return Response.create(service.deleteItemRating(request.itemid));
			
			else if (action.equals(GET_ITEM_PROFILE))
				return Response.create(service.getItemProfile(request.itemid));
			
			else if (action.equals(GET_ITEM_PROFILE_BY_EXTERNAL))
				return Response.create(service.getItemProfileByExternal(request.external_itemid));

			else if (action.equals(GET_ITEM_PROFILES))
				return Response.create2(service.getItemProfiles());
			
			else if (action.equals(GET_ITEM_ATTRIBUTE_LIST))
				return Response.create(service.getItemAttributeList());

			else if (action.equals(UPDATE_ITEM_PROFILE))
				return Response.create(service.updateItemProfile(request.profile));
			
			else if (action.equals(DELETE_ITEM_PROFILE))
				return Response.create(service.deleteUserProfile(request.itemid));
			
			else if (action.equals(GET_ITEM_EXTERNAL_RECORD))
				return Response.create(service.getItemExternalRecord(request.itemid));

			else if (action.equals(GET_NOMINAL))
				return Response.create(service.getNominal(request.unit, request.attribute));

			else if (action.equals(UPDATE_NOMINAL))
				return Response.create(service.updateNominal(request.unit, request.attribute, request.nominal));
			
			else if (action.equals(DELETE_NOMINAL))
				return Response.create(service.deleteNominal(request.unit, request.attribute));

			else if (action.equals(GET_EXTERNAL_RECORD))
				return Response.create(service.getExternalRecord(request.internal_record));

			else if (action.equals(UPDATE_EXTERNAL_RECORD))
				return Response.create(service.updateExternalRecord(request.internal_record, request.external_record));

			else if (action.equals(DELETE_EXTERNAL_RECORD))
				return Response.create(service.deleteExternalRecord(request.internal_record));

			else if (action.equals(VALIDATE_ACCOUNT))
				return Response.create(validateAccount(
						request.account_name, request.account_password, request.account_privileges));
			
			else if (action.equals(GET_SERVER_CONFIG))
				return Response.create(service.getServerConfig());
			
			else if (action.equals(GET_SESSION_ATTRIBUTE))
				return Response.create(userSession.get(request.attribute).toString());
			
			else if (action.equals(GET_SNAPSHOT))
				return Response.create(service.getSnapshot());
		}
		catch (Throwable e) {
			e.printStackTrace();
			logger.error("Delegator fail to process request, error is " + e.getMessage());
		}
		
		return null;
	}

	
	
	@Override
	protected boolean validateAccount(String account, String password, int privileges) {
		
		try {
			return server.validateAccount(account, password, privileges);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		return false;
	}

	
	
	
	
}
