package net.hudup;

import net.hudup.core.AccessPoint;
import net.hudup.core.Firer;

/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public final class Listener implements AccessPoint {

	
	/**
	 * @param args The argument parameter of main method. It contains command line arguments.
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		new Listener().run(args);
	}

	
	@Override
	public void run(String[] args) {
		// TODO Auto-generated method stub
		new Firer();
		
		net.hudup.listener.Listener listener = net.hudup.listener.Listener.create();
		listener.start();
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Listener";
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getName();
	}

	
	
}
