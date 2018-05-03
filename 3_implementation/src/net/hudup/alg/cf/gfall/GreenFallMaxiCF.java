/**
 * 
 */
package net.hudup.alg.cf.gfall;

import net.hudup.core.alg.Alg;


/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class GreenFallMaxiCF extends GreenFallCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public GreenFallMaxiCF() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public String getName() {
		return "gfallmaxi";
	}

	
	@Override
	protected FreqItemsetFinder createFreqItemsetFinder() {
		YRollerMaxi yrollermaxi = new YRollerMaxi();
		
		return yrollermaxi;
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new GreenFallMaxiCF();
	}
	
	


}
