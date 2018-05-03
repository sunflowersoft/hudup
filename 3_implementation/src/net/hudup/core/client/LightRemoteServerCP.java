package net.hudup.core.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.hudup.core.Constants;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.ui.PropPane;
import net.hudup.core.logistic.ui.UIUtil;


/**
 * This graphic user interface and a window application {@link JFrame} allows users to start, stop, pause, resume, configure {@link Server} remotely.
 * This class is also called lightweight control panel for remote server.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class LightRemoteServerCP extends JFrame {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Pane of server configuration as {@link PropPane}. 
	 */
	private PropPane paneConfig = null;
	
	/**
	 * Button to start server.
	 */
	private JButton btnStart = null;
	
	/**
	 * Button to pause/resume server.
	 */
	private JButton btnPauseResume = null;
	
	/**
	 * Button to stop server.
	 */
	private JButton btnStop = null;
	
	/**
	 * Button to apply changes of server configuration.
	 */
	private JButton btnApplyConfig = null;
	
	/**
	 * Button to reset server configuration.
	 */
	private JButton btnResetConfig = null;
	
	/**
	 * Button to refresh this control panel.
	 */
	private JButton btnRefresh = null;
	
	/**
	 * Reference to remote server.
	 */
	private Server server = null;
	
	/**
	 * Internal time counter.
	 * Every 30 seconds, this control panel updates itself by server information.
	 */
	private Timer timer = null;
	
	
	/**
	 * Constructor with reference to remote server.
	 * @param server reference to remote server.
	 */
	public LightRemoteServerCP(Server server) {
		super("Light remote server control panel");
		
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
	        Image image = UIUtil.getImage("remotecp-32x32.png");
	        if (image != null)
	        	setIconImage(image);
			
			this.server = server;
			
			Container container = getContentPane();
			container.setLayout(new BorderLayout());
			
			JPanel body = new JPanel(new BorderLayout());
			container.add(body, BorderLayout.CENTER);
			
			body.add(new JLabel("Server configuration"), BorderLayout.NORTH);
			paneConfig = new PropPane();
			paneConfig.setControlVisible(false);
			paneConfig.update(server.getConfig());
			body.add(paneConfig);
			
			
			JPanel footer = new JPanel();
			footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
			container.add(footer, BorderLayout.SOUTH);
			
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
			
			timer = new Timer();
			int milisec = 30 * 60 * 1000; //Every 30 seconds, this control panel updates itself by server information.
			timer.schedule(
				new TimerTask() {
				
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							updateControls();
						} 
						catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}, 
				milisec, 
				milisec);

			updateControls();
			setVisible(true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	/**
	 * Start server remotely.
	 */
	private void start() {
		try {
			server.start();
			updateControls();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Pause/resume server remotely.
	 */
	private void pauseResume() {
		try {
			if (server.isPaused())
				server.resume();
			else if (server.isRunning())
				server.pause();
			updateControls();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Stop server remotely.
	 */
	private void stop() {
		try {
			server.stop();
			updateControls();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Apply changes into server configuration remotely.
	 */
	private void applyConfig() {
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
						"Cannot apply", 
						"Cannot apply", 
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			server.setConfig((DataConfig)paneConfig.getPropTable().getPropList());
			JOptionPane.showMessageDialog(
					this, 
					"Apply configuration to server successfully", 
					"Apply successfully", 
					JOptionPane.INFORMATION_MESSAGE);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Reset server configuration.
	 */
	private void resetConfig() {
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
			JOptionPane.showMessageDialog(
					this, 
					"Reset configuration successfully. \n" + 
					"Please press button 'Apply Config' to make store configuration effect", 
					"Please press button 'Apply Config' to make store configuration effect", 
					JOptionPane.INFORMATION_MESSAGE);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Enable/Disable all controls (components) in this control panel.
	 * 
	 * @param enabled if {@code true}, all controls are enabled. Otherwise, all controls are disabled.
	 */
	private void enableControls(boolean enabled) {
		btnStart.setEnabled(enabled);
		btnPauseResume.setEnabled(enabled);
		btnStop.setEnabled(enabled);
		btnApplyConfig.setEnabled(enabled);
		btnResetConfig.setEnabled(enabled);
		btnRefresh.setEnabled(enabled);
		paneConfig.setEnabled(enabled);
	}
	
	
	/**
	 * Update all controls (components) in this control panel according to current server status.
	 * Please see {@link ServerStatusEvent#status} for more details about server statuses.
	 * @throws RemoteException if any error raises.
	 */
	private synchronized void updateControls() throws RemoteException {
		if (server == null)
			return;
		
		if (server.isRunning()) {
			enableControls(false);

			btnStart.setEnabled(false);
			btnPauseResume.setEnabled(false);
			btnPauseResume.setText("Pause");
			btnStop.setEnabled(true);
			
			btnApplyConfig.setEnabled(false);
			btnResetConfig.setEnabled(false);
			btnRefresh.setEnabled(true);
			paneConfig.setEnabled(false);
		}
		else if (server.isPaused()) {
			enableControls(false);

			btnStart.setEnabled(false);
			btnPauseResume.setEnabled(false);
			btnPauseResume.setText("Resume");
			btnStop.setEnabled(true);
			
			btnApplyConfig.setEnabled(false);
			btnResetConfig.setEnabled(false);
			btnRefresh.setEnabled(true);
			paneConfig.setEnabled(false);
		}
		else {
			enableControls(false);

			btnStart.setEnabled(true);
			btnPauseResume.setEnabled(false);
			btnPauseResume.setText("Pause");
			btnStop.setEnabled(false);
			
			btnApplyConfig.setEnabled(true);
			btnResetConfig.setEnabled(true);
			btnRefresh.setEnabled(true);
			paneConfig.setEnabled(true);
		}
		
		try {
			paneConfig.update(server.getConfig());
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Closing this control panel. All resources such as reference to server and time counter are released.
	 */
	private synchronized void close() {
		if (timer != null)
			timer.cancel();
		
		server = null;
		timer = null;
	}
	
	
	/**
	 * This graphic user interface (GUI) component shows a dialog for connecting to remote server.
	 * 
	 * @author Loc Nguyen
	 * @version 10.0
	 *
	 */
	private static class ConnectServerDlg extends JDialog {

		
		/**
		 * Serial version UID for serializable class. 
		 */
		private static final long serialVersionUID = 1L;
		
		
		/**
		 * {@link JTextField} to fill remote host.
		 */
		protected JTextField txtHost = null;
		
		
		/**
		 * {@link JTextField} to fill remote port.
		 */
		protected JTextField txtPort = null;
		
		
		/**
		 * {@link JTextField} to fill remote user name.
		 */
		protected JTextField txtUsername = null;
		
		
		/**
		 * {@link JPasswordField} to fill remote password.
		 */
		protected JPasswordField txtPassword = null;
		
		
		/**
		 * Connected result as remote server.
		 */
		protected Server connServer = null;
		
		
		/**
		 * Constructor with parent component.
		 * @param comp parent component.
		 */
		public ConnectServerDlg(Component comp) {
			super(UIUtil.getFrameForComponent(comp), "Server connection", true);
			
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			setSize(400, 200);
			setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
			setLayout(new BorderLayout());
			
	        Image image = UIUtil.getImage("connect-32x32.png");
	        if (image != null)
	        	setIconImage(image);

			JPanel header = new JPanel(new BorderLayout());
			add(header, BorderLayout.NORTH);
			
			JPanel left = new JPanel(new GridLayout(0, 1));
			header.add(left, BorderLayout.WEST);
			
			left.add(new JLabel("Host:"));
			left.add(new JLabel("Port:"));
			left.add(new JLabel("Username:"));
			left.add(new JLabel("password:"));
			
			JPanel right = new JPanel(new GridLayout(0, 1));
			header.add(right, BorderLayout.CENTER);
			
			txtHost = new JTextField("localhost");
			right.add(txtHost);
			
			txtPort = new JTextField("" + Constants.DEFAULT_SERVER_PORT);
			right.add(txtPort);
			
			txtUsername = new JTextField();
			right.add(txtUsername);

			txtPassword = new JPasswordField();
			right.add(txtPassword);

			
			JPanel footer = new JPanel();
			add(footer, BorderLayout.SOUTH);
			
			JButton connect = new JButton("Connect");
			connect.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					connect();
				}
			});
			footer.add(connect);
			
			JButton close = new JButton("Close");
			close.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					dispose();
				}
			});
			footer.add(close);
			
			setVisible(true);
		}
		
		
		@SuppressWarnings("deprecation")
		private void connect() {
			String host = txtHost.getText().trim();
			String port_s = txtPort.getText().trim();
			int port = -1;
			if (!port_s.isEmpty())
				port = Integer.parseInt(port_s);
				
			this.connServer = DriverManager.getRemoteServer(host, port, txtUsername.getText(), txtPassword.getText());
			
			if (this.connServer == null) {
				JOptionPane.showMessageDialog(
					this, "Can't connect to server", "Can't connect to server", JOptionPane.ERROR_MESSAGE);
			}
			else
				dispose();
		}
		
		
		/**
		 * Getting the connected server as the result of this dialog.
		 * @return connected {@link Server} as the result of this dialog.
		 */
		public Server getServer() {
			return this.connServer;
		}
		
	}
	
	
	
	/**
	 * The main method shows the {@link ConnectServerDlg} for users to enter authenticated information to connect server.
	 * Later on this method shows the {@link LightRemoteServerCP} for users to start, stop, pause and configure sever remotely.
	 * @param args argument parameter of main method. It contains command line arguments.
	 */
	public static void main(String[] args) {
		ConnectServerDlg dlg = new ConnectServerDlg(null);
		
		Server server = dlg.getServer();
		if (server != null)
			new LightRemoteServerCP(server);
	}
	 
	
	
}
