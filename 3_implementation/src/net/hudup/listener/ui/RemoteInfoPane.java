package net.hudup.listener.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.NumberFormatter;

import net.hudup.listener.RemoteInfo;
import net.hudup.listener.RemoteInfoList;



/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class RemoteInfoPane extends JPanel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * 
	 * @author Loc Nguyen
	 * @version 10.0
	 *
	 */
	private enum Mode {addnew, update};

	
	/**
	 * 
	 */
	private RemoteInfoTable tblRemoteInfo = null;
	
	
	/**
	 * 
	 */
	private JButton btnRefresh = null;
	
	
	/**
	 * 
	 */
	private JTextField txtHost = null;

	
	/**
	 * 
	 */
	private JFormattedTextField txtPort = null;

	
	/**
	 * 
	 */
	private JTextField txtAccount = null;

	
	/**
	 * 
	 */
	private JPasswordField txtPwd = null;

	
	/**
	 * 
	 */
	private JButton btnAddNew = null;
	
	
	/**
	 * 
	 */
	private JButton btnSave = null;

	
	/**
	 * 
	 */
	private JButton btnCancel = null;

	
	/**
	 * 
	 */
	private JButton btnRemove = null;

	
	/**
	 * 
	 */
	private Mode mode = Mode.update;

	
	/**
	 * 
	 */
	private RemoteInfoList rInfoList = null;
	
	
	/**
	 * 
	 */
	public RemoteInfoPane(final RemoteInfoList rInfoList) {
		super(new BorderLayout());
		
		this.rInfoList = rInfoList;
		
		JPanel header = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		add(header, BorderLayout.NORTH);
		btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				refresh();
			}
		});
		header.add(btnRefresh);
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);

		tblRemoteInfo = new RemoteInfoTable();
		body.add(new JScrollPane(tblRemoteInfo), BorderLayout.CENTER);
		
		JPanel paneEnter = new JPanel(new BorderLayout());
		body.add(paneEnter, BorderLayout.SOUTH);
		
		JPanel data = new JPanel(new GridLayout(1, 0));
		paneEnter.add(data, BorderLayout.NORTH);
		
		JPanel col1 = new JPanel(new BorderLayout());
		data.add(col1);
		
		JPanel left1 = new JPanel(new GridLayout(0, 1));
		col1.add(left1, BorderLayout.WEST);
		
		left1.add(new JLabel("Host (*): "));
		left1.add(new JLabel("Port: (*)"));
		left1.add(new JLabel("Account: (*)"));
		left1.add(new JLabel("Password: (*)"));
		
		JPanel right1 = new JPanel(new GridLayout(0, 1));
		col1.add(right1, BorderLayout.CENTER);
		
		txtHost = new JTextField();
		right1.add(txtHost);

		txtPort = new JFormattedTextField(new NumberFormatter());
		right1.add(txtPort);

		txtAccount = new JTextField();
		right1.add(txtAccount);

		txtPwd = new JPasswordField();
		right1.add(txtPwd);
		

		JPanel footer = new JPanel(new BorderLayout());
		add(footer, BorderLayout.SOUTH);
		
		JPanel toolbar1 = new JPanel();
		footer.add(toolbar1, BorderLayout.NORTH);
		
		btnAddNew = new JButton("Add new");
		toolbar1.add(btnAddNew);
		btnAddNew.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				changeMode(Mode.addnew);
			}
		});
		
		btnSave = new JButton("Save");
		toolbar1.add(btnSave);
		btnSave.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (save()) {
					JOptionPane.showMessageDialog(
						null, 
						"Update data successfully", 
						"Update data successfully", 
						JOptionPane.INFORMATION_MESSAGE);
					
					return;
				}
				else {
					JOptionPane.showMessageDialog(
							null, 
							"Update data fail", 
							"Update data fail", 
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		btnCancel = new JButton("Cancel");
		toolbar1.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				changeMode(Mode.update);
			}
		});

		
		btnRemove = new JButton("Remove");
		toolbar1.add(btnRemove);
		btnRemove.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (mode == Mode.addnew) {
					JOptionPane.showMessageDialog(
							getThis(), 
							"Can not remove in add new mode", 
							"Can not remove in add new mode", 
							JOptionPane.WARNING_MESSAGE);
					
					return;
				}
				
				if (rInfoList.size() < 2) {
					JOptionPane.showMessageDialog(
							getThis(), 
							"Remote infomation not empty", 
							"Remote infomation not empty", 
							JOptionPane.WARNING_MESSAGE);
					
					return;
				}
				
				int confirm = JOptionPane.showConfirmDialog(
						getThis(), 
						"Are you sure to remove this remote information ?", 
						"Remove remote information", JOptionPane.OK_CANCEL_OPTION, 
						JOptionPane.QUESTION_MESSAGE);
				if (confirm == JOptionPane.OK_OPTION)
					remove();
			}
		});

		
		tblRemoteInfo.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				updateEnterControls();
			}
		});
		
		tblRemoteInfo.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					
					if (mode == Mode.addnew) {
						JOptionPane.showMessageDialog(
								getThis(), 
								"Can not remove in add new mode", 
								"Can not remove in add new mode", 
								JOptionPane.WARNING_MESSAGE);
						
						return;
					}
					
					if (rInfoList.size() < 2) {
						JOptionPane.showMessageDialog(
								getThis(), 
								"Remote infomation not empty", 
								"Remote infomation not empty", 
								JOptionPane.WARNING_MESSAGE);
						
						return;
					}

					int confirm = JOptionPane.showConfirmDialog(
							getThis(), 
							"Are you sure to remove this remote information?", 
							"Remove remote information", JOptionPane.OK_CANCEL_OPTION, 
							JOptionPane.QUESTION_MESSAGE);
					
					if (confirm == JOptionPane.OK_OPTION)
						remove();
				}
				
			}
			
		});
		
		tblRemoteInfo.update(rInfoList);
		if (tblRemoteInfo.getRowCount() > 0)
			tblRemoteInfo.getSelectionModel().addSelectionInterval(0, 0);

		changeMode(Mode.update);
		
		setVisible(true);
	}
	
	
	/**
	 * 
	 * @return this remote pane
	 */
	private RemoteInfoPane getThis() {
		return this;
	}
	
	
	/**
	 * 
	 * @param mode
	 */
	private void changeMode(Mode mode) {
		setVisibleControls(true);
		setEnabledControls(true);
		
		if (mode == Mode.addnew) {
			btnAddNew.setVisible(false);
			btnCancel.setVisible(true);
			btnRemove.setVisible(false);
			
			btnSave.setText("Save");
		}
		else if (mode == Mode.update) {
			txtHost.setEnabled(false);
			txtPort.setEnabled(false);
			
			btnAddNew.setVisible(true);
			btnCancel.setVisible(false);
			btnRemove.setVisible(true);
			
			btnSave.setText("Update");
		}
		
		this.mode = mode;
		updateEnterControls();
	}

	
	/**
	 * 
	 */
	private void updateEnterControls() {
		clearEnterControls();
		
		if (mode == Mode.update) {
			RemoteInfo rInfo = tblRemoteInfo.getSelectedRemoteInfo();
			if (rInfo == null) {
				if (tblRemoteInfo.getRowCount() > 0)
					tblRemoteInfo.getSelectionModel().addSelectionInterval(0, 0);
			}
			else {
				txtHost.setText(rInfo.host);
				txtPort.setValue(rInfo.port);
				txtAccount.setText(rInfo.account);
				txtPwd.setText(rInfo.password.getText());
				
			}
		}
	}

	
	
	/**
	 * 
	 */
	private void clearEnterControls() {
		txtHost.setText("");
		txtPort.setValue(0);
		txtAccount.setText("");
		txtPwd.setText("");
	}

	
	
	/**
	 * 
	 * @param flag
	 */
	private void setVisibleControls(boolean flag) {
		btnRefresh.setVisible(flag);
		tblRemoteInfo.setVisible(flag);
		txtHost.setVisible(flag);
		txtPort.setVisible(flag);
		txtAccount.setVisible(flag);
		txtPwd.setVisible(flag);
		btnAddNew.setVisible(flag);
		btnSave.setVisible(flag);
		btnCancel.setVisible(flag);
	}

	
	/**
	 * 
	 * @param flag
	 */
	private void setEnabledControls(boolean flag) {
		btnRefresh.setEnabled(flag);
		tblRemoteInfo.setEnabled(flag);
		txtHost.setEnabled(flag);
		txtPort.setEnabled(flag);
		txtAccount.setEnabled(flag);
		txtPwd.setEnabled(flag);
		btnAddNew.setEnabled(flag);
		btnSave.setEnabled(flag);
		btnCancel.setEnabled(flag);
	}

	
	/**
	 * 
	 * @return whether data is valid
	 */
	private boolean validateData() {
		String host = getHost();
		String account = getAccount();
		if (host.isEmpty() || account.isEmpty()) {
			JOptionPane.showMessageDialog(
					this, 
					"Remote information not allow empty", 
					"Remote information empty", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		return true;
	}
	
	
	/**
	 * 
	 * @return whether save successfully
	 */
	private boolean save() {
		if (!validateData())
			return false;
		
		RemoteInfo rInfo = new RemoteInfo(
				getHost(), getPort(), getAccount(), getPwd());
		
		boolean result = false;
		if (mode == Mode.addnew)
			result = rInfoList.add(rInfo);
		else
			result = (rInfoList.set(rInfo) != null);
		
		tblRemoteInfo.update(rInfoList);
		tblRemoteInfo.selectRemoteInfo(rInfo.host, rInfo.port);
		
		if (mode == Mode.addnew)
			changeMode(Mode.update);
		else
			updateEnterControls();
			
		return result;
	}
	
	
	/**
	 * 
	 * @return whether remove successfully
	 */
	private boolean remove() {
		if (mode == Mode.addnew || rInfoList.size() < 2)
			return false;
		
		int selected = tblRemoteInfo.getSelectedRow();
		RemoteInfo rInfo = tblRemoteInfo.getSelectedRemoteInfo();
		if (rInfo == null)
			return false;
		
		rInfoList.remove(rInfo.host, rInfo.port);
		
		boolean result = (rInfoList.remove(rInfo.host, rInfo.port) != null);
		
		tblRemoteInfo.update(rInfoList);
		if (selected != -1 && selected < tblRemoteInfo.getRowCount())
			tblRemoteInfo.getSelectionModel().addSelectionInterval(selected, selected);
		
		updateEnterControls();
		return result;
	}

	
	/**
	 * 
	 * @return host
	 */
	private String getHost() {
		return txtHost.getText().trim();
	}

	
	/**
	 * 
	 * @return port
	 */
	private int getPort() {
		return Integer.parseInt(txtPort.getValue().toString());
	}
	
	
	/**
	 * 
	 * @return account
	 */
	private String getAccount() {
		return txtAccount.getText().trim();
	}

	
	/**
	 * 
	 * @return password
	 */
	@SuppressWarnings("deprecation")
	private String getPwd() {
		return txtPwd.getText();
	}
	
	
	/**
	 * 
	 */
	public void refresh() {
		// Do something
		RemoteInfo rInfo = tblRemoteInfo.getSelectedRemoteInfo();
		tblRemoteInfo.update(rInfoList);
		
		if (rInfo != null)
			tblRemoteInfo.selectRemoteInfo(rInfo.host, rInfo.port);
	}
	
	
	
	/**
	 * 
	 * @return {@link RemoteInfoList}
	 */
	public RemoteInfoList getRemoteInfoList() {
		return rInfoList;
	}
	
	
	
}
