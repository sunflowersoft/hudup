/**
 * 
 */
package net.hudup.core.data;

import java.io.Serializable;

import net.hudup.core.Cloneable;
import net.hudup.core.data.ctx.Context;
import net.hudup.core.data.ctx.ContextList;
import net.hudup.core.data.ctx.ContextTemplate;
import net.hudup.core.data.ctx.ContextTemplateSchema;


/**
 * Interface of recommendation database which is called conventionally <i>dataset</i>.
 * {@link Dataset} is one of the most important interfaces.
 * {@code Dataset} includes essentially rating matrix, user profiles, item profiles and context information.
 * There are two typical {@code Dataset} such as {@link Snapshot} and {@link Scanner}.
 * {@link Snapshot} or scanner is defined as an image of piece of {@code Dataset} and knowledge base ({@code KBase}) at certain time point.
 * This image is stored in share memory for fast access because it takes long time to access data and knowledge stored in hard disk.
 * The difference between {@code Snapshot} and {@code Scanner} that {@code Snapshot} copies whole piece of data into memory while scanner is merely a reference to such data piece.
 * {@code Snapshot} consumes much more memory but gives faster access and is more convenient.
 * {@code Snapshot} and {@code Scanner} are read-only objects because they provide only read operator.<br>
 * Another additional {@code Dataset} is {@link Pointer}.
 * {@code Pointer} does not contain any information nor provide any access means to dataset.
 * It only points to another {@code Snapshot} or {@code Scanner}.<br>
 * Although you can create your own {@code Dataset}, {@code Dataset} is often retrieved from utility class parsers that implement interface {@code DatasetParser}.
 * {@code Snapshot} is retrieved from {@code SnapshotParser}.
 * {@code Scanner} is retrieved from {@code ScannerParser}. Both {@code SnapshotParser} and {@code ScannerParser} implement interface {@code DatasetParser}.
 * {@code Pointer} is retrieved from {@code Indicator}. {@code Indicator} is {@code DatasetParser} specified to create {@code Pointer}.
 * <br><br>
 * Within Hudup framework, {@code Data layer} is responsible for manipulating recommendation data organized into two following formats:
 * <ul>
 * <li>
 * Low-level format is structured as rating matrix whose each row consists of user ratings on items, often called raw data.
 * Another low-level format is {@code Dataset} which consists of rating matrix and other information such as user profile, item profile and contextual information.
 * {@code Dataset} can be considered as intermediate format when it is modeled as complex and independent entity. {@code Dataset} is the most popular format.
 * </li>
 * <li>
 * High-level format contains fine-grained information and knowledge extracted from raw data and {@code Dataset}, for example, user interests and user purchasing pattern;
 * besides, it may have internal inference mechanism which allows us to deduce new knowledge. High-level format structure is called {@code knowledge base} or {@code KBase} in short.
 * {@code KBase} is less popular than {@code Dataset} because it is only used by recommendation algorithms while {@code Dataset} is exploited widely.
 * </li>
 * </ul>
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface Dataset extends Cloneable {

	
	/**
	 * Getting configuration of rating data.
	 * @return Configuration of rating data {@link DataConfig}
	 */
	DataConfig getConfig();
	
	
	/**
	 * Setting configuration of rating data.
	 * @param config Configuration of rating data {@link DataConfig}
	 */
	void setConfig(DataConfig config);
	
	
	/**
	 * 
	 * Retrieving collection of user ID (s) via {@link Fetcher}.
	 * @return {@link Fetcher} of user ID (s).
	 */
	Fetcher<Integer> fetchUserIds();


	/**
	 * In the case that a user ID is associated with an external user ID stored in external database,
	 * this function returns the user ID of specified external user ID.
	 * @param externalUserId External user ID stored in external database
	 * @return The user ID associated with the specified external user ID
	 */
	int getUserId(Serializable externalUserId);
	
	
	/**
	 * Retrieving external user ID associated with the specified user ID stored in this {@code Dataset}.
	 * In the case that {@code Dataset} is linked to an external database,
	 * each user ID in {@code Dataset} has different ID in such external database.
	 * @param userId specified internal user identifier.
	 * @return The external record {@link ExternalRecord} that contains the external user ID
	 */
	ExternalRecord getUserExternalRecord(int userId);
	
	
	/**
	 * Retrieving collection of item ID (s) via {@link Fetcher}.
	 * @return {@link Fetcher} of item ID (s)
	 */
	Fetcher<Integer> fetchItemIds();

	
	/**
	 * 
	 * In the case that an item ID is associated with an external item ID stored in external database,
	 * this function returns the item ID of specified external item ID.
	 * @param externalItemId External item ID stored in external database
	 * @return The item ID associated with the specified external item ID
	 */
	int getItemId(Serializable externalItemId);
	
	
	/**
	 * 
	 * Retrieving external item ID associated with the specified item ID stored in this {@code Dataset}.
	 * In the case that {@code Dataset} is linked to an external database,
	 * each item ID in {@code Dataset} has different ID in such external database.
	 * @param itemId specified internal item identifier.
	 * @return The external record {@link ExternalRecord} that contains the external item ID
	 */
	ExternalRecord getItemExternalRecord(int itemId);
	
	
	/**
	 * Getting {@link Rating} of specified user ID and item ID.
	 * {@link Rating} includes rating value, rated date and context information.
	 * @param userId Specified user ID
	 * @param itemId Specified item ID
	 * @return {@link Rating} of specified user ID and item ID includes rating value, rated date and context information
	 */
	Rating getRating(int userId, int itemId);
	
	
	/**
	 * Getting rating vector {@link RatingVector} of specified user ID.
	 * @param userId Specified user ID
	 * @return {@link RatingVector} of specified user ID
	 */
	RatingVector getUserRating(int userId);
	
	
	/**
	 * Retrieving collection of user {@link RatingVector} (s) via {@link Fetcher}.
	 * @return {@link Fetcher} of user {@link RatingVector}
	 */
	Fetcher<RatingVector> fetchUserRatings();
	
	
	/**
	 * Getting rating vector {@link RatingVector} of specified item ID.
	 * @param itemId Specified item ID
	 * @return {@link RatingVector} of specified item ID
	 */
	RatingVector getItemRating(int itemId);
	
	
	/**
	 * Retrieving collection of item {@link RatingVector} (s) via {@link Fetcher}.
	 * @return {@link Fetcher} of item {@link RatingVector}
	 */
	Fetcher<RatingVector> fetchItemRatings();
	
	
	/**
	 * Creating the user-based rating matrix whose each row represents ratings that a concrete user gives to many items.
	 * Please be careful to use this method in case of huge {@code Dataset} because such matrix is stored in memory.
	 * @return User-based rating matrix via {@link RatingMatrix}
	 */
	RatingMatrix createUserMatrix();
	
	
	/**
	 * Creating the item-based rating matrix whose each row represents ratings that a concrete item receives from many users.
	 * Please be careful to use this method in case of huge {@code Dataset} because such matrix is stored in memory.
	 * @return Item-based rating matrix via {@link RatingMatrix}
	 */
	RatingMatrix createItemMatrix();
	
	
	/**
	 * Getting {@link Profile} of specified user ID.
	 * {@link Profile} contains associative information of a given subject: user, item, context, etc.
	 * For example, {@link Profile} of user ID 1 can contains: John, male, john at mail.com
	 * @param userId Specified user ID
	 * @return {@link Profile} of specified user ID
	 */
	Profile getUserProfile(int userId);

	
	/**
	 * Retrieving collection of user {@link Profile} (s) via {@link Fetcher}.
	 * {@link Profile} contains associative information of a given subject: user, item, context, etc.
	 * For example, {@link Profile} of user ID 1 can contains: John, male, john at mail.com.
	 * @return {@link Fetcher} of user {@link Profile} (s)
	 */
	Fetcher<Profile> fetchUserProfiles();
	
	
	/**
	 * Getting list of user attributes via {@link AttributeList}.
	 * For example, user attributes can include user name, gender, email address, etc.
	 * @return {@link AttributeList} represents list of user attributes
	 */
	AttributeList getUserAttributes();

	
	/**
	 * Getting {@link Profile} of specified item ID.
	 * {@link Profile} contains associative information of a given subject: user, item, context, etc.
	 * For example, {@link Profile} of item ID 1 can contains: Computer COM1, CPU Intel, 2GB RAM.
	 * @param itemId Specified item ID
	 * @return {@link Profile} of specified item ID
	 */
	Profile getItemProfile (int itemId);
	
	
	/**
	 * Retrieving collection of item {@link Profile} (s) via {@link Fetcher}.
	 * {@link Profile} contains associative information of a given subject: user, item, context, etc.
	 * For example, {@link Profile} of item ID 1 can contains: Computer COM1, CPU Intel, 2GB RAM.
	 * @return {@link Fetcher} of item {@link Profile} (s)
	 */
	Fetcher<Profile> fetchItemProfiles();

	
	/**
	 * Getting list of item attributes via {@link AttributeList}.
	 * For example, item attributes can include item name, serial number, type, price, etc.
	 * @return {@link AttributeList} represents list of item attributes
	 */
	AttributeList getItemAttributes();

	
	/**
	 * Getting profile of the specified {@link Context}.
	 * {@link Context} represents time, location, companion associated with a user when she/he is buying, studying, etc.
	 * {@link Context} is an instance of context template {@link ContextTemplate} with specific value.
	 * @param context Specified {@link Context}
	 * @return {@link Profile} of specified {@link Context}
	 */
	Profile profileOf(Context context);
	
	
	/**
	 * Retrieving {@link Profile} (s) collection of specified context template ID via {@link Profiles}.
	 * Please see {@link ContextTemplate} and {@link ContextTemplateSchema} for more details of context template.
	 * @param ctxTemplateId Specified context template ID
	 * @return {@link Profiles} represents {@link Profile} collection of specified context template ID
	 */
	Profiles profilesOf(int ctxTemplateId);

	
	@Override
	Object clone();
	
	
	/**
	 * Retrieving the {@link Snapshot} of this {@code Dataset}.
	 * If this {@code Dataset} is {@link Snapshot}, the method returns this {@code Dataset}. 
	 * @return {@link Snapshot} created from this {@code Dataset}
	 */
	Dataset catchup();
	
	
	/**
	 * Creating the sub {@code Dataset} (filtered {@code Dataset}) of this {@code Dataset} by selecting ratings whose contexts are in the specified context list.
	 * {@link Context} represents time, location, companion associated with a user when she/he is buying, studying, etc.
	 * Please see {@link Context} and {@link ContextList} for more details of context and context list.
	 * @param contexts Specified context list
	 * @return Sub {@code Dataset} whose contexts are in the specified context list
	 */
	Dataset selectByContexts(ContextList contexts);
	
	
	/**
	 * Checking whether or not this {@code Dataset} is exclusive.
	 * {@code Dataset} is often loaded and feed to the {@code Recommender} algorithm via method {@code Recommender#setup(Dataset, Object...)}.
	 * If this {@code Dataset} is in exclusive mode, it is clear (method {@link #clear()} is called)
	 * when method {@code Recommender#unsetup()} is called ({@code Recommender} algorithm is stopped).
	 * @return Whether or not this {@code Dataset} is exclusive
	 */
	boolean isExclusive();
	
	
	/**
	 * Setting whether or not this {@code Dataset} is exclusive.
	 * {@code Dataset} is often loaded and feed to the {@code Recommender} algorithm via method {@code Recommender#setup(Dataset, Object...)}.
	 * If this {@code Dataset} is in exclusive mode, it is clear (method {@link #clear()} is called)
	 * when method {@code Recommender#unsetup()} is called ({@code Recommender} algorithm is stopped).
	 * @param exclusive indicates whether or not this {@code Dataset} is exclusive
	 */
	void setExclusive(boolean exclusive);

	
	/**
	 * Getting schema of context template {@link ContextTemplate}.
	 * {@link ContextTemplate} is template for a context represented by the class {@link Context}.
	 * {@link Context} includes time, location, companion, etc. associated with a user when she/he is buying, studying, etc.
	 * For example, <i>Time</i> is the template for time context <i>31/10/2015 6:45</i>.
	 * In other words, context is an instance of context template with specific value.
	 * A set of context templates is structured as the model which is called <i>context template schema</i> represented by interface {@link ContextTemplateSchema}.
	 * Please see {@link Context}, {@link ContextTemplate} and {@link ContextTemplateSchema} for more details of context, context template and context template schema.
	 * @return {@link ContextTemplateSchema}
	 */
	ContextTemplateSchema getCTSchema();
	
	
	/**
	 * Clearing this {@code Dataset}, deallocating memory of {@code Dataset}.
	 */
    void clear();
}
