package net.hudup.data.ui;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.hudup.core.Util;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.ExternalRecord;
import net.hudup.core.data.Fetcher;

/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ExternalRecordTable extends JTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public ExternalRecordTable(boolean user) {
		super(new ExternalRecordTableModel(user));
	}
	
	
	/**
	 * 
	 * @param dataset
	 */
	public void update(Dataset dataset) {
		getExternalRecordTableModel().update(dataset);
	}
	
	
	/**
	 * 
	 * @return table model
	 */
	ExternalRecordTableModel getExternalRecordTableModel() {
		return (ExternalRecordTableModel) getModel();
	}
	
	
	
}



/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class ExternalRecordTableModel extends DefaultTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	protected boolean user = false;
	
	
	/**
	 * 
	 */
	public ExternalRecordTableModel(boolean user) {
		super();
		
		this.user = user;
	}
	
	
	/**
	 * 
	 * @param dataset
	 */
	public void update(Dataset dataset) {
		Vector<Vector<Object>> data = Util.newVector();
		
		Fetcher<Integer> fetcher = user ? dataset.fetchUserIds() : dataset.fetchItemIds();
		try {
			while (fetcher.next()) {
				Integer id = fetcher.pick();
				if (id == null || id < 0)
					continue;
				
				ExternalRecord record = user ? dataset.getUserExternalRecord(id) : 
					dataset.getItemExternalRecord(id);
				if (record == null)
					continue;
				
				Vector<Object> row = Util.newVector();
				row.add(id);
				row.add(record.unit);
				row.add(record.attribute);
				row.add(record.value);
				data.add(row);
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				fetcher.close();
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		setDataVector(data, toColumn(user));
		
	}
	
	
	
	
	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return false;
	}


	/**
	 * 
	 * @param user
	 * @return column identifier
	 */
	static Vector<String> toColumn(boolean user) {
		Vector<String> columns = Util.newVector();
		columns.add(user ? "User id" : "Item id");
		columns.add("External unit");
		columns.add("External attribute");
		columns.add("External key");
		
		return columns;
	}
	
	
	
}



