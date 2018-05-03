package net.hudup.core.alg;

import java.io.Serializable;

import net.hudup.core.Util;
import net.hudup.core.data.DataConfig;


/**
 * This class represents description of an algorithm. In current version, it includes algorithm class name and configuration of such algorithm.
 * Please distinguish algorithm name and algorithm class name. The algorithm name is the string returned by {@link Alg#getName()} while the algorithm class name is class name of the class that implements interface {@link Alg}. 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class AlgDesc implements Serializable, net.hudup.core.Cloneable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Algorithm name.
	 */
	protected String algClassName = null;
	
	
	/**
	 * Configuration of the referred algorithm.
	 */
	protected DataConfig config = null;
	
	
	/**
	 * Default constructor
	 */
	public AlgDesc() {
		
	}
	
	
	/**
	 * Constructor with algorithm class name and algorithm configuration.
	 * 
	 * @param algClassName class name of algorithm. 
	 * @param config configuration of algorithm.
	 */
	public AlgDesc(String algClassName, DataConfig config) {
		this.algClassName = algClassName;
		this.config = config;
	}
	
	
	/**
	 * Constructor with specified algorithm. This construct extracts the class name and configuration of specified algorithm.
	 * @param alg specified algorithm
	 */
	public AlgDesc(Alg alg) {
		this(alg.getClass().getName(), alg.getConfig());
	}

	
	/**
	 * Getting algorithm class name.
	 * @return algorithm class name
	 */
	public String getAlgClassName() {
		return algClassName;
	}
	
	
	/**
	 * Setting algorithm class name.
	 * @param algClassName specified algorithm class name.
	 */
	public void setAlgClassName(String algClassName) {
		this.algClassName = algClassName;
	}
	
	
	/**
	 * Getting algorithm configuration as {@link DataConfig}.
	 * @return algorithm configuration
	 */
	public DataConfig getConfig() {
		return config;
	}
	
	
	/**
	 * Setting algorithm configuration via the specified {@link DataConfig}.
	 * @param config Specified configuration {@link DataConfig}
	 */
	public void setConfig(DataConfig config) {
		this.config = config;
	}
	
	
	/**
	 * Creating algorithm from its description.
	 * @return Created algorithm as an instance of {@link Alg}
	 */
	public Alg createAlg() {
		Alg alg = (Alg) Util.newInstance(algClassName);
		if (alg != null)
			alg.getConfig().putAll(config);
		
		return alg;
	}


	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		
		return new AlgDesc(algClassName.toString(), (DataConfig) config.clone());
	}
	
	

}
