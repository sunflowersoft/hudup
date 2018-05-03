/**
 * 
 */
package net.hudup.server;

import java.net.URI;
import java.net.URL;

import net.hudup.alg.cf.gfall.GreenFallCF;
import net.hudup.core.Constants;
import net.hudup.core.alg.Recommender;
import net.hudup.core.client.ServerConfig;
import net.hudup.core.data.HiddenText;
import net.hudup.core.logistic.xURI;
import net.hudup.logistic.math.HudupCipher;
import net.hudup.parser.SemiScannerParser;
import net.hudup.parser.SnapshotParserImpl;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class PowerServerConfig extends ServerConfig {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */
	public final static String serverConfig = Constants.WORKING_DIRECTORY + "/serverconfig.xml";
	
	
	/**
	 * 
	 */
	public final static String  TEMPLATES_SAMPLE_DATA = Constants.TEMPLATES_PACKAGE + "hudup_sample_data.zip";
	

	/**
	 * 
	 */
	public final static String SERVER_RECOMMENDER_FIELD = changeCase("server_recommender");

	
	/**
	 * 
	 */
	public final static String SERVER_PARSER_FIELD = changeCase("server_parser");
	
	
	/**
	 * 
	 */
	public PowerServerConfig() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * 
	 * @param uri
	 */
	public PowerServerConfig(xURI uri) {
		super(uri);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		super.reset();
		try {
			URL storeUrl = getClass().getResource(TEMPLATES_SAMPLE_DATA);
			URI storeUri = storeUrl.toURI();
			putDefaultUnitList(xURI.create(storeUri));
			setRecommender(new GreenFallCF());
			setParser(new SnapshotParserImpl());
		} 
		catch (Throwable e) {
			e.printStackTrace();
			putDefaultUnitList(xURI.create("jdbc:derby:hudup;create=true"));
			setRecommender(new GreenFallCF());
			setParser(new SemiScannerParser());
		}
	}

	
	/**
	 * 
	 * @return server recommender {@link Recommender}
	 */
	public Recommender getRecommender() {
		return (Recommender) get(SERVER_RECOMMENDER_FIELD);
	}
	
	
	/**
	 * 
	 * @param recommender Server recommender {@link Recommender}
	 */
	public void setRecommender(Recommender recommender) {
		if (recommender != null)
			put(SERVER_RECOMMENDER_FIELD, recommender);
	}
	
	
	@Override
	protected String encrypt(HiddenText hidden) {
		// TODO Auto-generated method stub
		HudupCipher cipher = new HudupCipher();
		return cipher.encrypt(hidden.getText());
	}


	@Override
	protected HiddenText decrypt(String text) {
		// TODO Auto-generated method stub
		HudupCipher cipher = new HudupCipher();
		return new HiddenText(cipher.decrypt(text));
	}
	
	
	@Override
	public Object clone() {
		PowerServerConfig cfg = new PowerServerConfig();
		cfg.putAll(this);
		
		return cfg;
	}

	
	
	
}
