package net.hudup.logistic.math.ui;

import javax.swing.JTable;

import net.hudup.data.bit.BitData;
import net.hudup.logistic.math.BinaryProbItemMatrix;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class BitProbMatrixTable extends JTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public BitProbMatrixTable() {
		super(new BitProbMatrixTableModel());
		
		setAutoResizeMode(AUTO_RESIZE_OFF);
	}
	
	
	/**
	 * 
	 * @param matrix
	 */
	public void update(BinaryProbItemMatrix matrix) {
		getMatrixModel().update(matrix);
	}
	
	
	/**
	 * 
	 * @param bitData
	 */
	public void update(BitData bitData) {
		BinaryProbItemMatrix matrix = new BinaryProbItemMatrix();
		matrix.setup(bitData);
		
		update(matrix);
	}
	
	
	/**
	 * 
	 * @return {@link BitProbMatrixTableModel}
	 */
	public BitProbMatrixTableModel getMatrixModel() {
		return (BitProbMatrixTableModel)getModel();
	}
	
	
	
}
