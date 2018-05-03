package net.hudup.data.ui;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.hudup.core.Util;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ProfileTable extends JTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public ProfileTable(boolean user) {
		super(new ProfileTableModel(user));
		
		setAutoResizeMode(AUTO_RESIZE_OFF);
	}
	
	
	/**
	 * 
	 * @param dataset
	 */
	public void update(Dataset dataset) {
		getProfileTableModel().update(dataset);
	}
	
	
	/**
	 * 
	 * @return table model
	 */
	ProfileTableModel getProfileTableModel() {
		return (ProfileTableModel) getModel();
	}
	
	
	
}


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class ProfileTableModel extends DefaultTableModel {

	
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
	public ProfileTableModel(boolean user) {
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
				
				Profile profile = user ? dataset.getUserProfile(id) : dataset.getItemProfile(id);
				if (profile == null)
					continue;
				
				int n = profile.getAttCount();
				Vector<Object> row = Util.newVector();
				for (int i = 0; i < n; i++) {
					row.add(profile.getValue(i));
				}
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
		
		
		setDataVector(
				data, 
				toColumns(user ? dataset.getUserAttributes() : dataset.getItemAttributes()));
		
	}
	
	
	
	
	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return false;
	}


	
	/**
	 * 
	 * @param attributes
	 * @return column identifiers
	 */
	static Vector<String> toColumns(AttributeList attributes) {
		Vector<String> columns = Util.newVector();
		
		for (int i = 0; i < attributes.size(); i++) {
			Attribute attribute = attributes.get(i);
			columns.add(attribute.getName());
		}
		
		return columns;
	}
	
	
	
}



