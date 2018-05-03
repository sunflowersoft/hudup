package net.hudup.core.data.ctx;

import java.awt.Component;
import java.io.Serializable;

import net.hudup.core.alg.Alg;
import net.hudup.core.data.AutoCloseable;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Profile;
import net.hudup.core.data.Profiles;

/**
 * This is a very important class which is responsible for managing contexts ({@link Context}) and context templates ({@link ContextTemplate}).
 * In other words, {@link CTSManager} manipulates context template schema {@link ContextTemplateSchema} and its instances.
 * Therefore, this class has a full name &quot;context template schema manager&quot;.
 * As a convention, it is called shortly {@code CTS manager} or {@code schema manager}. Context template schema is called shortly {@code schema}.
 * {@code CTS manager} also manages how to load (read) and save (write) {@code schema}.
 * <br>
 * Note, context template is template for a context, represented by the class {@code ContextTemplate}.
 * {@code Context} includes time, location, companion, etc. associated with a user when she/he is buying, studying, etc.
 * For example, <i>Time</i> is the template for time context <i>31/10/2015 6:45</i>.
 * In other words, context is an instance of context template with specific value.
 * A set of context templates is structured as the model which is called <i>context template schema</i> represented by {@code ContextTemplateSchema} interface.
 * CTS manager extends {@link Alg} interface and therefore, it is registered in system register table and identified by its name.
 * In other words, CTS manager is an algorithm. Plugin manager presented by {@code PluginManager} interface discovers automatically it via its name at the booting time.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface CTSManager extends Alg, AutoCloseable {

	
	/**
	 * Setting up (initializing) CTS manager via specified configuration.
	 * @param config specified configuration.
	 */
	void setup(DataConfig config);
	
	
	/**
	 * Retrieving the context template schema that CTS manager manages.
	 * @return context template schema that CTS manager manages.
	 */
	ContextTemplateSchema getCTSchema();
	
	
	/**
	 * Creating the unit for storing context templates. Unit can be a CSV file, a database table, etc.
	 * @return whether creating context template successfully
	 */
	boolean createContextTemplateUnit();

		
	/**
	 * Reloading context template schema.
	 */
	public void reload();
	
	
	/**
	 * Creating a context from the specified template ID and the specified context value.
	 * @param ctxTemplateId specified context template identification (ID).
	 * @param assignedValue specified value that is assigned to the returned context. Such value must be serializable.
	 * @return context created from the specified template ID and the specified value. 
	 */
	Context createContext(
			int ctxTemplateId, 
			Serializable assignedValue);
	
	
	/**
	 * Retrieving a list of contexts relevant to a specified user and a specified item.
	 * @param userId specified user identification (user ID).
	 * @param itemId specified item identification (item ID).
	 * @return context list represented by {@link ContextList} relevant to a specified user and a specified item.
	 */
	ContextList getContexts(int userId, int itemId);

	
	/**
	 * Getting {@link Profile} of each template with regard to its value {@link ContextValue}.
	 * The specified context contains such template and such value.
	 * This method is the same to {@link #profileOf(int, ContextValue)}
	 * @param context specified context.
	 * @return profile of the specified context.
	 */
	Profile profileOf(Context context);

	
	/**
	 * Getting {@link Profile} of each template with regard to its value {@link ContextValue}.
	 * Every template owns a profile table and each value {@link ContextValue} of template corresponds to a row in this table.
	 * Moreover such value is id of such row.
	 * 
	 * @param ctxTemplateId specified template identification (ID).
	 * @param ctxValue specified context value.
	 * @return profile of each template with regard to its value.
	 */
	Profile profileOf(int ctxTemplateId, ContextValue ctxValue);

	
	/**
	 * Retrieving all profiles associating with the same template identification (ID).
	 * 
	 * @param ctxTemplateId specified template ID.
	 * @return all profiles associating with the same template identification (ID).
	 */
	Profiles profilesOf(int ctxTemplateId);
	
	
	/**
	 * Every template owns a profile table and each value {@link ContextValue} of template corresponds to a row in this table.
	 * Each row in such template profile table is represented by {@link Profile} class.
	 * This method retrieves all profiles of all templates represented by {@link CTSMultiProfiles} class.
	 * @return all profiles of all templates, represented by {@link CTSMultiProfiles} class.
	 */
	CTSMultiProfiles createCTSProfiles();
	
	
	/**
	 * This method commits that the context template schema that {@code CTS manager} manages is valid in both memory and archive (file).
	 * @return whether the commitment is successful.
	 */
	boolean commitCTSchema();
	
	
	/**
	 * Importing context template schema from other CTS manager.
	 * @param ctsm other CTS manager.
	 * @return whether import successfully.
	 */
	boolean importCTSchema(CTSManager ctsm);
	
	
	/**
	 * Importing context template schema from other dataset.
	 * @param dataset other dataset represented by {@link Dataset} class.
	 * @return whether import successfully
	 */
	boolean importCTSchema(Dataset dataset);

	
	/**
	 * Initializing the internal context template schema and committing such schema.
	 */
	void defaultCTSchema();
	
	
	/**
	 * Showing a graphic user interface (GUI) for controlling CTS manager. Such GUI is called CTS control panel.
	 * @param comp the component working as a parent of such CTS control panel.
	 */
	void controlPanel(Component comp);
}
