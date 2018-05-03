/**
 * 
 */
package net.hudup.core;



/**
 * This utility final class defines essential constants used over Hudup framework.
 * @author Loc Nguyen
 * @version 10.0
 */
public final class Constants {

	
	/**
	 * Current version of Hudup framework.
	 */
	public final static String  VERSION            = "v11";
	
	/**
	 * Unused float number.
	 */
	public final static float   UNUSED             = Float.NaN;
	
	/**
	 * Default minimum rating value in rating matrix.
	 */
	public final static float   DEFAULT_MIN_RATING = 1;
	
	/**
	 * Default maximum rating value in rating matrix.
	 */
	public final static float   DEFAULT_MAX_RATING = 5;
	
	/**
	 * Default extension of default file. Such default file is called Hudup file &quot;.hdp&quot;.
	 */
	public final static String  DEFAULT_EXT        = "hdp";
	
	/**
	 * The maximum number digits in decimal precision.
	 */
	public final static int     DECIMAL_PRECISION  = 12;
	
	/**
	 * Default date format.
	 */
	public final static String  DATE_FORMAT        = "yyyy-MM-dd HH-mm-ss";
	
	/**
	 * Indicating whether or not the third-party library is used to load automatically plug-ins (algorithms, metrics, etc.) when booting.
	 * Default value of this constant is {@code false} which means there is no third-party library is used to load automatically plug-ins.
	 * However Hudup framework will use JPF boot library developed by Java Plug-in Framework (JPF) Project if this constant is set to be {@code true}.
	 * JPF boot library is available at <a href="http://jpf.sourceforge.net/boot.html">http://jpf.sourceforge.net/boot.html</a>.
	 */
	public final static boolean BOOT_PLUGIN        = false;
	
	
	/**
	 * Default relative working directory.
	 */
	public final static String  WORKING_DIRECTORY        = "working";
	
	/**
	 * Default relative knowledge base directory. This directory is used to store knowledge bases called {@code KBase} (s).
	 * {@code KBase} is the highest-level abstract data format (association rule, frequent pattern, Bayesian network, etc.) which often support model-based recommendation algorithm to produce list of recommended items.
	 */
	public final static String  KNOWLEDGE_BASE_DIRECTORY = WORKING_DIRECTORY + "/kb";
	
	/**
	 * Default relative log directory storing log files.
	 */
	public final static String  LOGS_DIRECTORY           = WORKING_DIRECTORY + "/log";
	
	/**
	 * Default relative temporary directory storing temporary files.
	 */
	public final static String  TEMP_DIRECTORY           = WORKING_DIRECTORY + "/temp";
	
	/**
	 * Default relative &quot;query&quot; directory that contains network services (JSP file, HTTP servlet, etc.) for requesting functions of Hudup server such as recommendation service and updating ratings service.
	 */
	public final static String  Q_DIRECTORY              = WORKING_DIRECTORY + "/q";
	
	/**
	 * Default relative directory containing database of Hudup framework. For example, this directory contains Derby data files of Derby database server.
	 * Other example, this directory is the place to install MySQL database server if MySQL database server is used. 
	 */
	public final static String  DATABASE_DIRECTORY       = WORKING_DIRECTORY + "/db";
	
	/**
	 * The root package (root directory) of all classes.
	 */
	public final static String  ROOT_PACKAGE             = "/net/hudup/";
	
	/**
	 * The resources directory that contains any resources except classes such as images, template files.
	 */
	public final static String  RESOURCES_PACKAGE        = ROOT_PACKAGE + "core/resources/";
	
	/**
	 * The directory contains images.
	 */
	public final static String  IMAGES_PACKAGE           = RESOURCES_PACKAGE + "images/";
	
	/**
	 * The directory contains template files.
	 */
	public final static String  TEMPLATES_PACKAGE        = RESOURCES_PACKAGE + "templates/";

	
	/**
	 * URI, abbreviation of Uniform Resource Identifier, is the string of characters used to identify a resource on Internet.
	 * URI like file system path (for example, http://localhost:8080/hudup) has many parts separated by the character specified by this constant.
	 */
	public final static String URI_SEPARATOR_CHAR  = "/";
	
	
	/**
	 * Default port of Hudup (recommendation) server
	 */
	public final static int     DEFAULT_SERVER_PORT           = 10151;
	
	/**
	 * Default port of Hudup listener. Note that listener is responsible for interact between user and Hudup server.
	 */
	public final static int     DEFAULT_LISTENER_PORT         = 10152;
	
	/**
	 * Default exported port of Hudup listener. RMI application connects with listener via this port.
	 * Java Remote Method Invocation (RMI) is a protocol that allows an Java application to call remotely methods of Java objects inside other Java application over network.
	 * Please see Oracle Java document for more details about RMI (<a href="https://docs.oracle.com/javase/tutorial/rmi/index.html">https://docs.oracle.com/javase/tutorial/rmi/index.html</a>).
	 */
	public final static int     DEFAULT_LISTENER_EXPORT_PORT  = 10153;
	
	/**
	 * Default port of Hudup balancer. Essentialy, balancer is a particular listener that supports balancing in busy network.
	 * For example, if there are many Hudup servers, balancer will choose a least busy server to process incoming request.
	 */
	public final static int     DEFAULT_BALANCER_PORT         = 10154;
	
	/**
	 * Default exported port of Hudup balancer. RMI application connects with balancer via this port.
	 * Java Remote Method Invocation (RMI) is a protocol that allows an Java application to call remotely methods of Java objects inside other Java application over network.
	 * Please see Oracle Java document for more details about RMI (<a href="https://docs.oracle.com/javase/tutorial/rmi/index.html">https://docs.oracle.com/javase/tutorial/rmi/index.html</a>).
	 */
	public final static int     DEFAULT_BALANCER_EXPORT_PORT  = 10155;
	
	/**
	 * The graphic user interface (GUI) allowing users to control Hudup server is called control panel. Control panel uses this port to connect with Hudup server instead of using {@link #DEFAULT_SERVER_PORT}.
	 */
	public final static int     DEFAULT_CONTROL_PANEL_PORT    = 10156;
	
	/**
	 * When Hudup server, listener, or balancer starts, it uses firstly the port {@link #DEFAULT_SERVER_PORT}. If this constant is {@code true}, many random ports are tried until success.
	 * By default, this constant is {@code false}, which means that there is no such randomization. 
	 */
	public static final boolean TRY_RANDOM_PORT               = false;
	
	/**
	 * This is the period in miliseconds that the Hudup server does periodically internal tasks such as data mining and learning knowledge base.
	 */
	public final static int     DEFAULT_SERVER_TASK_PERIOD    = (int) (1000 * 60 * 5); // 5 minute
	
	/**
	 * The Hudup server is available to serve incoming request in a interval called a timeout in miliseconds. This constant specifies such timeout.
	 * After timeout interval is reached, the server suspends and users must resumes it.
	 */
	public final static int     DEFAULT_SERVER_TIMEOUT        = (int) (1000 * 60 * 30); // 30 minutes
	
	/**
	 * This is the period in miliseconds that the listener does periodically internal tasks.
	 */
	public final static int     DEFAULT_LISTENER_TASK_PERIOD  = DEFAULT_SERVER_TIMEOUT;
	
	/**
	 * The integer identification of every object (item, user) in data set (database) is often increased automatically.
	 * However such auto-increment consumes much time and resources. This constant indicates whether or not the auto-increment is supported.
	 * By default, this constant is {@code false}, which means that there is no support of auto-increment.
	 */
	public static final boolean SUPPORT_AUTO_INCREMENT_ID     = false;

	
	
}
