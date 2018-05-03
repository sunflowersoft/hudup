package net.hudup.core.alg;

import org.apache.log4j.Logger;

import net.hudup.core.data.DataConfig;


/**
 * This is abstract class of {@link Alg} interface.
 * This abstract class gives out default implementation for some methods such as {@link #getConfig()},
 * {@link #resetConfig()} and {@link #createDefaultConfig()}. 
 * It also declares the configuration variable {@link #config}.
 * Note that every {@link Alg} owns a configuration specified by {@link DataConfig} class.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public abstract class AbstractAlg implements Alg {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Logger of this class.
	 */
	protected final static Logger logger = Logger.getLogger(AbstractAlg.class);

	
	/**
	 * This variable represents configuration of algorithm. It is returned value of {@link #getConfig()} method.
	 * The {@link #resetConfig()} resets it.
	 */
	protected DataConfig config = null;

	
	/**
	 * Default constructor.
	 */
	public AbstractAlg() {
		this.config = createDefaultConfig();
	}

	
	@Override
	public DataConfig getConfig() {
		// TODO Auto-generated method stub
		return config;
	}

	
	@Override
	public void resetConfig() {
		// TODO Auto-generated method stub
		config.clear();
		config.putAll(createDefaultConfig());
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		return new DataConfig();
	}

	
	@Override
	public String toString() {
		return getName();
	}


}
