/**
 * 
 */
package net.hudup.logistic;

import java.util.Properties;

import net.hudup.core.data.PropList;
import net.hudup.core.logistic.MathUtil;

import org.apache.log4j.Logger;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public final class SystemUtil {

	
	/**
	 * Logger of this class.
	 */
	protected final static Logger logger = Logger.getLogger(SystemUtil.class);

	
	/**
	 * 
	 */
	public final static void enhance() {
		try {
			System.runFinalization();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		try {
			System.gc();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		// Do more
	}

	
	/**
	 * 
	 */
	public final static void enhanceAuto() {
		try {
			enhance();
			logger.info("SystemUtil#enhanceAuto automatically calls system enhancement at thread " + Thread.currentThread());
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * @return system properties
	 */
	public static PropList getSystemProperties() {
		PropList props = new PropList();
		Properties sysProps = System.getProperties();
		
		props.put("Java", 
				sysProps.getProperty("java.runtime.name") + " version " + sysProps.getProperty("java.runtime.version") + ", " +
				sysProps.getProperty("java.vm.name") + " version " + sysProps.getProperty("java.vm.version") + ", " +
				"Class version " + sysProps.getProperty("java.class.version") + ", " +
				"Vendor \"" + sysProps.getProperty("java.vendor") + "\" at " + sysProps.getProperty("java.vendor.url") 
			);
		
		props.put("OS", 
				sysProps.getProperty("os.name") + ", " +
				sysProps.getProperty("os.arch") + ", " +
				"version " + sysProps.getProperty("os.version")
			);
		
		Runtime runtime = Runtime.getRuntime();
		float allocatedMemory = runtime.totalMemory() / 1024.0f / 1024.0f;
		float freeMemory = runtime.freeMemory() / 1024.0f / 1024.0f;
		float maxMemory = runtime.maxMemory() / 1024.0f / 1024.0f;
		
		props.put("Memory(VM)", 
				"Allocated memory = " + MathUtil.format(allocatedMemory, 2) + "MB, " +
				"Free memory = " + MathUtil.format(freeMemory, 2) + "MB, " +
				"Max memory = " + MathUtil.format(maxMemory, 2) + "MB"
			);
		
		props.put("CPU", 
				System.getenv("PROCESSOR_IDENTIFIER") + ", " +
				System.getenv("PROCESSOR_ARCHITECTURE") + ", " +
				"the number of processors is " + System.getenv("NUMBER_OF_PROCESSORS")
			);
	
		props.put("Directory", 
				"Current working directory is \"" + System.getProperty("user.dir") + "\""
			);
		
		return props;
	}
	
	
	
}
