package net.hudup.listener;

import java.util.Map;

import net.hudup.core.Util;
import net.hudup.core.client.Protocol;
import net.hudup.core.logistic.xURI;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class HttpUtil {

	
	/**
	 * 
	 * @param requestText
	 * @return map of parameters
	 */
	public static Map<String, String> getParameters(String requestText) {
		try {
			String content = getContent(requestText);
			if (content == null)
				return null;
			
			int mark = content.indexOf('?');
			if (mark == -1)
				return Util.newMap();
			
			return xURI.parseParameterText(content.substring(mark + 1));
		}
		catch (Throwable e) {
			e.printStackTrace();
			return Util.newMap();
		}
	}
	
	
	/**
	 * 
	 * @param requestText
	 * @return action
	 */
	public static String getAction(String requestText) {
		String action = null;
		try {
			String path = getPath(requestText);
			if (path == null)
				return null;
			
			int index = path.lastIndexOf("/");
			if (index == -1)
				action = path;
			else
				action = path.substring(index + 1);
			
			action = action.trim();
		}
		catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
		
		if (action == null || action.isEmpty())
			return null;
		return action;
	}
	
	
	/**
	 * 
	 * @param requestText
	 * @return path
	 */
	public static String getPath(String requestText) {
		String content = getContent(requestText);
		if (content == null)
			return null;
		
		try {
			int mark = content.indexOf('?');
			
			String path = null;
			if (mark == -1)
				path = content;
			else
				path = content.substring(0, mark).trim();
			
			if (path.startsWith("/"))
				path = path.substring(1);
			if (path.endsWith("/"))
				path = path.substring(0, path.length() - 1);
			
			path = path.trim();
			if (path.isEmpty())
				return null;
			else 
				return path;
		}
		catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * 
	 * @param requestText
	 * @return content
	 */
	public static String getContent(String requestText) {
		if (!requestText.toUpperCase().startsWith("GET"))
			return null;
		
		try {
			int start = requestText.indexOf(' ');
			int end = requestText.indexOf(' ', start + 1);
			String content = requestText.substring(start + 1, end);
			content = content.trim();
			
			if (content.isEmpty())
				return null;
			else 
				return content;
		}
		catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * 
	 * @param return_code
	 * @param file_type
	 * @return HTTP header
	 */
	public static String createHeader(int return_code, int file_type) {
		String header_s = "HTTP/1.0 ";
		String error_code = null;
		switch (return_code) {
			case 200:
				header_s = header_s + "200 OK";
				break;
			case 400:
				header_s = header_s + "400 Bad Request";
				error_code = "error.code.400";
				break;
			case 403:
				header_s = header_s + "403 Forbidden";
				error_code = "error.code.403";
				break;
			case 404:
				header_s = header_s + "404 Not Found";
				error_code = "error.code.404";
				break;
			case 500:
				header_s = header_s + "500 Internal Server Error";
				error_code = "error.code.500";
				break;
			case 501:
				header_s = header_s + "501 Not Implemented";
				error_code = "error.code.501";
				break;
		}
		header_s = "HTTP/1.0 200 OK";
		
		header_s = header_s + "\r\n"; //other header fields,
		header_s = header_s + "Connection: close\r\n"; //we can't handle persistent connections
		header_s = header_s + "Server: Hudup listener v6\r\n"; //server name

		switch (file_type) {
			case Protocol.UNKNOWN_FILE_TYPE:
				header_s = header_s + "Content-Type: application/json\r\n";
				break;
			case Protocol.HTML_FILE_TYPE:
				header_s = header_s + "Content-Type: text/html\r\n";
				break;
			case Protocol.JPEG_FILE_TYPE:
				header_s = header_s + "Content-Type: image/jpeg\r\n";
				break;
			case Protocol.GIF_FILE_TYPE:
				header_s = header_s + "Content-Type: image/gif\r\n";
				break;
			case Protocol.TEXTPLAIN_FILE_TYPE:
				header_s = header_s + "Content-Type: text/plain\r\n";
				break;
			case Protocol.JSON_FILE_TYPE:
				header_s = header_s + "Content-Type: application/json\r\n";
				break;
			case Protocol.XZIP_FILE_TYPE:
				header_s = header_s + "Content-Type: application/x-zip-compressed\r\n";
				break;
			default:
				header_s = header_s + "Content-Type: text/html\r\n";
				break;
		}

		header_s = header_s + "\r\n"; //this marks the end of the httpheader
		
		if(error_code != null)
			header_s = header_s + error_code;
		return header_s;
	}
	
	
	/**
	 * 
	 * @param requestText
	 * @return file type of request
	 */
	public static int getFileType(String requestText) {
		int type_is = Protocol.HTML_FILE_TYPE;
		String path = HttpUtil.getPath(requestText);
		
		if (path.indexOf('.') == -1) {
			type_is = Protocol.UNKNOWN_FILE_TYPE;
		}
		else if (path.endsWith(".htm") || path.endsWith(".html") || path.endsWith(".xhtml"))
			type_is = Protocol.HTML_FILE_TYPE;
		
		else if (path.endsWith(".jpg") || path.endsWith(".jpeg"))
			type_is = Protocol.JPEG_FILE_TYPE;
		
		else if (path.endsWith(".gif"))
			type_is = Protocol.GIF_FILE_TYPE;
		
		else if (path.endsWith(".txt"))
			type_is = Protocol.TEXTPLAIN_FILE_TYPE;
		
		else if (path.endsWith(".json"))
			type_is = Protocol.JSON_FILE_TYPE;
		
		else if (path.endsWith(".exe") || path.endsWith(".zip") || path.endsWith(".rar") || path.endsWith(".tar"))
			type_is = Protocol.XZIP_FILE_TYPE;
		
		return type_is;
	}
	

	
	
}
