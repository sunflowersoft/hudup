package net.hudup.data.ctx;

import java.io.Serializable;
import java.util.Map;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.NominalList;
import net.hudup.core.data.Profile;
import net.hudup.core.data.Profiles;
import net.hudup.core.data.ProviderAssoc;
import net.hudup.core.data.UnitList;
import net.hudup.core.data.ctx.CTProcessorAbstract;
import net.hudup.core.data.ctx.CTSManager;
import net.hudup.core.data.ctx.Context;
import net.hudup.core.data.ctx.ContextList;
import net.hudup.core.data.ctx.ContextTemplate;
import net.hudup.core.data.ctx.ContextTemplateList;
import net.hudup.core.data.ctx.ContextTemplateSchema;
import net.hudup.core.data.ctx.ContextValue;
import net.hudup.core.data.ctx.HierContextTemplate;
import net.hudup.core.parser.TextParserUtil;
import net.hudup.data.MemProfiles;
import net.hudup.data.ProviderAssocFactory;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class DefaultCTSManager extends CTSManagerAbstract {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	protected ContextTemplateSchema ctSchema = null;
	
	
	/**
	 * 
	 */
	protected ProviderAssoc assoc = null;

	
	/**
	 * 
	 */
	public DefaultCTSManager() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void setup(DataConfig config) {
		// TODO Auto-generated method stub

		try {
			close();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assoc = ProviderAssocFactory.create(config);
		ctSchema = ContextTemplateSchemaImpl.create();
		reload();

	}


	@Override
	public DataConfig getConfig() {
		// TODO Auto-generated method stub
		
		return assoc == null ? null : assoc.getConfig();
	}


	@Override
	public ContextTemplateSchema getCTSchema() {
		// TODO Auto-generated method stub
		
		return ctSchema;
	}

	
	@Override
	public boolean createContextTemplateUnit() {
		return assoc.createUnit(getConfig().getContextTemplateUnit(), AttributeList.defaultContextTemplateAttributeList());
	}
	
	
	@Override
	public void reload() {
		// TODO Auto-generated method stub
		ctSchema.clear();
		
		DataConfig config = getConfig();
		if (config.getContextTemplateUnit() == null || config.getContextUnit() == null)
			return;
		
		UnitList unitList = assoc.getUnitList();
		if (!unitList.contains(config.getContextTemplateUnit()))
			return;
		
		ContextTemplateList templates = new ContextTemplateList();
		Fetcher<Profile> fetcher = assoc.getProfiles(config.getContextTemplateUnit(), null);
		try {
			while (fetcher.next()) {
				Profile profile = fetcher.pick();
				if (profile == null)
					continue;
				
				int ctx_templateid = profile.getValueAsInt(DataConfig.CTX_TEMPLATEID_FIELD);
				
				ContextTemplate template = createTemplate(ctx_templateid, templates);
				if (template != null)
					templates.add(template);
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (fetcher != null)
					fetcher.close();
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
				
		((ContextTemplateSchemaImpl)ctSchema).assign(templates);
	}

	
	/**
	 * 
	 * @param templateId
	 * @param templates
	 * @return {@link ContextTemplate}
	 */
	private ContextTemplate createTemplate(int templateId, ContextTemplateList templates) {
		// Return if invalid template id or existing template
		if (templateId < 0 || templates.indexOf(templateId) != -1)
			return null;
		
		// Read context template
		Profile condition = new Profile(assoc.getAttributes(getConfig().getContextTemplateUnit()));
		condition.setValue(DataConfig.CTX_TEMPLATEID_FIELD, templateId);
		Profile profile = assoc.getProfile(getConfig().getContextTemplateUnit(), condition);
		if (profile == null)
			return null;
		
		// Create context template
		ContextTemplate template = ContextTemplateImpl.create(
				templateId, 
				profile.getValueAsString(DataConfig.CTX_NAME_FIELD), Type.integer);
		
		
		try {
			
			// Setting nominal values to template
			Attribute attribute = template.getAttribute();
			if (attribute.getType() == Type.nominal) {
				NominalList nominalList = assoc.getNominalList(
						getConfig().getContextTemplateUnit(), 
						DataConfig.CTX_VALUE_FIELD);
				
				attribute.setNominalList(nominalList);
			}
			
			// Setting profile attributes to template
			UnitList unitList = assoc.getUnitList();
			String profileUnit = getConfig().getContextTemplateProfileUnit(templateId);
			if (profileUnit != null && unitList.contains(profileUnit)) {
				AttributeList attributes = assoc.getAttributes(profileUnit);
				template.setProfileAttributes(attributes);
			}
			
			int ctx_parent = profile.getValueAsInt(DataConfig.CTX_PARENT_FIELD);
			if (ctx_parent < 0)
				return template;
			
			// Instantiating recursively template's parents 
			ContextTemplate parent = templates.getById(ctx_parent);
			parent = parent == null ? createTemplate(ctx_parent, templates) : parent;
			if (parent != null) {
				templates.add(parent);
				((HierContextTemplate)template).setParent(parent);
			}
			
			return template;
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		return null;
	}

	
	@Override
	public ContextList getContexts(int userId, int itemId) {
		// TODO Auto-generated method stub
		ContextList contexts = new ContextList();
		
		AttributeList attributes = new AttributeList();
		
		Attribute userIdAtt = new Attribute(DataConfig.USERID_FIELD, Type.integer);
		userIdAtt.setKey(true);
		attributes.add(userIdAtt);
		
		Attribute itemIdAtt = new Attribute(DataConfig.ITEMID_FIELD, Type.integer);
		itemIdAtt.setKey(true);
		attributes.add(itemIdAtt);

		Profile condition = new Profile(assoc.getAttributes(getConfig().getContextUnit()));
		condition.setValue(DataConfig.USERID_FIELD, userId);
		condition.setValue(DataConfig.ITEMID_FIELD, itemId);
		
		Fetcher<Profile> fetcher = assoc.getProfiles(getConfig().getContextUnit(), condition);
		try {
			while (fetcher.next()) {
				Profile profile = fetcher.pick();
				if (profile == null)
					continue;
				
				int ctxTemplateId = profile.getValueAsInt(DataConfig.CTX_TEMPLATEID_FIELD);
				if (ctxTemplateId < 0)
					continue;
				
				Object value = profile.getValue(DataConfig.CTX_VALUE_FIELD);
				if (value == null || !(value instanceof Serializable))
					value = null;
				
				Context context = createContext(ctxTemplateId, (Serializable) value);
				if (context != null)
					contexts.add(context);
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (fetcher != null)
					fetcher.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return contexts;
	}

	
	@Override
	public Profile profileOf(int ctxTemplateId, ContextValue ctxValue) {
		// TODO Auto-generated method stub
		if (ctxTemplateId < 0 || ctxValue == null || !ctxValue.isQuantized())
			return null;
		String profileUnit = getConfig().getContextTemplateProfileUnit(ctxTemplateId);
		if (profileUnit == null)
			return null;
		
		try {
			
			UnitList unitList = assoc.getUnitList();
			if (!unitList.contains(profileUnit))
				return null;

			Profile condition = new Profile(assoc.getAttributes(profileUnit));
			condition.setValue(0, (int) ctxValue.getQuantizedValue());
			return assoc.getProfile(profileUnit, condition);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		return null;
	}

	
	@Override
	public Profiles profilesOf(int ctxTemplateId) {
		// TODO Auto-generated method stub
		String profileUnit = getConfig().getContextTemplateProfileUnit(ctxTemplateId);
		if (profileUnit == null)
			return MemProfiles.createEmpty();
		UnitList unitList = assoc.getUnitList();
		if (!unitList.contains(profileUnit))
			return MemProfiles.createEmpty();
		
		AttributeList attList = assoc.getAttributes(profileUnit);
		Attribute idAtt = attList.getId();
		if (idAtt == null || attList.size() == 0)
			return MemProfiles.createEmpty();
		
		Map<Integer, Profile> profileMap = Util.newMap();
		
		Fetcher<Profile> fetcher = assoc.getProfiles(profileUnit, null);
		try {
			
			while (fetcher.next()) {
				Profile profile = fetcher.pick();
				if (profile == null)
					continue;
				
				Object id = profile.getIdValue();
				if (id == null || !(id instanceof Number))
					continue;
				
				int profileId = ((Number)id).intValue();
				if (profile != null) {
					profile.setAttRef(attList);
					profileMap.put(profileId, profile);
				}
			}
			
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (fetcher != null)
					fetcher.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return MemProfiles.assign(attList, profileMap);
	}

	
	@Override
	public boolean commitCTSchema() {
		// TODO Auto-generated method stub
		final String templateUnit = getConfig().getContextTemplateUnit();
		if (templateUnit == null)
			return false;
		
		UnitList unitList = assoc.getUnitList();
		if (!unitList.contains(templateUnit)) {
			createContextTemplateUnit(); // create template unit
		}
		else {
			assoc.deleteUnitData(templateUnit); // clear template unit data
		}
		
		final AttributeList templateAttRef = assoc.getAttributes(templateUnit);
		CTProcessorAbstract processor = new CTProcessorAbstract(null) {
			
			@Override
			public void process(ContextTemplate template) {
				// TODO Auto-generated method stub
				UnitList unitList = assoc.getUnitList();
				
				Profile templateRecord = new Profile(templateAttRef);
				templateRecord.setValue(DataConfig.CTX_TEMPLATEID_FIELD, template.getId());
				templateRecord.setValue(DataConfig.CTX_NAME_FIELD, template.getName());
				templateRecord.setValue(DataConfig.CTX_TYPE_FIELD, Attribute.toInt(template.getAttribute().getType()));
				templateRecord.setValue(DataConfig.CTX_PARENT_FIELD, 
						((HierContextTemplate)template).getParent() == null ? null : 
						((HierContextTemplate)template).getParent().getId());
				assoc.insertProfile(templateUnit, templateRecord);
				
				String profileUnit = getConfig().getContextTemplateProfileUnit(template.getId());

				if (!template.hasProfile()) {
					if (unitList.contains(profileUnit))
						assoc.dropUnit(profileUnit);
					
				}
				else if (unitList.contains(profileUnit)) {
					
					AttributeList templateAttList = template.getProfileAttributes();
					AttributeList profileUnitAttList = assoc.getAttributes(profileUnit);
					if (!templateAttList.identity(profileUnitAttList)) {
						assoc.dropUnit(profileUnit);
						assoc.createUnit(profileUnit, template.getProfileAttributes());
					}
					
				}
				else {
					assoc.createUnit(profileUnit, template.getProfileAttributes());
				}
					
				
			}//End process method
		};
		
		int n = ctSchema.rootSize();
		for (int i = 0; i < n; i++) {
			ContextTemplate root = ctSchema.getRoot(i);
			((HierContextTemplate)root).process(processor);
		}
		
		// Removing redundant template profile
		UnitList unitList2 = assoc.getUnitList();
		String prefix = getConfig().getContextTemplateUnit() +  TextParserUtil.CONNECT_SEP;
		for (int i = 0; i < unitList2.size(); i++) {
			String unitName = unitList2.get(i).getName();
			if (!unitName.startsWith(prefix))
				continue;
			
			int templateId = extractTemplateId(unitName);
			if (templateId == -1)
				continue;
			ContextTemplate template = ctSchema.getTemplateById(templateId);
			if (template == null) {
				String profileUnit = getConfig().getContextTemplateProfileUnit(templateId);
				assoc.dropUnit(profileUnit);
			}
				
		}
		return true;
	}

	
	@Override
	public boolean importCTSchema(CTSManager ctsm) {
		// TODO Auto-generated method stub
		
		// Creating schema
		ContextTemplateSchema schema = ctsm.getCTSchema();
		this.ctSchema.clear();
		for (int i= 0; i < schema.rootSize(); i++) {
			ContextTemplate root = schema.getRoot(i);
			this.ctSchema.addRoot(root);
		}
		boolean result = commitCTSchema(); // save schema
		
		int rootSize = schema.rootSize();
		for (int i = 0; i < rootSize; i++) {

			// Using processor for inserting template profile
			CTProcessorAbstract processor = new CTProcessorAbstract(ctsm) {
				
				@Override
				public void process(ContextTemplate template) {
					// TODO Auto-generated method stub
					if (!template.hasProfile())
						return;
					
					CTSManager ctsm = (CTSManager)param;
					Profiles profiles = ctsm.profilesOf(template.getId());
					if (profiles == null || profiles.size() == 0)
						return;
					
					String profileUnit = getConfig().getContextTemplateProfileUnit(template.getId());
					assoc.deleteUnitData(profileUnit); // clearing template profile data
					
					Fetcher<Profile> fetcher = profiles.fetch();
					try {
						while (fetcher.next()) {
							Profile profile = fetcher.pick();
							if (profile != null)
								assoc.insertProfile(profileUnit, profile);
						}
					}
					catch (Throwable e) {
						e.printStackTrace();
					}
					finally {
						try {
							fetcher.close();
						} 
						catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				} // end method process
			};
			
			ContextTemplate root = schema.getRoot(i);
			((HierContextTemplate)root).process(processor);
		}
		
		return result;
	}

	
	@Override
	public boolean importCTSchema(Dataset dataset) {
		// TODO Auto-generated method stub
		
		// Creating schema
		ContextTemplateSchema schema = dataset.getCTSchema();
		this.ctSchema.clear();
		for (int i= 0; i < schema.rootSize(); i++) {
			ContextTemplate root = schema.getRoot(i);
			this.ctSchema.addRoot(root);
		}
		boolean result = commitCTSchema(); // save schema
		
		int rootSize = schema.rootSize();
		for (int i = 0; i < rootSize; i++) {

			// Using processor for inserting template profile
			CTProcessorAbstract processor = new CTProcessorAbstract(dataset) {
				
				@Override
				public void process(ContextTemplate template) {
					// TODO Auto-generated method stub
					if (!template.hasProfile())
						return;
					
					Dataset dataset = (Dataset)param;
					Profiles profiles = dataset.profilesOf(template.getId());
					if (profiles == null || profiles.size() == 0)
						return;
					
					String profileUnit = getConfig().getContextTemplateProfileUnit(template.getId());
					assoc.deleteUnitData(profileUnit); // clearing template profile data
					
					Fetcher<Profile> fetcher = profiles.fetch();
					try {
						while (fetcher.next()) {
							Profile profile = fetcher.pick();
							if (profile != null)
								assoc.insertProfile(profileUnit, profile);
						}
					}
					catch (Throwable e) {
						e.printStackTrace();
					}
					finally {
						try {
							fetcher.close();
						} 
						catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				} // end method process
			};
			
			ContextTemplate root = schema.getRoot(i);
			((HierContextTemplate)root).process(processor);
		}
		
		return result;
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "default_cts_manager";
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new DefaultCTSManager();
	}

	
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		if (assoc != null) {
			try {
				assoc.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		assoc = null;
		
		if (ctSchema != null)
			ctSchema.clear();
		ctSchema = null;
	}

	
}
