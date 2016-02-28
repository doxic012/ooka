package org.ooka.sfisc12s.runtime.util;

import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by steve on 17.02.16.
 */
@Deprecated
public class ObservableMap<K, V> extends HashMap<K, V> {

    private Logger log = LoggerFactory.getRuntimeLogger(ObservableMap.class);

    private List<Consumer<V>> addEvents = new ArrayList<>();
    private List<Consumer<V>> removeEvents = new ArrayList<>();

    public ObservableMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public ObservableMap(int initialCapacity) {
        super(initialCapacity);
    }

    public ObservableMap() {
    }

    public ObservableMap(Map<? extends K, ? extends V> m) {
        super(m);
    }

    public void onAdd(Consumer<V> event) {
        addEvents.add(event);
    }

    public void onRemove(Consumer<V> event) {
        removeEvents.add(event);
    }

    @Override
    public V put(K key, V value) {
        V p = super.put(key, value);

        addEvents.forEach(event -> event.accept(value));

        return p;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach((k, v) -> {
            this.put(k, v);
        });
    }

    @Override
    public V remove(Object key) {
        V p = super.remove(key);

        removeEvents.forEach(event -> event.accept(p));

        return p;
    }

    @Override
    public V putIfAbsent(K key, V value) {
        V p = super.putIfAbsent(key, value);

        if (p != null)
            addEvents.forEach(e -> e.accept(p));

        return p;
    }

    @Override   
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        V p = super.computeIfAbsent(key, mappingFunction);

        if (p != null)
            addEvents.forEach(e -> e.accept(p));

        return p;
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        V p = super.computeIfPresent(key, remappingFunction);

        if (p != null)
            addEvents.forEach(e -> e.accept(p));

        return p;
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        V p = super.compute(key, remappingFunction);

        if (p != null)
            addEvents.forEach(e -> e.accept(p));

        return p;
    }
}
