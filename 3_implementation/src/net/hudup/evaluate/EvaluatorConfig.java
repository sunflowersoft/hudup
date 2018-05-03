package net.hudup.evaluate;

import java.io.Serializable;

import net.hudup.core.Constants;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.SysConfig;
import net.hudup.core.logistic.xURI;


/**
 * This class specifies the configuration of an evaluator represented ({@code Evaluator}.
 * Recall that {@link Evaluator} is one of main classes of Hudup framework, which is responsible for executing and evaluation algorithms according to built-in and user-defined metrics.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class EvaluatorConfig extends SysConfig {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default path including directory and file name to store evaluator configuration.
	 * By default, it is &quot;&lt;directory to deploy Hudup framework&gt;/working/evalconfig.xml&quot;.
	 */
	public final static String evalConfig = Constants.WORKING_DIRECTORY + "/evalconfig.xml";

	
	/**
	 * The maximum number of items in recommended list. By default, it is 10.
	 */
	public final static int DEFAULT_MAX_RECOMMEND_FIELD = 10;

	
	/**
	 * Default constructor.
	 */
	public EvaluatorConfig() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with specified URI. Note that URI, abbreviation of Uniform Resource Identifier, is the string of characters used to identify a resource on Internet.
	 * @param uri URI pointing to file path of configuration.
	 */
	public EvaluatorConfig(xURI uri) {
		super(uri);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void reset() {
		super.reset();
		
		put(DataConfig.MAX_RECOMMEND_FIELD, DEFAULT_MAX_RECOMMEND_FIELD);
	}

	
	
	@Override
	public boolean validate(String key, Serializable value) {
		
		if (key.equals(DataConfig.MAX_RECOMMEND_FIELD))
			return ((Number)value).intValue() >= 0;
		else
			return super.validate(key, value);
	}


	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		EvaluatorConfig cfg = new EvaluatorConfig();
		cfg.putAll(this);
		
		return cfg;
	}


}
