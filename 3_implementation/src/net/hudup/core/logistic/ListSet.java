package net.hudup.core.logistic;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.hudup.core.Util;

/**
 * This class implements the {@link Set} interface but its elements are stored in the internal list {@link #list}.
 * Therefore accessing elements in this {@link ListSet} is faster.
 * 
 * @author Loc Nguyen
 * @version 11.0
 *
 * @param <E> type of elements of this {@link ListSet}.
 */
public class ListSet<E> implements Set<E>, Serializable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * The internal {@link List} stores elements of this {@link ListSet}. 
	 */
	protected List<E> list = Util.newList();
	
	
	/**
	 * Default constructor.
	 */
	public ListSet() {
		super();
		// TODO Auto-generated constructor stub
		
		if (!(list instanceof Serializable))
			throw new RuntimeException("ListSet isn't serializable class");
	}


	@Override
	public int size() {
		// TODO Auto-generated method stub
		return list.size();
	}

	
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return list.isEmpty();
	}

	
	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return list.contains(o);
	}

	
	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return list.iterator();
	}

	
	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return list.toArray();
	}

	
	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return list.toArray(a);
	}

	
	@Override
	public boolean add(E e) {
		// TODO Auto-generated method stub
		if (contains(e))
			return false;
		else
			return list.add(e);
	}

	
	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return list.remove(o);
	}

	
	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return list.containsAll(c);
	}

	
	@Override
	public boolean addAll(Collection<? extends E> c) {
		// TODO Auto-generated method stub
		int count = 0;
		for (E e : c) {
			if (add(e))
				count++;
		}
		return count > 0;
	}

	
	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return list.retainAll(c);
	}

	
	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return list.removeAll(c);
	}

	
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		list.clear();
	}
	
	
}

