package net.hudup.core.data;

import java.io.Serializable;
import java.util.List;

import net.hudup.core.Cloneable;
import net.hudup.core.Util;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;

/**
 * This class includes both internal record represented by {@link InternalRecord} class and external record represented by {@link ExternalRecord} class.
 * As a convention, it is called {@code interchanged attribute map} for importing data (represented by internal attribute) from other database (represented by external attribute).
 * Note, internal record represents a single-value record of a {@code unit} in framework database.
 * External record represents an single-value record stored in other database (different from the framework database).
 * <br>
 * There is a special unit (having name &quot;hdp_attribute_map&quot;, for example) for storing many {@code interchanged attribute maps};
 * {@code Unit} represents a CSV file, database table, Excel sheet, etc. Unit is used to store object such as user profile, item profile, rating matrix, etc.
 *  
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class InterchangeAttributeMap implements Serializable, Cloneable, TextParsable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Internal record
	 */
	public InternalRecord internalRecord = new InternalRecord();
	
	
	/**
	 * External record
	 */
	public ExternalRecord externalRecord = new ExternalRecord();

	
	/**
	 * Default constructor.
	 */
	public InterchangeAttributeMap() {
	}
	
	
	/**
	 * Constructor with specified internal record and external record.
	 * @param internalRecord specified internal record.
	 * @param externalRecord specified external record.
	 */
	public InterchangeAttributeMap(InternalRecord internalRecord, ExternalRecord externalRecord) { 
		
		this.internalRecord = internalRecord;
		this.externalRecord = externalRecord;
		if (!isValid())
			throw new RuntimeException("Invalid fields");
	}


	
	/**
	 * Checking whether this {@code interchanged attribute map} is valid.
	 * Note, {@code interchanged attribute map} is valid if both its internal record and external record are valid.
	 * @return whether this {@code interchanged attribute map} is valid.
	 */
	public boolean isValid() {
		return internalRecord.isValid() && externalRecord.isValid();
	}
	
	
	/**
	 * Converting this {@code interchanged attribute map} into a list of both its internal record and external record.
	 * @return list form of {@code interchanged attribute map}.
	 */
	public List<Object> toList() {
		List<Object> list = Util.newList();
		list.addAll(internalRecord.toList());
		list.addAll(externalRecord.toList());
		
		return list;
	}



	@Override
	public String toText() {
		// TODO Auto-generated method stub
		if (!isValid())
			throw new RuntimeException("Invalid fields");
		
		return internalRecord.toText() + TextParserUtil.LINK_SEP + externalRecord.toText();
	}



	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		List<String> list = TextParserUtil.split(spec, TextParserUtil.LINK_SEP, null);
		
		internalRecord.parseText(list.get(0));
		externalRecord.parseText(list.get(0));
		
		if (!isValid())
			throw new RuntimeException("Invalid fields");
	}
	
	
	
	@Override
	public String toString() {
		return toText();
	}
	
	
	@Override
	public Object clone() {
		return new InterchangeAttributeMap(
			(InternalRecord)internalRecord.clone(), 
			(ExternalRecord)externalRecord.clone()); 
	}
	
	
}
