package net.hudup.listener.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
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
import javax.swing.JTabbedPane;

import net.hudup.core.client.ConnectServerDlg;
import net.hudup.core.client.Server;
import net.hudup.core.client.ServerStatusEvent;
import net.hudup.core.client.ServerStatusListener;
import net.hudup.core.client.ServerStatusEvent.Status;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.ui.SysConfigPane;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.UIUtil;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ListenerCP extends JFrame implements ServerStatusListener {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private SysConfigPane paneConfig = null;
	
	private JButton btnExitListener = null;
	
	private JButton btnStart = null;
	
	private JButton btnPauseResume = null;

	private JButton btnStop = null;
	
	private JButton btnApplyConfig = null;
	
	private JButton btnResetConfig = null;

	private JButton btnRefresh = null;

	protected Server listener = null;
	
	private Registry registry = null;

	private xURI bindUri = null;
	
	private boolean bRemote = false;

	
	/**
	 * 
	 * @param listener
	 * @param bindUri
	 * @param bRemote
	 */
	public ListenerCP(Server listener, xURI bindUri, boolean bRemote) {
		super("Listener control panel");
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
	        Image image = UIUtil.getImage("listener-32x32.png");
	        if (image != null)
	        	setIconImage(image);
			
			this.listener = listener;
			this.bindUri = bindUri;
			this.bRemote = bRemote;
			
			Container container = getContentPane();
			JTabbedPane main = new JTabbedPane();
			container.add(main);
			
			main.add(createGeneralPane(), "General");
	
			bindServer();

			updateControls();
			setVisible(true);
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		
		
		if(!this.bRemote) {
			
			Runtime.getRuntime().addShutdownHook(new Thread() {
	
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						close();
					} 
					catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			});
		}
		
	}

	
	/**
	 * 
	 * @param listener
	 */
	public ListenerCP(Server listener) {
		this(listener, null, false);
	}

	
	/**
	 * 
	 */
	private void bindServer() throws RemoteException {
		boolean result = false;
		
		if (!bRemote) {
			bindUri = null;
			registry = null;
			
			result = listener.addStatusListener(this);
		}
		else {
			btnExitListener.setVisible(false);
			
			if (bindUri != null) {
			
				try {
					registry = LocateRegistry.createRegistry(bindUri.getPort());
					UnicastRemoteObject.exportObject(this, bindUri.getPort());
					
					result = listener.addStatusListener(this);
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
	 * @throws RemoteException
	 */
	private JPanel createGeneralPane() throws RemoteException {
		JPanel general = new JPanel(new BorderLayout());
		
		JPanel body = new JPanel(new BorderLayout());
		general.add(body, BorderLayout.CENTER);
		
		body.add(new JLabel("Listener configuration"), BorderLayout.NORTH);
		paneConfig = new SysConfigPane();
		paneConfig.setToolbarVisible(false);
		paneConfig.setControlVisible(false);
		paneConfig.update(listener.getConfig());
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

		
		btnExitListener = new JButton("Exit listener");
		btnExitListener.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				exit();
			}
		});
		leftToolbar.add(btnExitListener);

		
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
	 */
	private void start() {
		try {
			listener.start();
			
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
	private void pauseResume() {
		try {
			if (listener.isPaused())
				listener.resume();
			else if (listener.isRunning())
				listener.pause();
			
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
	private void stop() {
		try {
			listener.stop();
			
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
	private void exit() {
		if (bRemote)
			return;

		try {
			listener.exit();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * 
	 */
	private void applyConfig() {
		try {
			if (listener.isRunning()) {
				JOptionPane.showMessageDialog(
					this, 
					"Listener running. Can't save configuration", 
					"Listener running", 
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
				listener.setConfig(
						(DataConfig)paneConfig.getPropTable().getPropList());

				JOptionPane.showMessageDialog(
						this, 
						"Apply configuration to listener successfully", 
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
	private void resetConfig() {
		try {
			if (listener.isRunning()) {
				JOptionPane.showMessageDialog(
					this, 
					"Listener running. Can't reset configuration", 
					"Listener running", 
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
	 * @param enabled
	 */
	private void enableControls(boolean enabled) {
		btnExitListener.setEnabled(enabled);
		btnStart.setEnabled(enabled);
		btnPauseResume.setEnabled(enabled);
		btnStop.setEnabled(enabled);
		btnApplyConfig.setEnabled(enabled);
		btnResetConfig.setEnabled(enabled);
		btnRefresh.setEnabled(enabled);
		paneConfig.setEnabled(enabled);
	}
	
	
	/**
	 * 
	 * @param state
	 */
	private void updateControls(ServerStatusEvent.Status state) 
			throws RemoteException {
		
		if (state == Status.started || state == Status.resumed) {
			enableControls(false);

			btnExitListener.setEnabled(true);
			btnStart.setEnabled(false);
			btnPauseResume.setEnabled(true && !bRemote);
			btnPauseResume.setText("Pause");
			btnStop.setEnabled(true);
			
			btnApplyConfig.setEnabled(false);
			btnResetConfig.setEnabled(false);
			paneConfig.setEnabled(false);
			
			try {
				paneConfig.update(listener.getConfig());
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		else if (state == Status.paused) {
			enableControls(false);

			btnExitListener.setEnabled(true);
			btnStart.setEnabled(false);
			btnPauseResume.setEnabled(true && !bRemote);
			btnPauseResume.setText("Resume");
			btnStop.setEnabled(true);
			
			btnApplyConfig.setEnabled(false);
			btnResetConfig.setEnabled(false);
			paneConfig.setEnabled(false);
			
		}
		else if (state == Status.stopped) {
			enableControls(false);

			btnExitListener.setEnabled(true);
			btnStart.setEnabled(true);
			btnPauseResume.setEnabled(false);
			btnPauseResume.setText("Pause");
			btnStop.setEnabled(false);
			
			btnApplyConfig.setEnabled(true);
			btnResetConfig.setEnabled(true);
			paneConfig.setEnabled(true);
			
		}
		else if (state == Status.setconfig) {
			paneConfig.update(listener.getConfig());
		}
		else if (state == Status.exit) {
			if (bRemote) {
				listener = null;
				dispose();
			}
		}
		
		
	}
	
	
	/**
	 * 
	 */
	private void updateControls() 
			throws RemoteException {
		if (listener == null)
			return;
		
		if (listener.isRunning())
			updateControls(Status.started);
		else if (listener.isPaused())
			updateControls(Status.paused);
		else
			updateControls(Status.stopped);
			
	}
	
	
	
	@Override
	public void statusChanged(ServerStatusEvent evt) 
			throws RemoteException {
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
			if (listener != null)
				listener.removeStatusListener(this);
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
		
		listener = null;
		bindUri = null;
		registry = null;
		
	}

	
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ConnectServerDlg dlg = new ConnectServerDlg();
		Image image = UIUtil.getImage("listener-32x32.png");
        if (image != null)
        	dlg.setIconImage(image);
		
		Server server = dlg.getServer();
		if (server != null)
			new ListenerCP(server, dlg.getBindUri(), true);
	}


	
	
	
}
