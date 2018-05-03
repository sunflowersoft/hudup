package net.hudup.server;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.Recommender;
import net.hudup.core.client.ServerInfo;
import net.hudup.core.client.Service;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.AutoCloseable;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetMetadata;
import net.hudup.core.data.ExternalRecord;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.InterchangeAttributeMap;
import net.hudup.core.data.InternalRecord;
import net.hudup.core.data.Nominal;
import net.hudup.core.data.NominalList;
import net.hudup.core.data.Profile;
import net.hudup.core.data.Provider;
import net.hudup.core.data.Rating;
import net.hudup.core.data.RatingVector;
import net.hudup.core.data.Scanner;
import net.hudup.core.data.Snapshot;
import net.hudup.data.MemFetcher;
import net.hudup.data.SnapshotImpl;

import org.apache.log4j.Logger;


/**
 * {@link DefaultService} class is default implementation of {@link Service} interface. {@link DefaultService} uses {@link Recommender} and {@link Provider} for processing recommendation request and update request, respectively.
 * Developers are suggested to build up their own services.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class DefaultService implements Service, AutoCloseable {


	/**
	 * Logger of this class.
	 */
	protected final static Logger logger = Logger.getLogger(Service.class);

	
	/**
	 * 
	 */
	protected Transaction trans = null;
	
	
	/**
	 * 
	 */
	protected Recommender recommender = null;
	
	
	/**
	 * 
	 * @param trans
	 */
	public DefaultService(Transaction trans) {
		super();
		// TODO Auto-generated constructor stub
		
		this.trans = trans;
	}

	
	/**
	 * 
	 * @param config
	 * @param params
	 * @return whether open service successfully
	 */
	public boolean open(PowerServerConfig config, Object... params) {
		try {
			close();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		boolean opened = true;
		try {
			Dataset dataset = config.getParser().parse((DataConfig)config.clone());
			dataset.setExclusive(true);
			
			recommender = (Recommender) config.getRecommender().newInstance();
			recommender.getConfig().putAll(config.getRecommender().getConfig());
			recommender.setup(dataset, params);
		}
		catch (Throwable e) {
			e.printStackTrace();
			close();
			opened = false;
			
			logger.error("Service fail to open, caused by " + e.getMessage());
		}
		
		return opened;
	}
	
	
	/**
	 * 
	 * @return whether service is opened
	 */
	public boolean isOpened() {
		return recommender != null;
	}
	
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		if (recommender != null)
			recommender.unsetup();
		recommender = null;
	}


	/**
	 * 
	 * @param target
	 * @return whether transfer successfully
	 */
	public boolean transfer(DefaultService target) {
		if (!isOpened())
			return false;
		
		boolean result = true;
		trans.lockWrite();
		try {
			target.close();
			target.recommender = recommender;
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = false;
			
			logger.error("Service fail to transfer to target service, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}
	
	
	/**
	 * 
	 * @return scanner inside recommender algorithm
	 */
	protected Dataset getDataset() {
		if (isOpened())
			return recommender.getDataset();
		else
			return null;
	}
	
	
	/**
	 * That provider is null means read only service.
	 * @return provider
	 */
	protected Provider getProvider() {
		Dataset dataset = getDataset();
		if (dataset == null)
			return null;
		else if (dataset instanceof Scanner)
			return ((Scanner)dataset).getProvider();
		else
			return null;
	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		// TODO Auto-generated method stub
		
		RatingVector result = null;
		
		trans.lockRead();
		try {
			result = recommender.estimate(param, queryIds);
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = null;
			
			logger.error("Service fail to estimate, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return result;
	}

	
	@Override
	public RatingVector recommend(RecommendParam param, int maxRecommend) throws RemoteException {
		// TODO Auto-generated method stub
		
		RatingVector result = null;
		
		trans.lockRead();
		try {
			result = recommender.recommend(param, maxRecommend);
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = null;
			
			logger.error("Service fail to recommend, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return result;
	}

	
	@Override
	public RatingVector recommend(int userId, int maxRecommend)
			throws RemoteException {
		// TODO Auto-generated method stub
		RatingVector result = null;
		
		trans.lockRead();
		try {
			RecommendParam param = new RecommendParam(userId);
			result = recommender.recommend(param, maxRecommend);
			param.clear();
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = null;
			
			logger.error("Service fail to recommend first time, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		if (result == null) {
			
			trans.lockRead();
			try {
				RecommendParam param = getProvider().getRecommendParam(userId);
				if (param != null) {
					result = recommender.recommend(param, maxRecommend);
					param.clear();
				}
			}
			catch (Throwable e) {
				e.printStackTrace();
				result = null;
				
				logger.error("Service fail to recommend second time, caused by " + e.getMessage());
			}
			finally {
				trans.unlockRead();
			}
		}
		
		
		return result;
	}


	@Override
	@Deprecated
	public net.hudup.core.client.Recommendlet recommend(
			String listenerHost, 
			int listenerPort,
			String regName, 
			Serializable externalUserId, 
			Serializable externalItemId,
			int maxRecommend, 
			Rating rating) throws RemoteException {
		// TODO Auto-generated method stub
		
		if (externalItemId != null && rating != null && rating.isRated()) {
			trans.lockWrite();
			try {
				int userId = getDataset().getUserId(externalUserId);
				int itemId = getDataset().getItemId(externalItemId);
				
				if (userId >= 0 && itemId >= 0)
					getProvider().updateRating(userId, itemId, rating);
			}
			catch (Throwable e) {
				e.printStackTrace();
				
				logger.error("Service fail to update rating when recommending net.hudup.core.client.Recommendlet, caused by " + e.getMessage());
			}
			finally {
				trans.unlockWrite();
			}
		}
		
		net.hudup.core.client.Recommendlet result = null;
		
		trans.lockRead();
		try {
			DataConfig cfg = getDataset().getConfig();
			
			DatasetMetadata metadata = cfg.getMetadata();
			ServerInfo listenerInfo = new ServerInfo();
			listenerInfo.setHost(listenerHost);
			listenerInfo.setPort(listenerPort);
			
			int userId = getDataset().getUserId(externalUserId);
			userId = userId < 0 ? Integer.parseInt(externalUserId.toString()) : userId; // for testing, should be removed
			if (userId >= 0) {
			
				RatingVector recommendedItems = recommend(userId, maxRecommend);
				Set<Integer> itemIds = Util.newSet();
				if (recommendedItems != null)
					itemIds.addAll(recommendedItems.fieldIds());
				
				ClientStubImpl clientStub = ClientStubImpl.create(regName, itemIds, null);
				result = new net.hudup.core.client.Recommendlet(
						externalUserId.toString(),
						externalItemId != null ? externalItemId.toString() : null, 
						recommendedItems, 
						metadata,
						listenerInfo,
						clientStub);
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = null;
			
			logger.error("Service fail to recommend net.hudup.core.client.Recommendlet, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return result;
	}


	@Override
	public boolean updateRating(RatingVector vRating) throws RemoteException {
		// TODO Auto-generated method stub
		boolean result = false;
		
		trans.lockWrite();
		try {
			result = getProvider().updateRating(vRating);
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = false;
			
			logger.error("Service fail to update rating, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}

	
	@Override
	public boolean updateRating(int userId, int itemId, Rating rating) 
			throws RemoteException {
		boolean result = false;
		
		trans.lockWrite();
		try {
			result = getProvider().updateRating(userId, itemId, rating);
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = false;
			
			logger.error("Service fail to update rating, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}
	
	
	@Override
	public boolean deleteRating(RatingVector vRating) throws RemoteException {
		boolean result = false;
		
		trans.lockWrite();
		try {
			result = getProvider().deleteRating(vRating);
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = false;
			
			logger.error("Service fail to delete rating, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}
	
	
	@Override
	public Fetcher<Integer> getUserIds() throws RemoteException {
		// TODO Auto-generated method stub
		Fetcher<Integer> fetcher = MemFetcher.createEmpty();
		
		trans.lockRead();
		try {
			
			fetcher = getDataset().fetchUserIds();
		}
		catch (Throwable e) {
			e.printStackTrace();
			fetcher = MemFetcher.createEmpty();
			
			logger.error("Service fail to get user id (s), caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return fetcher;
	}

	
	@Override
	public RatingVector getUserRating(int userId) throws RemoteException {
		RatingVector result = null;
		
		trans.lockRead();
		try {
			result = getDataset().getUserRating(userId);
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = null;
			
			logger.error("Service fail to get user rating, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return result;
	}
	
	
	@Override
	public Fetcher<RatingVector> getUserRatings() throws RemoteException {
		Fetcher<RatingVector> fetcher = MemFetcher.createEmpty();
		
		trans.lockRead();
		try {
			fetcher = getDataset().fetchUserRatings();
			
		}
		catch (Throwable e) {
			e.printStackTrace();
			fetcher = MemFetcher.createEmpty();
			
			logger.error("Service fail to get user rating (s), caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return fetcher;
	}
	
	
	@Override
	public boolean deleteUserRating(int userId) throws RemoteException {
		boolean result = false;
		
		trans.lockWrite();
		try {
			result = getProvider().deleteUserRating(userId);
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = false;
			
			logger.error("Service fail to delete user rating, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}
	
	
	@Override
	public Profile getUserProfile(int userId) throws RemoteException {
		// TODO Auto-generated method stub
		
		Profile user = null;
		
		trans.lockRead();
		try {
			user = getDataset().getUserProfile(userId);
		}
		catch (Throwable e) {
			e.printStackTrace();
			user = null;
			
			logger.error("Service fail to get user profile, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return user;
	}

	
	@Override
	public Profile getUserProfileByExternal(Serializable externalUserId)
			throws RemoteException {
		// TODO Auto-generated method stub

		Profile user = null;
		
		trans.lockRead();
		try {
			int userId = getDataset().getUserId(externalUserId);
			if (userId >= 0)
				user = getDataset().getUserProfile(userId);
		}
		catch (Throwable e) {
			e.printStackTrace();
			user = null;
			
			logger.error("Service fail to get user profile by external, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return user;
	}


	@Override
	public Fetcher<Profile> getUserProfiles() throws RemoteException {
		Fetcher<Profile> fetcher = MemFetcher.createEmpty();
		
		trans.lockRead();
		try {
			fetcher = getDataset().fetchUserProfiles();
		}
		catch (Throwable e) {
			e.printStackTrace();
			fetcher = MemFetcher.createEmpty();
			
			logger.error("Service fail to get user profiles, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return fetcher;
	}

		
	@Override
	public boolean updateUserProfile(Profile user) throws RemoteException {
		// TODO Auto-generated method stub
		
		boolean result = false;
		
		trans.lockWrite();
		try {
			result = getProvider().updateUserProfile(user);
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = false;
			
			logger.error("Service fail to update user profile, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}

	
	@Override
	public boolean deleteUserProfile(int userId) throws RemoteException {
		
		boolean result = false;
		
		trans.lockWrite();
		try {
			result = getProvider().deleteUserProfile(userId);
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = false;
			
			logger.error("Service fail to delete user profile, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}
	
	
	@Override
	public AttributeList getUserAttributeList()
			throws RemoteException {
		// TODO Auto-generated method stub
		
		AttributeList attributeList = null;
		
		trans.lockRead();
		try {
			attributeList = getDataset().getUserAttributes();
		}
		catch (Throwable e) {
			e.printStackTrace();
			attributeList = null;
			
			logger.error("Service fail to get user attribute list, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return attributeList;
	}

	
	@Override
	public ExternalRecord getUserExternalRecord(int userId)
			throws RemoteException {
		// TODO Auto-generated method stub
		
		ExternalRecord externalRecord = null;
		
		trans.lockRead();
		try {
			externalRecord = getDataset().getUserExternalRecord(userId);
		}
		catch (Throwable e) {
			e.printStackTrace();
			externalRecord = null;
			
			logger.error("Service fail to get user external record, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return externalRecord;
	}


	@Override
	public Fetcher<Integer> getItemIds() throws RemoteException {
		// TODO Auto-generated method stub
		Fetcher<Integer> fetcher = MemFetcher.createEmpty();
		
		trans.lockRead();
		try {
			fetcher = getDataset().fetchItemIds();
		}
		catch (Throwable e) {
			e.printStackTrace();
			fetcher = MemFetcher.createEmpty();
			
			logger.error("Service fail to get item id (s), caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return fetcher;
	}

	
	@Override
	public RatingVector getItemRating(int itemId) throws RemoteException {
		RatingVector result = null;
		
		trans.lockRead();
		try {
			result = getDataset().getItemRating(itemId);
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = null;
			
			logger.error("Service fail to get item rating, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return result;
	}
	
	
	@Override
	public Fetcher<RatingVector> getItemRatings() 
			throws RemoteException {
		
		Fetcher<RatingVector> fetcher = MemFetcher.createEmpty();
		
		trans.lockRead();
		try {
			fetcher = getDataset().fetchItemRatings();
			
		}
		catch (Throwable e) {
			e.printStackTrace();
			fetcher = MemFetcher.createEmpty();
			
			logger.error("Service fail to get item rating (s), caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return fetcher;
	}

		
	@Override
	public boolean deleteItemRating(int itemId) throws RemoteException {
		boolean result = false;
		
		trans.lockWrite();
		try {
			result = getProvider().deleteItemRating(itemId);
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = false;
			
			logger.error("Service fail to delete item rating, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}
	
	
	@Override
	public Profile getItemProfile(int itemId) throws RemoteException {
		// TODO Auto-generated method stub
		
		Profile item = null;
		
		trans.lockRead();
		try {
			item = getDataset().getItemProfile(itemId);
		}
		catch (Throwable e) {
			e.printStackTrace();
			item = null;
			
			logger.error("Service fail to get item profile, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return item;
	}

	
	@Override
	public Profile getItemProfileByExternal(Serializable externalItemId)
			throws RemoteException {
		// TODO Auto-generated method stub

		Profile item = null;
		
		trans.lockRead();
		try {
			int itemId = getDataset().getItemId(externalItemId);
			if (itemId >= 0)
				item = getDataset().getItemProfile(itemId);
		}
		catch (Throwable e) {
			e.printStackTrace();
			item = null;
			
			logger.error("Service fail to get item profile by external, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return item;
	}


	@Override
	public Fetcher<Profile> getItemProfiles() throws RemoteException {
		Fetcher<Profile> fetcher = MemFetcher.createEmpty();
		
		trans.lockRead();
		try {
			fetcher = getDataset().fetchItemProfiles();
		}
		catch (Throwable e) {
			e.printStackTrace();
			fetcher = MemFetcher.createEmpty();
			
			logger.error("Service fail to get item profiles, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return fetcher;
	}
	
	
	@Override
	public boolean updateItemProfile(Profile item) throws RemoteException {
		// TODO Auto-generated method stub
		
		boolean result = false;
		
		trans.lockWrite();
		try {
			result = getProvider().updateItemProfile(item);
			
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = false;
			
			logger.error("Service fail to update item profile, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}

	
	@Override
	public boolean deleteItemProfile(int itemId) throws RemoteException {
		
		boolean result = false;
		
		trans.lockWrite();
		try {
			result = getProvider().deleteItemProfile(itemId);
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = false;
			
			logger.error("Service fail to delete item profile, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}

	
	@Override
	public AttributeList getItemAttributeList()
			throws RemoteException {
		// TODO Auto-generated method stub
		
		AttributeList attributeList = null;
		
		trans.lockRead();
		try {
			attributeList = getDataset().getItemAttributes(); 
		}
		catch (Throwable e) {
			e.printStackTrace();
			attributeList = null;
			
			logger.error("Service fail to get item attribute list, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return attributeList;
	}
	
	
	@Override
	public ExternalRecord getItemExternalRecord(int itemId)
			throws RemoteException {
		// TODO Auto-generated method stub
		
		ExternalRecord externalRecord = null;
		
		trans.lockRead();
		try {
			externalRecord = getDataset().getItemExternalRecord(itemId);
		}
		catch (Throwable e) {
			e.printStackTrace();
			externalRecord = null;
			
			logger.error("Service fail to item external record, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return externalRecord;
	}

	
	@Override
	public NominalList getNominal(String unitName, String attribute) {
		// TODO Auto-generated method stub
		NominalList nominalList = null;
		
		trans.lockRead();
		try {
			nominalList = getProvider().getNominalList(unitName, attribute);
		}
		catch (Throwable e) {
			e.printStackTrace();
			nominalList = null;
			
			logger.error("Service fail to get nominal, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return nominalList;
	}


	@Override
	public boolean updateNominal(String unitName, String attribute, Nominal nominal)
			throws RemoteException {
		// TODO Auto-generated method stub
		
		boolean result = false;
		
		trans.lockWrite();
		try {
			result = getProvider().updateNominal(unitName, attribute, nominal);
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = false;
			
			logger.error("Service fail to update nominal, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}

	
	@Override
	public boolean deleteNominal(String unitName, String attribute) throws RemoteException {
		boolean result = false;
		
		trans.lockWrite();
		try {
			result = getProvider().deleteNominal(unitName, attribute);
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = false;
			
			logger.error("Service fail to delete nominal, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}
	
	
	@Override
	public ExternalRecord getExternalRecord(InternalRecord internalRecord) throws RemoteException {
		// TODO Auto-generated method stub
		
		ExternalRecord externalRecord = null;
		
		trans.lockRead();
		try {
			InterchangeAttributeMap attributeMap = getProvider().getAttributeMap(internalRecord);
			if (attributeMap != null && attributeMap.isValid())
				externalRecord = attributeMap.externalRecord;
		}
		catch (Throwable e) {
			e.printStackTrace();
			externalRecord = null;
			
			logger.error("Service fail to get external record, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return externalRecord;
	}


	@Override
	public boolean updateExternalRecord(InternalRecord internalRecord,
			ExternalRecord externalRecord) throws RemoteException {
		// TODO Auto-generated method stub
		boolean result = false;
		
		trans.lockWrite();
		try {
			result = getProvider().updateAttributeMap(
					new InterchangeAttributeMap(internalRecord, externalRecord));
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = false;
			
			logger.error("Service fail to update external record, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}


	@Override
	public boolean deleteExternalRecord(InternalRecord internalRecord) throws RemoteException {
		// TODO Auto-generated method stub
		boolean result = false;
		
		trans.lockWrite();
		try {
			result = getProvider().deleteAttributeMap(internalRecord);
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = false;
			
			logger.error("Service fail to delete external record, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}

	
	@Override
	public boolean validateAccount(String account, String password, int privileges) 
			throws RemoteException {
		// TODO Auto-generated method stub
		
		return getProvider().validateAccount(account, password, privileges);
	}

	
	@Override
	public Profile getProfile(String profileUnit, Profile condition) throws RemoteException {
		Profile profile = null;
		if (profileUnit.equals(getDataset().getConfig().getAccountUnit()))
			return profile;
		
		trans.lockRead();
		try {
			profile = getProvider().getProfile(profileUnit, condition);
		}
		catch (Throwable e) {
			e.printStackTrace();
			profile = null;
			
			logger.error("Service fail to get profile, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return profile;
	}
	
	
	@Override
	public AttributeList getProfileAttributeList(String unitName)
			throws RemoteException {
		
		AttributeList attributeList = null;
		
		trans.lockRead();
		try {
			attributeList = getProvider().getProfileAttributes(unitName);
		}
		catch (Throwable e) {
			e.printStackTrace();
			attributeList = null;
			
			logger.error("Service fail to get profile attribute list, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return attributeList;
	}

	
	@Override
	public boolean updateProfile(String profileUnit, Profile profile)  
			throws RemoteException {
		
		boolean result = false;
		if (profileUnit.equals(getDataset().getConfig().getAccountUnit()))
			return result;
		
		trans.lockWrite();
		try {
			result = getProvider().updateProfile(profileUnit, profile);
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = false;
			
			logger.error("Service fail to update profile, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
		
	}
	
	
	@Override
	public boolean deleteProfile(String profileUnit, Profile condition) 
			throws RemoteException {
		
		boolean result = false;
		if (profileUnit.equals(getDataset().getConfig().getAccountUnit()))
			return result;
		
		trans.lockWrite();
		try {
			result = getProvider().deleteProfile(profileUnit, condition);
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = false;
			
			logger.error("Service fail to delete profile, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return result;
	}

	
	@Override
	public DataConfig getServerConfig() throws RemoteException {
		// TODO Auto-generated method stub
		
		DataConfig serverConfig = null;
		
		trans.lockRead();
		try {
			serverConfig = new DataConfig();
			serverConfig.setMetadata(getDataset().getConfig().getMetadata());
		}
		catch (Throwable e) {
			e.printStackTrace();
			serverConfig = null;
			
			logger.error("Service fail to get server configuration, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return serverConfig;
	}

	
	@Override
	public Snapshot getSnapshot() throws RemoteException {
		Snapshot snapshot = null;
		
		trans.lockRead();
		try {
			DataConfig config = new DataConfig();
			Snapshot temp = (Snapshot) getDataset().catchup();
			config.setMetadata(temp.getConfig().getMetadata());
			
			snapshot = new SnapshotImpl();
			snapshot.assign(temp);
			snapshot.setConfig(config);
		}
		catch (Throwable e) {
			e.printStackTrace();
			snapshot = null;
			
			logger.error("Service fail to get snapshot, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return snapshot;
	}


	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		try {
			close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	


	
	
	
	
}
