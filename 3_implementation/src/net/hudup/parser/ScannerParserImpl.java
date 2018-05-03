/**
 * 
 */
package net.hudup.parser;

import net.hudup.core.alg.Alg;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.parser.ScannerParser;
import net.hudup.data.ScannerImpl;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ScannerParserImpl extends ScannerParser {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public ScannerParserImpl() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public Dataset parse(DataConfig config) {
		// TODO Auto-generated method stub
		
		config.setParser(this);
		return new ScannerImpl(config);
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "scanner_parser";
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new ScannerParserImpl();
	}

	
	
}
