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
import java.util.Objects;
import java.util.function.Consumer;

public class RuntimeEnvironment extends ContextDependencyInjector {

    private static RuntimeEnvironment instance = null;

    private static Logger log = LoggerFactory.getRuntimeLogger(RuntimeEnvironment.class);

    private ExtendedClassLoader classLoader = new ExtendedClassLoader();

    private RuntimeEnvironment() {
        setComponents(ComponentDAO.readAll()); // TODO: where filter auf scope?

        if (!getComponents().isEmpty()) {
            log.debug("List of current component dtos is loading");

            for(ComponentBase component : getComponents()){
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
        ComponentBase current = getComponents().stream().filter(c -> c.equals(component)).findAny().orElse(component);

        if (!current.isValid())
            return null;

        // add component if not already in list
        if (Objects.equals(component, current)) {
            current = ComponentDAO.create(current);

            // component wasnt added to database
            if(current == null)
                return null;

            current.setRuntimeEnvironment(this);
            getComponents().add(current);
        }

        return current;
    }

    public ComponentBase get(int id) {
        return getComponents().stream().filter(c -> c.getId().equals(id)).findAny().get();
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