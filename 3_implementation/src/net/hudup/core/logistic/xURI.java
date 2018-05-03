package net.hudup.core.logistic;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.data.DataDriver;
import net.hudup.core.parser.TextParserUtil;


/**
 * This class is a wrapper of URI. Note that URI, abbreviation of Uniform Resource Identifier, is the string of characters used to identify a resource on Internet.
 * Please refer to https://en.wikipedia.org/wiki/Uniform_Resource_Identifier in order to comprehend URI.
 * The class <i>xURI</i> provides many utility methods which process URI.
 * All URI (s) used in the framework are represented by <i>xURI</i> and so we can identify {@link URI} with this {@link xURI}.<br>
 * A hierarchical URI follows the syntax below: 
 * <blockquote>
 * [<i>scheme</i><tt><b>:</b></tt>][<tt><b>//</b></tt><i>authority</i>][<i>path</i>][<tt><b>?</b></tt><i>query</i>][<tt><b>#</b></tt><i>fragment</i>]
 * </blockquote>
 * The authority component follows the syntax below:
 * <blockquote>
 * [<i>user-info</i><tt><b>@</b></tt>]<i>host</i>[<tt><b>:</b></tt><i>port</i>]
 * </blockquote>
 * Note, square brackets [...] delineate optional components.<br>
 * For example, given the URI &ldquo;http://jack:secret@localhost:8080/hudup/recommend?itemid=1#filter=0&ldquo;<br>
 * Scheme is &ldquo;http&rdquo;. Host is &ldquo;localhost&rdquo;. Port is &ldquo;8080&rdquo;.
 * User-info is &ldquo;jack:secret&rdquo; in which user name is &ldquo;jack&rdquo; and password is &ldquo;secret&rdquo;. So the authority is &ldquo;jack:secret@localhost:8080&rdquo;. 
 * Path is &ldquo;/hudup/recommend&rdquo;. Query is &ldquo;itemid=1&rdquo;. Fragment is &ldquo;filter=0&rdquo;.
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class xURI implements Serializable, net.hudup.core.Cloneable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * The internal URI wrapped by this class.
	 */
	protected URI uri = null;
	
	
	/**
	 * Constructor with {@link URI}.
	 * @param uri Specified URI
	 */
	private xURI(URI uri) {
		// TODO Auto-generated constructor stub
		this.uri = uri;
	}


	/**
	 * Getting the internal URI {@link #uri}.
	 * @return {@link URI}
	 */
	public URI getURI() {
		return uri;
	}
	
	
	/**
	 * Getting the scheme of URI. 
	 * @return the scheme of URI
	 */
	public String getScheme() {
		xSubURI info = DataDriver.createSubURI(this);
		if (info != null)
			return info.scheme;
		else
			return uri.getScheme();
	}
	
	
	/**
	 * Getting the host of URI.
	 * @return the host of URI
	 */
	public String getHost() {
		xSubURI info = DataDriver.createSubURI(this);
		if (info != null)
			return info.brief.uri.getHost();
		else
			return uri.getHost();
	}

	
	/**
	 * Getting the port of URI.
	 * @return the port of URI.
	 */
	public int getPort() {
		xSubURI info = DataDriver.createSubURI(this);
		if (info != null)
			return info.brief.uri.getPort();
		else
			return uri.getPort();
	}
	
	
	/**
	 * Getting the user name (if existing) in the URI. The user name is coded in the <i>user-info</i> of authority component.
	 * Currently, this method returns null because it is not implemented yet.
	 * @return user name (if available) in the URI
	 */
	public String getUserName() {
		return null;
	}
	
	
	/**
	 * Getting the password (if existing) in the URI. The user name is coded in the <i>user-info</i> of authority component.
	 * Currently, this method returns null because it is not implemented yet.
	 * @return user name (if available) in the URI
	 */
	public String getPassword() {
		return null;
	}
	
	
	/**
	 * Getting the path of URI.
	 * @return path of URI
	 */
	public String getPath() {
		xSubURI info = DataDriver.createSubURI(this);
		if (info != null)
			return info.brief.uri.getPath();
		else
			return uri.getPath();
	}
	
	
	/**
	 * Getting the query of URI.
	 * @return query of URI
	 */
	public String getQuery() {
		xSubURI info = DataDriver.createSubURI(this);
		if (info != null)
			return info.brief.uri.getQuery();
		else
			return uri.getQuery();
	}
	
	
	/**
	 * Getting the fragment of URI.
	 * @return fragment of URI
	 */
	public String getFragment() {
		xSubURI info = DataDriver.createSubURI(this);
		if (info != null)
			return info.brief.uri.getFragment();
		else
			return uri.getFragment();
	}
	
	
	/**
	 * Converting URI into {@link URL}.
	 * @return The converted {@link URL}
	 */
	public URL toURL() {
		try {
			
			return uri.toURL();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return uri.toString();
	}


	@Override
	public boolean equals(Object obj) {
		return uri.equals(((xURI)obj).uri);
	}
	
	
	/**
	 * Wrapping URI by this class {@link xURI}.
	 * @param uri Specified URI
	 * @return {@link xURI}
	 */
    public static xURI create(URI uri) {
    	if (uri == null)
    		return null;
    	else
    		return new xURI(uri);
    }
    
    
	/**
	 * Creating URI from {@link File}.
	 * @param file Specified file
	 * @return extended URI
	 */
    public static xURI create(File file) {
    	try {
    		return new xURI(file.toURI());
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	return null;
    }
    		
    
    /**
     * Creating URI from {@link Path}.
     * @param path Specified path
     * @return {@link xURI}
     */
    public static xURI create(Path path) {
    	URI uri = null;
    	try {
    		uri = path.toUri();
    	}
    	catch (Throwable e) {
    		e.printStackTrace();
    		
    		uri = null;
    	}
    	
    	if (uri == null)
    		return null;
    	else
    		return new xURI(uri);
    }
    
    
	/**
	 * Creating URI from its essential components.
	 * @param scheme Specified scheme
	 * @param host Specified host
	 * @param port Specified port
	 * @param path Specified path
	 * @param query Specified query
	 * @param fragment Specified fragment
	 * @return extended URI
	 */
    public static xURI create(
    		String scheme,
            String host, int port,
            String path, String query, String fragment) {
    
    	URI uri = null;
		try {
			if (scheme != null && scheme.equals("file")) {
				host = null;
				port = -1;
			}
			
			if (path != null && !path.startsWith("/"))
				path = "/" + path;
			
			uri = new URI(
					scheme, 
					null, 
					host, 
					port, 
					path, 
					query, 
					fragment);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			uri = null;
		}
		
		if (uri == null)
			return null;
		else
			return new xURI(uri);
	}
    
    
    /**
     * Creating URI from its essential components.
     * @param scheme Specified scheme
     * @param host Specified host
     * @param port Specified port
     * @param path Specified path
	 * @return extended URI
     */
    public static xURI create(
    		String scheme,
            String host, int port,
            String path) {
    
    	URI uri = null;
		try {
			
			if (scheme != null && scheme.equals("file")) {
				host = null;
				port = -1;
			}

			if (path != null && !path.startsWith("/"))
				path = "/" + path;
			
			uri = new URI(
					scheme, 
					null, 
					host, 
					port, 
					path,
					null,
					null);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			uri = null;
		}
		
		if (uri == null)
			return null;
		else
			return new xURI(uri);
	}
    
    
    /**
     * Creating URI from specific string.
     * @param urispec Specific string
	 * @return extended URI
     */
    public static xURI create(String urispec) {
    	
    	URI uri = null;
    	try {
			uri = new URI(urispec);
		} 
    	catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			uri = null;
		}
    	
		if (uri == null)
			return null;
		else if (uri.getScheme() == null) {
			UriAdapter adapter = new UriAdapter();
			Path path = adapter.newPath(urispec);
			adapter.close();
			
			return new xURI(path.toUri());
		}
		else
			return new xURI(uri);
    }
    
    
	/**
	 * Contacting the path of this URI with the specified sub-path.
	 * @param subpath Specified sub-path
	 * @return extended URI
	 */
	public xURI concat(String subpath) {
		String path = decode(getPath());
		if (path == null)
			return null;
		
		if (subpath.startsWith("/") || path.endsWith("/"))
			path += subpath;
		else
			path += "/" + subpath;
		
		return create(
				getScheme(), 
				getHost(), 
				getPort(), 
				path, 
				getQuery(), 
				getFragment());
	}

	
	/**
	 * Getting last name of the path of URI. Given &ldquo;http://localhost/server/guidelines.html&rdquo;, the last name is &ldquo;guidelines.html&rdquo;.
	 * @return Last name of the path of URI
	 */
	public String getLastName() {
		String path = decode(getPath());
		return getLastName(path);
	}
	
	
	/**
	 * Getting extension of the last name of the path of URI. Given &ldquo;http://localhost/server/guidelines.html&rdquo;, the extension is &ldquo;html&rdquo;.
	 * @return Extension of the last name of the path of URI
	 */
	public String getLastNameExtension() {
		String lastName = getLastName();
		if (lastName == null || lastName.isEmpty())
			return null;
		
		else {
			String ext = null;
	        int i = lastName.lastIndexOf('.');
	        if (i > 0 &&  i < lastName.length() - 1)
	            ext = lastName.substring(i + 1).toLowerCase();
	        
	        return ext;
		}
	}

	
	/**
	 * Getting last name of specified path. Given path &ldquo;/server/guidelines.html&rdquo;, the last name is &ldquo;guidelines.html&rdquo;.
	 * @param path Specified path
	 * @return Last name of the specified path
	 */
	private static String getLastName(String path) {
		if (path == null || path.isEmpty() || path.equals("/"))
			return null;
		
		int index = path.lastIndexOf("/");
		if (index == -1)
			return path;
		else if (index < path.length() - 1)
			return path.substring(index + 1);
		else {
			path = path.substring(0, index);
			return getLastName(path);
		}
	}
	
	
	/**
	 * Parsing the specified parameter string into a map of key-value pairs. For example, the string &ldquo;itemid=1&amp;userid=2&rdquo; is parsed into a {@link Map} having two entries
	 * in which the first entry has key &ldquo;itemid&rdquo; and value &ldquo;1&rdquo; and the second entry has key &ldquo;userid&rdquo; and value &ldquo;2&rdquo;.
	 * @param paramList Specified parameter string
	 * @return Map of parameters
	 */
	public static Map<String, String> parseParameterText(String paramList) {
		Map<String, String> map = Util.newMap();
		if (paramList == null || paramList.isEmpty())
			return map;
		
		paramList = decode(paramList);
		
		List<String> textList = TextParserUtil.split(paramList, "&", null);
		for (String text : textList) {
			List<String> keyValue = TextParserUtil.split(text, "=", null);
			if (keyValue.size() < 2)
				continue;
			
			map.put(keyValue.get(0), keyValue.get(1));
		}
		
		return map;
	}
	

	/**
	 * Converting a map of key-value pairs into a string. This method is opposite to the method {@link #parseParameterText(String)}.
	 * Given a {@link Map} having two entries in which the first entry has key &ldquo;itemid&rdquo; and value &ldquo;1&rdquo; and the second entry has key &ldquo;userid&rdquo; and value &ldquo;2&rdquo;,
	 * this method converts such map into the string &ldquo;itemid=1&amp;userid=2&rdquo;. 
	 * @param map Specified {@link Map}
	 * @param encode whether encoding according to URL format
	 * @return parameter text form
	 */
	public static String toParameterText(Map<String, String> map, boolean encode) {
		StringBuffer buffer = new StringBuffer();
		if (map == null || map.isEmpty())
			return buffer.toString();
		
		Set<String> keys = map.keySet();
		int i = 0;
		for (String key : keys) {
			if (i > 0)
				buffer.append("&");
			
			String value = map.get(key);
			buffer.append(key + "=" + value);
			i++;
		}
		
		String params = buffer.toString();
		if (encode)
			params = encode(params);
		
		return params;
	}

	
	/**
	 * Encoding normal text according to <i>url-encoded</i> format. For example, the normal string &ldquo;jack secret&rdquo; is encoded into <i>url-encoded</i> string &ldquo;jack%20secret&rdquo;.
	 * Please see {@link URLEncoder#encode(String)}.
	 * @param text Specified text
	 * @return encoded text according to URL format
	 */
	private static String encode(String text) {
		if (text == null)
			return null;
		
		try {
			text = URLEncoder.encode(text, "UTF-8");
		} 
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return text;
	}
	
	
	/**
	 * Decoding <i>url-encoded</i> text into normal text. For example, the <i>url-encoded</i> string &ldquo;jack%20secret&rdquo; is decoded into normal string &ldquo;jack secret&rdquo;.
	 * Please see {@link URLDecoder#decode(String)}.
	 * @param text
	 * @return plain text
	 */
	private static String decode(String text) {
		if (text == null)
			return null;
		
		try {
			text = URLDecoder.decode(text, "UTF-8");
		} 
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return text;
	}


	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		if (uri == null)
			return null;
		else
			return create(uri.toString());
	}
	
	
}
