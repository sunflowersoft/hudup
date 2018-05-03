package net.hudup.core.logistic;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Util;


/**
 * This class implements the {@link Map} interface whose keys are copied in another {@link ListSet}.
 * Therefore accessing keys in this {@link ListMap} is faster.
 * 
 * @author Loc Nguyen
 * @version 11.0
 *
 * @param <K> type of keys.
 * @param <V> type of values.
 */
public class ListMap<K, V> implements Map<K, V>, Serializable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Main {@link Map} of entries.
	 */
	protected Map<K, V> map = Util.newMap();
	
	
	/**
	 * Keys are copied in this {@link ListSet}, which makes to access keys faster.
	 */
	protected ListSet<K> keys = new ListSet<K>();
	
	
	/**
	 * Default constructor.
	 */
	public ListMap() {
		super();
		// TODO Auto-generated constructor stub
		if (!(map instanceof Serializable) || !(keys instanceof Serializable))
			throw new RuntimeException("ListMap isn't serializable class");
	}

	
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return map.size();
	}

	
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return map.isEmpty();
	}

	
	@Override
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return map.containsKey(key);
	}

	
	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return map.containsValue(value);
	}

	
	@Override
	public V get(Object key) {
		// TODO Auto-generated method stub
		return map.get(key);
	}

	
	@Override
	public V put(K key, V value) {
		// TODO Auto-generated method stub
		V v = map.put(key, value);
		if (map.containsKey(key))
			keys.add(key);
		
		return v;
	}

	
	@Override
	public V remove(Object key) {
		// TODO Auto-generated method stub
		keys.remove(key);
		return map.remove(key);
	}

	
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		// TODO Auto-generated method stub
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
            put(e.getKey(), e.getValue());
	}

	
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		map.clear();
		keys.clear();
	}

	
	@Override
	public Set<K> keySet() {
		// TODO Auto-generated method stub
		return keys;
	}

	
	@Override
	public Collection<V> values() {
		// TODO Auto-generated method stub
		List<V> list = Util.newList();
		for (K key : keys) {
			list.add(map.get(key));
		}
		
		return list;
	}

	
	@Override
	public Set<Entry<K, V>> entrySet() {
		// TODO Auto-generated method stub
		Set<Entry<K, V>> entrySet = map.entrySet();
		ListSet<Entry<K, V>> listSet = new ListSet<Map.Entry<K,V>>();
		for (K key : keys) {
			
			for (Entry<K, V> entry : entrySet) {
				if (entry.getKey().equals(key)) {
					listSet.add(entry);
					break;
				}
			}
		}
		
		return listSet;
	}


}


