package net.hudup.data.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;

import net.hudup.core.Util;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.DataDriver.DataType;
import net.hudup.core.data.DataDriverList;
import net.hudup.core.data.ExternalConfig;
import net.hudup.core.data.HiddenText;
import net.hudup.core.data.ParamSql;
import net.hudup.core.data.Provider;
import net.hudup.core.data.ui.DataDriverComboBox;
import net.hudup.core.data.ui.PropTable;
import net.hudup.core.data.ui.UnitListBox;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.data.ProviderImpl;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ExternalConfigurator extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	private DataDriverComboBox  cmbDataDrivers = null;
	
	private JTextField txtHost = null;
	
	private JFormattedTextField txtPort = null;
	
	private JTextField txtPath = null;

	private JButton btnPath = null;
	
	private JTextField txtUsername = null;
	
	private JPasswordField txtPassword = null;

	private UnitListBox lbUnits = null;
	
	private PropTable tblConfig = null;

	private ExternalConfig externalConfig = null;

	private ExternalConfig config = null;
	
	
	/**
	 * 
	 * @param comp
	 * @param dataDriverList
	 * @param defaultConfig
	 */
	public ExternalConfigurator(
			Component comp, 
			DataDriverList dataDriverList, 
			ExternalConfig defaultConfig) {
		super(UIUtil.getFrameForComponent(comp), "External configurator", true);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		
		externalConfig = new ExternalConfig();

		//////////////////////// Header /////////////////////////
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		JPanel dataDrivers = new JPanel();
		header.add(dataDrivers, BorderLayout.NORTH);
		
		dataDrivers.add(new JLabel("Drivers"));
		
		cmbDataDrivers = new DataDriverComboBox(dataDriverList);
		cmbDataDrivers.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if (e.getStateChange() == ItemEvent.SELECTED)
					dataDriverChanged();
			}
		});
		dataDrivers.add(cmbDataDrivers);
		
		JPanel attNames = new JPanel(new GridLayout(0, 1));
		header.add(attNames, BorderLayout.WEST);
		attNames.add(new JLabel("Host:"));
		attNames.add(new JLabel("Port:"));
		attNames.add(new JLabel("Path:"));
		attNames.add(new JLabel("Username:"));
		attNames.add(new JLabel("Password:"));
		
		JPanel pane = null;
		
		JPanel attValues = new JPanel(new GridLayout(0, 1));
		header.add(attValues, BorderLayout.CENTER);
		//
		txtHost = new JTextField("localhost");
		attValues.add(txtHost);
		//
		txtPort = new JFormattedTextField(new NumberFormatter());
		txtPort.setValue(-1);
		attValues.add(txtPort);
		//
		pane = new JPanel(new BorderLayout());
		attValues.add(pane);
		txtPath = new JTextField();
		pane.add(txtPath, BorderLayout.CENTER);
		btnPath = new JButton("Browse");
		pane.add(btnPath, BorderLayout.EAST);
		btnPath.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				UriAdapter adapter = new UriAdapter();
				xURI uri = adapter.chooseUri(getThis(), true, null, null, null);
				adapter.close();
				
				if (uri == null)
					return;
				
				txtPath.setText(uri.toString());
			}
		});
		//
		txtUsername = new JTextField();
		attValues.add(txtUsername);
		//
		txtPassword = new JPasswordField();
		attValues.add(txtPassword);

		pane = new JPanel();
		JButton connect = new JButton("Connect");
		pane.add(connect);
		connect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				connect();
			}
		});
		header.add(pane, BorderLayout.SOUTH);

		//////////////////////////// Body ///////////////////////////
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		
		JPanel left = new JPanel(new BorderLayout());
		body.add(left, BorderLayout.WEST);
		
		left.add(new JLabel("List of units"), BorderLayout.NORTH);
		lbUnits = new UnitListBox();
		left.add(new JScrollPane(lbUnits), BorderLayout.CENTER);
		
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(0, 1));
		left.add(buttons, BorderLayout.EAST);
		
		JButton setUser = new JButton("Set user");
		setUser.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				setUnit(ExternalConfig.USER_UNIT);
			}
		});
		buttons.add(setUser);

		JButton setItem = new JButton("Set item");
		setItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				setUnit(ExternalConfig.ITEM_UNIT);
			}
		});
		buttons.add(setItem);

		JButton setRating = new JButton("Set rating");
		setRating.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				setUnit(ExternalConfig.RATING_UNIT);
			}
		});
		buttons.add(setRating);
		
		
		JPanel right = new JPanel(new BorderLayout());
		body.add(right, BorderLayout.CENTER);
		
		right.add(new JLabel("Configurations"), BorderLayout.NORTH);
		tblConfig = new PropTable();
		right.add(new JScrollPane(tblConfig), BorderLayout.CENTER);
		
		////////////////////// Footer //////////////////////////
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);

		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				onOk();
			}
		});
		footer.add(ok);
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				config = null;
				externalConfig = null;
				dispose();
			}
		});
		footer.add(cancel);

		
		dataDriverChanged();
		updateDefaultConfig(defaultConfig);

		setVisible(true);
	}
	
	
	/**
	 * 
	 * @return this {@link ExternalConfigurator}
	 */
	private ExternalConfigurator getThis() {
		return this;
	}

	
	/**
	 * 
	 * @param defaultCfg
	 */
	private void updateDefaultConfig(ExternalConfig defaultCfg) {
		if (defaultCfg == null)
			return;
		
		DataDriver dataDriver = null;
		String dataDriverName = defaultCfg.getDataDriverName();
		if (dataDriverName != null)
			dataDriver = DataDriver.createByName(dataDriverName);
		
		cmbDataDrivers.selectDataDriver(defaultCfg.getDataDriverName());
		
		xURI store = defaultCfg.getStoreUri();
		if (store == null)
			return;
		
		String host = store.getHost();
		if (host == null)
			host = "localhost";
		txtHost.setText(host);
		
		int port = store.getPort();
		if (port < 0 && dataDriver != null)
			port = dataDriver.getDefaultPort();
		txtPort.setValue(port);
		
		String path = store.getPath();
		if (path == null) {
			path = store.getLastName();
		}
		else if (dataDriver != null && dataDriver.isFlatServer()) {
			String ratingUnit = defaultCfg.getRatingUnit();
			if (ratingUnit != null && !ratingUnit.isEmpty()) {
				if (path.endsWith("/"))
					path += ratingUnit;
				else
					path += "/" + ratingUnit;
			}
		}
		if (path == null)
			path = "";
		txtPath.setText(normalizePath(path));
		
		String username = defaultCfg.getStoreAccount();
		if (username == null)
			username = "";
		txtUsername.setText(username);
		
		HiddenText password = defaultCfg.getStorePassword();
		if (password == null)
			password = new HiddenText("");
		txtPassword.setText(password.getText());
		
	}
	
	
	/**
	 * 
	 */
	private void dataDriverChanged() {
		DataDriver driver = (DataDriver)cmbDataDrivers.getSelectedItem();
		if (driver == null)
			return;
		
		setConnectInfoControlVisible(true);
		txtPort.setValue(driver.getDefaultPort());
		txtHost.setText("localhost");
		
		if (driver.isFlatServer()) {
			txtHost.setText("");
			txtHost.setVisible(false);
			
			txtPort.setValue(-1);
			txtPort.setVisible(false);
			
			txtUsername.setText("");
			txtUsername.setVisible(false);
			
			txtPassword.setText("");
			txtPassword.setVisible(false);
			
			btnPath.setVisible(true);
		}
		else {
			btnPath.setVisible(false);
		}
		
	}
	

	/**
	 * 
	 * @param aFlag
	 */
	private void setConnectInfoControlVisible(boolean aFlag) {
		txtHost.setVisible(aFlag);
		txtPort.setVisible(aFlag);
		txtPath.setVisible(aFlag);
		btnPath.setVisible(aFlag);
		txtUsername.setVisible(aFlag);
		txtPassword.setVisible(aFlag);
	}


	/**
	 * 
	 * @return {@link DataDriver}
	 */
	private DataDriver getDataDriver() {
		return (DataDriver)cmbDataDrivers.getSelectedItem();
	}


	/**
	 * 
	 */
	private void onOk() {
		if (externalConfig == null || externalConfig.size() == 0 || externalConfig.getStoreUri() == null) {
			JOptionPane.showMessageDialog(
					this, 
					"Not connect yet", 
					"Not connect yet", 
					JOptionPane.WARNING_MESSAGE);
			
			return;
		}
		
		config = (ExternalConfig) externalConfig.clone();
		externalConfig = null;
		dispose(); 
	}

	
	@SuppressWarnings("deprecation")
	private void connect() {
		externalConfig.clear();
		tblConfig.update(externalConfig);
		
		txtPath.setText(normalizePath(txtPath.getText()));
		DataDriver dataDriver = getDataDriver();
		String host = txtHost.getText().trim();
		host = host.isEmpty() ? null : host;
		int port = txtPort.getValue() instanceof Number ? ( (Number) txtPort.getValue()).intValue() : -1; 
		xURI uri = dataDriver.getUri(host, port, txtPath.getText());
		
		ExternalConfig config = new ExternalConfig();
		config.setStoreAccount(txtUsername.getText().trim());
		config.setStorePassword( new HiddenText(txtPassword.getText()) );
		config.setStoreUri(uri);
		
		boolean connect = lbUnits.connectUpdate(config);
		lbUnits.validate();
		lbUnits.updateUI();
		if (!connect) {
			externalConfig.clear();
			JOptionPane.showMessageDialog(
					this, "Connect fail", "Connect fail", JOptionPane.ERROR_MESSAGE);
			return;
		}
		else
			JOptionPane.showMessageDialog(
					this, "Connect successfully", "Connect successfully", JOptionPane.INFORMATION_MESSAGE);
		
		externalConfig.reset();
		externalConfig.setStoreUri(uri);
		if (dataDriver.isFlatServer()) {
			
		}
		else if (dataDriver.isDbServer()) {
			// It is OK
		}
		else if (dataDriver.isRecommendServer()) {
			
		}
		
		if (dataDriver.getType() != DataType.file) {
			externalConfig.setStoreAccount(txtUsername.getText().trim());
			externalConfig.setStorePassword( new HiddenText(txtPassword.getText()) );
		}
		
		List<String> unitNameList = lbUnits.getUnitList().toNameList();
		
		if (!unitNameList.contains(ExternalConfig.ITEM_UNIT)) {
			externalConfig.remove(ExternalConfig.ITEM_UNIT);
			externalConfig.remove(ExternalConfig.ITEMID_FIELD);
			externalConfig.remove(ExternalConfig.ITEM_TYPE_FIELD);
		}
		
		if (!unitNameList.contains(ExternalConfig.USER_UNIT)) {
			externalConfig.remove(ExternalConfig.USER_UNIT);
			externalConfig.remove(ExternalConfig.USERID_FIELD);
			externalConfig.remove(ExternalConfig.USER_TYPE_FIELD);
		}

		if (!unitNameList.contains(ExternalConfig.RATING_UNIT)) {
			externalConfig.remove(ExternalConfig.RATING_UNIT);
			externalConfig.remove(ExternalConfig.RATING_USERID_FIELD);
			externalConfig.remove(ExternalConfig.RATING_ITEMID_FIELD);
			externalConfig.remove(ExternalConfig.RATING_FIELD);
		}

		externalConfig.setDataDriverName(getDataDriver().getName());

		tblConfig.update(externalConfig);
	}

	
	/**
	 * 
	 * @return {@link ExternalConfig}
	 */
	public ExternalConfig getResult() {
		return config;
	}

	
	/**
	 * 
	 * @param unitType
	 */
	private void setUnit(String unitType) {
		if (externalConfig == null || externalConfig.size() == 0 || externalConfig.getStoreUri() == null) {
			JOptionPane.showMessageDialog(
					this, 
					"Not connect yet", 
					"Not connect yet", 
					JOptionPane.WARNING_MESSAGE);
			
			return;
		}
		
		String unit = lbUnits.getSelectedValue() == null ? null : lbUnits.getSelectedValue().getName();
		if (unit == null || unit.isEmpty()) {
			JOptionPane.showMessageDialog(
					this, 
					"Unit not chosen", 
					"Unit not chosen", 
					JOptionPane.WARNING_MESSAGE);
			
			return;
		}
		
		List<String> mappingFields = Util.newList();
		if (unitType.equals(ExternalConfig.USER_UNIT)) {
			mappingFields = Arrays.asList(new String[] { ExternalConfig.USERID_FIELD, ExternalConfig.USER_TYPE_FIELD});
		}
		else if (unitType.equals(ExternalConfig.ITEM_UNIT)) {
			mappingFields = Arrays.asList(new String[] { ExternalConfig.ITEMID_FIELD, ExternalConfig.ITEM_TYPE_FIELD});
		}
		else if (unitType.equals(ExternalConfig.RATING_UNIT)) {
			mappingFields = Arrays.asList(new String[] { 
						ExternalConfig.RATING_USERID_FIELD, 
						ExternalConfig.RATING_ITEMID_FIELD, 
						ExternalConfig.RATING_FIELD});
		}
		else {
			JOptionPane.showMessageDialog(
					this, 
					"Not support this unit type", 
					"Not support this unit type", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		ExternalConfig mappedConfig = new ExternalConfig();
		String selectSql = null;

		DataDriver ddriver = DataDriver.create(externalConfig.getStoreUri());
		if (ddriver.isDbServer()) {
			MappingColumnDlg2 mapDlg = new MappingColumnDlg2(
					getThis(),
					externalConfig, 
					mappingFields,
					unit);
			
			mappedConfig = mapDlg.getResult();
			selectSql = mapDlg.getSelectSql();
		}
		else {
			MappingColumnDlg mapDlg = new MappingColumnDlg(
					getThis(),
					externalConfig, 
					mappingFields,
					unit);
			
			mappedConfig = mapDlg.getResult();
		}
		
		if (mappedConfig.size() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Fields not mapped", 
					"Fields not mapped", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		externalConfig.putAll(mappedConfig);
		if (unit != null)
			externalConfig.setUnit(unitType, unit);
		
		if (selectSql != null && !selectSql.isEmpty()) {
			if (unitType.equals(DataConfig.USER_UNIT))
				externalConfig.setUserSql(selectSql);
			else if (unitType.equals(DataConfig.ITEM_UNIT))
				externalConfig.setItemSql(selectSql);
			else if (unitType.equals(DataConfig.RATING_UNIT))
				externalConfig.setRatingSql(selectSql);
		}
		
		tblConfig.update(externalConfig);
	}
	
	
	/**
	 * 
	 * @param path
	 * @return normalized path, remove all back slash
	 */
	private String normalizePath(String path) {
		return path.replaceAll("\\\\", "/");
	}


}


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
class MappingColumnDlg extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * 
	 */
	protected MappingTable tblMapping = null;
	
	
	/**
	 * 
	 */
	protected ExternalConfig result = new ExternalConfig();
	
	
	/**
	 * 
	 * @param attributes
	 * @param mappingFields
	 */
	public MappingColumnDlg(final Component comp, ExternalConfig externalConfig, final List<String> mappingFields, String unit) {
		super(UIUtil.getFrameForComponent(comp), "Mapping fields", true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(this);
		
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		header.add(new JLabel("Mapping fields"), BorderLayout.NORTH);
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);

		Provider provider = new ProviderImpl(externalConfig);
		AttributeList attributes = provider.getProfileAttributes(unit);
		provider.close();
		
		tblMapping = new MappingTable(attributes, mappingFields);
		body.add(new JScrollPane(tblMapping), BorderLayout.CENTER);

		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				ExternalConfig config = tblMapping.getMappingTM().extractResult();
				
				if (config.size() == 0) {
					result.clear();
					JOptionPane.showMessageDialog(
							UIUtil.getFrameForComponent(comp), 
							"Some fields not mapped", 
							"Some fields not mapped", 
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				result = config;
				
				dispose();
			}
		});
		footer.add(ok);
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				result.clear();
				dispose();
			}
		});
		footer.add(cancel);
		
		setVisible(true);
	}
	
	
	/**
	 * 
	 * @return mapping configuration
	 */
	public ExternalConfig getResult() {
		return result;
	}
	
	
}



/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class MappingColumnDlg2 extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * 
	 */
	protected JTextArea txtExternalSql = null;
	
	
	/**
	 * 
	 */
	protected MappingTable tblMapping = null;
	
	
	/**
	 * 
	 */
	protected ExternalConfig result = new ExternalConfig();
	
	
	/**
	 * 
	 */
	protected Provider externalProvider = null;
	
	
	/**
	 * 
	 * @param unit
	 * @param mappingFields
	 */
	public MappingColumnDlg2(final Component comp, final ExternalConfig externalConfig, final List<String> mappingFields, String unit) {
		super(UIUtil.getFrameForComponent(comp), "Mapping fields", true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(this);
		
		setLayout(new BorderLayout());
		
		externalProvider = new ProviderImpl((ExternalConfig) externalConfig.clone());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		header.add(new JLabel("External query"), BorderLayout.NORTH);

		txtExternalSql = new JTextArea(5, 5);
		if (unit != null && !unit.isEmpty())
			txtExternalSql.setText("select * from " + unit);
		header.add(new JScrollPane(txtExternalSql), BorderLayout.CENTER);
		
		JPanel tool = new JPanel();
		header.add(tool, BorderLayout.SOUTH);
		JButton btnQuery = new JButton("Query");
		tool.add(btnQuery);
		btnQuery.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				tblMapping.clear();
				AttributeList attributes = externalProvider.getProfileAttributes(new ParamSql(txtExternalSql.getText()), null);
				tblMapping.update(attributes, mappingFields);
			}
			
		});
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);

		tblMapping = new MappingTable();
		body.add(new JScrollPane(tblMapping), BorderLayout.CENTER);

		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				ExternalConfig config = tblMapping.getMappingTM().extractResult();
				
				if (config.size() == 0) {
					result.clear();
					JOptionPane.showMessageDialog(
							UIUtil.getFrameForComponent(comp), 
							"Some fields not mapped", 
							"Some fields not mapped", 
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				
				result = config;
				
				dispose();
			}
		});
		footer.add(ok);
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				result.clear();
				dispose();
			}
		});
		footer.add(cancel);
		
		setVisible(true);
	}
	
	
	/**
	 * 
	 * @return mapping configuration
	 */
	public ExternalConfig getResult() {
		return result;
	}

	
	/**
	 * 
	 * @return select SQL
	 */
	public String getSelectSql() {
		return txtExternalSql.getText().trim();
	}
	
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
		
		if (externalProvider != null)
			externalProvider.close();
		externalProvider = null;
	}
	
	
	
	
}


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class MappingTable extends JTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * 
	 */
	public MappingTable() {
		super(new MappingTM());
		setAutoResizeMode(AUTO_RESIZE_OFF);
	}
	
	
	/**
	 * 
	 * @param attributes
	 * @param mappingFields
	 */
	public MappingTable(AttributeList attributes, List<String> mappingFields) {
		super(new MappingTM(attributes, mappingFields));
		setAutoResizeMode(AUTO_RESIZE_OFF);
	}


	/**
	 * 
	 * @param attributes
	 * @param mappingFields
	 */
	public void update(AttributeList attributes, List<String> mappingFields) {
		getMappingTM().update(attributes, mappingFields);
	}
	
	
	/**
	 * 
	 * @return mapping table
	 */
	public MappingTM getMappingTM() {
		return (MappingTM) getModel();
	}
	
	
	/**
	 * 
	 */
	public void clear() {
		getMappingTM().clear();
	}
	
	
}



/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class MappingTM extends DefaultTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * 
	 */
	public MappingTM() {
		
	}
	
	
	/**
	 * 
	 * @param attributes
	 * @param mappingFields
	 */
	public MappingTM(AttributeList attributes, List<String> mappingFields) {
		update(attributes, mappingFields);
	}

	
	/**
	 * 
	 * @param attributes
	 * @param mappingFields
	 */
	public void update(AttributeList attributes, List<String> mappingFields) {
		Vector<Vector<Object>> data = Util.newVector();
		
		for (String mappingField : mappingFields) {
			Vector<Object> row = Util.newVector();
			
			row.add(mappingField);
			for (int i = 0; i < attributes.size(); i++) {
				row.add(false);
			}
			
			data.add(row);
		}
		
		setDataVector(data, toColumns(attributes));
	}
	
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		// TODO Auto-generated method stub
		if (columnIndex > 0)
			return Boolean.class;
		else
			return super.getColumnClass(columnIndex);
	}


	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return column > 0;
	}


	/**
	 * 
	 * @param attributes
	 * @return column identifiers
	 */
	static Vector<String> toColumns(AttributeList attributes) {
		Vector<String> columns = Util.newVector();
		
		columns.add("");
		for (int i = 0; i < attributes.size(); i++) {
			Attribute attribute = attributes.get(i);
			columns.add(attribute.getName());
		}
		
		return columns;
	}
	
	
	
	@Override
	public void setValueAt(Object aValue, int row, int column) {
		// TODO Auto-generated method stub
		super.setValueAt(aValue, row, column);
		
		if ( (aValue instanceof Boolean) && ((Boolean)aValue) ) {
			int n = getColumnCount();
			
			for (int j = 1; j < n; j++) {
				if (j == column)
					continue;
				boolean value = (Boolean) getValueAt(row, j);
				if (value)
					super.setValueAt(false, row, j);
			}
			
		}
	}


	/**
	 * 
	 * @return result {@link ExternalConfig}
	 */
	public ExternalConfig extractResult() {
		ExternalConfig config = new ExternalConfig();
		
		int m = getRowCount();
		int n = getColumnCount();
		for (int i = 0; i < m; i++) {
			String mappingField = (String) getValueAt(i, 0);
			
			int selectedIndex = -1;
			for (int j = 1; j < n; j++) {
				boolean mappedField = (Boolean) getValueAt(i, j);
				if (mappedField) {
					selectedIndex = j;
					break;
				}
			}
			
			if (selectedIndex == -1)
				return new ExternalConfig();
			else
				config.put(mappingField, getColumnName(selectedIndex));
		}
		
		return config;
	}
	
	
	/**
	 * 
	 */
	public void clear() {
		Vector<Vector<Object>> data = Util.newVector();
		Vector<String> columns = Util.newVector();
		
		setDataVector(data, columns);
	}
	
	
	
	
}
