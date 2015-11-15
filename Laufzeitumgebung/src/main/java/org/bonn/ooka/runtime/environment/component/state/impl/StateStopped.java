package org.bonn.ooka.runtime.environment.component.state.impl;

import org.bonn.ooka.runtime.environment.component.Component;
import org.bonn.ooka.runtime.environment.component.state.State;
import org.bonn.ooka.runtime.environment.component.state.exception.StateException;

/**
 * Created by Stefan on 26.10.2015.
 */
public class StateStopped implements State {

    private Component component;

    public StateStopped(Component component) {
        this.component = component;
    }

    @Override
    public void start(Object... args) throws StateException {
        component.startComponent(args);
        component.setState(new StateStarted(this.component));
        component.getLogger().debug("Component %s started.", component.getName());
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
    public void unload() {
        component.setState(new StateUnloaded(component));
        component.setComponentClass(null);
        component.getLogger().debug("Reference to component class/instance deleted: %s", component.getName());
    }
}
