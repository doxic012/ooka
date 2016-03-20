package org.ooka.sfisc12s.runtime.environment.state.impl;

import org.ooka.sfisc12s.runtime.environment.RuntimeEnvironment;
import org.ooka.sfisc12s.runtime.environment.scope.exception.ScopeException;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.persistence.Component;
import org.ooka.sfisc12s.runtime.environment.state.State;
import org.ooka.sfisc12s.runtime.environment.state.exception.StateException;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;

/**
 * Created by Stefan on 26.10.2015.
 */
public class StateStarted implements State {
    private static Logger log = LoggerFactory.getRuntimeLogger(StateStarted.class);

    private Component component;

    public StateStarted(Component component) {
        this.component = component;
    }

    @Override
    public String getName() {
        return "StateStarted";
    }

    @Override
    public void start(Object... args) throws StateException {
        throw new StateException("Component is already started.");
    }

    @Override
    public void stop(Object... args) throws StateException, ScopeException {
        component.stopComponent(args);

        RuntimeEnvironment re = component.getRuntimeEnvironment();
        re.updateComponentInjection(component, true); // Remove injected component instance from other components

        component.setState(new StateStopped(component));

        log.debug("Component stopped: %s", component);
    }

    @Override
    public void forceStop(Object... args) throws ScopeException {
        try {
            component.stopComponent(args);
        } catch (Exception ex) {
            log.error(ex, "Error while running stop-method in force-mode.");
        }

        RuntimeEnvironment re = component.getRuntimeEnvironment();
        re.updateComponentInjection(component, true); // Remove injected component instance from other components

        component.setState(new StateStopped(component));

        log.debug("Component force-stopped: %s", component);
    }

    @Override
    public void load() throws StateException {
        throw new StateException("Component is already loaded.");
    }

    @Override
    public void unload() throws StateException {
        throw new StateException("Component must be stopped first.");
    }


}
