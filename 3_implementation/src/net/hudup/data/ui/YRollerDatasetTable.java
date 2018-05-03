package net.hudup.data.ui;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class YRollerDatasetTable extends RatingValueTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	public YRollerDatasetTable() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public RatingValueTM newTableModel() {
		// TODO Auto-generated method stub
		return new YRollerDatasetTM();
	}

}



/**
 * @author Loc Nguyen
 * @version 10.0
 */
class YRollerDatasetTM extends RatingValueTM {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public YRollerDatasetTM() {
		super();
		// TODO Auto-generated constructor stub
	}

	
}
