package net.hudup.core.data.ctx;
import java.io.Serializable;

import net.hudup.core.Cloneable;


/**
 * <code>Context</code> represents time, location, companion, etc. associated with a user when she/he is buying, studying, etc.
 * <code>Context</code> is an instance of context template {@link ContextTemplate} with specific value.
 * For example, <i>Time</i> is the template for time context <i>31/10/2015 6:45</i>. So this class has two built-in variables such as {@link #template} representing context template and {@link #value} representing the value of such template.
 * A set of context templates is structured as the model which is called <i>context template schema</i> represented by interface {@link ContextTemplateSchema}.
 * Please see {@link ContextTemplate} and {@link ContextTemplateSchema} for more details of context template and context template schema.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class Context implements Serializable, Cloneable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Context template
	 */
	protected ContextTemplate template = null;
	
	
	/**
	 * Context value of context template. This value is represented by {@link ContextValue} class.
	 */
	protected ContextValue value = null;

	
	/**
	 * Constructor with context template and context value.
	 * @param template Specified context template.
	 * @param value Specified context value.
	 */
	private Context(ContextTemplate template, ContextValue value) {
		this.template = template;
		this.value = value;
	}
	
	
	/**
	 * Getting the context template, which returns the variable {@link #template}.
	 * @return {@link ContextTemplate}
	 */
	public ContextTemplate getTemplate() {
		return template;
	}

	
	/**
	 * Getting the context value, which returns the variable {@link #value}.
	 * @return {@link ContextValue}
	 */
	public ContextValue getValue() {
		return value;
	}

	
	/**
	 * Checking whether or not this context is valid. Context is valid if its template is not {@code null}.
	 * @return whether context is valid.
	 */
	public boolean isValid() {
		return template != null;
	}
	
	
	/**
	 * Checking whether or not this context is {@code null}. Context is {@code null} if its value is {@code null}.
	 * @return whether value is null
	 */
	public boolean isNullValue() {
		return value == null;
	}
	
	
	@Override
	public Object clone() {
		return new Context(template, (ContextValue)value.clone());
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null ||!(obj instanceof Context))
			return false;
		
		Context that = (Context)obj;
		if (this.template.equals(that.template)) {
			if (this.value == that.value)
				return true;
			else
				return false;
		}
		else			
			return false;
			
	}

	
	/**
	 * This method indicates whether or not this context can be inferred from the other context specified by the input parameter.
	 * There are three cases of possibility of inference:<br>
	 * 1. Two contexts are totally equal (template and value are the same).<br>
	 * 2. Two (context) templates are equal and this context has null value, it means that it is more generic than specified context.<br>
	 * 3. This template can be inferred from specified template, it means that this template is more generic than specified template.<br>
	 * <br>
	 * For example, this context &quot;December 2015&quot; can be inferred from the other context &quot;8th December 2015&quot;.
	 * @param that the other context.
	 * @return whether this context can be inferred from other context
	 */
	public boolean canInferFrom(Context that) {
		if (that == null)
			return false;
		
		if (this.equals(that))
			return true;
		else if (this.template.equals(that.template)) {
			if (that.getValue() == null)
				return true;
			else
				return false;
		}
		else
			return this.template.canInferFrom(that.template);
	}
	
	
	/**
	 * Opposite to {@link #canInferFrom(Context)}, which means that whether the other context can be inferred from this context.
	 * @param that The other context.
	 * @return  whether other context can be inferred from this context
	 */
	public boolean canInferTo(Context that) {
		if (that == null)
			return false;
		
		return that.canInferFrom(this);
	}

	
	/**
	 * This static method creates a context from the specified template and the specified value.
	 * @param template Specified template.
	 * @param value Specified value.
	 * @return A context created from the specified template and the specified value.
	 */
	public static Context create(ContextTemplate template, ContextValue value) {
		if (template == null)
			return null;
		
		return new Context(template, value);
	}
	
	
	/**
	 * This static method creates a context having null value from the specified template.
	 * @param template Specified template.
	 * @return A context having null value from the specified template.
	 */
	public static Context create(ContextTemplate template) {
		return create(template, null);
	}
	

}