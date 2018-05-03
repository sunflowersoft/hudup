package net.hudup.core.data.ctx;

import java.io.Serializable;
import java.util.List;

import net.hudup.core.Cloneable;
import net.hudup.core.Util;


/**
 * This class represents a list of context that a user rates on an item. Context is additional information related to user rating, for example, place that user makes a rating,
 * companion indicating the persons with whom user makes a rating such as alone, friends, girlfriend/boyfriend, family, co-workers.
 * Context is represented by {@link Context} class.
 * As a convention, this class is called context list, which provides utility methods to process (add, set, remove, etc.) a list of contexts.
 * This class has a built-in variable {@link #list} that is the {@link List} of {@link Context}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ContextList implements Serializable, Cloneable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * The built-in variable contains really contexts. In other words {@link ContextList} is essentially a wrapper of this variable.
	 */
	protected List<Context> list = Util.newList();
	
	
	/**
	 * Default constructor.
	 */
	public ContextList() {
		if (!(list instanceof Serializable))
			throw new RuntimeException("List is not serializable");
	}
	
	
	/**
	 * Getting the size of context list.
	 * @return size of context list.
	 */
	public int size() {
		return list.size();
	}
	
	
	/**
	 * Getting a context at specified index.
	 * @param index Specified index
	 * @return the context at specified index.
	 */
	public Context get(int index) {
		return list.get(index);
				
	}
	
	
	/**
	 * Adding the specified context into this list.
	 * @param context The specified context.
	 * @return whether adding successfully.
	 */
	public boolean add(Context context) {
		
		return list.add(context);
	}
	
	
	/**
	 * Removing a context from this list at specified index.
	 * @param index Specified index.
	 */
	public void remove(int index) {
		list.remove(index);
	}
	
	
	/**
	 * Clearing this list, which means that this method removes all contexts from this list.
	 */
	public void clear() {
		list.clear();
	}
	
	
	@Override
	public Object clone() {
		ContextList list = new ContextList();
		for (Context context : this.list) {
			list.add((Context)context.clone());
		}
		
		return list;
	}
	
	
	/**
	 * Given the specified context, this method returns {@code true} if all contexts inside this list can be inferred from such specified context.
	 * Otherwise, this method returns {@code false}.
	 * Please see {@link Context#canInferFrom(Context)} for more details about the concept &quot;can be inferred from&quot;
	 * @param context The specified context.
	 * @return whether this context list is inferred by the specified context
	 */
	public boolean canInferFrom(Context context) {
		
		for (Context ctx : list) {
			if(! ctx.canInferFrom(context))
				return false;
		}	
		
		return true;
	}
	
	
	/**
	 * Given the specified context list, this method returns {@code true} if all contexts inside this list can be inferred from such specified context list.
	 * Otherwise, this method returns {@code false}. Of course this methods calls {@link #canInferFrom(Context)} method.
	 * Please see {@link Context#canInferFrom(Context)} for more details about the concept &quot;can be inferred from&quot;
	 * @param contexts Specified context list.
	 * @return whether this context list is inferred by specified context list
	 */
	public boolean canInferFrom(ContextList contexts) {
		if (contexts == null || contexts.size() == 0)
			return false;
		
		for (Context ctx1 : list) {
			
			boolean found = false;
			for (Context ctx2 : contexts.list) {
				if (ctx1.canInferFrom(ctx2)) {
					found = true;
					break;
				}
			}
			
			if (!found)
				return false;
			
		}	
		
		return true;
	}

	
	/**
	 * Converting this context list into array of contexts.
	 * @return array of contexts.
	 */
	public Context[] toArray() {
		return list.toArray(new Context[] { });
	}


}
