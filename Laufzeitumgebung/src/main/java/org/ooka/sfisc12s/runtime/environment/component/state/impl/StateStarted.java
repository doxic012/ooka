package org.ooka.sfisc12s.runtime.environment.component.state.impl;

import org.ooka.sfisc12s.runtime.environment.RuntimeEnvironment;
import org.ooka.sfisc12s.runtime.util.Logger.Logger;
import org.ooka.sfisc12s.runtime.environment.component.ComponentBase;
import org.ooka.sfisc12s.runtime.environment.component.state.State;
import org.ooka.sfisc12s.runtime.environment.component.state.exception.StateException;
import org.ooka.sfisc12s.runtime.util.Logger.Impl.LoggerFactory;

/**
 * Created by Stefan on 26.10.2015.
 */
public class StateStarted implements State {
    private static Logger log = LoggerFactory.getRuntimeLogger(StateStarted.class);

    private ComponentBase componentBase;

    public StateStarted(ComponentBase componentBase) {
        this.componentBase = componentBase;
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
    public void stop(Object... args) throws StateException {
        componentBase.stopComponent(args);

        RuntimeEnvironment re = componentBase.getRuntimeEnvironment();
        re.updateComponentInjection(componentBase, true); // Remove injected component instance from other components

        componentBase.setState(new StateStopped(componentBase));

        log.debug("Component stopped: %s", componentBase);
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
