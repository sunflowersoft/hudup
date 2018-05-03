package net.hudup.core.logistic;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation indicates that the attached class represents a next-update algorithm.
 * Note, next-update algorithm is the one which needs to be updated in next version of Hudup framework because it is not perfect, for example.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Documented
@Target ( {ElementType.TYPE, ElementType.METHOD} )
@Retention(RetentionPolicy.RUNTIME)
public @interface NextUpdate {
	
	
	/**
	 * Defining the note for this annotation.
	 * @return note for this annotation.
	 */
	String note() default "No note";
	
	
}
