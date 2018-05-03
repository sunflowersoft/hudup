package net.hudup.data.ctx;

import java.awt.Component;
import java.io.Serializable;

import net.hudup.core.data.Attribute;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.data.Profiles;
import net.hudup.core.data.ctx.CTProcessorAbstract;
import net.hudup.core.data.ctx.CTSManager;
import net.hudup.core.data.ctx.CTSMultiProfiles;
import net.hudup.core.data.ctx.Context;
import net.hudup.core.data.ctx.ContextTemplate;
import net.hudup.core.data.ctx.ContextTemplateSchema;
import net.hudup.core.data.ctx.ContextValue;
import net.hudup.core.data.ctx.HierContextTemplate;
import net.hudup.core.parser.TextParserUtil;
import net.hudup.data.ctx.ui.CTScreator;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class CTSManagerAbstract implements CTSManager {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public CTSManagerAbstract() {
		
	}
	
	
	@Override
	public Context createContext(
			int ctxTemplateId, 
			Serializable assignedValue) {
		
		ContextTemplate template = getCTSchema().getRootById(ctxTemplateId);
		if (template == null)
			return null;
		
		Attribute attribute = template.getAttribute();
		ContextValue value = ContextValueImpl.create(attribute, assignedValue);
		return Context.create(template, value);
	}

	
	@Override
	public Profile profileOf(Context context) {
		if (context == null || context.getTemplate() == null || context.getValue() == null)
			return null;
		
		return profileOf(context.getTemplate().getId(), context.getValue());
	}
	
	
	@Override
	public CTSMultiProfiles createCTSProfiles() {
		CTSMemMultiProfiles ctsProfiles = CTSMemMultiProfiles.create();
		
		ContextTemplateSchema schema = getCTSchema();
		int rootSize = schema.rootSize();
		for (int i = 0; i < rootSize; i++) {
			ContextTemplate root = schema.getRoot(i);
			
			CTProcessorAbstract processor = new CTProcessorAbstract(ctsProfiles) {
				
				@Override
				public void process(ContextTemplate template) {
					// TODO Auto-generated method stub
					CTSMemMultiProfiles ctsProfiles = (CTSMemMultiProfiles)param;
					
					Profiles profiles = profilesOf(template.getId());
					if (profiles.size() > 0)
						ctsProfiles.put(template.getId(), profiles);
				}
			};
			
			((HierContextTemplate)root).process(processor);
		}
		
		return ctsProfiles;
	}
	
	
	@Override
	public void defaultCTSchema() {
		getCTSchema().defaultCTSchema();
		commitCTSchema();
	}

	
	@Override
	public void controlPanel(Component comp) {
		// TODO Auto-generated method stub
		new CTScreator(comp, this);
	}


	/**
	 * 
	 * @param unitName
	 * @return extracted number
	 */
	protected int extractTemplateId(String unitName) {
		try {
			if (unitName == null || unitName.isEmpty())
				return -1;
			String temp = getConfig().getContextTemplateUnit() +  TextParserUtil.CONNECT_SEP;
			if (!unitName.startsWith(temp) || unitName.length() <= temp.length())
				return -1;
			
			temp = unitName.substring(temp.length()).trim();
			int index = temp.indexOf(TextParserUtil.CONNECT_SEP);
			if (index == -1)
				return -1;
			
			String snum = temp.substring(0, index);
			return Integer.parseInt(snum);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		return -1;
	}
	

	@Override
	public void resetConfig() {
		// TODO Auto-generated method stub
		
		throw new RuntimeException("Not support this method");
	}


	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not support this method");
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getName();
	}


	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		try {
			close();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}

	
	
}
