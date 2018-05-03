package net.hudup.core.data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Cloneable;
import net.hudup.core.Util;
import net.hudup.core.logistic.MinMax;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;


/**
 * This class represent a list of nominal (s), called nominal list, in brief. Recall that nominal data indicates discrete and non-number data such as weekdays {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday}.
 * Nominal data is called {@code nominal}, in brief, represented by {@link Nominal} class. A nominal is always in this nominal list and so it has an index in this nominal list.
 * Nominal list can have hierarchical structure, for example, in the nominal list {fruit, table, apple, orange}, nominal (s) &quot;apple&quot; and &quot;orange&quot; are children of parent nominal &quot;fruit&quot;.
 * Nominal list uses a {@link Map} to store nominal (s) instead of using {@link List}. Such map is referred by the variable {@link #map}.
 * Each entry in this map is a key-value pair in which the value is a nominal and the key indicates the index of such nominal.
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class NominalList implements Serializable, Cloneable, TextParsable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * The {@link Map} to store all nominal (s). Each entry in this map is a key-value pair in which the value is a nominal and the key indicates the index of such nominal.
	 */
	protected Map<Integer, Nominal> map = Util.newMap();
	
	
	/**
	 * Default constructor.
	 */
	public NominalList() {
		if (!(map instanceof Serializable))
			throw new RuntimeException("Not serializable object");
	}
	
	
	/**
	 * Constructor with specified {@link List} of nominal (s).
	 * @param list Specified {@link List} of nominal (s).
	 */
	public NominalList(List<Nominal> list) {
		this();
		
		addAll(list);
	}

	
	/**
	 * Adding a specified nominal at the end of this nominal list.
	 * @param nominal Specified nominal.
	 */
	public void add(Nominal nominal) {
		map.put(nominal.getIndex(), nominal);
	}
	
	
	/**
	 * Adding a specified {@link List} of nominal (s) at the end of this nominal list.
	 * @param list Specified {@link List} of nominal (s).
	 */
	public void addAll(List<Nominal> list) {
		for (Nominal nominal : list)
			add(nominal);
	}
	
	
	/**
	 * Removing a nominal from this nominal list at specified index.
	 * @param index Specified index at which a nominal is removed from this nominal list.
	 */
	public void remove(int index) {
		map.remove(index);
	}
	
	
	/**
	 * Getting a nominal at specified index.
	 * @param index Specified index.
	 * @return the nominal at specified index.
	 */
	public Nominal get(int index) {
		return map.get(index);
	}
	
	
	/**
	 * Getting the size of this nominal  list.
	 * @return size of this nominal list.
	 */
	public int size() {
		return map.size();
	}


	@Override
	public String toText() {
		// TODO Auto-generated method stub
		return toText(this, ",");
	}

	
	@Override
	public String toString() {
		return toText();
	}
	
	
	/**
	 * Clearing this nominal list, which means that removing all nominal (s) this nominal list.
	 */
	public void clear() {
		map.clear();
	}
	
	
	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		clear();
		
		NominalList list = parseNominalList(spec, ",");
		this.map = list.map;
	}
	
	
	@Override
	public Object clone() {
		Map<Integer, Nominal> map = Util.newMap();
		
		Set<Integer> indexes = this.map.keySet();
		for (int index : indexes) {
			map.put(index, (Nominal) this.map.get(index).clone());
		}
		
		NominalList nominalList = new NominalList();
		nominalList.map = map;
		
		return nominalList;
	}
	
	
	/**
	 * Checking whether this nominal list is identical to specified nominal list.
	 * Two nominal lists are identical if they have the same size and each nominal of this list is identical to a corresponding nominal of other list.  
	 * @param list Specified nominal list.
	 * @return whether this nominal list is identical to specified nominal list
	 */
	public boolean identity(NominalList list) {
		// TODO Auto-generated method stub
		if (list == null || list.size() != size())
			return false;
		
		for (int i = 0; i < size(); i++) {
			if (!get(i).equals(list.get(i)))
				return false;
		}
		
		return true;
	}


	/**
	 * Parsing the specified string into nominal list. Such string have many parts separated by specified parameter {@code sep}.
	 * Each part is text string of a nominal, which is returned of method {@link Nominal#toText()} 
	 * @param spec Specified string representing nominal list
	 * @param sep This input parameter specifies a character or a string to separate many parts of the specified string.
	 * @return Nominal list parsed from the specified string. 
	 */
	public static NominalList parseNominalList(String spec, String sep) {
		Map<Integer, Nominal> map = Util.newMap();
		
		List<String> list = TextParserUtil.parseTextList(spec, sep);
		for (String string : list) {
			List<String> pair = TextParserUtil.split(string, "=", null);
			if (pair.size() < 2)
				continue;
			
			Nominal nominal = new Nominal();
			nominal.parseText(pair.get(1));
			
			map.put(Integer.parseInt(pair.get(0)), nominal);
		}
		
		NominalList nominalList = new NominalList();
		nominalList.map = map;
		
		return nominalList;
	}
	
	
	/**
	 * This static method converts the specified nominal list into a text string. There are many parts in such returned text string and each part is text representation of a nominal.
	 * These text strings are separated by a character or a string specified by the input parameter {@code sep}. 
	 * @param nominalList Specified nominal list.
	 * @param sep A character or a string to separate parts of returned text string. Each part is text presentation of a nominal.
	 * @return text form of nominal list.
	 */
	public static String toText(NominalList nominalList, String sep) {
		return TextParserUtil.toText(nominalList.map, sep);
	}
	
	
	/**
	 * Each nominal represented by {@link Nominal} class has a value and an index in this nominal list. Nominal value is a text string representing such nominal.
	 * This method looks up the index of given nominal value.
	 * @param nominalValue Specified nominal value.
	 * @return Index of specified nominal value
	 */
	public int indexOfValue(String nominalValue) {
		nominalValue = nominalValue.trim();
		
		Set<Integer> indexes = this.map.keySet();
		for (int index : indexes) {
			String value = map.get(index).getValue();
			
			List<String> list = TextParserUtil.split(value, "/", null);
			for (String string : list) {
				if (string.equals(nominalValue))
					return index;
			}
		}
		
		return -1;
	}

	
	/**
	 * This method looks up the index of the specified nominal in this nominal list.
	 * @param nominal Specified nominal.
	 * @return Index of the specified nominal in this nominal list.
	 */
	public int indexOf(Nominal nominal) {
		
		Set<Integer> indexes = this.map.keySet();
		for (int index : indexes) {
			Nominal thisnominal = map.get(index);
			
			if (thisnominal.equals(nominal))
				return index;
		}
		
		
		return -1;
	}
	
	
	/**
	 * Retrieving all value of nominal (s) in this nominal list.
	 * Note that each nominal represented by {@link Nominal} class has a value and an index in this nominal list.
	 * @return list of nominal values
	 */
	public List<String> getValueList() {
		List<String> list = Util.newList();
		
		Set<Integer> indexes = this.map.keySet();
		for (int index : indexes) {
			String value = map.get(index).getValue();
			
			list.add(value);
		}
		
		return list;
	}


	/**
	 * Recall that each nominal represented by {@link Nominal} class has a value and an index in this nominal list.
	 * This method returns the minimum index and the maximum index over all nominal (s) in this nominal list.
	 * @return The maximum index and minimum index over all nominal (s) in this nominal list, represented by {@link MinMax} class.
	 */
	public MinMax getMinMaxIndex() {
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		
		Set<Integer> indexes = this.map.keySet();
		for (int index : indexes) {
			if (index < min)
				min = index;
			
			if (index > max)
				max = index;
		}
		
		return new MinMax(min, max);
	}
	
	
}

