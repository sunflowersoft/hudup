/**
 * 
 */
package net.hudup.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.data.Profiles;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class MemProfiles implements Profiles, Serializable {


	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */
	private AttributeList attList = new AttributeList();
	
	
	/**
	 * 
	 */
	private Map<Integer, Profile> profileMap = Util.newMap();
	
	
	/**
	 * 
	 */
	protected MemProfiles() {
		if ( !(attList instanceof Serializable) || 
			 !(profileMap instanceof Serializable))
			throw new RuntimeException("Not serializable class");
	}
	
	
	/**
	 * 
	 * @param attList
	 * @param profileMap
	 */
	protected MemProfiles(AttributeList attList, Map<Integer, Profile> profileMap) {
		this();
		this.attList = attList;
		this.profileMap = profileMap;
	}


	@Override
	public Fetcher<Integer> fetchIds() {
		// TODO Auto-generated method stub
		return new MemFetcher<Integer>(profileMap.keySet());
	}


	@Override
	public Profile get(int id) {
		return profileMap.get(id);
	}
	
	
	@Override
	public Fetcher<Profile> fetch() {
		return new MemFetcher<Profile>(profileMap.values());
	}
	
	
	@Override
	public boolean contains(int id) {
		return profileMap.containsKey(id);
	}
	
	
	/**
	 * 
	 * @param id
	 * @param profile
	 */
	public void put(int id, Profile profile) {
		profileMap.put(id, profile);
	}
	
	
	@Override
	public AttributeList getAttributes() {
		return attList;
	}
	
	
	@Override
	public void clear() {
		attList.clear();
		profileMap.clear();
	}
	
	
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return profileMap.size();
	}


	/**
	 * 
	 * @param profileIds
	 */
	public void fillUnion(Collection<Integer> profileIds) {
		for (int profileId : profileIds) {
			if (profileMap.containsKey(profileId))
				continue;
			
			try {
				Profile profile = new Profile(attList);
				profile.setIdValue(new Integer(profileId));
				
				profileMap.put(profileId, profile);
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
			
		}
	}
	
	
	/**
	 * 
	 * @param profileIds
	 */
	public void fillIntersec(Collection<Integer> profileIds) {
		for (int profileId : profileIds) {
			if (!profileMap.containsKey(profileId))
				profileMap.remove(profileId);
		}
	}

	
	/**
	 * 
	 * @param mainIds
	 */
	public void fillAs(Collection<Integer> mainIds) {
		Set<Integer> profileIds = Util.newSet();
		profileIds.addAll(profileMap.keySet());
		
		for (int profileId : profileIds) {
			if (!mainIds.contains(profileId))
				profileMap.remove(profileId);
		}
		
		fillUnion(mainIds);
	}

	
	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		
		AttributeList newAttList = (AttributeList)this.attList.clone();
		Map<Integer, Profile> newProfileMap = Util.newMap();
		
		Set<Integer> ids = this.profileMap.keySet();
		for (int id : ids) {
			Profile profile = this.profileMap.get(id);
			Profile newProfile = (Profile)profile.clone();
			
			newProfile.setAttRef(newAttList);
			newProfileMap.put(id, newProfile);
			
		}
		
		return MemProfiles.assign(newAttList, newProfileMap);
	}
	
	
	@Override
	public Object transfer() {
		AttributeList newAttList = (AttributeList)this.attList.transfer();
		Map<Integer, Profile> newProfileMap = Util.newMap();
		newProfileMap.putAll(this.profileMap);
		
		return MemProfiles.assign(newAttList, newProfileMap);
	}
	
	
	
	/**
	 * 
	 * @param ids
	 * @return sub {@link MemProfiles}
	 */
	public MemProfiles getSub(Collection<Integer> ids) {
		Map<Integer, Profile> newProfileMap = Util.newMap();
		for (int id : ids) {
			Profile profile = this.profileMap.get(id);
			if (profile != null)
				newProfileMap.put(id, profile);
		}
		return MemProfiles.assign(this.attList, newProfileMap);
	}
	
	
	/**
	 * 
	 */
	public void enhance() {
		profileMap.keySet();
		profileMap.values();
	}
	
	
	/**
	 * 
	 * @return empty {@link MemProfiles}
	 */
	public static MemProfiles createEmpty() {
		return new MemProfiles();
	}
	
	
	/**
	 * 
	 * @param idName
	 * @param idType
	 * @return {@link MemProfiles}
	 */
	public static MemProfiles createEmpty(String idName, Type idType) {
		Map<Integer, Profile> profileMap = Util.newMap();
		AttributeList attList = AttributeList.create(
				new Attribute[] { new Attribute(idName, idType)});
		attList.setKey(0);
		
		return MemProfiles.assign(attList, profileMap);
	}

	
	/**
	 * 
	 * @param attList
	 * @param profileMap
	 * @return {@link MemProfiles}
	 */
	public static MemProfiles assign(AttributeList attList, Map<Integer, Profile> profileMap) {
		return new MemProfiles(attList, profileMap);
	}
	

	/**
	 * 
	 * @param fetcher
	 * @param closeFetcher
	 * @return {@link MemProfiles}
	 */
	public static MemProfiles create(Fetcher<Profile> fetcher, boolean closeFetcher) {
		
		Map<Integer, Profile> profileMap = Util.newMap();
		AttributeList attList = null;
		try {
			while (fetcher.next()) {
				Profile profile = fetcher.pick();
				if (profile == null)
					continue;
				
				int profileId = profile.getIdValueAsInt();
				if (profileId >= 0) {
					attList = profile.getAttRef();
					profileMap.put(profileId, profile);
				}
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (closeFetcher)
					fetcher.close();
				else
					fetcher.reset();
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		
		if (attList == null || profileMap.size() == 0)
			return MemProfiles.createEmpty();
		else
			return new MemProfiles(attList, profileMap);
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
