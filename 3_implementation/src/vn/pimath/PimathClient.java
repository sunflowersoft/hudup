package vn.pimath;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.client.DriverManager;
import net.hudup.core.client.Service;
import net.hudup.core.data.AutoCloseable;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.ExternalRecord;
import net.hudup.core.data.ObjectPair;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class PimathClient implements AutoCloseable {

	
	/**
	 * 
	 */
	public static final int MAX_RECOMMEND = 10;
	
	
	/**
	 * 
	 */
	protected Service service = null;
	
	
	/**
	 * 
	 */
	public PimathClient() {
		
	}
	
	
	/**
	 * 
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 * @return whether connect successfully
	 */
	public boolean connect(String host, int port, String username, String password) {
		service = DriverManager.getRemoteService(host, port, username, password);
		return service != null;
	}
	
	
	/**
	 * 
	 * @param userId
	 * @param exerciseId
	 * @param exerciseLevel
	 * @return recommended list
	 */
	public List<ObjectPair<String>> recommend(String userId, String exerciseId, final float exerciseLevel) {
		List<ObjectPair<String>> recommendedList = Util.newList();
		
		RatingVector vRating = null;
		try {
			Profile userProfile = service.getUserProfileByExternal(userId);
			if (userProfile == null)
				throw new Exception("User not exist");
			
			RecommendParam param = new RecommendParam(userProfile.getIdValueAsInt());
			Profile itemProfile = service.getItemProfileByExternal(exerciseId);
			if (itemProfile != null && itemProfile.getValueAsInt(DataConfig.ITEM_TYPE_FIELD) != -1) {
				param.extra = itemProfile.getValueAsInt(DataConfig.ITEM_TYPE_FIELD);
			}
			
			vRating = service.recommend(param, 0);
		}
		catch (Throwable e) {
			e.printStackTrace();
			vRating = null;
		}
		
		if (vRating == null || vRating.size() == 0)
			return recommendedList;
		
		Set<Integer> fieldIds = vRating.fieldIds(true);
		for (int fieldId : fieldIds) {
			try {
				ExternalRecord record = service.getItemExternalRecord(fieldId);
				if (record == null)
					continue;
				
				ObjectPair<String> pair = new ObjectPair<String>(
						record.value.toString(), vRating.get(fieldId).value);
				recommendedList.add(pair);
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		Comparator<ObjectPair<String>> comparator = new Comparator<ObjectPair<String>>() {
			
			@Override
			public int compare(ObjectPair<String> pair1, ObjectPair<String> pair2) {
				// Note: best for green fall
				double dev1 = Math.abs(pair1.value() - exerciseLevel);
				double dev2 = Math.abs(pair2.value() - exerciseLevel);
				
				if (dev1 < dev2)
					return -1;
				else if (dev1 == dev2)
					return 0;
				else
					return 1;
			}
		};

		Collections.sort(recommendedList, comparator);
		
		return recommendedList.subList(0, Math.min(MAX_RECOMMEND, recommendedList.size()));
	}


	/**
	 * 
	 * @param userId
	 * @param assessmentId
	 * @return recommended list
	 */
	public List<ObjectPair<String>> recommend(String userId, String assessmentId) {
		List<ObjectPair<String>> recommendedList = Util.newList();
		
		RatingVector vRating = null;
		try {
			Profile userProfile = service.getUserProfileByExternal(userId);
			if (userProfile == null)
				throw new Exception("User not exist");
			
			RecommendParam param = new RecommendParam(userProfile.getIdValueAsInt());
			param.extra = Integer.parseInt(assessmentId);
			
			vRating = service.recommend(param, MAX_RECOMMEND);
		}
		catch (Throwable e) {
			e.printStackTrace();
			vRating = null;
		}
		
		if (vRating == null || vRating.size() == 0)
			return recommendedList;
		
		Set<Integer> fieldIds = vRating.fieldIds(true);
		for (int fieldId : fieldIds) {
			try {
				ExternalRecord record = service.getItemExternalRecord(fieldId);
				if (record == null)
					continue;
				
				ObjectPair<String> pair = new ObjectPair<String>(
						record.value.toString(), vRating.get(fieldId).value);
				recommendedList.add(pair);
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		Comparator<ObjectPair<String>> comparator = new Comparator<ObjectPair<String>>() {
			
			@Override
			public int compare(ObjectPair<String> pair1, ObjectPair<String> pair2) {
				// Note: best for green fall
				double value1 = pair1.value();
				double value2 = pair2.value();
				
				if (value1 < value2)
					return 1;
				else if (value1 == value2)
					return 0;
				else
					return -1;
			}
		};

		Collections.sort(recommendedList, comparator);
		
		return recommendedList.subList(0, Math.min(MAX_RECOMMEND, recommendedList.size()));
	}
	
	
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		service = null;
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
