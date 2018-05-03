/**
 * 
 */
package net.hudup.core.client;

import java.util.EventObject;


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
 * Every time server change its current status, it notifies an event {@code ServerStatusEvent} to listeners that implement {@code ServerStatusListener} interface.
 * As a convention, this {@code ServerStatusEvent} class is called {@code server status event}.
 * Such listeners are called {@code server status listeners}, which must be registered with server by method {@code #addStatusListener(ServerStatusListener)} to receive server status events.
 * A {@code server status event} is removed from server by calling {@code #removeStatusListener(ServerStatusListener)}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ServerStatusEvent extends EventObject {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * This enum defines six status of server represented by {@code Server} class.
	 * <ul>
	 * <li>&quot;started&quot;: Server started. If server has just started right after calling {@code Server#start()}, it is in two statuses: &quot;started&quot; and &quot;running&quot;.</li>
	 * <li>&quot;paused&quot;: Server paused. Server are currently in both &quot;started&quot; status and &quot;paused&quot; status.</li>
	 * <li>&quot;resumed&quot;: Server resumed. Server are currently in three statuses: &quot;started&quot;, &quot;resumed&quot;, and &quot;running&quot;.</li>
	 * <li>&quot;stopped&quot;: Server stopped and so server is in &quot;stopped&quot; status.</li>
	 * <li>&quot;setconfig&quot;: Server is in setting its configuration. Sever must stop so that it can set its configuration. So server is now in two statuses: &quot;stopped&quot; and &quot;setconfig&quot;.</li>
	 * <li>&quot;exit&quot;: Server exits and so server does not exist. Status &quot;exit&quot; is unreal.</li>
	 * </ul>
	 * 
	 * @author Loc Nguyen
	 * @version 10.0
	 *
	 */
	public enum Status {
		
		/**
		 * &quot;started&quot; status. If server has just started right after calling {@code Server#start()}, it is in two statuses: &quot;started&quot; and &quot;running&quot;.
		 */
		started,
		
		/**
		 * &quot;paused&quot; status. Server are currently in both &quot;started&quot; status and &quot;paused&quot; status.
		 */
		paused,
		
		/**
		 * &quot;resumed&quot; status. Server are currently in three statuses: &quot;started&quot;, &quot;resumed&quot;, and &quot;running&quot;.
		 */
		resumed,
		
		/**
		 * &quot;stopped&quot; status. Server stopped and so server is in &quot;stopped&quot; status.
		 */
		stopped,
		
		/**
		 * &quot;setconfig&quot; status. Server is in setting its configuration. Sever must stop so that it can set its configuration. So server is now in two statuses: &quot;stopped&quot; and &quot;setconfig&quot;.
		 */
		setconfig,
		
		/**
		 * &quot;exit&quot; status. Server exits and so server does not exist. Status &quot;exit&quot; is unreal.
		 */
		exit
	}
	
	
	/**
	 * The current status that server stores in this event.
	 */
	protected Status status = Status.started;
	
	
	/**
	 * The message that server stores in this event.  
	 */
	protected String msg = "";
	
	
	/**
	 * Shutdown hook is the mechanism in which some tasks are done before the sever shutdown really.
	 * If this variable is {@code true}, this event indicates there is a mechanism of shutdown hook in server.
	 * The default value of this variable is {@code false}.
	 */
	protected boolean shutdownHookStatus = false;
	
	
	/**
	 * Constructor with specified server and specified status.
	 * @param server specified server.
	 * @param status specified status.
	 */
	public ServerStatusEvent(Server server, Status status) {
		super(server);
		// TODO Auto-generated constructor stub
		
		this.status = status;
	}

	
	/**
	 * Constructor with specified server, status, and message.
	 * @param server specified server.
	 * @param status specified status.
	 * @param msg specified status.
	 */
	public ServerStatusEvent(Server server, Status status, String msg) {
		this(server, status);
		// TODO Auto-generated constructor stub
		
		this.msg = msg;
	}
	
	
	/**
	 * Getting status given in this event.
	 * @return status given in this event.
	 */
	public Status getStatus() {
		return status;
	}
	
	
	/**
	 * Setting specified status in this event.
	 * @param status specified status.
	 */
	public void setStatus(Status status) {
		this.status = status;
	}
	
	
	/**
	 * Getting message given in this event.
	 * @return message given in this event.
	 */
	public String getMsg() {
		return msg;
	}
	
	
	
	/**
	 * Setting specified message in this event.
	 * @param msg specified message.
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
	/**
	 * Shutdown hook is the mechanism in which some tasks are done before the sever shutdown really.
	 * This method checks whether or not the mechanism of shutdown hook is supported in server.
	 * @return whether or not the mechanism of shutdown hook is supported in server.
	 */
	public boolean getShutdownHookStatus() {
		return shutdownHookStatus;
	}
	
	
	/**
	 * Shutdown hook is the mechanism in which some tasks are done before the sever shutdown really.
	 * This method sets whether or not the mechanism of shutdown hook is supported in server.
	 * @param shutdownHookStatus if {@code true}, the mechanism of shutdown hook is supported in server.
	 */
	public void setShutdownHookStatus(boolean shutdownHookStatus) {
		this.shutdownHookStatus = shutdownHookStatus;
	}

	
	@Override
	public String toString() {
		return "Event status " + status + " with message " + msg;
	}
	
	
}
