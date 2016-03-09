package org.ooka.sfisc12s.runtime.environment;

import org.ooka.sfisc12s.runtime.environment.cdi.ContextDependencyInjector;
import org.ooka.sfisc12s.runtime.environment.component.ComponentBase;
import org.ooka.sfisc12s.runtime.environment.component.dao.ComponentDAO;
import org.ooka.sfisc12s.runtime.environment.component.state.exception.StateException;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.loader.ExtendedClassLoader;

import java.net.URISyntaxException;
import java.util.List;
import java.util.function.Consumer;

public class RuntimeEnvironment extends ContextDependencyInjector {

    private static RuntimeEnvironment instance = null;

    private static Logger log = LoggerFactory.getRuntimeLogger(RuntimeEnvironment.class);

    private ExtendedClassLoader classLoader = new ExtendedClassLoader();

    private RuntimeEnvironment() {
        List<ComponentBase> componentBases = ComponentDAO.readAll(); // TODO: where filter auf scope?

        if (componentBases != null && !componentBases.isEmpty()) {
            log.debug("List of current component dtos is loading");

            componentBases.forEach(component -> {
                try {
                    getClassLoader().addUrl(component.getUrl()); // Add url to classpath only
                    getComponents().add(component.load());// add component to lzu
                } catch (StateException | URISyntaxException e) {
                    log.error(e, "Error while loading Component %s", component.toString());
                }
            });
        }
    }

    public ExtendedClassLoader getClassLoader() {
        return classLoader;
    }

    public static RuntimeEnvironment getInstance() {
        return instance == null ? instance = new RuntimeEnvironment() : instance;
    }

    public ComponentBase getOrAdd(ComponentBase component) {
        ComponentBase current = getComponents().stream().filter(c -> c.equals(component)).findAny().get();

        if (current != null)
            return current;

        if (!component.isValid())
            return null;

        // add component and return
        getComponents().add(component);
        return component;
    }

    public ComponentBase get(String name) {
        return getComponents().stream().filter(c -> c.getName().equals(name)).findAny().get();
    }

    public ComponentBase get(String checksum, String scope) {
        return getComponents().stream().filter(c -> c.getChecksum().equals(checksum) && c.getScope().equals(scope)).findAny().get();
    }

    public boolean remove(ComponentBase component) {
        ComponentBase current = getComponents().stream().filter(c -> c.equals(component)).findAny().get();

        // remove component
        if (current != null) {
            getComponents().remove(current);
            return ComponentDAO.delete(current);
        }

        return false;
    }
}