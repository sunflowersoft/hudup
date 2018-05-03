package net.hudup.server;

import net.hudup.core.client.ActiveMeasure;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ActiveMeasureImpl implements ActiveMeasure {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * 
	 */
	private int requestCount = 0;
	
	
	/**
	 * 
	 */
	public ActiveMeasureImpl() {
		
	}
	
	
	@Override
	public int compareTo(ActiveMeasure o) {
		// TODO Auto-generated method stub
		
		float a = this.compute();
		float b = o.compute();
		if (a < b)
			return -1;
		else if(a == b)
			return 0;
		else
			return 1;
	}

	
	@Override
	public float compute() {
		return requestCount;
	}
	
	
	@Override
	public synchronized void reset() {
		requestCount = 0;
	}
	
	
	@Override
	public int getRequestCount() {
		return requestCount;
	}
	
	
	@Override
	public synchronized void incRequestCount() {
		requestCount++;
	}
	
	
	@Override
	public synchronized void decRequestCount() {
		if (requestCount == 0)
			return;
		requestCount--;
	}

	
	@Override
	public synchronized void setRequestCount(int requestCount) {
		this.requestCount = Math.max(requestCount, 0);
	}


	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		float a = this.compute();
		float b = ((ActiveMeasure)obj).compute();
		return a == b;
	}
	
	
	
}
