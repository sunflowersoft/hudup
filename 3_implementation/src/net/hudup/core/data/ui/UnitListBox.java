/**
 * 
 */
package net.hudup.core.data.ui;

import java.awt.Container;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListModel;

import net.hudup.core.client.Service;
import net.hudup.core.client.SocketConnection;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.DataDriver.DataType;
import net.hudup.core.data.HiddenText;
import net.hudup.core.data.Unit;
import net.hudup.core.data.UnitList;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;

/**
 * The graphic user interface (GUI) component as list box {@link JList} shows the {@link UnitList}.
 * Note, {@link UnitList} represents a list of many units; as a convention, it is called unit list.
 * Recall objects in framework such as {@code profile}, {@code item profile}, {@code user profile}, {@code rating matrix}, {@code interchanged attribute map} are stored in archives (files) of entire framework.
 * Each archive (file) is called {@code unit} representing a CSV file, database table, Excel sheet, etc. Unit is represented by {@link Unit} class.
 * Your attention please, the list of default units is returned by the static method {@link DataConfig#getDefaultUnitList()}.
 * In fact, important units such as rating unit, user profile, item profile, context are registered with {@link DataConfig} class as default units.
 * <br><br>
 * This {@link UnitListBox} firstly connects with the store (directory or database) specified in the internal configuration {@link #config} so as to retrieve list of units (retrieved {@link UnitList}) from such store.
 * Later on, some units of the retrieved {@link UnitList} which are not in the default list returned by {@link DataConfig#getDefaultUnitList()} are marked as extra (auxiliary) units.
 * Finally, this {@link UnitListBox} shows the retrieved {@link UnitList}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class UnitListBox extends JList<Unit> {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * The configuration contains the store (directory or database) with which this {@link UnitListBox} connects so as to retrieve list of units (retrieved {@link UnitList}) from such store.
	 */
	protected DataConfig config = null;
	
	
	/**
	 * Default constructor.
	 */
	public UnitListBox() {
		
	}
	
	
	/**
	 * This method firstly connects with the store (directory or database) specified in the specified configuration so as to retrieve list of units (retrieved {@link UnitList}) from such store.
	 * Later on, some units of the retrieved {@link UnitList} which are not in the default list returned by {@link DataConfig#getDefaultUnitList()} are marked as extra (auxiliary) units.
	 * Finally, retrieved {@link UnitList} are shown in this {@link UnitListBox}.
	 * @param config specified configuration contains the store (directory or database) with which this {@link UnitListBox} connects so as to retrieve list of units (retrieved {@link UnitList}) from such store.
	 * @return whether connect successfully.
	 */
	public boolean connectUpdate(DataConfig config) {
		this.config = config;
		
		UnitList unitList = new UnitList();
		boolean connect = connectUnitList(config, unitList);
		
		update(unitList);
		return connect;
	}
	
	
	/**
	 * Update this {@link UnitListBox} by specified {@link UnitList}.
	 * @param unitList specified {@link UnitList}.
	 */
	public void update(UnitList unitList) {
		setListData(unitList.toArray());
		
		Container container = getParent();
		if (container instanceof JPanel)
			((JPanel)container).updateUI();
	}
	
	
	/**
	 * Get the list of units from this {@link UnitListBox}.
	 * @return {@link UnitList} of this {@link UnitListBox}.
	 */
	public UnitList getUnitList() {
		UnitList unitList = new UnitList();
		
		ListModel<Unit> model = getModel();
		for (int i = 0; i < model.getSize(); i++) {
			unitList.add(model.getElementAt(i));
		}
	
		return unitList;
	}
	
	
	/**
	 * Getting list of selected units.
	 * @return selected unit list.
	 */
	public UnitList getSelectedList() {
		List<Unit> list = getSelectedValuesList();
		UnitList unitList = new UnitList();
		for (Object object : list) {
			unitList.add((Unit)object);
		}
		return unitList;
	}

	
	/**
	 * Adding a specified unit into this {@link UnitListBox}.
	 * @param unit specified unit.
	 */
	public void add(Unit unit) {
		UnitList unitList = getUnitList();
		unitList.add(unit);
		update(unitList);
	}
	
	
	/**
	 * Adding a specified collection of units into this list box.
	 * @param units specified collection of units.
	 */
	public void addAll(Collection<Unit> units) {
		UnitList unitList = getUnitList();
		unitList.addAll(units);
		update(unitList);
	}

	
	/**
	 * Adding a specified list of units into this {@link UnitListBox}.
	 * @param list specified list of units as {@link UnitList}.
	 */
	public void addAll(UnitList list) {
		UnitList unitList = getUnitList();
		unitList.addAll(list);
		update(unitList);
	}

	
	/**
	 * Removing a unit from this {@link UnitListBox}.
	 * @param unit specified unit which is removed from this {@link UnitListBox}. 
	 */
	public void remove(Unit unit) {
		UnitList unitList = getUnitList();
		unitList.remove(unit.getName());
		update(unitList);
	}
	
	
	/**
	 * Removing units in the specified collection from this {@link UnitListBox}.
	 * @param units specified collection of units which are removed from this {@link UnitListBox}.
	 */
	public void removeAll(Collection<Unit> units) {
		UnitList unitList = getUnitList();
		unitList.removeAll(units);
		update(unitList);
	}

	
	/**
	 * Removing units in the specified {@link UnitList} from this {@link UnitListBox}.
	 * @param list specified {@link UnitList} whose units are removed from this {@link UnitListBox}.
	 */
	public void removeAll(UnitList list) {
		UnitList unitList = getUnitList();
		unitList.removeAll(list);
		
		update(unitList);
	}

	
	/**
	 * Removing selected units.
	 * @return list of removed units.
	 */
	public UnitList removeSelectedList() {
		UnitList selected = getSelectedList();
		removeAll(selected);
		return selected;
	}

	
	/**
	 * Clearing this list box, which means that making this list box empty.
	 */
	public void clear() {
		update(new UnitList());
	}
	
	
	/**
	 * This method firstly connects with the store (directory or database) specified in the specified configuration so as to retrieve list of units (retrieved {@link UnitList}) from such store.
	 * Later on, some units of the retrieved {@link UnitList} which are not in the default list returned by {@link DataConfig#getDefaultUnitList()} are marked as extra (auxiliary) units.
	 * Finally, retrieved {@link UnitList} are put in the output parameter {@code unitList}.
	 * @param config specified configuration contains the store (directory or database) with which this {@link UnitListBox} connects so as to retrieve list of units (retrieved {@link UnitList}) from such store.
	 * @param unitList this is output parameter that receives retrieved units.
	 * @return whether connected.
	 */
	private static boolean connectUnitList(DataConfig config, UnitList unitList) {
		unitList.clear();
		xURI uri = config.getStoreUri();
		
		boolean result = false;
		
		DataDriver driver = DataDriver.create(config.getStoreUri());
		if (driver.isFlatServer()) {
			UriAdapter adapter = new UriAdapter(config);
			xURI store = adapter.isStore(uri) ? uri : adapter.getStoreOf(uri);
			
			List<xURI> uriList = adapter.getUriList(store, null);
			
			for (xURI u : uriList) {
				String lastname = u.getLastName();
				if (lastname != null && !lastname.isEmpty()) {
					Unit unit = new Unit(lastname);
					unitList.add(unit);
				}
			}
			
			adapter.close();
			result = true;
		}
		else if (driver.isDbServer()) {
			
			Connection conn = null;
			ResultSet rs = null;
			try {
				conn = createConnection(config);
				
				DatabaseMetaData metadata = conn.getMetaData();
				rs = metadata.getTables(null, null, null, new String[] {"TABLE"});
				
				while (rs.next()) {
					Unit unit = new Unit(rs.getString("TABLE_NAME"));
					unitList.add(unit);
				}
				
				result = true;
			}
			catch (Exception e) {
				e.printStackTrace();
				result = false;
			}
			finally {
				try {
					if (rs != null)
						rs.close();
				} 
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					if (conn != null)
						conn.close();
				} 
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
		else if (driver.isRecommendServer()) {
			
			if (driver.getType() == DataType.hudup_rmi) {
				Service service = net.hudup.core.client.DriverManager.getRemoteService(
						uri.getHost(),
						uri.getPort(),
						config.getStoreAccount(), 
						config.getStorePassword().getText());
				
				result = (service != null);
			}
			else if (driver.getType() == DataType.hudup_socket) {
				SocketConnection connection = net.hudup.core.client.DriverManager.getSocketConnection(
						uri,
						config.getStoreAccount(), 
						config.getStorePassword().getText());
				
				result = (connection != null && connection.isConnected());
				if (connection != null)
					connection.close();
			}
				
		}
		
		
		UnitList defaultUnitList = DataConfig.getDefaultUnitList();
		for (int i = 0; i < unitList.size(); i++) {
			Unit unit = unitList.get(i);
			if (!defaultUnitList.contains(unit))
				unit.setExtra(true);
		}
		
		return result;
	}
	
	
	/**
	 * Creating database connection with given configuration.
	 * Connection information such as connection URI of store, user name, and password is stored in such configuration.
	 *  
	 * @param config specified connection.
	 * @return database connection.
	 */
	public static Connection createConnection(DataConfig config) {
		String username = config.getStoreAccount();
		HiddenText password = config.getStorePassword();
		
		try {
			
			String url = config.getStoreUri().toString();
			if (username == null || password == null)
				return DriverManager.getConnection(url);
			else
				return DriverManager.getConnection(url, username, password.getText());
			
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
}
