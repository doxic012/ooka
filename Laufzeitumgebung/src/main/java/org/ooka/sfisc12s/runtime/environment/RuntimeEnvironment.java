package org.ooka.sfisc12s.runtime.environment;

import org.ooka.sfisc12s.runtime.environment.cdi.ContextDependencyInjector;
import org.ooka.sfisc12s.runtime.environment.persistence.Component;
import org.ooka.sfisc12s.runtime.environment.persistence.dao.ComponentDAO;
import org.ooka.sfisc12s.runtime.environment.state.exception.StateException;
import org.ooka.sfisc12s.runtime.environment.scope.Scopeable;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.loader.ExtendedClassLoader;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RuntimeEnvironment extends ContextDependencyInjector implements Scopeable {

    private static boolean initializing = false;

    private static RuntimeEnvironment instance = null;

    private static Logger log = LoggerFactory.getRuntimeLogger(RuntimeEnvironment.class);

    private ExtendedClassLoader classLoader = new ExtendedClassLoader(Thread.currentThread().getContextClassLoader());

    private Scope scope = Scope.InProduction; // active scope

    private RuntimeEnvironment() {
        if (initializing)
            return;
        initializing = true;

        componentCache = ComponentDAO.readAll(); // TODO: where filter auf scope?

        if (!componentCache.isEmpty()) {
            log.debug("List of existing components is loading");

            for (Component component : componentCache) {
                try {
                    log.debug("Loading component %s", component.toString());
                    component.setRuntimeEnvironment(this);
                    component.load();// add component to lzu
                } catch (StateException e) {
                    log.error(e, "Error while loading Component %s", component.toString());
                }
            }
        }
    }

    public static RuntimeEnvironment getInstance() {
        return instance == null ? instance = new RuntimeEnvironment() : instance;
    }

    public List<Component> getComponents() {
        return Collections.unmodifiableList(componentCache);
    }

    public Component getOrAdd(Component component) {
        Component current = componentCache.stream().filter(c -> c.equals(component)).findAny().orElse(component);

        if (!current.isValid())
            return null;

        // add component if not already in list
        if (Objects.equals(component, current)) {
            current = ComponentDAO.create(current);
            log.debug("Component created: %s", (current != null));

            // component wasnt added to database
            if (current == null)
                return null;

            log.debug("Component is valid and not in list");

            current.setRuntimeEnvironment(this);
            componentCache.add(current);
        }

        return current;
    }

    public List<Component> getScopedComponents(Scope scope) {
        return componentCache.stream().filter(c -> Objects.equals(c.getScope(), scope)).collect(Collectors.toList());
    }

    public List<Component> getScopedComponents() {
        return componentCache.stream().filter(c -> Objects.equals(c.getScope(), getScope())).collect(Collectors.toList());
    }

    public Component get(int id) {
        return componentCache.stream().filter(c -> c.getId() == id).findAny().orElse(null);
    }

    public Component get(String checksum) {
        return get(checksum, getScope());
    }

    public Component get(String checksum, Scope scope) {
        return componentCache.stream().filter(c -> Objects.equals(c.getChecksum(), checksum) && Objects.equals(c.getScope(), scope)).findAny().orElse(null);
    }

    public Component update(Component component) {
        return ComponentDAO.update(component);
    }

    public boolean remove(Component component) {
        Component current = componentCache.stream().filter(c -> c.equals(component)).findAny().orElse(null);

        // remove component
        if (current != null) {
            componentCache.remove(current);
            return ComponentDAO.delete(current);
        }

        return false;
    }

    public ExtendedClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public void setScope(Scope scope) {
        this.scope = scope;
    }
}