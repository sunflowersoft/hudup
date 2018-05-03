package net.hudup.core.data.ctx;


/**
 * This interface specifies how to process a context template when browsing a collection of context templates such as fetcher, context template schema {@link ContextTemplateSchema}.
 * This interface is called {@code context template processor}.
 * The main method of {@link CTProcessor} is {@link #process(ContextTemplate)}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface CTProcessor {

	
	/**
	 * The main method processes a specified context template when browsing a collection of context templates such as fetcher, context template schema {@link ContextTemplateSchema}.
	 * Any class that implements {@link CTProcessor} must define this method according to particular application.
	 * @param template specified context template.
	 */
	void process(ContextTemplate template);
	
	
	/**
	 * Each {@link CTProcessor} has parameter. This method returns such parameter.
	 * @return parameter of {@link CTProcessor}.
	 */
	Object getParam();
	
	
}
