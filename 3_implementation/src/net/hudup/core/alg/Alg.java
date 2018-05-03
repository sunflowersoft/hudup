/**
 * 
 */
package net.hudup.core.alg;

import java.io.Serializable;

import net.hudup.core.data.DataConfig;


/**
 * <code>Alg</code> is the most abstract interface for all algorithms.
 * <code>Alg</code> is one of the most important interfaces.
 * It declares the most basic methods in an algorithm.
 * The most important thing is that every <code>Alg</code> owns a configuration specified by {@code DataConfig} class.
 * <code>Alg</code> has five typical inherited interfaces as follows:
 * <ul>
 * <li>Recommendation algorithm {@code Recommender}.</li> 
 * <li>Context template schema manager {@code CTSManager}.</li> 
 * <li>Data set parser {@code DatasetParser}.</li>
 * <li>External query {@code ExternalQuery}.</li>
 * <li>Metric {@code Metric}.</li>
 * </ul>
 * {@code Alg} provides configuration methods such as {@link #getConfig()} and {@link #createDefaultConfig()} which allow programmers to pass customization settings to a given algorithm before it runs.
 * Every algorithm has a unique name as returned value of {@link #getName()} method of {@code Alg}. Every algorithm is registered in system register table and identified by its name.
 * Plugin manager presented by {@code PluginManager} interface discovers automatically all algorithms via their names at the booting time.
 * {@code Alg} is the most general interface; anything that is programmable and executable is {@code Alg}. 
 * @author Loc Nguyen
 * @version 10.0
 */
public interface Alg extends Serializable {
	
	
	/**
	 * Getting the configuration of algorithm.
	 * @return Algorithm configuration represented by {@link DataConfig}
	 */
	DataConfig getConfig();
	
	
	/**
	 * Re-setting default configuration.
	 */
	void resetConfig();
	
	
	/**
	 * Creating default configuration, 
	 * it is called every time method {@link #resetConfig()} is called
	 * @return Default configuration represented by {@link DataConfig}
	 */
	DataConfig createDefaultConfig();

	
	/**
	 * Declaring the name of algorithm, the name is used to query algorithm in register table, 
	 * please see class of register table for more details. Plug-in manager discovers automatically all algorithms via their names.
	 * @return name of algorithm
	 */
	String getName();
	
	
	/**
	 * Create the new instance of algorithm, concrete algorithm is determined run-time.
	 * @return New {@link Alg} instance
	 */
	Alg newInstance();
	
}
