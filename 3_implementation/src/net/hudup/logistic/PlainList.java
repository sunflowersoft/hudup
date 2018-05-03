package net.hudup.logistic;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;

/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class PlainList implements TextParsable, Serializable {
	
	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * 
	 */
	protected List<String> list = Util.newList();
	
	
	/**
	 * 
	 */
	protected int selectedIndex = -1;
	
	
	/**
	 * 
	 */
	public PlainList() {
		
	}

	
	/**
	 * 
	 * @param index
	 * @return string at specified index
	 */
	public String get(int index) {
		return list.get(index);
	}
	
	
	/**
	 * 
	 * @return size of list
	 */
	public int size() {
		return list.size();
	}
	
	
	/**
	 * 
	 * @param element
	 */
	public void add(String element) {
		if (!list.contains(element))
			list.add(element);
		
		if (list.size() > 0 && selectedIndex == -1)
			selectedIndex = 0;
	}
	
	
	/**
	 * 
	 * @param collection
	 */
	public void addAll(Collection<String> collection) {
		for (String element : collection)
			add(element);
	}
	
	
	public void clear() {
		list.clear();
		selectedIndex = -1;
	}
	
	
	/**
	 * 
	 * @return selected index
	 */
	public int getSelectedIndex() {
		return selectedIndex;
	}
	
	
	/**
	 * 
	 * @return selected text
	 */
	public String getSelectedText() {
		if (selectedIndex == -1)
			return null;
		else
			return list.get(selectedIndex);
	}
	
	
	/**
	 * 
	 * @param text
	 */
	public void setSelectedText(String text) {
		int index = list.indexOf(text);
		if (index != -1)
			selectedIndex = index;
	}
	
	
	/**
	 * 
	 * @param selectedIndex
	 */
	public void setSelectedIndex(int selectedIndex) {
		if (selectedIndex >= 0 && selectedIndex < list.size())
			this.selectedIndex = selectedIndex;
	}
	
	
	/**
	 * 
	 * @param index
	 */
	public void remove(int index) {
		String selectedText = getSelectedText();
		list.remove(index);
		
		if (list.size() > 0) {
			
			if (selectedText != null)
				setSelectedText(selectedText);
		}
		else {
			selectedIndex = -1;
		}
		
		
	}
	
	
	/**
	 * 
	 * @return array of elements
	 */
	public String[] toArray() {
		return list.toArray(new String[] { });
	}
	
	
	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		list.clear();
		selectedIndex = -1;
		
		int index = spec.lastIndexOf(TextParserUtil.LINK_SEP);
		if (index == -1)
			return;
		
		List<String> newList = TextParserUtil.parseTextList(
				spec.substring(0, index), TextParserUtil.LINK_SEP);
		for (String el : newList) {
			list.add(TextParserUtil.decryptReservedChars(el));
		}
		
		selectedIndex = Integer.parseInt(spec.substring(index + 1));
	}


	@Override
	public String toText() {
		// TODO Auto-generated method stub
		List<String> newList = Util.newList();
		for (String element : list) {
			newList.add(TextParserUtil.encryptReservedChars(element));
		}
		
		String listText = TextParserUtil.toText(newList, TextParserUtil.LINK_SEP);
		listText = listText.replaceAll(" ", "");
		return listText + TextParserUtil.LINK_SEP + selectedIndex;
	}
	
	
	@Override
	public String toString() {
		return toText();
	}
	
	
}
