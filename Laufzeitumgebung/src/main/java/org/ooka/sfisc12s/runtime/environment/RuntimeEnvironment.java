package org.ooka.sfisc12s.runtime.environment;

import org.ooka.sfisc12s.runtime.environment.cdi.ContextDependencyInjector;
import org.ooka.sfisc12s.runtime.environment.component.ComponentBase;
import org.ooka.sfisc12s.runtime.environment.component.dao.ComponentDAO;
import org.ooka.sfisc12s.runtime.environment.component.state.exception.StateException;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.loader.ExtendedClassLoader;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RuntimeEnvironment extends ContextDependencyInjector {

    private static RuntimeEnvironment instance = null;

    private static Logger log = LoggerFactory.getRuntimeLogger(RuntimeEnvironment.class);

    private ExtendedClassLoader classLoader = new ExtendedClassLoader();

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

    public ExtendedClassLoader getClassLoader() {
        return classLoader;
    }

    public static RuntimeEnvironment getInstance() {
        return instance == null ? instance = new RuntimeEnvironment() : instance;
    }

    public ComponentBase getOrAdd(ComponentBase component) {
        ComponentBase current = componentCache.stream().filter(c -> c.equals(component)).findAny().orElse(component);

        if (!current.isValid())
            return null;

        // add component if not already in list
        if (Objects.equals(component, current)) {
            current = ComponentDAO.create(current);

            // component wasnt added to database
            if (current == null)
                return null;

            current.setRuntimeEnvironment(this);
            componentCache.add(current);
        }

        return current;
    }

    public List<ComponentBase> getComponents() {
        return Collections.unmodifiableList(componentCache);
    }

    public ComponentBase get(int id) {
        return componentCache.stream().filter(c -> c.getId() == id).findAny().orElse(null);
    }

    public ComponentBase get(String checksum, String scope) {
        return componentCache.stream().filter(c -> c.getChecksum().equals(checksum) && c.getScope().equals(scope)).findAny().orElse(null);
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
}