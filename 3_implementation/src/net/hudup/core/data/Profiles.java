/**
 * 
 */
package net.hudup.core.data;

import net.hudup.core.Cloneable;
import net.hudup.core.Transfer;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;

/**
 * This interface represents a collection of profiles. Recall that each profile is represented by {@link Profile} class.
 * As a convention, this interface is called {@code profiles} or {@code table} or {@code profile list}.
 * This interface provides methods to retrieve profile (s) and additional information.
 * Any class that implements this interface needs to have a built-in variable containing {@link Profile} (s).
 * Note that {@code Profile} is one of important data structures, like record of table in database, has a list of values. Each value belongs to a particular attribute.
 * Profile uses attributes to specify its data types.
 * 
 * @author Loc Nguyen
 * @version 10.0
 * 
 */
public interface Profiles extends Cloneable, Transfer {

	
	/**
	 * Retrieving fetcher of profile identifications (IDs). Note that {@link Fetcher} is the interface for iterating each item of an associated collection.
	 * Programmers use the returned fetcher to iterating all profile ID (s).
	 * @return Fetcher of profile identifications (IDs). 
	 */
	Fetcher<Integer> fetchIds();
	
	
	/**
	 * Clearing profiles, which means that all profile (s) are removed from this profile list.
	 */
	void clear();
	
	
	/**
	 * Getting the profile having specified identification (ID).
	 * @param id Specified identification (ID).
	 * @return The profile having specified identification (ID).
	 */
	Profile get(int id);
	
	
	/**
	 * Retrieving fetcher of profile (s). Note that {@link Fetcher} is the interface for iterating each item of an associated collection.
	 * Programmers use the returned fetcher to iterating all profile (s).
	 * @return Fetcher of profiles.
	 */
	Fetcher<Profile> fetch();
	
	
	/**
	 * Testing whether or not this profile list contains the profile specified by the given identification (ID).
	 * @param id Specified identification (ID).
	 * @return Whether or not this profile list contains the profile specified by the given identification (ID).
	 */
	boolean contains(int id);
	
	
	/**
	 * Each profile is always has a reference to a attribute list represented by {@link AttributeList}.
	 * By the best way, all profiles in the profile list should share the same attribute list.
	 * This method returns such attribute list.
	 * @return The attribute list in common used among profiles in this profile list.  
	 */
	AttributeList getAttributes();
	
	
	/**
	 * Getting the size of this profile list, which is the number of profile available in {@link Profiles}.
	 * @return The size of this profile list.
	 */
	int size();
	
	
	@Override
	Object clone();
	
	
}
