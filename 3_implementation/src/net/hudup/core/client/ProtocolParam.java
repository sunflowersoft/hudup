package net.hudup.core.client;

import java.io.Serializable;


/**
 * This abstract class represents a parameter for Hudup protocol. As a convention, it is called {@code protocol parameter}.
 * Two directed implementations of this {@code protocol parameter} are request represented by {@code Request} class and response represented by {@code Response} class.
 * Request includes recommendation request, retrieval request, update request.
 * Response stores result of Hudup services corresponding a particular request.
 * Both request and response extend directly this protocol parameter so they have a tight interaction.
 * <br>
 * Note, the Hudup protocol represented by {@code Protocol} interface establishes an interaction protocol of Hudup client-server network, which is named &quot;<b>hdp</b>&quot;.
 * Methods of {@code Protocol} uses request and response as returned values.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class ProtocolParam implements Serializable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Identifier of Hudup protocol (hdp).
	 */
	public int  protocol = net.hudup.core.client.Protocol.HDP_PROTOCOL;

	
	/**
	 * Default constructor.
	 */
	public ProtocolParam() {
		
	}
	
	
	/**
	 * Converting this parameter into JSON format.
	 * @return JSON text form of this parameter. Note, JSON (JavaScript Object Notation) is a human-read format used for interchange between many protocols, available at <a href="http://www.json.org/">http://www.json.org</a>.
	 */
	public abstract String toJson();
	
	
}
