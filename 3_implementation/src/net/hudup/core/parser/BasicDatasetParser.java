/**
 * 
 */
package net.hudup.core.parser;

import org.apache.log4j.Logger;

import net.hudup.core.data.DataConfig;


/**
 * This class implements partially the {@link DatasetParser} interface.
 * Its main feature is to add a configuration variable {@link #config} to normal parser.
 * Any complete dataset parser should extend this {@link BasicDatasetParser}.
 * Note, {@link DatasetParser} is one of main interface responsible for reading and parsing coarse data such as CSV file, Excel file, database table into {@code Dataset}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class BasicDatasetParser implements DatasetParser {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Logger of this class.
	 */
	protected final static Logger logger = Logger.getLogger(DatasetParser.class);

	
	/**
	 * Internal configuration.
	 */
	protected DataConfig config = null;
	
	
	/**
	 * Default constructor.
	 */
	public BasicDatasetParser() {
		this.config = createDefaultConfig();
	}

	
	@Override
	public String toString() {
		return getName();
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


}
