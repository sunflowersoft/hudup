package net.hudup.listener;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.rmi.RemoteException;

import net.hudup.core.client.PowerServer;
import net.hudup.core.client.ServerTrayIcon;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.HelpContent;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.listener.ui.BalancerCP;


/**
 * Because server supports many clients, it is more effective if deploying server on different platforms.
 * It means that we can distribute service layer and interface layer in different sites. Site can be a personal computer, mainframe, etc.
 * There are many scenarios of distribution, for example, many sites for service layer and one site for interface layer.
 * Interface layer has another component - {@code listener} component which is responsible for supporting distributed deployment.
 * Listener which has load balancing function is called {@code balancer}. For example, service layer is deployed on three sites and balancer is deployed on one site; whenever balancer receives user request, it looks up service sites and choose the site whose recommender service is least busy to require such recommender service to perform recommendation task.
 * Load balancing improves system performance and supports a huge of clients. Note that it is possible for the case that balancer or listener is deployed on more than one site.
 * <br>
 * The balancer is modeled as this {@link Balancer} class.
 * {@link Balancer} derives from {@link Listener} and overrides the {@link Listener#rebind()} method in order to support balancing function while it inherits all other functions of {@link Listener}.
 * {@link Balancer} selects least busy binded server to which user requests are dispatched.
 * Therefore, {@link Balancer} improves system performance more than {@link Listener} does. In general, {@link Listener} and {@link Balancer} are main components of interface layer.
 * Note, binded server is represented by {@link BindServer} class.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Balancer extends Listener {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * 
	 * @param config
	 */
	public Balancer(BalancerConfig config) {
		super(config);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	protected void rebind() {
		synchronized (bindServerList) {
			
			try {
				bindServerList.prune();
				bindServerList.bind(
						((BalancerConfig)config).getRemoteInfoList(), this);
				
		        logger.info("Balancer bind list of remote servers successfully");
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("Balancer fail to bind list of remote servers, caused by " + e.getMessage());
			}
			
		}
	}

	
	
	@Override
	protected AbstractDelegator delegate(Socket socket) {
		// TODO Auto-generated method stub
		synchronized (bindServerList) {
			BindServer bindServer = bindServerList.getIdleServer();
			if (bindServer != null) {
				PowerServer server = bindServer.getServer();
				return new Delegator(socket, server);
			}
			else
				return null;
		}
	}


	@Override
	protected void showCP() {
		// TODO Auto-generated method stub
		try {
			new BalancerCP(this);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}


	@Override
	protected boolean createSysTray() {
		// TODO Auto-generated method stub
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
            		UIUtil.getImage("balancer-16x16.png"), 
            		UIUtil.getImage("balancer-paused-16x16.png"), 
            		UIUtil.getImage("balancer-stopped-16x16.png"), 
            		getMessage("hudup_balancer"), 
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
			logger.error("Balancer fail to create system tray fail, caused by" + e.getMessage());
		}
		
		return false;
	}


	/**
	 * 
	 * @return {@link Balancer}
	 */
	public static Balancer create() {
		BalancerConfig config = new BalancerConfig(xURI.create(BalancerConfig.balancerConfig));

		return new Balancer(config);
	}
	
	
	
	
}
