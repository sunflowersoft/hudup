package net.hudup.core.logistic;


/**
 * This interface is used to select an object identified with a URI according to some custom criteria.
 * The main method of this interface is {@link #accept(xURI)} which makes a decision of object selection.
 * As a convention, any class implementing this interface is called <i>URI filter</i>
 * 
 * @author Loc Nguyen
 * @version 11.0
 *
 */
public interface UriFilter {

	
	/**
	 * Making a decision that whether or not the object identified with the specified URI is selected.
	 * Any <i>URI filter</i> must define this method.
	 * @param uri Specified URI
	 * @return Whether or not the object identified with the specified URI is selected
	 */
	boolean accept(xURI uri);
	
	
	/**
	 * Retrieving description of URI filter. Implicitly, this method names the URI filter that implements this interface {@link UriFilter}.
	 * @return Description of filter
	 */
	String getDescription();
	
	
}
