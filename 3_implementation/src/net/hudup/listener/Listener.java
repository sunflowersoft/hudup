package net.hudup.listener;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import net.hudup.core.Constants;
import net.hudup.core.client.Gateway;
import net.hudup.core.client.Protocol;
import net.hudup.core.client.Server;
import net.hudup.core.client.ServerStatusEvent;
import net.hudup.core.client.ServerStatusEvent.Status;
import net.hudup.core.client.ServerStatusListener;
import net.hudup.core.client.ServerTrayIcon;
import net.hudup.core.client.Service;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.HelpContent;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.listener.ui.ListenerCP;

/**
 * Because server supports many clients, it is more effective if deploying server on different platforms.
 * It means that we can distribute service layer and interface layer in different sites. Site can be a personal computer, mainframe, etc.
 * There are many scenarios of distribution, for example, many sites for service layer and one site for interface layer.
 * Interface layer has another component - {@code listener} component which is responsible for supporting distributed deployment.
 * Listener which has load balancing function is called {@code balancer}. For example, service layer is deployed on three sites and balancer is deployed on one site; whenever balancer receives user request, it looks up service sites and choose the site whose recommender service is least busy to require such recommender service to perform recommendation task.
 * Load balancing improves system performance and supports a huge of clients. Note that it is possible for the case that balancer or listener is deployed on more than one site.
 * <br>
 * The listener is modeled as this {@link Listener} class.
 * {@link Listener} is also a {@link Server} but it is not recommendation server.
 * {@link Listener} is deployed in interface layer, which supports distributed environment.
 * {@link Listener} stores a list of binded servers. Note, binded server is represented by {@link BindServer} class.
 * It can bind a new server by setting configuration and calling {@link #rebind()} method.
 * In case that there are many binded servers and one {@link Listener} then, {@link Listener} is responsible for dispatching user requests to its proper binded server.
 * {@link Listener} starts, pauses, resumes, and stops by its methods {@link #start()}, {@link #pause()}, {@link #resume()}, and {@link #stop()}, respectively.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Listener extends SocketServer implements ServerStatusListener, Gateway {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	protected BindServerList bindServerList = new BindServerList();

	
	/**
	 * 
	 */
	protected Registry registry = null;


	/**
	 * 
	 * @param config
	 */
	public Listener(ListenerConfig config) {
		super(config);
		// TODO Auto-generated constructor stub
		
		try {
			int port = NetUtil.getPort(config.getExportPort(), Constants.TRY_RANDOM_PORT);
			if (port < 0)
				throw new RuntimeException("Invalid port number");
			config.setExportPort(port);

			registry = LocateRegistry.createRegistry(port);
			UnicastRemoteObject.exportObject(this, port);
			
			Naming.rebind(getGatewayBindUri().toString(), this);
			
			if (!createSysTray())
				showCP();
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	        logger.error("Listener/Balancer fail to be constructed in constructor method, caused by " + e.getMessage());
			System.exit(0);
		}

	}

	
	/**
	 * This method is called inside or by derivative class, for example it is called by {@link #start()} method
	 */
	protected void rebind() {
		
		synchronized (bindServerList) {
			
			
			try {
				bindServerList.prune();
				bindServerList.bind(
						((ListenerConfig)config).getRemoteInfo(), this);
				
		        logger.info("Listener bind remote server successfully");
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("Listener fail to bind remote server, caused by " + e.getMessage());
			}
			
		}
		
	}
	
	
	
	@Override
	public synchronized void start() {
		// TODO Auto-generated method stub
		if (isStarted())
			return;
		
		super.start();
		
		rebind();
		logger.info("Listener/Balancer export at port " + ((ListenerConfig)config).getExportPort());
	}
	
	
	@Override
	public synchronized void stop() {
		// TODO Auto-generated method stub
		if (!isStarted())
			return;
		
		super.stop();
		
		bindServerList.unbindAll();
	}
	
	
	
	
	@Override
	protected synchronized void shutdown() throws RemoteException {
		// TODO Auto-generated method stub
		if (config == null)
			return;

		stop();

    	try {
    		Naming.unbind(getGatewayBindUri().toString());
    		
        	UnicastRemoteObject.unexportObject(this, true);
    		UnicastRemoteObject.unexportObject(registry, true);
    		registry = null;
    	}
    	catch (Throwable e) {
    		e.printStackTrace();
			logger.error("Listener/Balancer fail to shutdown, caused by" + e.getMessage());
    	}

		config.save();
		logger.info("Listener/Balancer shutdown");
		config = null;
		
		fireStatusEvent(new ServerStatusEvent(this, Status.exit));
	}


	@Override
	public synchronized void setConfig(DataConfig config) 
			throws RemoteException {
		if (isStarted())
			return;
		
		
		super.setConfig(config);
		rebind();
	}
	
	
	@Override
	protected AbstractDelegator delegate(Socket socket) {
		// TODO Auto-generated method stub
		synchronized (bindServerList) {
			
			if (bindServerList.size() > 0)
				return new Delegator(
						socket, bindServerList.get().getServer());
			else
				return null;
		}
	}


	@Override
	protected void serverTasks() {
		rebind();
	}
	
	
	
	@Override
	public synchronized void statusChanged(ServerStatusEvent evt) throws RemoteException {
		// TODO Auto-generated method stub
		switch(evt.getStatus()) {
		case started:
			break;
		case paused:
			pauseDelegators();
			break;
		case resumed:
			resumeDelegators();
			break;
		case stopped:
			stopDelegators();
			break;
		case setconfig:
			logger.error("Status setconfig is invalid");
			break;
		case exit:
			bindServerList.prune();
			break;
		}
	}


	/**
	 * 
	 * @return gateway bind {@link xURI}
	 */
	protected xURI getGatewayBindUri() {
		ListenerConfig cfg = (ListenerConfig)config;
		return xURI.create(
				"rmi://localhost:" + cfg.getExportPort() + "/" + Protocol.GATEWAY);
	}


	@Override
	public Server getRemoteServer(String account, String password)
			throws RemoteException {
		// TODO Auto-generated method stub
		if (config == null)
			return null;
		else
			return this;
	}


	@Override
	public Service getRemoteService(String account, String password)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/**
	 * 
	 * @return whether create system tray successfully
	 */
	protected boolean createSysTray() {
		if (!SystemTray.isSupported())
			return false;
		
		try {
            PopupMenu popup = new PopupMenu();

            MenuItem cp = new MenuItem(getMessage("control_panel"));
            cp.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					showCP();
				}
			});
            popup.add(cp);
            
            popup.addSeparator();

            MenuItem helpContent = new MenuItem(getMessage("help_content"));
            helpContent.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					try {
						new HelpContent(null);
					} 
					catch (Throwable ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
				}
			});
            popup.add(helpContent);

            
            MenuItem exit = new MenuItem(getMessage("exit"));
            exit.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					try {
						exit();
					} 
					catch (RemoteException re) {
						// TODO Auto-generated catch block
						re.printStackTrace();
					}
				}
			});
            popup.add(exit);
            
            
            TrayIcon trayIcon = new ServerTrayIcon(
            		this, 
            		UIUtil.getImage("listener-16x16.png"), 
            		UIUtil.getImage("listener-paused-16x16.png"), 
            		UIUtil.getImage("listener-stopped-16x16.png"), 
            		getMessage("hudup_listener"), 
            		popup); 
            trayIcon.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					showCP();
				}
			});
			SystemTray tray = SystemTray.getSystemTray();
			tray.add(trayIcon);
            
			return true;
		}
		catch (Exception e) {
			logger.error("Listener/Balancer fail to create system tray, caused by" + e.getMessage());
		}
		
		return false;
	}
	
	
	/**
	 * 
	 */
	protected void showCP() {
		try {
			new ListenerCP(this);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}


	/**
	 * 
	 * @return {@link Listener}
	 */
	public static Listener create() {
		ListenerConfig config = new ListenerConfig(xURI.create(ListenerConfig.listenerConfig));
		
		return new Listener(config);
	}
	
	
}
