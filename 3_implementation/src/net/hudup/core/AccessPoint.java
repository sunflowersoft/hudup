package net.hudup.core;


/**
 * This class is called {@code access point} which is used to start modules of Hudup framework.
 * There are 5 applications of Hudup framework such as {@code Evaluator}, {@code Server}, {@code Listener}, {@code Balancer}, {@code Toolkit}.
 * <ul>
 * <li>{@code Evaluator} makes evaluation on recommendation algorithms.</li>
 * <li>{@code Server} runs as a recommendation server that serves incoming user request for producing a list of recommended items or allowing users to access database.</li>
 * <li>{@code Listener} is responsible for receiving requests from users and dispatching such request to {@code Server}.
 * <li>{@code Balancer} is a special {@code Listener} that supports balancing.</li>
 * <li>{@code Toolkit} is a utility application that assists users to set up and modify Hudup database.</li>
 * </ul>
 * These applications must implement this interface. In fact, this interface plays the role of access point of any application. 
 * 
 * @author Loc Nguyen
 * @version 11.0
 *
 */
public interface AccessPoint {

	
	/**
	 * The main method of access point to start application.
	 * @param args argument parameter of main method. It contains command line arguments.
	 */
	void run(String[] args);
	
	
	/**
	 * Getting the name of this access point.
	 * @return the name of this access point.
	 */
	String getName();
	
	
}
