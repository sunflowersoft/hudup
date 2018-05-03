package net.hudup.core.data;

import java.io.Serializable;

import org.apache.log4j.Logger;


/**
 * This abstract class implements partially {@link Dataset} interface.
 * It add two variables such as {@link #config} which is the configuration of dataset and {@link #exclusive} indicating whether this dataset is exclusive.
 * Note, if this dataset is in exclusive mode, it is clear (method {@link #clear()} is called) when method Recommender#unsetup() is called (Recommender algorithm is stopped).
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public abstract class AbstractDataset implements Dataset, Serializable  {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Logger of this class.
	 */
	protected final static Logger logger = Logger.getLogger(Dataset.class);

	
	/**
	 * The configuration of dataset.
	 */
	protected DataConfig config = null;
	
	
	/**
	 * Indicator of whether this dataset is exclusive.
	 */
	protected boolean exclusive = false;
	
	
	@Override
	public DataConfig getConfig() {
		return config;
	}

	
	@Override
	public void setConfig(DataConfig config) {
		this.config = config;
	}

	
	@Override
	public void clear() {
		config = null;
		exclusive = false;
	}


	@Override
	public abstract Object clone();


	@Override
	public boolean isExclusive() {
		// TODO Auto-generated method stub
		return exclusive;
	}


	@Override
	public void setExclusive(boolean exclusive) {
		// TODO Auto-generated method stub
		this.exclusive = exclusive;
	}


	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		try {
			clear();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}


}
