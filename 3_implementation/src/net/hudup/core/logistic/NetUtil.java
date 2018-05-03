package net.hudup.core.logistic;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.Enumeration;
import java.util.Random;


/**
 * This final class provides utility static methods for network programming.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class NetUtil {
	
	
	/**
	 * Testing whether specified port is valid.
	 * @param port specified port.
	 * @return whether specified port is valid.
	 */
	public static boolean testPort(int port) {
		ServerSocket ss = null; 
		DatagramSocket ds = null;
	    
		try { 
			ss = new ServerSocket(port); 
			ss.setReuseAddress(true); 
			ds = new DatagramSocket(port); 
			ds.setReuseAddress(true);
			
			return true; 
	    }
		catch (Throwable e) {
			
	    } 
		finally { 
			if (ds != null)
	            ds.close(); 
	 
			if (ss != null) { 
				try { 
					ss.close(); 
				} 
				catch (Throwable e) { 
	            } 
	        } 
	    } 
	 
		return false;
	}
	
	
	/**
	 * Testing whether specified port is valid. If such port is valid, the method returns it.
	 * If the port is not valid but the input parameter {@code tryRandom} is {@code true}, the method tries to test random port so as to get a valid port.
	 * @param port specified port
	 * @param tryRandom if {@code true}, the method will try to test random port so as to get a valid port after determining that the specified port is invalid.
	 * @return valid port which can be used.
	 */
	public static int getPort(int port, boolean tryRandom) {
		if (testPort(port))
			return port;
		
		if (tryRandom) {
			Random rnd = new Random();
			int i = 0;
			while (i < 48128) {
				port = rnd.nextInt(48128) + 1024;
				if (testPort(port))
					return port;
				i++;
			}
		}
		return -1;
	}
	
	
	/**
	 * Getting the internet address of current terminator.
	 * @return internet address as {@link InetAddress} of current terminator.
	 */
	public static InetAddress getLocalInetAddress() {
		Enumeration<NetworkInterface> nis = null;
		
        try {
            nis = NetworkInterface.getNetworkInterfaces();
        } 
        catch (Exception e) {
            return null;
        }

        while (nis.hasMoreElements()) {
            NetworkInterface ni = nis.nextElement();
            Enumeration<InetAddress> addrs = ni.getInetAddresses();
            while (addrs.hasMoreElements()) {
                InetAddress addr = addrs.nextElement();
                if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()
                        && addr.getHostAddress().indexOf(":") == -1 ) {
                    return addr;
                }
            }
        }
        
        try {
            return InetAddress.getLocalHost();
        }
        catch (Exception e) {
            return null;
        }
	}
	
	
}
