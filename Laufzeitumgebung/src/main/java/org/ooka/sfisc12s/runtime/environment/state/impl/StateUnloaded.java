package org.ooka.sfisc12s.runtime.environment.state.impl;

import org.ooka.sfisc12s.runtime.environment.RuntimeEnvironment;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.persistence.Component;
import org.ooka.sfisc12s.runtime.environment.state.State;
import org.ooka.sfisc12s.runtime.environment.state.exception.StateException;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by Stefan on 26.10.2015.
 */
public class StateUnloaded implements State {
    private static Logger log = LoggerFactory.getRuntimeLogger(StateUnloaded.class);

    private Component component;

    public StateUnloaded(Component component) {
        this.component = component;
    }

    @Override
    public String getName() {
        return "StateUnloaded";
    }

    @Override
    public void start(Object... args) throws StateException {
        throw new StateException("Component is not loaded.");
    }

    @Override
    public void stop(Object... args) throws StateException {
        throw new StateException("Component is not loaded");
    }

    @Override
    public void forceStop(Object... args) {
        log.debug("Component is not loaded.");
    }

    @Override
    public void load() throws StateException {
        try {
            component.initialize();

            RuntimeEnvironment re = component.getRuntimeEnvironment();
            re.updateCache(component);
            re.injectDependencies(component);

            component.setState(new StateStopped(component));

            log.debug("Component initialized: %s", component);
        } catch (ClassNotFoundException e) {
            throw new StateException(e, "Class not found or could not load component: %s", component);
        } catch (NoClassDefFoundError e) {
            throw new StateException(e, "Component missing: %s", component);
        } catch (URISyntaxException e) {
            throw new StateException(e, "URI could was not loaded correctly by component: %s", component);
        } catch (IOException | InstantiationException | IllegalAccessException e) {
            throw new StateException(e, "Exception: %s", component);
        }
    }

    @Override
    public void unload() throws StateException {
        throw new StateException("Component is already unloaded.");
    }
}
