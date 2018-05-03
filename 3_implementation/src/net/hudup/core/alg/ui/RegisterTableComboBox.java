package net.hudup.core.alg.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

import net.hudup.core.PluginStorage;
import net.hudup.core.RegisterTable;
import net.hudup.core.RegisterTableList;
import net.hudup.core.RegisterTableList.RegisterTableItem;
import net.hudup.core.alg.Alg;


/**
 * This graphic user interface (GUI) component shows a combo box for choosing a register table item represented by {@link RegisterTableItem}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class RegisterTableComboBox extends JComboBox<RegisterTableItem> {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
    /**
     * Default constructor.
     */
	public RegisterTableComboBox() {
		super();
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if (!getThis().isEnabled())
					return;
				
				if(SwingUtilities.isRightMouseButton(e) ) {
				}
				else if (e.getClickCount() >= 2) {
				}
			}
			
		});
		
	}
	
	
	/**
	 * Getting this component.
	 * @return this component.
	 */
	private RegisterTableComboBox getThis() {
		return this;
	}

	
	/**
     * Constructor with list of register table items.
     * @param rtList specified list of register table items, as a {@link RegisterTableList}.
     */
    public RegisterTableComboBox(RegisterTableList rtList) {
    	this();
    	
    	update(rtList);
    }
    

    /**
     * Constructor with list of register table items.
     * @param list specified {@link List} of register table items.
     */
    public RegisterTableComboBox(List<RegisterTableItem> list) {
    	this();
    	
    	update(list);
    }
	
    
    /**
     * Updating this component with list of register table items.
     * @param rtList specified list of register table items, as a {@link RegisterTableList}.
     */
	public void update(RegisterTableList rtList) {
		removeAllItems();
		if (rtList == null)
			return;
		
		update(rtList.toList());
	}
    

	/**
     * Updating this component with list of register table items.
	 * @param list specified {@link List} of register table items.
	 */
	public void update(List<RegisterTableItem> list) {
		removeAllItems();
		if (list == null)
			return;
		
		Collections.sort(list);
		
		for (RegisterTableItem item : list) {
			addItem(item);
		}
		
		setDefaultSelected();
	}


    /**
     * Setting which register table item is selected as default.
     */
	protected void setDefaultSelected() {
		RegisterTable algReg = PluginStorage.getRecommenderReg();
		setDefaultSelected(algReg);
    }

	
	/**
	 * Setting which register table item is selected as default, based on specified object.
	 * The class of specified object guides how to select which register table item.
	 * 
	 * @param obj specified object.
	 */
	@SuppressWarnings("unchecked")
	public void setDefaultSelected(Object obj) {
		if (obj == null)
			return;
		
		if( (obj instanceof Class<?>) && (Alg.class.isAssignableFrom( (Class<?>) obj )) )
			setDefaultSelected((Class<? extends Alg>) obj);
		else if (obj instanceof Alg)
			setDefaultSelected(obj.getClass());
		else if (obj instanceof RegisterTableItem)
			setDefaultSelected( ((RegisterTableItem)obj).getName() );
		else if (obj instanceof RegisterTable)
			setDefaultSelected((RegisterTable)obj);
		else
			setDefaultSelected(obj.toString());
	}

	
	/**
	 * Setting which register table item is selected as default, based on specified class.
     * @param algClass specified class of algorithm.
     */
	public void setDefaultSelected(Class<? extends Alg> algClass) {
		int idx = findItem(algClass);
		if (idx != -1)
			setSelectedIndex(idx);
	}
	

	/**
	 * Setting which register table item is selected as default, based on name of item.
	 * @param name specified name of item.
	 */
	public void setDefaultSelected(String name) {
		int idx = findItem(name);
		if (idx != -1)
			setSelectedIndex(idx);
	}

	
	/**
	 * Setting the specified register table as the selected item by default.
	 * @param registerTable specified register table.
	 */
	public void setDefaultSelected(RegisterTable registerTable) {
		int idx = findItem(registerTable);
		if (idx != -1)
			setSelectedIndex(idx);
	}

	
	/**
	 * Finding register table item by name.
	 * @param name specified name.
	 * @return item index by name.
	 */
	@SuppressWarnings("unchecked")
	public int findItem(String name) {
		if (name == null)
			return -1;
		
		for (int i = 0; i < getItemCount(); i++) {
			RegisterTableItem item = getItemAt(i);
			if (item.getName().equals(name))
				return i;
		}
		
		try {
			Class<?> c = Class.forName(name);
			if (Alg.class.isAssignableFrom(c))
				return findItem((Class<? extends Alg>)c);
		}
		catch (Throwable e) {
		}
		
		return -1;
	}
	
	
	/**
	 * Finding register table item by specified class.
	 * @param algClass specified class of algorithm.
	 * @return item index by class.
	 */
	public int findItem(Class<? extends Alg> algClass) {
		for (int i = 0; i < getItemCount(); i++) {
			RegisterTableItem item = getItemAt(i);
			
			if (item.getRegisterTable().equals(PluginStorage.lookupTable(algClass)))
				return i;
		}
		
		return -1;
	}


	/**
	 * Finding register table item by specified register table.
	 * @param registerTable specified register table.
	 * @return item index by register table.
	 */
	public int findItem(RegisterTable registerTable) {
		if (registerTable == null)
			return -1;
		
		for (int i = 0; i < getItemCount(); i++) {
			RegisterTableItem item = getItemAt(i);
			
			if (item.getRegisterTable().equals(registerTable))
				return i;
		}
		
		return -1;
	}


}
