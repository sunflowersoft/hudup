package net.hudup.core.alg;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.parser.TextParserUtil;


/**
 * This class represents a list of algorithm descriptions. It has a built-in {@link List} of {@link AlgDesc} (s).
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class AlgDescList implements Serializable, net.hudup.core.Cloneable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Built-in {@link List} of algorithm descriptions.
	 */
	protected List<AlgDesc> list = Util.newList();
	
	
	/**
	 * Default constructor.
	 */
	public AlgDescList() {
		
	}

	
	/**
	 * Constructor with a collection of algorithm descriptions.
	 * @param algDescList specified collection of algorithm descriptions.
	 */
	public AlgDescList(Collection<AlgDesc> algDescList) {
		this.list.addAll(algDescList);
	}
	
	
	/**
	 * Constructor with other list of algorithm descriptions.
	 * @param algList other list of algorithm descriptions.
	 */
	public AlgDescList(AlgList algList) {
		addAll(algList);
	}

	
	/**
	 * Constructor with an algorithm description and so the list has only one algorithm description.
	 * @param algDesc specified algorithm description.
	 */
	public AlgDescList(AlgDesc algDesc) {
		this.list.add(algDesc);
	}
	
	
	/**
	 * Constructor with an algorithm and so the list has only one algorithm description.
	 * @param alg specified algorithm.
	 */
	public AlgDescList(Alg alg) {
		this(new AlgDesc(alg));
	}

	
	/**
	 * Getting an algorithm description at specified index.
	 * @param index specified index.
	 * @return Algorithm description at specified index.
	 */
	public AlgDesc get(int index) {
		return list.get(index);
	}
	
	
	/**
	 * Adding an algorithm description at the end of the list.
	 * @param algDesc specified algorithm description.
	 * @return whether adding algorithm description successfully
	 */
	public boolean add(AlgDesc algDesc) {
		return list.add(algDesc);
	}
	
	
	/**
	 * Given an algorithm {@link Alg}, adding the description of such algorithm at the end of the list.
	 * @param alg Specified algorithm
	 * @return whether adding algorithm description successfully
	 */
	public boolean add(Alg alg) {
		return list.add(new AlgDesc(alg));
	}

	
	/**
	 * Adding other list of algorithm descriptions at the end of this list.
	 * @param algDescList Specified list algorithm descriptions
	 * @return whether adding algorithm descriptions successfully
	 */
	public boolean addAll(AlgDescList algDescList) {
		return list.addAll(algDescList.list);
	}
	
	
	/**
	 * Extracting descriptions of specified list of algorithms and adding such description at the end of this list.
	 * @param algList Specified list of algorithms, please see {@link AlgList}.
	 * @return whether adding algorithm descriptions successfully
	 */
	public boolean addAll(AlgList algList) {
		for (int i = 0; i < algList.size(); i++) {
			add(algList.get(i));
		}
		
		return true;
	}

	
	/**
	 * Adding collection of algorithm descriptions at the end of this list.
	 * @param algDescs Collection of algorithm descriptions
	 * @return whether adding algorithm descriptions successfully
	 */
	public boolean addAll(Collection<AlgDesc> algDescs) {
		return list.addAll(algDescs);
	}

	
	/**
	 * Removing a algorithm description at specified index.
	 * @param index Specified index
	 * @return removed algorithm description
	 */
	public AlgDesc remove(int index) {
		return list.remove(index);
	}
	
	
	/**
	 * Setting the specified algorithm description at specified index.
	 * @param index Specified index
	 * @param algDesc Specified algorithm description
	 * @return replaced algorithm description
	 */
	public AlgDesc set(int index, AlgDesc algDesc) {
		return list.set(index, algDesc);
	}
	
	
	/**
	 * Given an algorithm, setting the description of such algorithm at specified index.
	 * @param index Specified index
	 * @param alg Specified algorithm
	 * @return replaced algorithm description
	 */
	public AlgDesc set(int index, Alg alg) {
		return set(index, new AlgDesc(alg));
	}

	
	/**
	 * Getting the size of this list.
	 * @return size of algorithm description list
	 */
	public int size() {
		return list.size();
	}
	
	
	/**
	 * Clearing this list, which means that removing all algorithm description from this list.
	 */
	public void clear() {
		list.clear();
	}
	
	
	/**
	 * Converting this list to the {@link List} of {@link AlgDesc}.
	 * @return {@link List} of algorithm descriptions
	 */
	public List<AlgDesc> toList() {
		List<AlgDesc> newList = Util.newList();
		newList.addAll(list);
		
		return newList;
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		
		return TextParserUtil.toText(getAlgClassNameList(), ",");
	}


	/**
	 * Extracting the {@link List} of algorithm class names from this list.
	 * @return {@link List} of algorithm class names
	 */
	public List<String> getAlgClassNameList() {
		List<String> algClassNameList = Util.newList();
		for (AlgDesc algDesc : list) {
			algClassNameList.add(algDesc.getAlgClassName());
		}
		
		return algClassNameList;
	}
	
	
	/**
	 * Creating a list of algorithm from this list.
	 * @return Created algorithm list {@link AlgList}
	 */
	public AlgList createAlgList() {
		AlgList algList = new AlgList();
		for (AlgDesc algDesc : list) {
			algList.add(algDesc.createAlg());
		}
		
		return algList;
	}
	
	
	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		AlgDescList algDescList = new AlgDescList();
		for (AlgDesc algDesc : list) {
			algDescList.add((AlgDesc)algDesc.clone());
		}
		
		return algDescList;
	}
	

	/**
	 * Creating the list of algorithm descriptions from a string of class names.
	 * Such class names are separated by commas.
	 * @param classNameList A string of class names separated by commas
	 * @return list of algorithm descriptions
	 */
	public static AlgDescList create(String classNameList) {
		AlgDescList algDescList = new AlgDescList();
		
		List<String> textList = TextParserUtil.parseTextList(classNameList, ",");
		for (String text : textList) {
			Object obj = Util.newInstance(text);
			if (obj == null)
				continue;
			else if (obj instanceof AlgDesc)
				algDescList.add((AlgDesc) obj);
			else if (obj instanceof Alg)
				algDescList.add(new AlgDesc((Alg)obj));
		}
		
		return algDescList;
	}


}
