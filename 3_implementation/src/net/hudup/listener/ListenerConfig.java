package net.hudup.listener;

import net.hudup.core.Constants;
import net.hudup.core.client.ServerConfig;
import net.hudup.core.data.HiddenText;
import net.hudup.core.logistic.Cipher;
import net.hudup.core.logistic.xURI;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ListenerConfig extends ServerConfig {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * 
	 */
	public final static String listenerConfig = Constants.WORKING_DIRECTORY + "/listenerconfig.xml";

	
	/**
	 * 
	 */
	public static final String REMOTE_HOST_FIELD = changeCase("remote_host");
	
	
	/**
	 * 
	 */
	public static final String REMOTE_PORT_FIELD = changeCase("remote_port");
	
	
	/**
	 * 
	 */
	public static final String REMOTE_ACCOUNT_FIELD = changeCase("remote_account");

	
	/**
	 * 
	 */
	public static final String REMOTE_PASSWORD_FIELD = changeCase("remote_password");
	
	
	/**
	 * 
	 */
	public final static String EXPORT_PORT_FIELD = changeCase("export_port");

	
	/**
	 * 
	 */
	public ListenerConfig() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * 
	 * @param uri
	 */
	public ListenerConfig(xURI uri) {
		super(uri);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		super.reset();
		
		setServerPort(Constants.DEFAULT_LISTENER_PORT);
		setServerTimeout(Constants.DEFAULT_SERVER_TIMEOUT);
		setServerTaskPeriod(Constants.DEFAULT_LISTENER_TASK_PERIOD);
		
		setRemoteHost("localhost");
		setRemotePort(Constants.DEFAULT_SERVER_PORT);
		setRemoteAccount("admin");
		setRemotePassword(new HiddenText("admin"));
		
		setExportPort(Constants.DEFAULT_LISTENER_EXPORT_PORT);
	}
	

	/**
	 * 
	 * @param port
	 */
	public void setExportPort(int port) {
		put(EXPORT_PORT_FIELD, port);
	}
	
	
	/**
	 * 
	 * @return export port used for remote control panel
	 */
	public int getExportPort() {
		return getAsInt(EXPORT_PORT_FIELD);
	}

	
	/**
	 * 
	 * @param host
	 */
	public void setRemoteHost(String host) {
		put(REMOTE_HOST_FIELD, host);
	}
	
	
	/**
	 * 
	 * @return remote host
	 */
	public String getRemoteHost() {
		return getAsString(REMOTE_HOST_FIELD);
	}

	
	/**
	 * 
	 * @param port
	 */
	public void setRemotePort(int port) {
		put(REMOTE_PORT_FIELD, port);
	}
	
	
	/**
	 * 
	 * @return remote port
	 */
	public int getRemotePort() {
		return getAsInt(REMOTE_PORT_FIELD);
	}


	/**
	 * 
	 * @param account
	 */
	public void setRemoteAccount(String account) {
		put(REMOTE_ACCOUNT_FIELD, account);
	}
	
	
	/**
	 * 
	 * @return remote account
	 */
	public String getRemoteAccount() {
		return getAsString(REMOTE_ACCOUNT_FIELD);
	}


	/**
	 * 
	 * @param password
	 */
	public void setRemotePassword(HiddenText password) {
		put(REMOTE_PASSWORD_FIELD, password);
	}
	
	
	/**
	 * 
	 * @return remote password
	 */
	public HiddenText getRemotePassword() {
		return getAsHidden(REMOTE_PASSWORD_FIELD);
	}

	

	
	/**
	 * 
	 * @return {@link RemoteInfo}
	 */
	public RemoteInfo getRemoteInfo() {
		return new RemoteInfo(
				getRemoteHost(), 
				getRemotePort(), getRemoteAccount(), getRemotePassword());
	}

	
	
	/**
	 * 
	 * @param rInfo
	 */
	public void setRemoteInfo(RemoteInfo rInfo) {
		setRemoteHost(rInfo.host);
		setRemotePort(rInfo.port);
		setRemoteAccount(rInfo.account);
		setRemotePassword(rInfo.password);
	}

	
	
	
	@Override
	protected String encrypt(HiddenText hidden) {
		// TODO Auto-generated method stub
		Cipher cipher = new Cipher();
		return cipher.encrypt(hidden.getText());
	}


	@Override
	protected HiddenText decrypt(String text) {
		// TODO Auto-generated method stub
		Cipher cipher = new Cipher();
		return new HiddenText(cipher.decrypt(text));
	}


	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		ListenerConfig cfg = new ListenerConfig();
		cfg.putAll(this);
		
		return cfg;
	}


	
}
