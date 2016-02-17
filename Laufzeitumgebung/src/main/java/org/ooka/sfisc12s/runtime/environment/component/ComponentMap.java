package org.ooka.sfisc12s.runtime.environment.component;

import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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

        removeEvents.forEach(event -> event.accept((Component) key));

        log.debug("removed %s", key);
        return p;
    }


}
