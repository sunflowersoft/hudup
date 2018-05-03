package net.hudup.data.ui;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class UnitTableFactory {

	
	/**
	 * 
	 * @return {@link UnitTable}
	 */
	public static UnitTable create() {
		return new UnitTableImpl();
	}
	
	
}
