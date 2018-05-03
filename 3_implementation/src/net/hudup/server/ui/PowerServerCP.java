/**
 * 
 */
package net.hudup.server.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.hudup.core.client.ConnectServerDlg;
import net.hudup.core.client.PowerServer;
import net.hudup.core.client.RemoteServerCP;
import net.hudup.core.client.Server;
import net.hudup.core.client.ServerStatusEvent;
import net.hudup.core.client.ServerStatusEvent.Status;
import net.hudup.core.client.ServerStatusListener;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.data.Provider;
import net.hudup.core.data.Unit;
import net.hudup.core.data.ui.SysConfigPane;
import net.hudup.core.data.ui.UnitListBox;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.data.DatasetUtil2;
import net.hudup.data.ProviderImpl;
import net.hudup.data.ui.UnitTable;
import net.hudup.data.ui.UnitTableFactory;
import net.hudup.data.ui.UnitTableImpl;
import net.hudup.logistic.SystemPropertiesPane;
import net.hudup.logistic.math.HudupCipher;
import net.hudup.server.PowerServerConfig;
import quick.dbtable.DBTableEventListener;
import quick.dbtable.DatabaseChangeListener;

/**
 * The difference from {@link RemoteServerCP} is to have full of features of server
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class PowerServerCP extends JFrame implements ServerStatusListener {


	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	//protected JButton btnExternalConfig = null;

	protected SysConfigPane paneConfig = null;
	
	protected JButton btnSetupServer = null;
	
	protected JButton btnExitServer = null;
	
	protected JButton btnStart = null;
	
	protected JButton btnPauseResume = null;

	protected JButton btnStop = null;
	
	protected JButton btnApplyConfig = null;
	
	protected JButton btnResetConfig = null;

	protected JButton btnRefresh = null;

	protected JButton btnLoadStore = null;

	protected UnitListBox unitList = null;
	
	protected UnitTable unitTable = null;
	
	protected UnitTable accUnitTable = null;

	protected JButton btnUpdateAcc = null;

	protected JButton btnDeleteAcc = null;
	
	protected JTextField txtAccName = null;
	
	protected JPasswordField txtAccPass = null;
	
	protected JTextField txtAccPrivs = null;
	
	protected PowerServer server = null;
	
	protected Provider provider = null;
	
	protected Registry registry = null;

	protected xURI bindUri = null;
	
	protected boolean bRemote = false;
	
	
	/**
	 * 
	 * @param server
	 * @param bindUri
	 * @param bRemote
	 */
	public PowerServerCP(PowerServer server, xURI bindUri, boolean bRemote) {
		super("Server control panel");
		try {
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			setSize(600, 400);
			setLocationRelativeTo(null);
			addWindowListener(new WindowAdapter() {
	
				@Override
				public void windowClosed(WindowEvent e) {
					// TODO Auto-generated method stub
					super.windowClosed(e);
					close();
				}
				
			});
			
	        Image image = UIUtil.getImage("server-32x32.png");
	        if (image != null)
	        	setIconImage(image);

			this.server = server;
			this.bindUri = bindUri;
			this.bRemote = bRemote;
			
			Container container = getContentPane();
			JTabbedPane main = new JTabbedPane();
			container.add(main);
			
			main.add(createGeneralPane(), "General");
			main.add(createStorePane(), "Store");
			main.add(createAccountPane(), "Account");
			main.add(new SystemPropertiesPane(), "System");

			bindServer();
			
			updateControls();
			setVisible(true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	
	/**
	 * 
	 * @param server
	 */
	public PowerServerCP(PowerServer server) {
		this(server, null, false);
	}
	
	
	/**
	 * 
	 */
	protected void bindServer() throws RemoteException {
		boolean result = false;
		
		if (!bRemote) {
			bindUri = null;
			registry = null;
			
			result = server.addStatusListener(this);
		}
		else {
			btnExitServer.setVisible(false);
			
			if (bindUri != null) {
			
				try {
					registry = LocateRegistry.createRegistry(bindUri.getPort());
					UnicastRemoteObject.exportObject(this, bindUri.getPort());
					
					result = server.addStatusListener(this);
					if (!result)
						throw new Exception();
				}
				catch (Throwable e) {
					e.printStackTrace();
					
					try {
			        	UnicastRemoteObject.unexportObject(this, true);
					}
					catch (Throwable e1) {
						e1.printStackTrace();
					}
					
					try {
			    		UnicastRemoteObject.unexportObject(registry, true);
					}
					catch (Throwable e1) {
						e1.printStackTrace();
					}
					
					registry = null;
					bindUri = null;
					result = false;
				}
			
			} // if (bindUri_ != null)
		}
		
		
		if (result)
			btnRefresh.setVisible(false);
	}

	
	/**
	 * 
	 * @return {@link JPanel}
	 * @throws Exception
	 */
	protected JPanel createGeneralPane() throws Exception {
		JPanel general = new JPanel(new BorderLayout());
		
		JPanel body = new JPanel(new BorderLayout());
		general.add(body, BorderLayout.CENTER);
		
		body.add(new JLabel("Server configuration"), BorderLayout.NORTH);
		paneConfig = new SysConfigPane();
		paneConfig.setControlVisible(false);
		paneConfig.update(server.getConfig());
		body.add(paneConfig, BorderLayout.CENTER);
		
		
		JPanel footer = new JPanel();
		footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
		general.add(footer, BorderLayout.SOUTH);
		
		JPanel configbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		footer.add(configbar);
		
		btnApplyConfig = new JButton("Apply config");
		btnApplyConfig.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				applyConfig();
			}
		});
		configbar.add(btnApplyConfig);

		btnResetConfig = new JButton("Reset config");
		btnResetConfig.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				resetConfig();
			}
		});
		configbar.add(btnResetConfig);

		
		btnLoadStore = new JButton("Load store");
		btnLoadStore.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				loadStore();
			}
		});
		configbar.add(btnLoadStore);

		
		JPanel mainToolbar = new JPanel(new BorderLayout());
		footer.add(mainToolbar);
		
		JPanel leftToolbar = new JPanel(new FlowLayout());
		mainToolbar.add(leftToolbar, BorderLayout.WEST);
		
		btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					updateControls();
				} 
				catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		leftToolbar.add(btnRefresh);

		btnSetupServer = new JButton("Setup server");
		btnSetupServer.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				setupServer();
			}
		});
		leftToolbar.add(btnSetupServer);

		btnExitServer = new JButton("Exit server");
		btnExitServer.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				exit();
			}
		});
		leftToolbar.add(btnExitServer);

		
		JPanel centerToolbar = new JPanel(new FlowLayout());
		mainToolbar.add(centerToolbar, BorderLayout.CENTER);
		
		btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				start();
			}
		});
		centerToolbar.add(btnStart);

		btnPauseResume = new JButton("Pause");
		btnPauseResume.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				pauseResume();
			}
		});
		centerToolbar.add(btnPauseResume);
		
		btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				stop();
			}
		});
		centerToolbar.add(btnStop);
		
		return general;
	}
	
	
	/**
	 * 
	 * @return {@link JPanel}
	 * @throws Exception
	 */
	private JPanel createStorePane() throws Exception {
		JPanel store = new JPanel(new BorderLayout());
		
		unitTable = UnitTableFactory.create();
		store.add(unitTable.getComponent(), BorderLayout.CENTER);

		
		unitList = new UnitListBox();
		store.add(new JScrollPane(unitList), BorderLayout.WEST);
		unitList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
			
				Unit unit = unitList.getSelectedValue();
				if (unit == null) {
					unitTable.clear();
					return;
				}
				
				
				unitTable.update(provider, unit.getName());
				
			}
		});
		
		
		return store;
	}
	
	
	/**
	 * 
	 * @return {@link JPanel}
	 */
	private JPanel createAccountPane() {
		final JPanel main = new JPanel(new BorderLayout());
		
		accUnitTable = UnitTableFactory.create();
		if (!(accUnitTable instanceof UnitTableImpl)) {
			JOptionPane.showMessageDialog(
					this, 
					"Not support non-database unit table", 
					"Not support non-database unit table", JOptionPane.INFORMATION_MESSAGE);
			
			return main;
		}
		
		JPanel body = new JPanel(new BorderLayout());
		main.add(body, BorderLayout.CENTER);
		
		body.add(accUnitTable.getComponent(), BorderLayout.CENTER);

		JPanel records = new JPanel(new BorderLayout());
		body.add(records, BorderLayout.SOUTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		records.add(left, BorderLayout.WEST);
		
		left.add(new JLabel("Account name:"));
		left.add(new JLabel("Account password:"));
		left.add(new JLabel("Account privileges:"));
		
		JPanel right = new JPanel(new GridLayout(0, 1));
		records.add(right, BorderLayout.CENTER);
		
		txtAccName = new JTextField();
		right.add(txtAccName);
		
		txtAccPass = new JPasswordField();
		right.add(txtAccPass);
		
		txtAccPrivs = new JTextField();
		right.add(txtAccPrivs);
		
		JPanel footer = new JPanel();
		main.add(footer, BorderLayout.SOUTH);
		
		btnUpdateAcc = new JButton("Add / Update");
		btnUpdateAcc.addActionListener(new ActionListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					Profile accProfile = new Profile(
							provider.getProfileAttributes(
							server.getConfig().getAccountUnit()));
					
					String acc = txtAccName.getText().trim();
					String password = txtAccPass.getText();
					String priv = txtAccPrivs.getText().trim();
					if (acc.isEmpty() || priv.isEmpty()) {
						JOptionPane.showMessageDialog(
								main, "Empty field", "Empty field", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					accProfile.setValue(
							DataConfig.ACCOUNT_NAME_FIELD, acc);
					accProfile.setValue(DataConfig.ACCOUNT_PASSWORD_FIELD, 
							new HudupCipher().md5Encrypt(password));
					accProfile.setValue(
							DataConfig.ACCOUNT_PRIVILEGES_FIELD, priv);
					
					provider.updateAccount(accProfile);
					accUnitTable.refresh();
					updateAccData();
				}
				catch (Throwable ex) {
					ex.printStackTrace();
				}
			}
		});
		footer.add(btnUpdateAcc);
		
		btnDeleteAcc = new JButton("Delete");
		btnDeleteAcc.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					String accName = txtAccName.getText().trim();
					if (accName.isEmpty() || accName.isEmpty()) {
						JOptionPane.showMessageDialog(
								main, 
								"Empty field", 
								"Empty field", 
								JOptionPane.INFORMATION_MESSAGE);
						
						return;
					}
					
					provider.deleteAccount(accName);
					accUnitTable.refresh();
					updateAccData();
				}
				catch (Throwable ex) {
					ex.printStackTrace();
				}
			}
		});
		footer.add(btnDeleteAcc);
		
		getAccUnitTable().addDBTableEventListener(new DBTableEventListener() {

			@Override
			public void afterRowSelectionChange(int fromRow, int toRow) {
				// TODO Auto-generated method stub
				super.afterRowSelectionChange(fromRow, toRow);
				
				updateAccData();
			}
			
		});
		
		getAccUnitTable().addDatabaseChangeListener(new DatabaseChangeListener() {

			@Override
			public void afterDelete(int row) {
				// TODO Auto-generated method stub
				super.afterDelete(row);
				updateAccData();
			}

			@Override
			public void afterInsert(int row) {
				// TODO Auto-generated method stub
				super.afterInsert(row);
				updateAccData();
			}

			@Override
			public void afterUpdate(int row) {
				// TODO Auto-generated method stub
				super.afterUpdate(row);
				updateAccData();
			}
			
		});
		
		return main;
	}
	
	
	/**
	 * 
	 */
	private void updateAccData() {
		if (getAccUnitTable().getSelectedRow() == -1 && 
				getAccUnitTable().getRowCount() > 0) {
			getAccUnitTable().selectFirstVisibleColumnCell(0, true);
		}
		txtAccName.setText("");
		txtAccPass.setText("");
		txtAccPrivs.setText("");
		
		int selected = getAccUnitTable().getSelectedRow();
		if (selected == -1)
			return;
		
		String accName = getAccUnitTable().getValueAt(selected, 0).toString();
		txtAccName.setText(accName);
		
		txtAccPass.setText("");
		
		String accPrivs = getAccUnitTable().getValueAt(selected, 2).toString();
		txtAccPrivs.setText(accPrivs);
	}
	
	
	/**
	 * 
	 * @return {@link UnitTableImpl}
	 */
	private UnitTableImpl getAccUnitTable() {
		return (UnitTableImpl) accUnitTable;
	}
	
	
	/**
	 * 
	 */
	protected void start() {
		try {
			server.start();
			
			if (btnRefresh.isVisible())
				updateControls();
			
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 */
	protected void pauseResume() {
		try {
			if (server.isPaused())
				server.resume();
			else if (server.isRunning())
				server.pause();
			
			if (btnRefresh.isVisible())
				updateControls();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 */
	protected void stop() {
		try {
			server.stop();
			
			if (btnRefresh.isVisible())
				updateControls();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 
	 */
	protected void setupServer() {
		try {
			if (server.isRunning()) {
				JOptionPane.showMessageDialog(
					this, 
					"Server running. Can't set up", 
					"Server running", 
					JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			SetupServerWizard dlg = new SetupServerWizard(this,
					(PowerServerConfig)server.getConfig());
			if (bRemote)
				server.setConfig(dlg.getServerConfig());
			
			
		} 
		catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	/**
	 * 
	 */
	protected void exit() {
		if (bRemote)
			return;
		
		try {
			server.exit();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 */
	protected void applyConfig() {
		try {
			if (server.isRunning()) {
				JOptionPane.showMessageDialog(
					this, 
					"Server running. Can't save configuration", 
					"Server running", 
					JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			boolean apply = paneConfig.apply();
			if (!apply) {
				JOptionPane.showMessageDialog(
						this, 
						"Cannot apply configuration", 
						"Cannot apply configuration", 
						JOptionPane.ERROR_MESSAGE);
			}
			else {
				server.setConfig(
						(DataConfig)paneConfig.getPropTable().getPropList());
		
				JOptionPane.showMessageDialog(
						this, 
						"Apply configuration to server successfully", 
						"Apply configuration successfully", 
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 */
	protected void resetConfig() {
		try {
			if (server.isRunning()) {
				JOptionPane.showMessageDialog(
					this, 
					"Server running. Can't reset configuration", 
					"Server running", 
					JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			paneConfig.reset();
			int confirm = JOptionPane.showConfirmDialog(
					this, 
					"Reset configuration successfully. \n" + 
					"Do you want to apply configuration into being effective?", 
					"Reset configuration successfully", 
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			
			if (confirm == JOptionPane.YES_OPTION)
				applyConfig();
			else {
				JOptionPane.showMessageDialog(
						this, 
						"Please press button 'Apply Config' to make store configuration effect later", 
						"Please press button 'Apply Config' to make store configuration effect later", 
						JOptionPane.INFORMATION_MESSAGE);
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * 
	 */
	protected void loadStore() {
		try {
			if (server.isRunning()) {
				JOptionPane.showMessageDialog(
					this, 
					"Server running. Can't load store", 
					"Server running", 
					JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			DataConfig config = DatasetUtil2.chooseServerConfig(this, server.getConfig());
			
			if (config == null) {
				JOptionPane.showMessageDialog(
						this, 
						"Not load store", 
						"Not load store", 
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			DataConfig cfg = new DataConfig();
			cfg.putAll(server.getConfig());
			cfg.putAll(config);
			
			paneConfig.getPropTable().updateNotSetup(cfg);
			JOptionPane.showMessageDialog(
					this, 
					"Load store configuration successfully. \n" + 
					"Please press button 'Apply Config' to make store configuration effect", 
					"Please press button 'Apply Config'", 
					JOptionPane.INFORMATION_MESSAGE);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 
	 * @param enabled
	 */
	private void enableControls(boolean enabled) {
		btnSetupServer.setEnabled(enabled);
		btnExitServer.setEnabled(enabled);
		btnStart.setEnabled(enabled);
		btnPauseResume.setEnabled(enabled);
		btnStop.setEnabled(enabled);
		btnApplyConfig.setEnabled(enabled);
		btnResetConfig.setEnabled(enabled);
		btnRefresh.setEnabled(enabled);
		btnLoadStore.setEnabled(enabled);
		paneConfig.setEnabled(enabled);
		
		btnUpdateAcc.setEnabled(enabled);
		btnDeleteAcc.setEnabled(enabled);
		txtAccName.setEnabled(enabled);
		txtAccPass.setEnabled(enabled);
		txtAccPrivs.setEnabled(enabled);
	}
	
	
	/**
	 * 
	 * @param state
	 */
	protected void updateControls(ServerStatusEvent.Status state)
			throws RemoteException {
		
		if (state == Status.started || state == Status.resumed) {
			enableControls(false);
			DataConfig config = server.getConfig();

			if (provider != null)
				provider.close();
			provider = new ProviderImpl(config);

			btnSetupServer.setEnabled(false);
			btnExitServer.setEnabled(true);
			btnStart.setEnabled(false);
			btnPauseResume.setEnabled(true && !bRemote);
			btnPauseResume.setText("Pause");
			btnStop.setEnabled(true);
			
			btnApplyConfig.setEnabled(false);
			btnResetConfig.setEnabled(false);
			btnLoadStore.setEnabled(false);
			paneConfig.setEnabled(false);
			
			btnUpdateAcc.setEnabled(true);
			btnDeleteAcc.setEnabled(true);
			txtAccName.setEnabled(true);
			txtAccPass.setEnabled(true);
			txtAccPrivs.setEnabled(true);
			
			unitList.connectUpdate(config);
			unitList.validate();
			unitList.updateUI();
			
			accUnitTable.update(provider, config.getAccountUnit());
			accUnitTable.first();
			updateAccData();
			
			try {
				paneConfig.update(config);
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
			
		}
		else if (state == Status.paused) {
			enableControls(false);

			btnSetupServer.setEnabled(false);
			btnExitServer.setEnabled(true);
			btnStart.setEnabled(false);
			btnPauseResume.setEnabled(true && !bRemote);
			btnPauseResume.setText("Resume");
			btnStop.setEnabled(true);
			
			btnApplyConfig.setEnabled(false);
			btnResetConfig.setEnabled(false);
			btnLoadStore.setEnabled(false);
			paneConfig.setEnabled(false);
			
			btnUpdateAcc.setEnabled(true);
			btnDeleteAcc.setEnabled(true);
			txtAccName.setEnabled(true);
			txtAccPass.setEnabled(true);
			txtAccPrivs.setEnabled(true);
		}
		else if (state == Status.stopped) {
			enableControls(false);

			btnSetupServer.setEnabled(true);
			btnExitServer.setEnabled(true);
			btnStart.setEnabled(true);
			btnPauseResume.setEnabled(false);
			btnPauseResume.setText("Pause");
			btnStop.setEnabled(false);
			
			btnApplyConfig.setEnabled(true);
			btnResetConfig.setEnabled(true);
			btnLoadStore.setEnabled(true);
			paneConfig.setEnabled(true);
			
			btnUpdateAcc.setEnabled(false);
			btnDeleteAcc.setEnabled(false);
			txtAccName.setEnabled(false);
			txtAccPass.setEnabled(false);
			txtAccPrivs.setEnabled(false);

			try {
				unitTable.clear();
				accUnitTable.clear();
				updateAccData();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			unitList.setListData(new Unit[0]);
			unitList.validate();
			unitList.updateUI();
			
			if (provider != null)
				provider.close();
			provider = null;

		}
		else if (state == Status.setconfig) {
			paneConfig.update(server.getConfig());
		}
		else if (state == Status.exit) {
			if (bRemote) {
				server = null;
				dispose();
			}
		}
	}
	
	
	/**
	 * 
	 */
	protected void updateControls() throws RemoteException {
		if (server == null)
			return;

		if (server.isRunning()) {
			updateControls(Status.started);
			btnRefresh.setEnabled(true);
		}
		else if (server.isPaused()) {
			updateControls(Status.paused);
			btnRefresh.setEnabled(true);
		}
		else {
			updateControls(Status.stopped);
			btnRefresh.setEnabled(true);
		}
			
	}
	
	
	
	@Override
	public void statusChanged(ServerStatusEvent evt) throws RemoteException {
		// TODO Auto-generated method stub
		if (bRemote)
			updateControls(evt.getStatus());
		else if (!evt.getShutdownHookStatus())
			updateControls(evt.getStatus());
	}
	
	
	
	/**
	 * 
	 */
	private void close() {
		
		try {
			if (server != null)
				server.removeStatusListener(this);
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (bRemote) {
			try {
				if (bindUri != null)
					UnicastRemoteObject.unexportObject(this, true);
			}
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				if (registry != null)
					UnicastRemoteObject.unexportObject(registry, true);
			}
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (provider != null)
			provider.close();
		
		server = null;
		bindUri = null;
		registry = null;
		provider = null;
	}
	
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ConnectServerDlg dlg = new ConnectServerDlg();
		
		Server server = dlg.getServer();
		if (server != null)
			new PowerServerCP((PowerServer)server, dlg.getBindUri(), true);
	}
	
	
	
}
