// GenericsNote: Converted.
/*
 *  Copyright 2003-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections15.map;

import org.apache.commons.collections15.MapIterator;
import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.OrderedMapIterator;
import org.apache.commons.collections15.Unmodifiable;
import org.apache.commons.collections15.collection.UnmodifiableCollection;
import org.apache.commons.collections15.iterators.UnmodifiableMapIterator;
import org.apache.commons.collections15.iterators.UnmodifiableOrderedMapIterator;
import org.apache.commons.collections15.set.UnmodifiableSet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Decorates another <code>OrderedMap</code> to ensure it can't be altered.
 * <p/>
 * This class is Serializable from Commons Collections 3.1.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:32 $
 * @since Commons Collections 3.0
 */
public final class UnmodifiableOrderedMap <K,V> extends AbstractOrderedMapDecorator<K, V> implements Unmodifiable, Serializable {

    /**
     * Serialization version
     */
    private static final long serialVersionUID = 8136428161720526266L;

    /**
     * Factory method to create an unmodifiable sorted map.
     *
     * @param map the map to decorate, must not be null
     * @throws IllegalArgumentException if map is null
     */
    public static <K,V> OrderedMap<K, V> decorate(OrderedMap<K, V> map) {
        if (map instanceof Unmodifiable) {
            return map;
        }
        return new UnmodifiableOrderedMap<K, V>(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     *
     * @param map the map to decorate, must not be null
     * @throws IllegalArgumentException if map is null
     */
    private UnmodifiableOrderedMap(OrderedMap<K, V> map) {
        super(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Write the map out using a custom routine.
     *
     * @param out the output stream
     * @throws IOException
     * @since Commons Collections 3.1
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(map);
    }

    /**
     * Read the map in using a custom routine.
     *
     * @param in the input stream
     * @throws IOException
     * @throws ClassNotFoundException
     * @since Commons Collections 3.1
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        map = (Map<K, V>) in.readObject();
    }

    //-----------------------------------------------------------------------
    public MapIterator<K, V> mapIterator() {
        MapIterator<K, V> it = getOrderedMap().mapIterator();
        return UnmodifiableMapIterator.decorate(it);
    }

    public OrderedMapIterator<K, V> orderedMapIterator() {
        OrderedMapIterator<K, V> it = getOrderedMap().orderedMapIterator();
        return UnmodifiableOrderedMapIterator.decorate(it);
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public V put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    public void putAll(Map<? extends K, ? extends V> mapToCopy) {
        throw new UnsupportedOperationException();
    }

    public V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> set = super.entrySet();
        return UnmodifiableEntrySet.decorate(set);
    }

    public Set<K> keySet() {
        Set<K> set = super.keySet();
        return UnmodifiableSet.decorate(set);
    }

    public Collection<V> values() {
        Collection<V> coll = super.values();
        return UnmodifiableCollection.decorate(coll);
    }

}
