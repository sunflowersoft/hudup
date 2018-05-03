package net.hudup.core;

/**
 * This interface indicates that any {@code Transfer} class which implements it can transfer all built-in variables to other {@code Transfer} class via the method {@link #transfer()} without deep copying (deep cloning).
 * It means that built-in variables are transferred by reference assignments.
 * For example, class {@code A} that implements this interface has following code:<br>
 * <code>
 * public class A implements Transfer {<br>
 *  Object x, y;<br>
 *  <br>
 *  public A(Object x, Object y) {<br>
 *   this.x = x; //reference assignment<br>
 *   this.y = y; //reference assignment<br>
 * }<br>
 * <br>
 * public Object transfer() {<br>
 *   return new A(this.x, this.y); //There is no deep copying, just only reference assignments<br>
 * }
 * </code> 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface Transfer {

	
	/**
	 * This method will transfer all built-in variables of this {@code Transfer} class to other {@code Transfer} class without deep copying (deep cloning).
	 * It means that built-in variables are transferred by reference assignments.
	 * @return Transferred object that received all references to built-in variables of this {@code Transfer} class.
	 */
	Object transfer();
	
	
}
