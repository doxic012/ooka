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

            System.out.printf("Component initialized: %s%s", component.getName(), System.lineSeparator());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.printf("Could not load component: %s%s", component.getName(), System.lineSeparator());
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
            System.out.printf("Component missing.%s%s", component.getName(), System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.printf("IO Exception.%s%s", component.getName(), System.lineSeparator());
        }
    }

    @Override
    public void unload() throws StateException {
        throw new StateException("Component is already unloaded.");
    }
}
