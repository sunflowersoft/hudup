/**
 * 
 */
package net.hudup.core.data;



/**
 * This class represent profile of an item, called item profile, which inherits directly {@link Profile}.
 * Note, {@link Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class User extends Profile {
	
	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public User() {
		super();
	}
	
	
	
	@Override
	public Object clone() {
		User user = new User();
		user.attRef = this.attRef;
		
		user.attValues.clear();
		user.attValues.addAll(this.attValues);
		
		return user;
	}
	
	
}
