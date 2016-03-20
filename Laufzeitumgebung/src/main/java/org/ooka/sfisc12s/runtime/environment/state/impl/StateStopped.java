package org.ooka.sfisc12s.runtime.environment.state.impl;

import org.ooka.sfisc12s.runtime.environment.RuntimeEnvironment;
import org.ooka.sfisc12s.runtime.environment.persistence.Component;
import org.ooka.sfisc12s.runtime.environment.scope.exception.ScopeException;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.state.State;
import org.ooka.sfisc12s.runtime.environment.state.exception.StateException;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;

/**
 * Created by Stefan on 26.10.2015.
 */
public class StateStopped implements State {

    private static Logger log = LoggerFactory.getRuntimeLogger(StateStopped.class);

    private Component component;

    public StateStopped(Component component) {
        this.component = component;
    }

    @Override
    public String getName() {
        return "StateStopped";
    }

    @Override
    public void start(Object... args) throws StateException, ScopeException {
        component.startComponent(args);

        // inject this component instance into other components
        RuntimeEnvironment re = component.getRuntimeEnvironment();
        re.updateComponentInjection(component);

        component.setState(new StateStarted(component));
        log.debug("Component %s started.", component);
    }

    @Override
    public void stop(Object... args) throws StateException {
        throw new StateException("Component is already stopped.");
    }

    @Override
    public void forceStop(Object... args) {
        log.debug("Component is already stopped.");
    }

    @Override
    public void load() throws StateException {
        throw new StateException("Component is already loaded.");
    }

    @Override
    public void unload() throws ScopeException {
        component.clear();

        // remove all references inside this component
        // set all references to this component to null
        // and remove all injections inside the components instance
        RuntimeEnvironment re = component.getRuntimeEnvironment();
        re.updateComponentInjection(component, true);
        re.removeDependencies(component);
        re.updateCache(component);

        component.setState(new StateUnloaded(component));

        log.debug("Reference to component class/instance deleted: %s", component);
    }
}
