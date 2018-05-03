/**
 * 
 */
package net.hudup.core.data;

import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.data.DataDriver.DataType;


/**
 * This class contains a list of {@link DataDriver} and provides utility method on processing such list such as adding new data driver and finding a data driver.
 * Note, {@link DataDriver} is the final class representing specific data types such as system file or database.
 * Each data driver has a data type and is always associated a driver class. For example, MySQL database is associated with the driver class &quot;com.mysql.jdbc.Driver&quot;.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public final class DataDriverList {

	
	/**
	 * The system default driver list. This is private and static variable used as global data driver list.
	 */
	private static DataDriverList dataDriverList = null;
			
			
	/**
	 * {@link List} of {@link DataDriver} (s).
	 */
	private List<DataDriver> dataDrivers = Util.newList();
	
	
	/**
	 * Getting the size of this driver list.
	 * @return size of this driver list.
	 */
	public int size() {
		return dataDrivers.size();
	}
	
	
	/**
	 * Getting the data driver at specified index.
	 * @param index specified index.
	 * @return {@link DataDriver} at specified index.
	 */
	public DataDriver get(int index) {
		return dataDrivers.get(index);
	}
	
	
	/**
	 * Finding the data driver having the specified class name.
	 * For example, the data driver for Derby engine database has class name &quot;org.apache.derby.jdbc.EmbeddedDriver&quot;.
	 * @param innerClassName specified class name.
	 * @return index of driver in list. The method returns -1 if no driver is found.
	 */
	public int findDriverByInnerClass(String innerClassName) {
		if (innerClassName == null)
			return -1;
		
		for (int i = 0; i < dataDrivers.size(); i++) {
			if (dataDrivers.get(i).getInnerClassName().equals(innerClassName))
				return i;
		}
		
		return -1;
	}
	
	
	/**
	 * Finding the data driver having the specified name.
	 * For example, the data driver for Derby engine database has class name &quot;derby&quot;.
	 * @param driverName specified name.
	 * @return index of driver in list. The method returns -1 if no driver is found.
	 */
	public int findDriverByName(String driverName) {
		if (driverName == null)
			return -1;
		
		for (int i = 0; i < dataDrivers.size(); i++) {
			if (dataDrivers.get(i).getName().equals(driverName))
				return i;
		}
		
		return -1;
	}

	
	/**
	 * Finding the data driver having the specified type.
	 * For example, the data driver for Derby engine database has {@link DataType#derby_engine}.
	 * @param driverType specified {@link DataType}.
	 * @return index of driver in list having the specified type.
	 */
	public int findDriver(DataType driverType) {
		for (int i = 0; i < dataDrivers.size(); i++) {
			if (dataDrivers.get(i).getType() ==  driverType)
				return i;
		}
		
		return -1;
	}
	
	
	/**
	 * Adding a specified data driver into this list.
	 * @param driver specified data driver.
	 */
	public void add(DataDriver driver) {
		if (driver.isValid() && findDriverByInnerClass(driver.getInnerClassName()) == -1)
			dataDrivers.add(driver);
	}
	
	
	/**
	 * Clearing this list, which means that all data drivers are removed from this list.
	 */
	public void clear() {
		dataDrivers.clear();
	}

	
	/**
	 * Loading default data driver list including data drivers such as
	 * Derby, Derby engine, file system, FTP file system, HTTP file system,
	 * Hudup RMI, Hudup socket, Microsoft SQL Sever database, MySQL database,
	 * Oracle database, PostgreSQL database.
	 */
	public void loadDefault() {
		clear();
		
		try {
			add(new DataDriver(DataType.derby));
			add(new DataDriver(DataType.derby_engine));
			add(new DataDriver(DataType.file));
			add(new DataDriver(DataType.ftp));
			add(new DataDriver(DataType.http));
			add(new DataDriver(DataType.hudup_rmi));
			add(new DataDriver(DataType.hudup_socket));
			add(new DataDriver(DataType.mssql));
			add(new DataDriver(DataType.mysql));
			add(new DataDriver(DataType.oracle));
			add(new DataDriver(DataType.postgresql));
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Retrieving global data driver list.
	 * @return global data driver list {@link #dataDriverList}.
	 */
	public static DataDriverList list() {
		if (dataDriverList == null) {
			dataDriverList = new DataDriverList();
			dataDriverList.loadDefault();
		}
		
		return dataDriverList;
	}
	
	
}
