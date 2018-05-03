/**
 * 
 */
package net.hudup.core.data;


import net.hudup.core.Cloneable;
import net.hudup.core.Constants;
import net.hudup.core.logistic.xSubURI;
import net.hudup.core.logistic.xURI;

import org.apache.log4j.Logger;


/**
 * {@link DataDriver} is the final class representing specific data types such as system file or database.
 * Currently, {@link DataDriver} represents Derby database, system file, ftp file, http file, network file, Microsoft SQL Server database, MySQL database, Oracle database, PostgreSQL database.
 * The constructor of {@link DataDriver} receives input as a data type with note that data type is defined as {@link Enum} class #{@link DataType}. 
 * Each data type has a name. For example, the name MySQL database is &quot;mysql&quot;.
 * Each data type is associated with a Java driver class. For example, MySQL database is associated with the driver class &quot;com.mysql.jdbc.Driver&quot;.
 * {@link DataDriver} supports many methods to process data type.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public final class DataDriver implements Cloneable {
	
	
	/**
	 * Logger of this class.
	 */
	protected final static Logger logger = Logger.getLogger(DataDriver.class);

	
	/**
	 * Driver class name associated with Derby database.
	 */
	public static final String DERBY_DRIVER_CLASS_NAME = "org.apache.derby.jdbc.ClientDriver";
	
	
	/**
	 * Driver class name associated with Derby engine database.
	 */
	public static final String DERBY_ENGINE_DRIVER_CLASS_NAME = "org.apache.derby.jdbc.EmbeddedDriver";
	
	
	/**
	 * Driver class name associated with file system.
	 */
	public static final String FILE_DRIVER_CLASS_NAME = "java.io.File";
	
	
	/**
	 * Driver class name associated with FTP file.
	 */
	public static final String FTP_DRIVER_CLASS_NAME = "cz.dhl.ftp.FtpConnect";
	
	
	/**
	 * Driver class name associated with HTTP file.
	 */
	public static final String HTTP_DRIVER_CLASS_NAME = "java.nio.file.Path";

	
	/**
	 * Driver class name associated with RMI protocol file.
	 */
	public static final String HUDUP_RMI_DRIVER_CLASS_NAME = net.hudup.core.client.PowerServer.class.getName();

	
	/**
	 * Driver class name associated with HUDUP protocol file.
	 */
	public static final String HUDUP_SOCKET_DRIVER_CLASS_NAME = net.hudup.core.client.SocketConnection.class.getName();

	
	/**
	 * Driver class name associated with Microsoft SQL Server database.
	 */
	public static final String MSSQL_DRIVER_CLASS_NAME = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
	
	
	/**
	 * Driver class name associated with MySQL database.
	 */
	public static final String MYSQL_DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
	
	
	/**
	 * Driver class name associated with Oracle database.
	 */
	public static final String ORACLE_DRIVER_CLASS_NAME = "oracle.jdbc.driver.OracleDriver";
	
	
	/**
	 * Driver class name associated with PostgreSQL database.
	 */
	public static final String POSTGRESQL_DRIVER_CLASS_NAME = "org.postgresql.Driver";

	
	/**
	 * {@link DataType} is {@link Enum} class that represents data type such as Derby database, system file, ftp file, http file, network file, Microsoft SQL Server database, MySQL database, Oracle database, PostgreSQL database.
	 * 
	 * @author Loc Nguyen
	 * @version 10.0
	 *
	 */
	public static enum DataType {
		
		/**
		 * Derby database.
		 */
		derby,
		
		/**
		 * Derby engine database.
		 */
		derby_engine,
		
		/**
		 * File system.
		 */
		file,
		
		/**
		 * FTP file system.
		 */
		ftp,
		
		/**
		 * HTTP file system.
		 */
		http,
		
		/**
		 * Hudup RMI.
		 */
		hudup_rmi,
		
		/**
		 * Hudup socket.
		 */
		hudup_socket,
		
		/**
		 * Microsoft SQL Server database.
		 */
		mssql,
		
		/**
		 * MySQL database.
		 */
		mysql,
		
		/**
		 * Oracle server database.
		 */
		oracle,
		
		/**
		 * PostgreSQL database.
		 */
		postgresql}
	
	
	/**
	 * Data type of this class. The default data type is Derby engine.
	 * This is the main property of this class.
	 */
	private DataType type = DataType.derby;
	
	
	/**
	 * This constructor receives input as a data type.
	 * @param type Specific data type
	 */
	public DataDriver(DataType type) {
		this.type = type;
	}
	
	
	/**
	 * Getting data type.
	 * @return {@link DataType}
	 */
	public DataType getType() {
		return type;
	}
	
	
	/**
	 * Getting name of data type. This method converts the data type represented by {@link Enum} class {@link DataType} into a name.
	 * For example, the data driver for Derby engine database has name &quot;derby&quot;.
	 * @return Name of data type
	 */
	public String getName() {
		switch (type) {
		case derby:
			return "derby";
		case derby_engine:
			return "derby_engine";
		case file:
			return "file";
		case ftp:
			return "ftp";
		case http:
			return "http";
		case hudup_rmi:
			return "hudup_rmi";
		case hudup_socket:
			return "hudup_socket";
		case mssql:
			return "mssql";
		case mysql:
			return "mysql";
		case oracle:
			return "oracle";
		case postgresql:
			return "postgresql";
		default:
			return "unknown";
		}
	}

	
	@Override
	public String toString() {
		switch (type) {
		case derby:
			return "Derby";
		case derby_engine:
			return "Derby engine";
		case file:
			return "File";
		case ftp:
			return "Ftp";
		case http:
			return "Http";
		case hudup_rmi:
			return "Hudup RMI server";
		case hudup_socket:
			return "Hudup socket server (Hdp)";
		case mssql:
			return "Microsift SQL Server";
		case mysql:
			return "MySQL";
		case oracle:
			return "Oracle";
		case postgresql:
			return "PostgreSQL";
		default:
			return "Unknown";
			
		}
	}
	
	
	/**
	 * Getting the driver class name associated with data type.
	 * @return Third-party class name associated with data type.
	 */
	public String getInnerClassName() {
		switch (type) {
		case derby:
			return DERBY_DRIVER_CLASS_NAME;
		case derby_engine:
			return DERBY_ENGINE_DRIVER_CLASS_NAME;
		case file:
			return FILE_DRIVER_CLASS_NAME;
		case ftp:
			return FTP_DRIVER_CLASS_NAME;
		case http:
			return HTTP_DRIVER_CLASS_NAME;
		case hudup_rmi:
			return HUDUP_RMI_DRIVER_CLASS_NAME;
		case hudup_socket:
			return HUDUP_SOCKET_DRIVER_CLASS_NAME;
		case mssql:
			return MSSQL_DRIVER_CLASS_NAME;
		case mysql:
			return MYSQL_DRIVER_CLASS_NAME;
		case oracle:
			return ORACLE_DRIVER_CLASS_NAME;
		case postgresql:
			return POSTGRESQL_DRIVER_CLASS_NAME;
		default:
			return null;
			
		}
	}
	
	
	/**
	 * Getting the real driver class associated with data type.
	 * @return Third-party class associated with data type.
	 */
	public Class<?> getInnerClass() {
		try {
			return Class.forName(getInnerClassName());
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			logger.error("Data driver can not load inner class " + getInnerClassName() + ", caused by " + e.getMessage());
		}
		
		return null;
	}
	
	
	/**
	 * Getting the default port of driver protocol. For example, the default port of MySQL database is 3306.
	 * @return Default port of driver protocol
	 */
	public int getDefaultPort() {
		switch (type) {
		case derby:
			return 1527;
		case derby_engine:
			return -1;
		case file:
			return -1;
		case ftp:
			return 21;
		case http:
			return 80;
		case hudup_rmi:
			return Constants.DEFAULT_SERVER_PORT;
		case hudup_socket:
			return Constants.DEFAULT_LISTENER_PORT;
		case mssql:
			return 1433;
		case mysql:
			return 3306;
		case oracle:
			return 1521;
		case postgresql:
			return 5432;
		default:
			return 0;
		}
		
	}
	
	
	/**
	 * Indicating whether or not the data driver has flat structure. For example, file system has flat structure and MySQL has no flat structure because MySQL is structured database.
	 * @return Whether data driver is flat structure
	 */
	private static boolean isFlatStructure(DataType type) {
		switch (type) {
		case derby:
			return false;
		case derby_engine:
			return false;
		case file:
			return true;
		case ftp:
			return true;
		case http:
			return true;
		case hudup_rmi:
			return false;
		case hudup_socket:
			return false;
		case mssql:
			return false;
		case mysql:
			return false;
		case oracle:
			return false;
		case postgresql:
			return false;
		default:
			return false;
			
		}
		
	}

	
	/**
	 * Indicating whether the data driver has flat structure and server. For example, FTP is flat structure server.
	 * The default implementation of this method is the same to {@link #isFlatStructure(DataType)} because the local file system is considered as serve now.
	 * @return Whether data driver is flat server
	 */
	public boolean isFlatServer() {
		return isFlatStructure(type);
	}
	
	
	/**
	 * Indicating whether the data driver is supported by recommendation server.
	 * For example, Hudup protocol is supported by recommendation server.
	 * @return Whether data driver is recommendation server
	 */
	public boolean isRecommendServer() {
		return type == DataType.hudup_rmi || type == DataType.hudup_socket;
	}
	
	
	/**
	 * Indicating whether the data driver is supported by database management system (DBMS) server.
	 * For example, MySQL protocol is supported by DBMS server.
	 * @return Whether data driver is DBMS server
	 */
	public boolean isDbServer() {
		return !isFlatServer() && !isRecommendServer();
	}
	
	
	/**
	 * Indicating whether the data driver is valid where the valid driver is associated with the valid driver class returned by {@link #getInnerClass()}.
	 * @return Whether data driver is valid
	 */
	public boolean isValid() {
		return getInnerClass() != null;
	}
	
	
	/**
	 * Creating URI of the data driver along with host, port, and path.
	 * Please see {@link xURI} for more details about URI.
	 * @param host Specific host of URI
	 * @param port Specific port of URI
	 * @param path Specific path of URI
	 * @return {@link xURI} URI of the data driver along with host, port, and path.
	 */
	public xURI getUri(String host, int port, String path) {
		host = host != null ? host.trim() : null;
			
		switch (type) {
		case derby:
			return xURI.create("jdbc:derby", host, port, path);
		case derby_engine:
			return xURI.create("jdbc:derby:" + path + ";create=true");
		case file:
			return xURI.create("file", host, port, path);
		case ftp:
			return xURI.create("ftp", host, port, path);
		case http:
			return xURI.create("http", host, port, path);
		case hudup_rmi:
			return xURI.create("rmi", host, port, path);
		case hudup_socket:
			return xURI.create("hdp", host, port, path);
		case mssql:
			return xURI.create("jdbc:microsoft:sqlserver", host, port, path);
		case mysql:
			return xURI.create("jdbc:mysql", host, port, path);
		case oracle:
			return xURI.create("jdbc:oracle:thin", host, port, path);
		case postgresql:
			return xURI.create("jdbc:postgresql", host, port, path);
		default:
			return null;
			
		}
	}
	
	
	/**
	 * Testing whether the specific URI locates a compress files (zip, jar).
	 * Please see {@link xURI} for more details about URI.
	 * @param uri Specific URI
	 * @return Whether URI is compressed storage unit
	 */
	public static boolean isCompressed(xURI uri) {
		String spec = uri.toString();
		return spec.startsWith("zip:") || spec.startsWith("jar:");
	}
	
	
	/**
	 * Creating {@link DataDriver} from URI.
	 * For example, given a URI &quot;jdbc:mysql://localhost:3306/hudup&quot;, the data driver is &quot;mysql&quot;.
	 * Please see {@link xURI} for more details about URI.
	 * @param uri Specific URI
	 * @return Data driver from {@link xURI}
	 */
	public static DataDriver create(xURI uri) {
		String spec = uri.toString();
		if (spec.startsWith("jdbc:derby:")) {
			if (spec.contains("//"))
				return new DataDriver(DataType.derby);
			else
				return new DataDriver(DataType.derby_engine);
		}
		else if (spec.startsWith("file:"))
			return new DataDriver(DataType.file);
		else if (spec.startsWith("ftp:"))
			return new DataDriver(DataType.ftp);
		else if (spec.startsWith("http:"))
			return new DataDriver(DataType.http);
		else if (spec.startsWith("rmi:"))
			return new DataDriver(DataType.hudup_rmi);
		else if (spec.startsWith("hdp:"))
			return new DataDriver(DataType.hudup_socket);
		else if (spec.startsWith("jdbc:microsoft:sqlserver:"))
			return new DataDriver(DataType.mssql);
		else if (spec.startsWith("jdbc:mysql:"))
			return new DataDriver(DataType.mysql);
		else if (spec.startsWith("jdbc:oracle:"))
			return new DataDriver(DataType.oracle);
		else if (spec.startsWith("jdbc:postgresql:"))
			return new DataDriver(DataType.oracle);
		else if (isCompressed(uri)) {
			xSubURI subUri = createSubURI(uri);
			return create(subUri.brief);
		}
		else
			return null;
	}
	
	
	/**
	 * Extracting the sub path of specific connection URI.
	 * For example, given a connection URI &quot;jdbc:mysql://localhost:3306/hudup&quot;, the sub path is &quot;mysql://localhost:3306/hudup&quot;.
	 * @param uri Specific URI
	 * @return Sub path of specific connection URI, specified by {@link xSubURI}
	 */
	public static xSubURI createSubURI(xURI uri) {
		String spec = uri.toString();
		if (spec.startsWith("jdbc:derby:"))
			return new xSubURI("jdbc:derby", spec.substring(5));
		else if (spec.startsWith("file:"))
			return new xSubURI("file", spec.substring(0));
		else if (spec.startsWith("ftp:"))
			return new xSubURI("ftp", spec.substring(0));
		else if (spec.startsWith("http:"))
			return new xSubURI("http", spec.substring(0));
		else if (spec.startsWith("rmi:"))
			return new xSubURI("rmi", spec.substring(0));
		else if (spec.startsWith("hdp:"))
			return new xSubURI("hdp", spec.substring(0));
		else if (spec.startsWith("jdbc:microsoft:sqlserver:"))
			return new xSubURI("jdbc:microsoft:sqlserver", spec.substring(15));
		else if (spec.startsWith("jdbc:mysql:"))
			return new xSubURI("jdbc:mysql", spec.substring(5));
		else if (spec.startsWith("jdbc:oracle:"))
			return new xSubURI("jdbc:oracle", spec.substring(5));
		else if (spec.startsWith("jdbc:postgresql:"))
			return new xSubURI("jdbc:postgresql", spec.substring(5));
		else if (isCompressed(uri)) {
			int firstIndex = spec.indexOf(":");
			int secondIndex = spec.indexOf(":", firstIndex + 1);
			
			return new xSubURI(spec.substring(0, secondIndex), spec.substring(firstIndex + 1));
		}
		else
			return null;
	}

	
	/**
	 * Creating {@link DataDriver} from the associated class name.
	 * For example, given an associated class name &quot;com.mysql.jdbc.Driver&quot;, the returned data type is &quot;mysql&quot;.
	 * @param className Associated class name
	 * @return Data driver from third-party class name
	 */
	public static DataDriver createByInnerClass(String className) {
		
		if (className.equals(DERBY_DRIVER_CLASS_NAME))
			return new DataDriver(DataType.derby);
		if (className.equals(DERBY_ENGINE_DRIVER_CLASS_NAME))
			return new DataDriver(DataType.derby_engine);
		else if (className.equals(FILE_DRIVER_CLASS_NAME))
			return new DataDriver(DataType.file);
		else if (className.equals(FTP_DRIVER_CLASS_NAME))
			return new DataDriver(DataType.ftp);
		else if (className.equals(HTTP_DRIVER_CLASS_NAME))
			return new DataDriver(DataType.http);
		else if (className.equals(HUDUP_RMI_DRIVER_CLASS_NAME))
			return new DataDriver(DataType.hudup_rmi);
		else if (className.equals(HUDUP_SOCKET_DRIVER_CLASS_NAME))
			return new DataDriver(DataType.hudup_socket);
		else if (className.equals(MSSQL_DRIVER_CLASS_NAME))
			return new DataDriver(DataType.mssql);
		else if (className.equals(MYSQL_DRIVER_CLASS_NAME))
			return new DataDriver(DataType.mysql);
		else if (className.equals(ORACLE_DRIVER_CLASS_NAME))
			return new DataDriver(DataType.oracle);
		else if (className.equals(POSTGRESQL_DRIVER_CLASS_NAME))
			return new DataDriver(DataType.postgresql);
		else
			return null;
	
	}
	
	
	/**
	 * Creating {@link DataDriver} from the associated real class.
	 * For example, given an associated class com.mysql.jdbc.Driver.class, the returned driver type is &quot;mysql&quot;.
	 * @param driverClass Specific driver class
	 * @return Data driver from driver class
	 */
	public static DataDriver createByInnerClass(Class<?> driverClass) {
		return createByInnerClass(driverClass.getName());
	}
	
	
	/**
	 * Creating {@link DataDriver} from the specific name.
	 * For example, the method returns a {@link DataDriver} of MySQL database from the specific name &quot;mysql&quot;.
	 * @param name Specific name
	 * @return Data driver from name
	 */
	public static DataDriver createByName(String name) {
		if (name == null || name.isEmpty())
			return null;
		else if (name.equals("derby"))
			return new DataDriver(DataType.derby);
		else if (name.equals("derby_engine"))
			return new DataDriver(DataType.derby_engine);
		else if (name.equals("file"))
			return new DataDriver(DataType.file);
		else if (name.equals("ftp"))
			return new DataDriver(DataType.ftp);
		else if (name.equals("http"))
			return new DataDriver(DataType.http);
		else if (name.equals("hudup_rmi"))
			return new DataDriver(DataType.hudup_rmi);
		else if (name.equals("hudup_socket"))
			return new DataDriver(DataType.hudup_socket);
		else if (name.equals("mssql"))
			return new DataDriver(DataType.mssql);
		else if (name.equals("mysql"))
			return new DataDriver(DataType.mysql);
		else if (name.equals("oracle"))
			return new DataDriver(DataType.oracle);
		else if (name.equals("postgresql"))
			return new DataDriver(DataType.postgresql);
		else 
			return null;
	}
	
	
	@Override
	public Object clone() {
		return new DataDriver(type);
	}


}
