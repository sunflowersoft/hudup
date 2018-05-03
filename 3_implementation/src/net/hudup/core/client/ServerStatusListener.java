/**
 * 
 */
package net.hudup.core.client;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.EventListener;


/**
* Server represented by {@code Server} class has six statuses as follows:
 * <ul>
 * <li>&quot;started&quot;: Server started. If server has just started right after calling {@code Server#start()}, it is in two statuses: &quot;started&quot; and &quot;running&quot;.</li>
 * <li>&quot;paused&quot;: Server paused. Server are currently in both &quot;started&quot; status and &quot;paused&quot; status.</li>
 * <li>&quot;resumed&quot;: Server resumed. Server are currently in three statuses: &quot;started&quot;, &quot;resumed&quot;, and &quot;running&quot;.</li>
 * <li>&quot;stopped&quot;: Server stopped and so server is in &quot;stopped&quot; status.</li>
 * <li>&quot;setconfig&quot;: Server is in setting its configuration. Sever must stop so that it can set its configuration. So server is now in two statuses: &quot;stopped&quot; and &quot;setconfig&quot;.</li>
 * <li>&quot;exit&quot;: Server exits and so server does not exist. Status &quot;exit&quot; is unreal.</li>
 * </ul>
 * Some statuses are overlapped; for example, server is in status &quot;paused&quot; is also in status &quot;started&quot;.
 * When server changes its current status by calling methods such as {@code Server#start()}, {@code Server#pause()}, {@code Server#resume()}, {@code Server#stop()}, and {@code Server#exit()}.
 * Methods such as {@code Server#isStarted()}, {@code Server#isPaused()}, and {@code Server#isRunning()} are used to query current status of server. 
 * Every time server change its current status, it notifies an event {@code ServerStatusEvent} to listeners that implement this {@code ServerStatusListener} interface.
 * Such listeners are called {@code server status listeners}, which must be registered with server by method {@code #addStatusListener(ServerStatusListener)} to receive server status events.
 * A {@code server status event} is removed from server by calling {@code #removeStatusListener(ServerStatusListener)}.
 * <br><br>
 * Server status listener defines its method {@link #statusChanged(ServerStatusEvent)} to do some particular tasks when received server status event from server.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface ServerStatusListener extends EventListener, Remote, Serializable {

	
	/**
	 * Any class implementing this interface is responsible for defining this method {@code statusChanged(ServerStatusEvent)} to do some particular tasks when received server status event from server.
	 * @param evt specified status event received from {@code Server}.
	 * @throws RemoteException if any error raises.
	 */
	void statusChanged(ServerStatusEvent evt) throws RemoteException;
	
	
}
