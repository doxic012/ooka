package org.bonn.ooka.runtime.environment.component.state.impl;

import org.bonn.ooka.runtime.environment.component.Component;
import org.bonn.ooka.runtime.environment.component.state.State;
import org.bonn.ooka.runtime.environment.component.state.exception.StateException;

import java.io.IOException;

/**
 * Created by Stefan on 26.10.2015.
 */
public class StateUnloaded implements State {

    private Component component;

    public StateUnloaded(Component component) {
        this.component = component;
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
    public void load() {
        try {
            component.initialize();
            component.setState(new StateStopped(component));
            component.getLogger().debug("Component initialized: %s", component.getName());
        } catch (ClassNotFoundException e) {
            component.getLogger().error(e, "Could not load component: %s", component.getName());
        } catch (NoClassDefFoundError e) {
            component.getLogger().error(e, "Component missing.%s", component.getName());
        } catch (IOException | InstantiationException | IllegalAccessException e) {
            component.getLogger().error(e, "Exception.%s", component.getName());
        }
    }

    @Override
    public void unload() throws StateException {
        throw new StateException("Component is already unloaded.");
    }
}
