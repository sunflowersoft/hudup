/**
 * 
 */
package net.hudup.plugins.demo;

import java.util.Set;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.cf.MemoryBasedCF;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.data.RatingVector;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class PluginDemoCF extends MemoryBasedCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */
	public PluginDemoCF() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "plugin_demo_cf";
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new PluginDemoCF();
	}


	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
