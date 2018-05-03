package net.hudup.core;


/**
 * This interface extends the built-in {@link java.lang.Cloneable}, which indicates that any its object can be cloned.
 * Any class that implements this interface must define the method {@link #clone()}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface Cloneable extends java.lang.Cloneable {


	/**
	 * Any class that implements this interface must define this method to clone itself.
	 * @return Cloned object
	 */
	public Object clone();
}
