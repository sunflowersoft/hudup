package net.hudup.core.client;

import java.io.Serializable;

import net.hudup.core.Cloneable;
import net.hudup.core.Constants;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;

/**
 * This class stores essential information about server. In this current implementation, it only stores the URI specified by {@link xURI} of Hudup sever in its variable {@link #uri}.
 * However, such URI contains many information about sever such as server port, user information.
 * An example of server URI is &quot;http://localhost:10152/hudup&quot;.
 * Note that URI, abbreviation of Uniform Resource Identifier, is the string of characters used to identify a resource on Internet.
 * Please refer to https://en.wikipedia.org/wiki/Uniform_Resource_Identifier in order to comprehend URI
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ServerInfo implements Serializable, TextParsable, Cloneable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * URI of Hudup server. An example of server URI is &quot;http://localhost:10152/hudup&quot;
	 */
	protected String uri = null;
	
	
	/**
	 * Default constructor.
	 */
	public ServerInfo() {
		this.uri = xURI.create("hdp://localhost:" + Constants.DEFAULT_LISTENER_PORT).toString();
	}
	
	
	/**
	 * Constructor with specified URI.
	 * @param uri specified URI.
	 */
	public ServerInfo(xURI uri) {
		this.uri = uri.toString();
	}
	
	
	/**
	 * Constructor with specified URI in text form.
	 * @param uri specified URI in text form.
	 */
	public ServerInfo(String uri) {
		xURI xuri = xURI.create(uri);
		if (xuri != null)
			this.uri = xuri.toString();
	}

	
	/**
	 * Testing whether or not this server information is valid.
	 * Such information is valid if the URI of Hudup server is not null.
	 * @return  whether or not this server information is valid.
	 */
	public boolean validate() {
		return (uri != null);
	}
	
	
	/**
	 * Getting URI of server.
	 * @return URI of server.
	 */
	public xURI getUri() {
		return xURI.create(uri);
	}
	
	
	/**
	 * Setting server URI by specified URI.
	 * @param uri specified URI.
	 */
	public void setUri(xURI uri) {
		this.uri = uri.toString();
	}
	
	
	/**
	 * Getting the host of server.
	 * @return host of server.
	 */
	public String getHost() {
		return getUri().getHost();
	}

	
	/**
	 * Setting server host by specified host.
	 * @param host specified host.
	 */
	public void setHost(String host) {
		int port = ( uri == null ? Constants.DEFAULT_LISTENER_PORT : getUri().getPort() );
		uri = xURI.create("hdp://" + host + ":" + port).toString();
	}
	
	
	/**
	 * Getting server port.
	 * @return port of server.
	 */
	public int getPort() {
		return getUri().getPort();
	}
	
	
	
	/**
	 * Setting server port by specified port.
	 * @param port specified port.
	 */
	public void setPort(int port) {
		String host = ( uri == null ? "localhost" : getUri().getHost() );
		uri = xURI.create("hdp://" + host + ":" + port).toString();
	}


	@Override
	public String toText() {
		// TODO Auto-generated method stub
		return TextParserUtil.encryptReservedChars(uri.toString());
	}


	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		uri = TextParserUtil.decryptReservedChars(spec);
	}


	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return xURI.create(uri.toString());
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return toText();
	}
	
	
}
