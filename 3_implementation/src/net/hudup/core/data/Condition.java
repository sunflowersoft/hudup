package net.hudup.core.data;



/**
 * Condition is the special profile which is used in condition clause {@code where} in SQL statement for processing database, for example.
 * Note that SQL is formal programming language used for querying, updating, processing objects in database.
 * However condition can be used in any necessary situation.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Condition extends Profile {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public Condition() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with a reference to a attribute list specified {@link AttributeList} class.
	 * This means that many conditions can share the same attribute list.
	 * @param attRef Reference to attribute list.
	 */
	public Condition(AttributeList attRef) {
		super(attRef);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public Object clone() {
		Condition condition = new Condition();
		condition.attRef = this.attRef;
		
		condition.attValues.clear();
		condition.attValues.addAll(this.attValues);
		
		return condition;
	}
	
	
}
