package com.ximasoftware.persistence;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * a thread-safe, consistent indexed list of things. probably references to calls or call events, indexed by
 * party, type, id, whatever.
 * @param <K>
 * @param <VT>
 */
class IndexedSet<K, VT> {
    private final Object addLock = new Object();
    private final Map<K, Set<VT>> index = new ConcurrentHashMap<>();

    public Set<VT> add(K key, VT thing) {
        Set<VT> set = index.get(key);
        if (set == null) {
            synchronized (addLock) {
                set = index.get(key);
                if (set == null) {
                    set = new HashSet<>();
                    index.put(key, set);
                }
            }
        }

        synchronized (set) {
            set.add(thing);
        }

        return Collections.unmodifiableSet(set);
    }

    public Set<VT> get(K key) {
        final Set<VT> set = index.get(key);
        if (set != null) {
            return Collections.unmodifiableSet(set);
        }
        return Collections.unmodifiableSet(new HashSet<>());
    }
}
