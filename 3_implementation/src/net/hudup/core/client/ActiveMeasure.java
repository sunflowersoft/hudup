package net.hudup.core.client;

import java.io.Serializable;


/**
 * {@link ActiveMeasure} interface specifies how to measure the active degree of {@link PowerServer}.
 * The higher active degree is, the more busy the {@link PowerServer} is.
 * Note, {@link PowerServer} is a powerful {@link Server} with advanced functions such as supporting balancing and retrieving service.
 * For example, there is a counter inside {@link ActiveMeasure}. Each time {@link PowerServer} receives a user request, the counter is increased by 1.
 * After {@link PowerServer} finishes serving such user request, the counter is decreased by 1.
 * If there are many {@link PowerServer} (s) deployed on different sites, balancer uses {@link ActiveMeasure} to determine which {@link PowerServer} is least busy in order to dispatch user request to such {@link PowerServer} whose active counter is smallest.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface ActiveMeasure extends Comparable<ActiveMeasure>, Serializable {
	
	
	/**
	 * Calculating the active indication or active degree.
	 * By default, this method returns the internal request counter.
	 * 
	 * @return active indication or active degree.
	 */
	float compute();
	
	
	/**
	 * Reseting this {@link ActiveMeasure}. By default, this method resets the internal request to be 0. 
	 */
	void reset();
	
	
	/**
	 * Getting the internal request counter.
	 * @return count of requests.
	 */
	int getRequestCount();
	
	
	/**
	 * Increasing the request counter by 1. This occurs every time {@link PowerServer} receives an incoming request.
	 */
	void incRequestCount();
	
	
	/**
	 * Decreasing the request counter by 1. This occurs every time {@link PowerServer} finished to serve a request.
	 */
	void decRequestCount();

	
	/**
	 * Setting the request count by specified count.
	 * @param requestCount specified count.
	 */
	void setRequestCount(int requestCount);


	@Override
	boolean equals(Object obj);
	
	
}
