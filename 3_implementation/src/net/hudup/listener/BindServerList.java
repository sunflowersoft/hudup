package net.hudup.listener;

import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.client.ActiveMeasure;
import net.hudup.core.client.PowerServer;
import net.hudup.core.client.ServerStatusListener;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class BindServerList {
	
	
	/**
	 * 
	 */
	protected List<BindServer> bindServers = Util.newList();

	
	/**
	 * 
	 */
	public BindServerList() {
		
	}
	
	
	/**
	 * 
	 * @param index
	 * @return {@link BindServer}
	 */
	public synchronized BindServer get(int index) {
		
		try {
			return bindServers.get(index);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/**
	 * 
	 * @return {@link BindServer}
	 */
	public synchronized BindServer get() {
		if (size() == 0)
			return null;
		else
			return get(0);
	}

	
	
	/**
	 * Getting idle remote server
	 * 
	 * @return {@link BindServer}
	 */
	public synchronized BindServer getIdleServer() {
		BindServer idle = null;
		for (BindServer bindServer : bindServers) {
			try {
				if (idle == null)
					idle = bindServer;
				else {
					ActiveMeasure m = bindServer.getServer().getActiveMeasure();
					ActiveMeasure mIdle = idle.getServer().getActiveMeasure();
					if (m.compareTo(mIdle) < 0)
						idle = bindServer;
				}
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return idle;
		
	}

	
	/**
	 * 
	 * @return size of this list
	 */
	public synchronized int size() {
		return bindServers.size();
	}
	
	
	/**
	 * 
	 * @param index
	 */
	public synchronized void unbind(int index) {
		try {
			BindServer bindServer = bindServers.get(index);
			bindServer.unbind();
			bindServers.remove(index);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 */
	public synchronized void unbindAll() {
		for (BindServer bindServer : bindServers) {
			try {
				bindServer.unbind();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		bindServers.clear();
	}
	
	
	
	/**
	 * 
	 * @param host
	 * @param port
	 * @return index of bind server
	 */
	public synchronized int indexOf(String host, int port) {
		for (int i = 0; i < bindServers.size(); i++) {
			BindServer bindServer = bindServers.get(i);
			RemoteInfo rInfo = bindServer.getRemoteInfo();
			if (rInfo.host.compareToIgnoreCase(host) == 0 && rInfo.port == port)
				return i;
		}
		
		return -1;
	}
	
	
	
	/**
	 * 
	 * @param host
	 * @param port
	 * @param account
	 * @param password
	 * @param bind
	 * @return whether bind successfully
	 */
	public synchronized boolean bind(
			String host, 
			int port, 
			String account, 
			String password, 
			ServerStatusListener bind) {
		
		int index = indexOf(host, port);
		
		if (index != -1) {
			BindServer bindServer = get(index);
			PowerServer server = bindServer.getServer();
			
			boolean validate = false;
			try {
				validate = server.ping();
			}
			catch (Throwable e) {
				e.printStackTrace();
				validate = false;
			}
			
			if (!validate) {
				this.bindServers.remove(bindServer);
				bindServer = BindServer.bind(host, port, account, password, bind);
				
				if (bindServer == null)
					return false;
				else {
					bindServers.add(bindServer);
					return true;
				}
			}
			else
				return true;
		}
		else {
			
			BindServer bindServer = BindServer.bind(host, port, account, password, bind);
			if (bindServer == null)
				return false;
			else {
				bindServers.add(bindServer);
				return true;
			}
		}
		
	}
	
	
	/**
	 * 
	 * @param host
	 * @param port
	 * @param account
	 * @param password
	 * @param bind
	 * @return whether re-bind successfully
	 */
	public synchronized boolean rebind(
			String host, 
			int port, 
			String account, 
			String password, 
			ServerStatusListener bind) {
		
		unbindAll();
		return bind(host, port, account, password, bind);
	}
	
	
	/**
	 * 
	 * @param rInfo
	 * @param bind
	 * @return whether bind successfully
	 */
	public synchronized boolean bind(RemoteInfo rInfo, ServerStatusListener bind) {
		return bind(rInfo.host, rInfo.port, rInfo.account, rInfo.password.getText(), bind);
	}
	
	
	/**
	 * 
	 * @param rInfo
	 * @param bind
	 * @return whether re-bind successfully
	 */
	public synchronized boolean rebind(RemoteInfo rInfo, ServerStatusListener bind) {
		unbindAll();
		return bind(rInfo, bind);
	}

		
	/**
	 * 
	 * @param rInfoList
	 * @param bind
	 */
	public synchronized void bind(RemoteInfoList rInfoList, ServerStatusListener bind) {
		int n = rInfoList.size();
		for (int i = 0; i < n; i++) {
			RemoteInfo rInfo = rInfoList.get(i);
			bind(rInfo, bind);
		} // end for
	}


	/**
	 * 
	 * @param rInfoList
	 * @param bind
	 */
	public synchronized void rebind(RemoteInfoList rInfoList, ServerStatusListener bind) {
		unbindAll();
		bind(rInfoList, bind);
	}
	
	
	/**
	 * Unbinding servers that are not running (stopped or paused)
	 * 
	 * 
	 */
	public synchronized void prune() {
		List<BindServer> temp = Util.newList();
		temp.addAll(bindServers);
		
		for (BindServer bindServer : temp) {
			boolean alive = true;
			
			try {
				alive = bindServer.getServer().ping();
			}
			catch (Throwable e) {
				e.printStackTrace();
				alive = false;
			}
			
			if (!alive)
				this.bindServers.remove(bindServer);
		}
	}
	
	
	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		try {
			unbindAll();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		super.finalize();
	}
	
	
}
