/**
 * 
 */
package net.hudup.core.client;

import java.rmi.Naming;

import net.hudup.core.Constants;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.xURI;


/**
 * This utility class provides methods relevant to connection and interaction in client-server architecture.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public final class DriverManager {
	
	
	/**
	 * Creating {@link SocketConnection} with given URI of server, user name, and password.
	 * {@link SocketConnection} is also a service that sends request to server and then receives response (result) from server, according to socket interaction.
	 * In the same session, {@link SocketConnection} can send many requests and receive many responses (from server)
	 * 
	 * @param uri URI of server
	 * @param username authenticated user name.
	 * @param password authenticated password.
	 * @return {@link SocketConnection} with given URI of server, user name, and password.
	 */
	public final static SocketConnection getSocketConnection(
			xURI uri, String username, String password) {
		
		String host = uri.getHost();
		if (host == null)
			return null;
		
		int port = uri.getPort();
		if (port < 0)
			port = Constants.DEFAULT_SERVER_PORT;
		
		SocketConnection connection = new SocketConnection(host, port);
		if (connection.connect(username, password, DataConfig.ACCOUNT_ACCESS_PRIVILEGE))
			return connection;
		else
			return null;
	}
	
	
	/**
	 * Creating {@link SocketWrapper} with given URI of server, user name, and password.
	 * {@link SocketWrapper} is also a service that sends only one request to server and then receives only one response (result) from server at one time, according to socket interaction.
	 * So every time users want to send/receive request/response to/from server, they must re-create a new instance of {@link SocketWrapper}.
	 * 
	 * @param uri URI of server
	 * @param username authenticated user name.
	 * @param password authenticated password.
	 * @return {@link SocketWrapper} with given URI of server, user name, and password.
	 */
	public final static SocketWrapper getSocketWrapper(xURI uri, String username, String password) {
		String schema = uri.getScheme();
		if (schema == null || !schema.equals(Protocol.HDP_PROTOCOL))
			return null;
		
		String host = uri.getHost();
		if (host == null)
			return null;
		
		int port = uri.getPort();
		if (port < 0)
			port = Constants.DEFAULT_SERVER_PORT;
		
		return new SocketWrapper(host, port);
	}


	/**
	 * Getting a remote service with given remote host, remote port, authenticated user name, and authenticated password.
	 * Service specifies methods to serve user requests according to RMI ((abbreviation of Java Remote Method Invocation)) protocol. These methods focus on providing recommendation, inserting, updating and getting information such as user ratings, user profiles, and item profiles stored in database.
	 * 
	 * @param host remote host.
	 * @param port remote port.
	 * @param username authenticated user name
	 * @param password authenticated password.
	 * @return remote {@link Service} according to RMI protocol.
	 */
	public final static Service getRemoteService(String host, int port, String username, String password) {
		String uri = "rmi://" + host;
		if (port < 1)
			uri += "/" + Protocol.GATEWAY;
		else
			uri += ":" + port + "/" + Protocol.GATEWAY;
			
		
		try {
			Gateway gateway = (Gateway)Naming.lookup(uri);
			if (gateway == null)
				return null;
			return gateway.getRemoteService(username, password);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/**
	 * Getting a reference to server via the gateway of such server with remote host, remote port, authenticated user name, and authenticated password.
	 * By the reference to server, users can start, stop, pause, and configure server remotely.
	 * 
	 * @param host remote host.
	 * @param port remote port.
	 * @param username authenticated user name.
	 * @param password authenticated password.
	 * @return reference to {@link Server} via the gateway of such server.
	 */
	public final static Server getRemoteServer(String host, int port, String username, String password) {
		String uri = "rmi://" + host;
		if (port < 1)
			uri += "/" + Protocol.GATEWAY;
		else
			uri += ":" + port + "/" + Protocol.GATEWAY;
			
		
		try {
			Gateway gateway = (Gateway)Naming.lookup(uri);
			return gateway.getRemoteServer(username, password);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
}
