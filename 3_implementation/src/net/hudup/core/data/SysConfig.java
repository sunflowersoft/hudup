/**
 * 
 */
package net.hudup.core.data;

import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.xURI;


/**
 * Note, a configuration represented by {@link DataConfig} class stores configuration properties. Each property is a pair of key and value.
 * This class extends directly {@code DataConfig} class, which is considered as a system configuration.
 * It is used for system or important case.
 * As a convention, this class is called system configuration. Every system configuration stores an internal URI string referred by its internal variable {@link #uri}.
 * Such URI string points to where to save (store, write) system configuration.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class SysConfig extends DataConfig {
	
	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * URI string points to where to save (store, write) system configuration.
	 */
	protected String uri;
	
	
	/**
	 * Default constructor of system configuration.
	 */
	public SysConfig() {
		super();
		reset();
	}
	
	
	/**
	 * Constructor with specified URI pointing to where to save this system configuration.
	 * @param uri specified URI pointing to where to save this system configuration.
	 */
	public SysConfig(xURI uri) {
		super(uri);
		// TODO Auto-generated constructor stub
		this.uri = uri.toString();
	}


	/**
	 * Loading configuration settings from inner URI.
	 * @return whether loading successfully.
	 */
	public boolean load() {
		if (this.uri == null)
			return false;
		else
			return load(xURI.create(this.uri));
	}
	
	
	@Override
	public boolean load(xURI uri) {
		// TODO Auto-generated method stub
		reset();
		
		this.uri = uri.toString();
		return super.load(uri);
	}


	/**
	 * Saving configuration settings to inner URI.
	 * @return whether loading successfully.
	 */
	public boolean save() {
		if (this.uri == null)
			return false;
		else
			return save(xURI.create(this.uri));
	}
	
	
	/**
	 * Resetting configuration. Firstly, all entries (properties) are removed and then some default tasks are executed.
	 */
	public void reset() {
		clear();
		
		put(I18nUtil.LANGUAGE, I18nUtil.DEFAULT_LANGUAGE);
		put(I18nUtil.COUNTRY, I18nUtil.DEFAULT_COUNTRY);
	}
	
	
}
