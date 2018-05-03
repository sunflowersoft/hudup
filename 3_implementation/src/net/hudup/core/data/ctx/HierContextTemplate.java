package net.hudup.core.data.ctx;


/**
 * This interface extending directly interface {@link ContextTemplate} specified a context template in for of hierarchical structure.
 * {@link ContextTemplate} is template for a context represented by the class {@link Context}.
 * {@link Context} includes time, location, companion, etc. associated with a user when she/he is buying, studying, etc.
 * According to hierarchical structure, templates are arranged in a tree. For example, template &quot;Location&quot; is the parent of templates &quot;Province&quot; and &quot;City&quot;
 * which, in turn, are parents of templates &quot;Suburb District&quot;, &quot;Town&quot;, &quot;District&quot;, &quot;Small City&quot;.
 *  
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface HierContextTemplate extends ContextTemplate {
	
	
	/**
	 * Testing whether this template is ascendant of specified template.
	 * @param template specified template.
	 * @return whether this template is ascendant of specified template.
	 */
	boolean ascendantOf(ContextTemplate template);

	
	/**
	 * Testing whether this template is child of specified template.
	 * @param template specified template.
	 * @return whether this template is child of specified template.
	 */
	boolean childOf(ContextTemplate template);

	
	/**
	 * Testing whether this template is descendant of specified template.
	 * @param template specified template.
	 * @return whether this template is descendant of specified template.
	 */
	boolean descendantOf(ContextTemplate template);

	
	/**
	 * Adding the child template to this template.
	 * @param child child template.
	 * @return whether adding successfully.
	 */
	boolean addChild(ContextTemplate child);
	
	
	/**
	 * Getting the child template of this template at specified index.
	 * @param index specified index.
	 * @return child template of this template at specified index.
	 */
	ContextTemplate getChild(int index);
	
	
	/**
	 * Getting the number of child templates of this template.
	 * @return the number of child templates of this template.
	 */
	int getChildSize();
	
	
	/**
	 * Finding index of the child template that has the specified identifier (ID).
	 * @param childId specified identifier (ID) of the child template.
	 * @return index of the child template that has the specified identifier (ID).
	 */
	int getChildIndex(int childId);
	
	
	/**
	 * Removing the child template at specified index.
	 * @param index specified index.
	 * @return removed child template.
	 */
	ContextTemplate removeChild(int index);
	
	
	/**
	 * Removing the child template by its identifier (ID).
	 * @param childId identifier (ID) of the child template.
	 * @return removed child template.
	 */
	ContextTemplate removeChildById(int childId);

	
	/**
	 * Getting the level of this template. For example, the roo template has level 0 and the leaf template (the template having no child) has largest level. 
	 * @return level of this template.
	 */
	int getLevel();

	
	/**
	 * Getting the parent template of this template.
	 * @return parent template of this template.
	 */
	ContextTemplate getParent();

	
	/**
	 * Setting the parent template of this template by the specified template.
	 * @param parent specified template.
	 * @return whether setting successfully.
	 */
	boolean setParent(ContextTemplate parent);
	
	
	/**
	 * Getting the root template of this template. The root template has no parent template.
	 * @return root template of this template.
	 */
	ContextTemplate getRoot();

	
	/**
	 * Getting sibling templates of this template.
	 * @return array of sibling templates of this template.
	 */
	ContextTemplate[] getSibling();

	
	/**
	 * Getting any template from this template by the specified identifier.
	 * @param id specified identifier (ID).
	 * @return {@link ContextTemplate} by id.
	 */
	ContextTemplate getTemplateById(int id);
	
	
	/**
	 * Testing whether this template is leaf. Leaf template has no child template.
	 * @return whether this template is leaf.
	 */
	boolean isLeaf();

	
	/**
	 * Testing whether this template is root. Root template has no parent template.
	 * @return whether this template is root.
	 */
	boolean isRoot();

	
	/**
	 * Testing whether this template is parent of specified template.
	 * @param template specified template.
	 * @return whether this template is parent of specified template.
	 */
	boolean parentOf(ContextTemplate template);

	
	/**
	 * Processing this template by specified context template processor.
	 * @param processor specified context template processor represented by {@link CTProcessor}.
	 * Note, {@link CTProcessor} specifies how to process a context template when browsing a collection of context templates such as fetcher, context template schema {@link ContextTemplateSchema}.
	 */
	void process(CTProcessor processor);

	
	/**
	 * Returning length of this template. Such length is defined according to particular application.
	 * For example, it can be the number of child templates of this template plus 1 (itself).
	 * @return length of this template.
	 */
	int length();
	
	
	/**
	 * Finding the maximum identifier (ID) over templates relevant to this template.
	 * How to define this method depends on particular application.
	 * For example, such maximum ID is foud out over this template and its child templates.
	 * @return maximum identifier (ID) over templates relevant to this template.
	 */
	int getMaxId();


	/**
	 * Getting all templates relevant to this template. How to define this method depends on particular application.
	 * For example, these template are this template and child templates of this template.
	 * @return all templates relevant to this template.
	 */
	ContextTemplate[] getAllTemplates();
	
}
