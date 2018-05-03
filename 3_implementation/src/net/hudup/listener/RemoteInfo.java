package net.hudup.listener;

import java.io.Serializable;

import net.hudup.core.Cloneable;
import net.hudup.core.data.HiddenText;
import net.hudup.core.logistic.Cipher;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class RemoteInfo implements Serializable, Cloneable, TextParsable {
	
	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * 
	 */
	public String host = "localhost";
	
	
	/**
	 * 
	 */
	public int port = 0;

	
	/**
	 * 
	 */
	public String account = "";

	
	/**
	 * 
	 */
	public HiddenText password = new HiddenText("");
	
	
	/**
	 * 
	 */
	public RemoteInfo() {
		
	}
	
	
	/**
	 * 
	 * @param host
	 * @param port
	 * @param account
	 * @param password
	 */
	public RemoteInfo(String host, int port, String account, String password) {
		this.host = host;
		this.port = port;
		this.account = account;
		this.password = new HiddenText(password);
	}

	
	/**
	 * 
	 * @param host
	 * @param port
	 * @param account
	 * @param password
	 */
	public RemoteInfo(String host, int port, String account, HiddenText password) {
		this.host = host;
		this.port = port;
		this.account = account;
		this.password = password;
	}

	
	@Override
	public String toText() {
		// TODO Auto-generated method stub
		Cipher cipher = new Cipher();
		return host + TextParserUtil.MAIN_SEP + 
				port + TextParserUtil.MAIN_SEP +
				account + TextParserUtil.MAIN_SEP +
				cipher.encrypt(password.getText());
	}

	
	
	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return new RemoteInfo(host, port, account, password.getText());
	}



	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return toText();
	}



	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		reset();
		if (spec.isEmpty())
			return;
		
		int begin = 0;
		int next = 0;
		int index = 0;
		while (true) {
			next = spec.indexOf(TextParserUtil.MAIN_SEP, begin);
			String part = "";
			if (next == -1)
				part = spec.substring(begin);
			else
				part = spec.substring(begin, next);
				
			if (!part.isEmpty()) {
				switch (index) {
				case 0:
					host = part;
					break;
				case 1:
					port = Integer.parseInt(part);
					break;
				case 2:
					account = part;
					break;
				case 3:
					Cipher cipher = new Cipher();
					password = new HiddenText(cipher.decrypt(part));
					break;
				}
				index ++;
			}
			if (next == -1 || next >= spec.length() - 1 || index > 4)
				break;
			
			begin = next + 1;
		}
	}
	
	
	/**
	 * 
	 */
	protected void reset() {
		host = "localhost";
		port = 0;
		account = "";
		password = new HiddenText("");
	}
	
	
	
	
	
}
