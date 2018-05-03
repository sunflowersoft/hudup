package net.hudup.core.data;

import java.io.Serializable;

/**
 * {@link CData} represents a fragment of XML code that is not processed by browser, called CDATA.
 * According to WikiPedia (<a href="https://en.wikipedia.org/wiki/CDATA">https://en.wikipedia.org/wiki/CDATA</a>), &quot;a CDATA section is a section of element content that is marked for the parser to interpret purely as textual data, not as markup&quot;.
 * CDATA section begins with tag &lt;![CDATA[ and ends with tag ]]&gt;
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class CData implements Serializable {
	
	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Data inside CDATA section that browser parses as normal text.
	 * It begins with tag &lt;![CDATA[ and ends with tag ]]&gt;
	 */
	protected String data = "";
	
	
	/**
	 * Default constructor.
	 */
	public CData() {
		
	}
	
	
	/**
	 * Constructor with CDATA section.
	 * @param data CDATA section
	 */
	public CData(String data) {
		this.data = data;
	}
	
	
	/**
	 * Getting CDATA section.
	 * @return Built-in CDATA section
	 */
	public String getData() {
		return data;
	}
	
	
	@Override
	public String toString() {
		return data;
	}
	
	
}
