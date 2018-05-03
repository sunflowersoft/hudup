package net.hudup.core.data.ctx;

import java.io.Serializable;
import java.util.List;

import net.hudup.core.Util;


/**
 * This class is the list of {@link ContextValue} (s). It contains an internal variable {@link #list} referred to this list.
 * It provides utility methods for processing such list.
 * Note, {@link ContextValue} represents a value of context template.
 * A context represented by {@link Context} is composed of context template represented by {@link ContextTemplate} and context value represented by {@link ContextValue}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ContextValueList implements Serializable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * The internal {@link List} of context values.
	 */
	protected List<ContextValue> list = Util.newList();
	
	
	/**
	 * Default constructor.
	 */
	public ContextValueList() {
		
	}
	
	
	/**
	 * Getting size of this list.
	 * @return size of this list.
	 */
	public int size() {
		return list.size();
	}
	
	
	/**
	 * Getting a context value at specified index.
	 * @param index specified index.
	 * @return {@link ContextValue} at specified index.
	 */
	public ContextValue get(int index) {
		return list.get(index);
				
	}
	
	
	/**
	 * Adding the specified context value into this list.
	 * @param value specified context value.
	 * @return whether add successfully.
	 */
	public boolean add(ContextValue value) {
		
		return list.add(value);
	}
	
	
	/**
	 * Removing the context value at specified index.
	 * @param index specified index.
	 */
	public void remove(int index) {
		list.remove(index);
	}
	
	
	/**
	 * Converting this list of context values into array of context values.
	 * @return array of context values.
	 */
	public ContextValue[] toArray() {
		return list.toArray(new ContextValue[] { });
	}


}
