/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.var.map;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author seh
 */
public class MapVar<K,V> implements Map<K,V> {

    private Map<K,V> m = new HashMap();

    public interface IfMapChanges<K,V> {
        public void onMapChanged(MapVar<K,V> map);
    }
    
    //TODO lazy-instantiate
    private List<IfMapChanges<K,V>> mapChanges = new LinkedList();

    public MapVar() {
        this(16, false);
    }

    public MapVar(int initialCapacity, boolean concurrent) {
        super();

        if (concurrent) {
            m = new ConcurrentHashMap(initialCapacity);
        }
        else {
            m = new HashMap(initialCapacity);
        }
        
    }

    public void add(IfMapChanges<K,V> c) {
        mapChanges.add(c);
    }
    public void remove(IfMapChanges<K,V> c) {
        mapChanges.remove(c);
    }

    protected void notifyChanged() {
        for (IfMapChanges<K,V> c : mapChanges) {
            c.onMapChanged(this);
        }
    }

    @Override public int size() {      return m.size();    }

    @Override public boolean isEmpty() {      return m.isEmpty();    }

    @Override public boolean containsKey(Object key) {      return m.containsKey(key);    }

    @Override public boolean containsValue(Object value) { return m.containsValue(value); }

    @Override public V get(Object key) {
        return m.get(key);
    }

    @Override
    public V put(K key, V value) {
        V replaced = m.put(key, value);
        notifyChanged();
        return replaced;
    }

    @Override
    public V remove(Object key) {
        V removed = m.remove(key);
        if (removed!=null)
            notifyChanged();
        return removed;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> o) {
        m.putAll(o);
        notifyChanged();
    }

    @Override
    public void clear() {
        m.clear();
        notifyChanged();
    }

    @Override
    public Set<K> keySet() {
        return m.keySet();
    }

    @Override
    public Collection<V> values() {
        return m.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return m.entrySet();
    }

}
