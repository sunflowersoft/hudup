package net.hudup;

import net.hudup.core.AccessPoint;
import net.hudup.core.Firer;

/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Balancer implements AccessPoint {

	
	/**
	 * @param args The argument parameter of main method. It contains command line arguments.
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		new Balancer().run(args);
	}

	
	@Override
	public void run(String[] args) {
		// TODO Auto-generated method stub
		new Firer();
		
		net.hudup.listener.Balancer balancer = net.hudup.listener.Balancer.create();
		balancer.start();
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Balancer";
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getName();
	}


}
