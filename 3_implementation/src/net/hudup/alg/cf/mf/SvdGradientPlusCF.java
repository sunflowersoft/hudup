package net.hudup.alg.cf.mf;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.KBase;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class SvdGradientPlusCF extends SvdGradientCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public SvdGradientPlusCF() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public KBase createKB() {
		// TODO Auto-generated method stub
		return SvdGradientPlusKB.create(this);
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new SvdGradientPlusCF();
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "svd_gradient_plus";
	}
	
}



