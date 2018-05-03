package net.hudup.core.alg.ui;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import net.hudup.core.PluginStorage;
import net.hudup.core.RegisterTable;
import net.hudup.core.alg.Alg;


/**
 * This graphic user interface (GUI) shows a component combo box for choosing and configuring algorithm.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class AlgComboBox extends JComboBox<Alg> implements AlgListUI {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
    /**
     * Default constructor.
     */
	public AlgComboBox() {
		super();
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if (!getThis().isEnabled())
					return;
				
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = AlgListUIUtil.createContextMenu(getThis());
					if(contextMenu != null) 
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
				else if (e.getClickCount() >= 2) {
					AlgListUIUtil.config(getThis());
				}
			}
			
		});
		
	}
	
	
    /**
     * Constructor with array of algorithms.
     * @param algList array of algorithms.
     */
    public AlgComboBox(Alg[] algList) {
    	this();
    	
    	update(algList);
    }
    

    /**
     * Constructor with list of algorithms.
     * @param algList list of algorithms.
     */
    public AlgComboBox(List<Alg> algList) {
    	this();
    	
    	update(algList);
    }
    
    
    /**
     * Setting which algorithm is selected as default.
     */
	protected void setDefaultSelected() {
		RegisterTable algReg = PluginStorage.getRecommenderReg();
		Alg defaultAlg = algReg.query("gfall");
		if (defaultAlg != null)
			setDefaultSelected(defaultAlg.getClass());
    }
    
    
    /**
	 * Setting which algorithm is selected as default, based on specified object.
	 * The class of specified object guides how to select which algorithm.
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
		else
			setDefaultSelected(obj.toString());
	}

	
	/**
	 * Setting which algorithm is selected as default, based on specified class.
     * @param algClass specified class of algorithm.
     */
	public void setDefaultSelected(Class<? extends Alg> algClass) {
		int idx = findItem(algClass);
		if (idx != -1)
			setSelectedIndex(idx);
	}
	

	/**
	 * Setting which algorithm is selected as default, based on name of algorithm.
	 * @param name of algorithm.
	 */
	public void setDefaultSelected(String name) {
		int idx = findItem(name);
		if (idx != -1)
			setSelectedIndex(idx);
	}

	
	/**
     * Update this {@link AlgComboBox} by specified list of algorithms.
     * @param algList specified list of algorithms.
     */
	public void update(List<Alg> algList) {
		removeAllItems();
		if (algList == null)
			return;
		
		Collections.sort(algList, new Comparator<Alg>() {

			@Override
			public int compare(Alg alg1, Alg alg2) {
				// TODO Auto-generated method stub
				return alg1.getName().compareTo(alg2.getName());
			}
		});
		
		for (Alg alg : algList) {
			addItem(alg);
		}
		
		setDefaultSelected();
	}
	
	
	/**
     * Update this {@link AlgComboBox} by specified array of algorithms.
	 * @param algList specified array of algorithms.
	 */
	public void update(Alg[] algList) {
		removeAllItems();
		if (algList == null)
			return;
		
		update(Arrays.asList(algList));
	}
	
	
	/**
	 * Removing specified algorithms from this {@link AlgComboBox}.
	 * @param removedAlgs collection of algorithms which are removed from this {@link AlgComboBox}.  
	 */
	public void remove(Collection<Alg> removedAlgs) {
		if (removedAlgs == null)
			return;
		
		for (Alg removedAlg : removedAlgs) {
			int index = findItem(removedAlg.getName());
			if (index != -1)
				this.removeItemAt(index);
		}
	}
	
	
	/**
	 * Finding algorithm by name.
	 * @param name specified name.
	 * @return item index by name.
	 */
	@SuppressWarnings("unchecked")
	public int findItem(String name) {
		if (name == null)
			return -1;
		
		for (int i = 0; i < getItemCount(); i++) {
			Alg a = getItemAt(i);
			if (a.getName().equals(name))
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
	 * Finding algorithm item by specified class.
	 * @param algClass specified class of algorithm.
	 * @return item index by class.
	 */
	public int findItem(Class<? extends Alg> algClass) {
		
		for (int i = 0; i < getItemCount(); i++) {
			Alg a = getItemAt(i);
			if (a.getClass().equals(algClass))
				return i;
		}
		
		return -1;
	}

	
	/**
	 * Returning this {@link AlgComboBox}.
	 * @return this {@link AlgComboBox}.
	 */
	private AlgComboBox getThis() {
		return this;
	}
	
	
	@Override
	public Alg getSelectedAlg() {
		return (Alg)getSelectedItem();
	}
	
	
}
