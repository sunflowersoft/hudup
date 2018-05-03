package net.hudup.evaluate;

import java.util.EventObject;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ProgressEvent extends EventObject {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	protected Evaluator evaluator = null;

	
	/**
	 * 
	 */
	protected int progressTotal = 0;
	
	
	/**
	 * 
	 */
	protected int progressStep = 0;
	
	
	/**
	 * 
	 */
	protected String algName = "";
	
	
	/**
	 * 
	 */
	protected int datasetId = -1;
	
	
	/**
	 * 
	 */
	protected int vCurrentTotal = 0;
	
	
	/**
	 * 
	 */
	protected int vCurrentCount = 0;
	
	
	/**
	 * 
	 * @param evaluator
	 * @param progressTotal
	 * @param progressStep
	 */
	public ProgressEvent(Evaluator evaluator, int progressTotal, int progressStep) {
		super(evaluator);
		// TODO Auto-generated constructor stub
		
		this.progressTotal = progressTotal;
		this.progressStep = progressStep;
	}
	
	
	/**
	 * 
	 * @return {@link Evaluator}
	 */
	public Evaluator getEvaluator() {
		return (Evaluator)getSource();
	}
	
	
	/**
	 * 
	 * @return total progress
	 */
	public int getProgressTotal() {
		return progressTotal;
	}
	

	/**
	 * 
	 * @return progress step
	 */
	public int getProgressStep() {
		return progressStep;
	}
	
	
	/**
	 * 
	 * @return algorithm name
	 */
	public String getAlgName() {
		return algName;
	}
	
	
	/**
	 * 
	 * @param algName
	 */
	public void setAlgName(String algName) {
		this.algName = algName;
	}
	
	
	/**
	 * 
	 * @return dataset id
	 */
	public int getDatasetId() {
		return datasetId;
	}
	
	
	/**
	 * 
	 * @param datasetId
	 */
	public void setDatasetId(int datasetId) {
		this.datasetId = datasetId;
	}
	
	
	/**
	 * 
	 * @return total rating vectors
	 */
	public int getCurrentTotal() {
		return vCurrentTotal;
	}
	
	
	/**
	 * 
	 * @param vCurrentTotal
	 */
	public void setCurrentTotal(int vCurrentTotal) {
		this.vCurrentTotal = vCurrentTotal;
	}
	
	
	/**
	 * 
	 * @return count of rating vectors
	 */
	public int getCurrentCount() {
		return vCurrentCount;
	}
	
	
	/**
	 * 
	 * @param vRatingCount
	 */
	public void setCurrentCount(int vRatingCount) {
		this.vCurrentCount = vRatingCount;
	}
	
	
	
}
