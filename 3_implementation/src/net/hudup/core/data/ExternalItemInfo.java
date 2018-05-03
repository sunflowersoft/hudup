package net.hudup.core.data;

import java.io.Serializable;
import java.util.List;

import net.hudup.core.Cloneable;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;

/**
 * Items represent many kinds of goods such as books, clothes. The Hudup database cannot store all information about various goods.
 * So this class called {@code external item information} stores additional information about items that are not stored in Hudup database.
 * For example, cover image of a book is an external information.
 * In other words, external item information is a record of item, stored in outside database different from Hudup database.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ExternalItemInfo implements Serializable, TextParsable, Cloneable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * External identifier of item (external ID).
	 */
	public String externalId     = "";
	
	/**
	 * Type of item.
	 */
	public String type           = "";
	
	/**
	 * The link points to a web page showing additional information about item.
	 */
	public String link           = "";
	
	/**
	 * Title of item.
	 */
	public String title          = "";
	
	/**
	 * The image path of item, for example, &quot;http://www.flowershop.com/flower1.jpg&quot;
	 */
	public String largeImagePath = "";

	
	/**
	 * Default constructor.
	 */
	public ExternalItemInfo() {
		
	}
	
	
	/**
	 * Constructor with specified external identifier, type (category), link, title, image path.
	 * @param externalId specified external identifier.
	 * @param category specified type (category)
	 * @param link specified link.
	 * @param title specified title.
	 * @param largeImagePath specified image path
	 */
	public ExternalItemInfo(
			String externalId,
			String category,
			String link, 
			String title, 
			String largeImagePath) {
		this.externalId = externalId;
		this.type = category;
		this.link = link;
		this.title = title;
		this.largeImagePath = largeImagePath;
	}
	
	
	@Override
	public String toText() {
		// TODO Auto-generated method stub
		return 
				TextParserUtil.encryptReservedChars(externalId) + TextParserUtil.LINK_SEP + 
				TextParserUtil.encryptReservedChars(type) + TextParserUtil.LINK_SEP + 
				TextParserUtil.encryptReservedChars(link) + TextParserUtil.LINK_SEP + 
				TextParserUtil.encryptReservedChars(title) + TextParserUtil.LINK_SEP + 
				TextParserUtil.encryptReservedChars(largeImagePath);
	}

	
	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		List<String> textList = TextParserUtil.split(spec, TextParserUtil.LINK_SEP, null);
		if (textList.size() > 0)
			externalId = TextParserUtil.decryptReservedChars(textList.get(0));
		
		if (textList.size() > 1)
			type = TextParserUtil.decryptReservedChars(textList.get(1));

		if (textList.size() > 2)
			link = TextParserUtil.decryptReservedChars(textList.get(2));
		
		if (textList.size() > 3)
			title = TextParserUtil.decryptReservedChars(textList.get(3));
		
		if (textList.size() > 4)
			largeImagePath = TextParserUtil.decryptReservedChars(textList.get(4));
	}
	
	
	@Override
	public Object clone() {
		return new ExternalItemInfo(externalId, type, link, title, largeImagePath);
	}
	
	
	@Override
	public String toString() {
		return toText();
	}
	
	
}
