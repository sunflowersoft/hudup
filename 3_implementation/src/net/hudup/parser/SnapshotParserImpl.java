/**
 * 
 */
package net.hudup.parser;

import net.hudup.core.alg.Alg;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.parser.SnapshotParser;
import net.hudup.data.SnapshotImpl;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class SnapshotParserImpl extends SnapshotParser {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public SnapshotParserImpl() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public Dataset parse(DataConfig config) {
		// TODO Auto-generated method stub
		
		config.setParser(this);
		return SnapshotImpl.create(config);
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new SnapshotParserImpl();
	}
	
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "snapshot_parser";
	}

	
	
	
}
