package net.hudup.parser;

import net.hudup.core.alg.Alg;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.data.SemiScanner;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class SemiScannerParser extends ScannerParserImpl {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public SemiScannerParser() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public Dataset parse(DataConfig config) {
		// TODO Auto-generated method stub
		
		config.setParser(this);
		return new SemiScanner(config);
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "semi_scanner_parser";
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		return new DataConfig();
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new SemiScannerParser();
	}
	
	
	
}
