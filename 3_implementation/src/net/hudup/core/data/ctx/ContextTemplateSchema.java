package net.hudup.core.data.ctx;

import java.io.Serializable;

import net.hudup.core.Transfer;



/**
 * {@link ContextTemplateSchema} class represents a scheme of context templates.
 * By default, it includes a set of root templates; each root template is a tree or a set, which in turn, have many templates
 * Note, {@link ContextTemplate} is template for a context represented by the class {@link Context}.
 * {@code Context} includes time, location, companion, etc. associated with a user when she/he is buying, studying, etc.
 * For example, <i>Time</i> is the template for time context <i>31/10/2015 6:45</i>.
 * In other words, context is an instance of context template with specific value.
 * A set of context templates is structured as the model which is called <i>context template schema</i> represented by this interface.
 * Please see {@link Context} and {@link ContextTemplate} for more details of context and context template.
 * Context template schema is managed by context template schema manager, called shortly {@code CTS manager}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface ContextTemplateSchema extends Serializable, Transfer {

	
	/**
	 * Getting context template via specified template ID.
	 * @param templateId specified context template identification (ID)
	 * @return context template having specified ID.
	 */
	ContextTemplate getTemplateById(int templateId);

	
	/**
	 * Every schema has a list of root context templates. This method returns a root template at specified index.
	 * @param index specified index.
	 * @return root context template at specified index.
	 */
	ContextTemplate getRoot(int index);
	
	
	/**
	 * Retrieving a root template via its identification (ID).
	 * @param rootId specified root template ID.
	 * @return root template having specified ID.
	 */
	ContextTemplate getRootById(int rootId);

	
	/**
	 * Every schema has a list of root context templates. Each root template has always an identification (ID).
	 * This method looks up index of the root template specified by given ID. 
	 * @param rootId specified root template ID.
	 * @return index of the root template specified by given ID.
	 */
	int indexOfRoot(int rootId);
	
	
	/**
	 * Every schema has a list of root context templates. Such list can be expended by adding a specified new root template via calling this method.
	 * @param root specified new root template.
	 * @return whether add successfully
	 */
	boolean addRoot(ContextTemplate root);
	
	
	/**
	 * Every schema has a list of root context templates. Such list can be shrunk by removing a root template having specified ID via calling this method.
	 * Moreover this method also returns the root template having just removed from the list.
	 * @param rootId specified root template identification (ID).
	 * @return the removed root template.
	 */
	ContextTemplate removeRootById(int rootId);
	
	
	/**
	 * Every schema has a list of root context templates. This method returns the size of such list.
	 * @return size of the list of root context templates.
	 */
	int rootSize();

	
	/**
	 * Creating the default template. For different implementation of {@code ContextTemplateSchema}, this method is defined differently.
	 * For example, in hierarchical schema, the default template also has hierarchical structure. 
	 * @return default {@link ContextTemplate}
	 */
	ContextTemplate defaultTemplate();
	
	
	/**
	 * After constructing, schema is empty, which have no templates.
	 * The implementation of this interface is responsible for defining this method in order to initialize itself with some default templates.
	 * For example, a class that implements this interface can create the template structure weekdays {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday} in this method.
	 */
	void defaultCTSchema();
	
	
	/**
	 * Retrieving all templates in schema.
	 * @return array of all templates in schema.
	 */
	ContextTemplate[] getAllTemplates();
	
	
	/**
	 * Clearing this schema, which means that removing all root templates from this schema.
	 */
	void clear();
	
	
}
