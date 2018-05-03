package net.hudup.core.data.ctx;

import java.io.Serializable;

import net.hudup.core.data.Attribute;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.AttributeList;


/**
 * The interface {@link ContextTemplate} represents a context template.
 * {@code ContextTemplate} is template for a context represented by the class {@link Context}.
 * {@code Context} includes time, location, companion, etc. associated with a user when she/he is buying, studying, etc.
 * For example, <i>Time</i> is the template for time context <i>31/10/2015 6:45</i>.
 * In other words, context is an instance of context template with specific value.
 * A set of context templates is structured as the model which is called <i>context template schema</i> represented by interface {@link ContextTemplateSchema}.
 * Please see {@link Context} and {@link ContextTemplateSchema} for more details of context and context template schema.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public interface ContextTemplate extends Serializable {

	
	/**
	 * Getting the identification (ID) of this template.
	 * @return id
	 */
	int getId();
	
	
	/**
	 * Getting the name of this template.
	 * @return name
	 */
	String getName();
	
	
	/**
	 * Setting the name of this template.
	 * @param name The specified name.
	 */
	void setName(String name);
	
	
	/**
	 * Every template has an attribute (or type). This method returns the attribute of this template.
	 * 
	 * @return {@link Attribute}
	 */
	Attribute getAttribute();
	
	
	/**
	 * Every template has an attribute (or type). This method sets an attribute to this template.
	 * @param attribute Specified attribute.
	 */
	void setAttribute(Attribute attribute);
	
	
	/**
	 * Every template has an attribute. This method sets an attribute to this template by a specified {@link Type}
	 * @param type Specified type.
	 */
	void setAttribute(Type type);
	
	
	/**
	 * Each template has associated profile named &quot;TemplateUnitName_TemplateId_Profile&quot;.
	 * In Hudup framework, this name has the form &quot;hdp_context_template_$templateid_profile&quot;.
	 * For example, if the ID of this template is 1, its profile name is &quot;hdp_context_template_1_profile&quot;.
	 * This methods returns the attribute list of such profile. The attribute list is represented by {@link AttributeList}.
	 * 
	 * @return Attribute list of such profile associated with the profile of this template. 
	 */
	AttributeList getProfileAttributes();
	
	
	/**
	 * Each template has associated profile named &quot;TemplateUnitName_TemplateId_Profile&quot;.
	 * In Hudup framework, this name has the form &quot;hdp_context_template_$templateid_profile&quot;.
	 * For example, if the ID of this template is 1, its profile name is &quot;hdp_context_template_1_profile&quot;.
	 * This method establishes a specified attribute list to such profile. The attribute list is represented by {@link AttributeList}.
	 * 
	 * @param profileAttributes Specified attribute list.
	 */
	void setProfileAttributes(AttributeList profileAttributes);

	
	/**
	 * Each template has associated profile named &quot;TemplateUnitName_TemplateId_Profile&quot;.
	 * In Hudup framework, this name has the form &quot;hdp_context_template_$templateid_profile&quot;.
	 * For example, if the ID of this template is 1, its profile name is &quot;hdp_context_template_1_profile&quot;.
	 * This method checks whether this template has a profile.
	 * @return whether this template has a profile.
	 */
	boolean hasProfile();
	
	
	/**
	 * This method indicates whether or not this template can be inferred from the other template specified by the input parameter.
	 * There are two cases of possibility of inference:<br>
	 * 1. Two templates are totally equal.<br>
	 * 2. This template can be inferred from specified template, it means that this template is more generic than specified template.<br>
	 * <br>
	 * For example, this template &quot;Year/Month&quot; can be inferred from the other template &quot;Year/Month/Day&quot;.
	 * @param template the other context template.
	 * @return whether this template can be inferred from specified template
	 */
	boolean canInferFrom(ContextTemplate template);

	
	/**
	 * Opposite to {@link #canInferFrom(ContextTemplate)}, which means that whether the other template can be inferred from this template.
	 * @param template The other context template.
	 * @return whether specified template can be inferred from this template
	 */
	boolean canInferTo(ContextTemplate template);

	
	@Override
	boolean equals(Object obj);

	
}