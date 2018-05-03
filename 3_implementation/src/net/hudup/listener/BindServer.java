package net.hudup.listener;

import net.hudup.core.client.DriverManager;
import net.hudup.core.client.PowerServer;
import net.hudup.core.client.Server;
import net.hudup.core.client.ServerStatusListener;


/**
 * This class is the model of remote (recommendation) server that is bound to {@link ServerStatusListener}, for example, listener or balancer
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class BindServer {
	
	
	/**
	 * 
	 */
	protected RemoteInfo rInfo = null;

	
	/**
	 * 
	 */
	protected PowerServer server = null;
	
	
	/**
	 * {@link ServerStatusListener} is an abstract model of listener or balancer
	 */
	protected ServerStatusListener bind = null;
	
	
	/**
	 * 
	 */
	private BindServer() {
		
	}
	
	
	/**
	 * 
	 * @param rInfo
	 * @param server
	 * @param bind
	 */
	private BindServer(RemoteInfo rInfo, PowerServer server, ServerStatusListener bind) {
		this.rInfo = rInfo;
		this.server = server;
		this.bind = bind;
	}

	
	/**
	 * 
	 */
	public void unbind() {
		if (rInfo == null || server == null || bind == null)
			return;
		
		try {
			server.removeStatusListener(bind);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		rInfo = null;
		server = null;
		bind = null;
	}
	
	
	/**
	 * 
	 * @return {@link PowerServer}
	 */
	public PowerServer getServer() {
		return server;
	}
	
	
	/**
	 * 
	 * @return {@link RemoteInfo}
	 */
	public RemoteInfo getRemoteInfo() {
		return rInfo;
	}
	
	
	/**
	 * 
	 * @return whether server is bound
	 */
	public boolean isBound() {
		return server != null;
	}
	
	
	/**
	 * 
	 * @param host
	 * @param port
	 * @param account
	 * @param password
	 * @param bind
	 * @return {@link BindServer}
	 */
	public static BindServer bind(
			String host, 
			int port, 
			String account, 
			String password, 
			ServerStatusListener bind) {
		
		try {
			Server server = (Server) DriverManager.getRemoteServer(
					host, 
					port, 
					account, 
					password);
			
			if (  (server == null) || !(server instanceof PowerServer) )
				return null;
			
			server.addStatusListener(bind);
			return new BindServer(
					new RemoteInfo(host, port, account, password), 
					(PowerServer)server, bind);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/**
	 * 
	 * @param rInfo
	 * @param bind
	 * @return {@link BindServer}
	 */
	public static BindServer bind(RemoteInfo rInfo, ServerStatusListener bind) {
		return bind(rInfo.host, rInfo.port, rInfo.account, rInfo.password.getText(), bind);
	}
	
	
	
	
	
	
	
}
