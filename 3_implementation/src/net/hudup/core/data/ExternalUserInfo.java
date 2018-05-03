package net.hudup.core.data;

import java.io.Serializable;
import java.util.List;

import net.hudup.core.Cloneable;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;


/**
 * User has a lot of additional information. The Hudup database cannot store all information about heterogeneous persons.
 * So this class called {@code external user information} stores additional information about users that are not stored in Hudup database.
 * For example, user interest is an external information.
 * In other words, external user information is a record of user, stored in outside database different from Hudup database.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ExternalUserInfo implements Serializable, TextParsable, Cloneable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * External user identifier stored in outside database.
	 */
	public String externalId = "";
	
	
	/**
	 * Default constructor.
	 */
	public ExternalUserInfo() {
		
	}
	
	
	/**
	 * Constructor with external user identifier.
	 * @param externalId specified external user identifier.
	 */
	public ExternalUserInfo(String externalId) {
		this.externalId = externalId;
	}
	
	
	@Override
	public String toText() {
		// TODO Auto-generated method stub
		return TextParserUtil.encryptReservedChars(externalId);
	}

	
	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		List<String> textList = TextParserUtil.split(spec, TextParserUtil.LINK_SEP, null);
		if (textList.size() > 0)
			externalId = TextParserUtil.decryptReservedChars(textList.get(0));
	}
	
	
	@Override
	public Object clone() {
		return new ExternalUserInfo(externalId);
	}
	
	
	@Override
	public String toString() {
		return toText();
	}
	
	
}
