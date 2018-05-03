/**
 * 
 */
package net.hudup.core.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This class extends directly {@link SocketWrapper} class and so it is also a service that sends request to server and then receives response (result) from server, according to socket interaction.
 * However, in the same session, it can send many requests and receive many responses (from server) whereas {@link SocketWrapper} can send/receive only one request/response to/from server at one time.
 * In other words, {@link SocketConnection} is an advanced version of {@link SocketWrapper}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class SocketConnection extends SocketWrapper {

	
	/**
	 * Internal network socket.
	 */
	protected Socket socket = null;
	
	
	/**
	 * Writer for writing data to network socket.
	 */
	protected PrintWriter out = null;
	
	
	/**
	 * Reader for reading data from network socket.
	 */
	protected BufferedReader in = null;
	
	
	/**
	 * Constructor with remote host and remote port.
	 * @param host remote host.
	 * @param port remote port.
	 */
	public SocketConnection(String host, int port) {
		super(host, port);
		// TODO Auto-generated constructor stub
	}


	/**
	 * Connecting to remote server at remote port with specified account, password, and privileges.
	 * @param account specified account.
	 * @param password specified password.
	 * @param priv specified privileges.
	 * @return whether connect successfully.
	 */
	public boolean connect(String account, String password, int priv) {
		try {
			close();
			
			socket = new Socket(host, port);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			boolean validated = validateAccount(account, password, priv);
			if (!validated)
				close();
			return validated;
		}
		catch (Throwable e) {
			e.printStackTrace();
			close();
			logger.error("Socket connects to server fail caused by error " + e.getMessage());
		}
		
		return false;
	}
	
	
	/**
	 * Testing whether the socket is connected.
	 * @return whether the socket is connected.
	 */
	public boolean isConnected() {
		return socket != null && out != null && in != null;
	}
	
	
	@Override
	protected Response sendRequest(Request request) {
		if (!isConnected()) {
			logger.error("Socket not connected");
			return null;
		}
		
		String responseText = null;
		try {
			out.println(request.toJson());
			
			responseText = in.readLine();
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("Socket sends request to server, causes error " + e.getMessage());
			responseText = null;
		}
		
		if (responseText == null)
			return null;
		else
			return Response.parse(responseText);
	}
	
	
	/**
	 * Closing this socket connection together its session.
	 */
	public void close() {
		try {
			if (in != null) {
				in.close();
				in = null;
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
			logger.error("Socket connection fail to close input stream, causes error " + e.getMessage());
		}
		
		try {
			if (out != null) {
				out.close();
				out = null;
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
			logger.error("Socket connection fail to close output stream, causes error " + e.getMessage());
		}

		try {
			if (socket != null) {
				socket.close();
				socket = null;
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
			logger.error("Socket connection fail to close socket, causes error " + e.getMessage());
		}
	}
	
	
}
