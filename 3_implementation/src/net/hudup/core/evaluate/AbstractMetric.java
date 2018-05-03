package net.hudup.core.evaluate;

import net.hudup.core.data.DataConfig;


/**
 * This abstract class, called {@code abstract metric}, implements partially {@link Metric} interface.
 * It only stores a configuration and defines some methods related to such configuration (getting configuration, creating configuration, etc.) because metric is also an algorithm represented by {@code Alg} interface. However, most of metrics extends it.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class AbstractMetric implements Metric {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Configuration of metric because metric is also an algorithm represented by {@code Alg} interface.
	 */
	protected DataConfig config = null;
	
	
	/**
	 * Default constructor.
	 */
	public AbstractMetric() {
		// TODO Auto-generated constructor stub
		this.config = createDefaultConfig();
	}

	
	@Override
	public void setup(Object... params) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public DataConfig getConfig() {
		return config;
	}
	

	@Override
	public void resetConfig() {
		config.clear();
		config.putAll(createDefaultConfig());
	}


	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		return new DataConfig();
	}


}
