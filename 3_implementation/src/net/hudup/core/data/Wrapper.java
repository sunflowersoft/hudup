package net.hudup.core.data;


/**
 * This is the wrapper of any object. The variable {@link #object} is such internal object.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Wrapper {

	
	/**
	 * The internal object wrapped by this wrapper.
	 */
	private Object object = null;
	
	
	/**
	 * Constructor with specified object.
	 * @param object specified object.
	 */
	public Wrapper(Object object) {
		this.object = object;
	}
	
	
	/**
	 * Getting the internal object wrapped by this wrapper.
	 * @return internal object wrapped by this wrapper.
	 */
	public Object getObject() {
		return object;
	}
	
	
	/**
	 * Setting the internal object by specified object.
	 * @param object specified object that is wrapped by this wrapper.
	 */
	public void setObject(Object object) {
		this.object = object;
	}
	
	
}
