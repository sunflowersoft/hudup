package net.hudup.server.external;

import java.util.Map;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.ExternalRecord;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.data.MemProfiles;
import net.hudup.data.SemiScanner;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class SemiScannerExt extends SemiScanner {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */
	protected Map<Integer, ExternalRecord> externalUserRecordMap;

	
	/**
	 * 
	 */
	protected Map<Integer, ExternalRecord> externalItemRecordMap;

	
	/**
	 * 
	 */
	protected MemProfiles itemProfiles;

	
	/**
	 * 
	 * @param config
	 */
	public SemiScannerExt(DataConfig config) {
		super(config);
		// TODO Auto-generated constructor stub
		
	}

	
	@Override
	protected void loadRatingData() {
		super.loadRatingData();
		
		loaded = false;
		
		if (externalUserRecordMap == null)
			externalUserRecordMap = Util.newMap();
		else
			externalUserRecordMap.clear();
		//
		Set<Integer> userIds = userRatingMap.keySet();
		for (int userId : userIds) {
			ExternalRecord userExternalRecord = getUserExternalRecord(userId);
			if (userExternalRecord != null)
				externalUserRecordMap.put(userId, userExternalRecord);
		}

		if (externalItemRecordMap == null)
			externalItemRecordMap = Util.newMap();
		else
			externalItemRecordMap.clear();
		if (itemProfiles == null)
			itemProfiles = MemProfiles.createEmpty();
		else
			itemProfiles.clear();
		Set<Integer> itemIds = itemRatingMap.keySet();
		for (int itemId : itemIds) {
			ExternalRecord itemExternalRecord = getItemExternalRecord(itemId);
			if (itemExternalRecord != null)
				externalItemRecordMap.put(itemId, itemExternalRecord);
			
			Profile itemProfile = getItemProfile(itemId);
			if (itemProfile != null)
				itemProfiles.put(itemId, itemProfile);
		}
		
		loaded = true;
	}
	
	
	@Override
	public ExternalRecord getUserExternalRecord(int userId) {
		// TODO Auto-generated method stub
		if (!loaded)
			return super.getUserExternalRecord(userId);
		else
			return externalUserRecordMap.get(userId);
	}

	
	@Override
	public ExternalRecord getItemExternalRecord(int itemId) {
		// TODO Auto-generated method stub
		if (!loaded)
			return super.getItemExternalRecord(itemId);
		else
			return externalItemRecordMap.get(itemId);
	}
	

	@Override
	public Profile getItemProfile(int itemId) {
		if (!loaded)
			return super.getItemProfile(itemId);
		else
			return itemProfiles.get(itemId);
	}

	
	@Override
	public Fetcher<Profile> fetchItemProfiles() {
		if (!loaded)
			return super.fetchItemProfiles();
		else
			return itemProfiles.fetch();
	}

	
	@Override
	public AttributeList getItemAttributes() {
		if (!loaded)
			return getItemAttributes();
		else
			return itemProfiles.getAttributes();
	}

	
	@Override
	public void clear() {
		super.clear();
		
		externalUserRecordMap.clear();
		externalItemRecordMap.clear();
		itemProfiles.clear();
	}
	
	
	
}
