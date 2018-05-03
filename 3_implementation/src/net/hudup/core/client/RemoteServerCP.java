package net.hudup.core.client;

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

import net.hudup.core.client.ServerStatusEvent.Status;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.ui.PropPane;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.UIUtil;


/**
 * This graphic user interface and a window application {@link JFrame} allows users to start, stop, pause, resume, configure {@link Server} remotely.
 * This class is also called powerful control panel for remote server, which shares many function which the lightweight control panel represented by {@link LightRemoteServerCP}.
 * The difference from {@link LightRemoteServerCP} is to update server status immediately because this {@link RemoteServerCP} has two advanced features as follows:
 * <ul>
 * <li>This control panel is itself a {@link ServerStatusListener} that is registered with server before.</li>
 * <li>This control panel is itself a remote RMI object that allows other applications to interact with it via RMI protocol.</li>
 * </ul>
 * Therefore, every time the server changes status, the server calls the method {@link #statusChanged(ServerStatusEvent)} of this control panel to update server current status immediately.
 * <br>
 * RMI is abbreviation of Java Remote Method Invocation for remote interaction in client-server architecture. The tutorial of RMI is available at <a href="https://docs.oracle.com/javase/tutorial/rmi">https://docs.oracle.com/javase/tutorial/rmi</a>.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class RemoteServerCP extends JFrame implements ServerStatusListener {

	
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
	 * RMI registry for exposing this control panel as remote RMI object. Please see {@link Registry} for more details about RMI registry.
	 */
	private Registry registry = null;
	
	/**
	 * Binded URI of this control panel as remote RMI object. It is URI pointing to where this control panel is located.
	 */
	private xURI bindUri = null;

	
	/**
	 * Constructor with reference to remote server and binded URI of this control panel as remote RMI object.
	 * @param server reference to remote server.
	 * @param bindUri binded URI of this control panel as remote RMI object.
	 */
	public RemoteServerCP(Server server, xURI bindUri) {
		super("Power remote server control panel");
		
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
			this.bindUri = bindUri;
			
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
			btnRefresh.setAlignmentX(LEFT_ALIGNMENT);
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
			
			bindServer();
			
			updateControls();
			setVisible(true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	/**
	 * Binding (exposing) this control panel as remote RMI object so that server or other applications can interact with it via RMI protocol.
	 * The internal variable {@link #bindUri} pointing to where to locate this control panel.
	 */
	private void bindServer() {
		boolean result = false;
		
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
		}
		
		
		if (result)
			btnRefresh.setVisible(false);
		
	}
	

	/**
	 * Start server remotely.
	 */
	private void start() {
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
	 * Pause/resume server remotely.
	 */
	private void pauseResume() {
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
	 * Stop server remotely.
	 */
	private void stop() {
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
	private void updateControls() throws RemoteException {
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
	
	
	/**
	 * Update all controls (components) in this control panel according to current server status.
	 * Please see {@link ServerStatusEvent#status} for more details about server statuses.
	 * @param state server current status.
	 */
	private void updateControls(ServerStatusEvent.Status state) throws RemoteException {
		
		if (state == Status.started || state == Status.resumed) {
			enableControls(false);

			btnStart.setEnabled(false);
			btnPauseResume.setEnabled(false);
			btnPauseResume.setText("Pause");
			btnStop.setEnabled(true);
			
			btnApplyConfig.setEnabled(false);
			btnResetConfig.setEnabled(false);
			paneConfig.setEnabled(false);
			
			try {
				paneConfig.update(server.getConfig());
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		else if (state == Status.paused) {
			enableControls(false);

			btnStart.setEnabled(false);
			btnPauseResume.setEnabled(false);
			btnPauseResume.setText("Resume");
			btnStop.setEnabled(true);
			
			btnApplyConfig.setEnabled(false);
			btnResetConfig.setEnabled(false);
			paneConfig.setEnabled(false);
		}
		else if (state == Status.stopped) {
			enableControls(false);

			btnStart.setEnabled(true);
			btnPauseResume.setEnabled(false);
			btnPauseResume.setText("Pause");
			btnStop.setEnabled(false);
			
			btnApplyConfig.setEnabled(true);
			btnResetConfig.setEnabled(true);
			paneConfig.setEnabled(true);
		}
		else if (state == Status.setconfig) {
			paneConfig.update(server.getConfig());
		}
		else if (state == Status.exit) {
			server = null;
			dispose();
		}
		
	}

	
	@Override
	public void statusChanged(ServerStatusEvent evt) throws RemoteException {
		// TODO Auto-generated method stub
		updateControls(evt.getStatus());
	}
	
	
	/**
	 * Closing this control panel. All resources such as reference to server and time counter are released.
	 */
	private void close() {
		
    	try {
    		if (server != null)
    			server.removeStatusListener(this);
    	}
    	catch (Throwable e) {
    		e.printStackTrace();
    	}
		
    	try {
    		if (bindUri != null)
    			UnicastRemoteObject.unexportObject(this, true);
    	}
    	catch (Throwable e) {
    		e.printStackTrace();
    	}

    	try {
    		if (registry != null)
    			UnicastRemoteObject.unexportObject(registry, true);
    	}
    	catch (Throwable e) {
    		e.printStackTrace();
    	}
    	
    	
    	server = null;
		bindUri = null;
		registry = null;
		
	}


	/**
	 * The main method shows the {@link ConnectServerDlg} for users to enter authenticated information to connect server.
	 * Later on this method shows the this control panel for users to start, stop, pause and configure sever remotely.
	 * @param args argument parameter of main method. It contains command line arguments.
	 */
	public static void main(String[] args) {
		ConnectServerDlg dlg = new ConnectServerDlg();
		
		Server server = dlg.getServer();
		if (server != null)
			new RemoteServerCP(server, dlg.getBindUri());
	}


}
