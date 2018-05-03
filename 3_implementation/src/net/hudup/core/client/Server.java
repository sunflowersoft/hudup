/**
 * 
 */
package net.hudup.core.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.hudup.core.data.DataConfig;

/**
 * {@code Server} interface defines abstract model of recommendation server. {@code Server} is responsible for creating {@code service} represented by {@code Service} interface to serve user requests.
 * {@code Server} starts, pauses, resumes, and stops by methods {@link #start()}, {@link #pause()}, {@link #resume()}, and {@link #stop()}, respectively.
 * {@code Server} interface must be implemented by programmer. Both {@code server} and {@code service} constitute a proper recommendation server (Hudup server).
 * As usual, server creates one or many service (s) for serving incoming requests.
 * {@code Server} extends {@link Remote}, which means that service supports RMI (abbreviation of Java Remote Method Invocation) for remote interaction in client-server architecture. The tutorial of RMI is available at <a href="https://docs.oracle.com/javase/tutorial/rmi">https://docs.oracle.com/javase/tutorial/rmi</a>.
 * <br><br>
 * Server has six statuses as follows:
 * <ul>
 * <li>&quot;started&quot;: Server started. If server has just started right after calling {@link #start()}, it is in two statuses: &quot;started&quot; and &quot;running&quot;.</li>
 * <li>&quot;paused&quot;: Server paused. Server are currently in both &quot;started&quot; status and &quot;paused&quot; status.</li>
 * <li>&quot;resumed&quot;: Server resumed. Server are currently in three statuses: &quot;started&quot;, &quot;resumed&quot;, and &quot;running&quot;.</li>
 * <li>&quot;stopped&quot;: Server stopped and so server is in &quot;stopped&quot; status.</li>
 * <li>&quot;setconfig&quot;: Server is in setting its configuration. Sever must stop so that it can set its configuration. So server is now in two statuses: &quot;stopped&quot; and &quot;setconfig&quot;.</li>
 * <li>&quot;exit&quot;: Server exits and so server does not exist. Status &quot;exit&quot; is unreal.</li>
 * </ul>
 * Some statuses are overlapped; for example, server is in status &quot;paused&quot; is also in status &quot;started&quot;.
 * When server changes its current status by calling methods such as {@link #start()}, {@link #pause()}, {@link #resume()}, {@link #stop()}, and {@link #exit()}.
 * Methods such as {@link #isStarted()}, {@link #isPaused()}, and {@link #isRunning()} are used to query current status of server. 
 * Every time server change its current status, it notifies an event {@code ServerStatusEvent} to listeners that implement {@link ServerStatusListener} interface.
 * Such listeners are called {@code server status listeners}, which must be registered with server by method {@link #addStatusListener(ServerStatusListener)} to receive server status events.
 * A {@code server status event} is removed from server by calling {@link #removeStatusListener(ServerStatusListener)}.
 * <br><br>
 * Within client-server architecture, the popular recommendation scenario includes five following steps in top-down order:
 * <ol>
 * <li>
 * User (or client application) specifies her / his request in text format. Typical client application is the {@code Evaluator} module.
 * {@code Interpreter} component in {@code interface layer} parses such text into JSON format request. {@code Listener} component in interface layer sends JSON format request to service layer.
 * In distributed environment, {@code balancer} is responsible for choosing optimal service layer site to send JSON request.
 * </li>
 * <li>
 * {Service layer} receives JSON request from interface layer. There are two occasions:
 * <ul>
 * <li>
 * Request is to get favorite items. In this case, request is passed to recommender service. Recommender service applies appropriate strategy into producing a list of favorite items.
 * If snapshot (or scanner) necessary to recommendation algorithms is not available in {@code share memory layer}, recommender service requires storage service to create {@code snapshot} (or {@code scanner}).
 * After that, the list of favorite items is sent back to interface layer as {@code JSON format result}.
 * </li>
 * <li>
 * Request is to retrieve or update data such as querying item profile, querying average rating on specified item, rating an item, and modifying user profile.
 * In this case, request is passed to storage service. If request is to update data then, an {@code update request} is sent to transaction layer.
 * If request is to retrieve information then {@code storage service} looks up share memory layer to find out appropriate snapshot or scanner.
 * If such snapshot (or scanner) does not exists nor contains requisite information then, a {@code retrieval request} is sent to transaction layer;
 * otherwise, in found case, requisite information is extracted from found snapshot (or scanner) and sent back to interface layer as {@code JSON format result}.
 * </li>
 * </ul>
 * </li>
 * <li>
 * {@code Transaction layer} analyzes {@code update requests} and {@code retrieval requests} from service layer and parses them into transactions.
 * Each transaction is a bunch of read and write operations. All low-level operations are harmonized in terms of concurrency requirement and sent to data layer later.
 * Some access concurrency algorithms can be used according to pre-defined isolation level.
 * </li>
 * <li>
 * {@code Data layer} processes read and write operations and sends back {@code raw result} to transaction layer. Raw result is the piece of information stored in {@code Dataset} and {@code KBase}.
 * Raw result can be output variable indicating whether or not update (write) request is processed successfully. Transaction layer collects and sends back the raw result to service layer.
 * Service layer translates raw result into {@code JSON format result} and sends such translated result to interface layer in succession.
 * </li>
 * <li>
 * The {@code interpreter} component in interface layer receives and translates {@code JSON format result} into text format result easily understandable for users.
 * </li>
 * </ol>
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface Server extends Remote {

	
	/**
	 * Server starts.
	 * @throws RemoteException if any error raises.
	 */
	void start() throws RemoteException;

	
	/**
	 * Server pauses.
	 * @throws RemoteException if any error raises.
	 */
	void pause() throws RemoteException;

	
	/**
	 * Server resumes
	 * @throws RemoteException if any error raises.
	 */
	void resume() throws RemoteException;

	
	/**
	 * Server stops. After server stopped, it can re-start by calling {@link #start()}.
	 * @throws RemoteException if any error raises.
	 */
	void stop() throws RemoteException;
	
	
	/**
	 * Exiting server. After server is exited, it is removed from memory and it cannot be started again.
	 * @throws RemoteException if any error raises.
	 */
	void exit() throws RemoteException;
	

	/**
	 * Testing whether server started.
	 * @return whether server started.
	 * @throws RemoteException if any error raises.
	 */
	boolean isStarted() throws RemoteException;
	
	
	/**
	 * Testing whether server paused.
	 * @return whether server paused.
	 * @throws RemoteException if any error raises.
	 */
	boolean isPaused() throws RemoteException;
	
	
	/**
	 * Testing whether server is running.
	 * @return whether server is running.
	 * @throws RemoteException if any error raises.
	 */
	boolean isRunning() throws RemoteException;

	
	
    /**
     * Every server owns an internal configuration. This method returns such configuration.
     * @return {@link DataConfig}
	 * @throws RemoteException if any error raises.
     */
	DataConfig getConfig() throws RemoteException;
    
	
	/**
	 * Every server owns an internal configuration. This method sets a new configuration.
	 * @param config new configuration.
	 * @throws RemoteException if any error raises.
	 */
	void setConfig(DataConfig config) throws RemoteException;

	
	/**
	 * Server has six statuses as follows:
	 * <ul>
	 * <li>&quot;started&quot;: Server started. If server has just started right after calling {@link #start()}, it is in two statuses: &quot;started&quot; and &quot;running&quot;.</li>
	 * <li>&quot;paused&quot;: Server paused. Server are currently in both &quot;started&quot; status and &quot;paused&quot; status.</li>
	 * <li>&quot;resumed&quot;: Server resumed. Server are currently in three statuses: &quot;started&quot;, &quot;resumed&quot;, and &quot;running&quot;.</li>
	 * <li>&quot;stopped&quot;: Server stopped and so server is in &quot;stopped&quot; status.</li>
	 * <li>&quot;setconfig&quot;: Server is in setting its configuration. Sever must stop so that it can set its configuration. So server is now in two statuses: &quot;stopped&quot; and &quot;setconfig&quot;.</li>
	 * <li>&quot;exit&quot;: Server exits and so server does not exist. Status &quot;exit&quot; is unreal.</li>
	 * </ul>
	 * Some statuses are overlapped; for example, server is in status &quot;paused&quot; is also in status &quot;started&quot;.
	 * When server changes its current status by calling methods such as {@link #start()}, {@link #pause()}, {@link #resume()}, {@link #stop()}, and {@link #exit()}.
	 * Methods such as {@link #isStarted()}, {@link #isPaused()}, and {@link #isRunning()} are used to query current status of server. 
	 * Every time server change its current status, it notifies an event {@code ServerStatusEvent} to listeners that implement {@link ServerStatusListener} interface.
	 * Such listeners are called {@code server status listeners}, which must be registered with server by this method {@link #addStatusListener(ServerStatusListener)} to receive server status events.
	 * A {@code server status event} is removed from server by calling {@link #removeStatusListener(ServerStatusListener)}.
	 * 
	 * @param listener the {@code server status listener} that is registered into this server in order to receive server status event represented by {@code ServerStatusEvent} class.
	 * @return whether add listener successfully
	 * @throws RemoteException if any error raises.
	 */
	boolean addStatusListener(ServerStatusListener listener) throws RemoteException;
	
	
	/**
	 * Server has six statuses as follows:
	 * <ul>
	 * <li>&quot;started&quot;: Server started. If server has just started right after calling {@link #start()}, it is in two statuses: &quot;started&quot; and &quot;running&quot;.</li>
	 * <li>&quot;paused&quot;: Server paused. Server are currently in both &quot;started&quot; status and &quot;paused&quot; status.</li>
	 * <li>&quot;resumed&quot;: Server resumed. Server are currently in three statuses: &quot;started&quot;, &quot;resumed&quot;, and &quot;running&quot;.</li>
	 * <li>&quot;stopped&quot;: Server stopped and so server is in &quot;stopped&quot; status.</li>
	 * <li>&quot;setconfig&quot;: Server is in setting its configuration. Sever must stop so that it can set its configuration. So server is now in two statuses: &quot;stopped&quot; and &quot;setconfig&quot;.</li>
	 * <li>&quot;exit&quot;: Server exits and so server does not exist. Status &quot;exit&quot; is unreal.</li>
	 * </ul>
	 * Some statuses are overlapped; for example, server is in status &quot;paused&quot; is also in status &quot;started&quot;.
	 * When server changes its current status by calling methods such as {@link #start()}, {@link #pause()}, {@link #resume()}, {@link #stop()}, and {@link #exit()}.
	 * Methods such as {@link #isStarted()}, {@link #isPaused()}, and {@link #isRunning()} are used to query current status of server. 
	 * Every time server change its current status, it notifies an event {@code ServerStatusEvent} to listeners that implement {@link ServerStatusListener} interface.
	 * Such listeners are called {@code server status listeners}, which must be registered with server by this method {@link #addStatusListener(ServerStatusListener)} to receive server status events.
	 * A {@code server status event} is unregistered from server by calling this method {@link #removeStatusListener(ServerStatusListener)}.
	 * @param listener the {@code server status listener} that is unregistered from server and it no longer received server status event.
	 * @return whether add listener successfully
	 * @throws RemoteException if any error raises.
	 */
	boolean removeStatusListener(ServerStatusListener listener) throws RemoteException;
    
    
    /**
     * Testing whether server is working properly even though it stopped.
	 * @return whether ping successfully.
     * @throws RemoteException if any error raises.
     */
	boolean ping() throws RemoteException;
	
	
}
