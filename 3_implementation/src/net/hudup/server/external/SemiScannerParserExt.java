package net.hudup.server.external;

import net.hudup.core.alg.Alg;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.parser.SemiScannerParser;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class SemiScannerParserExt extends SemiScannerParser {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public SemiScannerParserExt() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public Dataset parse(DataConfig config) {
		// TODO Auto-generated method stub
		
		config.setParser(this);
		return new SemiScannerExt(config);
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "semi_scanner_ext_parser";
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new SemiScannerParserExt();
	}
	
	
}
