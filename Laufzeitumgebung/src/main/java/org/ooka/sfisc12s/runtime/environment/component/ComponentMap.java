package org.ooka.sfisc12s.runtime.environment.component;

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
public class ComponentMap extends HashMap<String, Component> {

    private Logger log = LoggerFactory.getRuntimeLogger(ComponentMap.class);

    private List<Consumer<Component>> addEvents = new ArrayList<>();
    private List<Consumer<Component>> removeEvents = new ArrayList<>();

    public ComponentMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public ComponentMap(int initialCapacity) {
        super(initialCapacity);
    }

    public ComponentMap() {
    }

    public ComponentMap(Map<? extends String, ? extends Component> m) {
        super(m);
    }

    public void onAdd(Consumer<Component> event) {
        addEvents.add(event);
    }

    public void onRemove(Consumer<Component> event) {
        removeEvents.add(event);
    }

    @Override
    public Component put(String key, Component value) {
        Component p = super.put(key, value);

        addEvents.forEach(event -> event.accept(value));

        log.debug("putting %s to %s", key, value.getComponentClass().getName());

        return p;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Component> m) {
        m.forEach((k, v) -> {
            this.put(k, v);
        });
    }

    @Override
    public Component remove(Object key) {
        Component p = super.remove(key);

        removeEvents.forEach(event -> event.accept(p));

        log.debug("removed %s", key);
        return p;
    }

    @Override
    public Component putIfAbsent(String key, Component value) {
        Component p = super.putIfAbsent(key, value);

        if (p != null)
            addEvents.forEach(e -> e.accept(p));

        return p;
    }

    @Override
    public Component computeIfAbsent(String key, Function<? super String, ? extends Component> mappingFunction) {
        Component p = super.computeIfAbsent(key, mappingFunction);

        if (p != null)
            addEvents.forEach(e -> e.accept(p));

        return p;
    }

    @Override
    public Component computeIfPresent(String key, BiFunction<? super String, ? super Component, ? extends Component> remappingFunction) {
        Component p = super.computeIfPresent(key, remappingFunction);

        if (p != null)
            addEvents.forEach(e -> e.accept(p));

        return p;
    }

    @Override
    public Component compute(String key, BiFunction<? super String, ? super Component, ? extends Component> remappingFunction) {
        Component p = super.compute(key, remappingFunction);

        if (p != null)
            addEvents.forEach(e -> e.accept(p));

        return p;
    }
}
