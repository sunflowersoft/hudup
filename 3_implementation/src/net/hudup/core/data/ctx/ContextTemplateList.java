package net.hudup.core.data.ctx;

import java.io.Serializable;
import java.util.List;

import net.hudup.core.Transfer;
import net.hudup.core.Util;


/**
 * This class represents a list of context templates. It provides utility methods to process on such list such as adding template, removing template, and finding template.
 * Note, {@link ContextTemplate} is template for a context represented by the class {@link Context}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ContextTemplateList implements Serializable, Transfer {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * List of context templates.
	 */
	protected List<ContextTemplate> list = Util.newList();
	
	
	/**
	 * Default constructor.
	 */
	public ContextTemplateList() {
		
	}
	
	
	/**
	 * Constructor with other template list.
	 * @param list other template list.
	 */
	public ContextTemplateList(ContextTemplateList list) {
		addAll(list);
	}

	
	/**
	 * Getting the size of this template list which is the number of templates in this template list.
	 * @return size of template list.
	 */
	public int size() {
		return list.size();
	}
	
	
	/**
	 * Getting the template at specified index.
	 * @param index specified index.
	 * @return template at specified index.
	 */
	public ContextTemplate get(int index) {
		return list.get(index);
				
	}
	
	
	/**
	 * Getting the template by specified identifier (ID).
	 * @param templateId specified identifier (ID).
	 * @return template by specified identifier (ID).
	 */
	public ContextTemplate getById(int templateId) {
		int index = indexOf(templateId);
		if (index == -1)
			return null;
		
		return get(index);
				
	}

	
	/**
	 * Finding index of template that has the specified identifier (ID).
	 * @param templateId specified identifier (ID).
	 * @return index of the template that has the specified identifier (ID).
	 */
	public int indexOf(int templateId) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getId() == templateId)
				return i;
		}
		
		return -1;
	}
	
	
	/**
	 * Adding the specified template into this template list.
	 * @param template specified template.
	 * @return whether add successfully.
	 */
	public boolean add(ContextTemplate template) {
		if (indexOf(template.getId()) != -1)
			return false;
		
		return list.add(template);
	}
	
	
	/**
	 * Adding all templates of the specified template list into this template list.
	 * @param templateList specified template list.
	 */
	public void addAll(ContextTemplateList templateList) {
		this.list.addAll(templateList.list);
	}
	
	
	/**
	 * Removing the template at specified index.
	 * @param index specified index.
	 * @return removed {@link ContextTemplate}
	 */
	public ContextTemplate remove(int index) {
		return list.remove(index);
	}
	
	
	/**
	 * Removing a template by its identifier (ID).
	 * @param id specified identifier (ID).
	 * @return removed {@link ContextTemplate}.
	 * 
	 */
	public ContextTemplate removeById(int id) {
		int index = indexOf(id);
		if (index != -1)
			return remove(index);
		else
			return null;
	}
	
	
	@Override
	public Object transfer() {
		// TODO Auto-generated method stub
		
		return new ContextTemplateList(this);
	}


	/**
	 * Clearing this list, which means that all templates are removed from this list.
	 */
	public void clear() {
		list.clear();
	}
	
	
	/**
	 * Converting this template list into an array of templates.
	 * @return array of templates.
	 */
	public ContextTemplate[] toArray() {
		return list.toArray(new ContextTemplate[] { });
	}
	
	
}
