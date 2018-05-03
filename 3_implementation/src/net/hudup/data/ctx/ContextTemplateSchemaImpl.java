package net.hudup.data.ctx;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.ctx.ContextTemplate;
import net.hudup.core.data.ctx.ContextTemplateList;
import net.hudup.core.data.ctx.ContextTemplateSchema;
import net.hudup.core.data.ctx.HierContextTemplate;


/**
 * @author Loc Nguyen
 * @version 10.0
 */
public class ContextTemplateSchemaImpl implements ContextTemplateSchema {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */
	private ContextTemplateList roots = new ContextTemplateList();
	
	
	/**
	 * 
	 * @param roots
	 */
	private ContextTemplateSchemaImpl(ContextTemplateList roots) {
		this.roots = roots;
	}

	
	@Override
	public ContextTemplate getTemplateById(int templateId) {
		// TODO Auto-generated method stub
		
		for (int i = 0; i < roots.size(); i++) {
			ContextTemplate template = roots.get(i);
			ContextTemplate found = ((HierContextTemplate)template).getTemplateById(templateId);
			if (found != null)
				return found;
		}
		
		return null;
	}

	
	@Override
	public ContextTemplate getRoot(int index) {
		// TODO Auto-generated method stub
		return roots.get(index);
	}


	@Override
	public ContextTemplate getRootById(int rootId) {
		// TODO Auto-generated method stub
		int index = indexOfRoot(rootId);
		if (index == -1)
			return null;
		else
			return roots.get(index);
	}


	@Override
	public int indexOfRoot(int rootId) {
		// TODO Auto-generated method stub
		
		for (int i = 0; i < roots.size(); i++) {
			ContextTemplate template = roots.get(i);
			if (template.getId() == rootId)
				return i;
		}
		
		return -1;
	}


	@Override
	public boolean addRoot(ContextTemplate root) {
		// TODO Auto-generated method stub
		return roots.add(root);
	}


	@Override
	public ContextTemplate removeRootById(int rootId) {
		// TODO Auto-generated method stub
		return roots.removeById(rootId);
	}


	@Override
	public int rootSize() {
		return roots.size();
	}
	

	@Override
	public Object transfer() {
		// TODO Auto-generated method stub
		
		return new ContextTemplateSchemaImpl(
			(ContextTemplateList) roots.transfer());
	}

	
	/**
	 * 
	 * @param templates
	 */
	public void assign(ContextTemplateList templates) {
		clear();
		
		for (int i = 0; i < templates.size(); i++) {
			ContextTemplate template = templates.get(i);
			if (template == null)
				continue;
			
			if (((HierContextTemplate)template).getParent() == null)
				roots.add(template);
		}
	}
	
	
	@Override
	public ContextTemplate defaultTemplate() {
		// TODO Auto-generated method stub
		int maxId = Integer.MIN_VALUE;
		for (int i = 0; i < roots.size(); i++) {
			ContextTemplate root = roots.get(i);
			maxId = Math.max(maxId, ((HierContextTemplate)root).getMaxId());
		}
		maxId = Math.max(0, maxId) + 1;
		
		return ContextTemplateImpl.create(maxId, "Template " + maxId, Type.integer);
	}


	@Override
	public ContextTemplate[] getAllTemplates() {
		// TODO Auto-generated method stub
		
		List<ContextTemplate> templates = Util.newList();
		for (int i = 0; i < roots.size(); i++) {
			HierContextTemplate root = (HierContextTemplate) roots.get(i);
			templates.addAll(Arrays.asList(root.getAllTemplates()));
		}
		
		return templates.toArray(new ContextTemplate[] { });
	}


	@Override
	public void clear() {
		// TODO Auto-generated method stub
		if (roots != null)
			roots.clear();
	}


	@Override
	public void defaultCTSchema() {
		clear();
		
		int id = 0;
		ContextTemplateImpl time = ContextTemplateImpl.create(++id, "Time", Type.date);
		addRoot(time);
		//
		//
		ContextTemplateImpl dayOfWeek = ContextTemplateImpl.create(++id, "DayOfWeek", Type.date);
		time.addChild(dayOfWeek);
		dayOfWeek.addChildren(new ContextTemplate[] {
			ContextTemplateImpl.create(++id, "Monday", Type.date),
			ContextTemplateImpl.create(++id, "Tuesday", Type.date),
			ContextTemplateImpl.create(++id, "Wednesday", Type.date),
			ContextTemplateImpl.create(++id, "Thursday", Type.date),
			ContextTemplateImpl.create(++id, "Friday", Type.date),
			ContextTemplateImpl.create(++id, "Saturday", Type.date),
			ContextTemplateImpl.create(++id, "Sunday", Type.date),
		});
		//
		//
		ContextTemplateImpl timeOfWeek = ContextTemplateImpl.create(++id, "TimeOfWeek", Type.date);
		time.addChild(timeOfWeek);
		//
		ContextTemplateImpl weekday = ContextTemplateImpl.create(++id, "Weekday", Type.date);
		timeOfWeek.addChild(weekday);
		weekday.addChildren(new ContextTemplate[] {
			ContextTemplateImpl.create(++id, "Monday", Type.date),
			ContextTemplateImpl.create(++id, "Tuesday", Type.date),
			ContextTemplateImpl.create(++id, "Wednesday", Type.date),
			ContextTemplateImpl.create(++id, "Thursday", Type.date),
			ContextTemplateImpl.create(++id, "Friday", Type.date),
		});
		//
		ContextTemplateImpl weekend = ContextTemplateImpl.create(++id, "Weekend", Type.date);
		timeOfWeek.addChild(weekend);
		weekend.addChildren(new ContextTemplate[] {
			ContextTemplateImpl.create(++id, "Saturday", Type.date),
			ContextTemplateImpl.create(++id, "Sunday", Type.date),
		});
		
		
		ContextTemplateImpl location = ContextTemplateImpl.create(++id, "Location", Type.integer);
		addRoot(location);
		location.addChildren(new ContextTemplate[] {
			ContextTemplateImpl.create(++id, "Office", Type.integer),
			ContextTemplateImpl.create(++id, "Home", Type.integer),
			ContextTemplateImpl.create(++id, "Movies", Type.integer),
			ContextTemplateImpl.create(++id, "Coffee", Type.integer),
			ContextTemplateImpl.create(++id, "School", Type.integer),
			ContextTemplateImpl.create(++id, "Others", Type.integer),
		});
		//
		ContextTemplateImpl movies = (ContextTemplateImpl)location.getChild(2);
		AttributeList moviesAttributes = AttributeList.create(new Attribute[] {
			new Attribute(DataConfig.CTX_VALUE_FIELD, Type.integer),
			new Attribute("title", Type.string),
			new Attribute("length", Type.real),
			new Attribute("release_date", Type.date),
			new Attribute("director", Type.string),
			new Attribute("genre", Type.integer)
		});
		moviesAttributes.setKey(0);
		movies.setProfileAttributes(moviesAttributes);
		
		ContextTemplateImpl companion = ContextTemplateImpl.create(++id, "Companion", Type.integer);
		addRoot(companion);
		companion.addChildren(new ContextTemplate[] {
			ContextTemplateImpl.create(++id, "Alone", Type.integer),
			ContextTemplateImpl.create(++id, "Friends", Type.integer),
			ContextTemplateImpl.create(++id, "Girlfriend-Boyfriend", Type.integer),
			ContextTemplateImpl.create(++id, "Family", Type.integer),
			ContextTemplateImpl.create(++id, "Co-workers", Type.integer),
			ContextTemplateImpl.create(++id, "Others", Type.integer),
		});

		
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Context template schema";
	}


	/**
	 * 
	 * @param templates
	 * @return {@link ContextTemplateSchemaImpl}
	 */
	public static ContextTemplateSchemaImpl create(Collection<ContextTemplate> templates) {
		if (templates == null || templates.size() == 0)
			return create();
		
		ContextTemplateList roots = new ContextTemplateList();
		
		for (ContextTemplate template : templates) {
			if (template == null)
				continue;
			
			if (((HierContextTemplate)template).getParent() == null)
				roots.add(template);
		}
		
		return new ContextTemplateSchemaImpl(roots);
	}
	
	
	/**
	 * 
	 * @return {@link ContextTemplateSchemaImpl}
	 */
	public static ContextTemplateSchemaImpl create() {
		return new ContextTemplateSchemaImpl(new ContextTemplateList());
	}


	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		try {
			clear();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
	
	
}