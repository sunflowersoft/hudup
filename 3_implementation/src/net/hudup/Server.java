/**
 * 
 */
package net.hudup;

import java.rmi.RemoteException;

import net.hudup.core.AccessPoint;
import net.hudup.core.Firer;
import net.hudup.core.client.PowerServer;
import net.hudup.server.DefaultServer;
import net.hudup.server.external.ExternalServer;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public final class Server implements AccessPoint {

	
	/**
	 * @param args The argument parameter of main method. It contains command line arguments.
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		new Server().run(args);
	}

	
	@Override
	public void run(String[] args) {
		// TODO Auto-generated method stub
		
		new Firer();
		
		PowerServer server = null;
		if (args.length == 0) {
			server = DefaultServer.create();
		}
		else {
			String serverKind = args[0].trim().toLowerCase();
			
			if (serverKind.equals("-external"))
				server = ExternalServer.create();
			else
				server = DefaultServer.create();
		}
		
		if (server == null)
			return;
		
		try {
			server.start();
		} 
		catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Server";
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getName();
	}

	
	
}
