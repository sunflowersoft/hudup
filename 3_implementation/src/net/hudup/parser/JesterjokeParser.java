/**
 * 
 */
package net.hudup.parser;

import net.hudup.core.data.DataDriver;
import net.hudup.core.parser.SnapshotParser;



/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class JesterjokeParser extends SnapshotParser {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public JesterjokeParser() {
		
	}
	
	
	@Override
	public String getName() {
		return "jesterjoke_parser";
	}


	/**
	 * Equation y = (y1 - y0) / (x1 - x0) * x - (x0*y1 - x1*y0) / (x1 - x0)
	 * Mapping (1, -10) and (5, +10)
	 * @return {@link Mapper}
	 */
	protected Mapper getMapper() {
		return new Mapper() {
			
			@Override
			public float map(float value) {
				// TODO Auto-generated method stub
				return 5f * value -  15;
			}
			
			@Override
			public float imap(float value) {
				// TODO Auto-generated method stub
				return (value + 15f) / 5f;
			}
		};
	}
	
	
	@Override
	public boolean support(DataDriver driver) {
		// TODO Auto-generated method stub
		
		return driver.isFlatServer();
	}
	
	
}
