package net.hudup.core.logistic;



/**
 * This interface is used for processing some task on specified URI. Please see its method {@link #uriProcess(xURI)}.
 * For example, this interface is the input parameter of the method {@link UriAssocAbstract#uriListProcess(UriAssoc, xURI, UriFilter, UriProcessor)} of the abstract URI associator {@link UriAssocAbstract}.
 * @author Loc Nguyen
 * @version 11.0
 *
 */
public interface UriProcessor {

	
	/**
	 * Processing some task on specified URI. Programmer is responsible for defining this method according to some purpose.
	 * @param uri Specified URI
	 * @throws Exception if any error raises.
	 */
	void uriProcess(xURI uri) throws Exception;
	
	
}
