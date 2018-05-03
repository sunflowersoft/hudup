/**
 * 
 */
package net.hudup.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.alg.Alg;

import org.apache.log4j.Logger;

/**
 * Plug-in manager is responsible for discovering and managing registered recommendation algorithms.
 * The class that implements {@code PluginManager} is {@code Firer}.
 * {@code Alg} is the most abstract interface for all algorithms.
 * Every algorithm has a unique name. Every algorithm is registered in system register table and identified by its name.
 * Such system register table is modeled as this {@code RegisterTable} class.
 * Actually, plug-in manager discovers automatically all algorithms via their names at the booting time and requires {@link PluginStorage} to add such algorithms into register tables represented by {@link RegisterTable} classes.
 * After Hudup framework started, it is easy to retrieve any any algorithms by looking up algorithms in register tables from {@link PluginStorage}.
 * Please pay attention that system register tables are stored in a so-called {@code plug-in storage} which is represented by {@code PluginStorage} class.
 * <br>
 * {@link RegisterTable} provides two important methods as follows:
 * <ul>
 * <li>
 * Method {@link #register(Alg)} registers a given algorithm. This method is called by {@link PluginManager#discover(String)} at starting time of Hudup.
 * A algorithm which is registered in {@link RegisterTable} will be added in the internal map {@link #algMap}.
 * </li>
 * <li>
 * Method {@link #query(String)} retrieves an algorithm by its name with attention that such algorithm is named by returned value of its {@link Alg#getName()} method.
 * Method {@link #query(String)} is often called by evaluator and recommendation service to retrieve specified algorithms.
 * A algorithm which is unregistered in {@link RegisterTable} will be removed from the internal map {@link #algMap}.
 * </li>
 * </ul>
 * Note, general algorithm is represented by {@link Alg} interface. {@link Alg} has five typical inherited interfaces as follows:
 * <ul>
 * <li>Recommendation algorithm {@code Recommender}.</li> 
 * <li>Context template schema manager {@code CTSManager}.</li> 
 * <li>Data set parser {@code DatasetParser}.</li>
 * <li>External query {@code ExternalQuery}.</li>
 * <li>Metric {@code Metric}.</li>
 * </ul>
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public final class RegisterTable implements Cloneable {

	
	/**
	 * Logger of this class.
	 */
	protected final static Logger logger = Logger.getLogger(RegisterTable.class);

	
	/**
	 * Map of registered algorithms. Each algorithm is associated by an string key which is the name of this algorithm.
	 */
	private Map<String, Alg> algMap = Util.newMap();
	
	
	/**
	 * Default constructor.
	 */
	public RegisterTable() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * COnstructor with specified collection of algorithms.
	 * @param algs specified collection of algorithms. These algorithms are registered.
	 */
	public RegisterTable(Collection<Alg> algs) {
		for (Alg alg : algs) {
			register(alg);
		}
	}
	
	
	/**
	 * Registering an specified algorithm.
	 * @param alg specified algorithm.
	 * @return {@code true} if registration is successful.
	 */
	public boolean register(Alg alg) {
		if (!canRegister(alg)) {
			logger.error("Algorithm \"" + alg.getName() + "\" existed");
			return false;
		}
		else {
			algMap.put(alg.getName(), alg);
			return true;
		}
	}
	
	
	/**
	 * Checking if it is able to register the specified algorithm.
	 * Algorithms registered in this table are distinguished by their names.
	 * Algorithms in this table cannot duplicated, which means that two algorithms having the same name cannot exist in this table.
	 * 
	 * @param alg specified algorithm.
	 * @return {@code true} if the specified algorithm can be registered in this table.
	 */
	public boolean canRegister(Alg alg) {
		return !contains(alg.getName());
	}

	
	/**
	 * Return algorithm via name.
	 * @param algName specified name.
	 * @return algorithm having specified name.
	 */
	public Alg query(String algName) {
		if (algName == null)
			return null;
		else if (contains(algName))
			return algMap.get(algName);
		else
			return null;
	}

	
	/**
	 * Unregistering the algorithm (if it was registered in this table) via the specified name.
	 * @param algName specified algorithm name.
	 * @return the unregistered algorithm.
	 */
	public Alg unregister(String algName) {
		return algMap.remove(algName);
	}

	
	/**
	 * Return an array of names of registered algorithms.
	 * @return array of names of registered algorithms.
	 */
	public List<String> getAlgNames() {
		List<String> algNames = Util.newList();
		algNames.addAll(algMap.keySet());
		
		Collections.sort(algNames);
		return algNames;
	}
	
	
	/**
	 * Getting list of registered algorithms.
	 * @return list of registered algorithms.
	 */
	public List<Alg> getAlgList() {
		List<Alg> algs = Util.newList();
		algs.addAll(algMap.values());
		
		Collections.sort(algs, new Comparator<Alg>() {

			@Override
			public int compare(Alg alg1, Alg alg2) {
				// TODO Auto-generated method stub
				return alg1.getName().compareTo(alg2.getName());
			}
		});
		
		return algs;
	}
	
	
	/**
	 * Getting list of registered algorithms based on the specified algorithm filter. 
	 * @param filter specified algorithm filter represented by {@link AlgFilter}.
	 * @return filtered list of registered algorithms.
	 */
	public List<Alg> getAlgList(AlgFilter filter) {
		List<Alg> algList = getAlgList();
		List<Alg> newList = Util.newList();
		for (Alg alg : algList) {
			if (filter.accept(alg))
				newList.add(alg);
		}
		
		return newList;
	}
	
	
	/**
	 * Getting list of registered algorithms whose names in the specified list.
	 * @param nameList specified list of algorithm names.
	 * @return list of registered algorithms whose names in the specified list.
	 */
	public List<Alg> getAlgList(List<String> nameList) {
		List<Alg> algs = Util.newList();
		
		for (String name : nameList) {
			Alg alg = query(name);
			if (alg != null)
				algs.add(alg);
		}
		
		return algs;
	}
	
	
	/**
	 * Testing whether or not this table registers an algorithm having specified name.
	 * @param algName specified algorithm name.
	 * @return whether or not this table registers an algorithm having specified name.
	 */
	public boolean contains(String algName) {
		return algMap.containsKey(algName);
	}

	
	/**
	 * Getting size of this register table.
	 * @return size of this register table.
	 */
	public int size() {
		return algMap.size();
	}

	
	/**
	 * Clearing register table, which means that all algorithms are unregistered from this table.
	 */
	public void clear() {
		algMap.clear();
	}
	
	
	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		RegisterTable newReg = new RegisterTable();
		
		Set<String> keys = this.algMap.keySet();
		
		for (String key : keys) {
			Alg alg = this.algMap.get(key);
			newReg.register(alg.newInstance());
		}
		
		return newReg;
	}


	/**
	 * Registering all algorithms of the specified table into this table.
	 * @param from specified table.
	 */
	public void copy(RegisterTable from) {
		this.algMap.clear();
		
		Set<String> keys = from.algMap.keySet();
		for (String key : keys) {
			Alg alg = from.algMap.get(key);
			this.register(alg.newInstance());
		}
	}
	
	
	/**
	 * Registering all algorithms of the specified table into this table.
	 * Previous algorithms that are not registered in the specified table are unregistered from this table.
	 * @param from specified table.
	 */
	public void copyNewOnes(RegisterTable from) {
		
		Set<String> fromKeys = from.algMap.keySet();
		for (String fromKey : fromKeys) {
			Alg fromAlg = from.algMap.get(fromKey);
			if (!this.contains(fromAlg.getName()))
				this.register(fromAlg.newInstance());
		}
		
		
		Set<String> thisKeys = Util.newSet();
		thisKeys.addAll(algMap.keySet());
		for (String thisKey : thisKeys) {
			Alg thisAlg = algMap.get(thisKey);
			if (!from.contains(thisAlg.getName()))
				this.unregister(thisAlg.getName());
		}
	}

	
	/**
	 * This abstract class is used for filtering (selecting) an specified algorithm based on custom criteria.
	 * This abstract class must be completed in use by completing the method {@link #accept(Alg)}.
	 * 
	 * @author Loc Nguyen
	 * @version 10.0
	 *
	 */
	public static abstract class AlgFilter {
		
		
		/**
		 * This method defines some criteria to filtering (selecting) an specified algorithm.
		 * @param alg specified algorithm
		 * @return whether algorithm is accepted.
		 */
		public abstract boolean accept(Alg alg);
	}
	
}
