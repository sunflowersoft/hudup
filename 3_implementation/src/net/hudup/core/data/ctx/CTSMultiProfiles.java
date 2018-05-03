package net.hudup.core.data.ctx;

import java.io.Serializable;
import java.util.Set;

import net.hudup.core.Cloneable;
import net.hudup.core.Transfer;
import net.hudup.core.data.Profile;
import net.hudup.core.data.Profiles;


/**
 * Every context template represented by {@code ContextTemplate} interface owns a profile table and each value represented by {@link ContextValue} of template corresponds to a row in this table.
 * Each row in such template profile table is represented by {@link Profile} class.
 * A class that implements this interface {@code CTSMultiProfiles} contains all profiles of all templates.
 * Note that these templates belong to a context template schema represented {@code ContextTemplateSchema} interface.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface CTSMultiProfiles extends Serializable, Cloneable, Transfer {

	
	/**
	 * Getting the size of {@code CTSMultiProfiles}, which is the number of template profiles.
	 * @return size of multiple profiles
	 */
	int size();
	
	
	/**
	 * Checking whether this {@code CTSMultiProfiles} contains profiles of the template having specified ID.
	 * @param ctxTemplateId specified template ID.
	 * @return whether containing profiles of the template having specified ID.
	 */
	boolean contains(int ctxTemplateId);
	
	
	/**
	 * Retrieving all profiles of the same template having specified ID.
	 * @param ctxTemplateId specified template ID.
	 * @return all profiles of the same template having specified ID.
	 */
	Profiles get(int ctxTemplateId);
	
	
	/**
	 * Putting (saving) the list of profiles associated with the same template having specified ID into {@code CTSMultiProfiles}.
	 * @param ctxTemplateId specified template ID.
	 * @param profiles list of profiles associated with the same template having specified ID, which are put (saved) into {@code CTSMultiProfiles}.
	 */
	void put(int ctxTemplateId, Profiles profiles);
	
	
	/**
	 * Retrieving all template identifications (IDs) in {@code CTSMultiProfiles}.
	 * @return set of template ID (s) in {@code CTSMultiProfiles}..
	 */
	Set<Integer> templateIds();
	
	
	/**
	 * Clearing this {@code CTSMultiProfiles}, which means that removing all profiles from this {@code CTSMultiProfiles}. 
	 */
	void clear();
	
	
	/**
	 * Getting profile of the template having specified ID and specified value.
	 * 
	 * @param ctxTemplateId specified template ID.
	 * @param ctxValue specified value.
	 * @return profile of the template having specified ID and specified value.
	 */
	Profile profileOf(int ctxTemplateId, ContextValue ctxValue);
	
	
	/**
	 * Getting profile of the template having specified ID and specified value.
	 * Such ID and value are stored in the specified context.
	 * 
	 * @param context specified context having specified ID and specified value.
	 * @return profile of the template having specified ID and specified value.
	 */
	Profile profileOf(Context context);
	
	
}
