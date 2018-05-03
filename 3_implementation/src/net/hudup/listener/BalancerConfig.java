package net.hudup.listener;

import java.awt.Component;
import java.io.Serializable;

import javax.swing.JOptionPane;

import net.hudup.core.Constants;
import net.hudup.core.logistic.xURI;
import net.hudup.listener.ui.RemoteInfoDlg;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class BalancerConfig extends ListenerConfig {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public final static String balancerConfig = Constants.WORKING_DIRECTORY + "/balancerconfig.xml";

	
	/**
	 * 
	 */
	public final static String BALANCER_PORT_FIELD = changeCase("balancer_port");

	
	/**
	 * 
	 */
	public final static String BALANCER_TIMEOUT_FIELD = changeCase("balancer_timeout");

	
	/**
	 * 
	 */
	public final static String BALANCER_TASK_PERIOD_FIELD = changeCase("balancer_task_period");

	
	/**
	 * 
	 */
	public final static String REMOTE_INFO_LIST = changeCase("remote_info_list");

	
	/**
	 * 
	 */
	public BalancerConfig() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * 
	 * @param uri
	 */
	public BalancerConfig(xURI uri) {
		super(uri);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		super.reset();

		remove(REMOTE_HOST_FIELD);
		remove(REMOTE_PORT_FIELD);
		remove(REMOTE_ACCOUNT_FIELD);
		remove(REMOTE_PASSWORD_FIELD);
		
		setServerPort(Constants.DEFAULT_BALANCER_PORT);
		setServerTimeout(Constants.DEFAULT_SERVER_TIMEOUT);
		setServerTaskPeriod(Constants.DEFAULT_LISTENER_TASK_PERIOD);
		
		clearRemoteInfoList();
		RemoteInfo rInfo = 
				new RemoteInfo("localhost", Constants.DEFAULT_SERVER_PORT, "admin", "admin");
		addRemoteInfo(rInfo);
		
		setExportPort(Constants.DEFAULT_BALANCER_EXPORT_PORT);

	}

	
	/**
	 * 
	 * @return {@link RemoteInfoList}
	 */
	public RemoteInfoList getRemoteInfoList() {
		String text = getAsString(REMOTE_INFO_LIST);
		RemoteInfoList rInfoList = new RemoteInfoList();
		
		if (text != null)
			rInfoList.parseText(text);
		return rInfoList;
	}
	
	
	/**
	 * 
	 * @param rInfoList
	 */
	public void setRemoteInfoList(RemoteInfoList rInfoList) {
		String text = rInfoList.toText();
		put(REMOTE_INFO_LIST, text);
	}
	
	
	/**
	 * 
	 */
	public void clearRemoteInfoList() {
		put(REMOTE_INFO_LIST, "");
	}
	
	
	/**
	 * 
	 * @param rInfo
	 */
	public void addRemoteInfo(RemoteInfo rInfo) {
		RemoteInfoList rInfoList = getRemoteInfoList();
		rInfoList.add(rInfo);
		
		setRemoteInfoList(rInfoList);
	}
	
	
	
	/**
	 * 
	 * @param index
	 */
	public void removeRemoteInfo(int index) {
		RemoteInfoList rInfoList = getRemoteInfoList();
		rInfoList.remove(index);
		
		setRemoteInfoList(rInfoList);
	}
	
	
	/**
	 * 
	 * @param host
	 * @param port
	 */
	public void removeRemoteInfo(String host, int port) {
		RemoteInfoList rInfoList = getRemoteInfoList();
		rInfoList.remove(host, port);
		
		setRemoteInfoList(rInfoList);
	}
	
	
	/**
	 * 
	 * @return count of remote servers
	 */
	public int getRemoteInfoCount() {
		RemoteInfoList rInfoList = getRemoteInfoList();
		return rInfoList.size();
	}
	
	
	/**
	 * 
	 * @param index
	 * @return {@link RemoteInfo}
	 */
	public RemoteInfo getRemoteInfo(int index) {
		RemoteInfoList rInfoList = getRemoteInfoList();
		return rInfoList.get(index);
	}
	
	
	/**
	 * 
	 * @param index
	 * @param rInfo
	 */
	public void setRemoteInfo(int index, RemoteInfo rInfo) {
		RemoteInfoList rInfoList = getRemoteInfoList();
		rInfoList.set(index, rInfo);
		
		setRemoteInfoList(rInfoList);
	}


	@Override
	public Serializable userEdit(Component comp, String key, Serializable defaultValue) {
		// TODO Auto-generated method stub
		if (!key.equals(REMOTE_INFO_LIST))
			return super.userEdit(comp, key, defaultValue);
		
		RemoteInfoDlg rInfoDlg = new RemoteInfoDlg(comp, getRemoteInfoList());
		RemoteInfoList result = rInfoDlg.getResult();
		if (result == null) {
			JOptionPane.showMessageDialog(
				comp, 
				"Not set up remote hosts", 
				"Not set up remote hosts", 
				JOptionPane.INFORMATION_MESSAGE);
			
			return null;
		}
		else
			return result.toText();
	}


	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		BalancerConfig cfg = new BalancerConfig();
		cfg.putAll(this);
		
		return cfg;
	}
	
	
	
	
	
	
	
	
}
