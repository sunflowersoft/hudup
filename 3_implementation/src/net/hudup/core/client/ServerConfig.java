package net.hudup.core.client;

import net.hudup.core.Constants;
import net.hudup.core.data.SysConfig;
import net.hudup.core.logistic.xURI;

/**
 * This class represents server configuration.
 * It stores additional information of server such as host, port, task period, timeout interval.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ServerConfig extends SysConfig {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Every new information (new entry) that is put into server configuration has a key.
	 * This constant specifies key name of server port.
	 */
	public final static String SERVER_PORT_FIELD = changeCase("server_port");
	
	
	/**
	 * Every new information (new entry) that is put into server configuration has a key.
	 * This constant specifies key name of server task period.
	 * Note, the period in miliseconds that the Hudup server does periodically internal tasks such as data mining and learning knowledge base.
	 */
	public final static String SERVER_TASK_PERIOD_FIELD = changeCase("server_task_period");

	
	/**
	 * Every new information (new entry) that is put into server configuration has a key.
	 * This constant specifies key name of server timeout interval.
	 */
	public final static String SERVER_TIMEOUT_FIELD = changeCase("server_timeout");

	
	/**
	 * Default constructor.
	 */
	public ServerConfig() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * Constructor with URI of server.
	 * @param uri URI of server.
	 */
	public ServerConfig(xURI uri) {
		super(uri);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		super.reset();
		
		setServerPort(Constants.DEFAULT_SERVER_PORT);
		setServerTimeout(Constants.DEFAULT_SERVER_TIMEOUT);
		setServerTaskPeriod(Constants.DEFAULT_SERVER_TASK_PERIOD);
	}

	
	/**
	 * Setting server port by specified port.
	 * @param port specified port.
	 */
	public void setServerPort(int port) {
		put(SERVER_PORT_FIELD, port);
	}
	
	
	/**
	 * Getting server port.
	 * @return server port
	 */
	public int getServerPort() {
		return getAsInt(SERVER_PORT_FIELD);
	}
	
	
	/**
	 * Setting server task period in miliseconds.
	 * Note, the period in miliseconds that the Hudup server does periodically internal tasks such as data mining and learning knowledge base.
	 * @param milisec task period in miliseconds.
	 */
	public void setServerTaskPeriod(int milisec) {
		put(SERVER_TASK_PERIOD_FIELD, milisec);
	}
	
	
	/**
	 * Getting server task period in miliseconds.
	 * Note, the period in miliseconds that the Hudup server does periodically internal tasks such as data mining and learning knowledge base.
	 * @return task period in mili seconds.
	 */
	public int getServerTaskPeriod() {
		return getAsInt(SERVER_TASK_PERIOD_FIELD);
	}
	
	
	
	/**
	 * Setting server timeout in miliseconds.
	 * Note, server is available to serve incoming request in a interval called a timeout in miliseconds.
	 * @param timeout timeout in miliseconds.
	 */
	public void setServerTimeout(int timeout) {
		put(SERVER_TIMEOUT_FIELD, timeout);
	}
	
	
	/**
	 * Getting server timeout in miliseconds.
	 * Note, server is available to serve incoming request in a interval called a timeout in miliseconds.
	 * @return server timeout in miliseconds.
	 */
	public int getServerTimeout() {
		return getAsInt(SERVER_TIMEOUT_FIELD);
	}


	@Override
	public Object clone() {
		ServerConfig cfg = new ServerConfig();
		cfg.putAll(this);
		
		return cfg;
	}
	
	
}
