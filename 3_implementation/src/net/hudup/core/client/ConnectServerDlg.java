package net.hudup.core.client;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.hudup.core.Constants;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.UIUtil;



/**
 * This graphic user interface (GUI) component shows a dialog for connecting to remote server.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ConnectServerDlg extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * {@link JTextField} to fill remote host.
	 */
	protected JTextField txtRemoteHost = null;
	
	
	/**
	 * {@link JTextField} to fill remote port.
	 */
	protected JTextField txtRemotePort = null;
	
	
	/**
	 * {@link JTextField} to fill remote user name.
	 */
	protected JTextField txtRemoteUsername = null;
	
	
	/**
	 * {@link JPasswordField} to fill remote password.
	 */
	protected JPasswordField txtRemotePassword = null;
	
	
	/**
	 * Connected result as remote server.
	 */
	protected Server server = null;
	
	
	/**
	 * Default constructor.
	 */
	public ConnectServerDlg() {
		super((JFrame)null, "Server connection", true);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(400, 200);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		
        Image image = UIUtil.getImage("connect-32x32.png");
        if (image != null)
        	setIconImage(image);

        JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		
		left.add(new JLabel("Remote host:"));
		left.add(new JLabel("Remote port:"));
		left.add(new JLabel("Remote username:"));
		left.add(new JLabel("Remote password:"));
		
		JPanel right = new JPanel(new GridLayout(0, 1));
		header.add(right, BorderLayout.CENTER);
		
		txtRemoteHost = new JTextField("localhost");
		right.add(txtRemoteHost);
		
		txtRemotePort = new JTextField("" + Constants.DEFAULT_SERVER_PORT);
		right.add(txtRemotePort);
		
		txtRemoteUsername = new JTextField();
		right.add(txtRemoteUsername);

		txtRemotePassword = new JPasswordField();
		right.add(txtRemotePassword);

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
	
	
	/**
	 * Connect to remote server.
	 */
	@SuppressWarnings("deprecation")
	private void connect() {
		String remoteHost = txtRemoteHost.getText().trim();
		String remotePort_s = txtRemotePort.getText().trim();
		int remotePort = -1;
		if (!remotePort_s.isEmpty())
			remotePort = Integer.parseInt(remotePort_s);
			
		server = DriverManager.getRemoteServer(remoteHost, remotePort, txtRemoteUsername.getText(), txtRemotePassword.getText());
		
		if (server == null) {
			JOptionPane.showMessageDialog(
				this, "Can't connect to server", "Can't connect to server", JOptionPane.ERROR_MESSAGE);
		}
		else {
			dispose();
		}
	}
	
	
	/**
	 * Creating the binded URI. In current implementation, it is &quot;rmi://localhost:&lt;port&gt;/connect&quot;
	 * @return binded URI.
	 */
	public xURI getBindUri() {
		int port = NetUtil.getPort(Constants.DEFAULT_CONTROL_PANEL_PORT, true);
		if (port < 0)
			return null;
		else 
			return xURI.create( "rmi://localhost:" + port + "/connect");
	}
	
	
	/**
	 * Getting result as remote server.
	 * @return remote {@link Server}.
	 */
	public Server getServer() {
		return server;
	}
	
	
}
