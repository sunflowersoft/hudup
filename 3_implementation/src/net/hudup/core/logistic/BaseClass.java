package net.hudup.core.logistic;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation indicates that the attached class is base class. Such base class needs to be extended by sub-classes.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Documented
@Target ( {ElementType.TYPE, ElementType.METHOD} )
@Retention(RetentionPolicy.RUNTIME)
public @interface BaseClass {

}
