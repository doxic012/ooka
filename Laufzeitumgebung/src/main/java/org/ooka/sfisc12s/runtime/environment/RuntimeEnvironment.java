package org.ooka.sfisc12s.runtime.environment;

import org.ooka.sfisc12s.runtime.environment.cdi.ContextDependencyInjector;
import org.ooka.sfisc12s.runtime.environment.component.ComponentBase;
import org.ooka.sfisc12s.runtime.environment.component.dao.ComponentDAO;
import org.ooka.sfisc12s.runtime.environment.component.state.exception.StateException;
import org.ooka.sfisc12s.runtime.environment.scope.Scopeable;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.loader.ExtendedClassLoader;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RuntimeEnvironment extends ContextDependencyInjector implements Scopeable {

    private static RuntimeEnvironment instance = null;

    private static Logger log = LoggerFactory.getRuntimeLogger(RuntimeEnvironment.class);

    private ExtendedClassLoader classLoader = new ExtendedClassLoader();

    private Scope scope = Scope.InProduction; // active scope

    private RuntimeEnvironment() {
        componentCache = ComponentDAO.readAll(); // TODO: where filter auf scope?

        if (!componentCache.isEmpty()) {
            log.debug("List of current component dtos is loading");

            for (ComponentBase component : componentCache) {
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

    public List<ComponentBase> getComponents() {
        return Collections.unmodifiableList(componentCache);
    }

    public ComponentBase getOrAdd(ComponentBase component) {
        ComponentBase current = componentCache.stream().filter(c -> c.equals(component)).findAny().orElse(component);
        log.debug("getoradd valid: %s", current.isValid());

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
            current.setScope(this.getScope());
            componentCache.add(current);
        }

        return current;
    }

    public List<ComponentBase> getScopedComponents() {
        return componentCache.stream().filter(c -> Objects.equals(c.getScope(), getScope())).collect(Collectors.toList());
    }

    public ComponentBase get(int id) {
        return componentCache.stream().filter(c -> c.getId() == id).findAny().orElse(null);
    }

    public ComponentBase get(String checksum) {
        return get(checksum, getScope());
    }

    public ComponentBase get(String checksum, Scope scope) {
        return componentCache.stream().filter(c -> Objects.equals(c.getChecksum(), checksum) && Objects.equals(c.getScope(), scope)).findAny().orElse(null);
    }

    public ComponentBase update(ComponentBase component) {
        return ComponentDAO.update(component);
    }

    public boolean remove(ComponentBase component) {
        ComponentBase current = componentCache.stream().filter(c -> c.equals(component)).findAny().orElse(null);

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