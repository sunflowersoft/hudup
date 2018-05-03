/**
 * 
 */
package net.hudup.core.data.ui;


import javax.swing.JComboBox;

import net.hudup.core.data.DataDriver;
import net.hudup.core.data.DataDriver.DataType;
import net.hudup.core.data.DataDriverList;
import net.hudup.core.parser.DatasetParser;

/**
 * This graphic user interface (GUI) component shows a combo box for users to select a data driver represented by {@link DataDriver} class.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class DataDriverComboBox extends JComboBox<DataDriver> {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with specified data driver list and dataset parser.
	 * The dataset parser is used to assert which data drivers are valid. Only valid data drivers are shown.
	 * @param dataDriverList specified data driver list.
	 * @param parser specified dataset parser.
	 */
	public DataDriverComboBox(DataDriverList dataDriverList, DatasetParser parser) {
		super();
		
		update(dataDriverList, parser);
		
		selectDataDriver(DataType.file);
	}
	
	
	/**
	 * Constructor with specified list of data drivers.
	 * @param dataDriverList specified list of data drivers.
	 */
	public DataDriverComboBox(DataDriverList dataDriverList) {
		super();
		
		update(dataDriverList);
		
		selectDataDriver(DataType.file);
	}

	
	/**
	 * Updating this combo box with specified data driver list and dataset parser.
	 * The dataset parser is used to assert which data drivers are valid. Only valid data drivers are shown.
	 * @param dataDriverList specified data driver list represented by {@link DataDriverList}
	 * @param parser specified dataset parser.
	 */
	public void update(DataDriverList dataDriverList, DatasetParser parser) {
		this.removeAllItems();
		
		for (int i = 0; i < dataDriverList.size(); i++) {
			DataDriver dataDriver = dataDriverList.get(i);
			if (parser == null || parser.support(dataDriver))
				this.addItem(dataDriver);
		}
		
	}
	
	
	/**
	 * Updating this combo box with specified data driver list.
	 * @param dataDriverList specified data driver list.
	 */
	public void update(DataDriverList dataDriverList) {
		this.removeAllItems();
		
		for (int i = 0; i < dataDriverList.size(); i++) {
			DataDriver dataDriver = dataDriverList.get(i);
			this.addItem(dataDriver);
		}
		
	}

	
	/**
	 * Getting the internal data driver list of this {@link DataDriverComboBox}.
	 * @return {@link DataDriverList} of this {@link DataDriverComboBox}.
	 */
	public DataDriverList getDataDriverList() {
		DataDriverList dataDriverList = new DataDriverList();
		
		int n = getItemCount();
		for (int i = 0; i < n; i++)
			dataDriverList.add(getItemAt(i));
		
		return dataDriverList;
	}
	
	
	/**
	 * Select an item of data driver given {@link DataType}.
	 * @param type given data type.
	 */
	public void selectDataDriver(DataType type) {
		DataDriverList dataDriverList = getDataDriverList();
		int index = dataDriverList.findDriver(type);
		if (index != -1)
			this.setSelectedIndex(index);
	}
	
	
	/**
	 * Select an item of data driver given driver name.
	 * Note each data driver always has a name. For example, the data driver for Derby engine database has name &quot;derby&quot;.
	 * @param driverName specified driver name.
	 */
	public void selectDataDriver(String driverName) {
		DataDriverList dataDriverList = getDataDriverList();
		int index = dataDriverList.findDriverByName(driverName);
		if (index != -1)
			this.setSelectedIndex(index);
	}


}
