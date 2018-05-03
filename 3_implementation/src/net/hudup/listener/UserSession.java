package net.hudup.listener;

import java.util.Map;

import net.hudup.core.Util;
import net.hudup.core.data.DataConfig;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class UserSession {
	
	
	/**
	 * 
	 */
	protected Map<String, Object> userSession = Util.newMap();

	
	/**
	 * 
	 * @return size of this session
	 */
	public int size() {
		return userSession.size();
	}
	
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void put(String key, Object value) {
		userSession.put(key, value);
	}
	
	
	/**
	 * 
	 * @param key
	 * @return value
	 */
	public Object get(String key) {
		return userSession.get(key);
	}
	
	
	/**
	 * 
	 * @param account
	 */
	public void putAccount(String account) {
		userSession.put(DataConfig.ACCOUNT_NAME_FIELD, account);
	}
	
	
	/**
	 * 
	 * @return account
	 */
	public String getAccount() {
		return (String) userSession.get(DataConfig.ACCOUNT_NAME_FIELD);
	}
	
	
	/**
	 * 
	 * @param password
	 */
	public void putPassword(String password) {
		userSession.put(DataConfig.ACCOUNT_PASSWORD_FIELD, password);
	}
	
	
	/**
	 * 
	 * @return password
	 */
	public String getPassword() {
		return (String) userSession.get(DataConfig.ACCOUNT_PASSWORD_FIELD);
	}
	
	
	/**
	 * 
	 * @param priv
	 */
	public void putPriv(int priv) {
		userSession.put(DataConfig.ACCOUNT_PRIVILEGES_FIELD, priv);
	}
	
	
	/**
	 * 
	 * @return privileges
	 */
	public int getPriv() {
		return (Integer) userSession.get(DataConfig.ACCOUNT_PRIVILEGES_FIELD);
	}


	/**
	 * 
	 */
	public void clear() {
		userSession.clear();
	}
	
	
}
