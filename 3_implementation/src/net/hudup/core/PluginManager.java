package net.hudup.core;


/**
 * Plug-in manager is responsible for discovering and managing registered recommendation algorithms.
 * The class that implements {@code PluginManager} is {@code Firer}.
 * {@code Alg} is the most abstract interface for all algorithms.
 * Every algorithm has a unique name. Every algorithm is registered in system register table and identified by its name.
 * Such system register table is modeled as {@code RegisterTable} class.
 * Actually, plug-in manager discovers automatically all algorithms via their names at the booting time and requires {@link PluginStorage} to add such algorithms into register tables represented by {@link RegisterTable} classes.
 * After Hudup framework started, it is easy to retrieve any any algorithms by looking up algorithms in register tables from {@link PluginStorage}.
 * Please pay attention that system register tables are stored in a so-called {@code plug-in storage} which is represented by {@code PluginStorage} class.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface PluginManager {
	
	
	/**
	 * The main method discovers automatically all algorithms via their names at the booting time.
	 * @param prefix text string of root path to discover algorithms, for example, &quot;/net/hudup/&quot;.
	 */
	void discover(String prefix);
	
	
}
