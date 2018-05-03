package net.hudup.data;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetAssoc;
import net.hudup.core.data.ExternalRecord;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.FetcherUtil;
import net.hudup.core.data.InterchangeAttributeMap;
import net.hudup.core.data.Profile;
import net.hudup.core.data.Profiles;
import net.hudup.core.data.Provider;
import net.hudup.core.data.Rating;
import net.hudup.core.data.RatingMatrix;
import net.hudup.core.data.RatingVector;
import net.hudup.core.data.Scanner;
import net.hudup.core.data.ctx.Context;
import net.hudup.core.data.ctx.ContextList;
import net.hudup.core.data.ctx.ContextTemplateSchema;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ScannerImpl extends Scanner {


	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */
	protected Provider provider = null;
	
	
	/**
	 * 
	 */
	protected AttributeList userAttributes = null;
	
	
	/**
	 * 
	 */
	protected AttributeList itemAttributes = null;;
	
	
	/**
	 * 
	 * @param config
	 */
	public ScannerImpl(DataConfig config) {
		this (new ProviderImpl(config));
	}
	
	
	/**
	 * 
	 * @param provider
	 */
	protected ScannerImpl(Provider provider) {
		this.provider = provider;
		this.userAttributes = provider.getUserAttributes();
		this.itemAttributes = provider.getItemAttributes();
		
		this.config = provider.getConfig();
		
	}
	
	
	@Override
	public Fetcher<Integer> fetchUserIds() {
		// TODO Auto-generated method stub
		
		return provider.getProfileIds(config.getUserUnit());
	}

	
	@Override
	public int getUserId(Serializable externalUserId) {
		// TODO Auto-generated method stub
		Fetcher<Integer> userIds = fetchUserIds();
		int returnUserId = -1;
		try {
			while (userIds.next()) {
				Integer userId = userIds.pick();
				if (userId == null || userId < 0)
					continue;
				
				ExternalRecord record = getUserExternalRecord(userId);
				if (record != null && record.value.equals(externalUserId)) {
					returnUserId = userId;
					break;
				}
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
			returnUserId = -1;
		}
		finally {
			try {
				userIds.close();
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return returnUserId;
	}


	@Override
	public ExternalRecord getUserExternalRecord(int userId) {
		// TODO Auto-generated method stub
		
		InterchangeAttributeMap attributeMap = provider.getUserAttributeMap(userId);
		if (attributeMap != null)
			return attributeMap.externalRecord;
		else
			return null;
		
	}


	@Override
	public Fetcher<Integer> fetchItemIds() {
		// TODO Auto-generated method stub
		
		return provider.getProfileIds(config.getItemUnit());
	}

	
	@Override
	public int getItemId(Serializable externalItemId) {
		// TODO Auto-generated method stub
		Fetcher<Integer> itemIds = fetchItemIds();
		int returnItemId = -1;
		try {
			while (itemIds.next()) {
				Integer itemId = itemIds.pick();
				if (itemId == null || itemId < 0)
					continue;
				
				ExternalRecord record = getItemExternalRecord(itemId);
				if (record != null && record.value.equals(externalItemId)) {
					returnItemId = itemId;
					break;
				}
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
			returnItemId = -1;
		}
		finally {
			try {
				itemIds.close();
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return returnItemId;
	}

	
	@Override
	public ExternalRecord getItemExternalRecord(int itemId) {
		// TODO Auto-generated method stub
		
		InterchangeAttributeMap attributeMap = provider.getItemAttributeMap(itemId);
		
		if (attributeMap != null)
			return attributeMap.externalRecord;
		else
			return null;
	}


	@Override
	public Rating getRating(int userId, int itemId) {
		// TODO Auto-generated method stub
		RatingVector vRating = provider.getUserRatingVector(userId);
		if (vRating == null)
			return null;
		else
			return vRating.get(itemId);
	}

	
	@Override
	public RatingVector getUserRating(int userId) {
		// TODO Auto-generated method stub
		
		return provider.getUserRatingVector(userId);
	}

	
	@Override
	public Fetcher<RatingVector> fetchUserRatings() {
		// TODO Auto-generated method stub
		Fetcher<Integer> userIds = fetchUserIds();
		
		return new MetaFetcher<Integer, RatingVector>(userIds) {

			@Override
			public RatingVector create(Integer u) {
				// TODO Auto-generated method stub
				return getUserRating(u);
			}
			
		};
		
	}

	
	@Override
	public RatingVector getItemRating(int itemId) {
		// TODO Auto-generated method stub
		
		return provider.getItemRatingVector(itemId);
	}

	
	@Override
	public Fetcher<RatingVector> fetchItemRatings() {
		// TODO Auto-generated method stub
		Fetcher<Integer> itemIds = fetchItemIds();
		
		return new MetaFetcher<Integer, RatingVector>(itemIds) {

			@Override
			public RatingVector create(Integer u) {
				// TODO Auto-generated method stub
				return getItemRating(u);
			}
			
		};

	}

	
	@Override
	public RatingMatrix createUserMatrix() {
		return new DatasetAssoc(this).createMatrix(true);
	}


	@Override
	public RatingMatrix createItemMatrix() {
		return new DatasetAssoc(this).createMatrix(false);
	}

	
	@Override
	public Profile getUserProfile(int userId) {
		// TODO Auto-generated method stub
		
		return provider.getUserProfile(userId);
	}

	
	@Override
	public Fetcher<Profile> fetchUserProfiles() {
		// TODO Auto-generated method stub
		return provider.getProfiles(config.getUserUnit(), null);
		
	}

	
	@Override
	public AttributeList getUserAttributes() {
		// TODO Auto-generated method stub
		return userAttributes;
	}

	
	@Override
	public Profile getItemProfile(int itemId) {
		// TODO Auto-generated method stub
		
		return provider.getItemProfile(itemId);
	}

	
	@Override
	public Fetcher<Profile> fetchItemProfiles() {
		// TODO Auto-generated method stub
		return provider.getProfiles(config.getItemUnit(), null);
	}

	
	@Override
	public AttributeList getItemAttributes() {
		// TODO Auto-generated method stub
		
		return itemAttributes;
	}

	
	@Override
	public Dataset catchup() {
		// TODO Auto-generated method stub
		SnapshotImpl snapshot = new SnapshotImpl();
		
		Map<Integer, ExternalRecord> externalUserRecordMap = Util.newMap();
		Set<Integer> userIds = Util.newSet();
		FetcherUtil.fillCollection(userIds, fetchUserIds(), true);
		for (int userId : userIds) {
			ExternalRecord externalRecord = getUserExternalRecord(userId);
			if (externalRecord != null)
				externalUserRecordMap.put(userId, externalRecord);
		}
		
		Map<Integer, ExternalRecord> externalItemRecordMap = Util.newMap();
		Set<Integer> itemIds = Util.newSet();
		FetcherUtil.fillCollection(itemIds, fetchItemIds(), true);
		for (int itemId : itemIds) {
			ExternalRecord externalRecord = getItemExternalRecord(itemId);
			if (externalRecord != null)
				externalItemRecordMap.put(itemId, externalRecord);
		}

		snapshot.assign(
				(DataConfig)config.clone(),
				externalUserRecordMap,
				RatingVector.transfer(fetchUserRatings(), false, true), 
				MemProfiles.create(fetchUserProfiles(), true),
				externalItemRecordMap,
				RatingVector.transfer(fetchItemRatings(), false, true), 
				MemProfiles.create(fetchItemProfiles(), true),
				(ContextTemplateSchema) provider.getCTSManager().getCTSchema().transfer(),
				provider.getCTSManager().createCTSProfiles());

		return snapshot;
	}
	

	@Override
	public Profile profileOf(Context context) {
		// TODO Auto-generated method stub
		return provider.getCTSManager().profileOf(context);
	}


	@Override
	public Profiles profilesOf(int ctxTemplateId) {
		// TODO Auto-generated method stub
		return provider.getCTSManager().profilesOf(ctxTemplateId);
	}


	@Override
	public Dataset selectByContexts(ContextList contexts) {
		// TODO Auto-generated method stub
		
		SemiScanner semiScanner = new SemiScanner(config);
		
		semiScanner.userRatingMap = RatingVector.select(semiScanner.userRatingMap, contexts);
		semiScanner.itemRatingMap = RatingVector.select(semiScanner.itemRatingMap, contexts);
		
		logger.info("ScannerImpl@selectByContexts@557: Because context structure is complex, " +
				"it consumes a lot of redundant operations to select context via database instead of via memory. " +
				"So it isn't a perfect solution but very good in this case");
		
		return semiScanner;
	}


	@Override
	public ContextTemplateSchema getCTSchema() {
		// TODO Auto-generated method stub
		return provider.getCTSManager().getCTSchema();
	}


	@Override
	public Object clone() {
		ScannerImpl scanner = new ScannerImpl((Provider)this.provider.clone());
		return scanner;
	}
	
	
	@Override
	public Provider getProvider() {
		return provider;
	}
	
	
	@Override
	public void clear() {
		super.clear();
		
		if (provider != null) {
			provider.close();
			provider = null;
		}
		
		if (userAttributes != null) {
			userAttributes.clear();
			userAttributes = null;
		}
		
		if (itemAttributes != null) {
			itemAttributes.clear();
			itemAttributes = null;
		}
		
	}


	
	
}
