package net.hudup.listener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import net.hudup.core.client.ProtocolImpl;
import net.hudup.core.client.Request;
import net.hudup.core.client.Response;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.Runner;
import net.hudup.core.logistic.RunnerThread;


/**
 * This is abstract class of delegator.
 * Delegator is responsible for handling and processing user request represented by {@link Request}. Each time {@link Listener} receives a user request, it creates a respective delegator and passes such request to delegator.
 * After that delegator processes and dispatches the request to the proper binding server. The result of recommendation process from server, represented by {@link Response}, is sent back to users/applications by delegator.
 * In fact, delegator interacts directly with server. However, delegator is a part of {@link Listener} and the client-server interaction is always ensured.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class AbstractDelegator extends ProtocolImpl implements Runner {

	
	/**
	 * 
	 */
	protected Socket socket = null;
	
	
	/**
	 * 
	 */
	protected boolean started = false;
	
	
	/**
	 * 
	 */
	protected boolean paused = false;
	
	
	/**
	 * 
	 */
	protected UserSession userSession = new UserSession();
	
	
	/**
	 * 
	 * @param socket
	 */
	public AbstractDelegator(Socket socket) {
		this.socket = socket;
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		DataOutputStream out = null;
		BufferedReader in = null;
		
		synchronized (this) {
			try {
				started = true;
				out = new DataOutputStream(socket.getOutputStream());
				
				in = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
			}
			catch (Throwable e) {
				e.printStackTrace();
				logger.error("Delegator fail to listen from client " + socket + ", caused by " + e.getMessage());
			}
			
			notifyAll();
		}
			
		try {
			
			userSession.clear();
			String requestText = null;
			while ( (!socket.isClosed()) && (requestText = in.readLine()) != null ) {
				
				synchronized (this) {
					Request request = parseRequest(requestText);
					if (request == null || request.isQuitRequest()) {
						Response empty = Response.createEmpty(request.protocol);
						out.write( (empty.toJson() + "\n").getBytes());
						break;
					}

					
					if (!initUserSession(request)) {
						Response empty = Response.createEmpty(request.protocol);
						out.write( (empty.toJson() + "\n").getBytes());
						break;
					}
					
					boolean handled = handleRequest(request, out);
					if (request.protocol == HTTP_PROTOCOL)
						break;
					else if (!handled) {
						Response empty = Response.createEmpty(request.protocol);
						out.write( (empty.toJson() + "\n").getBytes());
					}
					
					while (paused) {
						notifyAll();
						wait();
					}
					
				} // synchronized (this)
				
			} // End while
		}
		catch (Throwable e) {
			e.printStackTrace();
			logger.error("Delegator interrupted by error: " + e.getMessage());
		}
		finally {
			userSession.clear();
		}
		
		synchronized (this) {
			try {
				if (in != null)
					in.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
				logger.error("Delegator fail to close input stream, causes error " + e.getMessage());
			}
			
			try {
				if (out != null)
					out.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
				logger.error("Delegator fail to close output stream, causes error " + e.getMessage());
			}

			try {
				if (socket != null && !socket.isClosed())
					socket.close();
				socket = null;
			}
			catch (Throwable e) {
				e.printStackTrace();
				logger.error("Delegator fail to close socket, causes error " + e.getMessage());
			}

			started = false;
			paused = false;
			
			notifyAll();
		}
		
	}
	
	
	/**
	 * 
	 * @param request
	 * @return whether user session initialized successfully
	 */
	protected boolean initUserSession(Request request) {
		if (request.protocol != HDP_PROTOCOL)
			return true;
		
		if (userSession.size() == 0) {
			
			String account = request.account_name;
			String password = request.account_password;
			int privileges = request.account_privileges;
			
			if (account == null || password ==  null || privileges <= 0)
				return false;
			
			if (  ((privileges & DataConfig.ACCOUNT_ACCESS_PRIVILEGE) == 0) ||
				  (!validateAccount(account, password, privileges))  ) {
				
				return false;
			}
			
			userSession.putAccount(account);
			userSession.putPassword(password);
			userSession.putPriv(privileges);
			
			return true;
		}
		else {
			int priv = userSession.getPriv();
			if ( (priv & DataConfig.ACCOUNT_ACCESS_PRIVILEGE) == 0) {
				return false;
			}
			
			return true;
		}
	
	}
	
	
	@Override
	public synchronized void start() {
		if (isStarted())
			return;
		
		new RunnerThread(this).start();
		
		try {
			wait();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Delegator fail to start, causes error " + e.getMessage());
		}
	}

	
	@Override
	public synchronized void pause() {
		if (!isStarted() || isPaused())
			return;
		
		paused  = true;
		
		try {
			wait();
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Delegator fail to pause, causes error " + e.getMessage());
		}
			
	}
	
	
	@Override
	public synchronized void resume() {
		if (!isStarted() || !isPaused())
			return;
		
		paused = false;
		notifyAll();
	}
	
	
	@Override
	public synchronized void stop() {
		if (!isStarted())
			return;
		
		try {
			socket.close();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Delegator fail to close socket when stop, causes error " + e.getMessage());
		}
		
		if (paused) {
			paused = false;
			notifyAll();
		}

		try {
			wait();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Delegator fail to stop, causes error " + e.getMessage());
		}
		
	}
	
	
	@Override
	public boolean isStarted() {
		return started;
	}
	
	
	@Override
	public boolean isPaused() {
		return started && paused;
	}
	
	
	@Override
	public boolean isRunning() {
		return isStarted() && !isPaused();
	}


	/**
	 * Parsing request in text format into {@link Request} object.
	 * It is called by {@link #run()} method
	 * 
	 * @param requestText
	 * @return {@link Request} parsed
	 */
	protected abstract Request parseRequest(String requestText);
	
	
	/**
	 * Processing {@link Request} resulted from {@link #parseRequest(String)}.
	 * It is called by {@link #run()} method
	 * 
	 * @param request
	 * @param out
	 * @return whether handle request successfully
	 */
	protected abstract boolean handleRequest(Request request, DataOutputStream out);
	
	
	/**
	 * 
	 * @param account
	 * @param password
	 * @param privileges
	 * @return whether account is valid
	 */
	protected abstract boolean validateAccount(String account, String password, int privileges);
	
}
