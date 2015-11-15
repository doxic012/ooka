package org.bonn.ooka.runtime.environment.component.state.impl;

import org.bonn.ooka.runtime.environment.component.Component;
import org.bonn.ooka.runtime.environment.component.state.State;
import org.bonn.ooka.runtime.environment.component.state.exception.StateException;

/**
 * Created by Stefan on 26.10.2015.
 */
public class StateStarted implements State {

    private Component component;

    public StateStarted(Component component) {
        this.component = component;
    }

    @Override
    public void start(Object... args) throws StateException {
        throw new StateException("Component is already started.");
    }

    @Override
    public void stop(Object... args) throws StateException {
        component.stopComponent(args);
        component.setState(new StateStopped(component));
        component.getLogger().debug("Component stopped: %s", component.getName());
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
