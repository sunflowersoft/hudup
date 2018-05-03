package net.hudup.data.ctx;

import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.ctx.CTProcessor;
import net.hudup.core.data.ctx.CTProcessorAbstract;
import net.hudup.core.data.ctx.ContextTemplate;
import net.hudup.core.data.ctx.ContextTemplateList;
import net.hudup.core.data.ctx.HierContextTemplate;

/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ContextTemplateImpl implements HierContextTemplate {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */
	protected DataConfig config = null;
	
	
	/**
	 * 
	 */
	protected int id = -1;
	
	
	/**
	 * 
	 */
	protected String name = "";
	
	
	/**
	 * 
	 */
	protected Attribute attribute = new Attribute(DataConfig.CONTEXT_TEMPLATE_UNIT, Type.integer);
	
	
	/**
	 * 
	 */
	protected ContextTemplate parent = null;
	
	
	/**
	 * 
	 */
	protected ContextTemplateList children = new ContextTemplateList();
	
	
	/**
	 * 
	 */
	protected AttributeList profileAttributes = new AttributeList();
	
	
	/**
	 * 
	 * @param id
	 * @param name
	 * @param attribute
	 */
	private ContextTemplateImpl(int id, String name, Attribute attribute) {
		super();
		// TODO Auto-generated constructor stub
		
		this.id = id;
		this.name = name;
		this.attribute = attribute;
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	
	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		if (name != null && !name.isEmpty())
			this.name = name;
	}


	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return id;
	}

	
	@Override
	public Attribute getAttribute() {
		// TODO Auto-generated method stub
		return attribute;
	}

	
	@Override
	public void setAttribute(Attribute attribute) {
		// TODO Auto-generated method stub
		this.attribute = attribute;
	}


	@Override
	public void setAttribute(Type type) {
		// TODO Auto-generated method stub
		this.attribute = new Attribute(DataConfig.CONTEXT_TEMPLATE_UNIT, type);
	}


	@Override
	public AttributeList getProfileAttributes() {
		// TODO Auto-generated method stub
		return profileAttributes;
	}


	@Override
	public void setProfileAttributes(AttributeList profileAttributes) {
		// TODO Auto-generated method stub
		if (profileAttributes == null)
			return;
		
		this.profileAttributes = profileAttributes;
	}


	@Override
	public boolean hasProfile() {
		// TODO Auto-generated method stub
		return profileAttributes.size() > 0;
	}


	@Override
	public boolean ascendantOf(ContextTemplate template) {
		// TODO Auto-generated method stub
	
		while (template != null) {
			ContextTemplate parent = ((HierContextTemplate)template).getParent();
			
			if (parent != null && parent.equals(this))
				return true;
			
			template = parent;
		}
		
		return false;
	}

	
	@Override
	public boolean canInferFrom(ContextTemplate template) {
		// TODO Auto-generated method stub
		
		// The main idea is general thing can be inferred by specified thing
		
		if (template == null)
			return false;
		else
			return this.ascendantOf(template);
	}

	
	@Override
	public boolean canInferTo(ContextTemplate template) {
		// TODO Auto-generated method stub
		
		// The main idea is general thing can be inferred by specified thing

		if (template == null)
			return false;
		else
			return ((HierContextTemplate)template).ascendantOf(this);
	}

	
	@Override
	public boolean childOf(ContextTemplate template) {
		// TODO Auto-generated method stub
		ContextTemplate parent = this.getParent();
		return parent != null && parent.equals(template);
	}

	
	@Override
	public boolean descendantOf(ContextTemplate template) {
		// TODO Auto-generated method stub
		ContextTemplate thisTemplate = this;
		
		while (thisTemplate != null) {
			ContextTemplate parent = ((HierContextTemplate)thisTemplate).getParent();
			
			if (parent != null && parent.equals(template))
				return true;
			
			thisTemplate = parent;
		}
		
		return false;
	}

	
	@Override
	public boolean addChild(ContextTemplate child) {
		if (children.add(child)) {
			((ContextTemplateImpl)child).parent = this;
			return true;
		}
		else
			return false;
	}
	
	
	/**
	 * 
	 * @param children
	 */
	public void addChildren(ContextTemplate[] children) {
		for (ContextTemplate child : children) {
			addChild(child);
		}
	}

	
	@Override
	public ContextTemplate getChild(int index) {
		// TODO Auto-generated method stub
		return children.get(index);
	}


	@Override
	public int getChildSize() {
		// TODO Auto-generated method stub
		return children.size();
	}


	@Override
	public int getChildIndex(int childId) {
		// TODO Auto-generated method stub
		return children.indexOf(childId);
	}


	@Override
	public ContextTemplate removeChild(int index) {
		// TODO Auto-generated method stub
		return children.remove(index);
	}


	@Override
	public ContextTemplate removeChildById(int childId) {
		// TODO Auto-generated method stub
		return children.removeById(childId);
	}


	@Override
	public int getLevel() {
		// TODO Auto-generated method stub
		int level = 0;
		
		ContextTemplate parent = getParent();
		while (parent != null) {
			level++;
			parent = ((HierContextTemplate)parent).getParent();
		}
		
		return level;
	}

	
	@Override
	public ContextTemplate getParent() {
		// TODO Auto-generated method stub
		return parent;
	}

	
	@Override
	public boolean setParent(ContextTemplate parent) {
		if (parent == null)
			return false;
		
		return ((HierContextTemplate)parent).addChild(this);
	}
	
	
	@Override
	public ContextTemplate getRoot() {
		// TODO Auto-generated method stub
		ContextTemplate root = getParent();
		if (root == null)
			return this;
		else
			return ((HierContextTemplate)root).getRoot();
		
	}

	
	@Override
	public ContextTemplate[] getSibling() {
		// TODO Auto-generated method stub
		ContextTemplate parent = getParent();
		if (parent == null)
			return new ContextTemplate[] { };
		
		ContextTemplateList sibling = new ContextTemplateList();
		int childSize = ((HierContextTemplate)parent).getChildSize();
		for (int i = 0; i < childSize; i++) {
			ContextTemplate child = ((HierContextTemplate)parent).getChild(i);
			
			if (!child.equals(this))
				sibling.add(child);
		}
		
		return sibling.toArray();
	}

	
	@Override
	public ContextTemplate getTemplateById(int id) {
		// TODO Auto-generated method stub
		if (this.id == id)
			return this;
		
		for (int i = 0; i < children.size(); i++) {
			ContextTemplate found = ((HierContextTemplate)children.get(i)).getTemplateById(id);
			if (found != null)
				return found;
		}
		
		return null;
			
	}

	
	public ContextTemplate[] getAllTemplates() {
		List<ContextTemplate> outList = Util.newList();
		getAllTemplates(this, outList);
		
		return outList.toArray(new ContextTemplate[] { });
	}
	
	
	/**
	 * 
	 * @param root
	 * @param outList
	 */
	private static void getAllTemplates(ContextTemplateImpl root, List<ContextTemplate> outList) {
		if (root == null)
			return;
		
		outList.add(root);
		
		for (int i = 0; i < root.children.size(); i++) {
			ContextTemplateImpl child = (ContextTemplateImpl) root.children.get(i);
			getAllTemplates(child, outList);
		}
	}
	
	
	@Override
	public boolean isLeaf() {
		// TODO Auto-generated method stub
		return children.size() == 0;
	}

	
	@Override
	public boolean isRoot() {
		// TODO Auto-generated method stub
		return getParent() == null;
	}

	
	@Override
	public boolean parentOf(ContextTemplate template) {
		// TODO Auto-generated method stub
		if (template == null)
			return false;
		
		ContextTemplate parent = ((HierContextTemplate)template).getParent();
		return (parent != null && parent.equals(this));
		
	}


	@Override
	public void process(CTProcessor processor) {
		
		processor.process(this);
		for (int i = 0; i < children.size(); i++) {
			ContextTemplate template = children.get(i);
			try {
				((HierContextTemplate)template).process(processor);
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
	
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj == null || !(obj instanceof ContextTemplate))
			return false;
		
		return getId() == ((ContextTemplate)obj).getId();
	}

	
	
	@Override
	public int length() {
		// TODO Auto-generated method stub
		
		CTProcessor processor = new CTProcessorAbstract((Integer)0) {
			
			@Override
			public void process(ContextTemplate template) {
				// TODO Auto-generated method stub
				param = (Integer)param + (Integer)1;
			}
		};
		
		process(processor);
		return (Integer)processor.getParam();
	}


	@Override
	public int getMaxId() {
		// TODO Auto-generated method stub
		
		CTProcessor processor = new CTProcessorAbstract((Integer)Integer.MIN_VALUE) {
			
			@Override
			public void process(ContextTemplate template) {
				// TODO Auto-generated method stub
				param = Math.max((Integer)param, template.getId());
			}
		};
		
		process(processor);
		return Math.max( (Integer)processor.getParam(), 0);
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name;
	}


	/**
	 * 
	 * @param id
	 * @param name
	 * @param attribute
	 * @return {@link ContextTemplate}
	 */
	public static ContextTemplateImpl create(int id, String name, Attribute attribute) {
		if (id < 0 || name == null || name.isEmpty() || attribute == null)
			return null;
		
		return new ContextTemplateImpl(id, name, attribute);
	}


	/**
	 * 
	 * @param id
	 * @param name
	 * @param type
	 * @return {@link ContextTemplate}
	 */
	public static ContextTemplateImpl create(int id, String name, Type type) {
		Attribute attribute = new Attribute(
				DataConfig.CONTEXT_TEMPLATE_UNIT, type);
		return create(id, name, attribute);
	}

	
	/**
	 * 
	 * @param id
	 * @param name
	 * @param type
	 * @return {@link ContextTemplate}
	 */
	public static ContextTemplateImpl create(int id, String name, int type) {
		Attribute attribute = new Attribute(
				DataConfig.CONTEXT_TEMPLATE_UNIT, Attribute.fromInt(type));
		return create(id, name, attribute);
	}


	
}
