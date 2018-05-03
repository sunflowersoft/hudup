/**
 * 
 */
package net.hudup.core.logistic.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.text.NumberFormatter;

import net.hudup.core.PluginStorage;
import net.hudup.core.RegisterTable;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.DataDriver.DataType;
import net.hudup.core.data.DataDriverList;
import net.hudup.core.data.HiddenText;
import net.hudup.core.data.Unit;
import net.hudup.core.data.UnitList;
import net.hudup.core.data.ui.DataDriverComboBox;
import net.hudup.core.data.ui.DatasetParserComboBox;
import net.hudup.core.data.ui.UnitListBox;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.DatasetParser;
import net.hudup.core.parser.FlatServerIndicator;
import net.hudup.core.parser.Indicator;


/**
 * This graphic user interface (GUI) component shows a dialog for users to choose URI (s) of units.
 * Of course, firstly the {@link UriChooser} connects with the remote storage (database, file system) in order to show units in such storage.
 * Note, {@link UnitList} represents a list of many units; as a convention, it is called unit list.
 * Recall objects in framework such as {@code profile}, {@code item profile}, {@code user profile}, {@code rating matrix}, {@code interchanged attribute map} are stored in archives (files) of entire framework.
 * Each archive (file) is called {@code unit} representing a CSV file, database table, Excel sheet, etc. Unit is represented by {@link Unit} class.
 * <br>
 * After that, {@link UriChooser} allows users to choose of units. The result is list of URI (s) of such units.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class UriChooser extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * {@link JTextField} to fill in remote host.
	 */
	private JTextField txtHost = null;
	
	/**
	 * {@link JTextField} to fill in remote port.
	 */
	private JFormattedTextField txtPort = null;
	
	/**
	 * {@link JTextField} to fill in path of remote storage.
	 */
	private JTextField txtPath = null;
	
	/**
	 * Button for users to choose the path of remote storage.
	 */
	private JButton btnPath = null;
	
	/**
	 * Authenticated user name.
	 */
	private JTextField txtUsername = null;
	
	/**
	 * Authenticated password.
	 */
	private JPasswordField txtPassword = null;
	
	/**
	 * {@link DatasetParserComboBox} to list dataset parsers.
	 */
	private DatasetParserComboBox cmbParsers = null;
	
	/**
	 * {@link DataDriverComboBox} to list data drivers.
	 */
	private DataDriverComboBox cmbDataDrivers = null;
	
	/**
	 * {@link UnitListBox} to show units in remote storage.
	 */
	private UnitListBox lbUnits = null;
	
	/**
	 * {@link UnitListBox} to list selected units.
	 */
	private UnitListBox lbSelectedUnits = null;

	
	/**
	 * Default data driver list shown in the {@link #cmbDataDrivers}.
	 */
	private DataDriverList thisDataDriverList = null;
	
	/**
	 * The default configuration to initialize this {@link UriChooser}.
	 */
	private DataConfig internalConfig = null;
	
	/**
	 * The result as a list of URI (s) of chosen units.
	 */
	private List<xURI> result = null;
	
	
	/**
	 * Constructor with list of dataset parsers, default data driver list, and default configuration.
	 * @param comp parent component.
	 * @param parserList list of dataset parsers shown in {@link #cmbParsers}.
	 * @param dataDriverList default data driver list shown in the {@link #cmbDataDrivers}.
	 * @param defaultConfig default configuration to initialize this {@link UriChooser}.
	 */
	public UriChooser(
			Component comp, 
			List<Alg> parserList, 
			DataDriverList dataDriverList,
			DataConfig defaultConfig) {
		super(UIUtil.getFrameForComponent(comp), "URI chooser", true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		
		thisDataDriverList = dataDriverList;
		internalConfig = new DataConfig();
		
		setLayout(new BorderLayout());
		
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
				result = null;
				internalConfig = null;
				dispose();
			}
		});
		footer.add(cancel);

		////////////////////// Body //////////////////////////
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		
		JPanel left = new JPanel(new BorderLayout());
		body.add(left, BorderLayout.WEST);
		
		left.add(new JLabel("Available units"), BorderLayout.NORTH);
		lbUnits = new UnitListBox();
		left.add(new JScrollPane(lbUnits), BorderLayout.CENTER);
		
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(0, 1));
		left.add(buttons, BorderLayout.EAST);
		
		JButton setAccount = new JButton("> ");
		setAccount.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				select();
			}
		});
		buttons.add(setAccount);


		JButton setAttributeMap = new JButton(">>");
		setAttributeMap.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				selectAll();
			}
		});
		buttons.add(setAttributeMap);

		
		JButton setConfig = new JButton("< ");
		setConfig.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				unselect();
			}
		});
		buttons.add(setConfig);
		
		JButton setContext = new JButton("<<");
		setContext.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				unselectAll();
			}
		});
		buttons.add(setContext);


		JPanel right = new JPanel(new BorderLayout());
		body.add(right, BorderLayout.CENTER);
		
		right.add(new JLabel("Selected units"), BorderLayout.NORTH);
		lbSelectedUnits = new UnitListBox();
		right.add(new JScrollPane(lbSelectedUnits), BorderLayout.CENTER);

		
		////////////////////// Header //////////////////////////
		JPanel pane = null;
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		JPanel attNames = new JPanel(new GridLayout(0, 1));
		header.add(attNames, BorderLayout.WEST);
		attNames.add(new JLabel("Host:"));
		attNames.add(new JLabel("Port:"));
		attNames.add(new JLabel("Path:"));
		attNames.add(new JLabel("Username:"));
		attNames.add(new JLabel("Password:"));

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
				
				String host = uri.getHost();
				if (host != null && !host.isEmpty())
					txtHost.setText(host);
				
				int port = uri.getPort();
				if (port > 0)
					txtPort.setValue(port);
				
				String username = uri.getUserName();
				if (username != null && !username.isEmpty())
					txtUsername.setText(username);
				
				String password = uri.getPassword();
				if (password != null && !password.isEmpty())
					txtPassword.setText(password);

				String path = uri.getPath();
				if (path != null && !path.isEmpty())
					txtPath.setText(path);
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

		
		JPanel tools = new JPanel(new GridLayout(1, 0));
		header.add(tools, BorderLayout.NORTH);
		
		JPanel parsers = new JPanel();
		tools.add(parsers);
		
		parsers.add(new JLabel("Parsers"));
		
		cmbParsers = new DatasetParserComboBox(parserList);
		cmbParsers.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				parserChanged();
			}
		});
	
		parsers.add(cmbParsers);
		
		JPanel dataDrivers = new JPanel();
		tools.add(dataDrivers);
		
		dataDrivers.add(new JLabel("Drivers"));
		
		cmbDataDrivers = new DataDriverComboBox(dataDriverList, (DatasetParser)cmbParsers.getSelectedItem());
		cmbDataDrivers.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if (e.getStateChange() == ItemEvent.SELECTED)
					dataDriverChanged();
			}
		});
		dataDrivers.add(cmbDataDrivers);
		
		dataDriverChanged();
		updateDefaultConfig(defaultConfig);
		
		setVisible(true);
	}
	
	
	/**
	 * Getting this {@link UriChooser}.
	 * @return this {@link UriChooser}.
	 */
	private UriChooser getThis() {
		return this;
	}
	
	
	/**
	 * Update this {@link UriChooser} by specified default configuration.
	 * @param defaultCfg specified default configuration.
	 */
	private void updateDefaultConfig(DataConfig defaultCfg) {
		if (defaultCfg == null)
			return;
		xURI store = defaultCfg.getStoreUri();
		
		DataDriver dataDriver = null;
		try {
			String dataDriverName = defaultCfg.getDataDriverName();
			if (dataDriverName != null)
				dataDriver = DataDriver.createByName(dataDriverName);
			else if (store != null)
				dataDriver = DataDriver.create(store);
		}
		catch (Exception e) {
			e.printStackTrace();
			dataDriver = null;
		}
		
		cmbParsers.setDefaultSelected(defaultCfg.getParser());
		if (dataDriver != null)
			cmbDataDrivers.selectDataDriver(dataDriver.getType());
		dataDriver = (DataDriver)cmbDataDrivers.getSelectedItem();
		
		if (store == null || dataDriver == null)
			return;
		
		String host = store.getHost();
		if (host == null && dataDriver.getType() != DataType.file)
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
	 * This is event-driven method responding to the action that user changes the dataset parser in {@link #cmbParsers}.
	 * Only {@link #cmbDataDrivers} is updated when dataset parser is changed.
	 */
	private void parserChanged() {
		cmbDataDrivers.update(
			thisDataDriverList, 
			(DatasetParser)cmbParsers.getSelectedItem());
	}
	
	
	/**
	 * This is event-driven method responding to the action that user changes the data driver in {@link #cmbDataDrivers}.
	 * This method updates controls (components) of this {@link UriChooser} when current data driver is changed.
	 */
	private void dataDriverChanged() {
		DataDriver driver = (DataDriver)cmbDataDrivers.getSelectedItem();
		if (driver == null)
			return;
		
		setConnectInfoControlVisible(true);
		txtPort.setValue(driver.getDefaultPort());
		txtHost.setText("localhost");
		
		if (driver.getType() == DataType.file) {
			txtHost.setText("");
			txtHost.setVisible(false);
			
			txtPort.setValue(-1);
			txtPort.setVisible(false);
			
			txtUsername.setText("");
			txtUsername.setVisible(false);
			
			txtPassword.setText("");
			txtPassword.setVisible(false);
		}
		else if (!driver.isFlatServer()) {
			btnPath.setVisible(false);
		}
		
	}
	
	
	/**
	 * Setting the visibility of controls (components) for showing connection information such as host, port, remote path of storage, user name, password according to the specified visible flag.
	 * @param aFlag specified visible flag. If {@code true}, such information controls are visible. Otherwise they are invisible.
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
	 * Getting the result as a list of URI (s) of chosen units.
	 * @return result as a list of URI (s) of chosen units.
	 */
	public List<xURI> getResult() {
		return result;
	}
	
	
	/**
	 * This is event-driven method responding to the action that user selects a set of units.
	 * Only the control {@link #lbSelectedUnits} to show selected units is updated.
	 */
	private void select() {
		UnitList list = lbUnits.getSelectedList();
		if (list.size() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Algorithm not selected or empty list", 
					"Algorithm not selected or empty list", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		lbSelectedUnits.addAll(list);
	}
	

	/**
	 * This is event-driven method responding to the action that user selects all units of remote storage.
	 * Only the control {@link #lbSelectedUnits} to show selected units is updated.
	 */
	private void selectAll() {
		UnitList list = lbUnits.getUnitList();
		if (list.size() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"List empty", 
					"List empty", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		lbSelectedUnits.addAll(list);
	}

	
	/**
	 * This is event-driven method responding to the action that user unselects a set of units.
	 * Only the control {@link #lbSelectedUnits} to show selected units is updated.
	 */
	private void unselect() {
		UnitList list = lbSelectedUnits.removeSelectedList();
		if (list.size() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Algorithm not selected or empty list", 
					"Algorithm not selected or empty list", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
	}
	

	/**
	 * This is event-driven method responding to the action that user unselects all units of remote storage.
	 * Only the control {@link #lbSelectedUnits} to show selected units is updated.
	 */
	private void unselectAll() {
		UnitList list = lbSelectedUnits.getUnitList();
		if (list.size() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"List empty", 
					"List empty", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		 lbSelectedUnits.clear();
	}
	
	
	/**
	 * Getting the selected data driver in {@link #cmbDataDrivers}.
	 * @return selected {@link DataDriver}.
	 */
	private DataDriver getDataDriver() {
		return (DataDriver)cmbDataDrivers.getSelectedItem();
	}
	
	
	/**
	 * Getting the selected dataset parser in {@link #cmbParsers}.
	 * @return selected {@link DatasetParser}.
	 */
	private DatasetParser getParser() {
		return (DatasetParser) cmbParsers.getSelectedAlg();
	}
	
	
	/**
	 * This is event-driven method responding to the action that user presses on &quot;OK&quot; button.
	 * The main task of this method is to set up the result as a list of URI (s) of chosen units.
	 * Finally, this method closes this {@link UriChooser}.
	 */
	private void onOk() {
		if (internalConfig == null || internalConfig.size() == 0 || internalConfig.getStoreUri() == null) {
			JOptionPane.showMessageDialog(
					this, 
					"Not connect yet", 
					"Not connect yet", 
					JOptionPane.WARNING_MESSAGE);
			
			return;
		}
		
		UnitList selectedUnitList = lbSelectedUnits.getUnitList();
		xURI storeUri = internalConfig.getStoreUri(); 
		result = Util.newList();
		if (selectedUnitList.size() > 0) {
			for (int i = 0; i < selectedUnitList.size(); i++) {
				Unit unit = selectedUnitList.get(i);
				xURI uri = storeUri.concat(unit.getName());
				
				result.add(uri);
			}
		}
		else
			result.add(storeUri);
			
		internalConfig = null;
		dispose(); 
	}

	
	/**
	 * This is event-driven method responding to the action that user presses on &quot;Connect&quot; button.
	 * This method mainly connects with the remote storage from filled information to retrieve units from such remote storage.
	 * The internal configuration is also updated.
	 */
	@SuppressWarnings("deprecation")
	private void connect() {
		internalConfig.clear();
		lbSelectedUnits.clear();
		
		txtPath.setText(normalizePath(txtPath.getText()));
		DataDriver dataDriver = getDataDriver();
		String host = txtHost.getText().trim();
		host = host.isEmpty() ? null : host;
		int port = txtPort.getValue() instanceof Number ? ( (Number) txtPort.getValue()).intValue() : -1; 
		xURI uri = dataDriver.getUri(host, port, txtPath.getText());
		
		DataConfig config = new DataConfig();
		config.setStoreAccount(txtUsername.getText().trim());
		config.setStorePassword( new HiddenText(txtPassword.getText()) );
		config.setStoreUri(uri);
		
		boolean connect = lbUnits.connectUpdate(config);
		UnitList unitList = lbUnits.getUnitList();
		unitList.setExtra(true);
		lbUnits.update(unitList);
		
		lbUnits.validate();
		lbUnits.updateUI();
		if (!connect) {
			internalConfig.clear();
			JOptionPane.showMessageDialog(
					this, "Connect fail", "Connect fail", JOptionPane.ERROR_MESSAGE);
			return;
		}
		else
			JOptionPane.showMessageDialog(
					this, "Connect successfully", "Connect successfully", JOptionPane.INFORMATION_MESSAGE);
		
		if (dataDriver.isFlatServer()) {
			UriAdapter adapter = new UriAdapter(uri);
			if (!adapter.isStore(uri))
				internalConfig.putAll(adapter.makeFlatDataConfig(uri));
			else
				internalConfig.putDefaultUnitList(uri);
			
			adapter.close();
		}
		else if (dataDriver.isDbServer())
			internalConfig.putDefaultUnitList(uri);
		else if (dataDriver.isRecommendServer()) {
			txtPath.setText("");
			uri = dataDriver.getUri(host, port, txtPath.getText());
			internalConfig.putDefaultUnitList(uri);
		}
		
		if (dataDriver.getType() != DataType.file) {
			internalConfig.setStoreAccount(txtUsername.getText().trim());
			internalConfig.setStorePassword( new HiddenText(txtPassword.getText()) );
		}
		
		internalConfig.setParser(getParser());
		internalConfig.setDataDriverName(getDataDriver().getName());
	}
	
	
	/**
	 * This utility method normalize path by replacing backward slashes by forward slashes.
	 * @param path original path which can has backward slashes.
	 * @return normalized path in which all backward slashes are replaced by forward slashes.
	 */
	private String normalizePath(String path) {
		return path.replaceAll("\\\\", "/");
	}
	
	
	/**
	 * This static method shows the {@link UriChooser} that allows user to select URI (s) of remote units.
	 * The method uses the specified default configuration to initialize the {@link UriChooser}.
	 * 
	 * @param parent parent component.
	 * @param defaultConfig default configuration to initialize the {@link UriChooser}.
	 * @return result as a list of URI (s) of chosen units.
	 */
	public static List<xURI> chooseUri(
			Component parent, 
			DataConfig defaultConfig) {
		
		RegisterTable regTable = (RegisterTable) PluginStorage.getParserReg().clone();
		List<Alg> indicators = regTable.getAlgList(new RegisterTable.AlgFilter() {

			@Override
			public boolean accept(Alg alg) {
				// TODO Auto-generated method stub
				return alg instanceof Indicator;
			}
			
		});
		
		if (defaultConfig != null) {
			UriAdapter adapter = new UriAdapter(defaultConfig);
			if (adapter != null)
				defaultConfig.setParser(new FlatServerIndicator());
			adapter.close();
		}
		
		UriChooser configurator = new UriChooser(
				parent, 
				indicators, 
				DataDriverList.list(),
				defaultConfig != null ? defaultConfig : new DataConfig());
		
		return configurator.getResult();
	}
	
	
	/**
	 * This static method shows the {@link UriChooser} that allows user to select URI (s) of remote units.
	 * The method uses the specified default URI of default configuration to initialize the {@link UriChooser}.
	 * 
	 * @param parent parent component.
	 * @param defaultUri default URI of default configuration.
	 * @return result as a list of URI (s) of chosen units.
	 */
	public static List<xURI> chooseUri(
			Component parent, 
			xURI defaultUri) {
		
		DataConfig defaultConfig = defaultUri != null ? DataConfig.create(defaultUri) : null;
		return chooseUri(parent, defaultConfig);
	}

	
}
