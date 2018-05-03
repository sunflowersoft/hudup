package net.hudup.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.hudup.core.parser.TextParserUtil;


/**
 * This utility class represents a list of {@link RegisterTable} (s).
 * Note that {@link PluginStorage} manages many {@link RegisterTable} (s) and each {@link RegisterTable} stores algorithms having the same type.
 * For example, a register table manages recommendation algorithms (recommenders) whereas another manages metrics for evaluating recommenders.
 * As a convention, {@link RegisterTable} is called register table and this class is called {@code register table item list}.
 * Note that each register table in this class is wrapped by {@link RegisterTableItem} class called {@code register table item}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class RegisterTableList {

	
	/**
	 * The internal list contains all register tables. Each register table in this class is wrapped by {@link RegisterTableItem} class.
	 */
	protected List<RegisterTableItem> list = Util.newList();
	
	
	/**
	 * Default constructor.
	 */
	public RegisterTableList() {
		
	}

	
	/**
	 * Constructor with specified collection of register table items.
	 * @param list specified collection of register table items.
	 */
	public RegisterTableList(Collection<RegisterTableItem> list) {
		this.list.addAll(list);
	}
	
	
	/**
	 * Constructor with specified array of register table items.
	 * @param arrays specified array of register table items.
	 */
	public RegisterTableList(RegisterTableItem... arrays) {
		list.addAll(Arrays.asList(arrays));
	}

	
	/**
	 * Constructor with one register table item.
	 * @param item specified register table item.
	 */
	public RegisterTableList(RegisterTableItem item) {
		this.list.add(item);
	}
	
	
	/**
	 * Getting register table item at specified index.
	 * @param index specified index.
	 * @return register table item at specified index.
	 */
	public RegisterTableItem get(int index) {
		return list.get(index);
	}
	
	
	/**
	 * Adding a specified register table item into this list.
	 * @param item specified register table item.
	 * @return whether adding register table successfully.
	 */
	public boolean add(RegisterTableItem item) {
		return list.add(item);
	}
	
	
	/**
	 * Adding other list into this list.
	 * @param itemList other list of register table items.
	 * @return whether adding register tables successfully.
	 */
	public boolean addAll(RegisterTableList itemList) {
		return list.addAll(itemList.list);
	}
	
	
	/**
	 * Adding specified collection of register table items into this list.
	 * @param items specified collection of register table items.
	 * @return whether adding register table items successfully.
	 */
	public boolean addAll(Collection<RegisterTableItem> items) {
		return list.addAll(items);
	}

	
	/**
	 * Removing a register table item at specified index.
	 * @param index specified index.
	 * @return removed register table item.
	 */
	public RegisterTableItem remove(int index) {
		return list.remove(index);
	}
	
	
	/**
	 * Setting the specified register table item at specified index.
	 * @param index specified index.
	 * @param item specified register table item.
	 * @return replaced register table item.
	 */
	public RegisterTableItem set(int index, RegisterTableItem item) {
		return list.set(index, item);
	}
	
	
	/**
	 * Getting size of this register table list.
	 * @return size of this register table list.
	 */
	public int size() {
		return list.size();
	}
	
	
	/**
	 * Clearing this list, which means that all register table items are removed from this list.
	 */
	public void clear() {
		list.clear();
	}
	
	
	/**
	 * Getting {@link List} of register table items.
	 * @return {@link List} of register table items.
	 */
	public List<RegisterTableItem> toList() {
		List<RegisterTableItem> newList = Util.newList();
		newList.addAll(list);
		
		return newList;
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		List<String> itemNameList = Util.newList();
		for (RegisterTableItem item : list)
			itemNameList.add(item.name);
		
		return TextParserUtil.toText(itemNameList, ",");
	}

	
	/**
	 * This class called {@code register table item} wraps a register table ({@link RegisterTable}).
	 * Each register table in such item has a name. This class is used for showing register table on graphic user interface (GUI).
	 * 
	 * @author Loc Nguyen
	 * @version 10.0
	 *
	 */
	public final static class RegisterTableItem implements Comparable<RegisterTableItem> {
		
		
		/**
		 * Name of register table.
		 */
		protected String name;
		
		
		/**
		 * Internal register table.
		 */
		protected RegisterTable registerTable;
		
		
		/**
		 * Default constructor.
		 */
		public RegisterTableItem() {
			
		}
		
		
		/**
		 * Constructor with specified register table and its name.
		 * @param item name of specified register table.
		 * @param registerTable specified register table.
		 */
		public RegisterTableItem(String item, RegisterTable registerTable) {
			this.name = item;
			this.registerTable = registerTable;
		}


		/**
		 * Getting name of register table.
		 * @return name name of register table.
		 */
		public String getName() {
			return name;
		}
		
		
		/**
		 * Getting the internal register table of this item.
		 * @return register table {@link RegisterTable} of this item.
		 */
		public RegisterTable getRegisterTable() {
			return registerTable;
		}
		
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return name;
		}


		@Override
		public int compareTo(RegisterTableItem o) {
			// TODO Auto-generated method stub
			return this.name.compareTo(o.name);
		}
		
		
	}


}
