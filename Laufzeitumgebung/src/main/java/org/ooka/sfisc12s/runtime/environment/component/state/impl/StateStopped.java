package org.ooka.sfisc12s.runtime.environment.component.state.impl;

import org.ooka.sfisc12s.runtime.environment.RuntimeEnvironment;
import org.ooka.sfisc12s.runtime.environment.component.ComponentBase;
import org.ooka.sfisc12s.runtime.environment.scope.exception.ScopeException;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.component.state.State;
import org.ooka.sfisc12s.runtime.environment.component.state.exception.StateException;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;

/**
 * Created by Stefan on 26.10.2015.
 */
public class StateStopped implements State {

    private static Logger log = LoggerFactory.getRuntimeLogger(StateStopped.class);

    private ComponentBase componentBase;

    public StateStopped(ComponentBase componentBase) {
        this.componentBase = componentBase;
    }

    @Override
    public String getName() {
        return "StateStopped";
    }

    @Override
    public void start(Object... args) throws StateException, ScopeException {
        componentBase.startComponent(args);

        // inject this component instance into other components
        RuntimeEnvironment re = componentBase.getRuntimeEnvironment();
        re.updateComponentInjection(componentBase);

        componentBase.setState(new StateStarted(componentBase));
        log.debug("Component %s started.", componentBase);
    }

    @Override
    public void stop(Object... args) throws StateException {
        throw new StateException("Component is already stopped.");
    }

    @Override
    public void load() throws StateException {
        throw new StateException("Component is already loaded.");
    }

    @Override
    public void unload() throws ScopeException {
        componentBase.clear();

        // remove all references inside this component
        // set all references to this component to null
        // and remove all injections inside the components instance
        RuntimeEnvironment re = componentBase.getRuntimeEnvironment();
        re.updateComponentInjection(componentBase, true);
        re.removeDependencies(componentBase);
        re.updateCache(componentBase);

        componentBase.setState(new StateUnloaded(componentBase));

        log.debug("Reference to component class/instance deleted: %s", componentBase);
    }
}
