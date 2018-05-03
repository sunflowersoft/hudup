package net.hudup.core.data;

import net.hudup.core.alg.Alg;
import net.hudup.core.logistic.ui.ProgressListener;


/**
 * {@link Alg} is the most abstract interface for all algorithms.
 * {@link Alg} is one of the most important interfaces.
 * It declares the most basic methods in an algorithm.
 * The most important thing is that every {@link Alg} owns a configuration specified by {@link DataConfig} class.
 * <code>Alg</code> has five typical inherited interfaces as follows:
 * <ul>
 * <li>Recommendation algorithm {@code Recommender}.</li> 
 * <li>Context template schema manager {@code CTSManager}.</li> 
 * <li>Data set parser {@code DatasetParser}.</li>
 * <li>External query {@link ExternalQuery}.</li>
 * <li>Metric {@code Metric}.</li>
 * </ul>
 * Therefore, this class is also an algorithm and it is discovered and registered in plug-in storage by plug-in manager.
 * External query is mainly used to retrieve external information stored in outside database different from Hudup database.
 * In current implementation, external query retrieve (queries) external information about user and item specified by {@link ExternalUserInfo} and {@link ExternalItemInfo}, respectively.
 * Moreover, external query can fill in Hudup database by importing data from outside database.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface ExternalQuery extends AutoCloseable, Alg {

	
	/**
	 * Setting up (initializing) this external query by both internal configuration and external configuration.
	 * @param internalConfig internal configuration to describe Hudup database.
	 * @param externalConfig external configuration to describe outside database.
	 * @return whether setting up successfully.
	 */
	boolean setup(
			DataConfig internalConfig, 
			ExternalConfig externalConfig);
	
	
	/**
	 * Getting external item information from internal item identifier.
	 * 
	 * @param itemId internal item id.
	 * @return {@link ExternalItemInfo} stored in outside database.
	 */
	ExternalItemInfo getItemInfo(int itemId);
	
	
	/**
	 * Getting external user information from internal user identifier.
	 * 
	 * @param userId internal user id.
	 * @return {@link ExternalUserInfo} stored in outside database.
	 */
	ExternalUserInfo getUserInfo(int userId);
	
	
	/**
	 * Fill in Hudup database by importing data from outside (external database) database.
	 * The Hudup database (internal database) is described by internal configuration mentioned in {@link #setup(DataConfig, ExternalConfig)}.
	 * The outside database (external database) is described by external configuration mentioned in {@link #setup(DataConfig, ExternalConfig)}.
	 * @param registeredListener the registered {@link ProgressListener} to observe the importing process. This listener can do some particular tasks such as showing the progress bar so that users know the progress of importing process.
	 */
	void importData(ProgressListener registeredListener);
	
}
