package net.hudup.server;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import net.hudup.core.client.ServerTrayIcon;
import net.hudup.core.client.Service;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.DataDriver.DataType;
import net.hudup.core.data.Provider;
import net.hudup.core.data.UnitList;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.HelpContent;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.data.ProviderImpl;
import net.hudup.server.ui.PowerServerCP;
import net.hudup.server.ui.SetupServerWizard;


/**
 * {@link DefaultServer} class is default and complete implementation of {@link net.hudup.core.client.Server} interface.
 * Actually, default server inherits {@link PowerServerImpl} class.
 * Default server creates and manages {@link DefaultService}. Developers are suggested to build up their own servers.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class DefaultServer extends PowerServerImpl {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */
	protected DefaultService service = null;
	
	
	/**
	 * 
	 * @param config
	 */
	protected DefaultServer(PowerServerConfig config) {
		super(config);
		// TODO Auto-generated constructor stub
		
		service = createService();
		
		if (!createSysTray())
			showCP();
	}


	@Override
	protected Transaction createTransaction() {
		// TODO Auto-generated method stub
		return new TransactionImpl();
	}

	
	/**
	 * 
	 * @return {@link Service}
	 */
	protected DefaultService createService() {
		return new DefaultService(trans);
	}
	
	
	@Override
	protected void doWhenStart() {
		service.open(config);
	}
	
	
	@Override
	protected void doWhenStop() {
		service.close();
	}
	
	
	@Override
	protected void serverTasks() {
		// TODO Auto-generated method stub
		
		// Task 1
		DefaultService newService = createService();
		newService.transfer(service);
	}
	
	
	@Override
	protected void initStorageSystem() {
		// TODO Auto-generated method stub
		
		try {
			DataDriver driver = DataDriver.create(config.getStoreUri());
			if (driver != null && driver.isDbServer())
				Class.forName(driver.getInnerClassName());
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Server fail to initialize storage system, error is " + e.getMessage());
		}
	}


	@Override
	protected void destroyStorageSystem() {
		// TODO Auto-generated method stub
		
		DataDriver driver = DataDriver.create(config.getStoreUri());
		
		if (driver != null && driver.getType() == DataType.derby_engine) {
			try {
				DriverManager.getConnection("jdbc:derby:;shutdown=true");
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				if (e instanceof SQLException) {
					if (! ((SQLException)e).getSQLState().equals("XJ015") ) {
						e.printStackTrace();
						logger.error("Server fail to destroy storage system (shutdown Derby engine), error is " + e.getMessage());
					}
				}
				else {
					e.printStackTrace();
					logger.error("Server fail to destroy storage system (shutdown Derby engine), error is " + e.getMessage());
				}
			}
		}
		
		
	}


	@Override
	public synchronized boolean validateAccount(String account, String password, int privileges) 
			throws RemoteException {
		
		if (service.isOpened()) {
			Provider provider = service.getProvider();
			if (provider == null)
				return true;
			else
				return provider.validateAccount(account, password, privileges);
		}
		
		ProviderImpl provider = new ProviderImpl(config);
		boolean validate = provider.validateAccount(account, password, privileges);
		provider.close();
		return validate;
	}

	
	@Override
	public Service getService() throws RemoteException {
		// TODO Auto-generated method stub
		return service;
	}


	/**
	 * 
	 * @param key
	 * @return message according to key
	 */
	protected String getMessage(String key) {
		return I18nUtil.getMessage(config, key);
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
					catch (Throwable ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
				}
			});
            popup.add(exit);
            
            
            TrayIcon trayIcon = new ServerTrayIcon(
            		this, 
            		UIUtil.getImage("server-16x16.png"), 
            		UIUtil.getImage("server-paused-16x16.png"), 
            		UIUtil.getImage("server-stopped-16x16.png"), 
            		getMessage("hudup_recommendation_server"), 
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
		catch (Throwable e) {
			e.printStackTrace();
			logger.error("Server fail to create system tray fail, caused by" + e.getMessage());
		}
		
		return false;
	}
	
	
	/**
	 * 
	 */
	protected void showCP() {
		try {
			new PowerServerCP(this);
		}
		catch (Throwable e) {
			e.printStackTrace();
			logger.error("Server fail to show control panel, caused by " + e.getMessage());
		}
	}
	
	
	/**
	 * 
	 * @return {@link DefaultServer}
	 */
	public static DefaultServer create() {
		return create(xURI.create(PowerServerConfig.serverConfig));
	}
	
	
	/**
	 * 
	 * @param srvConfigUri
	 * @return {@link DefaultServer}
	 */
	public static DefaultServer create(xURI srvConfigUri) {
		boolean require = requireSetup(srvConfigUri);
		
		if (!require)
			return new DefaultServer(new PowerServerConfig(srvConfigUri));
		else {
			
	        Image image = UIUtil.getImage("server-32x32.png");
			int confirm = JOptionPane.showConfirmDialog(
					null, 
					"Server not set up yet\n Do you want to setup server?", 
					"Setup server", 
					JOptionPane.OK_CANCEL_OPTION, 
					JOptionPane.INFORMATION_MESSAGE, 
					image == null ? null : new ImageIcon(image));
			
			if (confirm != JOptionPane.OK_OPTION) {
				logger.info("Server not created");
				return null;
			}
			
			
			PowerServerConfig config = new PowerServerConfig(srvConfigUri);
			SetupServerWizard dlg = new SetupServerWizard(null, config);
			
			if (!dlg.isFinished()) {
				logger.info("Server not created");
				return null;
			}
			
			require = requireSetup(srvConfigUri);
			if (require) {
				logger.info("Server not created");
				return null;
			}
			
			return new DefaultServer(new PowerServerConfig(srvConfigUri));
		}
		
	}
	
	
	/**
	 * @param srvConfigUri
	 * @return whether require set up
	 */
	protected static boolean requireSetup(xURI srvConfigUri) {
		
		try {
			UriAdapter adapter = new UriAdapter(srvConfigUri);
			
			if (!adapter.exists(srvConfigUri)) {
				adapter.close();
				return true;
			}
			adapter.close();
			
			PowerServerConfig config = new PowerServerConfig(srvConfigUri);
			
			if (config.size() == 0)
				return true;
			else {
				Provider provider = new ProviderImpl(config);
				
				UnitList unitList = provider.getUnitList();
				UnitList defaultUnitList = DataConfig.getDefaultUnitList();
				defaultUnitList.remove(DataConfig.CONTEXT_TEMPLATE_UNIT);
				defaultUnitList.remove(DataConfig.CONTEXT_UNIT);
				
				if (!unitList.contains(defaultUnitList)) {
					provider.close();
					return true;
				}
				provider.close();
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
			logger.error("Server require set up cause error " + e.getMessage());
			return true;
		}
		
		return false;
		
	}

	




}
