package net.hudup.core.data;


/**
 * This interface establishes the rule that all classes which implements {@link AutoCloseable} has a mechanism of auto-closing.
 * The main method of this interface is {@link #close()}. The class that implements this interface is called auto-closable class.
 * The common implementation of {@link #close()} method is to release resources occupied by auto-closable object.
 * The {@code finalize()} method of auto-closable class should call {@link #close()} method.
 * Concretely, if programmer forgets closing auto-closable object to release resources after using such object,
 * the {@link #close()} is automatically called by the {@code finalize()} to close release resources before the Java garbage collector destroys such object.
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface AutoCloseable {
	
	/**
	 * The main method of this interface, which is responsible for releasing resources occupied by auto-closable object.
	 * The {@code finalize()} method of auto-closable class should call {@link #close()} method so that {@link #close()} method is called automatically before the Java garbage collector destroys auto-closable object.
	 * @throws Exception if any error raises.
	 */
    void close() throws Exception;
}
