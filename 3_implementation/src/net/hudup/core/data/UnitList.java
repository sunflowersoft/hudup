package net.hudup.core.data;

import java.util.Collection;
import java.util.List;

import net.hudup.core.Util;


/**
 * This class represents a list of many units. As a convention, it is called unit list.
 * It provides utility methods processing on a list of unit such as getting unit, adding unit, removing unit.
 * It owns an internal variable {@link #unitList} containing all units.
 * Recall objects in framework such as {@code profile}, {@code item profile}, {@code user profile}, {@code rating matrix}, {@code interchanged attribute map} are stored in archives (files) of entire framework.
 * Each archive (file) is called {@code unit} representing a CSV file, database table, Excel sheet, etc. Unit is represented by {@link Unit} class.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class UnitList {
	
	
	/**
	 * Internal variable {@link #unitList} contains all units.
	 */
	protected List<Unit> unitList = Util.newList();
	
	
	/**
	 * Default constructor.
	 */
	public UnitList() {
		
	}
	
	
	/**
	 * Getting the size of unit list.
	 * @return size of unit list.
	 */
	public int size() {
		return unitList.size();
	}
	
	
	/**
	 * Looking up a unit inside this list via unit name.
	 * 
	 * @param unitName specified unit name.
	 * @return index of the unit having specified name in this unit list. If there is no such unit, the method returns -1.
	 */
	public int indexOf(String unitName) {
		for (int i = 0; i < unitList.size(); i++) {
			Unit unit = unitList.get(i);
			
			if (unit.getName().equals(unitName))
				return i;
		}
		
		return -1;
	}
	
	
	/**
	 * Getting a unit at specified index.
	 * @param index specified index.
	 * @return unit at specified index.
	 */
	public Unit get(int index) {
		return unitList.get(index);
	}
	
	
	/**
	 * Clearing this unit list, which means that all units are removed from this list.
	 */
	public void clear() {
		unitList.clear();
	}
	
	
	/**
	 * Adding the specified unit into this list.
	 * @param unit specified unit.
	 * @return whether add successfully.
	 */
	public boolean add(Unit unit) {
		if (contains(unit))
			return false;
		else
			return unitList.add(unit);
	}
	
	
	/**
	 * Converting the specified name into a unit and adding such unit into this list.
	 * @param unitName specified unit name.
	 * @return whether add successfully.
	 */
	public boolean add(String unitName) {
		return add(new Unit(unitName));
	}
	
	
	
	/**
	 * Adding all units of the specified list into this list.
	 * @param unitList specified unit list.
	 */
	public void addAll(UnitList unitList) {
		addAll(unitList.unitList);
	}

	
	
	/**
	 * Adding all units of the specified collection into this list.
	 * @param unitList specified collection of units.
	 */
	public void addAll(Collection<Unit> unitList) {
		for (Unit unit : unitList) {
			add(unit);
		}
	}
	
	
	/**
	 * Converting the specified collection of names into the collection of units and then adding all units of such collection into this list.
	 * @param unitNameList specified collection of names.
	 */
	public void addNameList(Collection<String> unitNameList) {
		for (String unitName : unitNameList) {
			add(unitName);
		}
	}
	
	
	/**
	 * Removing the unit at specified index.
	 * @param index specified index.
	 */
	public void remove(int index) {
		unitList.remove(index);
	}
	
	
	/**
	 * Removing the unit that has the specified name from this list.
	 * @param unitName specified name.
	 */
	public void remove(String unitName) {
		int index = indexOf(unitName);
		if (index != -1)
			remove(index);
	}
	
	
	/**
	 * Removing the specified unit from this list.
	 * @param unit specified unit.
	 */
	public void remove(Unit unit) {
		if (unit != null)
			remove(unit.getName());
	}

	
	/**
	 * Removing all units of specified collection from this list.
	 * @param units specified collection of units.
	 */
	public void removeAll(Collection<Unit> units) {
		for (Unit unit : units)
			remove(unit);
	}
	
	
	/**
	 * Removing all units of specified list from this list.
	 * @param unitList specified unit list.
	 */
	public void removeAll(UnitList unitList) {
		for (int i = 0; i < unitList.size(); i++) {
			Unit unit = unitList.get(i);
			remove(unit);
		}
	}

	
	/**
	 * Checking whether or not this list contains the specified unit.
	 * @param unit specified unit.
	 * @return whether or not this list contains the specified unit.
	 */
	public boolean contains(Unit unit) {
		if (unit == null)
			return false;
		else
			return unitList.contains(unit);
	}
	
	
	
	/**
	 * Checking whether or not this list contains the unit that as the specified name.
	 * @param unitName specified name.
	 * @return whether or not this list contains the unit that as the specified name.
	 */
	public boolean contains(String unitName) {
		for (Unit unit : unitList) {
			if (unit.getName().equals(unitName))
				return true;
		}
		
		return false;
	}
	
	
	/**
	 * Checking whether or not this list contains units of specified list.
	 * @param unitList specified unit list.
	 * @return whether or not this list contains units of specified list.
	 */
	public boolean contains(UnitList unitList) {
		for (Unit unit : unitList.unitList) {
			
			if (!contains(unit))
				return false;
		}
		
		return true;
	}
	
	
	/**
	 * Converting this unit list into a {@link List} of units.
	 * @return converted {@link List} of units.
	 */
	public List<Unit> getList() {
		return unitList;
	}
	
	
	/**
	 * Converting this unit list into an array of units.
	 * @return converted array of units.
	 */
	public Unit[] toArray() {
		return unitList.toArray(new Unit[0]);
	}
	
	
	/**
	 * Getting names of all units inside this list as a {@link List}.
	 * @return {@link List} of unit names.
	 */
	public List<String> toNameList() {
		List<String> list = Util.newList();
		for (Unit unit : unitList) {
			list.add(unit.getName());
		}
		return list;
	}

	
	/**
	 * Getting list of main (essential, important) units whose {@link Unit#extra} (s) are false.
	 * @return list of main unit.
	 */
	public UnitList getMainList() {
		UnitList list = new UnitList();
		
		for (Unit unit : unitList) {
			if (!unit.isExtra())
				list.add(unit);
		}
		return list;
	}
	
	
	/**
	 * Getting list of extra (auxiliary) units whose {@link Unit#extra} (s) are true.
	 * @return list of extra unit.
	 */
	public UnitList getExtraList() {
		UnitList list = new UnitList();
		
		for (Unit unit : unitList) {
			if (unit.isExtra())
				list.add(unit);
		}
		return list;
	}

	
	/**
	 * Setting all units of this list to be essential of extra according to the input parameter {@code extra}.
	 * @param extra if {@code true}, units are set to be extra (auxiliary) units. If {@code false}, units are set to be essential (important, main) units.
	 */
	public void setExtra(boolean extra) {
		for (Unit unit : unitList) {
			unit.setExtra(extra);
		}
	}
	
	
}
